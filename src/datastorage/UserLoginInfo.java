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

public class UserLoginInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String userAccount;
	public String userPassword;
	public boolean facebookUser;
	
	public UserLoginInfo(String userAccount, String userPassword, boolean facebookUser) {
		this.userAccount = userAccount;
		this.userPassword = userPassword;
		this.facebookUser = facebookUser;
	}
	
}
