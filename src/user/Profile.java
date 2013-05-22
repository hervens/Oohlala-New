package user;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import network.ErrorCodeParser;
import network.RetrieveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import datastorage.Rest;
import datastorage.RestClient;
import datastorage.Rest.request;

import android.os.AsyncTask;
import android.util.Log;

//import social.game.GameInfo;

public class Profile {
	
	
	public static int userId, dateOfRegistration, userPrivilege, dealsViewed,
			dealsUsed, eventsViewed, eventsCheckedIn, loyaltyCardsTotal,
			activeLoyaltyCards, loyaltyCardsRedeemed, dealsUploaded, flagcount,
			voteCount, schoolId, shareCount, status, time, expiry, current_course_time_since,
			photosShared, profileLikes, relationStatus, multiplier, current_course_id,
			post_char_limit, chat_char_limit, num_friends, stores_viewed, current_course_time_left,
			profile_hots, profile_crushes, points, trialTimeLeft, year_of_graduation;
	public static String displayName, email, avatar, gender, birthdate, current_course_code,
			campus_wall_fb_share_url, event_fb_share_url, avatar_thumb_url,
			schoolName, firstName, lastName, xmppPass, jid, pass, profileView,
			locationId, locationName, identifier, friendInviteQrUrl, academic_status, xmpp_host;
	public static double longitude, school_longitude;
	public static double latitude, school_latitude;
	public static String looking_for;
	public static int radius = 20;
	public static int cap = 20;
	public static String sk = "";
	public static String sk2 = "";
	public static String updateCode;
	public static boolean isFirstLogin, has_avatar, is_trial, isFacebook, allow_ratings, post_anonymous, has_update, is_update_mandatory,
					global_notify, campus_group_notify, nearby_online, is_dev;

	public static void dumpUser() {
		userId = 0;
		displayName = "";
		jid = "";
		status = 0;
	}
	
	public static void setAppConfiguration(String data) {
		try {
			xmpp_host = (new JSONObject(data)).getString("xmpp_host");
		} catch (JSONException e) {
		}
		try {
			post_char_limit = (new JSONObject(data)).getInt("post_char_limit");
		} catch (JSONException e1) {
		}
		try {
			chat_char_limit = (new JSONObject(data)).getInt("chat_char_limit");
		} catch (JSONException e) {
		}
		try {
			has_update = (new JSONObject(data)).getBoolean("has_update");
		} catch (JSONException e) {
		}
		try {
			is_update_mandatory = (new JSONObject(data)).getBoolean("is_update_mandatory");
		} catch (JSONException e) {
		}
		try {
			campus_wall_fb_share_url = (new JSONObject(data)).getString("campus_wall_fb_share_url");
		} catch (JSONException e) {
		}
		try {
			event_fb_share_url = (new JSONObject(data)).getString("event_fb_share_url");
		} catch (JSONException e) {
		}
	}

	public static void setSk(String sk) {
		Profile.sk = sk;
	}

	public static int getCap() {
		return cap;
	}

	public static void setCap(int cap) {
		Profile.cap = cap;
	}

	public static String getLooking_for() {
		return looking_for;
	}

	public static double getLongitude() {
		return longitude;
	}

	public static void setLongitude(double longitude) {
		Profile.longitude = longitude;
	}

	public static double getLatitude() {
		return latitude;
	}

	public static void setLatitude(double latitude) {
		Profile.latitude = latitude;
	}

	public static int getRadius() {
		return radius;
	}

	public static void setRadius(int radius) {
		Profile.radius = radius;
	}

	public static boolean isValid() {

		if (is_trial
				|| (userId != 0 && displayName.length() > 0 && jid.length() > 0 && status == 1)) {
			return true;
		} else {
			return false;
		}

	}

	public static void setLooking_for(String lookingFor) {
		looking_for = lookingFor;
	}

	public static int getUserId() {
		return userId;
	}

	public static void setUserId(int userId) {
		Profile.userId = userId;
	}

	public static int getDateOfRegistration() {
		return dateOfRegistration;
	}

	public static void setDateOfRegistration(int dateOfRegistration) {
		Profile.dateOfRegistration = dateOfRegistration;
	}

	public static int getUserPrivilege() {
		return userPrivilege;
	}

	public static void setUserPrivilege(int userPrivilege) {
		Profile.userPrivilege = userPrivilege;
	}

	public static int getDealsViewed() {
		return dealsViewed;
	}

	public static void setDealsViewed(int dealsViewed) {
		Profile.dealsViewed = dealsViewed;
	}

