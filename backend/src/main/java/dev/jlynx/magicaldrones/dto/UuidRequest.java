package dev.jlynx.magicaldrones.dto;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UUID;

/**
 * A request body object typically used when deleting images/files from a file storage service.
 * @param uuid the UUID of the file/image
 */

public record UuidRequest(@UUID @NotNull String uuid) {
}
