package com.ecare.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecare.model.db.PatientStatus;
import com.ecare.model.db.repository.PatientRepository;
import com.ecare.model.response.PatientDetail;
import com.ecare.utilities.DateTimeUtils;

@Component
public class GetDataHandler {

	@Autowired
	private PatientRepository patientRepository;
	
	public PatientDetail getData(String patientId) {
		//TODO add copy and beautification logic here 
		PatientDetail ptDetail = null; 
		Optional<PatientStatus> ptStatus = patientRepository.findById(patientId);
		if(ptStatus.isPresent()) {
			ptDetail = new PatientDetail();
			ptDetail.setActivityDetail(ptStatus.get().getActivityDetail());
			ptDetail.setPatientId(ptStatus.get().getPatientId());
			ptDetail.setEmail(ptStatus.get().getEmail());
			ptDetail.setNextActivity(ptStatus.get().getNextActivity());
			ptDetail.setPtMobileNumber(ptStatus.get().getPtMobileNumber());
			ptDetail.setPtCurrentDate(DateTimeUtils.getDateFromEPOCSeconds(ptStatus.get().getPtCurrentDate(),null));
			ptDetail.setPtCurrentState(ptStatus.get().getPtCurrentState());
		}
		
		return ptDetail;
	}
	
}
