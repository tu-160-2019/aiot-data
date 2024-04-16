package net.srt.framework.common.utils;


import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期处理
 *
 * @author 阿沐 babamu@126.com
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
	/** 时间格式(yyyy-MM-dd) */
	public final static String DATE_PATTERN = "yyyy-MM-dd";
	/** 时间格式(yyyy-MM-dd HH:mm:ss) */
	public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static String[] parsePatterns = {
			"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
			"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
			"yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};


	public static String YYYY = "yyyy";

	public static String YYYY_MM = "yyyy-MM";

	public static String YYYY_MM_DD = "yyyy-MM-dd";

	public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

	public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     * @param date  日期
     * @return  返回yyyy-MM-dd格式日期
     */
	public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

	/**
	 * 日期格式化 日期格式为：yyyy-MM-dd
	 * @param date  日期
	 * @return  返回yyyy-MM-dd格式日期
	 */
	public static String formatDateTime(Date date) {
		return format(date, DATE_TIME_PATTERN);
	}

	/**
	 * 日期路径 即年/月/日 如2018/08/08
	 */
	public static final String datePath()
	{
		Date now = new Date();
		return DateFormatUtils.format(now, "yyyy/MM/dd");
	}

	/**
	 * 日期型字符串转化为日期 格式
	 */
	public static Date parseDate(Object str)
	{
		if (str == null)
		{
			return null;
		}
		try
		{
			return parseDate(str.toString(), parsePatterns);
		}
		catch (ParseException e)
		{
			return null;
		}
	}

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     * @param date  日期
     * @param pattern  格式，如：DateUtils.DATE_TIME_PATTERN
     * @return  返回yyyy-MM-dd格式日期
     */
    public static String format(Date date, String pattern) {
        if(date != null){
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    /**
     * 日期解析
     * @param date  日期
     * @param pattern  格式，如：DateUtils.DATE_TIME_PATTERN
     * @return  返回Date
     */
    public static Date parse(String date, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

	public static Date asDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date asDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate asLocalDate(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDateTime asLocalDateTime(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static String getDuration(long jobStartTimeMills, long jobEndTimeMills) {
		Instant startTime = Instant.ofEpochMilli(jobStartTimeMills);
		Instant endTime = Instant.ofEpochMilli(jobEndTimeMills);

		long days = ChronoUnit.DAYS.between(startTime, endTime);
		long hours = ChronoUnit.HOURS.between(startTime, endTime);
		long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
		long seconds = ChronoUnit.SECONDS.between(startTime, endTime);
		return days + "天 " + (hours - (days * 24)) + "小时 " + (minutes - (hours * 60)) + "分 "
				+ (seconds - (minutes * 60)) + "秒";
	}

	/**
	 * 获取当前Date型日期
	 *
	 * @return Date() 当前日期
	 */
	public static Date getNowDate()
	{
		return new Date();
	}

	/**
	 * 获取当前日期, 默认格式为yyyy-MM-dd
	 *
	 * @return String
	 */
	public static String getDate()
	{
		return dateTimeNow(YYYY_MM_DD);
	}

	public static final String getTime()
	{
		return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
	}

	public static final String dateTimeNow()
	{
		return dateTimeNow(YYYYMMDDHHMMSS);
	}

	public static final String dateTimeNow(final String format)
	{
		return parseDateToStr(format, new Date());
	}

	public static final String dateTime(final Date date)
	{
		return parseDateToStr(YYYY_MM_DD, date);
	}

	public static final String parseDateToStr(final String format, final Date date)
	{
		return new SimpleDateFormat(format).format(date);
	}

	public static final Date dateTime(final String format, final String ts)
	{
		try
		{
			return new SimpleDateFormat(format).parse(ts);
		}
		catch (ParseException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * 日期路径 即年/月/日 如20180808
	 */
	public static final String dateTime()
	{
		Date now = new Date();
		return DateFormatUtils.format(now, "yyyyMMdd");
	}

	/**
	 * 获取服务器启动时间
	 */
	public static Date getServerStartDate()
	{
		long time = ManagementFactory.getRuntimeMXBean().getStartTime();
		return new Date(time);
	}

	/**
	 * 计算相差天数
	 */
	public static int differentDaysByMillisecond(Date date1, Date date2)
	{
		return Math.abs((int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24)));
	}

	/**
	 * 计算时间差
	 *
	 * @param endDate 最后时间
	 * @param startTime 开始时间
	 * @return 时间差（天/小时/分钟）
	 */
	public static String timeDistance(Date endDate, Date startTime)
	{
		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		// long ns = 1000;
		// 获得两个时间的毫秒时间差异
		long diff = endDate.getTime() - startTime.getTime();
		// 计算差多少天
		long day = diff / nd;
		// 计算差多少小时
		long hour = diff % nd / nh;
		// 计算差多少分钟
		long min = diff % nd % nh / nm;
		// 计算差多少秒//输出结果
		// long sec = diff % nd % nh % nm / ns;
		return day + "天" + hour + "小时" + min + "分钟";
	}

	/**
	 * 增加 LocalDateTime ==> Date
	 */
	public static Date toDate(LocalDateTime temporalAccessor)
	{
		ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
		return Date.from(zdt.toInstant());
	}

	/**
	 * 增加 LocalDate ==> Date
	 */
	public static Date toDate(LocalDate temporalAccessor)
	{
		LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
		ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
		return Date.from(zdt.toInstant());
	}
}
