package com.vending.back.machine.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * vyemialyanchyk on 5/28/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserAuthForPassword extends User {
    public UserAuthForPassword(Long id) {
        super(id);
    }
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalDateTime updated;
}
