package dev.jlynx.magicaldrones.dto;

import jakarta.validation.constraints.NotEmpty;

/**
 * A DTO object which holds data from the user login (authentication) request body.
 *
 * @param username a username submitted by the user
 * @param password a password submitted by the user before hashing
 */
public record AuthRequest(@NotEmpty String username, @NotEmpty String password) {
}
