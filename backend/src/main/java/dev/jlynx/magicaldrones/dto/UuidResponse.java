package dev.jlynx.magicaldrones.dto;

/**
 * Response body object typically used when saving files/images to return their UUID identifiers.
 * @param uuid the UUID of the file/image
 */
public record UuidResponse(String uuid) {
}
