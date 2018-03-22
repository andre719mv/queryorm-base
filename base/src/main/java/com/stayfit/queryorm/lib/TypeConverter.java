package com.stayfit.queryorm.lib;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Администратор on 10/8/2016.
 */

public class TypeConverter {
    /**
     * Read Retreiwe Date from SQLite dateTime string
     * @param dateTimeStr SQLite datetime string
     * @return Date
     */
    public Date readDate(String dateTimeStr) {
        if(StrUtils.isEmpty(dateTimeStr))
            return null;

        DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = iso8601Format.parse(dateTimeStr);
        } catch (ParseException e) {
            //Log.e("ORM", "Parsing ISO8601 datetime failed", e);
            throw new IllegalArgumentException("Wrong datetime in SQL");
        }

        return date;
    }

    /**
     * Writes date to SQLite datetime string
     * @return SQLite datetime string
     */
    public String writeDateTime(Date date) {
        if(date == null)
            return  null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }
}
