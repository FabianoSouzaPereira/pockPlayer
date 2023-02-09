package br.com.seventh.dguardcloud.utils

import android.annotation.SuppressLint
import androidx.core.net.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Seventh Ltda - http://www.seventh.com.br
 * Created by FabianoPereira on 27/05/2021.
 * Copyright (c) 2018 Seventh Ltda. All rights reserved.
 */

/** return a string date, passing the epoch in milliseconds and date format
 *      call:  epochToDateString(1532542937, "dd-MM-yyyy HH:mm:ss")
 *    return: "25-07-2018 15:22:17"
 * */
@SuppressLint("SimpleDateFormat")
fun epochToDateString(epoch: Long, formatString: String): String {
    val format = SimpleDateFormat(formatString)
    return format.format(Date(epoch))
}

/**
 *  return calendar date
 *
 *  @param epoch is the epoch to convert in milliseconds
 */
fun epochToCalendarDate(epoch: Long): Calendar {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = epoch * Constants.ONE_SECOND_IN_MILLISECONDS

    return calendar
}

/**
 * Convert day, month and year of Int to Time in Milliseconds
 */
fun dayMonthWeekToEpoch(
    day: Int, month: Int, year: Int, hour: Int,
    minutes: Int, seconds: Int
): Long {

    val date = Calendar.getInstance()
    date.set(year, month, day, hour, minutes, seconds)

    return date.timeInMillis / Constants.ONE_SECOND_IN_MILLISECONDS
}

fun convertDaysToEpoch(
    day: Int, month: Int, year: Int, hour: Int,
    minutes: Int, seconds: Int
): Calendar {

    val date = Calendar.getInstance()
    date.set(year, month, day, hour, minutes, seconds)

    return date
}

fun dayNowWithoutSecondsEpoch(): Long {
    val date = getDateNow()
    return dayMonthWeekToEpoch(
        date.get(Calendar.DAY_OF_MONTH),
        date.get(Calendar.MONTH),
        date.get(Calendar.YEAR),
        date.get(Calendar.HOUR_OF_DAY),
        date.get(Calendar.MINUTE), 0
    )
}

/** return the date actual with base at time zone */
fun getDateNow(): Calendar = Calendar.getInstance()


/** Get Unix time stamp for beginning day */
@Throws(ParseException::class)
fun getStartOfDayInMillis(date: String?): Long {
    val format = SimpleDateFormat("dd/mm/yyyy")
    val calendar = Calendar.getInstance()
    calendar.time = format.parse(date)
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    return calendar.timeInMillis
}

/**
 * Get Unix time stamp for end of day
 * @param date the date in the format ""dd/mm/yyyy""
 */
@Throws(ParseException::class)
fun getEndOfDayInMillis(date: String?): Long { // Add one day's time to the beginning of the day.
    // 24 hours * 60 minutes * 60 seconds * 1000 milliseconds = 1 day
    return getStartOfDayInMillis(date) + 24 * 60 * 60 * 1000
}

/** Check if date is betwing start and end time of day
 *  And if date is not greater then now
 */
fun validateHourTime(Date: Long, StartOfDay: Long, EndOfDay: Long): Boolean {
    if (StartOfDay < EndOfDay) {
        if (Date in StartOfDay..EndOfDay)
            return true
        if ((System.currentTimeMillis() / 1000) > StartOfDay)
            return true
    }
    return false
}

/** Diference betwing firstEpoch end EndEpoch in secunds
 *  timestamp = 1622378100  - 1622377800
 *  return number in secunds ex.: 300
 */
fun diferenceFirsAndEnd(first: Long, last: Long): Long {
    val ret =
        ((first * Constants.ONE_SECOND_IN_MILLISECONDS) - (last * Constants.ONE_SECOND_IN_MILLISECONDS))
    return ret / Constants.ONE_SECOND_IN_MILLISECONDS
}

fun stringToDate(dateString: String, format: String): Date {
    val dtStart = dateString //"2010-10-15T09:27:37Z"
    val format = SimpleDateFormat(format) //SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    try {
        val date = format.parse(dtStart)
        return date
    } catch (e: ParseException) {
        return Date()
    }

}


fun dateToString(date: Date, format: String): String {
    val dateFormat = SimpleDateFormat(format)    //SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    try {
        val dateTime = dateFormat.format(date)
        println("Current Date Time : $dateTime")
        return dateTime
    } catch (e: ParseException) {
        e.printStackTrace()
        return ""
    }
}


