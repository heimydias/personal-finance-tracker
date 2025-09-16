package dias.heimy.domain.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for ValidPasswordValidator")
class ValidPasswordValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintViolationBuilder violationBuilder;

    private ValidPasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ValidPasswordValidator();
        var annotation = mock(ValidPassword.class);
        when(annotation.minLength()).thenReturn(8);
        when(annotation.requireUppercase()).thenReturn(true);
        when(annotation.requireLowercase()).thenReturn(true);
        when(annotation.requireDigit()).thenReturn(true);
        when(annotation.requireSpecialChar()).thenReturn(true);
        validator.initialize(annotation);
    }

    @Test
    @DisplayName("Should return true for valid password")
    void shouldReturnTrue_ForValidPassword() {

        var validPassword = "MyStr0ng123!";

        var result = validator.isValid(validPassword, context);

        assertThat(result).isTrue();
        verify(context, never()).disableDefaultConstraintViolation();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "Pass1!", "mystr0ng123!", "MYSTR0NG123!", "MyStrong!", "MyStr0ng123"})
    @DisplayName("Should return false for invalid passwords")
    void shouldReturnFalse_ForInvalidPasswords(String invalidPassword) {

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        var result = validator.isValid(invalidPassword, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("Should return false for null password")
    void shouldReturnFalse_ForNullPassword() {

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        var result = validator.isValid(null, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Password123!", "123456", "admin", "qwerty123"})
    @DisplayName("Should return false for common passwords")
    void shouldReturnFalse_ForCommonPasswords(String commonPassword) {

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        var result = validator.isValid(commonPassword, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
    }

    @ParameterizedTest
    @ValueSource(strings = {"MyStr0ng123!", "MyStr0ng@2024!", "Secure123$Code", "ValidP@ssw0rd!"})
    @DisplayName("Should return true for valid strong passwords")
    void shouldReturnTrue_ForValidPasswords(String validPassword) {

        var result = validator.isValid(validPassword, context);

        assertThat(result).isTrue();
        verify(context, never()).disableDefaultConstraintViolation();
    }
}
