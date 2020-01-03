package com.ecare.constants;

public enum ActivityEnum {
	REGISTER_PATIENT("REGISTER_PATIENT",0),
	REGISTRATION_REMINDER("REGISTRATION_REMINDER",1),
	LABORATORY("LABORATORY",2),
	DOCTORCHEKUP("DOCTORCHEKUP",3),
	REVIEW("REVIEW",4),
	PHARMACY("PHARMACY",5);
	private String activityName;
	private int activitySequenceNo;
	ActivityEnum(String activityName,int activitySequenceNo){
		this.activityName=activityName;
		this.activitySequenceNo = activitySequenceNo;
	}
	public String getActivityName() {
		return activityName;
	}
	public int getActivitySequenceNo() {
		return activitySequenceNo;
	}
	
}
