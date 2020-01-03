package com.ecare.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;
import com.ecare.constants.Params;
import com.ecare.model.db.PatientStatus;
import com.ecare.utilities.DateTimeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface Activity {
	String ASM_ARN = "arn:aws:states:ap-south-1:44:stateMachine:eCare";
	String ACTIVITY_ARN_PREFIX = "arn:aws:states:ap-south-1:44:activity:";
	String ACTIVITY_REGISTER_PATIENT_ARN = ACTIVITY_ARN_PREFIX + "Register-patient";
	String ACTIVITY_REGISTERATION_REMINDER_ARN = ACTIVITY_ARN_PREFIX + "RegistrationReminderActivity";
	String ACTIVITY_LABORATORY_ARN = ACTIVITY_ARN_PREFIX + "LaboratoryActivity";
	String ACTIVITY_DOCTOR_CHEKUP_ARN = ACTIVITY_ARN_PREFIX + "DoctorCheckupActivity"; 
	String ACTIVITY_REVIEW_ARN = ACTIVITY_ARN_PREFIX + "ReviewActivity";
	String ACTIVITY_PHARMACY_ARN = ACTIVITY_ARN_PREFIX + "PharmacyActivity";
	int STATUS_SUCCESS = 1;
	int STATUS_FAILED = 0;
	ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 
	 * @throws Exception TODO
	 */
	public void performActivity(Map<String, Object> requestMap) throws Exception;

	/**
	 * 
	 * @return
	 */
	public default AWSStepFunctions getStepFunctionClient() {
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setSocketTimeout((int) TimeUnit.SECONDS.toMillis(70));
		// BasicAWSCredentials awsCreds = new BasicAWSCredentials("access_key_id",
		// "secret_key_id");
		/*
		 * AmazonS3 s3Client = AmazonS3ClientBuilder.standard() .withCredentials(new
		 * AWSStaticCredentialsProvider(awsCreds)) .build();
		 */

		AWSStepFunctions client = AWSStepFunctionsClientBuilder.standard().withRegion(Regions.AP_SOUTH_1)
				.withCredentials(new EnvironmentVariableCredentialsProvider())
				.withClientConfiguration(clientConfiguration).build();
		return client;
	}

	public default String populateInputforStateMachine(String patientId) throws Exception {
		Map<String, Object> inputMap = new HashMap<String, Object>();
		inputMap.put("patientId", patientId);
		return objectMapper.writeValueAsString(inputMap);

	}

	public default StartExecutionRequest getStartRequestforStateMachine(String patientId) throws Exception {
		StartExecutionRequest startExecutionRequest = new StartExecutionRequest();
		startExecutionRequest.withStateMachineArn(ASM_ARN);
		startExecutionRequest.withName(patientId);
		startExecutionRequest.withInput(populateInputforStateMachine(patientId));
		return startExecutionRequest;
	}

	public default PatientStatus getDataModelFromRequestMapforRegister(Map<String, Object> requestMap) {
		String patientId = (String) requestMap.get(Params.PATIENT_ID);
		String ptName = (String) requestMap.get(Params.PT_NAME);
		String ptMobileNumber = (String) requestMap.get(Params.MOBILE_NO);
		String email = (String) requestMap.get(Params.EMAIL);
		PatientStatus patientStatus = new PatientStatus();
		patientStatus.setPatientId(patientId);
		patientStatus.setPtName(ptName);
		patientStatus.setPtMobileNumber(ptMobileNumber);
		patientStatus.setEmail(email);
		patientStatus.setPtCurrentDate(DateTimeUtils.getGMTCurrentTimeInEpochSecond());
		return patientStatus;
	}
	public default PatientStatus getDataModelFromRequestMapForOther(Map<String, Object> requestMap) {
		String patientId = (String) requestMap.get(Params.PATIENT_ID);
		PatientStatus patientStatus = new PatientStatus();
		patientStatus.setPatientId(patientId);
		patientStatus.setPtCurrentDate(DateTimeUtils.getGMTCurrentTimeInEpochSecond());
		return patientStatus;
	}

	public default String setStatus(String input, boolean doctorCheckup) throws Exception {
		Map<String, Object> inputMap = new HashMap<String, Object>();
		inputMap.put("status", STATUS_SUCCESS);
		inputMap.put(Params.PATIENT_ID, input);
		if (doctorCheckup)
			inputMap.put("lab", STATUS_SUCCESS);
		else
			inputMap.put("lab", STATUS_FAILED);
		return objectMapper.writeValueAsString(inputMap);

	}

	public default void populateActivityDetail(Map<String, Long> activityDetail, PatientStatus ptStatus) {
		if (ptStatus.getActivityDetail() != null) {
			ptStatus.getActivityDetail().get(Params.ACTIVITY_KEY_DB).add(activityDetail);
		} else {
			List<Map<String, Long>> activityList = new ArrayList<Map<String, Long>>();
			activityList.add(activityDetail);
			Map<String, List<Map<String, Long>>> activityDetailMap = new HashMap<String, List<Map<String, Long>>>();
			activityDetailMap.put(Params.ACTIVITY_KEY_DB, activityList);
			ptStatus.setActivityDetail(activityDetailMap);
		}
	}

}
