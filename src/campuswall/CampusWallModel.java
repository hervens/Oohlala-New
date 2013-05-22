package campuswall;

import datastorage.ImageLoader;
import android.content.Context;
import android.graphics.Bitmap;

public class CampusWallModel {
	public String avatar_thumb;
	public String display_name;
	public String image_url;
	public String message;
	public String added_time;
	public int comment_count;
	public int likes;
	public int num_unread_comments;
	public String school_thread_id;
	public Bitmap bitmap, thumb_bitmap, RecBitmap;
	public String cut;
	public int user_id;
	public int user_like;
	public boolean first_load;
	public boolean is_anonymous;
	public boolean loaded;
	public boolean thread;
	public String added_time_compare;

	public CampusWallModel(String avatar_thumb, String display_name, String image_url, String message, String added_time, int comment_count, int likes, int num_unread_comments, String school_buzz_id, Bitmap bitmap, Bitmap thumb_bitmap, String cut, int user_id, int user_like, boolean first_load, boolean is_anonymous, boolean loaded, boolean thread, String added_time_compare) {
		this.avatar_thumb = avatar_thumb;
		this.display_name = display_name;
		this.image_url = image_url;
		this.message = message;
		this.added_time = added_time;
		this.comment_count = comment_count;
		this.likes = likes;
		this.num_unread_comments = num_unread_comments;
		this.school_thread_id = school_buzz_id;
		this.bitmap = bitmap;
		this.thumb_bitmap = thumb_bitmap;
		this.cut = cut;
		this.user_id = user_id;
		this.user_like = user_like;
		this.first_load = first_load;
		this.is_anonymous = is_anonymous;
		this.loaded = loaded;
		this.thread = thread;
		this.added_time_compare = added_time_compare;
	}

}
