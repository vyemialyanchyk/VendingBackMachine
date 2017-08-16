package com.vending.back.machine.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * vyemialyanchyk on 4/10/2017.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEmail {
    @JsonProperty
    Long userId;
    @JsonProperty
    String email;
}
