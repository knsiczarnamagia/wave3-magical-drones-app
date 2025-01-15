package dev.jlynx.magicaldrones.auth;

import dev.jlynx.magicaldrones.dto.AccountView;
import dev.jlynx.magicaldrones.transformation.Transformation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "account",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username", name = "username_unique "),
                @UniqueConstraint(columnNames = "profile_picture_id", name = "profile_picture_id_unique "),
        }
)
public class Account implements UserDetails {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountId")
    @SequenceGenerator(name = "accountId", sequenceName = "account_id_seq", allocationSize = 10)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private Boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_authority",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id"),
            foreignKey = @ForeignKey(name = "fk_account_authority"),
            inverseForeignKey = @ForeignKey(name = "fk_authority_account")
    )
    private List<Authority> authorities;

    /**
     * An id of the file containing this account's profile picture.
     */
    @Getter
    @Column(name = "profile_picture_id")
    private String profilePicture;

    @OneToMany(mappedBy = "account", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<Transformation> transformations;


    public Account(String username, String password) {
        this.username = username;
        this.password = password;
        profilePicture = null;
        accountNonExpired = true;
        accountNonLocked = true;
        credentialsNonExpired = true;
        enabled = true;
        authorities = new ArrayList<>();
        transformations = new ArrayList<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void addTransformation(Transformation transformation) {
        transformations.add(transformation);
        transformation.setAccount(this);
    }

    public boolean removeTransformation(Transformation transformation) {
        if (transformations.contains(transformation)) {
            transformations.remove(transformation);
            transformation.setAccount(null);
            return true;
        }
        return false;
    }

    public AccountView toDto() {
        String authorities = this.authorities.stream()
                .map(Authority::getName)
                .collect(Collectors.joining(" "));
        return new AccountView(
                this.getId(),
                this.getUsername(),
                this.isEnabled(),
                this.isAccountNonLocked(),
                this.isAccountNonExpired(),
                this.isCredentialsNonExpired(),
                authorities,
                this.getProfilePicture()
        );
    }
}
