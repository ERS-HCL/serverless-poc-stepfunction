package com.ecare.activity.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.GetActivityTaskRequest;
import com.amazonaws.services.stepfunctions.model.GetActivityTaskResult;
import com.amazonaws.services.stepfunctions.model.SendTaskFailureRequest;
import com.amazonaws.services.stepfunctions.model.SendTaskSuccessRequest;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;
import com.amazonaws.services.stepfunctions.model.StartExecutionResult;
import com.amazonaws.util.json.Jackson;
import com.ecare.activity.StepFunctionActivity;
import com.ecare.constants.ActivityEnum;
import com.ecare.constants.Params;
import com.ecare.exception.AlreadyRegistertedException;
import com.ecare.model.db.PatientStatus;
import com.ecare.model.db.repository.PatientRepository;
import com.fasterxml.jackson.databind.JsonNode;

@Component("registrationActivity")
public class PatientRegistrationActivity implements StepFunctionActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(PatientRegistrationActivity.class);

	@Autowired
	private PatientRepository patientRepository;
	@Autowired
	@Qualifier("awsStepFunctionClient")
	private AWSStepFunctions awsStepFunctionClient;

	@Override
	public void performActivity(Map<String, Object> requestMap) throws Exception {
		LOGGER.info("PatientRegistration Start ............ " + awsStepFunctionClient);

		PatientStatus patientStatus = getDataModelFromRequestMapforRegister(requestMap);
		patientStatus.setPtCurrentState(ActivityEnum.REGISTER_PATIENT.getActivityName());
		boolean isExist = false;
		if (patientRepository.findById(patientStatus.getPatientId()).isPresent()) {
			isExist = true;
		}
		if (!isExist) {
			StartExecutionRequest startExecutionRequest = getStartRequestforStateMachine(patientStatus.getPatientId());
			StartExecutionResult startExecutionResult = awsStepFunctionClient.startExecution(startExecutionRequest);
			LOGGER.info(" Start execution Result ............ " + startExecutionResult);
			patientStatus.setExecutionARN(startExecutionResult.getExecutionArn());
			LOGGER.info(" Start execution arn ............ " + startExecutionResult.getExecutionArn());
			Map<String, Long> actvityMap = new HashMap<String, Long>();
			actvityMap.put(ActivityEnum.REGISTER_PATIENT.getActivityName(), patientStatus.getPtCurrentDate());
			populateActivityDetail(actvityMap, patientStatus);
			patientStatus.setPtCurrentState(ActivityEnum.REGISTER_PATIENT.getActivityName());
			patientStatus.setNextActivity(ActivityEnum.LABORATORY.getActivityName());
			GetActivityTaskResult getActivityTaskResult = awsStepFunctionClient
					.getActivityTask(new GetActivityTaskRequest().withActivityArn(ACTIVITY_REGISTER_PATIENT_ARN));
			String taskToken = getActivityTaskResult.getTaskToken();
			patientStatus.setCurrentActivityTaskToken(taskToken);
			patientRepository.save(patientStatus);
			if (taskToken != null) {
				try {
					JsonNode json = Jackson.jsonNodeOf(getActivityTaskResult.getInput());
					String outputResult = setStatus(json.get(Params.PATIENT_ID).textValue(), true);
					LOGGER.info("outputResult " + outputResult);
					SendTaskSuccessRequest sendTaskRequest = new SendTaskSuccessRequest().withOutput(outputResult)
							.withTaskToken(taskToken);
					awsStepFunctionClient.sendTaskSuccess(sendTaskRequest);
					patientRepository.save(patientStatus);
				} catch (Exception e) {
					LOGGER.error("Error in send success notification ", e);
					awsStepFunctionClient.sendTaskFailure(new SendTaskFailureRequest().withTaskToken(taskToken));
					patientRepository.deleteById(patientStatus.getPatientId());

				}
			}
		}else {
			throw new Exception(Params.ALREADY_REGISTERED_EXCEPTION_MSG);
		}

	}

}