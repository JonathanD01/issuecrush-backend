package com.jonathand.issuecrush.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    private final Faker faker = new Faker();

    @Autowired
    private UserRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldFindUserByEmail() {
        // given
        String email = faker.internet()
            .emailAddress();
        String password = faker.internet()
            .password();

        User user = User.builder()
            .email(email)
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .password(password)
            .userRole(UserRole.USER)
            .build();
        underTest.save(user);

        // when
        boolean expected = underTest.findByEmail(email)
            .isPresent();

        // then
        assertThat(expected).isTrue();
    }

    @Test
    void itShouldNotFindUserByEmail() {
        // given
        String email = faker.internet()
            .emailAddress();

        // when
        boolean expected = underTest.findByEmail(email)
            .isPresent();

        // then
        assertThat(expected).isFalse();
    }

    @Test
    void itShouldFindExistingUserByEmail() {
        // given
        String email = faker.internet()
            .emailAddress();

        User user = User.builder()
            .email(email)
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .password(faker.internet()
                .password())
            .userRole(UserRole.USER)
            .build();

        underTest.save(user);

        // when
        boolean expected = underTest.existsByEmail(email);

        // then
        assertThat(expected).isTrue();
    }

    @Test
    void itShouldNotFindExistingUserByEmail() {
        // given
        String email = faker.internet()
            .emailAddress();

        // when
        boolean expected = underTest.existsByEmail(email);

        // then
        assertThat(expected).isFalse();
    }

}
