package datastorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import network.RetrieveData;
import android.content.Context;
import android.content.ContextWrapper;

public class UserFirstLogin implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String userAccount;
	public boolean first_time;
	public boolean first_time_social_tab;
	public boolean first_time_campuswall_tab;
	
	public UserFirstLogin(String userAccount, boolean first_time, boolean first_time_social_tab, boolean first_time_campuswall_tab) {
		this.userAccount = userAccount;
		this.first_time = first_time;
		this.first_time_social_tab = first_time_social_tab;
		this.first_time_campuswall_tab = first_time_campuswall_tab;
	}
	
}
