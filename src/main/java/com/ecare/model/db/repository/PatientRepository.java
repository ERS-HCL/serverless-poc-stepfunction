package com.ecare.model.db.repository;

import java.util.Optional;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.ecare.model.db.PatientStatus;

@EnableScan
public interface PatientRepository extends CrudRepository<PatientStatus, String> {
	
	Optional<PatientStatus> findById(String id);
	
}
