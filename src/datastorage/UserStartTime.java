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

public class UserStartTime implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long startTime = 0;
	
	public UserStartTime(long startTime) {
		this.startTime = startTime;
	}
	
}
