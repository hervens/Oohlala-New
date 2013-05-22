package events;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import rewards.CustomPinPoint;

import network.RetrieveData;

import user.Profile;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.gotoohlala.R;

import datastorage.CacheInternalStorage;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.StringLanguageSelector;
import datastorage.Rest.request;

public class EventsCampusStoresDetail extends MapActivity {
	
	String name, image_url, descr, address, phone, email, website, location, store_id;
	String[] open_time;
	double lat, longi, myLat, myLongi;
	ImageView ivThumb;
	TextView tvName, tvLocation, tvDescription, tvDirectionTo;
	Button bCallStore, bEmailStore, bWebsiteStore, bOpen;
	
	MapView map;
	MapController controller;
	
	List<Overlay> overlayList;
	private Drawable drawable;
	
	CustomPinPoint storeLocation;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventscampusstoresdetail);
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
		
		Bundle b = this.getIntent().getExtras();
		name = b.getString("name");
		image_url = b.getString("image_url");
		lat = b.getDouble("lat");
		longi = b.getDouble("longi");
		store_id = b.getString("store_id");
		
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
				open_time = new String[store.getJSONArray("store_hours").length()];
				for (int j = 0; j < store.getJSONArray("store_hours").length(); j++){
					open_time[j] = (String) store.getJSONArray("store_hours").get(j);
				}
			} else {
				open_time = new String[0];
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ivThumb = (ImageView) findViewById(R.id.ivThumb);
		tvName = (TextView) findViewById(R.id.tvName);
		tvLocation = (TextView) findViewById(R.id.tvLocation);
		tvDescription = (TextView) findViewById(R.id.tvDescription);
		tvDirectionTo = (TextView) findViewById(R.id.tvDirectionTo);
		bOpen = (Button) findViewById(R.id.bOpen);
		bCallStore = (Button) findViewById(R.id.bCallStore);
		bEmailStore = (Button) findViewById(R.id.bEmailStore);
		bWebsiteStore = (Button) findViewById(R.id.bWebsiteStore);
		map = (MapView) findViewById(R.id.mvStoresMap);
		
		if (longi == 0 && lat == 0){
			tvDirectionTo.setVisibility(View.GONE);
			map.setVisibility(View.GONE);
		}
		
		if (image_url.trim().length() > 0){
			ivThumb.setImageBitmap(ImageLoader.thumbImageStoreAndLoad(image_url, getBaseContext()));
		}
		
		tvName.setText(StringLanguageSelector.retrieveString(name));
		tvLocation.setText(StringLanguageSelector.retrieveString(location));
		tvDirectionTo.setText(getString(R.string.Directions_to_) + address);
		tvDirectionTo.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
				Uri.parse("http://maps.google.com/maps?saddr="+myLat+","+myLongi+"&daddr="+lat+","+longi));
				startActivity(intent);
			}
		});
		
		tvDescription.setText(StringLanguageSelector.retrieveString(descr));
		
		bOpen.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (open_time.length > 0){
					Bundle extras = new Bundle();
					extras.putStringArray("open_time", open_time);
					
					Intent i = new Intent(getBaseContext(), EventsCampusStoresDetailOpenTime.class);
					i.putExtras(extras);
					startActivity(i);
				} else {
					Toast.makeText(
							getBaseContext(),
							getString(R.string.Store_open_time_not_available),
							Toast.LENGTH_SHORT).show();
				}
			}
	    }); 
		
		bCallStore.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				call(phone);
			}
	    }); 
		
		bEmailStore.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, getString(R.string.Send_email_via)));
			}
	    }); 
		
		bWebsiteStore.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (website.trim().length() > 0){
					if (!website.startsWith("http://") && !website.startsWith("https://")){
						website = "http://" + website;
					}
					
					Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
					websiteIntent.setData(Uri.parse(website));
					startActivity(websiteIntent);
				} else {
					Toast.makeText(
							getBaseContext(),
							getString(R.string.Website_link_not_available),
							Toast.LENGTH_SHORT).show();
				}
			}
	    }); 
		
		drawPinPointOnMap();
		
		Hashtable<String, String> paramsAnalytics = new Hashtable<String, String>();
		paramsAnalytics.put("store_id", store_id);
		//String resultAnalytics = RetrieveData.requestMethod(RetrieveData.STORE_VIEW, paramsAnalytics);
	}

	private void drawPinPointOnMap() {
		// TODO Auto-generated method stub
		map.setBuiltInZoomControls(true);
		
		overlayList = map.getOverlays();
		
		controller = map.getController();
		GeoPoint storePoint = new GeoPoint((int)(lat *1E6), (int)(longi *1E6));
		controller.setCenter(storePoint);
		controller.setZoom(16);
		
		drawable = this.getResources().getDrawable(R.drawable.mapmarker);
		
		OverlayItem overlayItem = new OverlayItem(storePoint, "Store Location", "no");
		storeLocation = new CustomPinPoint(drawable, EventsCampusStoresDetail.this);
		storeLocation.insertPinPoint(overlayItem);
		overlayList.add(storeLocation);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void call(String numbers) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+ numbers));
            startActivity(callIntent);
        } catch (ActivityNotFoundException e) {
            //Log.e("helloandroid dialing example", "Call failed", e);
        }
    }
	
	
}
