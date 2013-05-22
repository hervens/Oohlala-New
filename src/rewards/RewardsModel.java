package rewards;

import android.graphics.Bitmap;

public class RewardsModel {
	public String name;
	public int category_id;
	public int store_id;
	public String image_url;
	public Bitmap image_bitmap;
	public double lat;
	public double longi;

	public RewardsModel(String name, int category_id, int store_id, String image_url, Bitmap image_bitmap, double lat, double longi) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.category_id = category_id;
		this.store_id = store_id;
		this.image_url = image_url;
		this.image_bitmap = image_bitmap;
		this.lat = lat;
		this.longi = longi;
	}

}
