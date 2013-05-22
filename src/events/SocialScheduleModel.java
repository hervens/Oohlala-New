package events;

public class SocialScheduleModel {
	public int user_id;
	public String start_time_text;
	public String end_time_text;
	public int check_status;
	public String location;
	public double latitude;
	public double longitude;
	public int alert_time;
	public int start_time;
	public int end_time;
	public int status;
	public String description;
	public int event_id;
	public int extra_id;
	public String title;
	public int type;
	public int course_id;
	public String course_name;
	public String course_code;
	public int course_time_id;
	public int is_biweekly;
	public int day_of_week;
	public String color;
	public String icon;
	public int epoch_start_day;
	public int epoch_end_day;
	public String date;
	public int daySelected, month, year;

	public SocialScheduleModel(int user_id, String start_time_text, String end_time_text, int check_status, 
			String location, double latitude, double longitude, int alert_time, int start_time, int end_time, 
			int status, String description, int event_id, int extra_id, String title, int type, int course_id, 
			String course_name, String course_code, int course_time_id, int is_biweekly, int day_of_week, 
			String color, String icon, int epoch_start_day, int epoch_end_day, String date, int daySelected, 
			int month, int year) {
		// TODO Auto-generated constructor stub
		this.user_id = user_id;
		this.start_time_text = start_time_text;
		this.end_time_text = end_time_text;
		this.check_status = check_status;
		this.location = location;
		this.latitude = latitude;
		this.longitude = longitude;
		this.alert_time = alert_time;
		this.start_time = start_time;
		this.end_time = end_time;
		this.status = status;
		this.description = description;
		this.event_id = event_id;
		this.extra_id = extra_id;
		this.title = title;
		this.type = type;
		this.course_id = course_id;
		this.course_name = course_name;
		this.course_code = course_code;
		this.course_time_id = course_time_id;
		this.is_biweekly = is_biweekly;
		this.day_of_week = day_of_week;
		this.color = color;
		this.icon = icon;
		this.epoch_start_day = epoch_start_day;
		this.epoch_end_day = epoch_end_day;
		this.date = date;
		this.daySelected = daySelected;
		this.month = month;
		this.year = year;
	}
	
}
