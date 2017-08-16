package com.vending.back.machine.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * vyemialyanchyk on 6/20/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGroup {
    @JsonProperty
    private Long groupId;
    @JsonProperty
    private Integer userGroupType;
    @JsonProperty
    private Integer userGroupMemberType;
    @JsonProperty
    private String title;
    @JsonProperty
    private String logo;
}
