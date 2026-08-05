package com.douglaasph.clinic_api.adapters.outbound.repository.user;

import com.douglaasph.clinic_api.adapter.outbound.repository.user.SaveUserAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserRepository;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class SaveUserAdapterTest {

    @Autowired
    private UserRepository userRepository;

    private SaveUserAdapter saveUserAdapter;

    @BeforeEach
    void setUp() {
        saveUserAdapter = new SaveUserAdapter(userRepository);
    }

    @Test
    @DisplayName("Must save user")
    void case1() {
        // arrange
        User user = new User(
                null,
                "Example example",
                "example@example.com",
                "example",
                Roles.ADMIN,
                null,
                null
        );

        // act
        User savedUser = saveUserAdapter.save(user);

        // assert
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Example example");
        assertThat(savedUser.getEmail()).isEqualTo("example@example.com");
    }

    @Test
    @DisplayName("Should propagate exception when email already exists")
    void case2() {
        // arrange
        User firstUser = new User(
                null,
                "Douglas Silva",
                "duplicate@example.com",
                "password123",
                Roles.PATIENT,
                null,
                null
        );
        saveUserAdapter.save(firstUser);
        User duplicateUser = new User(
                null,
                "Outro Usuario",
                "duplicate@example.com", // Mesmo e-mail
                "password456",
                Roles.PATIENT,
                null,
                null
        );

        // act + assert
        assertThatThrownBy(() -> saveUserAdapter.save(duplicateUser))
                .isInstanceOf(DatabaseException.class)
                .hasMessage("Registration error: Email already exist.");
    }
}
