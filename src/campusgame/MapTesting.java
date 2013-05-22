package campusgame;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.gotoohlala.R;
import com.gotoohlala.R.id;
import com.gotoohlala.R.layout;

public class MapTesting extends MapActivity{
	
	MapView map;
	MapController controller;
	LocationManager lm;
	String towers;
	int myLat = 10;
	int myLongi = 10;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.gamemap);
		
		map = (MapView) findViewById(R.id.mvGameMap);
		map.setBuiltInZoomControls(true);
		
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//compass = new MyLocationOverlay(GameMap.this, map);
		Criteria crit = new Criteria();
		
		towers = lm.getBestProvider(crit, false);
		Location location = lm.getLastKnownLocation(towers);
		
		if (location != null){
			myLat = (int) (location.getLatitude() *1E6);;
			myLongi = (int) (location.getLongitude() *1E6);;
		}
		
		controller = map.getController();
		GeoPoint startPoint = new GeoPoint(myLat, myLongi);
		controller.setCenter(startPoint);
		controller.setZoom(18);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
