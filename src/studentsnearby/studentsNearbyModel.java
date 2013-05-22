package studentsnearby;

import android.graphics.Bitmap;

public class studentsNearbyModel {
	public String avatar;
	public String first_name;
	public String looking_for;
	public String location_name;
	public String school_name;
	public String jid;
	public String last_name;
	public int user_id;
	public Bitmap thumb_bitmap;
	public boolean has_avatar;
	public boolean has_liked;
	public int profile_likes;
	public int profile_hots;
	public int profile_crushes;
	
	public studentsNearbyModel(String avatar, String first_name, String looking_for, String location_name, String school_name, String jid, String last_name, int user_id, Bitmap thumb_bitmap, boolean has_avatar, boolean has_liked, int profile_likes, int profile_hots, int profile_crushes) {
		this.avatar = avatar;
		this.first_name = first_name;
		this.looking_for = looking_for;
		this.location_name = location_name;
		this.school_name = school_name;
		this.jid = jid;
		this.last_name = last_name;
		this.user_id = user_id;
		this.thumb_bitmap = thumb_bitmap;
		this.has_avatar = has_avatar;
		this.has_liked = has_liked;
		this.profile_likes = profile_likes;
		this.profile_hots = profile_hots;
		this.profile_crushes = profile_crushes;
	}
	
}
