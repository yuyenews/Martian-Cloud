package com.mars.cloud.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {

    /**
     * 两个时间是否在一个范围内
     * @param one
     * @param two
     * @param ran
     * @param fmt
     * @return
     * @throws Exception
     */
    public static boolean range(Date one, Date two, long ran, String fmt) throws Exception {
        one = getFmtDate(one, fmt);
        two = getFmtDate(two, fmt);

        long oneTime = one.getTime();
        long twoTime = two.getTime();

        long substr = oneTime - twoTime;
        if (substr < 0) {
            substr = substr * -1;
        }

        if (substr <= ran) {
            return true;
        }
        return false;
    }

    /**
     * 获取格式化后的时间
     * @param date
     * @param fmt
     * @return
     * @throws Exception
     */
    public static Date getFmtDate(Date date, String fmt) throws Exception {
        String fmtDateString = getFmtString(date, fmt);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fmt);
        return simpleDateFormat.parse(fmtDateString);
    }

    /**
     * 获取格式化后的事件字符串
     * @param date
     * @param fmt
     * @return
     */
    public static String getFmtString(Date date, String fmt){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fmt);
        return simpleDateFormat.format(date);
    }
}
