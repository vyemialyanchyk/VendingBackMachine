package com.vending.back.machine.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * vyemialyanchyk on 1/30/2017.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "roles")
public class UserInfoWithRoles extends UserInfo {
    public UserInfoWithRoles(Long id) {
        super(id);
    }
    private Set<String> roles;
}
