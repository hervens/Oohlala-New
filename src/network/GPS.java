package network;

import com.gotoohlala.R;
import com.gotoohlala.UnreadNumCheck;

import datastorage.CacheInternalStorage;
import datastorage.UserLocation;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GPS {
	
	public static LocationManager mlocManager;
	public static LocationManager serviceLocManager;
	public static LocationListener mlocListener;
	public static double myLat;
	public static double myLongi;
	static Context context;
	public static boolean getGPS = false;
	public static GPSLocationChanged act;
	public static String towers;
	public static boolean getServiceProvider = false;
	
	public static void getMyLocation(Context c) {
		// TODO Auto-generated method stub
		context = c;
		mlocManager = (LocationManager)c.getSystemService(Context.LOCATION_SERVICE);
		serviceLocManager = (LocationManager)c.getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener();
		
		ServiceProvider();
		
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
		//mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
	}
	
	private static void ServiceProvider() {
		// TODO Auto-generated method stub
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_COARSE);
		
		towers = serviceLocManager.getBestProvider(crit, false);
		Location loc = serviceLocManager.getLastKnownLocation(towers);
		
		if (loc != null){
			myLat = loc.getLatitude();
			myLongi = loc.getLongitude();
			CacheInternalStorage.cacheUserLocation(new UserLocation(myLat, myLongi), context); //cache user info
			
			Log.i("my location Lat", String.valueOf(myLat));
			Log.i("my location Longi", String.valueOf(myLongi));
			
			getServiceProvider = true;
			act.locationChanged();	
		} 
		
		/*
		LocationProvider low=
				  mlocManager.getProvider(mlocManager.getBestProvider(createCoarseCriteria(), true));
		
		mlocManager.requestLocationUpdates(low.getName(), 0, 0f, mlocListener);
		*/
	}
	
	public static class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
			if (!getGPS){
				myLat = loc.getLatitude();
				myLongi = loc.getLongitude();
				CacheInternalStorage.cacheUserLocation(new UserLocation(myLat, myLongi), context); //cache user info
				
				String Text = "My current location is: " + "Latitud = " + loc.getLatitude() + "Longitud = " + loc.getLongitude();
				//Toast.makeText(context, Text, Toast.LENGTH_SHORT).show();
				
				getGPS = true;
				act.locationChanged();
				
				stopGPS();
			}
		}
	
	
		public void onProviderDisabled(String provider){
			Toast.makeText(context, context.getString(R.string.Gps_disabled_please_turn_it_on), Toast.LENGTH_SHORT).show();
			getGPS = false;
		}
	
	
		public void onProviderEnabled(String provider){
			//Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
		}
	
	
		public void onStatusChanged(String provider, int status, Bundle extras){
		}
	}
	
	public static void restartGPS() {
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
	}
	
	public static void stopGPS() {
		mlocManager.removeUpdates(mlocListener);
		Log.i("gps stop", "--------------------------- gps stop --------------------------");
	}
	
	/** this criteria will settle for less accuracy, high power, and cost */
	public static Criteria createCoarseCriteria() {
	 
	  Criteria c = new Criteria();
	  c.setAccuracy(Criteria.ACCURACY_COARSE);
	  c.setAltitudeRequired(false);
	  c.setBearingRequired(false);
	  c.setSpeedRequired(false);
	  c.setCostAllowed(true);
	  c.setPowerRequirement(Criteria.POWER_HIGH);
	  return c;
	 
	}
	 
	/** this criteria needs high accuracy, high power, and cost */
	public static Criteria createFineCriteria() {
	 
	  Criteria c = new Criteria();
	  c.setAccuracy(Criteria.ACCURACY_FINE);
	  c.setAltitudeRequired(false);
	  c.setBearingRequired(false);
	  c.setSpeedRequired(false);
	  c.setCostAllowed(true);
	  c.setPowerRequirement(Criteria.POWER_HIGH);
	  return c;
	 
	}
	 
	/** 
	  make sure to call this in the main thread, not a background thread
	  make sure to call locMgr.removeUpdates(...) when you are done
	*/
	public static void init(){
	 
	  //LocationManager locMgr = LocationUtils.getLocationManager(ctx.getMyContext());
	 
	  // get low accuracy provider
	  LocationProvider low=
			  mlocManager.getProvider(mlocManager.getBestProvider(createCoarseCriteria(), true));
	 
	  // get high accuracy provider
	  LocationProvider high=
			  mlocManager.getProvider(mlocManager.getBestProvider(createFineCriteria(), true));
	 
	  // using low accuracy provider... to listen for updates
	  mlocManager.requestLocationUpdates(low.getName(), 0, 0f,
	        new LocationListener() {
	        public void onLocationChanged(Location location) {
	          // do something here to save this new location
	        }
	        public void onStatusChanged(String s, int i, Bundle bundle) {
	 
	        }
	        public void onProviderEnabled(String s) {
	           // try switching to a different provider
	        }
	        public void onProviderDisabled(String s) {
	           // try switching to a different provider
	        }
	      });
	 
	  // using high accuracy provider... to listen for updates
	  mlocManager.requestLocationUpdates(high.getName(), 0, 0f,
	        new LocationListener() {
	        public void onLocationChanged(Location location) {
	          // do something here to save this new location
	        }
	        public void onStatusChanged(String s, int i, Bundle bundle) {
	 
	        }
	        public void onProviderEnabled(String s) {
	          // try switching to a different provider
	        }
	        public void onProviderDisabled(String s) {
	          // try switching to a different provider
	        }
	      });
	}
	
}
