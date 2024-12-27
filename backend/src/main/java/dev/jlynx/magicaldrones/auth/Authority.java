package dev.jlynx.magicaldrones.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

/**
 * A custom implementation of the {@link GrantedAuthority} interface.
 *
 * @see Role
 * @see GrantedAuthority
 */
@Getter @Setter
@NoArgsConstructor
@Entity
@Table(
        name = "authority",
        uniqueConstraints = { @UniqueConstraint(columnNames = "name", name = "name_unique") }
)
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authorityId")
    @SequenceGenerator(name = "authorityId", sequenceName = "authority_id_seq", allocationSize = 1)
    private Long id;

    /**
     * A string representation of this authority.
     */
    @Column(name = "name", nullable = false)
    private String name;

    public Authority(Role role) {
        this.name = role.getValue();
    }

    /**
     * Uses {@code stringValue} internally. Equivalent to {@code getName()}.
     *
     * @return a string representation of this authority
     */
    @Override
    public String getAuthority() {
        return name;
    }
}
