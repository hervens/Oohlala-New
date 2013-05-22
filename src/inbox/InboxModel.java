package inbox;

import android.graphics.Bitmap;

public class InboxModel {
	public int num_unread, user_id, inbox_id, friend_request_id, friend_request_user_id;
	public String last_message_time, avatar, first_name, last_name, jid, last_message, time, friend_request_message, friend_request_time_sent;
	public Bitmap thumb_bitmap;
	
	public InboxModel(String last_message_time, int num_unread, String avatar, String first_name, String last_name, int user_id, String jid, int inbox_id, String last_message, String time, Bitmap thumb_bitmap, 
			int friend_request_id, int friend_request_user_id, String friend_request_message, String friend_request_time_sent) {
		this.last_message_time = last_message_time;
		this.num_unread = num_unread;
		this.avatar = avatar;
		this.first_name = first_name;
		this.last_name = last_name;
		this.user_id = user_id;
		this.jid = jid;
		this.inbox_id = inbox_id;
		this.last_message = last_message;
		this.time = time;
		this.thumb_bitmap = thumb_bitmap;
		this.friend_request_id = friend_request_id;
		this.friend_request_user_id = friend_request_user_id;
		this.friend_request_message = friend_request_message;
		this.friend_request_time_sent = friend_request_time_sent;
	}
	
}
