package user;

import org.json.JSONException;
import org.json.JSONObject;

public class University {
	private int schId;
	private String SchName;
	public University(int schId, String schName){
		this.schId = schId;
		this.SchName = schName;
	}

	public University(JSONObject uni){
		try {
			schId = uni.getInt("school_id");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			SchName = uni.getString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getSchId() {
		return schId;
	}

	public String getSchName() {
		return SchName;
	}
}
