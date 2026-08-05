package com.douglaasph.clinic_api.adapters.outbound.repository.appointment;

import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.GetAppointmentsLinkedToThePatientAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.results.AppointmentsLinkedToThePatientResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GetAppointmentsLinkedToThePatientAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private GetAppointmentsLinkedToThePatientAdapter getAppointmentsLinkedToThePatientAdapter;

    @BeforeEach
    void setUp() { getAppointmentsLinkedToThePatientAdapter = new GetAppointmentsLinkedToThePatientAdapter(appointmentRepository); }

    private EmployeeJpaEntity persistedEmployee(String email, String licenseNumber) {
        UserJpaEntity user = entityManager.persistAndFlush(
                new UserJpaEntity(null, "Example example", email, "pass123", Roles.EMPLOYEE)
        );
        return entityManager.persistAndFlush(
                new EmployeeJpaEntity(null, licenseNumber, Position.DOCTOR, user)
        );
    }

    private PatientJpaEntity persistedPatient(String cpf, String email) {
        UserJpaEntity user = entityManager.persistAndFlush(
                new UserJpaEntity(null, "Paciente Teste", email, "pass123", Roles.PATIENT)
        );
        return entityManager.persistAndFlush(
                new PatientJpaEntity(null, cpf, "+55 81 90000-0000", user)
        );
    }

    private void persistAppointment(EmployeeJpaEntity employee, PatientJpaEntity patient, LocalDateTime dateHour, AppointmentType type, AppointmentStatus status) {
        AppointmentJpaEntity appointment = new AppointmentJpaEntity();
        appointment.setEmployee(employee);
        appointment.setPatient(patient);
        appointment.setDateHour(dateHour);
        appointment.setType(type);
        appointment.setStatus(status);
        entityManager.persistAndFlush(appointment);
    }

    @Test
    @DisplayName("Must return only the appointments linked to the patient for the specified patient email.")
    void case1() {
        // arrange
        LocalDate targetDate = LocalDate.of(2026, 8, 10);
        String patientEmail = "patient1@example.com";
        String otherPatientEmail = "otherpatient@example.com";

        EmployeeJpaEntity doctor = persistedEmployee("doc1@clinic.com", "LIC-100");
        PatientJpaEntity targetPatient = persistedPatient("12345678900", patientEmail);
        PatientJpaEntity otherPatient = persistedPatient("98765432100", otherPatientEmail);


        persistAppointment(doctor, targetPatient, targetDate.atTime(10, 0), AppointmentType.REPORT_REVIEW, AppointmentStatus.SCHEDULED);
        persistAppointment(doctor, targetPatient, targetDate.atTime(14, 0), AppointmentType.EXAM_CAPTURE, AppointmentStatus.SCHEDULED);

        persistAppointment(doctor, otherPatient, targetDate.atTime(16, 0), AppointmentType.REPORT_REVIEW, AppointmentStatus.SCHEDULED);

        // act
        List<AppointmentsLinkedToThePatientResult> result = getAppointmentsLinkedToThePatientAdapter.get(patientEmail);

        // assert
        assertThat(result)
                .isNotEmpty()
                .hasSize(2);

        assertThat(result.get(0).getDateHour()).isEqualTo(targetDate.atTime(10, 0));
        assertThat(result.get(0).getAppointmentType()).isEqualTo(AppointmentType.REPORT_REVIEW.getCode());
        assertThat(result.get(0).getEmployeeName()).isEqualTo("Example example");

        assertThat(result.get(1).getDateHour()).isEqualTo(targetDate.atTime(14, 0));
        assertThat(result.get(1).getAppointmentType()).isEqualTo(AppointmentType.EXAM_CAPTURE.getCode());
        assertThat(result.get(1).getEmployeeName()).isEqualTo("Example example");

        assertThat(result)
                .extracting(AppointmentsLinkedToThePatientResult::getEmployeeName)
                .containsOnly("Example example");
    }
}
