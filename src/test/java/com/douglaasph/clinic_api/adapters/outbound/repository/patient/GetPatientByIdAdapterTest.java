package com.douglaasph.clinic_api.adapters.outbound.repository.patient;

import com.douglaasph.clinic_api.adapter.outbound.repository.patient.GetPatientByIdAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.domain.domain.Patient;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GetPatientByIdAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PatientRepository patientRepository;

    private GetPatientByIdAdapter getPatientByIdAdapter;

    @BeforeEach
    void setUp() {
        getPatientByIdAdapter = new GetPatientByIdAdapter(patientRepository);
    }

    @Test
    @DisplayName("Must find patient by id")
    void case1() {
        // arrange
        UserJpaEntity userEntity = new UserJpaEntity(
                null,
                "Patient User",
                "patient@example.com",
                "password123",
                Roles.PATIENT
        );
        UserJpaEntity savedUser = entityManager.persistAndFlush(userEntity);
        PatientJpaEntity patientEntity = new PatientJpaEntity(
                null,
                "000.000.000-00",
                "+55 81 90000-0000",
                savedUser
        );
        PatientJpaEntity savedPatient = entityManager.persistAndFlush(patientEntity);

        // act
        Optional<Patient> result = getPatientByIdAdapter.findById(savedPatient.getId());

        // assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedPatient.getId());
        assertThat(result.get().getCpf()).isEqualTo("000.000.000-00");
        assertThat(result.get().getPhone()).isEqualTo("+55 81 90000-0000");
    }

    @Test
    @DisplayName("Should return an empty Optional when the id does not exists")
    void case2() {
        // act
        Optional<Patient> result = getPatientByIdAdapter.findById(1L);

        // assert
        assertThat(result).isEmpty();
    }
}
