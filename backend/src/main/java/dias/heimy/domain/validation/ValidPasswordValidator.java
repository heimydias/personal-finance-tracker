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
            addCustomMessage(context, "Senha não pode ser nula ou vazia");
            return false;
        }

        List<String> violations = new ArrayList<>();

        if (password.length() < minLength) {
            violations.add(String.format("deve ter pelo menos %d caracteres", minLength));
        }

        if (requireUppercase && !password.matches(".*[A-Z].*")) {
            violations.add("deve conter pelo menos uma letra maiúscula");
        }

        if (requireLowercase && !password.matches(".*[a-z].*")) {
            violations.add("deve conter pelo menos uma letra minúscula");
        }

        if (requireDigit && !password.matches(".*\\d.*")) {
            violations.add("deve conter pelo menos um dígito");
        }

        if (requireSpecialChar && !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            violations.add("deve conter pelo menos um caractere especial");
        }

        if (isCommonPassword(password)) {
            violations.add("não pode ser uma senha comum");
        }

        if (!violations.isEmpty()) {
            String message = "Senha " + String.join(", ", violations);
            addCustomMessage(context, message);
            return false;
        }

        return true;
    }

    private void addCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    private boolean isCommonPassword(String password) {
        String lowerPassword = password.toLowerCase();
        return lowerPassword.contains("password")
                || lowerPassword.equals("123456")
                || lowerPassword.equals("admin")
                || lowerPassword.contains("qwerty");
    }
}
