package com.vending.back.machine.domain.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

/**
 * vyemialyanchyk on 30.11.16.
 */
@Slf4j
public class ObjectUtil {

	public static interface Validator {
		boolean isValidateStr(String str);
	}

	public static <T> boolean isValid(T obj, Enum[] vals) {
		boolean res = true;
		for (Enum valEnum : vals) {
			Field field = null;
			try {
				field = obj.getClass().getDeclaredField(valEnum.name());
			} catch (NoSuchFieldException ex) {
				// ignore
			}
			if (field == null) {
				log.error(obj.getClass().toString() + " no field: " + valEnum.name());
			}
			field.setAccessible(true);
			Object value = null;
			try {
				value = field != null ? field.get(obj) : "";
			} catch (IllegalAccessException ex) {
				// ignore
			}
			String str = StringUtils.isBlank(value == null ? null : value.toString()) ? "" : value.toString();
			Validator validator = valEnum instanceof Validator ? ((Validator)valEnum) : null;
			res = validator != null ? validator.isValidateStr(str) : false;
			if (!res) {
				log.error(obj.getClass().toString() + " invalid value: " + valEnum.name() + ", " + value);
				break;
			}
		}
		return res;
	}

	public static <T> String generateRecord(T obj, Enum[] vals) {
		final String sep = ",";
		StringBuilder sb = new StringBuilder();
		for (Enum valEnum : vals) {
			Field field = null;
			try {
				field = obj.getClass().getDeclaredField(valEnum.name());
			} catch (NoSuchFieldException ex) {
				// ignore
			}
			field.setAccessible(true);
			Object value = null;
			try {
				value = field != null ? field.get(obj) : "";
			} catch (IllegalAccessException ex) {
				// ignore
			}
			String str = StringUtils.isBlank(value == null ? null : value.toString()) ? "" : value.toString();
			sb.append("\"").append(str).append("\"").append(sep);
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - sep.length());
		}
		return sb.toString();
	}

	public static <T> void initFrom(T obj, String[] arr, Enum[] vals) {
		for (int i = 0; i < vals.length; ++i) {
			Field field = null;
			try {
				field = obj.getClass().getDeclaredField(vals[i].name());
			} catch (NoSuchFieldException ex) {
				// ignore
			}
			if (field == null || i >= arr.length) {
				continue;
			}
			field.setAccessible(true);
			try {
				field.set(obj, arr[i]);
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				// ignore
			}
		}
	}
}
