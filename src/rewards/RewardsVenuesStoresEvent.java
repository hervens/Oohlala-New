package rewards;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.Badge;

import network.ErrorCodeParser;
import network.RetrieveData;
import rewards.RewardsVenuesStoresDeal;
import rewards.RewardsVenuesStoresDetail;
import session.SessionStore;
import user.Profile;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import campuswall.CampusWallImage;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.gotoohlala.R;

import datastorage.CacheInternalStorage;
import datastorage.ConvertDpsToPixels;
import datastorage.FB;
import datastorage.FacebookReauthorize;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import events.EventsEventsDetail;
import events.EventsEventsMap;

public class RewardsVenuesStoresEvent extends Activity {
	
	String event_id, store_logo, title, description, start_time, end_time, address, location, store_id, storeName, phone, email, website, descr, image;
	double lat, longi, myLat, myLongi;
	ImageView ivThumb, ivImage, bgFeaturedBlur;
	TextView tvTitle, tvName, tvAddress, tvDescription, bEventsMap, tvStartTime, tvEndTime;
	Button bAddCalendar, bLikeStore, bDislikeStore, bShareStore, bReportStore, bCheckIn, bMore;
	
	private static final List<String> PERMISSIONS = Arrays.asList("user_about_me", "user_birthday", "email", "publish_stream");
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
	String[] open_time;
	int user_like, user_attend;
	
	Handler mHandler = new Handler();
	
	RelativeLayout content1, content2, content3, content4, content5;
	boolean descrMaxLine = false;
	
