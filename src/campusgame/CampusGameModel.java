package campusgame;

import org.json.JSONArray;
import org.json.JSONObject;

import datastorage.ImageLoader;
import android.content.Context;
import android.graphics.Bitmap;

public class CampusGameModel {
	public int promo_event_id;
	public boolean has_shared;
	public JSONObject shareinfo;
	public String game_image_url, game_icon_url, game_url;
	public String gameName;
	public Bitmap image_bitmap;
	public String game_video_url;
	public int[] winner_id = {0, 0};
	public String[] winner_name = {null, null};
	public String[] prize_name = {null, null};
	public String[] winner_avatar = {null, null};
	public int[] prize_rank = {0, 0};
	public int[] prize_type = {0, 0};
	public int type, game_type, steal_timeout, steal_distance, player_count;
	public int start, end;
	public String game_rules;
	public String description;
	public JSONArray area_boundary_coord_array;
	public int max_submissions, invites_required, user_submissions;

	public CampusGameModel(int promo_event_id, boolean has_shared, JSONObject shareinfo, String game_image_url, 
			String gameName, Bitmap image_bitmap, String game_video_url, int[] winner_id, String[] winner_name, 
			String[] prize_name, String[] winner_avatar, int type, int start, int end, String game_rules, 
			String description, int game_type, String game_icon_url, String game_url, int steal_timeout, 
			int steal_distance, JSONArray area_boundary_coord_array, int player_count, int[] prize_rank, int[] prize_type, 
			int max_submissions, int invites_required, int user_submissions) {
		this.promo_event_id = promo_event_id;
		this.has_shared = has_shared;
		this.shareinfo = shareinfo;
		this.game_image_url = game_image_url;
		this.gameName = gameName;
		this.image_bitmap = image_bitmap;
		this.game_video_url = game_video_url;
		this.winner_id = winner_id;
		this.winner_name = winner_name;
		this.prize_name = prize_name;
		this.winner_avatar = winner_avatar;
		this.type = type;
		this.start = start;
		this.end = end;
		this.game_rules = game_rules;
		this.description = description;
		this.game_type = game_type;
		this.game_icon_url = game_icon_url;
		this.game_url = game_url;
		this.steal_timeout = steal_timeout;
		this.steal_distance = steal_distance;
		this.area_boundary_coord_array = area_boundary_coord_array;
		this.player_count = player_count;
		this.prize_rank = prize_rank;
		this.prize_type = prize_type;
		this.max_submissions = max_submissions;
		this.invites_required = invites_required;
		this.user_submissions = user_submissions;
	}

}
