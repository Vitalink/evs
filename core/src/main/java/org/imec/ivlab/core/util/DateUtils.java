package org.imec.ivlab.core.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    public static final String DEFAULT_DATETIME_FORMAT_SHORT = "yyyyMMdd-HHmmss";

    public static String formatDate(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String stringDate = sdf.format(date);
        return stringDate;
    }

    public static String formatDate(Date date) {
        return formatDate(date, DEFAULT_DATE_FORMAT);
    }

    public static String formatDateTime(Date date) {
        return formatDateTime(date, DEFAULT_DATE_FORMAT);
    }

    public static String formatDateTime(Date date, String format) {

        return formatDate(date, format);

    }

    public static Date parseDate(String dateString, String format) throws ParseException {
        DateFormat df = new SimpleDateFormat(format);
        return df.parse(dateString);
    }

    public static Date getDate() {
        return Calendar.getInstance().getTime();
    }

    public static GregorianCalendar toCalendar(LocalDate localDate) {
        return GregorianCalendar.from(localDate.atStartOfDay(ZoneId.systemDefault()));
    }

    public static GregorianCalendar toCalendar(LocalDateTime localDateTime) {
        return GregorianCalendar.from(localDateTime.atZone(ZoneId.systemDefault()));
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(LocalDate localDate) throws DatatypeConfigurationException {
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(toCalendar(localDate));
        return xcal;
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(LocalDateTime localDateTime) throws DatatypeConfigurationException {
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(toCalendar(localDateTime));
        return xcal;
    }


    public static LocalTime toLocalTime(XMLGregorianCalendar xmlGregorianCalendar) {

        return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalTime();

    }

    public static LocalDate toLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {

        return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDate();

    }

    public static LocalDate toLocalDate(DateTime joDateTime) {

        return LocalDate.of(
            joDateTime.getYear(),
            joDateTime.getMonthOfYear(),
            joDateTime.getDayOfMonth()
        );
    }

    public static LocalTime toLocalTime(DateTime joDateTime) {
        return LocalTime.of(
            joDateTime.getHourOfDay(),
            joDateTime.getMinuteOfHour(),
            joDateTime.getSecondOfMinute()
        );
    }


    public static LocalDateTime toLocalDateTime(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault());
    }

    // TODO issue here
    public static LocalDateTime toLocalDateTime(org.joda.time.DateTime calendar) {
        if (calendar == null) {
            return null;
        }
        return LocalDateTime.of(
            calendar.getYear(),
            calendar.getMonthOfYear(),
            calendar.getDayOfMonth(),
            calendar.getHourOfDay(),
            calendar.getMinuteOfHour(),
            calendar.getSecondOfMinute()
        );
    }


    public static LocalDate toLocalDate(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate;
    }

    public static Calendar getCalendar() {
        return Calendar.getInstance();
    }

    public static LocalDate toLocalDate(Calendar calendar) {
        return calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
