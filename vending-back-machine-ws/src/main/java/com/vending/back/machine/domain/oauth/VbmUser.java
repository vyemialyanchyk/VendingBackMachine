package com.vending.back.machine.domain.oauth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;

/**
 * vyemialyanchyk on 1/30/2017.
 */
@Getter
public class VbmUser extends User implements VbmUserDetails {
    private final Long userId;
    private final String firstName;
    private final String lastName;
    private final Boolean verifyEmail;

    public VbmUser(Long userId, String username, String firstName, String lastName, Boolean verifyEmail,
                   String password, boolean enabled, boolean accountNonExpired,
                   boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends
            GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.verifyEmail = verifyEmail;
    }

    public String[] getRoles() {
        return getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }

    public Set<String> getRolesSet() {
        return Collections.unmodifiableSet(new TreeSet<String>(Arrays.asList(getRoles())));
    }
}
