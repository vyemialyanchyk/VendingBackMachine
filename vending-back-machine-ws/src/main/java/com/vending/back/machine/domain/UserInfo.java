package com.vending.back.machine.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * vyemialyanchyk on 1/30/2017.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    public UserInfo(Long id) {
        this.id = id;
    }
    @JsonProperty
    private Long id;
    @JsonProperty
    private String email;
    @JsonProperty
    private String firstName;
    @JsonProperty
    private String lastName;
    @JsonProperty
    private Boolean verifyEmail;

    public void namesNullToEmpty() {
        if (getFirstName() == null) {
            setFirstName("");
        }
        if (getLastName() == null) {
            setLastName("");
        }
    }
}
