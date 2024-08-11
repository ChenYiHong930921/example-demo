package com.chenyihong.plugin.utils;

public class CommonUtils {

    public static void log(String message) {
        System.out.println("example-plugin-log:" + message);
    }

    public static boolean isEmpty(String stringValue) {
        return stringValue == null || stringValue.isEmpty() || stringValue.isBlank();
    }
}
