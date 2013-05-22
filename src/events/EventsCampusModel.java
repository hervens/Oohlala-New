package events;

import android.graphics.Bitmap;

public class EventsCampusModel {
	public String store_name;
	public String franchise_name;
	public String image_url;
	public String franchise_image;
	public String number;
	public String store_id;
	public String descr;
	public double lat;
	public double longi;
	public String address;
	public String phone;
	public String email;
	public String website;
	public int franchise_id;
	public String general_location;
	public String[] open_time;
	public Bitmap image_bitmap;
	public Bitmap franchise_bitmap;

	public EventsCampusModel(String store_name, String franchise_name, String image_url, String franchise_image, String number, String store_id, String descr, double lat, double longi, String address, String phone, String email, String website, int franchise_id, String general_location, String[] open_time, Bitmap image_bitmap, Bitmap franchise_bitmap) {
		// TODO Auto-generated constructor stub
		this.store_name = store_name;
		this.franchise_name = franchise_name;
		this.image_url = image_url;
		this.franchise_image = franchise_image;
		this.number = number;
		this.store_id = store_id;
		this.descr = descr;
		this.lat = lat;
		this.longi = longi;
		this.address = address;
		this.phone = phone;
		this.email = email;
		this.website = website;
		this.franchise_id = franchise_id;
		this.general_location = general_location;
		this.open_time = open_time;
		this.image_bitmap = image_bitmap;
		this.franchise_bitmap = franchise_bitmap;
	}
	
}
