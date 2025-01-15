package dev.jlynx.magicaldrones.dto;

import dev.jlynx.magicaldrones.utils.Constraints;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * A DTO object which holds body of the "change current account's password" request.
 *
 * @param currentPassword current password of the user
 * @param newPassword new password to set
 */
public record UpdateMyPasswordRequest(

        @NotEmpty
        String currentPassword,

        @NotBlank
        @Size(min = 8, max = 255)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,}$", message = Constraints.INVALID_PWD_MSG)
        String newPassword
) {}
