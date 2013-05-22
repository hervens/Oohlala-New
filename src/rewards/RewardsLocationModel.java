package rewards;

public class RewardsLocationModel {
	public int hotspot_id;
	public String name;
	public double latitude;
	public double longitude;
	
	public RewardsLocationModel(int hotspot_id, String name, double latitude, double longitude) {
		this.hotspot_id = hotspot_id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
}
