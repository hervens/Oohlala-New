package rewards;

import android.graphics.Bitmap;

public class RewardsVenuesModel {
	public String name;
	public String itemName;
	public String image_url;
	public String number;
	public String store_id;
	public String descr;
	public double lat;
	public double longi;
	public String address;
	public String phone;
	public String email;
	public String website;
	public Bitmap image_bitmap;
	public boolean user_fav;

	public RewardsVenuesModel(String name, String itemName, String image_url, String number, String store_id, String descr, double lat, double longi, String address, String phone, String email, String website, Bitmap image_bitmap, boolean user_fav) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.itemName = itemName;
		this.image_url = image_url;
		this.number = number;
		this.store_id = store_id;
		this.descr = descr;
		this.lat = lat;
		this.longi = longi;
		this.address = address;
		this.phone = phone;
		this.email = email;
		this.website = website;
		this.image_bitmap = image_bitmap;
		this.user_fav = user_fav;
	}
	
}
