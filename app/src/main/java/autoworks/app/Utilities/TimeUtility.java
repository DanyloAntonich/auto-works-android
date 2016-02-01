package autoworks.app.Utilities;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import autoworks.app.controller.Constant;

/**
 * Created by Administrator on 1/4/2016.
 */
public class TimeUtility {
    ///////////////convert date
    public static long[] getHours(String start, String end) {
        long[] difs = new long[2];

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm a");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(start);
            d2 = format.parse(end);

            // in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            difs[0] = diffHours;
            difs[1] = diffMinutes;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return difs;

    }
    public static String getDateStringFromTimeStamp(long time) {
//        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//        cal.setTimeInMillis(time);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(time * 1000);
        String strDate = dateFormat.format(date).toString();

        return strDate;
    }

    public static String dateWithCustomFormat(String date, String curFormat,
                                              String format) {
        DateFormat dateFormat = new SimpleDateFormat(curFormat);
        Date convertedDate = null;

        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat1 = new SimpleDateFormat(format);

        return dateFormat1.format(convertedDate);
    }
    public static String parseDateFormat(String dateString) {
        if (dateString == null) {
            return "";
        }
        Date date = null;

        String mDate = dateString;
        DateFormat dateFormat = new SimpleDateFormat(Constant.DATE_PATTERN_MMM_dd_yyyy,
                Locale.ENGLISH);

        try {
            if (date == null) {
                date = dateFormat.parse(dateString);
            }
            String mdateString = dateFormat.format(date);
            date = dateFormat.parse(mdateString);
            Date currentdate = new Date();
            String when = dateFormat.format(currentdate);
            currentdate = dateFormat.parse(when);

            long diffInMillisec = currentdate.getTime() - date.getTime();
            long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMillisec);
            long seconds = diffInSec % 60;
            diffInSec /= 60;
            long minutes = diffInSec % 60;
            diffInSec /= 60;
            long hours = diffInSec % 24;
            diffInSec /= 24;
            long days = diffInSec % 30;
            diffInSec /= 30;
            long months = diffInSec % 12;
            diffInSec /= 12;
            long year = diffInSec;
            mDate = String.valueOf(seconds);
            if (year > 0) {

                int length = mdateString.lastIndexOf(":");
                mdateString = mdateString.substring(0, length);
                mdateString = mdateString.replace(";", " at ");
                mDate = mdateString;
            } else if (months > 0) {

                int length = mdateString.lastIndexOf(":");
                mdateString = mdateString.substring(0, length);
                mdateString = mdateString.replace(";", " at,");
                mDate = mdateString;
            } else if (days > 1) {

                int length = mdateString.lastIndexOf(":");
                mdateString = mdateString.substring(0, length);
                mdateString = mdateString.replace(";", " at,");
                mDate = mdateString;
            } else if (days == 1) {
                int end = mdateString.lastIndexOf(";");
                int length = mdateString.lastIndexOf(":");
                mDate = "Yesterday at, "
                        + mdateString.substring(end + 1, length);
            } else if (days > 0) {
                mDate = String.valueOf(days) + " days ago";
            } else if (hours > 0) {
                mDate = String.valueOf(hours) + " hours ago";
            } else {
                mDate = String.valueOf(minutes) + " minutes ago";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDate;
    }
    public static String countTime(long timeStamp){
        if(timeStamp < 60){
            return String.valueOf(timeStamp) + "s";
        }
        String str = "0s";
        int second = (int)(timeStamp);
        int result = second;

        if((int)(result / 60) < 60){
            str = String.valueOf((int) (result / 60)) + "m";

        } else {
            result = (int)(second / 3600);
            if(result < 24){
                str = String.valueOf(result) + "h";
            }else{
                result = (int)(second /  (3600 * 24));
                if(result < 7){
                    str = String.valueOf(result) + "d";
                }else{
                    result = (int)(second / (3600 * 24 * 7));
                    if(result < 4){
                        str = String.valueOf(result) + "w";
                    }else {
                        result = (int)(second / (3600 * 24 * 30));
                        if(result < 12){
                            str = String.valueOf(result) + "m";
                        }else {
                            result = (int)(second / (3600 * 24 * 365));
                            str = String.valueOf(result) + "y";
                        }
                    }
                }
            }
        }

        return str;
    }
    @SuppressLint("SimpleDateFormat")
    public static Date getMyDateFromGMTString(String gmtDateString) {
        Date date = null;
        try {
            DateFormat df = new SimpleDateFormat(Constant.GMT_PATTERN_24);
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = df.parse(gmtDateString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

//    static AlertDialog alertDialog = null;

    public static String getCurrentTimeStamp() {

        double timestamp = System.currentTimeMillis() / 1000f;

        return String.valueOf((int) timestamp);
    }
    public static Long getTimeStampFromString(String str_date) {
        str_date = str_date.replace("-", "/");
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = (Date)formatter.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        System.out.println("Today is " +date.getTime());
        return date.getTime();
    }

    public static String getDatewithFormat(String date) {

        Date mDate = new Date(date.replace("-", "/"));
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        String showDate = df.format(mDate);

        return showDate;
    }

    public static String getDatewithFormat2(Date date) {
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MMM/yyyy");
        String showDate1 = df2.format(date);

        return showDate1;
    }

    public static String getDatewithFormat3(Date date) {

        SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
        String showDate1 = df2.format(date);

        return showDate1;
    }

    public static int getCurrentDAY() {
        // TODO Auto-generated method stub
        int showhours = 1;
        try {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            Date mDate = cal.getTime();
            SimpleDateFormat df2 = new SimpleDateFormat("dd");
            String date = df2.format(mDate);
            showhours = Integer.parseInt(date);
        } catch (Exception e) {

        }
        return showhours;
    }

    public static Integer getdaysFromdate(String date) {
        int days = 1;
        try {
            Date mDate = new Date(date.replace("-", "/"));



            SimpleDateFormat df = new SimpleDateFormat("dd");
            String sdays = df.format(mDate);
            days = Integer.parseInt(sdays);
        } catch (Exception e) {

        }
        return days;

    }

}
