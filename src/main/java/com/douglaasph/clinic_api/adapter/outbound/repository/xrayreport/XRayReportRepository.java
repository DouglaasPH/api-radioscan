package com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface XRayReportRepository extends JpaRepository<XRayReportJpaEntity, Long> {
    boolean existsByAppointment_Patient_IdAndProcessingStatusNot(Long patientId, Integer processingStatus);

    Optional<XRayReportJpaEntity> findByAppointmentId(Long appointmentId);

    List<XRayReportJpaEntity> findByCreatedAtBetween(Instant startOfDay, Instant endOfDay);
}
