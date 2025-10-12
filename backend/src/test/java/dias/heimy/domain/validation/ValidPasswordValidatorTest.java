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
import org.junit.jupiter.params.provider.CsvSource;
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
    @ValueSource(strings = {"Pass1!", "mystr0ng123!", "MYSTR0NG123!", "MyStrong!", "MyStr0ng123"})
    @DisplayName("Should return false for invalid passwords")
    void shouldReturnFalse_ForInvalidPasswords(String invalidPassword) {

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        var result = validator.isValid(invalidPassword, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("Should return true for null password (optional field)")
    void shouldReturnTrue_ForNullPassword() {

        var result = validator.isValid(null, context);

        assertThat(result).isTrue();
        verify(context, never()).disableDefaultConstraintViolation();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("Should return true for empty/blank password (optional field)")
    void shouldReturnTrue_ForEmptyPassword(String emptyPassword) {

        var result = validator.isValid(emptyPassword, context);

        assertThat(result).isTrue();
        verify(context, never()).disableDefaultConstraintViolation();
    }

    @ParameterizedTest
    @ValueSource(strings = {"MyStr0ng123!", "MyStr0ng@2024!", "Secure123$Code", "ValidP@ssw0rd!"})
    @DisplayName("Should return true for valid strong passwords")
    void shouldReturnTrue_ForValidPasswords(String validPassword) {

        var result = validator.isValid(validPassword, context);

        assertThat(result).isTrue();
        verify(context, never()).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("Should return false when password is too short")
    void shouldReturnFalse_WhenPasswordIsTooShort() {

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        var result = validator.isValid("Sh0rt!", context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(anyString());
    }

    @ParameterizedTest(name = "Should return false when password missing {0}")
    @CsvSource(
            delimiterString = "|",
            value = {
                "uppercase letter|mystr0ng123!",
                "lowercase letter|MYSTR0NG123!",
                "digit|MyStrong!",
                "special character|MyStr0ng123"
            })
    @DisplayName("Should return false when password is missing required elements")
    void shouldReturnFalse_WhenMissingRequiredElement(String requirement, String password) {

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        var result = validator.isValid(password, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("Should accept various special characters")
    void shouldAcceptVariousSpecialCharacters() {

        String[] specialChars = {
            "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+", "-", "=", "[", "]", "{", "}", ";", ":", "'",
            "\"", "\\", "|", ",", ".", "<", ">", "/", "?"
        };

        for (String specialChar : specialChars) {
            String password = "MyStr0ng" + specialChar;
            var result = validator.isValid(password, context);
            assertThat(result)
                    .as("Password with special char '%s' should be valid", specialChar)
                    .isTrue();
        }
    }

    @Test
    @DisplayName("Should accept password without uppercase when requireUppercase is false")
    void shouldAcceptPasswordWithoutUppercase_WhenRequireUppercaseIsFalse() {

        var annotation = mock(ValidPassword.class);
        when(annotation.minLength()).thenReturn(8);
        when(annotation.requireUppercase()).thenReturn(false);
        when(annotation.requireLowercase()).thenReturn(true);
        when(annotation.requireDigit()).thenReturn(true);
        when(annotation.requireSpecialChar()).thenReturn(true);
        validator.initialize(annotation);

        var result = validator.isValid("mystr0ng123!", context);

        assertThat(result).isTrue();
        verify(context, never()).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("Should accept password without lowercase when requireLowercase is false")
    void shouldAcceptPasswordWithoutLowercase_WhenRequireLowercaseIsFalse() {

        var annotation = mock(ValidPassword.class);
        when(annotation.minLength()).thenReturn(8);
        when(annotation.requireUppercase()).thenReturn(true);
        when(annotation.requireLowercase()).thenReturn(false);
        when(annotation.requireDigit()).thenReturn(true);
        when(annotation.requireSpecialChar()).thenReturn(true);
        validator.initialize(annotation);

        var result = validator.isValid("MYSTR0NG123!", context);

        assertThat(result).isTrue();
        verify(context, never()).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("Should accept password without digit when requireDigit is false")
    void shouldAcceptPasswordWithoutDigit_WhenRequireDigitIsFalse() {

        var annotation = mock(ValidPassword.class);
        when(annotation.minLength()).thenReturn(8);
        when(annotation.requireUppercase()).thenReturn(true);
        when(annotation.requireLowercase()).thenReturn(true);
        when(annotation.requireDigit()).thenReturn(false);
        when(annotation.requireSpecialChar()).thenReturn(true);
        validator.initialize(annotation);

        var result = validator.isValid("MyStrong!", context);

        assertThat(result).isTrue();
        verify(context, never()).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("Should accept password without special char when requireSpecialChar is false")
    void shouldAcceptPasswordWithoutSpecialChar_WhenRequireSpecialCharIsFalse() {

        var annotation = mock(ValidPassword.class);
        when(annotation.minLength()).thenReturn(8);
        when(annotation.requireUppercase()).thenReturn(true);
        when(annotation.requireLowercase()).thenReturn(true);
        when(annotation.requireDigit()).thenReturn(true);
        when(annotation.requireSpecialChar()).thenReturn(false);
        validator.initialize(annotation);

        var result = validator.isValid("MyStr0ng123", context);

        assertThat(result).isTrue();
        verify(context, never()).disableDefaultConstraintViolation();
    }
}
