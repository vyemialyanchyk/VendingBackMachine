package com.vending.back.machine.model;

import com.vending.back.machine.domain.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * vyemialyanchyk on 2/15/2017.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"sessionUid", "recaptcha"})
public class CreateUserRequest extends User {
    private String sessionUid;
}
