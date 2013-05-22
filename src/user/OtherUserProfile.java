package user;

import org.json.JSONException;
import org.json.JSONObject;

public class OtherUserProfile extends NearbyStudent{
	

	int profileView, dealsUploaded, loyaltyCardsTotal, flagCount,
			dealsUsed, dealsViewed, storesViewed, photoShared, eventsViewed,
			 likeRank, photoRank, profile, relationship, point;

	String  birthdate;
	boolean isOnNearby;
	
	public OtherUserProfile() {
		
	}
	
	@Override
	public void parse(JSONObject profile){
		super.parse(profile);
		
		
		try {
			relationship = profile.getInt("relationship_status");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			photoRank = profile.getInt("photo_share_rank");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			likeRank = profile.getInt("like_rank");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		

	
		try {
			profileView = profile.getInt("profile_views");
		} catch (JSONException e14) {
			e14.printStackTrace();
		}
		
		try {
			birthdate = profile.getString("birthdate");
		} catch (JSONException e10) {
			e10.printStackTrace();
		}
		try {
			dealsUsed = profile.getInt("deals_used");
		} catch (JSONException e9) {
			e9.printStackTrace();
		}
		
		try {
			dealsViewed = profile.getInt("deals_viewed");
		} catch (JSONException e7) {
			e7.printStackTrace();
		}
		
		
		try {
			storesViewed = profile.getInt("stores_viewed");
		} catch (JSONException e4) {
			e4.printStackTrace();
		}
		
		try {
			eventsViewed = profile.getInt("events_viewed");
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		
		try {
			photoShared = profile.getInt("photos_shared");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			this.dealsUploaded = profile.getInt("deals_uploaded");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			this.loyaltyCardsTotal = profile.getInt("loyalty_cards_total");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			point = profile.getInt("points");
		} catch (JSONException e) {
			point = 0;
		}
	}


	public int getLikeRank() {
		return likeRank;
	}

	public void setLikeRank(int likeRank) {
		this.likeRank = likeRank;
	}

	public int getProfileView() {
		return profileView;
	}

	public void setProfileView(int profileView) {
		this.profileView = profileView;
	}



	public int getDealsUploaded() {
		return dealsUploaded;
	}

	public void setDealsUploaded(int dealsUploaded) {
		this.dealsUploaded = dealsUploaded;
	}

	public int getLoyaltyCardsTotal() {
		return loyaltyCardsTotal;
	}

	public void setLoyaltyCardsTotal(int loyaltyCardsTotal) {
		this.loyaltyCardsTotal = loyaltyCardsTotal;
	}

	public int getFlagCount() {
		return flagCount;
	}

	public void setFlagCount(int flagCount) {
		this.flagCount = flagCount;
	}

	public int getDealsUsed() {
		return dealsUsed;
	}

	public void setDealsUsed(int dealsUsed) {
		this.dealsUsed = dealsUsed;
	}

	public int getDealsViewed() {
		return dealsViewed;
	}

	public void setDealsViewed(int dealsViewed) {
		this.dealsViewed = dealsViewed;
	}

	public int getStoresViewed() {
		return storesViewed;
	}

	public void setStoresViewed(int storesViewed) {
		this.storesViewed = storesViewed;
	}

	public int getEventsViewed() {
		return eventsViewed;
	}

	public void setEventsViewed(int eventsViewed) {
		this.eventsViewed = eventsViewed;
	}

	

	public int getPhotoRank() {
		return photoRank;
	}

	public void setPhotoRank(int photoRank) {
		this.photoRank = photoRank;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}


	public int getRelationship() {
		return relationship;
	}

	public void setRelationship(int relationship) {
		this.relationship = relationship;
	}
	
	

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public boolean isOnNearby() {
		return isOnNearby;
	}

	public void setOnNearby(boolean isOnNearby) {
		this.isOnNearby = isOnNearby;
	}

	public int getPhotoShared() {
		return photoShared;
	}

	public void setPhotoShared(int photoShared) {
		this.photoShared = photoShared;
	}


}
