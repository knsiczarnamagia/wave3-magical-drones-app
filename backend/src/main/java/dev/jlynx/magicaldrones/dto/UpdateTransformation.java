package dev.jlynx.magicaldrones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * A DTO object holding data from the <em>Update {@code Transformation}</em> request.
 *
 * @param title the title of the transformation; must not be blank or null; max. 50 characters
 * @param description the description text of the transformation; max. 3000 characters; may be null
 */

public record UpdateTransformation(
        @NotBlank
        @Size(max = 50)
        String title,
        @Size(max = 3000)
        String description
        // todo: complete this dto
) {}
