package dev.jlynx.magicaldrones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * A DTO object which maps to the body of the "change my username" request.
 * @param username
 */
public record UpdateMyUsernameRequest(

        @NotBlank
        @Size(min = 2, max = 30)
        @Pattern(regexp = "^\\w+$")
        String username
) {}
