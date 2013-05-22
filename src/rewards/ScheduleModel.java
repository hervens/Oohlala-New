package rewards;

import android.graphics.Bitmap;

public class ScheduleModel {
	public int course_id;
	public String course_name;
	public String course_code;
	public int course_time_id;
	public String location;
	public boolean is_biweekly;
	public int day_of_week;
	public int start_time;
	public int end_time;

	public ScheduleModel(int course_id, String course_name, String course_code, int course_time_id, String location, boolean is_biweekly, int day_of_week, int start_time, int end_time) {
		// TODO Auto-generated constructor stub
		this.course_id = course_id;
		this.course_name = course_name;
		this.course_code = course_code;
		this.course_time_id = course_time_id;
		this.location = location;
		this.is_biweekly = is_biweekly;
		this.day_of_week = day_of_week;
		this.start_time = start_time;
		this.end_time = end_time;
	}

}
