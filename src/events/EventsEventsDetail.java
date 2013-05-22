package events;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import launchOohlala.CheckEmail;
import launchOohlala.Login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.Badge;
import profile.ModifyProfile;
import profile.ProfileSettings;

import network.ErrorCodeParser;
import network.RetrieveData;
import rewards.RewardsVenuesStoresDeal;
import rewards.RewardsVenuesStoresDetail;
import session.SessionStore;
import user.Profile;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import campusgame.CampusGameRules;
import campuswall.CampusWallComment;
import campuswall.CampusWallImage;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;

import datastorage.CacheInternalStorage;
import datastorage.ConvertDpsToPixels;
import datastorage.FB;
import datastorage.FacebookReauthorize;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.LoginState;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import datastorage.UserLoginInfo;
import datastorage.Rest.request;
import events.EventsEvents.getEventThread;

public class EventsEventsDetail extends Activity {
	String event_id, store_logo, title, description, start_time, end_time, address, location, store_id, storeName, phone, email, website, descr, image;
	int[] open_time_start;
	int[] open_time_stop;
	double lat, longi, myLat, myLongi;
	ImageView ivThumb, ivImage, bgFeaturedBlur;
	TextView tvTitle, tvName, tvAddress, tvDescription, tvStartTime, tvEndTime;
	Button bAddCalendar, bLikeStore, bDislikeStore, bShareStore, bReportStore, bCheckIn, bMore;
	int user_like, user_attend;
	
	private static final List<String> PERMISSIONS = Arrays.asList("user_about_me", "user_birthday", "email", "publish_stream");
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
	Handler mHandler = new Handler();
	
	RelativeLayout content1, content2, content3, content4, content5;
	boolean descrMaxLine = false;
	
	int badge_id, badge_id_unlock, user_checkin, attends;
	String badge_icon_thumb_url, badge_name_unlock;
	JSONObject badge_info, badge_info_unlock;
	
	ImageView ivBadgeImage, ivBadgeIcon;
	TextView tvBadgeName;
	RelativeLayout rlBadgeBg;
	
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
		user_like = -1;
		user_like = b.getInt("user_like");
		image = b.getString("image");
		user_attend = b.getInt("user_attend");
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				ivThumb.setImageBitmap(null);	
				bgFeaturedBlur.setImageBitmap(null);
				
