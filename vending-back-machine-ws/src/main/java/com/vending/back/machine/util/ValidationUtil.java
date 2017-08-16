package com.vending.back.machine.util;

import com.vending.back.machine.error.InputValidationException;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * vyemialyanchyk on 2/8/2017.
 */
public class ValidationUtil {

    public enum Field {
        EMAIL, CREATE_USER_INFO, PASSWORD, FIRST_NAME, LAST_NAME, USER_INFO, TOKEN;
    }

    public static <T> void checkRequired(Field field, T value) {
        if (value == null) {
            throw new InputValidationException(field.name(), field.name() + " is required");
        }
    }

    public static <T> void checkFieldRequired(Field field, T value) {
        checkRequired(field, value);
    }

    public static void checkEmail(String name, String value) {
        checkRegExp(name, value,
                "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");
    }

    public static void checkRegExp(String name, String value, String regExp) {
        if (!value.matches(regExp)) {
            throw new InputValidationException(name, String.format("%s value %s is not valid", name, value));
        }
    }

    public static void checkMinLength(String name, String value, int minLength) {
        if (StringUtils.isBlank(value) || value.length() < minLength) {
            throw new InputValidationException(name, name + " must be " + minLength + " characters long");
        }
    }

    public static void compareMinAndMax(Integer minValue, Integer maxValue, Field minField, Field maxField) {
        if (maxValue != null && minValue > maxValue) {
            throw new InputValidationException("Wrong min and max values", minField.name() + " must be less than" + maxField.name());
        }
    }

    public static void checkPositiveNumber(Integer value, Field field) {
        if (value <= 0) {
            throw new InputValidationException(field.name(), field.name() + " must be positive");
        }
    }

    public static void checkNonNegativeNumber(Integer value, Field field) {
        if (value < 0) {
            throw new InputValidationException(field.name(), field.name() + " must be more than 0");
        }
    }

    public static void checkNotEmptyCollection(Collection value, Field field) {
        if (value.isEmpty()) {
            throw new InputValidationException(field.name(), field.name() + " list must be not empty");
        }
    }

    public static void compareGlobalAndStateConfig(Integer minGlobalValue, Integer maxGlobalValue, Integer stateValue, Field field) {
        if (stateValue < minGlobalValue || (maxGlobalValue != null && stateValue > maxGlobalValue)) {
            throw new InputValidationException(field.name(), field.name() + " is out of bounds of the min and max set in the Global Config");
        }
    }

    public static void checkPositiveNumber(Double value, Field field) {
        if (value <= 0d) {
            throw new InputValidationException(field.name(), field.name() + " must be positive");
        }
    }

    public static void checkCollectionLength(Collection value, int maxLength, Field field) {
        if (value.size() > maxLength) {
            throw new InputValidationException(field.name(), field.name() + " list size must be less than " + maxLength);
        }
    }
}
