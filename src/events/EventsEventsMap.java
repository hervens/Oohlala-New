package events;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import rewards.CustomPinPoint;
import rewards.RewardsVenuesStoresContent;
import user.Profile;

import network.RetrieveData;

import ManageThreads.TaskQueueImage;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.gotoohlala.R;

import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import events.EventsEventsDetail.loadThumbImage;

public class EventsEventsMap extends MapActivity {
	
	String store_name, location, image_url, descr, address, phone, email, website;
	int[] open_time_start;
	int[] open_time_stop;
	double lat, longi, myLat, myLongi;
	ImageView ivThumb;
	TextView tvStoreName, tvDescription, tvDirectionTo, tvHours;
	Button bCallStore, bEmailStore, bWebsiteStore;
	
	MapView map;
	MapController controller;
	
	List<Overlay> overlayList;
	private Drawable drawable;
	
	CustomPinPoint storeLocation;
	
	RelativeLayout content1, content2, content3, content4;
	boolean descrMaxLine = false;
	boolean hoursMaxLine = false;
	
	TextView tvOpentime0, tvOpentime1, tvOpentime2, tvOpentime3, tvOpentime4, tvOpentime5, tvOpentime6, tvDay0, tvDay1, tvDay2, tvDay3, tvDay4, tvDay5, tvDay6;
	View divider1, divider2, divider3;
	
	Handler mHandler = new Handler();
	