				onBackPressed();
			}
	    }); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(EventsEventsDetail.this));
		
		if (CacheInternalStorage.getCacheUserLocation(getApplicationContext()) != null){
      		if (CacheInternalStorage.getCacheUserLocation(getApplicationContext()).lat != 0){
      			myLat = CacheInternalStorage.getCacheUserLocation(getApplicationContext()).lat;
				myLongi = CacheInternalStorage.getCacheUserLocation(getApplicationContext()).longi;
      		}
      	}
		
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
		ivBadgeImage = (ImageView) findViewById(R.id.ivBadgeImage);
		tvBadgeName = (TextView) findViewById(R.id.tvBadgeName);
		rlBadgeBg = (RelativeLayout) findViewById(R.id.rlBadgeBg);
		ivBadgeIcon = (ImageView) findViewById(R.id.ivBadgeIcon);

		content1 = (RelativeLayout) findViewById(R.id.content1);
		content2 = (RelativeLayout) findViewById(R.id.content2);
		content3 = (RelativeLayout) findViewById(R.id.content3);
		content4 = (RelativeLayout) findViewById(R.id.content4);
		content5 = (RelativeLayout) findViewById(R.id.content5);
		
		TaskQueueImage.addTask(new loadThumbImage(), EventsEventsDetail.this);
		TaskQueueImage.addTask(new loadEventDetails(), EventsEventsDetail.this);
		TaskQueueImage.addTask(new loadEventDetails2(), EventsEventsDetail.this);
		TaskQueueImage.addTask(new loadEventDetails3(), EventsEventsDetail.this);
		
		tvTitle.setText(StringLanguageSelector.retrieveString(title));	
		tvStartTime.setText(TimeCounter.getTime(start_time));
		tvEndTime.setText(getString(R.string.Ends_at_) + TimeCounter.getTime(end_time));
		
		/*
		content1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
				Bundle extras = new Bundle();
				extras.putString("image_url", image);
				extras.putInt("picWidth", 512);
				Intent i = new Intent(EventsEventsDetail.this, CampusWallImage.class);
				i.putExtras(extras);
				startActivity(i);
			}
	    });
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
		
		content2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				showCalendarAlertDialog();
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
				
				//------------------------ old api for event like ------------------
				/*
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("type", String.valueOf(3));
				params.put("id", event_id);
				params.put("is_dislike", String.valueOf(0));
				String result = RetrieveData.requestMethod(RetrieveData.USER_LIKE_STORE, params);
				Log.i("user like event", result);
				*/
				if (result.getResponseCode() == 200){
					bLikeStore.setBackgroundDrawable(getResources().getDrawable(R.drawable.campuswall_button3));
					bLikeStore.setClickable(false);
					bLikeStore.setText(getString(R.string.Liked));
					bLikeStore.setTextColor(getResources().getColorStateList(R.color.white1));
					
					bDislikeStore.setBackgroundDrawable(getResources().getDrawable(R.drawable.campuswall_button_background));
					bDislikeStore.setClickable(true);
					bDislikeStore.setText(getString(R.string.Dislike));
					bDislikeStore.setTextColor(getResources().getColorStateList(R.color.dimgrey3));
				}
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
				// ---------------------- new api for event ---------------------------------
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
				
				//----------------------- old api--------------------------
				/*
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("type", String.valueOf(3));
				params.put("id", event_id);
				params.put("is_dislike", String.valueOf(1));
				String result = RetrieveData.requestMethod(RetrieveData.USER_LIKE_STORE, params);
				Log.i("user dislike event", result);
				*/
				if (result.getResponseCode() == 200){
					bDislikeStore.setBackgroundDrawable(getResources().getDrawable(R.drawable.campuswall_button3));
					bDislikeStore.setClickable(false);
					bDislikeStore.setText(getString(R.string.Disliked));
					bDislikeStore.setTextColor(getResources().getColorStateList(R.color.white1));
					
					bLikeStore.setBackgroundDrawable(getResources().getDrawable(R.drawable.campuswall_button_background));
					bLikeStore.setClickable(true);
					bLikeStore.setText(getString(R.string.Like));
					bLikeStore.setTextColor(getResources().getColorStateList(R.color.dimgrey3));
				}
			}
	    });  
		
		bReportStore.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				final Dialog dialog = new Dialog(EventsEventsDetail.this);

				dialog.setContentView(R.layout.rewardsvenuesstoresreport);
				dialog.setTitle(getString(R.string.Report_Problem));

				final EditText etReportProblem = (EditText) dialog.findViewById(R.id.etReportProblem);
				Button bCancel = (Button) dialog.findViewById(R.id.bCancel);
				Button bSend = (Button) dialog.findViewById(R.id.bSend);
				bSend.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						if (etReportProblem.getText().toString().trim().length() > 0){
							/*
							Hashtable<String, String> params = new Hashtable<String, String>();
							params.put("store_id", store_id);
							params.put("object_id", event_id);
							params.put("object_type", String.valueOf(3));
							params.put("message", etReportProblem.getText().toString());
							String result = RetrieveData.requestMethod(RetrieveData.REPORT_VENUE_PROBLEM, params);
							Log.i("report problem", result);
							*/
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
	                	Log.i("shareOnFacebook", "Success");
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
	
	private class loadOriginalImage extends AsyncTask<String, Void, Void> {
    	
		@Override
		protected Void doInBackground(String... badge_icon_url) {
			if (badge_icon_url[0] != null){
				final Bitmap bitmap = ImageLoader.originalImageStoreAndLoad(badge_icon_url[0], EventsEventsDetail.this);
				
				runOnUiThread(new Runnable() {
					public void run() {
						if (bitmap != null){
							ivBadgeImage.setImageBitmap(bitmap);
						}
					}
				});
			}
			
			return null;
		}
    }
	
	private void showCalendarAlertDialog() {
		// TODO Auto-generated method stub
    	AlertDialog alert = new AlertDialog.Builder(EventsEventsDetail.this).create();
		alert.setMessage(getString(R.string.Add_into_your_calendar));
		alert.setButton(getString(R.string.No), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		alert.setButton2(getString(R.string.Yes), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Calendar cal = Calendar.getInstance();              
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setType("vnd.android.cursor.item/event");
				intent.putExtra("beginTime", getCalendarTime(start_time));
				intent.putExtra("allDay", false);
				//intent.putExtra("rrule", "FREQ=YEARLY");
				intent.putExtra("endTime", getCalendarTime(end_time));
				intent.putExtra("title", StringLanguageSelector.retrieveString(title));
				startActivity(intent);
			}
		});
		alert.show();
	}
	
	class loadEventDetails extends Thread {
	    // This method is called when the thread runs
	    public void run() {
	    	//--------------- new api for store ----------
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
			
			mHandler.post(new Runnable() {
	    		public void run() {
	    			content5.setOnClickListener(new Button.OnClickListener() {
	    				public void onClick(View v) {
	    					Bundle extras = new Bundle();
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
	    			
					if (longi == 0 && lat == 0){
						tvAddress.setVisibility(View.GONE);			
					}
					
					tvName.setText(StringLanguageSelector.retrieveString(storeName));
					tvAddress.setText(address);
					content3.setOnClickListener(new Button.OnClickListener() {
						public void onClick(View v) {
							Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
							Uri.parse("http://maps.google.com/maps?saddr="+myLat+","+myLongi+"&daddr="+lat+","+longi));
							startActivity(intent);
						}
					});

					bShareStore.setOnClickListener(new Button.OnClickListener() {
						public void onClick(View v) {
							final CharSequence[] items = {getString(R.string.Share_on_Facebook), getString(R.string.Share_by_SMS), getString(R.string.Share_by_Email), getString(R.string.Cancel)};
							
							AlertDialog.Builder builder = new AlertDialog.Builder(EventsEventsDetail.this);
							builder.setTitle(getString(R.string.Share));
							builder.setItems(items, new DialogInterface.OnClickListener() {
							    public void onClick(DialogInterface dialog, int item) {
							    	switch(item){
							    	case 0:
							    		Session session = Session.getActiveSession();
							            if (session == null) {
							                if (session == null) {
							                    session = new Session(EventsEventsDetail.this);
							                }
							                
							                Session.setActiveSession(session);
							                if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
							                    session.openForRead(new Session.OpenRequest(EventsEventsDetail.this).setCallback(statusCallback));
							                }
							            } else {
									        if (session.isOpened()) {
								    	    	shareOnFacebook(getString(R.string.ShareFacebookCaption), 
														getString(R.string.just_discovered_) + StringLanguageSelector.retrieveString(title) + getString(R.string._at_) + storeName, 
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
							    		SMSIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.just_discovered_) + StringLanguageSelector.retrieveString(title) + getString(R.string._at_) + storeName + getString(R.string._using_) + getString(R.string.ShareFacebookName));
							    		SMSIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.Join_us_in_Oohlala));
										startActivity(Intent.createChooser(SMSIntent, getString(R.string.Choose_a_SMS_client)));
							    		break;
							    	case 2:
							    		Intent emailIntent = new Intent(Intent.ACTION_SEND);
			                            emailIntent.setType("plain/text");
			                            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
			                            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.Join_us_in_Oohlala));
			                            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.just_discovered_) + StringLanguageSelector.retrieveString(title) + getString(R.string._at_) + storeName + getString(R.string._using_) + getString(R.string.ShareFacebookName));
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
	    		}
			});
	    }
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
	    				getString(R.string.just_discovered_) + StringLanguageSelector.retrieveString(title) + getString(R.string._at_) + storeName, 
	    				store_logo, 
	    				getString(R.string.ShareFacebookLink), 
	    				StringLanguageSelector.retrieveString(description), 
	    				getString(R.string.ShareFacebookName));
	        } else {
	        	FacebookSessionStart();
	        }
        }
    }
	
	class loadEventDetails2 extends Thread {
	    // This method is called when the thread runs
	    public void run() {
	    	RestClient result3 = null;
			try {
				result3 = new request().execute(Rest.EVENT + event_id, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				attends = new JSONObject(result3.getResponse()).getInt("attends");
				user_checkin = new JSONObject(result3.getResponse()).getInt("user_checkin");
				user_like = new JSONObject(result3.getResponse()).getInt("user_like");
				
				badge_info = (JSONObject) new JSONObject(result3.getResponse()).getJSONArray("badge_info").get(0);
				badge_id = badge_info.getInt("badge_id");
				badge_icon_thumb_url = badge_info.getString("badge_icon_thumb_url");
				Log.i("badge_icon_thumb_url", badge_icon_thumb_url);
				
				Bitmap bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(badge_icon_thumb_url, getApplicationContext(), 30);
				if (bitmap != null){
					ivBadgeIcon.setImageBitmap(ImageLoader.ImageCrop(bitmap));
					
					RestClient result2 = null;
					try {
						result2 = new request().execute(Rest.BADGE + badge_id, Rest.OSESS + Profile.sk, Rest.GET).get();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Log.i("badge_id", result2.getResponse());
					
					try {
						String badge_name = new JSONObject(result2.getResponse()).getString("name");
						String badge_icon_url = new JSONObject(result2.getResponse()).getString("badge_icon_url");
						String description = new JSONObject(result2.getResponse()).getString("description");
						
						tvBadgeName.setText(badge_name);
						new loadOriginalImage().execute(badge_icon_url);
						
						ivBadgeIcon.setOnClickListener(new Button.OnClickListener() {
							public void onClick(View v) {
								rlBadgeBg.setVisibility(View.VISIBLE);
								rlBadgeBg.setOnClickListener(new Button.OnClickListener() {
									public void onClick(View v) {
										rlBadgeBg.setVisibility(View.GONE);
									}
								});
							}
						});
					} catch (JSONException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			mHandler.post(new Runnable() {
	    		public void run() {
					if (longi == 0 && lat == 0){
						tvAddress.setVisibility(View.GONE);			
					}
					
					if (TimeCounter.isEventGoingOn(start_time, end_time)){
						if (user_checkin == 0){
							bCheckIn.setBackgroundResource(R.drawable.button_action_checkin);
							bCheckIn.setText(getString(R.string.Check_In));
							RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
									ConvertDpsToPixels.getPixels(90, getBaseContext()), 
									ConvertDpsToPixels.getPixels(30, getBaseContext()));
							param.addRule(RelativeLayout.RIGHT_OF, ivThumb.getId());
							param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
							param.setMargins(0, ConvertDpsToPixels.getPixels(5, getBaseContext()), 0, ConvertDpsToPixels.getPixels(10, getBaseContext()));
							bCheckIn.setLayoutParams(param);
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
						}
					} else if (user_attend == 0){
						bCheckIn.setBackgroundResource(R.drawable.button_action_attending);
						bCheckIn.setText(getString(R.string._Attending));
						RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
								ConvertDpsToPixels.getPixels(110, getBaseContext()), 
								ConvertDpsToPixels.getPixels(30, getBaseContext()));
						param.addRule(RelativeLayout.RIGHT_OF, ivThumb.getId());
						param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
						param.setMargins(0, ConvertDpsToPixels.getPixels(5, getBaseContext()), 0, ConvertDpsToPixels.getPixels(10, getBaseContext()));
						bCheckIn.setLayoutParams(param);
					} else if (user_attend == 1){
						bCheckIn.setBackgroundResource(R.drawable.button_attended);
						bCheckIn.setPadding(70, 0, 0, 0);
						bCheckIn.setTextColor(getResources().getColorStateList(R.color.black1));
						//bCheckIn.setText(attends + getString(R.string._Attending));
						bCheckIn.setText(getString(R.string._Attending));
						RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
								ConvertDpsToPixels.getPixels(110, getBaseContext()), 
								ConvertDpsToPixels.getPixels(30, getBaseContext()));
						param.addRule(RelativeLayout.RIGHT_OF, ivThumb.getId());
						param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
						param.setMargins(0, ConvertDpsToPixels.getPixels(5, getBaseContext()), 0, ConvertDpsToPixels.getPixels(10, getBaseContext()));
						bCheckIn.setLayoutParams(param);
						bCheckIn.setClickable(false);
					}
					bCheckIn.setOnClickListener(new Button.OnClickListener() {
						public void onClick(View v) {
							if (TimeCounter.isEventGoingOn(start_time, end_time)){
								if (user_checkin == 0){
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
									Log.i("user checkin event", result.getResponse());
									
									int code = result.getResponseCode();
									
									if (code == 200) {
										bCheckIn.setBackgroundResource(R.drawable.button_action_checkin_disabled);
										bCheckIn.setClickable(false);
										user_checkin = 1;
										
										try {
											badge_info_unlock = (JSONObject) new JSONObject(result.getResponse()).getJSONArray("user_badge_unlock_info").get(0);
											badge_id_unlock = badge_info_unlock.getInt("badge_id");
											badge_name_unlock = badge_info_unlock.getString("name");
											Log.i("badge_name_unlock", badge_name_unlock);
											
											RestClient result2 = null;
											try {
												result2 = new request().execute(Rest.BADGE + badge_id_unlock, Rest.OSESS + Profile.sk, Rest.GET).get();
											} catch (InterruptedException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											} catch (ExecutionException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
											
											try {
												String badge_name = new JSONObject(result2.getResponse()).getString("name");
												String badge_icon_url = new JSONObject(result2.getResponse()).getString("badge_icon_url");
												String description = new JSONObject(result2.getResponse()).getString("description");
												
												rlBadgeBg.setVisibility(View.VISIBLE);
												tvBadgeName.setText(badge_name);
												new loadOriginalImage().execute(badge_icon_url);
												rlBadgeBg.setOnClickListener(new Button.OnClickListener() {
													public void onClick(View v) {
														rlBadgeBg.setVisibility(View.GONE);
													}
												});
											} catch (JSONException e2) {
												// TODO Auto-generated catch block
												e2.printStackTrace();
											}
										} catch (JSONException e2) {
											// TODO Auto-generated catch block
											e2.printStackTrace();
										}
									} else {
										//event check in
										Toast.makeText(v.getContext(), getString(R.string.checkin_error_dialog_alert), Toast.LENGTH_SHORT).show();
									}
								}
							} else if (user_attend == 0){
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
								Log.i("user attend event", result.getResponse());
								
								int code = result.getResponseCode();
								
								if (code == 200) {
									bCheckIn.setBackgroundResource(R.drawable.button_attended);
									bCheckIn.setPadding(70, 0, 0, 0);
									bCheckIn.setTextColor(getResources().getColorStateList(R.color.black1));
									//bCheckIn.setText(attends + getString(R.string._Attending));
									bCheckIn.setText(getString(R.string._Attending));
									
									bCheckIn.setClickable(false);
									user_attend = 1;
								}
							} 
						}
					});
	    		}
			});
	    }
	}
	
	class loadEventDetails3 extends Thread {
	    // This method is called when the thread runs
	    public void run() {
	    	//get the full description
			RestClient result4 = null;
			try {
				result4 = new request().execute(Rest.EVENT + event_id, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				description = StringLanguageSelector.retrieveString(new JSONObject(result4.getResponse()).getString("description"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mHandler.post(new Runnable() {
	    		public void run() {
	    			tvDescription.setText(StringLanguageSelector.retrieveString(description));
	    		}
			});
	    }
	}
	
	class loadThumbImage extends Thread {
	    // This method is called when the thread runs
	    public void run() {
			mHandler.post(new Runnable() {
        		public void run() {
        			if (image.trim().length() > 0){
        				Bitmap bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(image, getApplicationContext(), 70);
        				if (bitmap != null){
        					ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(bitmap), 10));	
        					bgFeaturedBlur.setImageBitmap(bitmap);
        				}
        			} else if (store_logo.trim().length() > 0){
        				Bitmap bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(store_logo, getApplicationContext(), 70);
        				if (bitmap != null){
        					ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(bitmap), 10));	
        					bgFeaturedBlur.setImageBitmap(bitmap);
        				}
        			}
				}
			});
		}
    }

}
