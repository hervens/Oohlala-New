package user;

import org.json.JSONException;
import org.json.JSONObject;

public class NearbyStudent {
	private String schoolName, lookingFor, jid, smallAvatar, firstName, lastName,
			gender, locationName, bigAvatar;
	private int userId, schoolId, profileLikes, hot, crush, likeType;
	private double latitude, longitude;
	private boolean isFriend, hasLike, isMale, hasAvatar;

	public NearbyStudent(){

	}
	
	public void parse(JSONObject ns) {
		try {
			hasAvatar = ns.getBoolean("has_avatar");
		} catch (JSONException e) {
		}
		try {
			likeType = ns.getInt("like_type");
		} catch (JSONException e) {
		}
		
		try {
			locationName = ns.getString("location_name");
		} catch (JSONException e) {
			locationName = "";

		}
		try {
			jid = ns.getString("jid");
		} catch (JSONException e) {

		}
		try {
			gender = ns.getString("gender");
			isMale = gender.compareTo("M") == 0;
		} catch (JSONException e1) {
			
		}
		try {
			hot = ns.getInt("profile_hots");
		} catch (JSONException e1) {
			
		}
		try {
			crush = ns.getInt("profile_crushes");
		} catch (JSONException e1) {

		}
		try {
			userId = ns.getInt("user_id");
		} catch (JSONException e1) {

		}
		try {
			hasLike = ns.getBoolean("has_liked");
		} catch (JSONException e) {

		}
		try {
			profileLikes = ns.getInt("profile_likes");
		} catch (JSONException e) {

		}
		try {
			smallAvatar = ns.getString("avatar");
		} catch (JSONException e) {

		}
		try {
			latitude = ns.getDouble("latitude");
		} catch (JSONException e) {

		}
		try {
			longitude = ns.getDouble("longitude");
		} catch (JSONException e) {


		}
		try {
			lookingFor = ns.getString("looking_for");
		} catch (JSONException e) {


		}
		try {
			schoolId = ns.getInt("school_id");
		} catch (JSONException e) {

		
		}
		try {
			schoolName = ns.getString("school_name");
		} catch (JSONException e1) {

			
		}
		try {
			firstName = ns.getString("first_name");
		} catch (JSONException e) {


		}
		try {
			lastName = ns.getString("last_name");
		} catch (JSONException e) {


		}
		try {
			isFriend = ns.getBoolean("is_friend");
		} catch (Exception e) {


		}
		try {
			bigAvatar = ns.getString("avatar_big");
		} catch (Exception e) {

		}
	}
	
	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public boolean isFriend() {
		return isFriend;
	}

	public void setFriend(boolean isFriend) {
		this.isFriend = isFriend;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getLookingFor() {
		return lookingFor;
	}

	public void setLookingFor(String lookingFor) {
		this.lookingFor = lookingFor;
	}

	public int getProfileLikes() {
		return profileLikes;
	}

	public void setProfileLikes(int profileLikes) {
		this.profileLikes = profileLikes;
	}

	public boolean isHasLike() {
		return hasLike;
	}

	public void setHasLike(boolean hasLike) {
		this.hasLike = hasLike;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public boolean isMale() {
		return isMale;
	}

	public void setMale(boolean isMale) {
		this.isMale = isMale;
	}

	public int getHot() {
		return hot;
	}

	public void setHot(int hot) {
		this.hot = hot;
	}

	public int getCrush() {
		return crush;
	}

	public void setCrush(int crush) {
		this.crush = crush;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public String getAvatar() {
		return smallAvatar;
	}

	public void setAvatar(String avatar) {
		this.smallAvatar = avatar;
	}


	public String getBigAvatar() {
		return bigAvatar;
	}

	public void setBigAvatar(String avatar) {
		this.bigAvatar = avatar;
	}
	
	

	
	public int getLikeType() {
		return likeType;
	}

	public void setLikeType(int likeType) {
		this.likeType = likeType;
	}

	public boolean hasAvatar() {
		return hasAvatar;
	}

	public void setHasAvatar(boolean hasAvatar) {
		this.hasAvatar = hasAvatar;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
