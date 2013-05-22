package profile;

public class SearchCourseModel {
	public int course_id, course_time_id;
	public String course_code, course_name;
	
	public SearchCourseModel(int course_id, String course_name, String course_code, int course_time_id) {
		this.course_id = course_id;
		this.course_name = course_name;
		this.course_code = course_code;
		this.course_time_id = course_time_id;
	}
	
}
