package events;

import android.graphics.Bitmap;

public class EventsEventsModel {
	public String event_id;
	public String title;
	public String description;
	public String store_id;
	public String store_logo;
	public String end_time;
	public String start_time;
	public Bitmap store_bitmap;
	public int user_like;
	public String image_thumb;
	public String image;
	public int attends;
	public int user_attend;
	public boolean is_featured;

	public EventsEventsModel(String event_id, String title, String description, String store_id, String store_logo, String end_time, String start_time, Bitmap store_bitmap, int user_like, String image, String image_thumb, int attends, int user_attend, boolean is_featured) {
		// TODO Auto-generated constructor stub
		this.event_id = event_id;
		this.title = title;
		this.description = description;
		this.store_id = store_id;
		this.store_logo = store_logo;
		this.end_time = end_time;
		this.start_time = start_time;
		this.store_bitmap = store_bitmap;
		this.user_like = user_like;
		this.image = image;
		this.image_thumb = image_thumb;
		this.attends = attends;
		this.user_attend = user_attend;
		this.is_featured = is_featured;
	}
	
}
