package com.zhjy.hdcivilization.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @version 对不同日期和时间的格式方法的封装
 * @类名称：DateUtil
 * @类描述： 关于日期和时间的管理类
 * @创建人：HXF
 * @修改人：
 * @创建时间：2015-12-10 下午2:38:14
 */
public class DateUtil {

    private static DateUtil instance;

    private DateUtil() {

    }

    public static DateUtil getInstance() {
        if (instance == null) {
            synchronized (DateUtil.class) {
                if (instance == null) {
                    instance = new DateUtil();
                }
            }
        }
        return instance;
    }

    public final long ONEDAYMIL = 1000 * 60 * 60 * 24;

    /**
     * get special time by special rules
     *
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public String getSpecTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd hh:mm");
        return sdf.format(new Date(time));
    }


    /**
     * 通过long类型的值返回当前的 星期几
     *
     * @param time
     * @return
     */
    public String getWeekday(long time) {
        Calendar calen = Calendar.getInstance();
        calen.setTimeInMillis(time);
        int week = calen.get(Calendar.DAY_OF_WEEK);
        String result = "星期一";
        switch (week) {
            case Calendar.SUNDAY:
                result = "星期日";
                break;
            case Calendar.MONDAY:
                result = "星期一";
                break;
            case Calendar.TUESDAY:
                result = "星期二";
                break;
            case Calendar.WEDNESDAY:
                result = "星期三";
                break;
            case Calendar.THURSDAY:
                result = "星期四";
                break;
            case Calendar.FRIDAY:
                result = "星期五";
                break;
            case Calendar.SATURDAY:
                result = "星期六";
                break;
            default:
                result = "星期一";
                break;
        }
        return result;
    }

    /**
     * 判断两个时间是否属于同一天
     *
     * @param time1
     * @param time2
     * @return
     */
    public boolean isSameDay(long time1, long time2) {
        Calendar calen = Calendar.getInstance();
        calen.setTimeInMillis(time1);
        int day1 = calen.get(Calendar.DAY_OF_YEAR);
        calen.setTimeInMillis(time2);
        int day2 = calen.get(Calendar.DAY_OF_YEAR);
        return day1 == day2;
    }

    /**
     * 通过已知参数返回当前时间的年份
     *
     * @param time
     * @return
     */
    public int getYear(long time) {
        Calendar calen = Calendar.getInstance();
        calen.setTimeInMillis(time);
        return calen.get(Calendar.YEAR);
    }

    /**
     * 通过已知参数返回当前时间的分月(我能识别的1,2,3...)
     *
     * @param time
     * @return
     */
    public int getMonth(long time) {
        Calendar calen = Calendar.getInstance();
        calen.setTimeInMillis(time);
        return calen.get(Calendar.MONTH) + 1;
    }

    /**
     * 通过已知参数返回当前时间的日期（月份的）
     *
     * @param time
     * @return
     */
    public int getDayOfMonth(long time) {
        Calendar calen = Calendar.getInstance();
        calen.setTimeInMillis(time);
        return calen.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 通过已知参数返回当前时间的日期（年份的）
     *
     * @param time
     * @return
     */
    public int getDayOfYear(long time) {
        Calendar calen = Calendar.getInstance();
        calen.setTimeInMillis(time);
        return calen.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 通过给定的时间，返回我想要的日期形式
     *
     * @param time
     * @return
     */
    public String getMyDate(long time) {
        return getMonth(time) + "月/" + getDayOfMonth(time);
    }


    /**
     * 通过给定的时间，返回我想要的日期形式
     *
     * @param time
     * @return
     */
    public String getMyDateOne(long time) {
        return getMonth(time) + "-" + getDayOfMonth(time);
    }

    /**
     * 通过给定时间，返回小时和分钟的时间形式
     *
     * @param time
     * @return
     */
    public String getHourMinute(long time) {
        Calendar calen = Calendar.getInstance();
        calen.setTimeInMillis(time);
        String hour = calen.get(Calendar.HOUR_OF_DAY) + "";
        String minute = calen.get(Calendar.MINUTE) + "";
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        return hour + ":" + minute;
    }

    /**
     * @param time
     * @return
     * @描述:得到小时的时间形式
     */
    public String getHour(long time) {
        Calendar calen = Calendar.getInstance();
        calen.setTimeInMillis(time);
        String hour = calen.get(Calendar.HOUR_OF_DAY) + "";
        return hour;
    }

    /**
     * @param time
     * @return
     * @描述:得到分钟的时间形式
     */
    public String getMinute(long time) {
        Calendar calen = Calendar.getInstance();
        calen.setTimeInMillis(time);
        String minute = calen.get(Calendar.MINUTE) + "";
        return minute;
    }

    /**
     * @param time
     * @return
     * @描述:得到固定格式的月和日07-13
     */
    public String getDateTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        return sdf.format(new Date(time));
    }

    /**
     * @param time
     * @return
     * @描述:得到固定格式的月和日2015-07-13
     */
    public String getYearOrMonthOrDay(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        return sdf.format(new Date(time));
    }

    /**
     * @param time
     * @return
     * @描述:得到固定格式的分和秒10:30
     */
    public String getMinuteOrSeconds(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        return sdf.format(new Date(time));
    }


    /**
     * @param time
     * @return
     * @描述:得到固定格式的月和日
     */
    public String getDayOrMonthOrYear(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(time));
    }

    /**
     * @param time
     * @return
     * @描述:得到固定格式的月和日
     */
    public String getDayOrMonthOrYear2(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        return sdf.format(new Date(time));
    }


    public String getDayOrMonthOrYear1(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        return sdf.format(new Date(time));
    }

    /**
     * @param time
     * @return
     * @描述:得到固定格式的月和日27/01/16
     */
    public String getCollectionDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        return sdf.format(new Date(time));
    }

    public String dateFormat2(Date date){
        return new SimpleDateFormat("yyyy年-MM月-dd日  HH:mm:ss", Locale.getDefault()).format(date);
    }

    /**
     * 计算long类型的时间是一天的下午还是上午
     * HH是24小时制的时间
     * hh是12小时制的时间
     * @param time
     * @return
     */
    public String getTimeDifference(long time){
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        int hour = Integer.parseInt(sdf.format(new Date(time)));
        System.out.println("hour = "+hour);
        if (hour<=12){
            return "AM";
        }else{
            return "PM";
        }
    }



    /**
     * 计算long类型的时间是一天的下午还是上午
     * HH是24小时制的时间
     * hh是12小时制的时间
     * @param time
     * @return
     */
    public String getTimeDifferences(long time){

        SimpleDateFormat sdf = new SimpleDateFormat("HH:MM");
        int hour = Integer.parseInt(sdf.format(new Date(time)));
        System.out.println("hour = "+hour);
        if (hour<=12){
            return sdf.format(new Date(time)).toString();
        }else{
//            System.out.println("hour2 = "+sdf.format(new Date(time)).toString());
            SimpleDateFormat sdfs= new SimpleDateFormat("MM");
            int month = Integer.parseInt(sdfs.format(new Date(time)));
            String.valueOf(hour-12);
            StringBuffer sb = new StringBuffer();
            return sb.append(String.valueOf(hour-12)).append(":").append(String.valueOf(month)).toString();
        }
    }
}
