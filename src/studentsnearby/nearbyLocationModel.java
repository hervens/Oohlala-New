package studentsnearby;

public class nearbyLocationModel {
	public int location_id;
	public String name;
	public double latitude;
	public double longitude;
	
	public nearbyLocationModel(int location_id, String name, double latitude, double longitude) {
		this.location_id = location_id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
}
