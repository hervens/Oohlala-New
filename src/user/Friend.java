package user;

import org.json.JSONException;
import org.json.JSONObject;

public class Friend {
	private String lastName, schoolName,  displayName, lookingFor, firstName, gender, birthdate, jid, avatar, email, lastMsg, locationName;
	private int userPrivilege, userId, schoolId, inboxId, lastMessageTime, numUnread, createTime;
	private boolean select = false;
	private boolean lastMsgHasImg;
	public Friend(JSONObject fd){
		
		try {
			this.setLocationName(fd.getString("user_location_name"));
		} catch (JSONException e15) {
			
		}
		try {
			setLastMessageTime(fd.getInt("last_message_time"));
		} catch (JSONException e15) {
			// TODO Auto-generated catch block
			e15.printStackTrace();
		}
		try {
			setNumUnread(fd.getInt("num_unread"));
		} catch (JSONException e14) {
			// TODO Auto-generated catch block
			e14.printStackTrace();
		}
		try {
			setCreateTime(fd.getInt("created_time"));
		} catch (JSONException e13) {
			// TODO Auto-generated catch block
			e13.printStackTrace();
		}
		try {
			setLastMsg(fd.getString("last_message"));
		} catch (JSONException e12) {
			// TODO Auto-generated catch block
			e12.printStackTrace();
		}
		try {
			setLastMsgHasImg(fd.getBoolean("last_message_has_image"));
		} catch (JSONException e9) {
			// TODO Auto-generated catch block
			e9.printStackTrace();
		}
		try {
			setInboxId(fd.getInt("inbox_id"));
		} catch (JSONException e11) {
			// TODO Auto-generated catch block
			e11.printStackTrace();
		}
		try {
			setAvatar(fd.getString("avatar"));
		} catch (JSONException e10) {
			// TODO Auto-generated catch block
			e10.printStackTrace();
		}
//		try {
//			setEmail(fd.getString("email"));
//		} catch (JSONException e9) {
//			// TODO Auto-generated catch block
//			e9.printStackTrace();
//		}
		try {
			setFirstName(fd.getString("first_name"));
		} catch (JSONException e8) {
			// TODO Auto-generated catch block
			e8.printStackTrace();
		}
		try {
			setGender(fd.getString("gender"));
		} catch (JSONException e7) {
			// TODO Auto-generated catch block
			e7.printStackTrace();
		}
		try {
			setJid(fd.getString("jid"));
		} catch (JSONException e6) {
			// TODO Auto-generated catch block
			e6.printStackTrace();
		}
		try {
			setLastName(fd.getString("last_name"));
		} catch (JSONException e5) {
			// TODO Auto-generated catch block
			e5.printStackTrace();
		}
		try {
			setLookingFor(fd.getString("looking_for"));
		} catch (JSONException e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		}
		try {
			setSchoolId(fd.getInt("school_id"));
		} catch (JSONException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		try {
			setSchoolName(fd.getString("school_name"));
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			setUserId(fd.getInt("user_id"));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			setUserPrivilege(fd.getInt("user_privilege"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public String getLastMsg() {
		return lastMsg;
	}

	public void setLastMsg(String lastMsg) {
		this.lastMsg = lastMsg;
	}

	public int getLastMessageTime() {
		return lastMessageTime;
	}

	public void setLastMessageTime(int lastMessageTime) {
		this.lastMessageTime = lastMessageTime;
	}

	public int getNumUnread() {
		return numUnread;
	}

	public void setNumUnread(int numUnread) {
		this.numUnread = numUnread;
	}

	public int getCreateTime() {
		return createTime;
	}

	public void setCreateTime(int createTime) {
		this.createTime = createTime;
	}

	public boolean isLastMsgHasImg() {
		return lastMsgHasImg;
	}

	public void setLastMsgHasImg(boolean lastMsgHasImg) {
		this.lastMsgHasImg = lastMsgHasImg;
	}

	public int getInboxId() {
		return inboxId;
	}

	public void setInboxId(int inboxId) {
		this.inboxId = inboxId;
	}

	public boolean isSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
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
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getLookingFor() {
		return lookingFor;
	}
	public void setLookingFor(String lookingFor) {
		this.lookingFor = lookingFor;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getBirthdate() {
		return birthdate;
	}
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}
	public String getJid() {
		return jid;
	}
	public void setJid(String jid) {
		this.jid = jid;
	}
	public int getUserPrivilege() {
		return userPrivilege;
	}
	public void setUserPrivilege(int userPrivilege) {
		this.userPrivilege = userPrivilege;
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
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	
	
}
