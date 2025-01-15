package dev.jlynx.magicaldrones.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class AuthorityInitializer {

    @Order(1)
    @Bean
    CommandLineRunner initializeAuthorities(AuthorityRepository repository) {
        return args -> {
            for (Role role : Role.values()) {
                if (!repository.existsByName(role.getValue())) {
                    repository.save(new Authority(role));
                }
            }
        };
    }
}
