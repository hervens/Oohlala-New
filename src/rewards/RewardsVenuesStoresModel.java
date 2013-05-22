package rewards;

import android.content.Context;
import android.graphics.Bitmap;

import com.gotoohlala.R;

import datastorage.TimeCounter;

public class RewardsVenuesStoresModel {
	public String title;
	public String description;
	public String bottom;
	public int expiration;
	public String deal_id;
	public String code;
	public int type;
	public String event_id;
	public String start_time;
	public String end_time;
	public String expirationTime;
	public String card_id;
	public int user_points;
	public int points;
	public String ref_code;
	public Context c;
	public int user_like;
	public String image;
	public String image_thumb;
	public Bitmap bitmap;
	public int attends;
	public int user_attend;
	public boolean is_featured;
	public String store_id;
	public String store_logo;

	public RewardsVenuesStoresModel(String title, String description, String bottom, String deal_id, String code, int type, String event_id, String start_time, String end_time, int expiration, String card_id, int user_points, int points, String ref_code, Context c, int user_like, String image, String image_thumb, Bitmap bitmap, int attends, int user_attend, boolean is_featured, String store_id, String store_logo) {
		// TODO Auto-generated constructor stub
		this.c = c;
		this.title = title;
		this.description = description;
		if (type == 0){
			if (expiration == -1){
				this.bottom = c.getString(R.string.Ongoing);
			} else {
				int remain = expiration - (int)(System.currentTimeMillis()/1000);
				int days = remain/86400;
				this.bottom = days + c.getString(R.string._days_remaining);
			}
		} else if (type == 1){
			this.bottom = TimeCounter.getTime(bottom);
		} else if (type == 2){
			this.bottom = bottom + c.getString(R.string._stamped);
		}
		this.deal_id = deal_id;
		this.code = code;
		this.type = type;
		this.event_id = event_id;
		this.start_time = start_time;
		this.end_time = end_time;
		this.expiration = expiration;
		
		if (expiration == -1){
			this.expirationTime = c.getString(R.string.Ongoing);
		} else {
			int remain = expiration - (int)(System.currentTimeMillis()/1000);
			int days = remain/86400;
			this.expirationTime = days + c.getString(R.string._days_remaining);
		}
		
		this.card_id = card_id;
		this.user_points = user_points;
		this.points = points;
		this.ref_code = ref_code;
		this.user_like = user_like;
		this.image = image;
		this.image_thumb = image_thumb;
		this.bitmap = bitmap;
		
		this.attends = attends;
		this.user_attend = user_attend;
		this.is_featured = is_featured;
		this.store_id = store_id;
		this.store_logo = store_logo;
	}
	
}