	public static int getDealsUsed() {
		return dealsUsed;
	}

	public static void setDealsUsed(int dealsUsed) {
		Profile.dealsUsed = dealsUsed;
	}

	public static int getEventsViewed() {
		return eventsViewed;
	}

	public static void setEventsViewed(int eventsViewed) {
		Profile.eventsViewed = eventsViewed;
	}

	public static int getEventsCheckedIn() {
		return eventsCheckedIn;
	}

	public static void setEventsCheckedIn(int eventsCheckedIn) {
		Profile.eventsCheckedIn = eventsCheckedIn;
	}

	public static int getLoyaltyCardsTotal() {
		return loyaltyCardsTotal;
	}

	public static void setLoyaltyCardsTotal(int loyaltyCardsTotal) {
		Profile.loyaltyCardsTotal = loyaltyCardsTotal;
	}

	public static int getActiveLoyaltyCards() {
		return activeLoyaltyCards;
	}

	public static void setActiveLoyaltyCards(int activeLoyaltyCards) {
		Profile.activeLoyaltyCards = activeLoyaltyCards;
	}

	public static int getLoyaltyCardsRedeemed() {
		return loyaltyCardsRedeemed;
	}

	public static void setLoyaltyCardsRedeemed(int loyaltyCardsRedeemed) {
		Profile.loyaltyCardsRedeemed = loyaltyCardsRedeemed;
	}

	public static int getDealsUploaded() {
		return dealsUploaded;
	}

	public static void setDealsUploaded(int dealsUploaded) {
		Profile.dealsUploaded = dealsUploaded;
	}

	public static int getFlagcount() {
		return flagcount;
	}

	public static void setFlagcount(int flagcount) {
		Profile.flagcount = flagcount;
	}

	public static int getVoteCount() {
		return voteCount;
	}

	public static void setVoteCount(int voteCount) {
		Profile.voteCount = voteCount;
	}

	public static int getSchoolId() {
		return schoolId;
	}

	public static void setSchoolId(int schoolId) {
		Profile.schoolId = schoolId;
	}

	public static int getShareCount() {
		return shareCount;
	}

	public static void setShareCount(int shareCount) {
		Profile.shareCount = shareCount;
	}

	public static int getStatus() {
		return status;
	}

	public static void setStatus(int status) {
		Profile.status = status;
	}

	public static String getDisplayName() {
		return displayName;
	}

	public static void setDisplayName(String displayName) {
		Profile.displayName = displayName;
	}

	public static String getEmail() {
		return email;
	}

	public static void setEmail(String email) {
		Profile.email = email;
	}

	public static String getAvatar() {
		return avatar;
	}

	public static void setAvatar(String avatar) {
		Profile.avatar = avatar;
	}

	public static String getGender() {
		return gender;
	}

	public static void setGender(String gender) {
		Profile.gender = gender;
	}

	public static String getBirthdate() {
		return birthdate;
	}

	public static void setBirthdate(String birthdate) {
		Profile.birthdate = birthdate;
	}

	public static String getFirstName() {
		return firstName;
	}

	public static void setFirstName(String firstName) {
		Profile.firstName = firstName;
	}

	public static String getLastName() {
		return lastName;
	}

	public static void setLastName(String lastName) {
		Profile.lastName = lastName;
	}

	public static String getXmppPass() {
		return xmppPass;
	}

	public static void setXmppPass(String xmppPass) {
		Profile.xmppPass = xmppPass;
	}

	public static String getJid() {
		return jid;
	}

	public static void setJid(String jid) {
		Profile.jid = jid;
	}

	public static void setKey(String data) {
		try {
			sk = (new JSONObject(data)).getString("id");
		} catch (JSONException e) {
		}
		try {
			time = (new JSONObject(data)).getInt("time");
		} catch (JSONException e1) {
		}
		try {
			expiry = (new JSONObject(data)).getInt("expire_time");
		} catch (JSONException e) {
		}
		try {
			updateCode = (new JSONObject(data)).getString("ver");
		} catch (JSONException e) {
		}
	}
	
	public static void setKey2(String data) {
		try {
			sk2 = (new JSONObject(data)).getString("key");
		} catch (JSONException e) {
		}
		try {
			time = (new JSONObject(data)).getInt("time");
		} catch (JSONException e1) {
		}
		try {
			expiry = (new JSONObject(data)).getInt("expiry");
		} catch (JSONException e) {
		}
		try {
			updateCode = (new JSONObject(data)).getString("ver");
		} catch (JSONException e) {
		}
	}

	public static String getSchoolName() {
		return schoolName;
	}

