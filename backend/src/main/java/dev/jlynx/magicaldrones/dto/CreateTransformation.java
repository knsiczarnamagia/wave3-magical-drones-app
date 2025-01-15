package dev.jlynx.magicaldrones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UUID;

/**
 * A DTO object holding data from a <em>create {@code Transformation}</em> request.
 *
 * @param sourceImage a valid UUID of the source image; must not be null
 * @param title the title of the transformation; must not be blank or null; max. 50 characters
 * @param description the description text of the transformation; max. 3000 characters; may be null
 */
public record CreateTransformation(
        @NotNull @UUID String  sourceImage,
        @NotBlank @Size(max = 50) String title,
        @Size(max = 3000) String description
) {}
