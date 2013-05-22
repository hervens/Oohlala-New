package datastorage;

import android.content.Context;
import android.util.DisplayMetrics;

public class ConvertDpsToPixels {
	
	public static int getPixels(int dps, Context c){
		final float scale = c.getResources().getDisplayMetrics().density;
		int pixels = (int) (dps * scale + 0.5f);
		
		return pixels;
	}
	
	public static int getDps(int px, Context c){
	    DisplayMetrics metrics = c.getResources().getDisplayMetrics();
	    int dp = (int) (px / (metrics.densityDpi / 160f));
	    
	    return dp;
	}
		
}
