package campusmap;

public class CampusMapModel {
	public int id;
	public String name, short_name, address;
	public double longitude;
	public double latitude;
	

	public CampusMapModel(int id, String name, String short_name, double longitude, double latitude, String address) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.name = name;
		this.short_name = short_name;
		this.longitude = longitude;
		this.latitude = latitude;
		this.address = address;
	}

}