	public static void setSchoolName(String schoolName) {
		Profile.schoolName = schoolName;
	}
	
	public static String getReadableLeftTime(){
		if (trialTimeLeft<60){
			return trialTimeLeft+" sec";
		}
		else if (trialTimeLeft<3600){
			int min = trialTimeLeft / 60;
		
			return min + " mins";
		} else {
			int hr = trialTimeLeft / 3600;
			int min = trialTimeLeft % 3600;
			min = min / 60;
			return hr + " hours "+ min +" min";
		}

	}
	
	public static void setSchoolLongitude(double school_longitude){
		Profile.school_longitude = school_longitude;
	}
	
	public static void setSchoolLatitude(double school_latitude){
		Profile.school_latitude = school_latitude;
	}

	public static void updateProfile(String data, String pass) {
		try {
			Profile.pass = pass;
			try {
				profileLikes = (new JSONObject(data)).getInt("profile_likes");
			} catch (JSONException e) {
	
			}
			try {
				profile_crushes = (new JSONObject(data)).getInt("profile_crushes");
			} catch (JSONException e) {

			}
			try {
				relationStatus = (new JSONObject(data)).getInt("relationship_status");
			} catch (JSONException e) {

			}
			try {
				profile_hots = (new JSONObject(data)).getInt("profile_hots");
			} catch (JSONException e) {

			}
			try {
				has_avatar = (new JSONObject(data)).getBoolean("has_avatar");
			} catch (JSONException e) {

			}
			try {
				avatar_thumb_url = (new JSONObject(data)).getString("avatar_thumb_url");
			} catch (JSONException e) {

			}
			try {
				isFacebook = (new JSONObject(data)).getBoolean("is_facebook");
			} catch (JSONException e) {

			}
			try {
				allow_ratings = (new JSONObject(data)).getBoolean("allow_ratings");
			} catch (JSONException e) {

			}
			try {
				academic_status = (new JSONObject(data)).getString("academic_status");
			} catch (JSONException e) {

			}
			try {
				year_of_graduation = (new JSONObject(data)).getInt("year_of_graduation");
			} catch (JSONException e) {

			}
			try {
				num_friends = (new JSONObject(data)).getInt("num_friends");
			} catch (JSONException e) {

			}
			try {
				stores_viewed = (new JSONObject(data)).getInt("stores_viewed");
			} catch (JSONException e) {

			}
			try {
				global_notify = (new JSONObject(data)).getBoolean("global_notify");
			} catch (JSONException e) {

			}
			try {
				campus_group_notify = (new JSONObject(data)).getBoolean("campus_group_notify");
			} catch (JSONException e) {

			}
			try {
				nearby_online = (new JSONObject(data)).getBoolean("nearby_online");
			} catch (JSONException e) {

			}
			try {
				is_dev = (new JSONObject(data)).getBoolean("is_dev");
			} catch (JSONException e) {

			}
			try {
				current_course_id = (new JSONObject(data)).getInt("current_course_id");
			} catch (JSONException e) {

			}
			try {
				current_course_time_since = (new JSONObject(data)).getInt("current_course_time_since");
			} catch (JSONException e) {

			}
			try {
				current_course_time_left = (new JSONObject(data)).getInt("current_course_time_left");
			} catch (JSONException e) {

			}
			try {
				current_course_code = (new JSONObject(data)).getString("current_course_code");
			} catch (JSONException e) {

			}
			
			setJid((new JSONObject(data)).getString("jid"));
			setXmppPass((new JSONObject(data)).getString("jid_pass"));
			setUserId((new JSONObject(data)).getInt("id"));
			setDisplayName((new JSONObject(data)).getString("username"));
			setFirstName((new JSONObject(data)).getString("firstname"));
			setLastName((new JSONObject(data)).getString("lastname"));
			setEmail((new JSONObject(data)).getString("email"));
			setAvatar((new JSONObject(data)).getString("avatar_url"));
			setDateOfRegistration((new JSONObject(data)).getInt("date_joined"));
			setGender((new JSONObject(data)).getString("gender"));
			setBirthdate((new JSONObject(data)).getString("birthdate"));
			setDealsViewed((new JSONObject(data)).getInt("deals_viewed"));
			setEventsViewed((new JSONObject(data)).getInt("events_viewed"));
			setSchoolId((new JSONObject(data)).getInt("school_id"));
			setStatus((new JSONObject(data)).getInt("status"));
			setLooking_for((new JSONObject(data)).getString("looking_for"));
			schoolName = (new JSONObject(data)).getString("school_name");
			profileView = (new JSONObject(data)).getString("profile_views");
			photosShared = (new JSONObject(data)).getInt("photos_shared");

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void setProfile(String data, String pass) {
		try {
			Profile.pass = pass;
			try {
				profileLikes = (new JSONObject(data)).getInt("profile_likes");
			} catch (JSONException e) {
	
			}
			try {
				profile_crushes = (new JSONObject(data)).getInt("profile_crushes");
			} catch (JSONException e) {

			}
			try {
				relationStatus = (new JSONObject(data)).getInt("relationship_status");
			} catch (JSONException e) {

			}
			try {
				profile_hots = (new JSONObject(data)).getInt("profile_hots");
			} catch (JSONException e) {

			}
			try {
				has_avatar = (new JSONObject(data)).getBoolean("has_avatar");
			} catch (JSONException e) {

			}
			try {
				avatar_thumb_url = (new JSONObject(data)).getString("avatar_thumb_url");
			} catch (JSONException e) {

			}
			try {
				isFacebook = (new JSONObject(data)).getBoolean("is_facebook");
			} catch (JSONException e) {

			}
			try {
				allow_ratings = (new JSONObject(data)).getBoolean("allow_ratings");
			} catch (JSONException e) {

			}
			try {
				academic_status = (new JSONObject(data)).getString("academic_status");
			} catch (JSONException e) {

			}
			try {
				year_of_graduation = (new JSONObject(data)).getInt("year_of_graduation");
			} catch (JSONException e) {

			}
			try {
				num_friends = (new JSONObject(data)).getInt("num_friends");
			} catch (JSONException e) {

			}
			try {
				stores_viewed = (new JSONObject(data)).getInt("stores_viewed");
			} catch (JSONException e) {

			}
			try {
				global_notify = (new JSONObject(data)).getBoolean("global_notify");
			} catch (JSONException e) {

			}
			try {
				campus_group_notify = (new JSONObject(data)).getBoolean("campus_group_notify");
			} catch (JSONException e) {

			}
			try {
				nearby_online = (new JSONObject(data)).getBoolean("nearby_online");
			} catch (JSONException e) {

			}
			try {
				is_dev = (new JSONObject(data)).getBoolean("is_dev");
			} catch (JSONException e) {

			}
			try {
				current_course_id = (new JSONObject(data)).getInt("current_course_id");
			} catch (JSONException e) {

			}
			try {
				current_course_time_since = (new JSONObject(data)).getInt("current_course_time_since");
			} catch (JSONException e) {

			}
			try {
				current_course_time_left = (new JSONObject(data)).getInt("current_course_time_left");
			} catch (JSONException e) {

			}
			try {
				current_course_code = (new JSONObject(data)).getString("current_course_code");
			} catch (JSONException e) {

			}
			
			setJid((new JSONObject(data)).getString("jid"));
			setXmppPass((new JSONObject(data)).getString("jid_pass"));
			setUserId((new JSONObject(data)).getInt("id"));
			setDisplayName((new JSONObject(data)).getString("username"));
			setFirstName((new JSONObject(data)).getString("firstname"));
			setLastName((new JSONObject(data)).getString("lastname"));
			setEmail((new JSONObject(data)).getString("email"));
			setAvatar((new JSONObject(data)).getString("avatar_url"));
			setDateOfRegistration((new JSONObject(data)).getInt("date_joined"));
			setGender((new JSONObject(data)).getString("gender"));
			setBirthdate((new JSONObject(data)).getString("birthdate"));
			setDealsViewed((new JSONObject(data)).getInt("deals_viewed"));
			setEventsViewed((new JSONObject(data)).getInt("events_viewed"));
			setSchoolId((new JSONObject(data)).getInt("school_id"));
			setStatus((new JSONObject(data)).getInt("status"));
			setLooking_for((new JSONObject(data)).getString("looking_for"));
			schoolName = (new JSONObject(data)).getString("school_name");
			profileView = (new JSONObject(data)).getString("profile_views");
			photosShared = (new JSONObject(data)).getInt("photos_shared");

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void updateUserProfile() {
		new updateProfile().execute();
	}
	
	public static class updateProfile extends AsyncTask<Void, Void, Void> {
    	
		@Override
		protected Void doInBackground(Void... params) {
			RestClient result3 = null;
			try {
				result3 = new request().execute(Rest.USER, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (result3.getResponseCode() == 200) {
				Log.i("update user profile: ", result3.getResponse());
				Profile.updateProfile(result3.getResponse(), Profile.pass);
			}
			
			return null;
		}
		
    }
	
}
