package dev.jlynx.magicaldrones.security;

import dev.jlynx.magicaldrones.auth.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

/**
 * A service class for handling JWT tokens.
 */
@Service
public class TokenService {

    private final JwtEncoder encoder;

    @Autowired
    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    /**
     * Generates a new JWT token for this {@code Authentication} object.
     *
     * <p>It fills the token's payload with this {@code Authentication}'s name and authorities.
     * Sets the expiration time to 1 hour.
     *
     * @param authentication the authentication object whose data is used to generate a token. Generally,
     *                       it's a currently authenticated user's {@link Authentication} object.
     * @return a new JWT token
     */
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        Long accountId = ((Account) authentication.getPrincipal()).getId();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .claim("id", accountId)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
