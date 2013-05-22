package rewards;

import inbox.Inbox;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import network.GPS;
import network.GPSLocationChanged;
import network.RetrieveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.UserProfile;

import studentsnearby.LocationArrayAdapter;
import studentsnearby.nearbyLocationModel;
import studentsnearby.studentsNearby;
import user.Profile;

import campusmap.CampusMap;
import campusmap.CampusMapArrayAdapter;
import campusmap.CampusMapModel;
import campuswall.CampusWallModel;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.UnreadNumCheck;
import com.gotoohlala.R.layout;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import datastorage.CacheInternalStorage;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.TimeCounter;
import datastorage.UserLocation;
import datastorage.UserLoginInfo;
import datastorage.Rest.request;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Rewards extends Activity implements GPSLocationChanged {
	
	RewardsArrayAdapter adapter;
	ListView listView, listView2;
	PullToRefreshListView mPullRefreshListView;
	
	double myLat, myLongi, myLatCurrent, myLongiCurrent;
	
	List<RewardsModel> list = new ArrayList<RewardsModel>();
	List<RewardsLocationModel> locationlist = new ArrayList<RewardsLocationModel>();
	RewardsLocationArrayAdapter Locationadapter;
	
	private Handler mHandler = new Handler();
	
	boolean changeLocationClicked = false;
	
	loadBitmapThread loadBitmapThread;
	boolean cachedLocationFound = false;
	
	boolean currentView = false;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rewards);
		
		currentView = true;
		
		GPS.act = Rewards.this;
		GPS.getMyLocation(Rewards.this);
	
		//content
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.lvRewardsCategories);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
				
				reloadView();
			}
		});
		mPullRefreshListView.setRefreshing();
		
		listView = mPullRefreshListView.getRefreshableView();
		adapter = new RewardsArrayAdapter(Rewards.this, Rewards.this, list);
		listView.setAdapter(adapter);
		
		listView2 = (ListView) findViewById(R.id.lvRewardsLocations);
		
		final Button bChangeRewardsLocation = (Button) findViewById(R.id.bChangeRewardsLocation);
		bChangeRewardsLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	if (!changeLocationClicked){
            		locationlist.clear();
            		
            		listView.setVisibility(View.GONE);
            		listView2.setVisibility(View.VISIBLE);
            		
            		changeLocationClicked = true;
            		bChangeRewardsLocation.setText(getString(R.string.Cancel));
            		
            		Locationadapter = new RewardsLocationArrayAdapter(Rewards.this, Rewards.this, locationlist);
	            	listView2.setAdapter(Locationadapter);
	            	listView2.setOnItemClickListener(new OnItemClickListener() {
	        			public void onItemClick(AdapterView<?> parent, View view,
	        				int position, long id) {
		    	    			listView.setVisibility(View.VISIBLE);
		                		listView2.setVisibility(View.GONE);
		                		
		                		changeLocationClicked = false;
		                		bChangeRewardsLocation.setText("Change Location");
		                		
		                		setLocation(locationlist.get(position).latitude, locationlist.get(position).longitude);
		    	    			
		    	    			refreshRewards();
	        			}
	        		});
	            	
	            	/*
	            	Hashtable<String, String> params = new Hashtable<String, String>();
	    			String result = RetrieveData.requestMethod(RetrieveData.HOTSPOTS, params);
	    			Log.i("hotspots: ", result);
	    			try {
	    				JSONArray hotspots = (new JSONObject(result)).getJSONArray("hotspots");
	    				
	    				locationlist.add(new RewardsLocationModel(0, getString(R.string.Current_Location), myLatCurrent, myLongiCurrent)); //add current location
	    				for (int i = 0; i < hotspots.length(); i++){
	    					int hotspot_id = hotspots.getJSONObject(i).getInt("hotspot_id");
	    					String name = hotspots.getJSONObject(i).getString("name");
	    					double longitude = hotspots.getJSONObject(i).getDouble("longitude");
	    					double latitude = hotspots.getJSONObject(i).getDouble("latitude");
	    					locationlist.add(new RewardsLocationModel(hotspot_id, name, latitude, longitude));
	    				}
	    			} catch (JSONException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	    			*/
            	} else {
            		listView.setVisibility(View.VISIBLE);
            		listView2.setVisibility(View.GONE);
            		
            		changeLocationClicked = false;
            		bChangeRewardsLocation.setText(getString(R.string.Change_Location));
            	}
            }
		});
		
		if (CacheInternalStorage.getCacheUserLocation(getApplicationContext()) != null){
      		if (CacheInternalStorage.getCacheUserLocation(getApplicationContext()).lat != 0){
      			cachedLocationFound = true;
      			myLatCurrent = CacheInternalStorage.getCacheUserLocation(getApplicationContext()).lat;
    			myLongiCurrent = CacheInternalStorage.getCacheUserLocation(getApplicationContext()).longi;
      			setLocation(CacheInternalStorage.getCacheUserLocation(getApplicationContext()).lat, 
      					CacheInternalStorage.getCacheUserLocation(getApplicationContext()).longi);
      			
      			Log.i("cached location 0", "--------------------------------------------");
      			new updateListThread2().execute();      				
      		} else if (!GPS.getGPS && !GPS.getServiceProvider){
          		Log.i("0,0 location 1", "--------------------------------------------");
          		new updateListThread2().execute();
    		}
      	} else if (!GPS.getGPS && !GPS.getServiceProvider){
      		Log.i("0,0 location 2", "--------------------------------------------");
      		new updateListThread2().execute();
		}
		
	}
	
	public void reloadView() {
        // Do work to refresh the list here.
		if (loadBitmapThread != null){
			loadBitmapThread.cancel(true);
		}
		
		mHandler.post(new Runnable() {
    		public void run() {
		    	list.clear();
		    	
		    	if (list.isEmpty()){
		    		new updateListThread2().execute();
				}
		    }
		});
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
	}
	
	public void setLocation(double lat, double longi){
		myLat = lat;
		myLongi = longi;
	}
	
	public void refreshRewards(){
		//loadBitmapThread.cancel(true);
		list.clear();
		
		new updateListThread2().execute();
	}
	
	class updateListThread2 extends AsyncTask<Void, List<RewardsModel>, List<RewardsModel>> {
	    // This method is called when the thread runs
		
		List<RewardsModel> listTemp = new ArrayList<RewardsModel>();

		protected List<RewardsModel> doInBackground(Void... cuts) {
			RestClient result = null;
			try {
				result = new request().execute(Rest.STORE_CATEGORY + "?latitude=" + myLat + "&longitude=" + myLongi, Rest.OSESS + Profile.sk, Rest.GET).get();
				Log.i("myLat", String.valueOf(myLat));
				Log.i("myLongi", String.valueOf(myLongi));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray categories = new JSONArray(result.getResponse());
				Log.i("categories: ", categories.toString());
				
				for (int i = 0; i < categories.length(); i++){
					String name = categories.getJSONObject(i).getString("name");
					int category_id = categories.getJSONObject(i).getInt("id");
					//int store_id = categories.getJSONObject(i).getInt("store_id");
					int store_id = 2; 
					int store_count = categories.getJSONObject(i).getInt("store_count");
					String image_url = categories.getJSONObject(i).getString("image_url");
					String description = categories.getJSONObject(i).getString("description");

					if (store_count > 0){
						listTemp.add(new RewardsModel(name, category_id, store_id, image_url, null, myLat, myLongi));
					}
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return listTemp;
	    }
		
		protected void onPostExecute(List<RewardsModel> result) {
			if (currentView){
		    	//listView.setVisibility(View.GONE);
		    	list.addAll(result);
		    	listView.invalidateViews();
		    	adapter.notifyDataSetChanged();
		    	//listView.setVisibility(View.VISIBLE);
		    	
		    	preLoadImages();
		    	
		    	mPullRefreshListView.onRefreshComplete();
		    	
		    	listTemp.clear();
				listTemp = null;	
			}
	     }
		
	}
	
	class updateListThread3 extends AsyncTask<Void, List<RewardsModel>, List<RewardsModel>> {
	    // This method is called when the thread runs

		protected List<RewardsModel> doInBackground(Void... cuts) {
			List<RewardsModel> listTemp = new ArrayList<RewardsModel>();
			
			RestClient result = null;
			try {
				result = new request().execute(Rest.STORE_CATEGORY, Rest.OSESS + Profile.sk, Rest.GET).get();
				Log.i("myLat", String.valueOf(myLat));
				Log.i("myLongi", String.valueOf(myLongi));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray categories = new JSONArray(result.getResponse());
				Log.i("categories: ", categories.toString());
				
				for (int i = 0; i < categories.length(); i++){
					String name = categories.getJSONObject(i).getString("name");
					int category_id = categories.getJSONObject(i).getInt("id");
					//int store_id = categories.getJSONObject(i).getInt("store_id");
					int store_id = 2; 
					//int store_count = categories.getJSONObject(i).getInt("store_count");
					int store_count = 1;
					String image_url = categories.getJSONObject(i).getString("image_url");
					String description = categories.getJSONObject(i).getString("description");

					if (store_count > 0){
						listTemp.add(new RewardsModel(name, category_id, store_id, image_url, null, myLat, myLongi));
					}
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return listTemp;
	    }
		
		protected void onPostExecute(List<RewardsModel> result) {
	    	//listView.setVisibility(View.GONE);
	    	list.addAll(result);
	    	listView.invalidateViews();
	    	adapter.notifyDataSetChanged();
	    	//listView.setVisibility(View.VISIBLE);
	    	
	    	preLoadImages();
	    	
	    	mPullRefreshListView.onRefreshComplete();
	     }
		
	}
	
	public void preLoadImages() {
		// TODO Auto-generated method stub
		loadBitmapThread = new loadBitmapThread();
		loadBitmapThread.execute(0, list.size());
	}
	
	class loadBitmapThread extends AsyncTask<Integer, Void, Void> {
	    // This method is called when the thread runs
		public int start, stop;

		protected Void doInBackground(Integer... num) {
	    	//get the all the current games
			this.start = num[0];
			this.stop = num[1];
	    	Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	for (int i = start; i < stop; i++){
	    		if (i < list.size() && currentView){
					if (list.get(i).image_url != null && list.get(i).image_url.contains(".png") && list.get(i).image_bitmap == null){					
						list.get(i).image_bitmap = ImageLoader.thumbImageStoreAndLoad(list.get(i).image_url, Rewards.this);
						mHandler.post(new Runnable() {
			        		public void run() {
						    	listView.invalidateViews();
						    }
						});
					}
	    		} else {
	    			break;
	    		}
			}
	    	
	    	return null;
	    }
	}
	
	public void locationChanged() {
		// TODO Auto-generated method stub	
		if (!cachedLocationFound && (GPS.getGPS || GPS.getServiceProvider)){
			myLatCurrent = GPS.myLat;
			myLongiCurrent = GPS.myLongi;
			setLocation(myLatCurrent, myLongiCurrent);
			
			refreshRewards();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
    		intent.addCategory(Intent.CATEGORY_HOME);
    		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		startActivity(intent);
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		currentView = false;
	}
	
}
