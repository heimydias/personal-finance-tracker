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

    @Test
    @DisplayName("Should return false for null password")
    void shouldReturnFalse_ForNullPassword() {

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        var result = validator.isValid(null, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("Should return false for empty password")
    void shouldReturnFalse_ForEmptyPassword() {

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        var result = validator.isValid("", context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("Should return false for password too short")
    void shouldReturnFalse_ForPasswordTooShort() {

        var shortPassword = "Pass1!";
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        var result = validator.isValid(shortPassword, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("Should return false for password without uppercase")
    void shouldReturnFalse_ForPasswordWithoutUppercase() {

        var passwordWithoutUpper = "mystr0ng123!";
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        var result = validator.isValid(passwordWithoutUpper, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("Should return false for password without lowercase")
    void shouldReturnFalse_ForPasswordWithoutLowercase() {

        var passwordWithoutLower = "MYSTR0NG123!";
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        var result = validator.isValid(passwordWithoutLower, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("Should return false for password without digits")
    void shouldReturnFalse_ForPasswordWithoutDigits() {

        var passwordWithoutDigits = "MyStrong!";
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        var result = validator.isValid(passwordWithoutDigits, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("Should return false for password without special characters")
    void shouldReturnFalse_ForPasswordWithoutSpecialChars() {

        var passwordWithoutSpecial = "MyStr0ng123";
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        var result = validator.isValid(passwordWithoutSpecial, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("Should return false for password with common patterns")
    void shouldReturnFalse_ForPasswordWithCommonPatterns() {

        var commonPassword = "Password123!";
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        var result = validator.isValid(commonPassword, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("Should return true for strong password with all requirements")
    void shouldReturnTrue_ForStrongPassword() {

        var strongPassword = "MyStr0ng@2024!";

        var result = validator.isValid(strongPassword, context);

        assertThat(result).isTrue();
        verify(context, never()).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("Should return true for another valid password")
    void shouldReturnTrue_ForAnotherValidPassword() {

        var validPassword = "Secure123$Code";

        var result = validator.isValid(validPassword, context);

        assertThat(result).isTrue();
        verify(context, never()).disableDefaultConstraintViolation();
    }
}
