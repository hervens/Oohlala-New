package rewards;

import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import network.ErrorCodeParser;
import network.RetrieveData;
import session.SessionStore;
import user.Profile;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import datastorage.FB;
import datastorage.FacebookReauthorize;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.StringLanguageSelector;
import datastorage.Rest.request;
import events.EventsEventsDetail;

public class RewardsVenuesStoresDeal extends Activity {
	
	String name, image_url, title, description, expiration, descr, address, location, deal_id, code, store_id, phone, email, website, card_code_name, image;
	String[] open_time;
	double lat, longi, myLat, myLongi;
	ImageView ivThumb, ivImage;
	TextView tvTitle, tvName, tvExpiration, tvDescription, bStoreDetail;
	Button bUseReward, bLikeStore, bDislikeStore, bShareStore, bReportStore;
	int user_like;
	
	private static final List<String> PERMISSIONS = Arrays.asList("user_about_me", "user_birthday", "email", "publish_stream");
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
	Handler mHandler = new Handler();
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rewardsvenuesstoresdeal);
		
		Bundle b = this.getIntent().getExtras();
		name = b.getString("name");
		image_url = b.getString("image_url");
		title = b.getString("title");
		description = b.getString("description");
		expiration = b.getString("expiration");
		descr = b.getString("descr");
		lat = b.getDouble("lat");
		longi = b.getDouble("longi");
		address = b.getString("address");
		deal_id = b.getString("deal_id");
		code = b.getString("code");
		store_id = b.getString("store_id");
		phone = b.getString("phone");
		email = b.getString("email");
		website = b.getString("website");
		card_code_name = b.getString("card_code_name");
		user_like = -1;
		user_like = b.getInt("user_like");
		image = b.getString("image");
		myLat = b.getDouble("myLat");
		myLongi = b.getDouble("myLongi");
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
		
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
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvName = (TextView) findViewById(R.id.tvName);
		tvExpiration = (TextView) findViewById(R.id.tvExpiration);
		tvDescription = (TextView) findViewById(R.id.tvDescription);
		bStoreDetail = (TextView) findViewById(R.id.bStoreDetail);
		bUseReward = (Button) findViewById(R.id.bUseReward);
		bLikeStore = (Button) findViewById(R.id.bLikeStore);
		bDislikeStore = (Button) findViewById(R.id.bDislikeStore);
		bShareStore = (Button) findViewById(R.id.bShareStore);
		bReportStore = (Button) findViewById(R.id.bReportStore);
		ivImage = (ImageView) findViewById(R.id.ivImage);
		
		if (image_url != null && image_url.contains(".png")){
			ivThumb.setImageBitmap(ImageLoader.thumbImageStoreAndLoad(image_url, getBaseContext()));
		}
		
		if (image.trim().length() > 0){
			new loadBitmap().execute();
			
			ivImage.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
					Bundle extras = new Bundle();
					extras.putString("image_url", image);
					extras.putInt("picWidth", 0);
					Intent i = new Intent(RewardsVenuesStoresDeal.this, CampusWallImage.class);
					i.putExtras(extras);
					startActivity(i);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
		    }); 
		}
		
		tvTitle.setText(StringLanguageSelector.retrieveString(title));
		tvName.setText(StringLanguageSelector.retrieveString(name));
		tvExpiration.setText(expiration);
		tvDescription.setText(StringLanguageSelector.retrieveString(description));
		
		bStoreDetail.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Bundle extras = new Bundle();
				extras.putString("name", name);
				extras.putString("image_url", image_url);
				extras.putString("descr", descr);
				extras.putDouble("lat", lat);
				extras.putDouble("longi", longi);
				extras.putString("address", address);
				extras.putString("phone", phone);
				extras.putString("email", email);
				extras.putString("website", website);
				extras.putStringArray("open_time", open_time);
				extras.putString("location", location);
				extras.putDouble("myLat", myLat);
				extras.putDouble("myLongi", myLongi);
				extras.putString("store_id", store_id);
				
				Intent i = new Intent(getBaseContext(), RewardsVenuesStoresDetail.class);
				i.putExtras(extras);
				startActivity(i);
			}
	    }); 
		
		bUseReward.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// ---------------------- new api for use deal ---------------------------------
				RestClient result = null;
				try {
					result = new Rest.requestBody().execute(Rest.DEAL + deal_id, Rest.OSESS + "EpXonC7JHAGg8yNdyZpElAxra8JJboXZ", Rest.PUT, "2", "use", "1", "code", code).get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.i("use deal", result.getResponse());
				
				if (ErrorCodeParser.parser(result.getResponse()) == 0) {
					bUseReward.setClickable(false);
					bUseReward.setText(getString(R.string.Card_Code) + ": " + card_code_name);
					bUseReward.setBackgroundDrawable(RewardsVenuesStoresDeal.this.getResources().getDrawable(R.drawable.button2));
				}
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
				// ---------------------- new api for deal like ---------------------------------
				RestClient result = null;
				try {
					result = new Rest.requestBody().execute(Rest.DEAL + deal_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "like", "1").get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.i("user like deal", result.getResponse());

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
				// ---------------------- new api for deal like ---------------------------------
				RestClient result = null;
				try {
					result = new Rest.requestBody().execute(Rest.DEAL + deal_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "like", "0").get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.i("user dislike deal", result.getResponse());

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
				params.put("object_id", deal_id);
				params.put("object_type", String.valueOf(1));
				//String result = RetrieveData.requestMethod(RetrieveData.USER_SHARE_VENUE_CONTENT, params);
				//Log.i("use share deal", result);
				
				final CharSequence[] items = {getString(R.string.Share_on_Facebook), getString(R.string.Share_by_SMS), getString(R.string.Share_by_Email), getString(R.string.Cancel)};
				
				AlertDialog.Builder builder = new AlertDialog.Builder(RewardsVenuesStoresDeal.this);
				builder.setTitle(getString(R.string.Share));
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	switch(item){
				    	case 0:
				    		Session session = Session.getActiveSession();
				            if (session == null) {
				                if (session == null) {
				                    session = new Session(RewardsVenuesStoresDeal.this);
				                }
				                
				                Session.setActiveSession(session);
				                if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				                    session.openForRead(new Session.OpenRequest(RewardsVenuesStoresDeal.this).setCallback(statusCallback));
				                }
				            } else {
						        if (session.isOpened()) {
						    		shareOnFacebook(getString(R.string.ShareFacebookCaption), 
						    				getString(R.string.just_discovered_) + StringLanguageSelector.retrieveString(title) + getString(R.string._at_) + StringLanguageSelector.retrieveString(name), 
											image_url, 
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
				    		SMSIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.just_discovered_) + StringLanguageSelector.retrieveString(title) + getString(R.string._at_) + StringLanguageSelector.retrieveString(name) + getString(R.string._using_) + getString(R.string.ShareFacebookName));
				    		SMSIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.Join_us_in_Oohlala));
							startActivity(Intent.createChooser(SMSIntent, getString(R.string.Choose_a_SMS_client)));
				    		break;
				    	case 2:
				    		Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setType("plain/text");
                            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
                            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.Join_us_in_Oohlala));
                            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.just_discovered_) + StringLanguageSelector.retrieveString(title) + getString(R.string._at_) + StringLanguageSelector.retrieveString(name) + getString(R.string._using_) + getString(R.string.ShareFacebookName));
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
				
				final Dialog dialog = new Dialog(RewardsVenuesStoresDeal.this);

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
							params.put("object_id", deal_id);
							params.put("object_type", String.valueOf(1));
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
		
		Hashtable<String, String> paramsAnalytics = new Hashtable<String, String>();
		paramsAnalytics.put("deal_id", deal_id);
		//String resultAnalytics = RetrieveData.requestMethod(RetrieveData.DEAL_VIEW, paramsAnalytics);
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
	    				getString(R.string.just_discovered_) + StringLanguageSelector.retrieveString(title) + getString(R.string._at_) + StringLanguageSelector.retrieveString(name), 
	    				image_url, 
	    				getString(R.string.ShareFacebookLink), 
	    				StringLanguageSelector.retrieveString(description), 
	    				getString(R.string.ShareFacebookName));
	        } else {
	        	FacebookSessionStart();
	        }
        }
    }
	
	class loadBitmap extends AsyncTask<Void, Void, Void> {
	    // This method is called when the thread runs
		protected Void doInBackground(Void... params) {
			mHandler.post(new Runnable() {
	    		public void run() {
					Bitmap bitmapImage = null;
					if (image.contains(".jpg")){
						bitmapImage = ImageLoader.superSmallThumbImageStoreAndLoad(image, getBaseContext());
					} else {
						bitmapImage = ImageLoader.thumbImageStoreAndLoad(image, getBaseContext());
					}
			
	    			ivImage.setImageBitmap(bitmapImage);
			    }
			});
			
			return null;
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

}
