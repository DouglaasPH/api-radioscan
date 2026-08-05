package com.douglaasph.clinic_api.adapters.outbound.repository.appointment;

import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.GetMetricsForEmployeeDashboardAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.results.EmployeesMetricsResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GetMetricsForEmployeeDashboardAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private GetMetricsForEmployeeDashboardAdapter getMetricsForEmployeeDashboardAdapter;

    @BeforeEach
    void setUp() {
        getMetricsForEmployeeDashboardAdapter = new GetMetricsForEmployeeDashboardAdapter(appointmentRepository);
    }

    private EmployeeJpaEntity persistEmployee(String email, String licenseNumber) {
        UserJpaEntity user = entityManager.persistAndFlush(
                new UserJpaEntity(null, "Dr. Silva", email, "pass123", Roles.EMPLOYEE)
        );
        return entityManager.persistAndFlush(
                new EmployeeJpaEntity(null, licenseNumber, Position.DOCTOR, user)
        );
    }

    private PatientJpaEntity persistPatient(String cpf, String email) {
        UserJpaEntity user = entityManager.persistAndFlush(
                new UserJpaEntity(null, "Paciente Teste", email, "pass123", Roles.PATIENT)
        );
        return entityManager.persistAndFlush(
                new PatientJpaEntity(null, cpf, "+55 81 90000-0000", user)
        );
    }

    private void persistAppointment(EmployeeJpaEntity employee, PatientJpaEntity patient, LocalDateTime dateHour, AppointmentStatus status) {
        AppointmentJpaEntity appointment = new AppointmentJpaEntity();
        appointment.setEmployee(employee);
        appointment.setPatient(patient);
        appointment.setDateHour(dateHour);
        appointment.setType(AppointmentType.REPORT_REVIEW);
        appointment.setStatus(status);
        entityManager.persistAndFlush(appointment);
    }
    @Test
    @DisplayName("Must correctly calculate the daily metrics for the specified employee")
    void case1() {
// arrange
        EmployeeJpaEntity doctor = persistEmployee("doc1@clinic.com", "LIC-100");
        EmployeeJpaEntity otherDoctor = persistEmployee("doc2@clinic.com", "LIC-200");
        PatientJpaEntity patient = persistPatient("12345678900", "patient@clinic.com");

        LocalDateTime todayMorning = LocalDate.now().atTime(9, 0);
        LocalDateTime todayAfternoon = LocalDate.now().atTime(15, 0);
        LocalDateTime yesterday = LocalDate.now().minusDays(1).atTime(10, 0);

        persistAppointment(doctor, patient, todayMorning, AppointmentStatus.COMPLETED);
        persistAppointment(doctor, patient, todayAfternoon, AppointmentStatus.AVAILABLE);

        persistAppointment(doctor, patient, yesterday, AppointmentStatus.COMPLETED); // Data de ontem
        persistAppointment(otherDoctor, patient, todayMorning, AppointmentStatus.COMPLETED); // Outro médico

        // act
        EmployeesMetricsResult metrics = getMetricsForEmployeeDashboardAdapter.get(doctor.getId());

        // assert
        assertThat(metrics).isNotNull();
        assertThat(metrics.getTotalAppointments()).isEqualTo(2);
        assertThat(metrics.getCompletedAppointments()).isEqualTo(1);
        assertThat(metrics.getPendingAppointments()).isEqualTo(1);
    }
}
