package com.spl.util;

public class ThreadUtil {

    public static final ThreadLocal<Long> CURRENT_THREAD_LOCAL = new ThreadLocal<>();
    public static final ThreadLocal<Long> CURRENT_LAST_THREAD_LOCAL = new ThreadLocal<>();

}
