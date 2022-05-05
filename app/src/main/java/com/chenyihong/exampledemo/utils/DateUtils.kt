package com.chenyihong.exampledemo.utils

import android.annotation.SuppressLint
import android.content.Context
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * desc:
 *
 * @author biaowen.yu
 * @date 2020/3/23 17:10
 *
 **/

@Suppress("unused", "MemberVisibilityCanBePrivate")
@SuppressLint("SimpleDateFormat")
object DateUtils {

    val DATE_FORMATTER_SLASH_1 = SimpleDateFormat("yy/MM/dd")
    val DATE_FORMATTER_MONTH_DAY_WITH_SEPARATOR = SimpleDateFormat("MM/dd")
    val DATE_FORMATTER_YEAR_STR_MONTH_STR_DAY_STR_WITH_BROKEN_LINE = SimpleDateFormat("yyyy-MM-dd")
    val DATE_FORMATTER_HOUR_MINUTE = SimpleDateFormat("HH:mm")
    val DATE_FORMATTER_MONTH_STR_DAY_STR = SimpleDateFormat("MM月dd日")
    val DATE_FORMATTER_MONTH_STR_DAY_STR_WITHOUT_SECOND = SimpleDateFormat("MM月dd日 HH:mm")
    val DATE_FORMATTER_MONTH_STR_DAY_STR_FULL_TIME = SimpleDateFormat("MM月dd日 HH:mm:ss")
    val DATE_FORMATTER_YEAR_STR_MONTH_STR_DAY_STR = SimpleDateFormat("yyyy年MM月dd日")

    val DATE_FORMATTER_FULL_PATTERN = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val DATE_FORMATTER_FULL_PATTERN_STR = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
    val DATE_FORMATTER_FULL_PATTERN_WITHOUT_SECOND = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val DATE_FORMATTER_FULL_PATTERN_STR_WITHOUT_SECOND = SimpleDateFormat("yyyy年MM月dd日 HH:mm")
    val DATE_FORMATTER_FULL_PATTERN_STR_FULL_TIME = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
    val DATE_FORMATTER_FULL_PATTERN_STR_WITHOUT_SECOND1 = SimpleDateFormat("yyyy/MM/dd HH:mm")
    val DATE_FORMATTER_YEAR_MONTH_DAY_WITH_SEPARATOR = SimpleDateFormat("yyyy/MM/dd")
    val DATE_FORMATTER_YEAR_MONTH_DAY_WITH_POINT = SimpleDateFormat("yyyy.MM.dd")

    const val SECOND: Long = 1000
    const val MINUTE = SECOND * 60
    const val HOURS = MINUTE * 60
    const val DAY = HOURS * 24

    @JvmStatic
    fun getBeginDayOfWeek(): Date {
        val date = Date()
        val cal = Calendar.getInstance()
        cal.time = date
        var dayofweek = cal.get(Calendar.DAY_OF_WEEK)
        if (dayofweek == 1) {
            dayofweek += 7
        }
        cal.add(Calendar.DATE, 2 - dayofweek)
        return getDayStartTime(cal.time)
    }

    @JvmStatic
    fun getEndDayOfWeek(): Date {
        val cal = Calendar.getInstance()
        cal.time = getBeginDayOfWeek()
        cal.add(Calendar.DAY_OF_WEEK, 6)
        val weekEndSta = cal.time
        return getDayEndTime(weekEndSta)
    }

    @JvmStatic
    fun getDayStartTime(d: Date): Timestamp {
        val calendar = Calendar.getInstance()
        calendar.time = (d)
        calendar.set(
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0
        )
        calendar.set(Calendar.MILLISECOND, 0)
        return Timestamp(calendar.timeInMillis)
    }

    @JvmStatic
    fun getDayEndTime(d: Date): Timestamp {
        val calendar = Calendar.getInstance()
        calendar.time = (d)
        calendar.set(
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59
        )
        calendar.set(Calendar.MILLISECOND, 999)
        return Timestamp(calendar.timeInMillis)
    }

