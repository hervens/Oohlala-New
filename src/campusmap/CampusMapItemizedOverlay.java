package campusmap;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class CampusMapItemizedOverlay extends ItemizedOverlay<OverlayItem>{
	    //member variables
	    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	    private Context mContext;
	    private int mTextSize;
	    private Paint mInnerPaint, mBorderPaint, mTextPaint;

	    public CampusMapItemizedOverlay(Drawable defaultMarker, Context context, int textSize)
	    {
	        super(boundCenterBottom(defaultMarker));
	        mContext = context;
	        mTextSize = textSize;
	    }


	    //In order for the populate() method to read each OverlayItem, it will make a request to createItem(int)
	    // define this method to properly read from our ArrayList
	    @Override
	    protected OverlayItem createItem(int i)
	    {
	        return mOverlays.get(i);
	    }


	    @Override
	    public int size()
	    {
	        return mOverlays.size();
	    }

	    /*
	    @Override
	    protected boolean onTap(int index)
	    {
	        OverlayItem item = mOverlays.get(index);

	        //Do stuff here when you tap, i.e. :
	        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	        dialog.setTitle(item.getTitle());
	        dialog.setMessage(item.getSnippet());
	        dialog.show();

	        //return true to indicate we've taken care of it
	        return true;
	    }
		*/
	    
	    @Override
	    public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow)
	    {
	        super.draw(canvas, mapView, shadow);
	        drawInfoWindow(canvas, mapView, shadow);
	        if (shadow == false)
	        {
	            //cycle through all overlays
	            for (int index = 0; index < mOverlays.size(); index++)
	            {
	                OverlayItem item = mOverlays.get(index);

	                // Converts lat/lng-Point to coordinates on the screen
	                GeoPoint point = item.getPoint();
	                Point ptScreenCoord = new Point() ;
	                mapView.getProjection().toPixels(point, ptScreenCoord);
	                
	                /*
	                //Paint
	                Paint paint = new Paint();
	                paint.setTextAlign(Paint.Align.CENTER);
	                paint.setStyle(Style.FILL);
	                paint.setTextSize(mTextSize);
	                paint.setARGB(150, 0, 0, 0); // alpha, r, g, b (Black, semi see-through)

	                //show text to the right of the icon
	                canvas.drawText(item.getTitle(), ptScreenCoord.x, ptScreenCoord.y+mTextSize, paint);
	                */
	            }
	        }
	    }
	    
	    private void drawInfoWindow(Canvas canvas, MapView	mapView, boolean shadow) {
	    	
	    	if (shadow == false){
	    		//cycle through all overlays
	            for (int index = 0; index < mOverlays.size(); index++) {
	                OverlayItem item = mOverlays.get(index);
					//  First we need to determine the screen coordinates of the selected MapLocation
	                GeoPoint point = item.getPoint();
	                Point ptScreenCoord = new Point() ;
					mapView.getProjection().toPixels(point, ptScreenCoord);
			    	
			    	//  Setup the info window
					int INFO_WINDOW_WIDTH = item.getTitle().length()*11;
					int INFO_WINDOW_HEIGHT = 40;
					RectF infoWindowRect = new RectF(0,0,INFO_WINDOW_WIDTH,INFO_WINDOW_HEIGHT);				
					int infoWindowOffsetX = ptScreenCoord.x-INFO_WINDOW_WIDTH/2;
					int infoWindowOffsetY = ptScreenCoord.y-INFO_WINDOW_HEIGHT-25;
					infoWindowRect.offset(infoWindowOffsetX,infoWindowOffsetY);

					//  Drawing the inner info window
					canvas.drawRoundRect(infoWindowRect, 5, 5, getmInnerPaint());
					
					//  Drawing the border for info window
					canvas.drawRoundRect(infoWindowRect, 5, 5, getmBorderPaint());
						
					//  Draw the MapLocation's name
					int TEXT_OFFSET_X = 15;
					int TEXT_OFFSET_Y = 25;
					canvas.drawText(item.getTitle(),infoWindowOffsetX+TEXT_OFFSET_X,infoWindowOffsetY+TEXT_OFFSET_Y,getmTextPaint());
	            }
	    	}
	    }
	    
	    public Paint getmInnerPaint() {
			if ( mInnerPaint == null) {
				mInnerPaint = new Paint();
				mInnerPaint.setARGB(225, 50, 50, 50); //inner color
				mInnerPaint.setAntiAlias(true);
			}
			return mInnerPaint;
		}

		public Paint getmBorderPaint() {
			if ( mBorderPaint == null) {
				mBorderPaint = new Paint();
				mBorderPaint.setARGB(255, 255, 255, 255);
				mBorderPaint.setAntiAlias(true);
				mBorderPaint.setStyle(Style.STROKE);
				mBorderPaint.setStrokeWidth(2);
			}
			return mBorderPaint;
		}

		public Paint getmTextPaint() {
			if ( mTextPaint == null) {
				mTextPaint = new Paint();
				//mTextPaint.setTextAlign(Paint.Align.CENTER);
				mTextPaint.setARGB(255, 255, 255, 255);
				mTextPaint.setTextSize(mTextSize);
				mTextPaint.setAntiAlias(true);
			}
			return mTextPaint;
		}

	    public void addOverlay(OverlayItem overlay)
	    {
	        mOverlays.add(overlay);
	        populate();
	    }


	    public void removeOverlay(OverlayItem overlay)
	    {
	        mOverlays.remove(overlay);
	        populate();
	    }


	    public void clear()
	    {
	        mOverlays.clear();
	        populate();
	    }

	
}
