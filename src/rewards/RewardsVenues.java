package rewards;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import network.RetrieveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import user.Profile;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import datastorage.CacheInternalStorage;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import datastorage.Rest.request;
import events.EventsEventsModel;

public class RewardsVenues extends ActivityGroup {
	
	String category_id, card_code_name;
	double lat, longi;
	LocationManager lm;
	String towers;
	RewardsVenuesArrayAdapter adapter;
	ListView listView;
	PullToRefreshListView mPullRefreshListView;
	
	List<RewardsVenuesModel> list = new ArrayList<RewardsVenuesModel>();
	
	loadBitmapTread loadBitmapTread;
	
	Handler mHandler = new Handler();
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rewardsvenues);
		
		Bundle b = this.getIntent().getExtras();
		category_id = b.getString("category_id");
		card_code_name = b.getString("card_code_name");
		lat = b.getDouble("lat");
		longi = b.getDouble("longi");
		
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.lvRewardsVenues);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
				
				reloadView();
			}
		});
		mPullRefreshListView.setRefreshing();
		
		listView = mPullRefreshListView.getRefreshableView();
		adapter = new RewardsVenuesArrayAdapter(this, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				position--;
				Bundle extras = new Bundle();
				extras.putString("name", list.get(position).name);
				extras.putString("store_id", list.get(position).store_id);
				extras.putString("image_url", list.get(position).image_url);
				extras.putString("descr", list.get(position).descr);
				extras.putDouble("lat", list.get(position).lat);
				extras.putDouble("longi", list.get(position).longi);
				extras.putString("address", list.get(position).address);
				extras.putString("phone", list.get(position).phone);
				extras.putString("email", list.get(position).email);
				extras.putString("website", list.get(position).website);
				extras.putString("card_code_name", card_code_name);
				extras.putBoolean("user_fav", list.get(position).user_fav);
				
				Intent i = new Intent(getBaseContext(), RewardsVenuesStoresContent.class);
				i.putExtras(extras);
				startActivity(i);
			}
		});
		
		new updateListThread2().execute();
	}
	
	public void reloadView() {
        // Do work to refresh the list here.
		if (loadBitmapTread != null){
			loadBitmapTread.cancel(true);
		}
		
		runOnUiThread(new Runnable() {
		    public void run() {
		    	list.clear();
		    }
		});
    	
		new updateListThread2().execute();
    }
	
	class updateListThread2 extends AsyncTask<Void, List<RewardsVenuesModel>, List<RewardsVenuesModel>> {
	    // This method is called when the thread runs

		protected List<RewardsVenuesModel> doInBackground(Void... cuts) {
			List<RewardsVenuesModel> listTemp = new ArrayList<RewardsVenuesModel>();
			
			RestClient result = null;
			try {
				result = new request().execute(Rest.STORE + "?category_id=" + category_id + "&latitude=" +  lat + "&longitude=" +  longi, Rest.OSESS + Profile.sk, Rest.GET).get();
				Log.i("Lat", String.valueOf(lat));
				Log.i("Longi", String.valueOf(longi));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray stores = new JSONArray(result.getResponse());
				Log.i("store venues: ", stores.toString());
				
				for (int i = 0; i < stores.length(); i++){
					String name = StringLanguageSelector.retrieveString(stores.getJSONObject(i).getString("name"));
					String itemName = StringLanguageSelector.retrieveString(stores.getJSONObject(i).getString("secondary_name"));
					String image_url = stores.getJSONObject(i).getString("logo_url");
					//String descr = StringLanguageSelector.retrieveString(stores.getJSONObject(i).getString("description"));
					String descr = null;
					//int number = stores.getJSONObject(i).getInt("G");
					int number = 0;
					int store_id = stores.getJSONObject(i).getInt("id");
					double lat = stores.getJSONObject(i).getDouble("latitude");
					double longi = stores.getJSONObject(i).getDouble("longitude");
					//String address = StringLanguageSelector.retrieveString(stores.getJSONObject(i).getString("address"));
					String address = null;
					
					//String phone = stores.getJSONObject(i).getString("phone");
					String phone = null;
					//String email = stores.getJSONObject(i).getString("email");
					String email = null;
					//String website = stores.getJSONObject(i).getString("website");
					String website = null;
					boolean user_fav = stores.getJSONObject(i).getBoolean("user_fav");
					
					listTemp.add(new RewardsVenuesModel(name, itemName, image_url, String.valueOf(number), String.valueOf(store_id), descr, lat, longi, address, phone, email, website, null, user_fav));
				}
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return listTemp;
	    }
		
		protected void onPostExecute(List<RewardsVenuesModel> result) {
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
		loadBitmapTread = new loadBitmapTread();
		loadBitmapTread.execute(0, list.size());
	}
	
	class loadBitmapTread extends AsyncTask<Integer, Void, Void> {
	    // This method is called when the thread runs
		public int start, stop;

		protected Void doInBackground(Integer... num) {
	    	//get the all the current games
			this.start = num[0];
			this.stop = num[1];
	    	Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	for (int i = start; i < stop; i++){
	    		if (i < list.size()){
					if (list.get(i).image_url != null && list.get(i).image_bitmap == null){
						if (list.get(i).image_url.contains(".jpg")){
		    				list.get(i).image_bitmap = ImageLoader.KembrelImageStoreAndLoad(list.get(i).image_url, RewardsVenues.this);
		    				mHandler.post(new Runnable() {
		    	        		public void run() {
		    				    	listView.invalidateViews();
		    				    }
		    				});
		    			} else {
		    				list.get(i).image_bitmap = ImageLoader.KembrelImageStoreAndLoad(list.get(i).image_url, RewardsVenues.this);
		    				mHandler.post(new Runnable() {
		    	        		public void run() {
		    				    	listView.invalidateViews();
		    				    }
		    				});
		    			}
					}
	    		} else {
	    			break;
	    		}
			}
	    	
	    	return null;
	    }
	}
	
	public void onResume() {
		super.onResume();

		new resumeBitmap().execute();
	}
	
	class resumeBitmap extends AsyncTask<Void, Void, Void> {
	    // This method is called when the thread runs
		protected Void doInBackground(Void... params) {
			for (int i = 0; i < list.size(); i++){
				if (i < list.size()){
					if (list.get(i).image_url != null && list.get(i).image_bitmap == null){
						if (list.get(i).image_url.contains(".jpg")){
		    				list.get(i).image_bitmap = ImageLoader.KembrelImageStoreAndLoad(list.get(i).image_url, getApplicationContext());
		    				mHandler.post(new Runnable() {
		    	        		public void run() {
		    				    	listView.invalidateViews();
		    				    }
		    				});
		    			} else {
		    				list.get(i).image_bitmap = ImageLoader.KembrelImageStoreAndLoad(list.get(i).image_url, getApplicationContext());
		    				mHandler.post(new Runnable() {
		    	        		public void run() {
		    				    	listView.invalidateViews();
		    				    }
		    				});
		    			}
					}
				} else {
					break;
				}
			}
			
			return null;
		}
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		//compass.disableCompass();
		super.onPause();
		
		for (int i = 0; i < list.size(); i++){
			if (i < list.size()){
				if (list.get(i).image_bitmap != null){
					list.get(i).image_bitmap.recycle();
					list.get(i).image_bitmap = null;
				}
			} else {
				break;
			}
		}	
	}
	
}
