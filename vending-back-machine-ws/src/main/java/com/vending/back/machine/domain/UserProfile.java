package com.vending.back.machine.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vending.back.machine.helper.DateFormats;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

/**
 * vyemialyanchyk on 1/30/2017.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfile {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    @JsonProperty
    private Long userId;
    @JsonProperty
    private String profileAlias;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormats.DATE_PATTERN)
    private LocalDate birthDate;
    @JsonProperty
    private String city;
    @JsonProperty
    private String country;
}
