package com.vending.back.machine.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

/**
 * vyemialyanchyk on 7/1/2017.
 */
@Getter
@Setter
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMember {
    @JsonProperty
    private Long id;
    @JsonProperty
    private Long groupId;
    @JsonProperty
    private Long userId;
    @JsonProperty
    private Integer memberType;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date created;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date updated;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long updater;
}
