package network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import user.Profile;

public class ErrorCodeParser {

	public static int parser(String data) {
		int code;
		try {
			JSONObject jData = new JSONObject(data);
			code = jData.getInt("code");

		} catch (JSONException e) {
			System.out.println("JSONException in randomFindStoresTest");
			code = -1;
		}
		return code;
	}
	
	public static int parseExpiry(String data) {
		int expiry;
		try {
			expiry = (new JSONObject(data)).getInt("expire_time");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.i("parse expiry error", e.toString());
			expiry = -1;
		}
		return expiry;
	}
	
	public static int parsePoint(String data){
		int message;
		try {
			JSONObject jData = new JSONObject(data);
			message = jData.getInt("points");
			message = message*Profile.multiplier;
		} catch (JSONException e) {
			System.out.println("JSONException in randomFindStoresTest");
			message = 0;
		}
		if (message>0){
			Profile.points+=message;
		}
		return message;
	}

	
	public static int getLastCommentId(String data){
		
		int message;
		try {
			JSONObject jData = new JSONObject(data);
			message = jData.getInt("last_read_comment_id");
			
		} catch (JSONException e) {
			System.out.println("JSONException in randomFindStoresTest");
			message = 0;
		}
		
		return message;	
	}

	
	public static String getSMSMessage(String data){
		String message;
		try {
			JSONObject jData = new JSONObject(data);
			message = jData.getString("sms_message");

		} catch (JSONException e) {
			System.out.println("JSONException in randomFindStoresTest");
			message = "";
		}
		return message;
	}

	
	
	public static String getSchoolURL(String data){
		String url;
		try {
			JSONObject jData = new JSONObject(data);
			url = jData.getString("webmail_url");

		} catch (JSONException e) {
			System.out.println("JSONException in randomFindStoresTest");
			url = "";
		}
		return url;
	}
	
	public static int parserGroupId(String data) {
		int code;
		try {
			JSONObject jData = new JSONObject(data);
			code = jData.getInt("new_group_id");

		} catch (JSONException e) {
			System.out.println("JSONException in randomFindStoresTest");
			code = -1;
		}
		return code;
	}

	public static String parseAvatarLink(String data) {
		String newAvatar;
		try {
			JSONObject jData = new JSONObject(data);
			newAvatar = jData.getString("new_avatar");

		} catch (JSONException e) {
			newAvatar = "";
		}
		return newAvatar;
	}
	
	public static boolean parseIsEmailValid(String data){
		boolean isValid = false;
		try {
			JSONObject jData = new JSONObject(data);
			isValid = jData.getBoolean("is_valid");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isValid;
	}
	
	public static String ParseVer(String data){
		String code;
		try {
			JSONObject jData = new JSONObject(data);
			code = jData.getString("ver");

		} catch (JSONException e) {
			code = "-1";
		}
		return code;
	}

	public static int parseTime(String data) {
		int code;
		try {
			JSONObject jData = new JSONObject(data);
			code = jData.getInt("refresh_time");

		} catch (JSONException e) {
			code = -1;
		}
		return code;
	}

}
