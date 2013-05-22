package discoverMyCampus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Each object represents one point of interest, or marker that belongs to a particular university
 */
public class InterestPoint
{
	String myLat, myLong, myIcon, myContents, myId, myTitle;
	ArrayList<String> myPictures;
	ArrayList<String> myTextPictures;
	ArrayList<String> myVideos;
	
	public InterestPoint(String myLat, String myLong, ArrayList<String> pictures,ArrayList<String> pictureText,ArrayList<String> video, String myContents, String myId, String myTitle)
	{
		this.myLat = myLat;
		this.myLong = myLong;
		this.myPictures = pictures;
		this.myTextPictures = pictureText;
		this.myContents = myContents;
		this.myId = myId;
		this.myTitle = myTitle;
		this.myVideos = video;
	}
	
	//getters and setters
	public String getLatitude() { return myLat; }
	public String getLongtitude() { return myLong; }
	public ArrayList<String> getPictures() { return myPictures; }
	public ArrayList<String> getTextPictures() { return myTextPictures; }
	public ArrayList<String> getVideos() { return myVideos; }
	public String getContents() { return myContents; }
	public String getId() { return myId; }
	public String getTitle() { return myTitle; }
	
	public void storeLocation(String lat, String lo)
	{
		myLat = lat;
		myLong = lo;
	}
	
	
	Bitmap myBitmap = null;
	/**
	 * Returns a bitmap, given a particular url
	 */
	public Bitmap getBitmapFromURL() {
		
		
		Thread thread = new Thread()
		{
			public void run()
			{
				try
				{
					URL url = new URL(myIcon);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			        connection.setDoInput(true);
			        connection.connect();
			        InputStream input = connection.getInputStream();
			        myBitmap = BitmapFactory.decodeStream(input);
				}
				catch (IOException e) {
			        e.printStackTrace();
			        
				}
		}
		};
		
	    thread.start();
	    try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        
	        return myBitmap;
	}
}