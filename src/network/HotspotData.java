package network;

import org.json.JSONException;
import org.json.JSONObject;

public class HotspotData {
	
//	[{"hotspot_id": 1, "name": "Toronto", "city": "Toronto",
//		"country": "Canada", "longitude": -79.386600000000001, "latitude": 43.670400000000001}

	private int hotspotId;
	private String name, city, country;
	private double longitude, latitude;
	
	public HotspotData(){
		
	}
	public HotspotData(JSONObject hotspot){
		try {
			hotspotId = hotspot.getInt("hotspot_id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			name = hotspot.getString("name");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			city = hotspot.getString("city");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			country = hotspot.getString("country");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			longitude = hotspot.getDouble("longitude");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			latitude = hotspot.getDouble("latitude");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		
	}
	public int getHotspotId() {
		return hotspotId;
	}
	public void setHotspotId(int hotspotId) {
		this.hotspotId = hotspotId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	
}
