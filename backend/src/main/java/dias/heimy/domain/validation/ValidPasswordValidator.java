package dias.heimy.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private int minLength;
    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireDigit;
    private boolean requireSpecialChar;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.requireUppercase = constraintAnnotation.requireUppercase();
        this.requireLowercase = constraintAnnotation.requireLowercase();
        this.requireDigit = constraintAnnotation.requireDigit();
        this.requireSpecialChar = constraintAnnotation.requireSpecialChar();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.trim().isEmpty()) {
            return true;
        }

        List<String> violations = new ArrayList<>();

        if (password.length() < minLength) {
            violations.add(String.format("deve ter pelo menos %d caracteres", minLength));
        }

        if (requireUppercase && !containsUppercase(password)) {
            violations.add("deve conter pelo menos uma letra maiúscula");
        }

        if (requireLowercase && !containsLowercase(password)) {
            violations.add("deve conter pelo menos uma letra minúscula");
        }

        if (requireDigit && !containsDigit(password)) {
            violations.add("deve conter pelo menos um dígito");
        }

        if (requireSpecialChar && !containsSpecialChar(password)) {
            violations.add("deve conter pelo menos um caractere especial");
        }

        if (!violations.isEmpty()) {
            String message = "Senha " + String.join(", ", violations);
            addCustomMessage(context, message);
            return false;
        }

        return true;
    }

    private boolean containsUppercase(String password) {
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsLowercase(String password) {
        for (int i = 0; i < password.length(); i++) {
            if (Character.isLowerCase(password.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsDigit(String password) {
        for (int i = 0; i < password.length(); i++) {
            if (Character.isDigit(password.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSpecialChar(String password) {
        String specialChars = "!@#$%^&*()_+-=[]{};\':\"\\|,.<>/?";
        for (int i = 0; i < password.length(); i++) {
            if (specialChars.indexOf(password.charAt(i)) >= 0) {
                return true;
            }
        }
        return false;
    }

    private void addCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
