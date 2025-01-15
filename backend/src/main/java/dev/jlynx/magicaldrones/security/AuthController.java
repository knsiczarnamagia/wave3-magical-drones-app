package dev.jlynx.magicaldrones.security;

import dev.jlynx.magicaldrones.dto.AuthRequest;
import dev.jlynx.magicaldrones.dto.AuthResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequestMapping("/auth")
@RestController
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final TokenService tokenService;
    private final AuthenticationManager authManager;

    @Autowired
    public AuthController(TokenService tokenService, AuthenticationManager authManager) {
        this.tokenService = tokenService;
        this.authManager = authManager;
    }

    @PostMapping("/token")
    public ResponseEntity<AuthResponse> authenticateForToken(@RequestBody @Valid AuthRequest request) {
        log.info("Authenticating...");
        // todo: is the SecurityContextHolder.getContext().setAuthentication() invoked automatically here? answer: APPARENTLY YES
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        log.info("Authentication successful!");
        String jwtToken = tokenService.generateToken(authentication);
        log.info("Token generated!");
        AuthResponse body = new AuthResponse(jwtToken);
        return ResponseEntity.ok(body);
    }
}
