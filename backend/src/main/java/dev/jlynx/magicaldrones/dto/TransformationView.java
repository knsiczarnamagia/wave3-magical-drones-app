package dev.jlynx.magicaldrones.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * A DTO object which holds transformation's data for GET requests.
 *
 * @param id id of the {@code Transformation} object
 * @param startedAt
 * @param completedAt
 * @param sourceImageUuid a UUID of source image
 * @param transformedImageUuid a UUID of transformed image
 * @param title
 * @param description
 */
public record TransformationView(
        long id,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "ddMMyyyy_hhmmss")
        LocalDateTime startedAt,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "ddMMyyyy_hhmmss")
        LocalDateTime completedAt,
        @JsonProperty("sourceImageUuid")
        String sourceImageUuid,
        @JsonProperty("transformedImageUuid")
        String transformedImageUuid,
        String title,
        String description
) {}
