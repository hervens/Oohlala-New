package rewards;

import android.graphics.Bitmap;

public class ExploreModel {
	public int featured_club_id;
	public int franchise_id;
	public int total_item_count;
	public int category_id;
	public String franchise_logo_url;
	public String name;
	public String franchise_name;
	public Bitmap image_bitmap;
	public double lat;
	public double longi;
	public boolean user_fav;
	public String logo_url;
	public String item_preview;

	public ExploreModel(int featured_club_id, int franchise_id, int total_item_count, int category_id, String franchise_logo_url, String name, String franchise_name, Bitmap image_bitmap, double lat, double longi, boolean user_fav, String logo_url, String item_preview) {
		// TODO Auto-generated constructor stub
		this.featured_club_id = featured_club_id;
		this.franchise_id = franchise_id;
		this.total_item_count = total_item_count;
		this.category_id = category_id;
		this.franchise_logo_url = franchise_logo_url;
		this.name = name;
		this.franchise_name = franchise_name;
		this.image_bitmap = image_bitmap;
		this.lat = lat;
		this.longi = longi;
		this.user_fav = user_fav;
		this.logo_url = logo_url;
		this.item_preview = item_preview;
	}

}
