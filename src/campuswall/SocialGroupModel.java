package campuswall;

import datastorage.ImageLoader;
import android.content.Context;
import android.graphics.Bitmap;

public class SocialGroupModel {
	public int id;
	public String name;
	public int num_unread_threads;
	public int group_type;
	public boolean is_read_only, allow_thread_user_search, is_member;
	

	public SocialGroupModel(int id, String name, int num_unread_threads, int group_type, boolean is_read_only,
			boolean allow_thread_user_search, boolean is_member) {
		this.id = id;
		this.name = name;
		this.num_unread_threads = num_unread_threads;
		this.group_type = group_type;
		this.is_read_only = is_read_only;
		this.allow_thread_user_search = allow_thread_user_search;
		this.is_member = is_member;
	}

}
