package datastorage;

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Typeface;

public class Fonts {
	
	final static Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();
	
	public static Typeface getOpenSansLightItalic(Context ctx){
		synchronized(cache){
			if(!cache.containsKey("fonts/OpenSans-LightItalic.ttf")){
				Typeface t = Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSans-LightItalic.ttf");
				cache.put("fonts/OpenSans-LightItalic.ttf", t);
			}
			return cache.get("fonts/OpenSans-LightItalic.ttf");
		}
	}
	
	public static Typeface getOpenSansBold(Context ctx){
		synchronized(cache){
			if(!cache.containsKey("fonts/OpenSans-Bold.ttf")){
				Typeface t = Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSans-Bold.ttf");
				cache.put("fonts/OpenSans-Bold.ttf", t);
			}
			return cache.get("fonts/OpenSans-Bold.ttf");
		}
	}
	
	public static Typeface getOpenSansBoldItalic(Context ctx){
		synchronized(cache){
			if(!cache.containsKey("fonts/OpenSans-BoldItalic.ttf")){
				Typeface t = Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSans-BoldItalic.ttf");
				cache.put("fonts/OpenSans-BoldItalic.ttf", t);
			}
			return cache.get("fonts/OpenSans-BoldItalic.ttf");
		}
	}
	
	public static Typeface getOpenSansExtraBold(Context ctx){
		synchronized(cache){
			if(!cache.containsKey("fonts/OpenSans-ExtraBold.ttf")){
				Typeface t = Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSans-ExtraBold.ttf");
				cache.put("fonts/OpenSans-ExtraBold.ttf", t);
			}
			return cache.get("fonts/OpenSans-ExtraBold.ttf");
		}
	}
	
	public static Typeface getOpenSansExtraBoldItalic(Context ctx){
		synchronized(cache){
			if(!cache.containsKey("fonts/OpenSans-ExtraBoldItalic.ttf")){
				Typeface t = Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSans-ExtraBoldItalic.ttf");
				cache.put("fonts/OpenSans-ExtraBoldItalic.ttf", t);
			}
			return cache.get("fonts/OpenSans-ExtraBoldItalic.ttf");
		}
	}
	
	public static Typeface getOpenSansItalic(Context ctx){
		synchronized(cache){
			if(!cache.containsKey("fonts/OpenSans-Italic.ttf")){
				Typeface t = Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSans-Italic.ttf");
				cache.put("fonts/OpenSans-Italic.ttf", t);
			}
			return cache.get("fonts/OpenSans-Italic.ttf");
		}
	}
	
	public static Typeface getOpenSansLight(Context ctx){
		synchronized(cache){
			if(!cache.containsKey("fonts/OpenSans-Light.ttf")){
				Typeface t = Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSans-Light.ttf");
				cache.put("fonts/OpenSans-Light.ttf", t);
			}
			return cache.get("fonts/OpenSans-Light.ttf");
		}
	}
	
	public static Typeface getOpenSansRegular(Context ctx){
		synchronized(cache){
			if(!cache.containsKey("fonts/OpenSans-Regular.ttf")){
				Typeface t = Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSans-Regular.ttf");
				cache.put("fonts/OpenSans-Regular.ttf", t);
			}
			return cache.get("fonts/OpenSans-Regular.ttf");
		}
	}
	
	public static Typeface getOpenSansSemibold(Context ctx){
		synchronized(cache){
			if(!cache.containsKey("fonts/OpenSans-Semibold.ttf")){
				Typeface t = Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSans-Semibold.ttf");
				cache.put("fonts/OpenSans-Semibold.ttf", t);
			}
			return cache.get("fonts/OpenSans-Semibold.ttf");
		}
	}
	
	public static Typeface getOpenSansSemiboldItalic(Context ctx){
		synchronized(cache){
			if(!cache.containsKey("fonts/OpenSans-SemiboldItalic.ttf")){
				Typeface t = Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSans-SemiboldItalic.ttf");
				cache.put("fonts/OpenSans-SemiboldItalic.ttf", t);
			}
			return cache.get("fonts/OpenSans-SemiboldItalic.ttf");
		}
	}
	
}
