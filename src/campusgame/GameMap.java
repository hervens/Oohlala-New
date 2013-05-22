package campusgame;

import inbox.Inbox;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import network.RetrieveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import session.SessionStore;
import user.Profile;

import ManageThreads.TaskQueueImage;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import campusmap.CampusMap;
import campuswall.CampusWallPostInterface;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.UnreadNumCheck;
import com.gotoohlala.R.drawable;
import com.gotoohlala.R.id;
import com.gotoohlala.R.layout;

import datastorage.CacheInternalStorage;
import datastorage.CpuUsage;
import datastorage.FB;
import datastorage.FacebookReauthorize;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.UserLocation;
import datastorage.Rest.request;
import events.EventsEventsDetail;
import events.EventsEventsModel;

@SuppressLint("HandlerLeak")
public class GameMap extends MapActivity {
	
	MapView map;
	TextView tvTime;
	TextView tvPriceDisdance;
	MyLocationOverlay compass;
	MapController controller;
	private int myLat = 0;
	private int myLongi = 0;
	private int priceLat = 0;
	private int priceLongi = 0;
	int x, y;
	int campusX, campusY;
	
	GeoPoint touchedPoint;
	public GeoPoint borderP1;
	public GeoPoint borderP2;
	
	Drawable treasurePic;
	Drawable othersPic;
	Drawable myPic;
	Drawable treasureImage;
	Drawable myImage;
	Drawable myandtreasureImage;
	List<Overlay> overlayList = new Vector<Overlay>();
	Overlay gameborder;
	boolean gameborderDraw = false;
	Overlay gametext;
	CustomPinPoint myPoint;
	CustomPinPoint pricePoint;
	CustomPinPoint otherPoint;
	CustomPinImage myImagePoint;
	CustomPinImage priceImagePoint;
	Overlay priceCircle; 
	
	private int promo_event_id = 0;
	private int last_update_time = 0;
	private int time_until_end = 0;
	private int minimum_distance_to_steal = 0;
	private double PriceDistance = 0;
	
	Timer timer;
	
	JSONObject currentGame;
	Button takePriceB;
	Button shareOnFacebookB;
	private boolean takePriceBShown = false;
	private boolean takePriceBClicked = false;
	private boolean shareOnFacebookBShown = false;
	
	boolean getGPS = false;
	LocationManager mlocManager;
	LocationListener mlocListener;
	updateTimeCounter counter1;
	updateUsersCounter counter2;
	
	boolean getGameUpdateInfoRunning = false;
	boolean counter1on = true;
	boolean counter2on = true;
	
	private static final List<String> PERMISSIONS = Arrays.asList("user_about_me", "user_birthday", "email", "publish_stream");
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
	int game_last_modified_time = 0;
	
	public static CampusWallPostInterface act;
	
	TextView bGameRule;
	String game_rules;
	
	private Handler mHandler = new Handler();
	
