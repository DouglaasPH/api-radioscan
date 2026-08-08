package com.douglaasph.clinic_api.adapter.outbound.repository.appointment;

import com.douglaasph.clinic_api.domain.results.AppointmentsLinkedToThePatientResult;
import com.douglaasph.clinic_api.domain.results.AppointmentsManagementResult;
import com.douglaasph.clinic_api.domain.results.AvailabilitiesAppointmentsResult;
import com.douglaasph.clinic_api.domain.results.EmployeesMetricsResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentJpaEntity, Long> {

    @Query("""
        SELECT new com.douglaasph.clinic_api.domain.results.AppointmentsLinkedToThePatientResult(
            a.id,
            a.appointmentStatus,
            a.appointmentType,
            eUser.name,
            a.dateHour,
            ax.id,
            ax.releasedToPatient,
            CASE WHEN (ax.processingStatus NOT IN (3, 4)) THEN true ELSE false END,
                ax.finalMedicalDiagnosis
        )
        FROM AppointmentJpaEntity a
        LEFT JOIN a.employee e
        LEFT JOIN e.user eUser
        LEFT JOIN a.patient p
        LEFT JOIN p.user pUser
        LEFT JOIN a.xRayReport ax
        WHERE pUser.email = :email
    """)
    List<AppointmentsLinkedToThePatientResult> findByPatientEmail(@Param("email") String email);

    List<AppointmentJpaEntity> findByAppointmentStatusAndDateHourAfter(int appointmentStatus, LocalDateTime dateHour);

    @Query("""
        SELECT new com.douglaasph.clinic_api.domain.results.AvailabilitiesAppointmentsResult(
            a.id,
            a.dateHour,
            a.appointmentType,
            eUser.name
        )
        FROM AppointmentJpaEntity a
        LEFT JOIN a.employee e
        LEFT JOIN e.user eUser
        LEFT JOIN a.patient p
        LEFT JOIN p.user pUser
        WHERE (a.appointmentStatus = 1)
            AND (:appointmentType IS NULL OR a.appointmentType = :appointmentType)
            AND (a.dateHour BETWEEN :startDate AND :endDate)
    """)
    Page<AvailabilitiesAppointmentsResult> findAllAvailablesAppointments(
            @Param("appointmentType") Integer appointmentType,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    @Query("""
        SELECT new com.douglaasph.clinic_api.domain.results.AppointmentsManagementResult(
            a.id,
            a.dateHour,
            a.appointmentStatus,
            a.appointmentType,
            eUser.name,
            pUser.name
        )
        FROM AppointmentJpaEntity a
        LEFT JOIN a.employee e
        LEFT JOIN e.user eUser
        LEFT JOIN a.patient p
        LEFT JOIN p.user pUser
        WHERE (:appointmentStatus IS NULL OR a.appointmentStatus = :appointmentStatus)
            AND (:employeeName IS NULL OR eUser.name = :employeeName)
    """)
    Page<AppointmentsManagementResult> findAllAppointmentsManagement(
            @Param("appointmentStatus") Integer appointmentStatus,
            @Param("employeeName") String employeeName,
            Pageable pageable
    );

    @Query("""
        SELECT new com.douglaasph.clinic_api.domain.results.EmployeesMetricsResult(
            CAST(COUNT(a) AS integer),
            CAST(SUM(CASE WHEN a.appointmentStatus = 4 THEN 1 ELSE 0 END) AS integer),
            CAST(SUM(CASE WHEN a.appointmentStatus = 1 THEN 1 ELSE 0 END) AS integer)
        )
        FROM AppointmentJpaEntity a
        WHERE (a.dateHour BETWEEN :startDate AND :endDate)
            AND (a.employee.id = :userId)
    """)
    EmployeesMetricsResult getDailyMetrics(
            @Param("userId") Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
