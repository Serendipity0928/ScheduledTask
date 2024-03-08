package com.spl.util;

public class PrintUtils {


    public static void printUtil(Object msg) {
        System.out.println(DateUtils.convertStampToDate(System.currentTimeMillis()) + "-->" + msg);
    }

    public static void printUtilForClass(Class<?> clazz, Object msg) {
        System.out.println(DateUtils.convertStampToDate(System.currentTimeMillis()) + "" +
                ", className=" + clazz.getName()
                + "-->" + msg);
    }

}
