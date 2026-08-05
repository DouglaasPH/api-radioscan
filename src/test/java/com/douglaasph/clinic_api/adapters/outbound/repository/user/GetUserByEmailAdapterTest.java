package com.douglaasph.clinic_api.adapters.outbound.repository.user;

import com.douglaasph.clinic_api.adapter.outbound.repository.user.GetUserByEmailAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserRepository;
import com.douglaasph.clinic_api.domain.domain.User;
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
public class GetUserByEmailAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private GetUserByEmailAdapter getUserByEmailAdapter;

    @BeforeEach
    void setUp() {
        getUserByEmailAdapter = new GetUserByEmailAdapter(userRepository);
    }

    @Test
    @DisplayName("Must find user by email")
    void case1() {
        // arrange
        UserJpaEntity entity = new UserJpaEntity(
                null,
                "Example Example",
                "example@example.com",
                "example",
                Roles.PATIENT
        );
        entityManager.persistAndFlush(entity);

        // act
        Optional<User> result = getUserByEmailAdapter.findByEmail("example@example.com");

        // assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Example Example");
        assertThat(result.get().getEmail()).isEqualTo("example@example.com");
    }

    @Test
    @DisplayName("Should return an empty Optional when the email does not exist")
    void case2() {
        // act
        Optional<User> result = getUserByEmailAdapter.findByEmail("notexist@example.com");

        // assert
        assertThat(result).isEmpty();
    }
}
