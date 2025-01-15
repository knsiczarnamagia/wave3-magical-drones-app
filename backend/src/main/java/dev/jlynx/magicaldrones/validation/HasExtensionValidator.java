package dev.jlynx.magicaldrones.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

public class HasExtensionValidator implements ConstraintValidator<HasExtension, MultipartFile> {

    private String[] allowedExtensions;

    @Override
    public void initialize(HasExtension constraintAnnotation) {
        this.allowedExtensions = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            return false;
        }
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        return Arrays.asList(allowedExtensions).contains(extension);
    }
}
