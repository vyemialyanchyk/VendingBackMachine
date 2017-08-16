package com.vending.back.machine.app.filter;

/**
 * vyemialyanchyk on 3/7/2017.
 */
public enum AllowHeaders {
    ORIGIN("Origin"),
    X_REQUESTED_WITH("X-Requested-With"),
    CONTENT_TYPE("Content-Type"),
    ACCEPT("Accept"),
    AUTHORIZATION("Authorization"),
    ACCESS_CONTROL_ALLOW_CREDENTIALS("Access-Control-Allow-Credentials"),
    CACHE_CONTROL("Cache-Control");

    private String realName;

    AllowHeaders(String realName) {
        this.realName = realName;
    }

    public String getRealName() {
        return realName;
    }

    public static String[] getRealNames() {
        String[] arr = new String[AllowHeaders.values().length];
        int i = 0;
        for (AllowHeaders val : AllowHeaders.values()) {
            arr[i++] = val.realName;
        }
        return arr;
    }
}
