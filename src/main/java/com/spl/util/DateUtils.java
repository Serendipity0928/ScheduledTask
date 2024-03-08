package com.spl.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static long convertDateToStamp(String dateStr) throws ParseException {
        if(StringUtils.isBlank(dateStr)) {
            return 0;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = format.parse(dateStr);
        return parse.getTime();
    }

    public static String convertStampToDate(Long stamp) {
        if(stamp < 0) {
            return null;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(stamp);
        return format.format(date);
    }
}
