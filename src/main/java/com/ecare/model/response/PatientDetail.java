package com.ecare.model.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecare.utilities.DateTimeUtils;

public class PatientDetail {
	private String patientId;
	private String ptName;
	private String ptMobileNumber;
	private String email;
	private String ptCurrentState;
	private String ptCurrentDate;
	private String nextActivity;
	private Map<String, List<Map<String, String>>> activityDetails;

	/**
	 * @return the patientId
	 */
	public String getPatientId() {
		return patientId;
	}

	/**
	 * @param patientId the patientId to set
	 */
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	/**
	 * @return the ptName
	 */
	public String getPtName() {
		return ptName;
	}

	/**
	 * @param ptName the ptName to set
	 */
	public void setPtName(String ptName) {
		this.ptName = ptName;
	}

	/**
	 * @return the ptMobileNumber
	 */
	public String getPtMobileNumber() {
		return ptMobileNumber;
	}

	/**
	 * @param ptMobileNumber the ptMobileNumber to set
	 */
	public void setPtMobileNumber(String ptMobileNumber) {
		this.ptMobileNumber = ptMobileNumber;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the ptCurrentState
	 */
	public String getPtCurrentState() {
		return ptCurrentState;
	}

	/**
	 * @param ptCurrentState the ptCurrentState to set
	 */
	public void setPtCurrentState(String ptCurrentState) {
		this.ptCurrentState = ptCurrentState;
	}

	/**
	 * @return the ptCurrentDate
	 */
	public String getPtCurrentDate() {
		return ptCurrentDate;
	}

	/**
	 * @param ptCurrentDate the ptCurrentDate to set
	 */
	public void setPtCurrentDate(String ptCurrentDate) {
		this.ptCurrentDate = ptCurrentDate;
	}

	/**
	 * @return the nextActivity
	 */
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
	 * @return the activityDetail
	 */
	public Map<String, List<Map<String, String>>> getActivityDetail() {
		return activityDetails;
	}

	/**
	 * @param activityDetail the activityDetail to set
	 */
	public void setActivityDetail(Map<String, List<Map<String, Long>>> activityDetail) {
		activityDetails = new HashMap<String, List<Map<String, String>>>();
		List<Map<String, String>> dataList = null;
		Map<String, String> convertedDataMap = null;
		for (String key : activityDetail.keySet()) {
			List<Map<String, Long>> activityDetailsList = activityDetail.get(key);
			dataList = new ArrayList<Map<String, String>>();
			for (Map<String, Long> activityDataMap : activityDetailsList) {
				for (String dataKey : activityDataMap.keySet()) {
					Long epochSeconds = activityDataMap.get(dataKey);
					String convertedDateStr = DateTimeUtils.getDateFromEPOCSeconds(epochSeconds, null);
					convertedDataMap = new HashMap<String, String>();
					convertedDataMap.put(dataKey, convertedDateStr);
					dataList.add(convertedDataMap);
				}
			}
			activityDetails.put(key, dataList);
		}
	}
}
