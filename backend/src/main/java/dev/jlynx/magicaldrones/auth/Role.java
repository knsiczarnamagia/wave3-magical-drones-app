package dev.jlynx.magicaldrones.auth;

/**
 * Defines valid user roles and their string values.
 * <p>
 * The string value of this enum should be used to define {@code UserAuthority} values.
 * <p>
 * Example:
 * <pre>{@code
 * UserAuthority authority = new UserAuthority(Role.USER);
 * }</pre>
 *
 * @see Authority
 * @see org.springframework.security.core.GrantedAuthority
 */
public enum Role {

    /**
     * Represents the most basic role of a user/consumer who utilises the application.
     * All accounts are granted this role by default.
     */
    USER("ROLE_USER"),

    // todo: remove the SUPPORT role and leave only ADMIN?
    /**
     * Grants access to:
     * <ul>
     *     <li>Account locking</li>
     *     <li>Enforce password reset</li>
     *     <li>Account asset and data lookup</li>
     *     <li>User data modification</li>
     * </ul>
     * <p>Doesn't allow to grant other accounts the {@code SUPPORT} role.
     * <p>Doesn't include privileges of other roles.
     */
    SUPPORT("ROLE_SUPPORT"),

    /**
     * Grants access to:
     * <ul>
     *     <li>Account removal</li>
     *     <li>Asset removal</li>
     *     <li>Granting and revoking the {@code SUPPORT} role to other accounts</li>
     * </ul>
     * <p>
     */
    ADMIN("ROLE_ADMIN"),

    /**
     * Represents the "super admin" role.
     * <p>Only a single account may have this role.
     * <p>Allows to grant and revoke the {@code ADMIN} role to other accounts.
     */
    ROOT("ROLE_ROOT");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    /**
     * Returns a string value representing this role.
     *
     * @return a string value used for defining authorities
     */
    public String getValue() {
        return value;
    }
}
