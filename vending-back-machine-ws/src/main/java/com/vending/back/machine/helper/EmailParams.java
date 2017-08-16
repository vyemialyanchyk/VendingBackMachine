package com.vending.back.machine.helper;

import com.vending.back.machine.domain.UserInfo;
import com.vending.back.machine.domain.UserProfile;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

/**
 * vyemialyanchyk on 2/8/2017.
 */
public class EmailParams {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DateFormats.DATE_TIME_PATTERN_NOTIFICATION);
    private static final DateTimeFormatter US_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DateFormats.US_DATE_PATTERN);

    public static Map<String, Object> getCommonParameters(UserInfo userInfo) {
        return getCommonParameters(userInfo, null);
    }

    public static Map<String, Object> getCommonParameters(UserInfo userInfo, UserProfile userProfile) {
        final Map<String, Object> emailParams = new TreeMap<>();
        emailParams.put("time", DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        emailParams.put("user", userInfo.getFirstName() + " " + userInfo.getLastName());
        emailParams.put("user_first_name", userInfo.getFirstName());
        emailParams.put("user_last_name", userInfo.getLastName());
        emailParams.put("date", US_DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        String address = "";
        if (userProfile != null) {
            address = appendToAddress(address, userProfile.getCity());
            address = appendToAddress(address, userProfile.getCountry());
        }
        emailParams.put("address", address);
        return emailParams;
    }

    public static String appendToAddress(String address, String str) {
        return (address + (StringUtils.isBlank(str) ? "" : (StringUtils.isBlank(address) ? str.trim() : ", " + str.trim())));
    }
}
