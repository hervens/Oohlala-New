package datastorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import network.RetrieveData;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class ImageCache {
	
	public static void Cache(String imageUrl){
		try {
			URL url = new URL(imageUrl);
			//Next create a file, the example below will save to the SDCARD using JPEG format
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File (sdCard.getAbsolutePath() + "/OohlalaCache");
			dir.mkdirs();
			File file = new File(dir, RetrieveData.hash(imageUrl) + ".png");
			
			//Log.i("image file dir", file.toString());
			//Next create a Bitmap object and download the image to bitmap
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeStream(url.openStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//Finally compress the bitmap, saving to the file previously created
		
				OutputStream outStream = null;
				try {
					outStream = new FileOutputStream(file);
					//Log.i("bitmap save in file dir 1", file.toString());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				bitmap.compress(CompressFormat.PNG, 100, outStream);
				try {
					outStream.flush();
					//Log.i("bitmap save in file dir 2", file.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    try {
					outStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String HashedImageName(String imageUrl){
		return RetrieveData.hash(imageUrl) + ".png";
	}
	
	public static String HashedImageDir(String imageUrl){
		return "sdcard/OohlalaCache/" + RetrieveData.hash(imageUrl) + ".png";
	}
	
	public static Boolean isImageExist(String imageName){
		boolean isExist = false;
		
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File (sdCard.getAbsolutePath() + "/OohlalaCache");
		dir.mkdirs();
		
		File file[] = dir.listFiles();  
		//Log.i("file name", file[1].getName());
		
		for (int i = 0; i < file.length; i++){
			if (file[i].getName().contentEquals(imageName)){
				isExist = true;
				break;
			}
		}
		
		return isExist;
	}
	
}
