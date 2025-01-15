package dev.jlynx.magicaldrones.auth;

import org.springframework.data.repository.ListCrudRepository;

public interface AuthorityRepository extends ListCrudRepository<Authority, Long> {

    boolean existsByName(String name);

    Authority findByName(String name);
}