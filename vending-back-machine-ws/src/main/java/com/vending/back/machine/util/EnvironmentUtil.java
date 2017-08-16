package com.vending.back.machine.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

/**
 * vyemialyanchyk on 2/8/2017.
 */
public class EnvironmentUtil {

    public static final String PROD = "prod";
    public static final String STAGE = "stage";
    public static final String TEST = "test";
    public static final String DEV = "dev";

    public static boolean isDev(Environment environment) {
        return correspondToProfile(environment, DEV);
    }

    public static boolean isTest(Environment environment) {
        return correspondToProfile(environment, TEST);
    }

    public static boolean isStage(Environment environment) {
        return correspondToProfile(environment, STAGE);
    }

    public static boolean isProd(Environment environment) {
        return correspondToProfile(environment, PROD);
    }

    public static boolean correspondToProfile(Environment environment, String profile) {
        boolean res = false;
        for (String str : environment.getActiveProfiles()) {
            if (StringUtils.startsWithIgnoreCase(str, profile)) {
                res = true;
                break;
            }
        }
        return res;
    }
}
