package rewards;

import java.util.List;

import network.GPS;
import network.GPSLocationChanged;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.gotoohlala.R;

import datastorage.CacheInternalStorage;

public class ExploreMap extends MapActivity implements GPSLocationChanged {
	boolean cachedLocationFound = false;
	double myLat, myLongi, myLatCurrent, myLongiCurrent;
	MapView map;
	
	MapController controller;
	
	List<Overlay> overlayList;
	private Drawable drawable;
	
	CustomPinPoint storeLocation;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exploremap);
		
		GPS.act = ExploreMap.this;
		GPS.getMyLocation(ExploreMap.this);
		
		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		}); 
		
		map = (MapView) findViewById(R.id.mvExploreMap);
		drawPinPointOnMap();
		
		if (CacheInternalStorage.getCacheUserLocation(getApplicationContext()) != null){
      		if (CacheInternalStorage.getCacheUserLocation(getApplicationContext()).lat != 0){
      			cachedLocationFound = true;
      			myLatCurrent = CacheInternalStorage.getCacheUserLocation(getApplicationContext()).lat;
    			myLongiCurrent = CacheInternalStorage.getCacheUserLocation(getApplicationContext()).longi;
      			
      			Log.i("cached location 0", "--------------------------------------------");
      						
      		} 
      	} 
		
	}
	
	private void drawPinPointOnMap() {
		// TODO Auto-generated method stub
		map.setBuiltInZoomControls(true);
		
		overlayList = map.getOverlays();
		
		controller = map.getController();
		GeoPoint storePoint = new GeoPoint((int)(myLatCurrent *1E6), (int)(myLongiCurrent *1E6));
		controller.setCenter(storePoint);
		controller.setZoom(16);
		
		drawable = this.getResources().getDrawable(R.drawable.mapmarker);
		
		OverlayItem overlayItem = new OverlayItem(storePoint, "Store Location", "no");
		storeLocation = new CustomPinPoint(drawable, ExploreMap.this);
		storeLocation.insertPinPoint(overlayItem);
		overlayList.add(storeLocation);
	}

	public void locationChanged() {
		// TODO Auto-generated method stub
		if (!cachedLocationFound && (GPS.getGPS || GPS.getServiceProvider)){
			myLatCurrent = GPS.myLat;
			myLongiCurrent = GPS.myLongi;
			
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
