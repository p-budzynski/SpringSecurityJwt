package pl.kurs.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PasswordValidatorTest {
    private final PasswordValidator validator = new PasswordValidator();

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void shouldReturnFalseWhenPasswordIsNull() {
        //when
        boolean result = validator.isValid(null, context);

        //then
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenPasswordIsBlank() {
        //when
        boolean result = validator.isValid("    ", context);

        //then
        assertFalse(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Password123!", "passWord789*", "paSSWOrd43&^"})
    void shouldReturnTrueWhenPasswordIsValid(String password) {
        //when
        boolean result = validator.isValid(password, context);

        //then
        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"pass", "PASSWORD$3", "password1!", "aA1#", "passworDD1234", "PaSsWoRd@#$"})
    void shouldReturnFalseWhenPasswordIsInvalid(String password) {
        //when
        boolean result = validator.isValid(password, context);

        //then
        assertFalse(result);
    }

}