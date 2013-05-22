package datastorage;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DeviceDimensions {
	
	/**
     * get the height of the phone screen in pixels
     */
	public static int getHeight(Context ctx){
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		int height = metrics.heightPixels;
		
		return height;
	}
	
	/**
     * get the width of the phone screen in pixels
     */
	public static int getWidth(Context ctx){
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		
		return width;
	}	
	
}
