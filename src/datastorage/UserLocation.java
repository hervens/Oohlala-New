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

public class UserLocation implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public double lat;
	public double longi;
	
	public UserLocation(double lat, double longi) {
		this.lat = lat;
		this.longi = longi;
	}
	
}
