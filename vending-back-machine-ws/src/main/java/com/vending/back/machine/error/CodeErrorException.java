package com.vending.back.machine.error;

import lombok.Getter;

/**
 * vyemialyanchyk on 2/8/2017.
 */
@Getter
public class CodeErrorException extends RuntimeException {
    public enum ErrorCode {
        SYSTEM_ERROR, NOT_FOUND, DUPLICATE_EMAIL,
        APPLICATION_STARTED, WRONG_PASSWORD,
        CAN_NOT_START_JOB, ACCOUNT_FAILD_VERIFICATION
    }

    private String code;

    public CodeErrorException(ErrorCode code, String message) {
        this(code, message, null);
    }

    public CodeErrorException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code.name();
    }
}
