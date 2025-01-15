package dev.jlynx.magicaldrones.dto;

import jakarta.validation.constraints.NotEmpty;

/**
 * A DTO object which maps to the "delete my account" request body.
 *
 * @param currentPassword the current password of the current account
 */
public record DeleteMyAccountRequest(
        @NotEmpty
        String currentPassword
) {}
