package profile;

public class BlockListModel {
	public int user_id;
	public String avatar, first_name, last_name;
	
	public BlockListModel(String avatar, String first_name, String last_name, int user_id) {
		this.avatar = avatar;
		this.first_name = first_name;
		this.last_name = last_name;
		this.user_id = user_id;
	}
	
}
