package dev.jlynx.magicaldrones.security;

import dev.jlynx.magicaldrones.auth.Account;
import dev.jlynx.magicaldrones.auth.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A custom {@link UserDetailsService} implementation that retrieves user details from the database.
 * This service is activated only when the "prod" profile is active, as specified by the {@code @Profile} annotation.
 *
 * <p>It uses {@link AccountRepository} to fetch user accounts by username. If the username is not found,
 * a {@link UsernameNotFoundException} is thrown, and an appropriate trace log is recorded.</p>
 *
 * <p>This class is used by Spring Security to authenticate and authorize users based on the details
 * retrieved from the database.</p>
 *
 * <strong>Note:</strong> Ensure the "prod" profile is active for this bean to be initialized.
 *
 * @see UserDetailsService
 * @see AccountRepository
 * @see UserDetails
 * @author Jacek Nowak <jaceknowakdev@gmail.com>
 */
@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseUserDetailsService.class);


    private final AccountRepository accountRepository;

    @Autowired
    public DatabaseUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Loads the user details by the given username.
     *
     * <p>This method fetches the user account from the database using the {@link AccountRepository}.
     * If the account does not exist, a {@link UsernameNotFoundException} is thrown.</p>
     *
     * @param username the username of the user to be loaded
     * @return the {@link UserDetails} of the user, which includes account information
     * @throws UsernameNotFoundException if the user with the given username is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = accountRepository.findByUsername(username);
        if (account.isEmpty()) {
            log.trace("Account with username '{}' doesn't exist.", username);
            throw new UsernameNotFoundException("Account with username '%s' doesn't exist.".formatted(username));
        }
        log.trace("Loading account with username '{}'", username);
        return account.get();
    }
}
