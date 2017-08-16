package com.vending.back.machine.app.security;

import com.vending.back.machine.domain.User;
import com.vending.back.machine.domain.oauth.VbmUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * vyemialyanchyk on 5/31/2017.
 */
public class UserDetailsUtil {

    public static UserDetails createUserByUsername(final User user) {
        boolean enabled = user.isActive();
        List<GrantedAuthority> authorities = buildUserAuthority(user, enabled);
        return buildUserForAuthentication(user, authorities, enabled);
    }

    private static UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities, boolean enabled) {
        return new VbmUser(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getVerifyEmail(), user.getPasswordHash(), enabled, true, true, true, authorities);
    }

    private static List<GrantedAuthority> buildUserAuthority(User user, boolean enabled) {
        List<GrantedAuthority> auths = Collections.emptyList();
        // Build user's authorities
        if (enabled && !user.getRoles().isEmpty()) {
            auths = user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }
        return auths;
    }
}
