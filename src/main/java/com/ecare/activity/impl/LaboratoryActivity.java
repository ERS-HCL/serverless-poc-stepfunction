package com.ecare.activity.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.GetActivityTaskRequest;
import com.amazonaws.services.stepfunctions.model.GetActivityTaskResult;
import com.amazonaws.services.stepfunctions.model.SendTaskHeartbeatRequest;
import com.amazonaws.services.stepfunctions.model.SendTaskSuccessRequest;
import com.amazonaws.util.json.Jackson;
import com.ecare.activity.StepFunctionActivity;
import com.ecare.constants.ActivityEnum;
import com.ecare.constants.Params;
import com.ecare.model.db.PatientStatus;
import com.ecare.model.db.repository.PatientRepository;
import com.ecare.utilities.DateTimeUtils;
import com.fasterxml.jackson.databind.JsonNode;

@Component("laboratoryActivity")
public class LaboratoryActivity implements StepFunctionActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(LaboratoryActivity.class);
	@Autowired
	private AWSStepFunctions awsStepFunctionClient;
	@Autowired
	PatientRepository patientRepository;

	@Override
	public void performActivity(Map<String, Object> requestMap) throws Exception {
		LOGGER.info("polling activity worker ");
		GetActivityTaskResult getActivityTaskResult = awsStepFunctionClient
				.getActivityTask(new GetActivityTaskRequest().withActivityArn(ACTIVITY_LABORATORY_ARN));
		String taskToken = getActivityTaskResult.getTaskToken();
		PatientStatus patientStatus = getDataModelFromRequestMapForOther(requestMap);
		Optional<PatientStatus> patientStatusFromDB = patientRepository.findById(patientStatus.getPatientId());
		patientStatusFromDB.get().setPtCurrentDate(DateTimeUtils.getGMTCurrentTimeInEpochSecond());
		patientStatusFromDB.get().setPtCurrentState(ActivityEnum.LABORATORY.getActivityName());
		patientStatusFromDB.get().setNextActivity(ActivityEnum.DOCTORCHEKUP.getActivityName());
		Map<String,Long> actvityMap = new HashMap<String,Long>();
		actvityMap.put(ActivityEnum.LABORATORY.getActivityName(), patientStatus.getPtCurrentDate());
		populateActivityDetail(actvityMap, patientStatusFromDB.get()); 
		
		if (taskToken != null) {
			patientStatusFromDB.get().setCurrentActivityTaskToken(taskToken);
		} else {
			//taskToken = patientStatusFromDB.get().getCurrentActivityTaskToken();
		}
		if (taskToken != null) {
			try {
				JsonNode json = Jackson.jsonNodeOf(getActivityTaskResult.getInput());
				String outputResult = setStatus(json.get(Params.PATIENT_ID).textValue(),false);
				String ptId = json.get(Params.PATIENT_ID).textValue();
				LOGGER.info("ptId form inptput " + ptId +" ptId from db "+patientStatusFromDB.get().getPatientId()+" taskToken"+taskToken);
				if (patientStatusFromDB.get().getPatientId().equalsIgnoreCase(ptId)) {
					LOGGER.info("ptId are equals by request and workFLow");
					SendTaskSuccessRequest sendTaskRequest = new SendTaskSuccessRequest().withOutput(outputResult)
							.withTaskToken(taskToken);
					awsStepFunctionClient.sendTaskSuccess(sendTaskRequest);
				} else {
					awsStepFunctionClient.sendTaskHeartbeat(new SendTaskHeartbeatRequest().withTaskToken(taskToken));
				}
				patientRepository.save(patientStatusFromDB.get());

			} catch (Exception e) {
				awsStepFunctionClient.sendTaskHeartbeat(new SendTaskHeartbeatRequest().withTaskToken(taskToken));
				/*
				 * awsStepFunctionClient.sendTaskFailure( new
				 * SendTaskFailureRequest().withTaskToken(taskToken))
				 * ;
				 */
			}
		} else {
			Thread.sleep(1000);
		}

	}

}
