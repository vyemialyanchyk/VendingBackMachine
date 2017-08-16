package com.vending.back.machine.util;

/**
 * vyemialyanchyk on 2/8/2017.
 */
public class Log {

    public static String infoStr(Object... params) {
        final String paramsSeparator = ", ";
        final StringBuilder sb = new StringBuilder();
        sb.append('(');
        int i = 0;
        for (Object obj : params) {
            sb.append(i++).append(": ").append(obj).append(paramsSeparator);
        }
        if (i > 0) {
            sb.delete(sb.length() - paramsSeparator.length(), sb.length());
        }
        sb.append(')');
        return sb.toString();
    }

}
