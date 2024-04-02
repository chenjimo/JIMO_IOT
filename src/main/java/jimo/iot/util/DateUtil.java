package jimo.iot.util;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/***
 * 用作对时间进行规范处理了的工具
 */
public class DateUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /***
     * @param localDateTime 2022-08-05T12:00:05.459
     * @return 2022-08-05 12:00:05
     */
    public static String localDateTimeToString(LocalDateTime localDateTime){
        return localDateTime.format(formatter);
    }

    /***
     * @param date Fri Aug 05 11:58:50 CST 2022
     * @return 2022年8月5日 星期五
     */
    public static String dateToString(Date date){
        DateFormat df = DateFormat.getDateInstance(DateFormat.ERA_FIELD);
        return df.format(date);
    }

    /***
     * 根据秒数据处理动态显示天-时-分
     * @param seconds
     * @return
     */
    public static String formatDuration(long seconds) {
        // 定义一个StringBuilder来构建结果字符串
        StringBuilder result = new StringBuilder();

        // 计算天数
        long days = seconds / (24 * 3600);
        if (days > 0) {
            result.append(days).append("天 ");
            seconds %= (24 * 3600);
        }

        // 计算小时数
        long hours = seconds / 3600;
        if (hours > 0) {
            result.append(hours).append("小时 ");
            seconds %= 3600;
        }

        // 计算分钟数
        long minutes = seconds / 60;
        if (minutes > 0 || result.length() == 0) {
            result.append(minutes).append("分钟");
        }

        return result.toString();
    }

}
