package datastorage;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapByteArray {
	
	public static byte[] getByteArray(Bitmap bmp){
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}
	
	public static Bitmap getBitmap(byte[] bitmapdata){
		return BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata .length);
	}
	
}
