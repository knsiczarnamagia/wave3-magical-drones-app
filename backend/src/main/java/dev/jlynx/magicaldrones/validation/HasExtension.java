package dev.jlynx.magicaldrones.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Checks if the {@code MultipartFile} has one of the given file extensions.
 */
@Constraint(validatedBy = HasExtensionValidator.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HasExtension {

    String[] value();

    String message() default "The given file extension is not allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
