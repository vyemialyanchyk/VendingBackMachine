package com.vending.back.machine.error;

import lombok.Getter;

/**
 * vyemialyanchyk on 2/8/2017.
 */
@Getter
public class InputValidationException extends RuntimeException {

    private String field;

    public InputValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

}
