package rewards;

import inbox.Inbox;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import network.RetrieveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.Badge;
import profile.UserProfile;
import rewards.RewardsVenues.resumeBitmap;
import user.Profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import campusmap.CampusMap;
import campuswall.CampusWallImage;

import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.UnreadNumCheck;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import datastorage.CacheInternalStorage;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import datastorage.Rest.request;
import events.EventsEvents;
import events.EventsEventsDetail;
import events.EventsEventsMap;
import events.EventsEventsModel;

public class RewardsVenuesStoresContent extends Activity {
	
	String name, store_id, image_url, descr, address, location, phone, email, website, card_code_name;
	int[] open_time_start;
	int[] open_time_stop;
	double lat, longi, myLat, myLongi;
	RewardsVenuesStoresArrayAdapter adapter;
	ListView listView;
	PullToRefreshListView mPullRefreshListView;
	RelativeLayout rlRewardsVenuesStoreContent;
	
	List<RewardsVenuesStoresModel> list = new ArrayList<RewardsVenuesStoresModel>();
	loadThumbBitmapThread loadThumbBitmapThread;
	
	ImageView ivThumb, bgFeaturedBlur;
	
	Handler mHandler = new Handler();
	
	boolean user_fav;
	int fav = 1;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rewardsvenuesstorecontent);
		
		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(RewardsVenuesStoresContent.this));
					
		//content
		Bundle b = this.getIntent().getExtras();
		name = b.getString("name");
		store_id = b.getString("store_id");
		if (b.containsKey("image_url")){
			image_url = b.getString("image_url");
		}
		lat = b.getDouble("lat");
		longi = b.getDouble("longi");
		card_code_name = b.getString("card_code_name");
		user_fav = b.getBoolean("user_fav");
		
		final Button bFav = (Button) findViewById(R.id.bFav);
		if (user_fav){
			fav = 0;
			bFav.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_faved));
		} else {
			fav = 1;
			bFav.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_fav));
		}
		bFav.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				RestClient result = null;
				try {
					result = new Rest.requestBody().execute(Rest.STORE + store_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "fav", String.valueOf(fav)).get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.i("user like store", result.getResponse());
				
				if (result.getResponseCode() == 200){
					if (user_fav){
						user_fav = false;
						fav = 1;
						bFav.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_fav));
					} else {
						user_fav = true;
						fav = 0;
						bFav.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_faved));
					}
				}
			}
	    }); 
		
		header.setText(name.toUpperCase());
		
		if (CacheInternalStorage.getCacheUserLocation(getApplicationContext()) != null){
      		if (CacheInternalStorage.getCacheUserLocation(getApplicationContext()).lat != 0){
      			myLat = CacheInternalStorage.getCacheUserLocation(getApplicationContext()).lat;
				myLongi = CacheInternalStorage.getCacheUserLocation(getApplicationContext()).longi;
      		}
      	} 
		
		//--------------- new api ----------------------
		RestClient result = null;
		try {
			result = new request().execute(Rest.STORE + store_id, Rest.OSESS + Profile.sk, Rest.GET).get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			JSONObject store = new JSONObject(result.getResponse());
			Log.i("store: ", store.toString());
			
			descr = store.getString("description");
			address = store.getString("address");
			phone = store.getString("phone");
			email = store.getString("email");
			website = store.getString("website");
			location = store.getString("location");
			if (store.getBoolean("has_hours")){
				open_time_start = new int[store.getJSONArray("store_hours").length()];
				open_time_stop = new int[store.getJSONArray("store_hours").length()];
				for (int j = 0; j < store.getJSONArray("store_hours").length(); j++){
					JSONArray sublist = (JSONArray) store.getJSONArray("store_hours").get(j);
					open_time_start[j] = (Integer) sublist.get(0);
					open_time_stop[j] = (Integer) sublist.get(1);
				}
			} else {
				open_time_start = new int[0];
				open_time_stop = new int[0];
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//---------------------- old api ------------------------
		/*
		Hashtable<String, String> params2 = new Hashtable<String, String>();
		params2.put("object_id", store_id); //-1 for campus
		params2.put("is_franchise", String.valueOf(0));		
		String result2 = RetrieveData.requestMethod(RetrieveData.GET_STORE_BY_ID, params2);
		try {
			JSONArray stores = (new JSONObject(result2)).getJSONArray("stores");
			Log.i("stores: ", stores.toString());
			Log.i("store_id: ", store_id);
			
			for (int i = 0; i < stores.length(); i++){
				if (stores.getJSONObject(i).getInt("a") == Integer.valueOf(store_id)){
					location = stores.getJSONObject(i).getString("A");
					open_time = new String[stores.getJSONObject(i).getJSONArray("C").length()];
					for (int j = 0; j < stores.getJSONObject(i).getJSONArray("C").length(); j++){
						open_time[j] = (String) stores.getJSONObject(i).getJSONArray("C").get(j);
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		ivThumb = (ImageView) findViewById(R.id.ivThumb);
		new loadThumbImage().execute();
		
		bgFeaturedBlur = (ImageView) findViewById(R.id.bgFeaturedBlur);
		if (image_url != null && image_url.contains(".png")){
			bgFeaturedBlur.setImageBitmap(ImageLoader.KembrelImageStoreAndLoad(image_url, getBaseContext()));
		}
		
		TextView tvName = (TextView) findViewById(R.id.tvName);
		tvName.setText(StringLanguageSelector.retrieveString(name));
		
		TextView tvDescr = (TextView) findViewById(R.id.tvDescr);
		tvDescr.setTypeface(Fonts.getOpenSansLightItalic(RewardsVenuesStoresContent.this));
		tvDescr.setText(StringLanguageSelector.retrieveString(descr));
		
		rlRewardsVenuesStoreContent = (RelativeLayout) findViewById(R.id.rlRewardsVenuesStoreContent);
		rlRewardsVenuesStoreContent.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Bundle extras = new Bundle();
				extras.putString("store_name", name);
				extras.putString("location", location);
				extras.putString("store_logo", image_url);
				extras.putDouble("lat", lat);
				extras.putDouble("longi", longi);
				extras.putString("address", address);
				extras.putString("descr", descr);
				extras.putString("phone", phone);
				extras.putString("email", email);
				extras.putString("website", website);
				extras.putIntArray("open_time_start", open_time_start);
				extras.putIntArray("open_time_stop", open_time_stop);
				extras.putDouble("myLat", myLat);
				extras.putDouble("myLongi", myLongi);
				extras.putBoolean("user_fav", user_fav);
				extras.putString("store_id", store_id);
				
				Intent i = new Intent(getBaseContext(), EventsEventsMap.class);
				i.putExtras(extras);
				startActivity(i);
			}
	    }); 
		
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.lvRewardsVenuesStoreContent);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
				
				reloadView();
			}
		});
		mPullRefreshListView.setRefreshing();
		
		listView = mPullRefreshListView.getRefreshableView();
		adapter = new RewardsVenuesStoresArrayAdapter(this, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				position--;
					if (list.get(position).type == 0){
						Bundle extras = new Bundle();
						extras.putString("name", name);
						extras.putString("image_url", image_url);
						extras.putString("title", list.get(position).title);
						extras.putString("description", list.get(position).description);
						extras.putString("expiration", list.get(position).expirationTime);
						extras.putString("descr", descr);
						extras.putDouble("lat", lat);
						extras.putDouble("longi", longi);
						extras.putString("address", address);
						extras.putString("deal_id", list.get(position).deal_id);
						extras.putString("code", list.get(position).code);
						extras.putString("store_id", store_id);
						extras.putString("phone", phone);
						extras.putString("email", email);
						extras.putString("website", website);
						extras.putString("card_code_name", card_code_name);
						extras.putInt("user_like", list.get(position).user_like);
						extras.putString("image", list.get(position).image);
						extras.putDouble("myLat", myLat);
						extras.putDouble("myLongi", myLongi);
						
						Intent i = new Intent(RewardsVenuesStoresContent.this, RewardsVenuesStoresDeal.class);
						i.putExtras(extras);
						startActivity(i);		
					} else if (list.get(position).type == 1){
						Bundle extras = new Bundle();
						extras.putString("event_id", list.get(position).event_id);
						extras.putString("title", list.get(position).title);
						extras.putString("description", list.get(position).description);
						extras.putString("store_logo", list.get(position).store_logo);
						extras.putString("start_time", list.get(position).start_time);
						extras.putString("end_time", list.get(position).end_time);
						extras.putString("store_id", list.get(position).store_id);
						extras.putInt("user_like", list.get(position).user_like);
						extras.putString("image", list.get(position).image);
						extras.putInt("user_attend", list.get(position).user_attend);
						
						Intent i = new Intent(RewardsVenuesStoresContent.this, EventsEventsDetail.class);
						i.putExtras(extras);
						startActivity(i);
					} else if (list.get(position).type == 2){
						Bundle extras = new Bundle();
						extras.putString("name", name);
						extras.putString("image_url", image_url);
						extras.putString("title", list.get(position).title);
						extras.putString("description", list.get(position).description);
						extras.putString("expiration", list.get(position).expirationTime);
						extras.putString("descr", descr);
						extras.putDouble("lat", lat);
						extras.putDouble("longi", longi);
						extras.putString("address", address);
						extras.putString("card_id", list.get(position).card_id);
						extras.putString("code", list.get(position).code);
						extras.putString("store_id", store_id);
						extras.putString("phone", phone);
						extras.putString("email", email);
						extras.putString("website", website);
						extras.putString("card_code_name", card_code_name);
						extras.putInt("user_points", list.get(position).user_points);
						extras.putInt("points", list.get(position).points);
						extras.putString("ref_code", list.get(position).ref_code);
						extras.putInt("user_like", list.get(position).user_like);
						extras.putDouble("myLat", myLat);
						extras.putDouble("myLongi", myLongi);
						
						//Intent i = new Intent(RewardsVenuesStoresContent.this, RewardsVenuesStoresCard.class);
						//i.putExtras(extras);
						//startActivity(i);		
					} 
			}
		});
		
		new updateListThread2().execute();
	}
	
	private class loadThumbImage extends AsyncTask<Void, Void, Void> {
    	
		@Override
		protected Void doInBackground(Void... params) {
			
			runOnUiThread(new Runnable() {
				public void run() {
					if (image_url != null && image_url.contains(".png")){
						ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(ImageLoader.KembrelImageStoreAndLoad(image_url, getBaseContext())), 8));
					}
				}
			});
			
			return null;
		}
    }
	
	class updateListThread2 extends AsyncTask<Void, List<RewardsVenuesStoresModel>, List<RewardsVenuesStoresModel>> {
	    // This method is called when the thread runs
		List<RewardsVenuesStoresModel> listTemp = null;
		
		protected List<RewardsVenuesStoresModel> doInBackground(Void... cuts) {
				listTemp = new ArrayList<RewardsVenuesStoresModel>();
				
				RestClient result = null;
				try {
					result = new request().execute(Rest.DEAL + "?store_id=" + store_id, Rest.OSESS + Profile.sk, Rest.GET).get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try {
					JSONArray deals = new JSONArray(result.getResponse());
					Log.i("deals: ", deals.toString());
					
					for (int i = 0; i < deals.length(); i++){
						String title = StringLanguageSelector.retrieveString(deals.getJSONObject(i).getString("title"));
						String description = StringLanguageSelector.retrieveString(deals.getJSONObject(i).getString("description"));
						int bottom = deals.getJSONObject(i).getInt("expiration") - (int)(System.currentTimeMillis()/1000);
						int deal_id = deals.getJSONObject(i).getInt("id");
						String code = deals.getJSONObject(i).getString("code");
						int expiration = deals.getJSONObject(i).getInt("expiration");
						String ref_code = deals.getJSONObject(i).getString("ref_code");
						int user_like = deals.getJSONObject(i).getInt("user_like");
						String image = deals.getJSONObject(i).getString("image_url");
						String image_thumb = deals.getJSONObject(i).getString("image_thumb_url");
						
						listTemp.add(new RewardsVenuesStoresModel(title, description, String.valueOf(bottom), String.valueOf(deal_id), code, 0, null, null, null, expiration, null, 0, 0, ref_code, RewardsVenuesStoresContent.this, user_like, image, image_thumb, null, 0, 0, false, null, null));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
					
				RestClient result2 = null;
				try {
					result2 = new request().execute(Rest.EVENT + "?store_id=" + store_id, Rest.OSESS + Profile.sk, Rest.GET).get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try {
					JSONArray events = new JSONArray(result2.getResponse());
					Log.i("events: ", events.toString());
					
					for (int i = 0; i < events.length(); i++){
						String event_id = String.valueOf(events.getJSONObject(i).getInt("id"));
						String title = events.getJSONObject(i).getString("title");
						String description = events.getJSONObject(i).getString("description");
						String store_id = String.valueOf(events.getJSONObject(i).getString("store_id"));
						String store_logo = events.getJSONObject(i).getString("store_logo_url");
						String end_time = String.valueOf(events.getJSONObject(i).getInt("end"));
						String start_time = String.valueOf(events.getJSONObject(i).getInt("start"));
						int user_like = events.getJSONObject(i).getInt("likes");
						String image = events.getJSONObject(i).getString("poster_url");
						String image_thumb = events.getJSONObject(i).getString("poster_thumb_url");
						
						int attends = events.getJSONObject(i).getInt("attends");
						int user_attend = events.getJSONObject(i).getInt("user_attend");
						boolean is_featured = events.getJSONObject(i).getBoolean("is_featured");
						
						listTemp.add(new RewardsVenuesStoresModel(title, description, String.valueOf(start_time), null, null, 1, event_id, start_time, end_time, 0, null, 0, 0, null, RewardsVenuesStoresContent.this, user_like, image, image_thumb, null, attends, user_attend, is_featured, store_id, store_logo));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 	
				
				RestClient result3 = null;
				try {
					result3 = new request().execute(Rest.CARD + "?store_id=" + store_id, Rest.OSESS + Profile.sk, Rest.GET).get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try {
					JSONArray cards = new JSONArray(result3.getResponse());
					Log.i("cards: ", cards.toString());
					
					for (int i = 0; i < cards.length(); i++){
						String card_id = String.valueOf(cards.getJSONObject(i).getInt("id"));
						String title = StringLanguageSelector.retrieveString(cards.getJSONObject(i).getString("title"));
						String description = StringLanguageSelector.retrieveString(cards.getJSONObject(i).getString("description"));
						int user_points = cards.getJSONObject(i).getInt("user_points");
						int points = cards.getJSONObject(i).getInt("points");
						String stampedUsage = user_points + "/" + points;
						
						String code = cards.getJSONObject(i).getString("code");
						int expiration = cards.getJSONObject(i).getInt("expiration");
						String ref_code = cards.getJSONObject(i).getString("ref_code");
						int user_like = cards.getJSONObject(i).getInt("user_like");
						
						listTemp.add(new RewardsVenuesStoresModel(title, description, stampedUsage, null, code, 2, null, null, null, expiration, card_id, user_points, points, ref_code, RewardsVenuesStoresContent.this, user_like, null, null, null, 0, 0, false, null, null));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				return listTemp;
		}
		
		protected void onPostExecute(List<RewardsVenuesStoresModel> result) {
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
	
	public void preLoadImages() {
		// TODO Auto-generated method stub
		loadThumbBitmapThread = new loadThumbBitmapThread();
		loadThumbBitmapThread.execute(0, list.size());
	}
	
	class loadThumbBitmapThread extends AsyncTask<Integer, Void, Void> {
	    // This method is called when the thread runs
		public int start, stop;

		protected Void doInBackground(Integer... num) {
	    	//get the all the current games
			this.start = num[0];
			this.stop = num[1];
	    	Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	for (int i = start; i < stop; i++){
	    		if (i < list.size()){
		    		if (list.get(i).image_thumb != null && (list.get(i).image_thumb.contains(".png") || list.get(i).image_thumb.contains(".jpg")) && list.get(i).bitmap == null){
		    			if (list.get(i).image_thumb.contains(".jpg")){
		    				list.get(i).bitmap = ImageLoader.superSmallThumbImageStoreAndLoad(list.get(i).image, RewardsVenuesStoresContent.this);
		    				mHandler.post(new Runnable() {
		    	        		public void run() {
		    				    	listView.invalidateViews();
		    				    }
		    				});
		    			} else {
		    				list.get(i).bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(list.get(i).image, RewardsVenuesStoresContent.this, 70);
		    				mHandler.post(new Runnable() {
		    	        		public void run() {
		    				    	listView.invalidateViews();
		    				    }
		    				});
		    			}
					} else if (list.get(i).store_logo != null && (list.get(i).store_logo.contains(".png") || list.get(i).store_logo.contains(".jpg")) && list.get(i).bitmap == null){
		    			if (list.get(i).store_logo.contains(".jpg")){
		    				list.get(i).bitmap = ImageLoader.superSmallThumbImageStoreAndLoad(list.get(i).store_logo, RewardsVenuesStoresContent.this);
		    				mHandler.post(new Runnable() {
		    	        		public void run() {
		    				    	listView.invalidateViews();
		    				    }
		    				});
		    			} else {
		    				list.get(i).bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(list.get(i).store_logo, RewardsVenuesStoresContent.this, 70);
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
	
	public void reloadView() {
        // Do work to refresh the list here.
		if (loadThumbBitmapThread != null){
			loadThumbBitmapThread.cancel(true);
		}
		
		runOnUiThread(new Runnable() {
		    public void run() {
		    	list.clear();
		    }
		});
    	
		new updateListThread2().execute();
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
					if (list.get(i).image_thumb != null && (list.get(i).image_thumb.contains(".png") || list.get(i).image_thumb.contains(".jpg")) && list.get(i).bitmap == null){	    			
						if (list.get(i).image_thumb.contains(".jpg")){
		    				list.get(i).bitmap = ImageLoader.superSmallThumbImageStoreAndLoad(list.get(i).image, RewardsVenuesStoresContent.this);
		    				mHandler.post(new Runnable() {
		    	        		public void run() {
		    				    	listView.invalidateViews();
		    				    }
		    				});
		    			} else {
		    				list.get(i).bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(list.get(i).image, RewardsVenuesStoresContent.this, 70);
		    				mHandler.post(new Runnable() {
		    	        		public void run() {
		    				    	listView.invalidateViews();
		    				    }
		    				});
		    			}
					} else if (list.get(i).store_logo != null && (list.get(i).store_logo.contains(".png") || list.get(i).store_logo.contains(".jpg")) && list.get(i).bitmap == null){
		    			if (list.get(i).store_logo.contains(".jpg")){
		    				list.get(i).bitmap = ImageLoader.superSmallThumbImageStoreAndLoad(list.get(i).store_logo, RewardsVenuesStoresContent.this);
		    				mHandler.post(new Runnable() {
		    	        		public void run() {
		    				    	listView.invalidateViews();
		    				    }
		    				});
		    			} else {
		    				list.get(i).bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(list.get(i).store_logo, RewardsVenuesStoresContent.this, 70);
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
				if (list.get(i).bitmap != null){
					list.get(i).bitmap.recycle();
					list.get(i).bitmap = null;
				}
			} else {
				break;
			}
		}	
	}
	
}