	boolean user_fav;
	int fav = 1;
	String store_id;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventseventsmap);
		
		Bundle b = this.getIntent().getExtras();
		store_name = b.getString("store_name");
		location = b.getString("location");
		image_url = b.getString("store_logo");
		lat = b.getDouble("lat");
		longi = b.getDouble("longi");
		address = b.getString("address");
		descr = b.getString("descr");
		phone = b.getString("phone");
		email = b.getString("email");
		website = b.getString("website");
		open_time_start = b.getIntArray("open_time_start");
		open_time_stop = b.getIntArray("open_time_stop");
		myLat = b.getDouble("myLat");
		myLongi = b.getDouble("myLongi");
		
		user_fav = b.getBoolean("user_fav");
		store_id = b.getString("store_id");
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
		
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
		
		ivThumb = (ImageView) findViewById(R.id.ivThumb);
		tvStoreName = (TextView) findViewById(R.id.tvStoreName);
		tvDescription = (TextView) findViewById(R.id.tvDescription);
		tvDirectionTo = (TextView) findViewById(R.id.tvDirectionTo);
		bCallStore = (Button) findViewById(R.id.bCallStore);
		bEmailStore = (Button) findViewById(R.id.bEmailStore);
		bWebsiteStore = (Button) findViewById(R.id.bWebsiteStore);
		map = (MapView) findViewById(R.id.mvEventsMap);
		
		content1 = (RelativeLayout) findViewById(R.id.content1);
		content2 = (RelativeLayout) findViewById(R.id.content2);
		content3 = (RelativeLayout) findViewById(R.id.content3);
		content4 = (RelativeLayout) findViewById(R.id.content4);
		
		tvOpentime0 = (TextView) findViewById(R.id.tvOpentime0);
		tvOpentime1 = (TextView) findViewById(R.id.tvOpentime1);
		tvOpentime2 = (TextView) findViewById(R.id.tvOpentime2);
		tvOpentime3 = (TextView) findViewById(R.id.tvOpentime3);
		tvOpentime4 = (TextView) findViewById(R.id.tvOpentime4);
		tvOpentime5 = (TextView) findViewById(R.id.tvOpentime5);
		tvOpentime6 = (TextView) findViewById(R.id.tvOpentime6);
		
		tvDay0 = (TextView) findViewById(R.id.tvDay0);
		tvDay1 = (TextView) findViewById(R.id.tvDay1);
		tvDay2 = (TextView) findViewById(R.id.tvDay2);
		tvDay3 = (TextView) findViewById(R.id.tvDay3);
		tvDay4 = (TextView) findViewById(R.id.tvDay4);
		tvDay5 = (TextView) findViewById(R.id.tvDay5);
		tvDay6 = (TextView) findViewById(R.id.tvDay6);
		
		divider1 = (View) findViewById(R.id.divider1);
		divider2 = (View) findViewById(R.id.divider2);
		divider3 = (View) findViewById(R.id.divider3);
		
		content2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
				Uri.parse("http://maps.google.com/maps?saddr="+myLat+","+myLongi+"&daddr="+lat+","+longi));
				startActivity(intent);
			}
		});
		
		content3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	if (!descrMaxLine){
            		tvDescription.setMaxLines(Integer.MAX_VALUE);
            		descrMaxLine = true;
            	} else {
            		tvDescription.setMaxLines(3);
            		descrMaxLine = false;
            	}
			}
	    }); 
		
		tvDay1.setVisibility(View.GONE);
		tvDay2.setVisibility(View.GONE);
		tvDay3.setVisibility(View.GONE);
		tvDay4.setVisibility(View.GONE);
		tvDay5.setVisibility(View.GONE);
		tvDay6.setVisibility(View.GONE);
		
		tvOpentime1.setVisibility(View.GONE);
		tvOpentime2.setVisibility(View.GONE);
		tvOpentime3.setVisibility(View.GONE);
		tvOpentime4.setVisibility(View.GONE);
		tvOpentime5.setVisibility(View.GONE);
		tvOpentime6.setVisibility(View.GONE);
		
		content4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	if (!hoursMaxLine){
            		tvDay1.setVisibility(View.VISIBLE);
            		tvDay2.setVisibility(View.VISIBLE);
            		tvDay3.setVisibility(View.VISIBLE);
            		tvDay4.setVisibility(View.VISIBLE);
            		tvDay5.setVisibility(View.VISIBLE);
            		tvDay6.setVisibility(View.VISIBLE);
            		
            		tvOpentime1.setVisibility(View.VISIBLE);
            		tvOpentime2.setVisibility(View.VISIBLE);
            		tvOpentime3.setVisibility(View.VISIBLE);
            		tvOpentime4.setVisibility(View.VISIBLE);
            		tvOpentime5.setVisibility(View.VISIBLE);
            		tvOpentime6.setVisibility(View.VISIBLE);
            		
            		hoursMaxLine = true;
            	} else {
            		tvDay1.setVisibility(View.GONE);
            		tvDay2.setVisibility(View.GONE);
            		tvDay3.setVisibility(View.GONE);
            		tvDay4.setVisibility(View.GONE);
            		tvDay5.setVisibility(View.GONE);
            		tvDay6.setVisibility(View.GONE);
            		
            		tvOpentime1.setVisibility(View.GONE);
            		tvOpentime2.setVisibility(View.GONE);
            		tvOpentime3.setVisibility(View.GONE);
            		tvOpentime4.setVisibility(View.GONE);
            		tvOpentime5.setVisibility(View.GONE);
            		tvOpentime6.setVisibility(View.GONE);
            		
            		hoursMaxLine = false;
            	}
			}
	    }); 
		
		if (open_time_start != null){
			if (open_time_start.length == 0){
				content4.setVisibility(View.GONE);
				divider3.setVisibility(View.GONE);
			} else if (open_time_start.length > 0){
				tvOpentime0.setTypeface(Fonts.getOpenSansLightItalic(EventsEventsMap.this));
				if (open_time_start[0] == -1 || open_time_start[0] == 0){
					tvOpentime0.setText(TimeCounter.getOpeningTime(open_time_start[0], EventsEventsMap.this));
				} else {
					tvOpentime0.setText(TimeCounter.getOpeningTime(open_time_start[0], EventsEventsMap.this) + " - " + TimeCounter.getOpeningTime(open_time_stop[0], EventsEventsMap.this));
				}
				
				tvOpentime1.setTypeface(Fonts.getOpenSansLightItalic(EventsEventsMap.this));
				if (open_time_start[1] == -1 || open_time_start[1] == 0){
					tvOpentime1.setText(TimeCounter.getOpeningTime(open_time_start[1], EventsEventsMap.this));
				} else {
					tvOpentime1.setText(TimeCounter.getOpeningTime(open_time_start[1], EventsEventsMap.this) + " - " + TimeCounter.getOpeningTime(open_time_stop[1], EventsEventsMap.this));
				}
				
				tvOpentime2.setTypeface(Fonts.getOpenSansLightItalic(EventsEventsMap.this));
				if (open_time_start[2] == -1 || open_time_start[2] == 0){
					tvOpentime2.setText(TimeCounter.getOpeningTime(open_time_start[2], EventsEventsMap.this));
				} else {
					tvOpentime2.setText(TimeCounter.getOpeningTime(open_time_start[2], EventsEventsMap.this) + " - " + TimeCounter.getOpeningTime(open_time_stop[2], EventsEventsMap.this));
				}
				
				tvOpentime3.setTypeface(Fonts.getOpenSansLightItalic(EventsEventsMap.this));
				if (open_time_start[3] == -1 || open_time_start[3] == 0){
					tvOpentime3.setText(TimeCounter.getOpeningTime(open_time_start[3], EventsEventsMap.this));
				} else {
					tvOpentime3.setText(TimeCounter.getOpeningTime(open_time_start[3], EventsEventsMap.this) + " - " + TimeCounter.getOpeningTime(open_time_stop[3], EventsEventsMap.this));
				}
				
				tvOpentime4.setTypeface(Fonts.getOpenSansLightItalic(EventsEventsMap.this));
				if (open_time_start[4] == -1 || open_time_start[4] == 0){
					tvOpentime4.setText(TimeCounter.getOpeningTime(open_time_start[4], EventsEventsMap.this));
				} else {
					tvOpentime4.setText(TimeCounter.getOpeningTime(open_time_start[4], EventsEventsMap.this) + " - " + TimeCounter.getOpeningTime(open_time_stop[4], EventsEventsMap.this));
				}
				
				tvOpentime5.setTypeface(Fonts.getOpenSansLightItalic(EventsEventsMap.this));
				if (open_time_start[5] == -1 || open_time_start[5] == 0){
					tvOpentime5.setText(TimeCounter.getOpeningTime(open_time_start[5], EventsEventsMap.this));
				} else {
					tvOpentime5.setText(TimeCounter.getOpeningTime(open_time_start[5], EventsEventsMap.this) + " - " + TimeCounter.getOpeningTime(open_time_stop[5], EventsEventsMap.this));
				}
				
				tvOpentime6.setTypeface(Fonts.getOpenSansLightItalic(EventsEventsMap.this));
				if (open_time_start[6] == -1 || open_time_start[6] == 0){
					tvOpentime6.setText(TimeCounter.getOpeningTime(open_time_start[6], EventsEventsMap.this));
				} else {
					tvOpentime6.setText(TimeCounter.getOpeningTime(open_time_start[6], EventsEventsMap.this) + " - " + TimeCounter.getOpeningTime(open_time_stop[6], EventsEventsMap.this));
				}
			}
		}
		
		if (longi == 0 && lat == 0){
			content2.setVisibility(View.GONE);
			map.setVisibility(View.GONE);
			divider1.setVisibility(View.GONE);
		}
		
		TaskQueueImage.addTask(new loadThumbImage(), EventsEventsMap.this);
		
		tvStoreName.setText(store_name);
		tvDirectionTo.setText(address);
		tvDirectionTo.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
				Uri.parse("http://maps.google.com/maps?saddr="+myLat+","+myLongi+"&daddr="+lat+","+longi));
				startActivity(intent);
			}
		});
		
		tvDescription.setText(StringLanguageSelector.retrieveString(descr));

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
					
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(website));
					startActivity(i);
				} else {
					Toast.makeText(
							getBaseContext(),
							getString(R.string.Website_link_not_available),
							Toast.LENGTH_SHORT).show();
				}
			}
	    }); 
		
		drawPinPointOnMap();
	}

	class loadThumbImage extends Thread {
	    // This method is called when the thread runs
	    public void run() {
			mHandler.post(new Runnable() {
        		public void run() {
        			Bitmap bitmap = null;
        			if (image_url != null && image_url.contains(".png")){
        				bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(image_url, getBaseContext(), 70);
        			}
        			if (bitmap != null){
        				ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(bitmap), 10));
        			}
        		}
			});
	    }
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
		storeLocation = new CustomPinPoint(drawable, EventsEventsMap.this);
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
            Log.e("helloandroid dialing example", "Call failed", e);
        }
    }
	
	
}
