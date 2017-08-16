package com.vending.back.machine.app.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * vyemialyanchyk on 2/8/2017.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private String exceptionStackTrace;
    private String exceptionMessage;

    public ErrorResponse(String code, String message) {
        this(code, message, null, null);
    }
}
