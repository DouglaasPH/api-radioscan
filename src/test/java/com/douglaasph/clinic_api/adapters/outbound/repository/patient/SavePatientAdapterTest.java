package com.douglaasph.clinic_api.adapters.outbound.repository.patient;

import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.SavePatientAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaMapper;
import com.douglaasph.clinic_api.domain.domain.Patient;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class SavePatientAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PatientRepository patientRepository;

    private SavePatientAdapter savePatientAdapter;

    @BeforeEach
    void setUp() {
        savePatientAdapter = new SavePatientAdapter(patientRepository);
    }

    @Test
    @DisplayName("Must save patient")
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
        Patient patient = new Patient(
                null,
                "000.000.000-00",
                "+55 81 90000-0000",
                UserJpaMapper.toDomain(savedUser)
        );

        // act
        Patient result = savePatientAdapter.save(patient);

        // assert
        assertThat(result.getCpf()).isEqualTo("000.000.000-00");
        assertThat(result.getPhone()).isEqualTo("+55 81 90000-0000");
    }

    @Test
    @DisplayName("Should propagate exception when email already exists")
    void case2() {
        // arrange
        UserJpaEntity userEntityFirst = new UserJpaEntity(
                null,
                "Patient User",
                "patient@example.com",
                "password123",
                Roles.PATIENT
        );
        UserJpaEntity savedUserFirst = entityManager.persistAndFlush(userEntityFirst);
        PatientJpaEntity patientFirst = new PatientJpaEntity(
                null,
                "000.000.000-00",
                "+55 81 90000-0000",
                savedUserFirst
        );
        entityManager.persistAndFlush(patientFirst);
        UserJpaEntity userEntityDuplicate = new UserJpaEntity(
                null,
                "Patient User",
                "patient2@example.com",
                "password123",
                Roles.PATIENT
        );
        UserJpaEntity savedUserDuplicate = entityManager.persistAndFlush(userEntityDuplicate);
        Patient patientDuplicate = new Patient(
                null,
                "000.000.000-00",
                "+55 81 90000-0000",
                UserJpaMapper.toDomain(savedUserDuplicate)
        );

        // act + assert
        assertThatThrownBy(() -> savePatientAdapter.save(patientDuplicate))
                .isInstanceOf(DatabaseException.class)
                .hasMessage("Registration error: CPF already exist.");
    }
}
