package com.douglaasph.clinic_api.adapter.outbound.repository.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<PatientJpaEntity, Long> {
}
