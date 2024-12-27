package dev.jlynx.magicaldrones.auth;

import dev.jlynx.magicaldrones.dto.*;
import dev.jlynx.magicaldrones.exception.AccessForbiddenException;
import dev.jlynx.magicaldrones.exception.ResourceNotFoundException;
import dev.jlynx.magicaldrones.exception.UsernameExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * An account management service.
 */
@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final PasswordEncoder encoder;
    private final AuthorityRepository authorityRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, PasswordEncoder encoder,
                          AuthorityRepository authorityRepository) {
        this.accountRepository = accountRepository;
        this.encoder = encoder;
        this.authorityRepository = authorityRepository;
    }

    /**
     * Registers a new user account if the username does not already exist.
     *
     * @param reg a DTO object containing the data for the new account
     * @return the persisted {@code Account} entity
     * @throws UsernameExistsException if an account with the given username already exists
     */
    public AccountView registerAccount(AccountRegistration reg) {
        if (accountRepository.existsByUsername(reg.username())) {
            log.debug("Failed to register account with existing username '{}'", reg.username());
            throw new UsernameExistsException(String.format("Username '%s' already exists.", reg.username()));
        }
        Account newAccount = new Account(
                reg.username(),
                encoder.encode(reg.password())
        );
        Authority userAuthority = authorityRepository.findByName("ROLE_USER");
        newAccount.setAuthorities(List.of(userAuthority));
        Account saved = accountRepository.save(newAccount);
        log.debug("Registered new account with username='{}' and id={}", saved.getUsername(), saved.getId());
        return saved.toDto();
    }

    @PreAuthorize("hasRole('USER') && #id == principal.claims['id']")
    @Transactional
    public AccountView updatePassword(@P("id") long accountId, UpdateMyPasswordRequest request) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> {
            log.debug("Failed to update password of a non-existing account with id={}", accountId);
            return new ResourceNotFoundException("Account with id=%d doesn't exist.".formatted(accountId));
        });
        if (!encoder.matches(request.currentPassword(), account.getPassword())) {
            log.debug("Value for currentPassword is invalid. Password was not updated.");
            throw new AccessForbiddenException("Value for current password is invalid.");
        }
        account.setPassword(encoder.encode(request.newPassword()));
        updateAuthenticationForPwd(request.newPassword());
        log.trace("Password for account id={} updated successfully.", accountId);
        return account.toDto();
    }

    private void updateAuthenticationForPwd(String newPassword) {
        Authentication curAuth = SecurityContextHolder.getContext().getAuthentication();
        var newAuth = new UsernamePasswordAuthenticationToken(
                curAuth.getName(),
                newPassword,
                curAuth.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        log.trace("Authentication context updated with new credentials.");
        // todo: return a new JWT token ? ANSWER: Yes, but in general use short-lived tokens with refresh tokens
    }

    @PreAuthorize("hasRole('USER') && #id == principal.claims['id']")
    @Transactional
    public AccountView updateUsername(@P("id") long accountId, UpdateMyUsernameRequest request) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> {
            log.debug("Tried to update username of a non-existing account with id={}", accountId);
            return new ResourceNotFoundException("Non-existing account with id=%d".formatted(accountId));
        });
        if (accountRepository.existsByUsername(request.username())) {
            log.debug("Failed to set new username as an already existing username: {}", request.username());
            throw new UsernameExistsException(String.format("Username '%s' already exists.",
                    request.username()));
        }
        account.setUsername(request.username());
        updateAuthenticationForUsername(request.username());
        log.trace("Username for account id={} updated to {}.", accountId, request.username());
        return account.toDto();
    }

    private void updateAuthenticationForUsername(String newUsername) {
        Authentication curAuth = SecurityContextHolder.getContext().getAuthentication();
        var newAuth = new UsernamePasswordAuthenticationToken(
                newUsername,
                curAuth.getCredentials(),
                curAuth.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        log.trace("Authentication context updated with new username.");
        // todo: return a new JWT token ? ANSWER: Yes, but in general use short-lived tokens with refresh tokens
    }

    /**
     * Deletes current user's account by its ID if it exists.
     *
     * @param id the ID of the account to delete
     * @return {@code true} if the account was deleted, {@code false} if no account with the given ID exists
     */
    @PreAuthorize("hasRole('USER') && #id == principal.claims['id']")
    public void deleteAccount(long id, DeleteMyAccountRequest request) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            log.debug("Failed to delete an account with non-existing id={}", id);
            throw new ResourceNotFoundException("Account with id=%d doesn't exist".formatted(id));
        }
        Account account = accountOptional.get();
        if (!encoder.matches(request.currentPassword(), account.getPassword())) {
            log.debug("Value for currentPassword is invalid. Account was not deleted.");
            throw new AccessForbiddenException("Value for current password is invalid.");
        }
        accountRepository.deleteById(id);
        SecurityContextHolder.getContext().setAuthentication(null);
        log.debug("Deleted account with id={}", id);
    }
}
