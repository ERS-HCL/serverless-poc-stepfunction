package com.ecare.model.db;

public class ActivityDetails {
	private String activityName;
	private Long activityTime;
	private String taskTokenForActivity;
	private String inputStringforActivity;
	/**
	 * @return the activityName
	 */
	public String getActivityName() {
		return activityName;
	}
	/**
	 * @param activityName the activityName to set
	 */
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	/**
	 * @return the activityTime
	 */
	public Long getActivityTime() {
		return activityTime;
	}
	/**
	 * @param activityTime the activityTime to set
	 */
	public void setActivityTime(Long activityTime) {
		this.activityTime = activityTime;
	}
	/**
	 * @return the taskTokenForActivity
	 */
	public String getTaskTokenForActivity() {
		return taskTokenForActivity;
	}
	/**
	 * @param taskTokenForActivity the taskTokenForActivity to set
	 */
	public void setTaskTokenForActivity(String taskTokenForActivity) {
		this.taskTokenForActivity = taskTokenForActivity;
	}
	/**
	 * @return the inputStringforActivity
	 */
	public String getInputStringforActivity() {
		return inputStringforActivity;
	}
	/**
	 * @param inputStringforActivity the inputStringforActivity to set
	 */
	public void setInputStringforActivity(String inputStringforActivity) {
		this.inputStringforActivity = inputStringforActivity;
	}

}
