package com.vending.back.machine.model;

import lombok.Getter;
import lombok.Setter;

/**
 * vyemialyanchyk on 5/28/2017.
 */
@Getter
@Setter
public class ResetPasswordRequest {
    private String email;
    private String password;
    private String passwordResetToken;
}
