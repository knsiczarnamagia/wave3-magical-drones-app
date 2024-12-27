package dev.jlynx.magicaldrones.dto;

import dev.jlynx.magicaldrones.auth.Account;

/**
 * A DTO for {@link Account}'s data presentation.
 *
 * @param id
 * @param enabled
 * @param accountNonLocked
 * @param accountNonExpired
 * @param credentialsNonExpired
 * @param authorities a list of authorities separated by spaces
 * @param profilePictureId account's profile picture UUID
 */
public record AccountView(
        long id,
        String username,
        boolean enabled,
        boolean accountNonLocked,
        boolean accountNonExpired,
        boolean credentialsNonExpired,
        String authorities,
        String profilePictureId
) {}
