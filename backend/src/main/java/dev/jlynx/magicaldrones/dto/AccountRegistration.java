package dev.jlynx.magicaldrones.dto;

import dev.jlynx.magicaldrones.utils.Constraints;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * A DTO object which holds data from a new account registration form submission.
 *
 * @param username a unique username for the registered account
 * @param password a password for the registered account
 */
public record AccountRegistration(

        @NotBlank
        @Size(min = 2, max = 30)
        @Pattern(regexp = "^\\w+$")
        String username,

        @NotBlank
        @Size(min = 8, max = 255)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,}$", message = Constraints.INVALID_PWD_MSG)
        String password
) {}
