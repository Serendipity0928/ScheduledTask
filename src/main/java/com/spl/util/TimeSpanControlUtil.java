package com.spl.util;

import org.junit.Test;

import java.util.Calendar;

public class TimeSpanControlUtil {

    // 今日凌晨时间戳
    private static final long todayStartTimeMillis;

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        todayStartTimeMillis = calendar.getTimeInMillis();
    }

    /**
     * 卫生检验考试证书查询时间段控制，时间段为每天的[10:29, 10:31]
     * @return true:执行任务, false:跳过任务
     */
    public static boolean healthExamScheduleTime() {
        long currentTimeMillis = System.currentTimeMillis();
        long timeDelta = currentTimeMillis - todayStartTimeMillis - 37740000;
        return timeDelta > 0 && timeDelta < 120000;
    }

}
