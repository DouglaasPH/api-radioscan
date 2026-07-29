package com.douglaasph.clinic_api.repositories;

import com.douglaasph.clinic_api.controllers.dto.appointment.AllAvailablesAppointmentsResponseDto;
import com.douglaasph.clinic_api.controllers.dto.appointment.AppointmentManagementAdminDto;
import com.douglaasph.clinic_api.controllers.dto.appointment.DashboardMetricsForEmployeeResponseDto;
import com.douglaasph.clinic_api.controllers.dto.appointment.PatientEmployeeAppointmentResponseDto;
import com.douglaasph.clinic_api.models.entities.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
        SELECT new com.douglaasph.clinic_api.controllers.dto.appointment.PatientEmployeeAppointmentResponseDto(
            a.id,
            a.appointmentStatus,
            a.appointmentType,
            eUser.name,
            a.dateHour,
            ax.id,
            ax.releasedToPatient
        )
        FROM Appointment a
        LEFT JOIN a.employee e
        LEFT JOIN e.user eUser
        LEFT JOIN a.patient p
        LEFT JOIN p.user pUser
        LEFT JOIN a.xRayReport ax
        WHERE pUser.email = :email
    """)
    List<PatientEmployeeAppointmentResponseDto> findByPatientEmail(@Param("email") String email);

    List<Appointment> findByAppointmentStatusAndDateHourAfter(int appointmentStatus, LocalDateTime dateHour);

    @Query("""
        SELECT new com.douglaasph.clinic_api.controllers.dto.appointment.AllAvailablesAppointmentsResponseDto(
            a.id,
            a.dateHour,
            a.appointmentType,
            eUser.name
        )
        FROM Appointment a
        LEFT JOIN a.employee e
        LEFT JOIN e.user eUser
        LEFT JOIN a.patient p
        LEFT JOIN p.user pUser
        WHERE (a.appointmentStatus = 1)
            AND (:appointmentType IS NULL OR a.appointmentType = :appointmentType)
            AND (a.dateHour BETWEEN :startDate AND :endDate)
    """)
    Page<AllAvailablesAppointmentsResponseDto> findAllAvailablesAppointments(
            @Param("appointmentType") Integer appointmentType,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    @Query("""
        SELECT new com.douglaasph.clinic_api.controllers.dto.appointment.AppointmentManagementAdminDto(
            a.id,
            a.dateHour,
            a.appointmentStatus,
            a.appointmentType,
            eUser.name,
            pUser.name
        )
        FROM Appointment a
        LEFT JOIN a.employee e
        LEFT JOIN e.user eUser
        LEFT JOIN a.patient p
        LEFT JOIN p.user pUser
        WHERE (:appointmentStatus IS NULL OR a.appointmentStatus = :appointmentStatus)
            AND (:employeeName IS NULL OR eUser.name = :employeeName)
    """)
    Page<AppointmentManagementAdminDto> findAllAppointmentsManagement(
            @Param("appointmentStatus") Integer appointmentStatus,
            @Param("employeeName") String employeeName,
            Pageable pageable
    );

    @Query("""
        SELECT new com.douglaasph.clinic_api.controllers.dto.appointment.DashboardMetricsForEmployeeResponseDto(
            COUNT(a),
            SUM(CASE WHEN a.appointmentStatus = 4 THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.appointmentStatus = 1 THEN 1 ELSE 0 END)
        )
        FROM Appointment a
        WHERE (a.dateHour BETWEEN :startDate AND :endDate)
            AND (a.employee.id = :userId)
    """)
    DashboardMetricsForEmployeeResponseDto getDailyMetrics(
            @Param("userId") Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
