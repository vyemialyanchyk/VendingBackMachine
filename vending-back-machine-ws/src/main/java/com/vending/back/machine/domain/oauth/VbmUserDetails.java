package com.vending.back.machine.domain.oauth;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * vyemialyanchyk on 1/30/2017.
 */
public interface VbmUserDetails extends UserDetails {

    Long getUserId();

    String getFirstName();

    String getLastName();

    Boolean getVerifyEmail();

    String[] getRoles();

    Set<String> getRolesSet();

}
