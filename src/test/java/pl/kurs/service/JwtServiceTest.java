package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtServiceTest {
    private final JwtService jwtService = new JwtService();

    @Test
    void shouldGenerateValidToken() {
        //given
        String username = "userTest";

        //when
        String token = jwtService.generateToken(username);

        //then
        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo(username);
    }

    @Test
    void shouldValidateCorrectToken() {
        //given
        String username = "userTest";
        String token = jwtService.generateToken(username);

        UserDetails userDetails = User.withUsername(username)
                .password("password")
                .authorities("ROLE_USER")
                .build();

        //when
        boolean isValid = jwtService.validateToken(token, userDetails);

        //then
        assertThat(isValid).isTrue();
    }

    @Test
    void shouldFailValidationForWrongUser() {
        //given
        String token = jwtService.generateToken("realUser");
        UserDetails wrongUser = User.withUsername("wrongUser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        //when
        boolean isValid = jwtService.validateToken(token, wrongUser);

        //then
        assertThat(isValid).isFalse();
    }


}