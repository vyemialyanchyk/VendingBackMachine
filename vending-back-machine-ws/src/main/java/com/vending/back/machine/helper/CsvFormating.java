package com.vending.back.machine.helper;

import java.util.ArrayList;

/**
 * vyemialyanchyk on 12/20/2016.
 */
public class CsvFormating {

    public static String[] parseCsvLine(String line) {
        if (line == null) {
            return null;
        }
        ArrayList<String> store = new ArrayList<String>();
        StringBuilder curVal = new StringBuilder();
        boolean inquotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (inquotes) {
                if (ch == '\"') {
                    inquotes = false;
                } else {
                    curVal.append(ch);
                }
            } else {
                if (ch == '\"') {
                    inquotes = true;
                    if (curVal.length() > 0) {
                        //if this is the second quote in a value, add a quote
                        //this is for the double quote in the middle of a value
                        curVal.append('\"');
                    }
                } else if (ch == ',') {
                    store.add(curVal.toString());
                    curVal = new StringBuilder();
                } else {
                    curVal.append(ch);
                }
            }
        }
        store.add(curVal.toString());
        return store.toArray(new String[0]);
    }

    public static String[] parsePseudoCsvLine(String line) {
        if (line == null) {
            return null;
        }
        line = line.trim();
        int beginIndex = 0, endIndex = line.length();
        char ch = line.charAt(line.length() - 1);
        if (ch == '\"') {
            endIndex--;
        }
        ch = line.charAt(0);
        if (ch == '\"') {
            beginIndex++;
        }
        return line.substring(beginIndex, endIndex).split("\",\"");
    }

    public static String[] parsePseudoCsvLineTrimAndIntern(String line) {
        String[] res = parsePseudoCsvLine(line);
        if (res != null) {
            for (int i = 0; i < res.length; ++i) {
                res[i] = res[i].trim().intern();
            }
        }
        return res;
    }

    public static String[] parseCsvLineTrimAndIntern(String line) {
        String[] res = parseCsvLine(line);
        if (res != null) {
            for (int i = 0; i < res.length; ++i) {
                res[i] = res[i].trim().intern();
            }
        }
        return res;
    }

}
