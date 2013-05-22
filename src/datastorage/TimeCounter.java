package datastorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;

import com.gotoohlala.R;


public class TimeCounter {
	
	public static boolean isEventGoingOn(String start_time, String end_time){
		long now = System.currentTimeMillis();
		long st = Long.parseLong(start_time) * 1000;
		long ed = Long.parseLong(end_time) * 1000;
		
		if (now >= st && now <= ed){
			return true;
		} else {
			return false;
		}
	}
	
	public static String getGameTime(int seconds){
		SimpleDateFormat sdf_Time = new SimpleDateFormat("h:mma");
		SimpleDateFormat sdf_Day = new SimpleDateFormat("EEEE");
		SimpleDateFormat sdf_Date = new SimpleDateFormat("MMM dd");
		
		Date testdate = new Date(Long.parseLong(String.valueOf(seconds)) * 1000);
		
		return sdf_Date.format(testdate) + ", " + sdf_Time.format(testdate);
	}
	
	public static String getTime(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd hh:mm a");
		Date testdate = new Date(Long.parseLong(time) * 1000);
		return sdf.format(testdate);
	}
	
	public static String getFreshUpdateTime(){
		Long now = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		Date testdate = new Date(now);
		return sdf.format(testdate);
	}
	
	public static String getDateChat(int time){
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
		Date testdate = new Date(Long.parseLong(String.valueOf(time)) * 1000);
		return sdf.format(testdate);
	}
	
	public static String getExactTimeChat(int time){
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy h:mm a");
		Date testdate = new Date(Long.parseLong(String.valueOf(time)) * 1000);
		return sdf.format(testdate);
	}
	
	public static String getTimeChat(int time){
		SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
		Date testdate = new Date(Long.parseLong(String.valueOf(time)) * 1000);
		return sdf.format(testdate);
	}
	
	public static String getOpeningTime(int time, Context c){
		if (time == -1){
			return c.getString(R.string.closed);
		} else if (time == 0){
			return c.getString(R.string.open_all_day);
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("h:mma");
			Date testdate = new Date(Long.parseLong(String.valueOf(time + getSecondsOnMidnight())) * 1000);
			return sdf.format(testdate);
		}
	}
	
	public static String getClassTime(int time){
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		Date testdate = new Date(Long.parseLong(String.valueOf(time + getSecondsOnMidnight())) * 1000);
		return sdf.format(testdate);
	}
	
	public static int getEpochClassTime(int time){
		return time + getSecondsOnMidnight();
	}
	
	public static String getUserEventTime(int time){
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		Date testdate = new Date(Long.parseLong(String.valueOf(time)) * 1000);
		return sdf.format(testdate);
	}
	
	public static boolean checkClassStartTimeDay(int time){
		if (time < 64800){
			return true;
		} else {
			return false;
		}
	}
	
	public static String convertDate(int seconds){
		int secondsOnMonday = getSecondsOnMonday();
		
		SimpleDateFormat sdf_Time = new SimpleDateFormat("h:mma");
		SimpleDateFormat sdf_Day = new SimpleDateFormat("EEEE");
		SimpleDateFormat sdf_Date = new SimpleDateFormat("MMM dd");
		
		Date testdate = new Date(Long.parseLong(String.valueOf(seconds)) * 1000);
		
		if (seconds >= secondsOnMonday){
			return sdf_Day.format(testdate) + " @ " + sdf_Time.format(testdate);
		} else {
			return sdf_Day.format(testdate) + ", " + sdf_Date.format(testdate) + " @ " + sdf_Time.format(testdate);
		}
	}
	
	public static String EventDate(int seconds){
		SimpleDateFormat sdf_Time = new SimpleDateFormat("h:mma");
		SimpleDateFormat sdf_Day = new SimpleDateFormat("EEEE");
		SimpleDateFormat sdf_Date = new SimpleDateFormat("MMM dd");
		
		Date testdate = new Date(Long.parseLong(String.valueOf(seconds)) * 1000);
		return sdf_Day.format(testdate) + ", " + sdf_Date.format(testdate) + " @ " + sdf_Time.format(testdate);
	}
	
	public static int getSecondsOnMidnight(){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		int midnight = (int) (c.getTimeInMillis()/1000);
		
		return midnight;
	}
	
	public static int getSecondsFromMidnightToNow(){
		int now = (int) (System.currentTimeMillis()/1000);
		return now - getSecondsOnMidnight();
	}
	
	public static int getSecondsOnMonday(){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK, 2);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		int monday = (int) (c.getTimeInMillis()/1000);
		
		return monday;
	}
	
	public static int getEpochTimeOnDate(int day, int month, int year){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.YEAR, year);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		int epoch = (int) (c.getTimeInMillis()/1000);
		
		return epoch;
	}
	
	public static int getEpochTimeOnDateAndTime(int day, int month, int year, int hour, int minute){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.YEAR, year);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		int epoch = (int) (c.getTimeInMillis()/1000);
		
		return epoch;
	}
	
	public static String getUserEventDate(int seconds){
		SimpleDateFormat sdf_Time = new SimpleDateFormat("h:mma");
		SimpleDateFormat sdf_Day = new SimpleDateFormat("EE");
		SimpleDateFormat sdf_Date = new SimpleDateFormat("MMM dd");
		
		Date testdate = new Date(Long.parseLong(String.valueOf(seconds)) * 1000);
		return sdf_Day.format(testdate) + ", " + sdf_Date.format(testdate);
	}
	
	public static String getMonthAndDay(int seconds){
		SimpleDateFormat sdf_Date = new SimpleDateFormat("MMM d");
		
		Date testdate = new Date(Long.parseLong(String.valueOf(seconds)) * 1000);
		return sdf_Date.format(testdate);
	}
	
	public static String getWeekdayMonthDayYear(int seconds){
		SimpleDateFormat sdf_Weekday = new SimpleDateFormat("EEEE");
		SimpleDateFormat sdf_MonthDayYear = new SimpleDateFormat("MMM d, yyyy");
		
		Date testdate = new Date(Long.parseLong(String.valueOf(seconds)) * 1000);
		return sdf_Weekday.format(testdate) + "\n" + sdf_MonthDayYear.format(testdate);
	}
	
	public static String getWeekdayMonthDayYear2(int seconds){
		SimpleDateFormat sdf_Weekday = new SimpleDateFormat("EE");
		SimpleDateFormat sdf_MonthDayYear = new SimpleDateFormat("MMM d");
		
		Date testdate = new Date(Long.parseLong(String.valueOf(seconds)) * 1000);
		return sdf_Weekday.format(testdate) + " " + sdf_MonthDayYear.format(testdate);
	}

}
