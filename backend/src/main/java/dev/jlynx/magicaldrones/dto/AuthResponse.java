package dev.jlynx.magicaldrones.dto;

import dev.jlynx.magicaldrones.security.AuthController;

/**
 * A response body object for the {@link AuthController#authenticateForToken(AuthRequest)} request.
 *
 * @param token a JWT token generated for the current user authentication request.
 */
public record AuthResponse(String token) {
}
