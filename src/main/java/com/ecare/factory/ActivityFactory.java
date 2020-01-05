package com.ecare.factory;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ecare.activity.StepFunctionActivity;
import com.ecare.constants.ActivityEnum;

@Component
public class ActivityFactory {
	@Autowired
	@Qualifier("registrationActivity")
	private StepFunctionActivity  registrationActivity;
	@Autowired
	@Qualifier("doctorCheckupActivity")
	private StepFunctionActivity  doctorCheckupActivity;
	@Autowired
	@Qualifier("laboratoryActivity")
	private StepFunctionActivity  laboratoryActivity;
	@Autowired
	@Qualifier("pharmacyActivity")
	private StepFunctionActivity  pharmacyActivity;
	@Autowired
	@Qualifier("registrationReminderActivity")
	private StepFunctionActivity  registrationReminderActivity;
	@Autowired
	@Qualifier("reviewActivity")
	private StepFunctionActivity  reviewActivity;

	public Optional<StepFunctionActivity> getInstance(ActivityEnum activityEnum) {
		Optional<StepFunctionActivity> activityOptional = null;
		switch (activityEnum) {
		case REGISTER_PATIENT:
			activityOptional = Optional.of(registrationActivity);
			break;
		case REGISTRATION_REMINDER:
			activityOptional = Optional.of(registrationReminderActivity);
			break;
		case DOCTORCHEKUP:
			activityOptional = Optional.of(doctorCheckupActivity);
			break;
		case LABORATORY:
			activityOptional = Optional.of(laboratoryActivity);
			break;
		case REVIEW:
			activityOptional = Optional.of(reviewActivity);
			break;
		case PHARMACY:
			activityOptional = Optional.of(pharmacyActivity);
			break;
		}
		return activityOptional;
	}
}
