package dias.heimy.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPasswordValidator.class)
@Documented
public @interface ValidPassword {

    String message() default "Senha não atende aos critérios de segurança";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int minLength() default 8;

    boolean requireUppercase() default true;

    boolean requireLowercase() default true;

    boolean requireDigit() default true;

    boolean requireSpecialChar() default true;
}
