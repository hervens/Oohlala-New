package campusgame;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.gotoohlala.R;

import datastorage.DrawableToBitmap;

public class CustomPinImage extends Overlay {

	public Context c;
	public Drawable marker, compare;
	public GeoPoint geoPoint;
	
	public CustomPinImage(Drawable m, Drawable comp, GeoPoint gP, Context context) {
		c = context;
		marker = m;
		compare = comp;
		geoPoint = gP;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {                             
	        super.draw(canvas, mapView, shadow);
	 
	        if (!shadow) {                                                                          
	            Point point = new Point();
	            mapView.getProjection().toPixels(geoPoint, point);                                  
	            
	            if (marker != null){
		            Bitmap bmp = DrawableToBitmap.drawableToBitmap(marker, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());   
		            int x = point.x - bmp.getWidth()/2;                                          
		            int y = point.y - bmp.getHeight() - compare.getIntrinsicHeight()/2;                                                    
		 
		            canvas.drawBitmap(bmp, x, y, null);     
	            }
	        }
	 
	}
	 

}