	BroadcastReceiver brLogout;
	
	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.gamemap);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		getMyLocation();
		
		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		}); 
		
		bGameRule = (TextView) findViewById(R.id.bGameRule);
		bGameRule.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Bundle extras = new Bundle();
				extras.putString("game_rules", game_rules);
				Intent i = new Intent(GameMap.this, CampusGameShowRule.class);
				i.putExtras(extras);
				startActivity(i);
			}
		});
				
		//content
		Bundle b = this.getIntent().getExtras();
		promo_event_id = b.getInt("promo_event_id");
		game_rules = b.getString("game_rules");
		//Log.i ("promo event info2: ", String.valueOf(promo_event_id));
		
		map = (MapView) findViewById(R.id.mvGameMap);
		map.setBuiltInZoomControls(true);
		
		treasurePic = getResources().getDrawable(R.drawable.placeholder_object_annotation);
		othersPic = getResources().getDrawable(R.drawable.placeholder_annotation);
		myPic = getResources().getDrawable(R.drawable.placeholder_self_annotation);
		
		treasureImage = getResources().getDrawable(R.drawable.prize_annotation_tag);
		myImage = getResources().getDrawable(R.drawable.prize_annotation_you);
		myandtreasureImage = getResources().getDrawable(R.drawable.prize_annotation_you_plus);

		//compass = new MyLocationOverlay(GameMap.this, map);
		overlayList = Collections.synchronizedList(map.getOverlays());
		otherPoint = new CustomPinPoint(othersPic, GameMap.this);
		
		tvTime = (TextView) findViewById(R.id.tvTimeUntilEnd);
		tvPriceDisdance = (TextView) findViewById(R.id.tvPriceDistance);
		
		TaskQueueImage.addTask(new initialMapCenter(), GameMap.this);
		
		//set up the currentGame and add gameborders
		TaskQueueImage.addTask(new getGameInfo(), GameMap.this);
		
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.gotoohlala.profile.ProfileSettings");
        brLogout = new BroadcastReceiver() {
        	@Override
         	public void onReceive(Context context, Intent intent) {
        		Log.d("onReceive","Logout in progress");
            	//At this point you should start the login activity and finish this one
        		finish();
        	}
        };
        registerReceiver(brLogout, intentFilter);
	}
	
	public void onDestroy(){
		super.onDestroy();

		unregisterReceiver(brLogout);
	}
	
	class updateTimeCounter extends CountDownTimer {
        public updateTimeCounter(long millisInFuture, long countDownInterval) {
        	super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            //dialog.dismiss();
        	//Use Intent to Navigate from this activity to another
        	Toast.makeText(getBaseContext(), getString(R.string.The_game_is_over), Toast.LENGTH_SHORT).show();
        	
        	act.refreshAfterPost();
			onBackPressed();		
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub
        	getGameTimeEnd();
        }
    }
	
	class updateUsersCounter extends CountDownTimer {
        public updateUsersCounter(long millisInFuture, long countDownInterval) {
        	super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            //dialog.dismiss();
        	//Use Intent to Navigate from this activity to another
        	Toast.makeText(getBaseContext(), getString(R.string.The_game_is_over), Toast.LENGTH_SHORT).show();
        	
        	act.refreshAfterPost();
			onBackPressed();		
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub
        	setupOverlayList();
        }
    }

	private synchronized void setupOverlayList() {
		// TODO Auto-generated method stub
		
		//placing pinpoint at my location
		runOnUiThread(new Runnable() {
		    public void run() {
				if (!getGPS){
					//when GPS is off, check service provider first then check cached user location
					Criteria crit = new Criteria();
					
					String towers = mlocManager.getBestProvider(crit, false);
					Location loc = mlocManager.getLastKnownLocation(towers);
					
					if (loc != null){
						myLat = (int) (loc.getLatitude() *1E6);
						myLongi = (int) (loc.getLongitude() *1E6);
						CacheInternalStorage.cacheUserLocation(new UserLocation(myLat, myLongi), getApplicationContext()); //cache user info
						
						Log.i("my location Lat", String.valueOf(myLat));
						Log.i("my location Longi", String.valueOf(myLongi));
						
						GeoPoint ourLocationPoint = new GeoPoint(myLat, myLongi);
						OverlayItem overlayItem = new OverlayItem(ourLocationPoint, "My Location", "no");
						if (myPoint != null){
							overlayList.remove(myPoint);
						}
						myPoint = null;
						myPoint = new CustomPinPoint(myPic, GameMap.this);
						myPoint.insertPinPoint(overlayItem);
						overlayList.add(myPoint);
						
						if (!shareOnFacebookBShown){
							if (myImagePoint != null){
								overlayList.remove(myImagePoint);
							}
							myImagePoint = null;
							myImagePoint = new CustomPinImage(myImage, myPic, ourLocationPoint, GameMap.this);
							overlayList.add(myImagePoint);
						} else {
							if (myImagePoint != null){
								overlayList.remove(myImagePoint);
							}
							myImagePoint = null;
							myImagePoint = new CustomPinImage(myandtreasureImage, myPic, ourLocationPoint, GameMap.this);
							overlayList.add(myImagePoint);
						}
					} else if (CacheInternalStorage.getCacheUserLocation(getApplicationContext()) != null){
			      		if (CacheInternalStorage.getCacheUserLocation(getApplicationContext()).lat != 0){
			      			myLat = (int) (CacheInternalStorage.getCacheUserLocation(getApplicationContext()).lat *1E6);
							myLongi = (int) (CacheInternalStorage.getCacheUserLocation(getApplicationContext()).longi *1E6);
							
							Debug.out("my location lat " + String.valueOf(myLat));
							Debug.out("my location longi " + String.valueOf(myLongi));
							
							GeoPoint ourLocationPoint = new GeoPoint(myLat, myLongi);
							OverlayItem overlayItem = new OverlayItem(ourLocationPoint, "My Location", "no");
							if (myPoint != null){
								overlayList.remove(myPoint);
							}
							myPoint = null;
							myPoint = new CustomPinPoint(myPic, GameMap.this);
							myPoint.insertPinPoint(overlayItem);
							overlayList.add(myPoint);	
							
							if (!shareOnFacebookBShown){
								if (myImagePoint != null){
									overlayList.remove(myImagePoint);
								}
								myImagePoint = null;
								myImagePoint = new CustomPinImage(myImage, myPic, ourLocationPoint, GameMap.this);
								overlayList.add(myImagePoint);
							} else {
								if (myImagePoint != null){
									overlayList.remove(myImagePoint);
								}
								myImagePoint = null;
								myImagePoint = new CustomPinImage(myandtreasureImage, myPic, ourLocationPoint, GameMap.this);
								overlayList.add(myImagePoint);
							}
			      		}
					}
		      	} else {
					Debug.out("my location lat " + String.valueOf(myLat));
					Debug.out("my location longi " + String.valueOf(myLongi));
					
					GeoPoint ourLocationPoint = new GeoPoint(myLat, myLongi);
					OverlayItem overlayItem = new OverlayItem(ourLocationPoint, "My Location", "no");
					if (myPoint != null){
						overlayList.remove(myPoint);
					}
					myPoint = null;
					myPoint = new CustomPinPoint(myPic, GameMap.this);
					myPoint.insertPinPoint(overlayItem);
					overlayList.add(myPoint);
					
					if (!shareOnFacebookBShown){
						if (myImagePoint != null){
							overlayList.remove(myImagePoint);
						}
						myImagePoint = null;
						myImagePoint = new CustomPinImage(myImage, myPic, ourLocationPoint, GameMap.this);
						overlayList.add(myImagePoint);
					} else {
						if (myImagePoint != null){
							overlayList.remove(myImagePoint);
						}
						myImagePoint = null;
						myImagePoint = new CustomPinImage(myandtreasureImage, myPic, ourLocationPoint, GameMap.this);
						overlayList.add(myImagePoint);
					}
				}
				
				map.postInvalidate();
				//Debug.out("added my point");
				
				//update game info
				if (!getGameUpdateInfoRunning){
					getGameUpdateInfo(myLat, myLongi);
				}
				
				//check if the priceDistance is less then 50m
				if (PriceDistance*1000 <= minimum_distance_to_steal){
					if (!takePriceBShown && !shareOnFacebookBShown){
						Message m = new Message();
						GameMap.this.showTakePriceButton.sendMessage(m);
						takePriceBShown = true;
					} 
				} else if (takePriceBShown){
					RelativeLayout RL = (RelativeLayout) findViewById(R.id.RLGameMap);
		    		RL.removeView(takePriceB);
		    		takePriceBShown = false;
				}
		    }
		});
	}
	
	class initialMapCenter extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	if (!getGPS){
				//when GPS is off, check service provider first then check cached user location
				Criteria crit = new Criteria();
				
				String towers = mlocManager.getBestProvider(crit, false);
				Location loc = mlocManager.getLastKnownLocation(towers);
				
				if (loc != null){
					myLat = (int) (loc.getLatitude() *1E6);
					myLongi = (int) (loc.getLongitude() *1E6);
					CacheInternalStorage.cacheUserLocation(new UserLocation(myLat, myLongi), getApplicationContext()); //cache user info
					
					Log.i("my location Lat", String.valueOf(myLat));
					Log.i("my location Longi", String.valueOf(myLongi));
				} else if (CacheInternalStorage.getCacheUserLocation(getApplicationContext()) != null){
		      		if (CacheInternalStorage.getCacheUserLocation(getApplicationContext()).lat != 0){
		      			myLat = (int) (CacheInternalStorage.getCacheUserLocation(getApplicationContext()).lat *1E6);
						myLongi = (int) (CacheInternalStorage.getCacheUserLocation(getApplicationContext()).longi *1E6);
						
						Debug.out("my location lat " + String.valueOf(myLat));
						Debug.out("my location longi " + String.valueOf(myLongi));
		      		}
				}
	      	} else {
				Debug.out("my location lat " + String.valueOf(myLat));
				Debug.out("my location longi " + String.valueOf(myLongi));
			}
	    	
			RestClient result = null;
			try {
				result = new Rest.requestBody().execute(Rest.GAME + promo_event_id, Rest.OSESS + Profile.sk, Rest.PUT, "3", "latitude", String.valueOf((double)(myLat/1E6)), "longitude", String.valueOf((double)(myLongi/1E6)), "ver", String.valueOf(RetrieveData.version)).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("initialMapCenter", String.valueOf(result.getResponseCode()) + "-----------------------");
			
			int code = result.getResponseCode();
			if (code == 200){
				try {
					//Debug.out("before getting gameupdateinfo");
					JSONObject gameupdateinfo = new JSONObject(result.getResponse());
					
					//get price location info
					priceLat = (int) (gameupdateinfo.getJSONObject("prize_info").getDouble("latitude") *1E6);
					priceLongi = (int) (gameupdateinfo.getJSONObject("prize_info").getDouble("longitude") *1E6);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//create the map controller, setCenter and zoom
				controller = map.getController();
				GeoPoint startPoint = new GeoPoint(priceLat, priceLongi);
				controller.setCenter(startPoint);
				controller.setZoom(18);
			} else if (code == 403 && result.getResponse().contains("game ended")){
				mHandler.post(new Runnable() {
	        		public void run() {
						Toast.makeText(getBaseContext(), getString(R.string.The_game_is_over), Toast.LENGTH_SHORT).show();
						
						act.refreshAfterPost();
						onBackPressed();
				    }
				});
			} else if (code == 403 && result.getResponse().contains("version too low")){
				mHandler.post(new Runnable() {
	        		public void run() {
						Toast.makeText(getBaseContext(), getString(R.string.version_too_low), Toast.LENGTH_SHORT).show();
						
						act.refreshAfterPost();
						onBackPressed();
				    }
				});
			} else if (code == 403 && result.getResponse().contains("game not started")){
				mHandler.post(new Runnable() {
	        		public void run() {
						Toast.makeText(getBaseContext(), getString(R.string.game_not_started), Toast.LENGTH_SHORT).show();
						
						act.refreshAfterPost();
						onBackPressed();
				    }
				});
			} else if (code == 403 && result.getResponse().contains("user banned")){
				mHandler.post(new Runnable() {
	        		public void run() {
						Toast.makeText(getBaseContext(), getString(R.string.user_banned), Toast.LENGTH_SHORT).show();
						
						act.refreshAfterPost();
						onBackPressed();
				    }
				});
			} else if (code == 403 && result.getResponse().contains("max_submissions")){
				mHandler.post(new Runnable() {
	        		public void run() {
						Toast.makeText(getBaseContext(), getString(R.string.max_submissions), Toast.LENGTH_SHORT).show();
						
						act.refreshAfterPost();
						onBackPressed();
				    }
				});
			} 
	    }
	}
	
	Handler showTakePriceButton = new Handler(){
    	public void handleMessage(Message msg){	
    		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
    				ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
    		p.addRule(RelativeLayout.BELOW, tvPriceDisdance.getId());
    		p.addRule(RelativeLayout.CENTER_HORIZONTAL);
    		p.setMargins(0, 10, 0, 0);
    		takePriceB = new Button(getBaseContext());
    		takePriceB.setText(getString(R.string.Take_the_Prize));
    		takePriceB.setTextSize(17f);
    		takePriceB.setTextColor(Color.WHITE);
    		takePriceB.setBackgroundDrawable(getBaseContext().getResources().getDrawable(R.drawable.button3));
    		takePriceB.setLayoutParams(p);
    		takePriceB.setOnClickListener(new Button.OnClickListener() {  
				public void onClick(View v) {
					// TODO Auto-generated method stub
					TaskQueueImage.addTask(new takeGamePrice(), GameMap.this);
				}
    	    }); 
    		
    		RelativeLayout RL = (RelativeLayout) findViewById(R.id.RLGameMap);
    		RL.addView(takePriceB);
        }
    };
	
	class takeGamePrice extends Thread {
		// This method is called when the thread runs
				
		public void run() {
			// TODO Auto-generated method stub
			RestClient result = null;
			try {
				result = new Rest.requestBody().execute(Rest.GAME + promo_event_id, Rest.OSESS + Profile.sk, Rest.PUT, "4", "latitude", String.valueOf((double)(myLat/1E6)), "longitude", String.valueOf((double)(myLongi/1E6)), "take_prize", "1", "ver", String.valueOf(RetrieveData.version)).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("takeGamePrice", String.valueOf(result.getResponseCode()));
			
			final int code = result.getResponseCode();
			
			runOnUiThread(new Runnable() {
			    public void run() {
					String takePriceMsg = "";
					if (code == 200){
						takePriceMsg = getString(R.string.You_just_took_the_price_successfully);
						
						RelativeLayout RL = (RelativeLayout) findViewById(R.id.RLGameMap);
			    		RL.removeView(takePriceB);
			    		takePriceBClicked = true;
					} else if (code == 403){
						takePriceMsg = getString(R.string.The_game_is_over);
						
						Toast.makeText(getBaseContext(), getString(R.string.The_game_is_over), Toast.LENGTH_SHORT).show();
							
	        			act.refreshAfterPost();
	        			onBackPressed();
					}
					
					//set the take price message to the tvTakePriceMessage textview
					tvPriceDisdance.setText(takePriceMsg);
			    }
			});
		}
	}

	public synchronized void getGameTimeEnd() {
			//display the time left info
			int days = time_until_end/86400;
			
			long millis = time_until_end*1000L;
	        Date date = new Date(millis);
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	        String formattedDate = sdf.format(date);
	       
			Bundle b = new Bundle();
			b.putString("timeleft", getString(R.string.Game_ends_in_) + days + getString(R.string._days_) + formattedDate);
			Message m = new Message();
			m.setData(b);
			this.updateTimeHandler.sendMessage(m);
			
			if (time_until_end <= 0){
				Log.i("game ended", "---------------------");
				Toast.makeText(getBaseContext(), getString(R.string.The_game_is_over), Toast.LENGTH_SHORT).show();
								
				act.refreshAfterPost();
				onBackPressed();		 
			}
				
			time_until_end--;
	}
	
	Handler updateTimeHandler = new Handler(){	
    	public synchronized void handleMessage(Message msg){
    		tvTime.setText(msg.getData().getString("timeleft"));
    		//super.handleMessage(msg);
        }
    };
	
    class getGameInfo extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
			RestClient result = null;
			try {
				result = new request().execute(Rest.GAME + promo_event_id, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (result.getResponseCode() == 200){
				try {
					currentGame = new JSONObject(result.getResponse());
					Log.i("currentGame: ", currentGame.toString());
					
					//setup some update tasks on timer
					try {
						int now = (int) (System.currentTimeMillis()/1000);
						time_until_end = currentGame.getInt("end") - now;
						//time_until_end = 10;  ------------------------------------ test when the game is ended --------------------------------
						
						Log.i("time_until_end", String.valueOf(time_until_end));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//campusX = (int) (currentGame.getDouble("latitude") *1E6);
					//campusY = (int) (currentGame.getDouble("longitude") *1E6);
					
					//add game borders
					JSONArray borderpoints = currentGame.getJSONArray("area_boundary_coord_array");
					if (!gameborderDraw){
						for (int i = 0; i < borderpoints.length(); i++){
							if (i == borderpoints.length()-1){
								borderP1 = new GeoPoint((int) (borderpoints.getJSONArray(i).getDouble(0) *1E6), (int) (borderpoints.getJSONArray(i).getDouble(1) *1E6));
								borderP2 = new GeoPoint((int) (borderpoints.getJSONArray(0).getDouble(0) *1E6), (int) (borderpoints.getJSONArray(0).getDouble(1) *1E6));
							} else {
								borderP1 = new GeoPoint((int) (borderpoints.getJSONArray(i).getDouble(0) *1E6), (int) (borderpoints.getJSONArray(i).getDouble(1) *1E6));
								borderP2 = new GeoPoint((int) (borderpoints.getJSONArray(i+1).getDouble(0) *1E6), (int) (borderpoints.getJSONArray(i+1).getDouble(1) *1E6));
							}
							gameborder = new GameBorder(borderP1, borderP2);
							overlayList.add(gameborder);
						}
						gameborderDraw = true;
					}
					
					//first time set game_last_modified_time
					game_last_modified_time = currentGame.getInt("last_modified_time");
					Log.i("last_modified_time", String.valueOf(game_last_modified_time));
					
					runOnUiThread(new Runnable() {
					    public void run() {
					    	if (time_until_end > 0){
								//setup 2 major counters
								counter1 = new updateTimeCounter((long) time_until_end*1000, 1000);
								counter2 = new updateUsersCounter((long) time_until_end*1000, 5000);
								counter1.start(); 
								counter2.start();		
					    	}
					    }
					});
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    }
	}
		
    public void getGameUpdateInfo(int myLat, int myLongi){
		getGameUpdateInfoRunning = true;
		//Debug.out("getGameUpdateInfoRunning " + String.valueOf(getGameUpdateInfoRunning));
		String gameUpdateResult = null;
		RestClient result = null;
		try {
			result = new Rest.requestBody().execute(Rest.GAME + promo_event_id, Rest.OSESS + Profile.sk, Rest.PUT, "3", "latitude", String.valueOf((double)(myLat/1E6)), "longitude", String.valueOf((double)(myLongi/1E6)), "ver", String.valueOf(RetrieveData.version)).get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//Log.i("user attend event", result.getResponse());
		
		int code = result.getResponseCode();
		if (code == 200){
			gameUpdateResult = result.getResponse();
			
			/*
			getGameUpdateInfoRunning = true;
			//Debug.out("getGameUpdateInfoRunning " + String.valueOf(getGameUpdateInfoRunning));
			String gameUpdateResult = null;
			Hashtable<String, String> params4 = new Hashtable<String, String>();
			if (params4 != null){
				params4.put("latitude", String.valueOf((double)(myLat/1E6)));
				params4.put("longitude", String.valueOf((double)(myLongi/1E6)));
				params4.put("last_update_time", String.valueOf(last_update_time));
				params4.put("promo_event_id", String.valueOf(promo_event_id));
				//Debug.out("before json GAME_UPDATE_LOC");
				gameUpdateResult = RetrieveData.requestMethod(RetrieveData.GAME_UPDATE_LOC, params4);
			}
			//Debug.out(gameUpdateResult);
			//Debug.out("after json GAME_UPDATE_LOC");
			*/
			
			try {
				//Debug.out("before getting gameupdateinfo");
				JSONObject gameupdateinfo = new JSONObject(gameUpdateResult);
				
				//compare game_last_modified_time with the first one you got when you call getgameinfo
				if (game_last_modified_time != 0 && game_last_modified_time != gameupdateinfo.getInt("game_last_modified")){
					//reset the currentGame and gameborders due to the changes in game
					overlayList.clear();
					gameborderDraw = false;
					if (counter1 != null && counter2 != null){
						counter1.cancel();
						counter2.cancel();
					}
					TaskQueueImage.addTask(new getGameInfo(), GameMap.this);
					
					//game_last_modified_time = gameupdateinfo.getInt("game_last_modified_time");
				} else if (game_last_modified_time == 0){
					game_last_modified_time = gameupdateinfo.getInt("game_last_modified");
				}
				Log.i("game_last_modified_time", String.valueOf(gameupdateinfo.getInt("game_last_modified")));
				
				//get all the other users 
				JSONArray userlists = gameupdateinfo.getJSONArray("nearby_players");
				//Debug.out(userlists.toString());
				
				//add other users point
				otherPoint.clearPinPoint();
				overlayList.remove(otherPoint);
				for (int i = 0; i < userlists.length(); i++){
					JSONArray user = userlists.getJSONArray(i);
					int otherLat = (int) (user.getDouble(0) *1E6);
					int otherLongi = (int) (user.getDouble(1) *1E6);
					//Debug.out("others " + i + " " + String.valueOf(otherLat));
					//Debug.out("others " + i + " " + String.valueOf(otherLongi));
					GeoPoint otherLocationPoint = new GeoPoint(otherLat, otherLongi);
					OverlayItem overlayItem2 = new OverlayItem(otherLocationPoint, "others Location", "no");
					otherPoint.insertPinPoint(overlayItem2);
				}
				overlayList.add(otherPoint);
				//Debug.out("added otherPoint");
				
				//Debug.out("before getting price location");
				//get price location info
				priceLat = (int) (gameupdateinfo.getJSONObject("prize_info").getDouble("latitude") *1E6);
				priceLongi = (int) (gameupdateinfo.getJSONObject("prize_info").getDouble("longitude") *1E6);
				//Debug.out(gameupdateinfo.getJSONObject("prize_info").toString());
				
				//Debug.out("before setup priceLocationPoint");
				//create a GeoPoint for pricePoint
				GeoPoint priceLocationPoint = new GeoPoint(priceLat, priceLongi);
				minimum_distance_to_steal = gameupdateinfo.getInt("minimum_distance_to_steal");
				Log.i("minimum_distance_to_steal", String.valueOf(minimum_distance_to_steal));
				//Debug.out("before adding price circle");
				
				//add the price circle
				overlayList.remove(priceCircle);
				priceCircle = null;
				priceCircle = new PriceCircle(priceLocationPoint, minimum_distance_to_steal);
				overlayList.add(priceCircle);
				//Debug.out("added price circle");
				
				if (!shareOnFacebookBShown){
					//add price point
					OverlayItem overlayItem = new OverlayItem(priceLocationPoint, "Treasure Location", "no");
					if (pricePoint != null){
						overlayList.remove(pricePoint);
					}
					pricePoint = null;
					pricePoint = new CustomPinPoint(treasurePic, GameMap.this);
					pricePoint.insertPinPoint(overlayItem);
					overlayList.add(pricePoint);
					//Debug.out("added price point");
					
					if (priceImagePoint != null){
						overlayList.remove(priceImagePoint);
					}
					priceImagePoint = null;
					priceImagePoint = new CustomPinImage(treasureImage, treasurePic, priceLocationPoint, GameMap.this);
					overlayList.add(priceImagePoint);
				} else {
					if (gameupdateinfo.getJSONObject("prize_info").getInt("owner_id") == 0){
						RelativeLayout RL = (RelativeLayout) findViewById(R.id.RLGameMap);
						RL.removeView(shareOnFacebookB);
						shareOnFacebookBShown = false;
						
						//resume price point
						OverlayItem overlayItem = new OverlayItem(priceLocationPoint, "Treasure Location", "no");
						if (pricePoint != null){
							overlayList.remove(pricePoint);
						}
						pricePoint = null;
						pricePoint = new CustomPinPoint(treasurePic, GameMap.this);
						pricePoint.insertPinPoint(overlayItem);
						overlayList.add(pricePoint);
						//Debug.out("added price point");
						
						//resume price image point
						if (priceImagePoint != null){
							overlayList.remove(priceImagePoint);
						}
						priceImagePoint = null;
						priceImagePoint = new CustomPinImage(treasureImage, treasurePic, priceLocationPoint, GameMap.this);
						overlayList.add(priceImagePoint);
						
						//resume only my image point, since my point is already displayed.
						GeoPoint ourLocationPoint = new GeoPoint(myLat, myLongi);
						if (myImagePoint != null){
							overlayList.remove(myImagePoint);
						}
						myImagePoint = null;
						myImagePoint = new CustomPinImage(myImage, myPic, ourLocationPoint, GameMap.this);
						overlayList.add(myImagePoint);
						
						Bundle b = new Bundle();
						b.putString("distance", getString(R.string.You_are_outside_of_the_game_boundary_area_You_can_not_take_the_prize_now));
						Message m = new Message();
						m.setData(b);
						updateDistanceHandler.sendMessage(m);
					} else {
						if (pricePoint != null){
							overlayList.remove(pricePoint);
						}
						if (priceImagePoint != null){
							overlayList.remove(priceImagePoint);
						}
					}
				}
				
				map.postInvalidate();
				
				//last_update_time = gameupdateinfo.getInt("time");
				
				//display the price distance info
				DecimalFormat df = new DecimalFormat("#.#");
				PriceDistance = new CalculateGPDistance(priceLat/1E6, priceLongi/1E6, myLat/1E6, myLongi/1E6).Calulate();
				Bundle b = new Bundle();
				//if the PriceDistance is less than 1000 meters
				if (PriceDistance < 1){
					if (PriceDistance*1000 < minimum_distance_to_steal){
						if (gameupdateinfo.getJSONObject("prize_info").getInt("owner_id") == Profile.userId){
							b.putString("distance", getString(R.string.You_are_holding_the_price_now_run));
							if (!shareOnFacebookBShown){
								showShareOnFacebookButton();
								shareOnFacebookBShown = true;
								
								//change my image point to plus image
								GeoPoint ourLocationPoint = new GeoPoint(myLat, myLongi);
								if (myImagePoint != null){
									overlayList.remove(myImagePoint);
								}
								myImagePoint = null;
								myImagePoint = new CustomPinImage(myandtreasureImage, myPic, ourLocationPoint, GameMap.this);
								overlayList.add(myImagePoint);
								
								//remove both pricePoint and priceImagePoint once you got the prize
								if (pricePoint != null){
									overlayList.remove(pricePoint);
								}
								if (priceImagePoint != null){
									overlayList.remove(priceImagePoint);
								}
							}
						} else if(gameupdateinfo.getInt("user_steal_time_out") > 0){
							b.putString("distance", getString(R.string.Your_price_just_got_stolen_Your_timeout_has_) + gameupdateinfo.getInt("user_steal_time_out") + getString(R.string._s_left));
							if (shareOnFacebookBShown){
								RelativeLayout RL = (RelativeLayout) findViewById(R.id.RLGameMap);
								RL.removeView(shareOnFacebookB);
								shareOnFacebookBShown = false;
							}
							
							//resume price point
							OverlayItem overlayItem = new OverlayItem(priceLocationPoint, "Treasure Location", "no");
							if (pricePoint != null){
								overlayList.remove(pricePoint);
							}
							pricePoint = null;
							pricePoint = new CustomPinPoint(treasurePic, GameMap.this);
							pricePoint.insertPinPoint(overlayItem);
							overlayList.add(pricePoint);
							//Debug.out("added price point");
							
							//resume price image point
							if (priceImagePoint != null){
								overlayList.remove(priceImagePoint);
							}
							priceImagePoint = null;
							priceImagePoint = new CustomPinImage(treasureImage, treasurePic, priceLocationPoint, GameMap.this);
							overlayList.add(priceImagePoint);
							
							//resume only my image point, since my point is already displayed.
							GeoPoint ourLocationPoint = new GeoPoint(myLat, myLongi);
							if (myImagePoint != null){
								overlayList.remove(myImagePoint);
							}
							myImagePoint = null;
							myImagePoint = new CustomPinImage(myImage, myPic, ourLocationPoint, GameMap.this);
							overlayList.add(myImagePoint);
							
							//avoid the situation when once you entered the game, you already got the prize without click the take_the_prize button
							takePriceBShown = true;
						} else {
							b.putString("distance", getString(R.string.The_price_is_only_) + df.format(PriceDistance*1000) + getString(R.string._m_away_click_the_button_to_take_it));
							
							//resume your take price button
							if (takePriceBClicked){
								takePriceBShown = false;
								takePriceBClicked = false;
							}
						}
					} else {
						b.putString("distance", getString(R.string.The_price_is_) + df.format(PriceDistance*1000) + getString(R.string._m_away_be_within_) + minimum_distance_to_steal + getString(R.string._m_to_take_it));
						
						if(gameupdateinfo.getInt("user_steal_time_out") > 0){
							b.putString("distance", getString(R.string.Your_price_just_got_stolen_Your_timeout_has_) + gameupdateinfo.getInt("user_steal_time_out") + getString(R.string._s_left));
							if (shareOnFacebookBShown){
								RelativeLayout RL = (RelativeLayout) findViewById(R.id.RLGameMap);
								RL.removeView(shareOnFacebookB);
								shareOnFacebookBShown = false;
							}
							
							//avoid the situation when once you entered the game, you already got the prize without click the take_the_prize button
							takePriceBShown = true;
						} 
					}
				} else {
					if (PriceDistance*1000 < minimum_distance_to_steal){
						if (gameupdateinfo.getJSONObject("prize_info").getInt("owner_id") == Profile.userId){
							b.putString("distance", getString(R.string.You_are_holding_the_price_now_run));
							if (!shareOnFacebookBShown){
								showShareOnFacebookButton();
								shareOnFacebookBShown = true;
								
								//change my image point to plus image
								GeoPoint ourLocationPoint = new GeoPoint(myLat, myLongi);
								if (myImagePoint != null){
									overlayList.remove(myImagePoint);
								}
								myImagePoint = null;
								myImagePoint = new CustomPinImage(myandtreasureImage, myPic, ourLocationPoint, GameMap.this);
								overlayList.add(myImagePoint);
								
								//remove both pricePoint and priceImagePoint once you got the prize
								if (pricePoint != null){
									overlayList.remove(pricePoint);
								}
								if (priceImagePoint != null){
									overlayList.remove(priceImagePoint);
								}
							}
						} else if(gameupdateinfo.getInt("user_steal_time_out") > 0){
							b.putString("distance", getString(R.string.Your_price_just_got_stolen_Your_timeout_has_) + gameupdateinfo.getInt("user_steal_time_out") + getString(R.string._s_left));
							if (shareOnFacebookBShown){
								RelativeLayout RL = (RelativeLayout) findViewById(R.id.RLGameMap);
								RL.removeView(shareOnFacebookB);
								Log.i("shareOnFacebookBShown", "false---------------");
								shareOnFacebookBShown = false;
							}
							
							//resume price point
							OverlayItem overlayItem = new OverlayItem(priceLocationPoint, "Treasure Location", "no");
							if (pricePoint != null){
								overlayList.remove(pricePoint);
							}
							pricePoint = null;
							pricePoint = new CustomPinPoint(treasurePic, GameMap.this);
							pricePoint.insertPinPoint(overlayItem);
							overlayList.add(pricePoint);
							//Debug.out("added price point");
							
							//resume price image point
							if (priceImagePoint != null){
								overlayList.remove(priceImagePoint);
							}
							priceImagePoint = null;
							priceImagePoint = new CustomPinImage(treasureImage, treasurePic, priceLocationPoint, GameMap.this);
							overlayList.add(priceImagePoint);
							
							//resume only my image point, since my point is already displayed.
							GeoPoint ourLocationPoint = new GeoPoint(myLat, myLongi);
							if (myImagePoint != null){
								overlayList.remove(myImagePoint);
							}
							myImagePoint = null;
							myImagePoint = new CustomPinImage(myImage, myPic, ourLocationPoint, GameMap.this);
							overlayList.add(myImagePoint);
							
							//avoid the situation when once you entered the game, you already got the prize without click the take_the_prize button
							takePriceBShown = true;
						} else {
							b.putString("distance", getString(R.string.The_price_is_only_) + df.format(PriceDistance) + getString(R.string._km_away_click_the_button_to_take_it));
							
							//resume your take price button
							if (takePriceBClicked){
								Log.i("takePriceBShown", "false---------------");
								takePriceBShown = false;
								takePriceBClicked = false;
							}
						}
					} else {
						b.putString("distance", getString(R.string.The_price_is_) + df.format(PriceDistance) + getString(R.string._km_away_be_within_) + minimum_distance_to_steal + getString(R.string._m_to_take_it));
						
						if(gameupdateinfo.getInt("user_steal_time_out") > 0){
							b.putString("distance", getString(R.string.Your_price_just_got_stolen_Your_timeout_has_) + gameupdateinfo.getInt("user_steal_time_out") + getString(R.string._s_left));
							if (shareOnFacebookBShown){
								RelativeLayout RL = (RelativeLayout) findViewById(R.id.RLGameMap);
								RL.removeView(shareOnFacebookB);
								shareOnFacebookBShown = false;
							}
							
							//avoid the situation when once you entered the game, you already got the prize without click the take_the_prize button
							takePriceBShown = true;
						} 
					}
					
					//if switch to km as distance unit ---->
					//b.putString("distance", getString(R.string.The_price_is_) + df.format(PriceDistance) + getString(R.string._km_away_be_within_) + minimum_distance_to_steal + getString(R.string._m_to_take_it));
				}
				
				Message m = new Message();
				m.setData(b);
				updateDistanceHandler.sendMessage(m);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			getGameUpdateInfoRunning = false;
			//Debug.out("getGameUpdateInfoRunning " + String.valueOf(getGameUpdateInfoRunning));
		} else if (code == 403 && result.getResponse().contains("game ended")){
			mHandler.post(new Runnable() {
        		public void run() {
					Toast.makeText(getBaseContext(), getString(R.string.The_game_is_over), Toast.LENGTH_SHORT).show();
					
					act.refreshAfterPost();
					onBackPressed();
			    }
			});
		} else if (code == 403 && result.getResponse().contains("version too low")){
			mHandler.post(new Runnable() {
        		public void run() {
					Toast.makeText(getBaseContext(), getString(R.string.version_too_low), Toast.LENGTH_SHORT).show();
					
					act.refreshAfterPost();
					onBackPressed();
			    }
			});
		} else if (code == 403 && result.getResponse().contains("game not started")){
			mHandler.post(new Runnable() {
        		public void run() {
					Toast.makeText(getBaseContext(), getString(R.string.game_not_started), Toast.LENGTH_SHORT).show();
					
					act.refreshAfterPost();
					onBackPressed();
			    }
			});
		} else if (code == 403 && result.getResponse().contains("user banned")){
			mHandler.post(new Runnable() {
        		public void run() {
					Toast.makeText(getBaseContext(), getString(R.string.user_banned), Toast.LENGTH_SHORT).show();
					
					act.refreshAfterPost();
					onBackPressed();
			    }
			});
		} else if (code == 403 && result.getResponse().contains("max_submissions")){
			mHandler.post(new Runnable() {
        		public void run() {
					Toast.makeText(getBaseContext(), getString(R.string.max_submissions), Toast.LENGTH_SHORT).show();
					
					act.refreshAfterPost();
					onBackPressed();
			    }
			});
		} 
	}
	
	Handler updateDistanceHandler = new Handler() {	
    	public synchronized void handleMessage(Message msg) {	
    		tvPriceDisdance.setText(msg.getData().getString("distance"));
    		//super.handleMessage(msg);
        }
    };
	
	private synchronized void showShareOnFacebookButton() {
		// TODO Auto-generated method stub
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
		p.addRule(RelativeLayout.BELOW, tvPriceDisdance.getId());
		//p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		p.addRule(RelativeLayout.CENTER_HORIZONTAL);
		p.setMargins(0, 10, 0, 0);
		shareOnFacebookB = new Button(getBaseContext());
		shareOnFacebookB.setText(getString(R.string.Share_on_Facebook));
		shareOnFacebookB.setTextSize(17f);
		shareOnFacebookB.setTextColor(Color.WHITE);
		shareOnFacebookB.setBackgroundDrawable(getBaseContext().getResources().getDrawable(R.drawable.button3));
		shareOnFacebookB.setLayoutParams(p);
		shareOnFacebookB.setOnClickListener(new Button.OnClickListener() {  
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Session session = Session.getActiveSession();
		            if (session == null) {
		                if (session == null) {
		                    session = new Session(GameMap.this);
		                }
		                
		                Session.setActiveSession(session);
		                if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
		                    session.openForRead(new Session.OpenRequest(GameMap.this).setCallback(statusCallback));
		                }
		            } else {
						if (session.isOpened()) {
					        if (currentGame != null){
				    	    	JSONObject shareinfo = currentGame.getJSONObject("sharing_info");
				    	    	shareOnFacebook(shareinfo.getString("caption"), shareinfo.getString("message"), shareinfo.getString("icon_url"), shareinfo.getString("url"), shareinfo.getString("description"), shareinfo.getString("url_name"), promo_event_id);   
				    	    }
						} else {
					        FacebookSessionStart();
					   	}
		            }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    }); 
		
		RelativeLayout RL = (RelativeLayout) findViewById(R.id.RLGameMap);
		RL.addView(shareOnFacebookB);
	}
	
	private void FacebookSessionStart() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
	     	session.openForRead(new Session.OpenRequest(GameMap.this).setCallback(statusCallback));
		} else {
	    	Session.openActiveSession(GameMap.this, true, statusCallback);
		}
	}
	
	private class SessionStatusCallback implements Session.StatusCallback {
        public void call(Session session, SessionState state, Exception exception) {
	        if (session.isOpened()) {
	        	if (currentGame != null){
    	    		JSONObject shareinfo;
					try {
						shareinfo = currentGame.getJSONObject("sharing_info");
						shareOnFacebook(shareinfo.getString("caption"), shareinfo.getString("message"), shareinfo.getString("icon_url"), shareinfo.getString("url"), shareinfo.getString("description"), shareinfo.getString("url_name"), promo_event_id);   
	    	    		//"prize_taken_message" is for the price message
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	    	}
	        } else {
	        	FacebookSessionStart();
	        }
        }
    }
	
	public synchronized void shareOnFacebook(String caption, String message, String icon_url, String url, String description, String url_name, int promoEventId) {
		
		Session session = Session.getActiveSession();
	    if (session != null){
	        // Check for publish permissions    
	        List<String> permissions = session.getPermissions();
	        if (!isSubsetOf(PERMISSIONS, permissions)) {
	            Session.NewPermissionsRequest newPermissionsRequest = new Session
	                    .NewPermissionsRequest(this, PERMISSIONS);
	            session.requestNewPublishPermissions(newPermissionsRequest);
	            return;
	        }

	        Bundle postParams = new Bundle();
	        postParams.putString("name", url_name);
	        postParams.putString("caption", caption);
	        postParams.putString("message", message);
	        postParams.putString("description", description);
	        postParams.putString("link", url);
	        postParams.putString("picture", icon_url);

	        Request.Callback callback= new Request.Callback() {
	            public void onCompleted(Response response) {
	                JSONObject graphResponse = response
	                                           .getGraphObject()
	                                           .getInnerJSONObject();
	                String postId = null;
	                try {
	                    postId = graphResponse.getString("id");
	                } catch (JSONException e) {
	                    Log.i("shareOnFacebook", "JSON error "+ e.getMessage());
	                }
	                FacebookRequestError error = response.getError();
	                if (error != null) {
	                	Log.i("shareOnFacebook", "Error"+ error.getErrorMessage());
	                } else {
	                	Log.i("shareOnFacebook", "Success");
	                	
	                	//post the share info to the server
	                	RestClient result = null;
	            		try {
	            			result = new Rest.requestBody().execute(Rest.GAME + promo_event_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "fb_share", "1").get();
	            		} catch (InterruptedException e1) {
	            			// TODO Auto-generated catch block
	            			e1.printStackTrace();
	            		} catch (ExecutionException e1) {
	            			// TODO Auto-generated catch block
	            			e1.printStackTrace();
	            		}
	            		//Log.i("user attend event", result.getResponse());
	            		
	            		int code = result.getResponseCode();
	            		if (code == 200){
	            			Toast.makeText(getApplicationContext(), getString(R.string.Shared_on_Facebook), Toast.LENGTH_SHORT).show(); 
	            		} else if (code == 403 && result.getResponse().contains("game ended")){
	            			mHandler.post(new Runnable() {
	        	        		public void run() {
			        				Toast.makeText(getBaseContext(), getString(R.string.The_game_is_over), Toast.LENGTH_SHORT).show();
			        				
			        				act.refreshAfterPost();
			        				onBackPressed();
	            			    }
	            			});
	        			} else if (code == 403 && result.getResponse().contains("version too low")){
	        				mHandler.post(new Runnable() {
	        	        		public void run() {
			        				Toast.makeText(getBaseContext(), getString(R.string.version_too_low), Toast.LENGTH_SHORT).show();
			        				
			        				act.refreshAfterPost();
			        				onBackPressed();
	        				    }
	        				});
	        			} else if (code == 403 && result.getResponse().contains("game not started")){
	        				mHandler.post(new Runnable() {
	        	        		public void run() {
			        				Toast.makeText(getBaseContext(), getString(R.string.game_not_started), Toast.LENGTH_SHORT).show();
			        				
			        				act.refreshAfterPost();
			        				onBackPressed();
	        				    }
	        				});
	        			} else if (code == 403 && result.getResponse().contains("user banned")){
	        				mHandler.post(new Runnable() {
	        	        		public void run() {
			        				Toast.makeText(getBaseContext(), getString(R.string.user_banned), Toast.LENGTH_SHORT).show();
			        				
			        				act.refreshAfterPost();
			        				onBackPressed();
	        				    }
	        				});
	        			} else if (code == 403 && result.getResponse().contains("max_submissions")){
	        				mHandler.post(new Runnable() {
	        	        		public void run() {
			        				Toast.makeText(getBaseContext(), getString(R.string.max_submissions), Toast.LENGTH_SHORT).show();
			        				
			        				act.refreshAfterPost();
			        				onBackPressed();
	        				    }
	        				});
	        			} 
	        			Log.i("Share result: ----------------", String.valueOf(result.getResponse()));
	                }
	            }
	        };

	        Request request = new Request(session, "me/feed", postParams, 
	                              HttpMethod.POST, callback);

	        RequestAsyncTask task = new RequestAsyncTask(request);
	        task.execute();
	    }
    }

	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
	    for (String string : subset) {
	        if (!superset.contains(string)) {
	            return false;
	        }
	    }
	    return true;
	}
	
	//---------------- convert meters to radius method -------------------------------------------------------------------------------------------
	public static int metersToRadius(float meters, MapView map, double latitude) {
	    return (int) (map.getProjection().metersToEquatorPixels(meters) * (1/ Math.cos(Math.toRadians(latitude))));         
	}
	//--------------------------------------------------------------------------------------------------------------------------------------------
	
	// --------------- debug method --------------------------------------------------------------------------------------------------------------
	public final static class Debug{
        private Debug (){}

        public static void out (String msg){
            Log.i ("campus location", msg);
        }
    }
	
	private void getMyLocation() {
		// TODO Auto-generated method stub
		mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
	}
	
	public class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
				/*
				 *  TEST FOR FAKE LOCAION
				 */
			
				//myLat = priceLat;
				//myLongi = priceLongi;
				//myLat -= 100;
				//myLongi += 100;
				
				myLat = (int) (loc.getLatitude() *1E6);
				myLongi = (int) (loc.getLongitude() *1E6);
				CacheInternalStorage.cacheUserLocation(new UserLocation(loc.getLatitude(), loc.getLongitude()), getApplicationContext()); //cache user info
				
				String Text = "My current location is: " + "Latitud = " + loc.getLatitude() + "Longitud = " + loc.getLongitude();
				//Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT).show();					
				
				getGPS = true;
		}
	
	
		public void onProviderDisabled(String provider){
			Toast.makeText(getApplicationContext(), getString(R.string.Gps_disabled_please_turn_it_on_to_play_the_game), Toast.LENGTH_SHORT).show();
			getGPS = false;
		}
	
	
		public void onProviderEnabled(String provider){
			//Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
		}
	
	
		public void onStatusChanged(String provider, int status, Bundle extras){
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
		
		if (!counter1on){
			TaskQueueImage.addTask(new getGameInfo(), GameMap.this);
		}
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		//compass.disableCompass();
		super.onPause();
		
		mlocManager.removeUpdates(mlocListener);
		
		if (counter1 != null && counter2 != null){
			counter1.cancel();
			counter2.cancel();
		}
		counter1on = false;
		counter2on = false;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public class synchronizedList extends Vector<Overlay>{
		
		public synchronized boolean remove(Overlay ob){
			super.remove(ob);
			return true;
		}
		
		public synchronized boolean add(Overlay ob){
			super.add(ob);
			return true;
		}
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
	
}
