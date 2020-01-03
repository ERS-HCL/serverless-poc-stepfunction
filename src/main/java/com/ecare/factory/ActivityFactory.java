package com.ecare.factory;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ecare.activity.Activity;
import com.ecare.constants.ActivityEnum;

@Component
public class ActivityFactory {
	@Autowired
	@Qualifier("registrationActivity")
	private Activity registrationActivity;
	@Autowired
	@Qualifier("doctorCheckupActivity")
	private Activity doctorCheckupActivity;
	@Autowired
	@Qualifier("laboratoryActivity")
	private Activity laboratoryActivity;
	@Autowired
	@Qualifier("pharmacyActivity")
	private Activity pharmacyActivity;
	@Autowired
	@Qualifier("registrationReminderActivity")
	private Activity registrationReminderActivity;
	@Autowired
	@Qualifier("reviewActivity")
	private Activity reviewActivity;

	public Optional<Activity> getInstance(ActivityEnum activityEnum) {
		Optional<Activity> activityOptional = null;
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
