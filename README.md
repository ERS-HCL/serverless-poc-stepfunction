# serverless-poc-stepfunction

This POC gives the handson  for below  mentioned points 
* StepFunction Uses with Java SDK
* AWS Lambda with Spring Containers(Best way to use) 
* Dyanmo DB with Java SDK using Spring Data 
* Manual Activity with Step Function on AWS using Java SDK.

## POC Detail 
  Paitent compelete process use to implment by this POC . Following flow are implemented 

* Paitent Registration 
* Assign Doctor
* Lab Test
* Review Test by Doctor
* Pharmacy 
* END

Below Snipet used to communicate StepFunction activitites.

```
GetActivityTaskResult getActivityTaskResult = awsStepFunctionClient
				.getActivityTask(new GetActivityTaskRequest().withActivityArn(ACTIVITY_LABORATORY_ARN));
		String taskToken = getActivityTaskResult.getTaskToken();
		PatientStatus patientStatus = getDataModelFromRequestMapForOther(requestMap);
		Optional<PatientStatus> patientStatusFromDB = patientRepository.findById(patientStatus.getPatientId());
		
		if (taskToken != null) {
			try {
				JsonNode json = Jackson.jsonNodeOf(getActivityTaskResult.getInput());
				String outputResult = setStatus(json.get(Params.PATIENT_ID).textValue(),false);
				String ptId = json.get(Params.PATIENT_ID).textValue();

				if (patientStatusFromDB.get().getPatientId().equalsIgnoreCase(ptId)) {
					
					SendTaskSuccessRequest sendTaskRequest = new SendTaskSuccessRequest().withOutput(outputResult)
							.withTaskToken(taskToken);
					awsStepFunctionClient.sendTaskSuccess(sendTaskRequest);
				} else {
					awsStepFunctionClient.sendTaskHeartbeat(new SendTaskHeartbeatRequest().withTaskToken(taskToken));
				}
				patientRepository.save(patientStatusFromDB.get());

			} catch (Exception e) {
				awsStepFunctionClient.sendTaskHeartbeat(new SendTaskHeartbeatRequest().withTaskToken(taskToken));

			}
		} 
```
Spring Data Has been used to save data in Dynamo DB . 

### Entity 
```
package com.ecare.model.db;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "PatientStatus")
public class PatientStatus {
	private String patientId;
	private String executionARN;
	private String ptName;
	private String ptMobileNumber;
	private String email;
	private String ptCurrentState;
	private Long ptCurrentDate;
	private String currentActivityTaskToken;
	private String nextActivity;
	private String nextActivityToken;
	private String currentActivityInput;
	private Map<String, List<Map<String, Long>>> activityDetail;

	@DynamoDBHashKey
	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	@DynamoDBAttribute
	public String getExecutionARN() {
		return executionARN;
	}

	public void setExecutionARN(String executionARN) {
		this.executionARN = executionARN;
	}

	@DynamoDBAttribute
	public String getPtName() {
		return ptName;
	}

	public void setPtName(String ptName) {
		this.ptName = ptName;
	}

	@DynamoDBAttribute
	public String getPtMobileNumber() {
		return ptMobileNumber;
	}

	public void setPtMobileNumber(String ptMobileNumber) {
		this.ptMobileNumber = ptMobileNumber;
	}

	@DynamoDBAttribute
	public String getPtCurrentState() {
		return ptCurrentState;
	}

	public void setPtCurrentState(String ptCurrentState) {
		this.ptCurrentState = ptCurrentState;
	}

	@DynamoDBAttribute
	public Long getPtCurrentDate() {
		return ptCurrentDate;
	}

	public void setPtCurrentDate(Long ptCurrentDate) {
		this.ptCurrentDate = ptCurrentDate;
	}

	@DynamoDBAttribute
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@DynamoDBAttribute
	public Map<String, List<Map<String, Long>>> getActivityDetail() {
		return activityDetail;
	}

	public void setActivityDetail(Map<String, List<Map<String, Long>>> activityDetail) {
		this.activityDetail = activityDetail;
	}

	/**
	 * @return the currentActivityTaskToken
	 */
	@DynamoDBAttribute
	public String getCurrentActivityTaskToken() {
		return currentActivityTaskToken;
	}

	/**
	 * @param currentActivityTaskToken the currentActivityTaskToken to set
	 */
	public void setCurrentActivityTaskToken(String currentActivityTaskToken) {
		this.currentActivityTaskToken = currentActivityTaskToken;
	}

	/**
	 * @return the currentActivityInput
	 */
	@DynamoDBAttribute
	public String getCurrentActivityInput() {
		return currentActivityInput;
	}

	/**
	 * @param currentActivityInput the currentActivityInput to set
	 */
	public void setCurrentActivityInput(String currentActivityInput) {
		this.currentActivityInput = currentActivityInput;
	}

	/**
	 * @return the nextActivity
	 */
	@DynamoDBAttribute
	public String getNextActivity() {
		return nextActivity;
	}

	/**
	 * @param nextActivity the nextActivity to set
	 */
	public void setNextActivity(String nextActivity) {
		this.nextActivity = nextActivity;
	}

	/**
	 * @return the nextActivityToken
	 */
	@DynamoDBAttribute
	public String getNextActivityToken() {
		return nextActivityToken;
	}

	/**
	 * @param nextActivityToken the nextActivityToken to set
	 */
	public void setNextActivityToken(String nextActivityToken) {
		this.nextActivityToken = nextActivityToken;
	}

}
```
### Repository 


```
package com.ecare.model.db.repository;

import java.util.Optional;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.ecare.model.db.PatientStatus;

@EnableScan
public interface PatientRepository extends CrudRepository<PatientStatus, String> {
	
	Optional<PatientStatus> findById(String id);
	
}
```