	boolean user_fav;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventseventsdetail);
		
		Bundle b = this.getIntent().getExtras();
		event_id = b.getString("event_id");
		title = b.getString("title");
		store_logo = b.getString("store_logo");
		start_time = b.getString("start_time");
		end_time = b.getString("end_time");
		store_id = b.getString("store_id");
		myLat = b.getDouble("lat");
		myLongi = b.getDouble("longi");
		user_like = -1;
		user_like = b.getInt("user_like");
		image = b.getString("image");
		user_attend = b.getInt("user_attend");
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(RewardsVenuesStoresEvent.this));
		
		//--------------- new api ----------------------
		RestClient result = null;
		try {
			result = new Rest.request().execute(Rest.STORE + store_id, Rest.OSESS + Profile.sk, Rest.GET).get();
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
			
			user_fav = store.getBoolean("user_fav");
			storeName = StringLanguageSelector.retrieveString(store.getString("name"));
			address = store.getString("address");
			location = store.getString("location");
			phone = store.getString("phone");
			email = store.getString("email");
			website = store.getString("website");
			lat = store.getDouble("latitude");
			longi = store.getDouble("longitude");
			descr = StringLanguageSelector.retrieveString(store.getString("description"));
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
		
		//---------------------- old api ------------------------
		/*
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("object_id", store_id); //-1 for campus
		params.put("is_franchise", String.valueOf(0));		
		String result = RetrieveData.requestMethod(RetrieveData.GET_STORE_BY_ID, params);
		try {
			JSONArray stores = (new JSONObject(result)).getJSONArray("stores");
			Log.i("stores: ", stores.toString());
			Log.i("store_id: ", store_id);
			
			for (int i = 0; i < stores.length(); i++){
				if (stores.getJSONObject(i).getInt("a") == Integer.valueOf(store_id)){
					storeName = StringLanguageSelector.retrieveString(stores.getJSONObject(i).getString("e"));
					address = stores.getJSONObject(i).getString("k");
					location = stores.getJSONObject(i).getString("A");
					phone = stores.getJSONObject(i).getString("s");
					email = stores.getJSONObject(i).getString("u");
					website = stores.getJSONObject(i).getString("l");
					lat = stores.getJSONObject(i).getDouble("n");
					longi = stores.getJSONObject(i).getDouble("o");
					descr = StringLanguageSelector.retrieveString(stores.getJSONObject(i).getString("f"));
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
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvName = (TextView) findViewById(R.id.tvName);
		tvAddress = (TextView) findViewById(R.id.tvAddress);
		tvDescription = (TextView) findViewById(R.id.tvDescription);
		tvStartTime = (TextView) findViewById(R.id.tvStartTime);
		tvEndTime = (TextView) findViewById(R.id.tvEndTime);
		bLikeStore = (Button) findViewById(R.id.bLikeStore);
		bDislikeStore = (Button) findViewById(R.id.bDislikeStore);
		bShareStore = (Button) findViewById(R.id.bShareStore);
		bReportStore = (Button) findViewById(R.id.bReportStore);
		ivImage = (ImageView) findViewById(R.id.ivImage);
		bCheckIn = (Button) findViewById(R.id.bCheckIn);
		bMore = (Button) findViewById(R.id.bMore);
		bgFeaturedBlur = (ImageView) findViewById(R.id.bgFeaturedBlur);
		
		content1 = (RelativeLayout) findViewById(R.id.content1);
		content2 = (RelativeLayout) findViewById(R.id.content2);
		content3 = (RelativeLayout) findViewById(R.id.content3);
		content4 = (RelativeLayout) findViewById(R.id.content4);
		content5 = (RelativeLayout) findViewById(R.id.content5);
		
		content1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
				Bundle extras = new Bundle();
				extras.putString("image_url", image);
				extras.putInt("picWidth", 0);
				Intent i = new Intent(RewardsVenuesStoresEvent.this, CampusWallImage.class);
				i.putExtras(extras);
				startActivity(i);
			}
	    }); 
		
		if (longi == 0 && lat == 0){
			tvAddress.setVisibility(View.GONE);			
		}
		
		if (store_logo.trim().length() > 0){
			Bitmap bitmap = ImageLoader.thumbImageStoreAndLoad(store_logo, getApplicationContext());
			if (bitmap != null){
				ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(bitmap), 10));
				bgFeaturedBlur.setImageBitmap(bitmap);				
			}
		}
		
		if (user_attend == 0){
			bCheckIn.setBackgroundResource(R.drawable.button_action_attending);
			bCheckIn.setText(getString(R.string._Attending));
			RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
					ConvertDpsToPixels.getPixels(110, getBaseContext()), 
					ConvertDpsToPixels.getPixels(30, getBaseContext()));
			param.addRule(RelativeLayout.RIGHT_OF, ivThumb.getId());
			param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			param.setMargins(0, ConvertDpsToPixels.getPixels(5, getBaseContext()), 0, ConvertDpsToPixels.getPixels(10, getBaseContext()));
			bCheckIn.setLayoutParams(param);
		} else if (TimeCounter.isEventGoingOn(start_time, end_time)){
			bCheckIn.setBackgroundResource(R.drawable.button_action_checkin);
		} else {
			bCheckIn.setClickable(false);
		}
		bCheckIn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (user_attend == 0){
					RestClient result = null;
					try {
						result = new Rest.requestBody().execute(Rest.EVENT + event_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "attend", "1").get();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//Log.i("user attend event", result.getResponse());
					
					int code = result.getResponseCode();
					
					if (code == 200) {
						if (TimeCounter.isEventGoingOn(start_time, end_time)){	
							bCheckIn.setBackgroundResource(R.drawable.button_action_checkin);
							bCheckIn.setText(getString(R.string.Check_In));
							RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
									ConvertDpsToPixels.getPixels(90, getBaseContext()), 
									ConvertDpsToPixels.getPixels(30, getBaseContext()));
							param.addRule(RelativeLayout.RIGHT_OF, ivThumb.getId());
							param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
							param.setMargins(0, ConvertDpsToPixels.getPixels(5, getBaseContext()), 0, ConvertDpsToPixels.getPixels(10, getBaseContext()));
							bCheckIn.setLayoutParams(param);
							user_attend = 1;
						} else {
							bCheckIn.setBackgroundResource(R.drawable.button_action_checkin_disabled);
							bCheckIn.setText(getString(R.string.Check_In));
							RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
									ConvertDpsToPixels.getPixels(90, getBaseContext()), 
									ConvertDpsToPixels.getPixels(30, getBaseContext()));
							param.addRule(RelativeLayout.RIGHT_OF, ivThumb.getId());
							param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
							param.setMargins(0, ConvertDpsToPixels.getPixels(5, getBaseContext()), 0, ConvertDpsToPixels.getPixels(10, getBaseContext()));
							bCheckIn.setLayoutParams(param);
							bCheckIn.setClickable(false);
							user_attend = 1;
						}
					}
				} else if (TimeCounter.isEventGoingOn(start_time, end_time)){
					RestClient result = null;
					try {
						result = new Rest.requestBody().execute(Rest.EVENT + event_id, Rest.OSESS + Profile.sk, Rest.PUT, "3", "checkin", "1", "latitude", String.valueOf(myLat), "longitude", String.valueOf(myLongi)).get();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//Log.i("user attend event", result.getResponse());
					
					int code = result.getResponseCode();
					
					if (code == 200) {
						bCheckIn.setBackgroundResource(R.drawable.button_action_checkin_disabled);
						bCheckIn.setClickable(false);
					}
				}
			}
		});
		
		tvTitle.setText(StringLanguageSelector.retrieveString(title));
		tvName.setText(StringLanguageSelector.retrieveString(storeName));
		tvAddress.setText(StringLanguageSelector.retrieveString(address));
		content3.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
				Uri.parse("http://maps.google.com/maps?saddr="+myLat+","+myLongi+"&daddr="+lat+","+longi));
				startActivity(intent);
			}
		});
		
		//get the full description
		/*
		Hashtable<String, String> params2 = new Hashtable<String, String>();
		params2.put("event_id", event_id);
		String result2 = RetrieveData.requestMethod(RetrieveData.GET_FULL_EVENT_DESCRIPTION, params2);
		Log.i("event full descr: ", result);
		try {
			description = StringLanguageSelector.retrieveString((new JSONObject(result2)).getString("description"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		content4.setOnClickListener(new View.OnClickListener() {
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
		
		tvDescription.setText(StringLanguageSelector.retrieveString(description));
		tvStartTime.setText(TimeCounter.getTime(start_time));
		tvEndTime.setText(getString(R.string.Ends_at_) + TimeCounter.getTime(end_time));
		
		content5.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				Bundle extras = new Bundle();
				extras.putString("event_id", event_id);
				extras.putString("store_name", StringLanguageSelector.retrieveString(storeName));
				extras.putString("location", location);
				extras.putString("store_logo", store_logo);
				extras.putDouble("lat", lat);
				extras.putDouble("longi", longi);
				extras.putString("address", address);
				extras.putString("descr", descr);
				extras.putString("phone", phone);
				extras.putString("email", email);
				extras.putString("website", website);
				extras.putStringArray("open_time", open_time);
				extras.putDouble("myLat", myLat);
				extras.putDouble("myLongi", myLongi);
				extras.putBoolean("user_fav", user_fav);
				extras.putString("store_id", store_id);
				
				Intent i = new Intent(getBaseContext(), EventsEventsMap.class);
				i.putExtras(extras);
				startActivity(i);
				
			}
	    }); 
		
		content2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Calendar cal = Calendar.getInstance();              
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setType("vnd.android.cursor.item/event");
				intent.putExtra("beginTime", getCalendarTime(start_time));
				intent.putExtra("allDay", false);
				//intent.putExtra("rrule", "FREQ=YEARLY");
				intent.putExtra("endTime", getCalendarTime(end_time));
				intent.putExtra("title", StringLanguageSelector.retrieveString(title));
				startActivity(intent);
				
				//Toast.makeText(getApplicationContext(), "Added " + StringLanguageSelector.retrieveString(title) + " in calendar", Toast.LENGTH_SHORT).show();
			}
	    }); 
		
		bMore.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
			}
		});
		
		
		if (user_like == 1){
			bLikeStore.setBackgroundDrawable(getResources().getDrawable(R.drawable.campuswall_button3));
			bLikeStore.setClickable(false);
			bLikeStore.setText(getString(R.string.Liked));
			bLikeStore.setTextColor(getResources().getColorStateList(R.color.white1));
		}
		bLikeStore.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// ---------------------- new api for event like ---------------------------------
				RestClient result = null;
				try {
					result = new Rest.requestBody().execute(Rest.EVENT + event_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "like", "1").get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.i("user like event", result.getResponse());

				bLikeStore.setBackgroundDrawable(getResources().getDrawable(R.drawable.campuswall_button3));
				bLikeStore.setClickable(false);
				bLikeStore.setText(getString(R.string.Liked));
				bLikeStore.setTextColor(getResources().getColorStateList(R.color.white1));
				
				bDislikeStore.setBackgroundDrawable(getResources().getDrawable(R.drawable.campuswall_button_background));
				bDislikeStore.setClickable(true);
				bDislikeStore.setText(getString(R.string.Dislike));
				bDislikeStore.setTextColor(getResources().getColorStateList(R.color.dimgrey3));
			}
	    }); 
		
		if (user_like == 0){
			bDislikeStore.setBackgroundDrawable(getResources().getDrawable(R.drawable.campuswall_button3));
			bDislikeStore.setClickable(false);
			bDislikeStore.setText(getString(R.string.Disliked));
			bDislikeStore.setTextColor(getResources().getColorStateList(R.color.white1));
		}
		bDislikeStore.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// ---------------------- new api for event like ---------------------------------
				RestClient result = null;
				try {
					result = new Rest.requestBody().execute(Rest.EVENT + event_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "like", "0").get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.i("user dislike event", result.getResponse());
				
				bDislikeStore.setBackgroundDrawable(getResources().getDrawable(R.drawable.campuswall_button3));
				bDislikeStore.setClickable(false);
				bDislikeStore.setText(getString(R.string.Disliked));
				bDislikeStore.setTextColor(getResources().getColorStateList(R.color.white1));
				
				bLikeStore.setBackgroundDrawable(getResources().getDrawable(R.drawable.campuswall_button_background));
				bLikeStore.setClickable(true);
				bLikeStore.setText(getString(R.string.Like));
				bLikeStore.setTextColor(getResources().getColorStateList(R.color.dimgrey3));
			}
	    }); 
		
		bShareStore.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("object_id", event_id);
				params.put("object_type", String.valueOf(3));
				//String result = RetrieveData.requestMethod(RetrieveData.USER_SHARE_VENUE_CONTENT, params);
				//Log.i("use share deal", result);
				
				final CharSequence[] items = {getString(R.string.Share_on_Facebook), getString(R.string.Share_by_SMS), getString(R.string.Share_by_Email), getString(R.string.Cancel)};
				
				AlertDialog.Builder builder = new AlertDialog.Builder(RewardsVenuesStoresEvent.this);
				builder.setTitle(getString(R.string.Share));
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	switch(item){
				    	case 0:
				    		Session session = Session.getActiveSession();
				            if (session == null) {
				                if (session == null) {
				                    session = new Session(RewardsVenuesStoresEvent.this);
				                }
				                
				                Session.setActiveSession(session);
				                if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				                    session.openForRead(new Session.OpenRequest(RewardsVenuesStoresEvent.this).setCallback(statusCallback));
				                }
				            } else {
						        if (session.isOpened()) {
						    		shareOnFacebook(getString(R.string.ShareFacebookCaption), 
						    				getString(R.string.just_discovered_) + StringLanguageSelector.retrieveString(title) + getString(R.string._at_) + StringLanguageSelector.retrieveString(storeName), 
											store_logo, 
											getString(R.string.ShareFacebookLink), 
											StringLanguageSelector.retrieveString(description), 
											getString(R.string.ShareFacebookName));
						        } else {
						        	FacebookSessionStart();
						        }
				            }   
				    		break;
				    	case 1:
				    		Intent SMSIntent = new Intent(Intent.ACTION_SEND);
				    		SMSIntent.setType("text/plain");
				    		SMSIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.just_discovered_) + StringLanguageSelector.retrieveString(title) + getString(R.string._at_) + StringLanguageSelector.retrieveString(storeName) + getString(R.string._using_) + getString(R.string.ShareFacebookName));
				    		SMSIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.Join_us_in_Oohlala));
							startActivity(Intent.createChooser(SMSIntent, getString(R.string.Choose_a_SMS_client)));
				    		break;
				    	case 2:
				    		Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setType("plain/text");
                            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
                            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.Join_us_in_Oohlala));
                            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.just_discovered_) + StringLanguageSelector.retrieveString(title) + getString(R.string._at_) + StringLanguageSelector.retrieveString(storeName) + getString(R.string._using_) + getString(R.string.ShareFacebookName));
                            startActivity(Intent.createChooser(emailIntent, getString(R.string.Send_email_via)));
				    		break;
				    	case 3:
				    		break;
				    	}
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
	    }); 
		
		bReportStore.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				final Dialog dialog = new Dialog(RewardsVenuesStoresEvent.this);

				dialog.setContentView(R.layout.rewardsvenuesstoresreport);
				dialog.setTitle(getString(R.string.Report_Problem));

				final EditText etReportProblem = (EditText) dialog.findViewById(R.id.etReportProblem);
				Button bCancel = (Button) dialog.findViewById(R.id.bCancel);
				Button bSend = (Button) dialog.findViewById(R.id.bSend);
				bSend.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						if (etReportProblem.getText().toString().trim().length() > 0){
							Hashtable<String, String> params = new Hashtable<String, String>();
							params.put("store_id", store_id);
							params.put("object_id", event_id);
							params.put("object_type", String.valueOf(3));
							params.put("message", etReportProblem.getText().toString());
							//String result = RetrieveData.requestMethod(RetrieveData.REPORT_VENUE_PROBLEM, params);
							//Log.i("report problem", result);
							dialog.dismiss();
						} else {
							Toast.makeText(getApplicationContext(), getString(R.string.You_can_not_send_a_blank_report), Toast.LENGTH_SHORT).show();
						}
					}
				});
				
				bCancel.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				
				dialog.show();
			}
	    }); 
		
	}
	
	public void FacebookSessionStart() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
	     	session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
		} else {
	    	Session.openActiveSession(this, true, statusCallback);
		}
	}
	
	private class SessionStatusCallback implements Session.StatusCallback {
        public void call(Session session, SessionState state, Exception exception) {
	        if (session.isOpened()) {
	        	shareOnFacebook(getString(R.string.ShareFacebookCaption), 
	    				getString(R.string.just_discovered_) + StringLanguageSelector.retrieveString(title) + getString(R.string._at_) + StringLanguageSelector.retrieveString(storeName), 
	    				store_logo, 
	    				getString(R.string.ShareFacebookLink), 
	    				StringLanguageSelector.retrieveString(description), 
	    				getString(R.string.ShareFacebookName));
	        } else {
	        	FacebookSessionStart();
	        }
        }
    }
	
	public void shareOnFacebook(String caption, String message, String picture, String link, String description, String name) {
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
	        postParams.putString("name", name);
	        postParams.putString("caption", caption);
	        postParams.putString("message", message);
	        postParams.putString("description", description);
	        postParams.putString("link", link);
	        postParams.putString("picture", picture);

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
	                	Log.i("shareOnFacebook - venues", "Success");
	                	Toast.makeText(getApplicationContext(), getString(R.string.Shared_on_Facebook), Toast.LENGTH_SHORT).show();  
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
	
	public long getCalendarTime(String time){
		long millis = Integer.valueOf(time)*1000L;
		return millis;
	}

}
