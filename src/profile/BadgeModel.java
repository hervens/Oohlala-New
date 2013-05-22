package profile;

public class BadgeModel {
	public int id;
	public String name, badge_icon_thumb_url, badge_icon_url;
	
	public BadgeModel(int id, String name, String badge_icon_thumb_url, String badge_icon_url) {
		this.id = id;
		this.name = name;
		this.badge_icon_thumb_url = badge_icon_thumb_url;
		this.badge_icon_url = badge_icon_url;
	}
	
}