    @JvmStatic
    fun getNextDay(date: Date, i: Int): Date {
        val cal = GregorianCalendar()
        cal.time = (date)
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + i)
        return cal.time
    }

    @JvmStatic
    fun isTheSameDate(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return cal1[Calendar.YEAR] == cal2[Calendar.YEAR] &&
                cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR]
    }

    @JvmStatic
    fun isTheSameCalendar(date1: Calendar, date2: Calendar): Boolean {
        return date1[Calendar.YEAR] == date2[Calendar.YEAR] &&
                date1[Calendar.DAY_OF_YEAR] == date2[Calendar.DAY_OF_YEAR]
    }

    fun formatLongToTimeStr(l: Long): String {
        var minute = 0
        var second: Int = l.toInt()
        if (second > 60) {
            minute = second / 60
            second %= 60
        }
        if (minute > 60) {
            minute %= 60
        }

        var secondText: String = second.toString()
        if (second < 10) {
            secondText = "0$secondText"
        }

        return "$minute:$secondText"
    }

    @JvmStatic
    fun isCurrentYear(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        calendar.time = Date(System.currentTimeMillis())
        val currentYear = calendar.get(Calendar.YEAR)
        return year == currentYear
    }

    @JvmStatic
    fun isCurrentWeek(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.time = date
        val now = Calendar.getInstance()
        now.firstDayOfWeek = Calendar.MONDAY
        return calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                calendar.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)
    }

    @JvmStatic
    fun monthDiffOverOne(before: Long, after: Long): Boolean {
        val timeBefore = Calendar.getInstance()
        val timeAfter = Calendar.getInstance()
        timeBefore.timeInMillis = before
        timeAfter.timeInMillis = after

        val yearDiff = timeAfter.get(Calendar.YEAR) - timeBefore.get(Calendar.YEAR)
        val monthDiff = timeAfter.get(Calendar.MONTH) - timeBefore.get(Calendar.MONTH)
        return abs(yearDiff) > 0 || abs(monthDiff) > 0
    }

    @JvmStatic
    fun yearDiff(before: Long, after: Long): Int {
        val timeBefore = Calendar.getInstance()
        val timeAfter = Calendar.getInstance()
        timeBefore.timeInMillis = before
        timeAfter.timeInMillis = after

        return timeAfter.get(Calendar.YEAR) - timeBefore.get(Calendar.YEAR)
    }

    @JvmStatic
    fun monthDiff(before: Long, after: Long): Int {
        val timeBefore = Calendar.getInstance()
        val timeAfter = Calendar.getInstance()
        timeBefore.timeInMillis = before
        timeAfter.timeInMillis = after

        return timeAfter.get(Calendar.MONTH) - timeBefore.get(Calendar.MONTH)
    }

    @JvmStatic
    fun dayDiff(before: Long, after: Long): Int {
        val timeBefore = Calendar.getInstance()
        val timeAfter = Calendar.getInstance()
        timeBefore.timeInMillis = before
        timeAfter.timeInMillis = after

        return timeAfter.get(Calendar.DAY_OF_YEAR) - timeBefore.get(Calendar.DAY_OF_YEAR)
    }

    @JvmStatic
    fun getDiffTimeStr(diffTime: Long, isNeedHour: Boolean): String {
        val diffTimeStr = StringBuilder()
        val hour = diffTime / HOURS
        val minute = (diffTime - HOURS * hour) / MINUTE
        val second = (diffTime - HOURS * hour - MINUTE * minute) / SECOND

        if (hour > 0) {
            if (hour < 10) {
                diffTimeStr.append("0")
            }
            diffTimeStr.append(hour)
            diffTimeStr.append(":")
        } else {
            if (isNeedHour) {
                diffTimeStr.append("00")
                diffTimeStr.append(":")
            }
        }

        if (minute > 0) {
            if (minute < 10) {
                diffTimeStr.append("0")
            }
            diffTimeStr.append(minute)
            diffTimeStr.append(":")
        } else {
            diffTimeStr.append("00")
            diffTimeStr.append(":")
        }

        if (second > 0) {
            if (second < 10) {
                diffTimeStr.append("0")
            }
            diffTimeStr.append(second)
        } else {
            diffTimeStr.append("00")
        }
        return diffTimeStr.toString()
    }

    @JvmStatic
    fun isBeforeDay(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val now = Calendar.getInstance()

        return when {
            calendar.get(Calendar.YEAR) < now.get(Calendar.YEAR) -> {
                true
            }
            calendar.get(Calendar.YEAR) > now.get(Calendar.YEAR) -> {
                false
            }
            else -> {
                calendar.get(Calendar.DAY_OF_YEAR) < now.get(Calendar.DAY_OF_YEAR)
            }
        }
    }

    @JvmStatic
    fun isCurrentDay(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val now = Calendar.getInstance()

        return calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
    }

    @JvmStatic
    fun isAfterDay(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val now = Calendar.getInstance()

        return when {
            calendar.get(Calendar.YEAR) > now.get(Calendar.YEAR) -> {
                true
            }
            calendar.get(Calendar.YEAR) < now.get(Calendar.YEAR) -> {
                false
            }
            else -> {
                calendar.get(Calendar.DAY_OF_YEAR) > now.get(Calendar.DAY_OF_YEAR)
            }
        }
    }

    @JvmStatic
    fun getSpeedAllocationStr(diffTime: Long): String {
        val diffTimeStr = StringBuilder()

        val minute = (diffTime) / MINUTE
        val second = (diffTime - MINUTE * minute) / SECOND

        if (minute > 0) {
            if (minute < 10) {
                diffTimeStr.append("0")
            }
            diffTimeStr.append(minute)
            diffTimeStr.append("'")
        } else {
            diffTimeStr.append("0")
            diffTimeStr.append("'")
        }

        if (second > 0) {
            if (second < 10) {
                diffTimeStr.append("0")
            }
            diffTimeStr.append(second)
            diffTimeStr.append("''")
        } else {
            diffTimeStr.append("00")
            diffTimeStr.append("''")
        }
        return diffTimeStr.toString()
    }

    @JvmStatic
    fun getTimeStr(diffTime: Long, fullMinuteUnit: Boolean = true): String {
        val diffTimeStr = StringBuilder()

        val hour = diffTime / HOURS
        val minute = (diffTime - HOURS * hour) / MINUTE

        if (hour > 0) {
            diffTimeStr.append(hour)
            diffTimeStr.append("小时")
        }

        if (minute > 0) {
            diffTimeStr.append(minute)
            diffTimeStr.append(if (fullMinuteUnit) "分钟" else "分")
        }
        return diffTimeStr.toString()
    }

    @JvmStatic
    fun getDiffTimeStr(diffTime: Long, context: Context, fullMinuteUnit: Boolean = true): String {
        val diffTimeStr = StringBuilder()
        val hour = diffTime / HOURS
        val minute = (diffTime - HOURS * hour) / MINUTE
        val second = (diffTime - HOURS * hour - MINUTE * minute) / SECOND

        if (hour > 0) {
            diffTimeStr.append(hour)
            diffTimeStr.append("小时")
        }

        if (minute > 0) {
            diffTimeStr.append(minute)
            diffTimeStr.append(if (fullMinuteUnit) "分钟" else "分")
        }

        if (second > 0) {
            diffTimeStr.append(second)
            diffTimeStr.append("秒")
        }
        return diffTimeStr.toString()
    }

    @JvmStatic
    @SuppressLint("SimpleDateFormat")
    fun getAgeFromBirthTime(birthDayStr: String?): String {
        if (birthDayStr.isNullOrEmpty() || birthDayStr.isNullOrBlank()) {
            return "-"
        }
        var date: Date? = null
        try {
            date = SimpleDateFormat("yyyy-MM-dd").parse(birthDayStr)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (date != null) {
            val cal = Calendar.getInstance()
            val yearNow = cal[Calendar.YEAR]
            val monthNow = cal[Calendar.MONTH] + 1
            val dayNow = cal[Calendar.DATE]
            cal.time = date
            val selectYear = cal[Calendar.YEAR]
            val selectMonth = cal[Calendar.MONTH] + 1
            val selectDay = cal[Calendar.DATE]

            val yearMinus = yearNow - selectYear
            val monthMinus = monthNow - selectMonth
            val dayMinus = dayNow - selectDay

            var age = yearMinus
            if (yearMinus <= 0) {
                age = 0
            }

//                if (monthMinus < 0) {
//                    age -= 1
//                } else if (monthMinus == 0) {
//                    if (dayMinus < 0) {
//                        age -= 1
//                    }
//                }
            return age.toString()
        }
        return "-"
    }
}