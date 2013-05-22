package campusgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.location.Geocoder;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class PriceCircle extends Overlay {

		private GeoPoint geoPoint;
		private int radius;

	    public PriceCircle(GeoPoint geoPoint, int r) {
	    	this.geoPoint = geoPoint;
	    	radius = r;
	    }

	    @Override
	    public void draw(Canvas canvas, MapView mapV, boolean shadow){

	        if(shadow){
	            Projection projection = mapV.getProjection();
	            Point pt = new Point();
	            projection.toPixels(geoPoint,pt);
	            
	            float circleRadius = projection.metersToEquatorPixels(radius);

	            Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	            circlePaint.setColor(0x30000000);
	            circlePaint.setStyle(Style.FILL_AND_STROKE);
	            canvas.drawCircle((float)pt.x, (float)pt.y, circleRadius, circlePaint);

	            circlePaint.setColor(0x99000000);
	            circlePaint.setStyle(Style.STROKE);
	            canvas.drawCircle((float)pt.x, (float)pt.y, circleRadius, circlePaint);

	            super.draw(canvas,mapV,shadow);
	        }
	    }
	
}
