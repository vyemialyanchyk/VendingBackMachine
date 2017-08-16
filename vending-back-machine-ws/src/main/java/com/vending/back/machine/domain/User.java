package com.vending.back.machine.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * vyemialyanchyk on 1/30/2017.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends UserInfoWithRoles {
    public User(Long id) {
        super(id);
    }
    // plain password, not persisted
    private String password;
    // BCrypt hashed password
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordHash;
    private String passwordResetToken;
    private int status;
    private boolean active;
}
