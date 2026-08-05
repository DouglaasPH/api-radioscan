package com.douglaasph.clinic_api.adapters.outbound.repository.appointment;

import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.GetAppointmentsManagementWithPaginationAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.results.AppointmentsManagementResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GetAppointmentsManagementWithPaginationAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private GetAppointmentsManagementWithPaginationAdapter getAppointmentsManagementWithPaginationAdapter;

    @BeforeEach
    void setUp() {
        getAppointmentsManagementWithPaginationAdapter = new GetAppointmentsManagementWithPaginationAdapter(appointmentRepository);
    }

    private EmployeeJpaEntity persistEmployee(String name, String email, String licenseNumber) {
        UserJpaEntity user = entityManager.persistAndFlush(
                new UserJpaEntity(null, name, email, "pass123", Roles.EMPLOYEE)
        );
        return entityManager.persistAndFlush(
                new EmployeeJpaEntity(null, licenseNumber, Position.DOCTOR, user)
        );
    }

    private PatientJpaEntity persistPatient(String name, String cpf, String email) {
        UserJpaEntity user = entityManager.persistAndFlush(
                new UserJpaEntity(null, name, email, "pass123", Roles.PATIENT)
        );
        return entityManager.persistAndFlush(
                new PatientJpaEntity(null, cpf, "+55 81 90000-0000", user)
        );
    }

    private void persistAppointment(EmployeeJpaEntity employee, PatientJpaEntity patient, LocalDateTime dateHour, AppointmentStatus status, AppointmentType type) {
        AppointmentJpaEntity appointment = new AppointmentJpaEntity();
        appointment.setEmployee(employee);
        appointment.setPatient(patient);
        appointment.setDateHour(dateHour);
        appointment.setStatus(status);
        appointment.setType(type);
        entityManager.persistAndFlush(appointment);
    }

    @Test
    @DisplayName("Should return the unfiltered scheduling page with a page size of 4")
    void case1() {
        // arrange
        EmployeeJpaEntity doctor = persistEmployee("Dr. Silva", "doc1@clinic.com", "LIC-100");
        PatientJpaEntity patient = persistPatient("Paciente 1", "12345678900", "patient1@clinic.com");

        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 5; i++) {
            persistAppointment(doctor, patient, now.plusHours(i), AppointmentStatus.AVAILABLE, AppointmentType.REPORT_REVIEW);
        }

        // act
        Page<AppointmentsManagementResult> result = getAppointmentsManagementWithPaginationAdapter.get(null, null, 0);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(4);
        assertThat(result.getTotalElements()).isEqualTo(5L);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent().get(0).getEmployeeName()).isEqualTo("Dr. Silva");
        assertThat(result.getContent().get(0).getPatientName()).isEqualTo("Paciente 1");
    }

    @Test
    @DisplayName("Must correctly filter appointments by status and employee name")
    void case2() {
        // arrange
        EmployeeJpaEntity targetDoctor = persistEmployee("Dr. Roberto", "doc2@clinic.com", "LIC-200");
        EmployeeJpaEntity otherDoctor = persistEmployee("Dra. Ana", "doc3@clinic.com", "LIC-300");
        PatientJpaEntity patient = persistPatient("Paciente Teste", "98765432100", "patient2@clinic.com");

        LocalDateTime now = LocalDateTime.now();
        AppointmentStatus targetStatus = AppointmentStatus.AVAILABLE;
        AppointmentStatus otherStatus = AppointmentStatus.SCHEDULED;

        persistAppointment(targetDoctor, patient, now.plusHours(1), targetStatus, AppointmentType.REPORT_REVIEW);
        persistAppointment(targetDoctor, patient, now.plusHours(2), otherStatus, AppointmentType.REPORT_REVIEW);
        persistAppointment(otherDoctor, patient, now.plusHours(3), targetStatus, AppointmentType.REPORT_REVIEW);

        // act
        Page<AppointmentsManagementResult> result = getAppointmentsManagementWithPaginationAdapter.get(targetStatus, "Dr. Roberto", 0);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);

        AppointmentsManagementResult item = result.getContent().get(0);
        assertThat(item.getEmployeeName()).isEqualTo("Dr. Roberto");
        assertThat(item.getPatientName()).isEqualTo("Paciente Teste");
        assertThat(item.getAppointmentStatus()).isEqualTo(targetStatus.getCode());
    }
}
