package com.vending.back.machine.helper.translate;

import java.util.ResourceBundle;

/**
 * vyemialyanchyk on 3/7/2017.
 */
public enum ErrorMsg {

    INCORRECT_USER_TRY_TO_UPDATE_PROFILE,
    NO_HASH_CORRESPONDING_FILE,
    EXPIRED_TOKEN,
    INVALID_USER_DETAILS,
    INVALID_TOKEN,
    YOUR_EMAIL_ADDRESS_IS_NOT_RECOGNIZED,
    YOUR_PASSWORD_WAS_UPDATED_ONE_HOUR_AGO,
    USER_WITH_EMAIL_ALREADY_EXISTS,
    USER_WAS_BLOCKED_BY_OPPONENT;

    private static final String ERRORS_BUNDLE_PATH = "translate.errors_";

    private ResourceBundle errorsResourceBundle;

    public String get(Object... args) {
        String res = getStringValue(name());
        if (res == null) {
            res = name();
        }
        return String.format(res, args);
    }

    public String getStringValue(String key) {
        String res = errorsResourceBundle.getString(key);
        return res;
    }
}
