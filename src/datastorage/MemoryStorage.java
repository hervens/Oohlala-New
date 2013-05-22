package datastorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.lang.OutOfMemoryError;

public class MemoryStorage {
		
	public static Map<String, byte[]> dictionary = new TreeMap<String, byte[]>();
	public static int NUM_OF_IMAGES = 10;
	
	public static void insert(String key, String imageUrl){
		if (dictionary.size() > NUM_OF_IMAGES){
			removeFirst();
		}
		
		URL url = null;
		try {
			url = new URL(imageUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(url.openStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		if (bitmap != null){
			bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos); 
		}
		byte[] bitmapdata = bos.toByteArray();
		dictionary.put(key, bitmapdata);
		//Log.i("inserted the image into memory", key);
	}
	
	public static void remove(String key){
		dictionary.remove(key);
	}
	
	public static Bitmap returnImage(String key){
		byte[] image = dictionary.get(key);
		Bitmap bitmap = BitmapFactory.decodeByteArray(image , 0, image.length);
		//Log.i("returned the image", key);
		return bitmap;
	}
	
	public static int size(){
		return dictionary.size();
	}
	
	public static boolean findKey(String key){
		boolean hasKey = false;
		for (Map.Entry<String, byte[]> entry : dictionary.entrySet()) {
		    if(key.contentEquals(entry.getKey())){
		    	hasKey = true;
		    	break;
		    }
		}
		//Log.i("has key", String.valueOf(hasKey));
		return hasKey;
	}
	
	public static void removeFirst(){
		String firstKey = null;
		for (Map.Entry<String, byte[]> entry : dictionary.entrySet()) {
		    firstKey = entry.getKey();
		    break;
		}
		Log.i("remove first memory key", firstKey);
		dictionary.remove(firstKey);
	}
	
	public static void removeAll(){
		dictionary.clear();
	}

}
