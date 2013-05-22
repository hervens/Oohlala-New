package campusgame;

import inbox.Inbox;

import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import network.RetrieveData;

import org.json.JSONException;
import org.json.JSONObject;

import session.SessionStore;
import user.Profile;

import campusmap.CampusMap;
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
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.UnreadNumCheck;

import datastorage.FB;
import datastorage.FacebookReauthorize;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CampusGameRules extends Activity {

	String caption_facebookShare, message_facebookShare, icon_url_facebookShare, url_facebookShare, description_facebookShare, url_name_facebookShare, game_video_url, game_image_url, game_name, game_rules, description;
	int event_id_facebookShare, time_until_start;
	
    private static final List<String> PERMISSIONS = Arrays.asList("user_about_me", "user_birthday", "email", "publish_stream");
	
	int start, end, user_submissions, max_submissions, game_type;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campusgamerules);
		
		Bundle b = this.getIntent().getExtras();
		event_id_facebookShare = b.getInt("event_id_facebookShare");
		time_until_start = b.getInt("time_until_start");
		caption_facebookShare = b.getString("caption_facebookShare");
		message_facebookShare = b.getString("message_facebookShare");
		icon_url_facebookShare = b.getString("icon_url_facebookShare");
		url_facebookShare = b.getString("url_facebookShare");
		description_facebookShare = b.getString("description_facebookShare");
		url_name_facebookShare = b.getString("url_name_facebookShare");
		game_video_url = b.getString("game_video_url");
		game_image_url = b.getString("game_image_url");
		game_name = b.getString("game_name");
		game_rules = b.getString("game_rules");
		description = b.getString("description");
		
		if (b.containsKey("game_type")){
			game_type = b.getInt("game_type");
			start = b.getInt("start");
			end = b.getInt("end");
			user_submissions = b.getInt("user_submissions");
			max_submissions = b.getInt("max_submissions");
		}

		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
        
        //content
        TextView tvGameRules = (TextView) findViewById(R.id.tvGameRules);
        Button bAgree = (Button) findViewById(R.id.bAgree);
        
        tvGameRules.setText(game_rules);
        
        bAgree.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (game_type == 3){
					Bundle extras = new Bundle();
    				extras.putString("caption_facebookShare", caption_facebookShare);
					extras.putString("message_facebookShare", message_facebookShare);
					extras.putString("icon_url_facebookShare", icon_url_facebookShare);
					extras.putString("url_facebookShare", url_facebookShare);
					extras.putString("description_facebookShare", description_facebookShare);
					extras.putString("url_name_facebookShare", url_name_facebookShare);
					extras.putInt("event_id_facebookShare", event_id_facebookShare);
					extras.putInt("start", start);
					extras.putInt("end", end);
    				extras.putString("game_image_url", game_image_url);
    				extras.putString("game_name", game_name);
    				extras.putInt("user_submissions", user_submissions);
    				extras.putInt("max_submissions", max_submissions);
    				extras.putString("game_rules", game_rules);
    				extras.putString("description", description);
    				
    				Intent i = new Intent(CampusGameRules.this, CampusGameType3.class);
    				i.putExtras(extras);
    				startActivity(i);
				} else {
					Session session = Session.getActiveSession();
		            if (session == null) {
		                if (session == null) {
		                    session = new Session(CampusGameRules.this);
		                }
		                
		                Session.setActiveSession(session);
		                if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
		                    session.openForRead(new Session.OpenRequest(CampusGameRules.this).setCallback(statusCallback));
		                }
		            } else {
				        if (session.isOpened()) {
				        	shareOnFacebook(caption_facebookShare, 
			    					message_facebookShare, 
			    					icon_url_facebookShare, 
			    					url_facebookShare, 
			    					description_facebookShare, 
			    					url_name_facebookShare, 
			    					event_id_facebookShare);
			    	    	
			    	    	if (time_until_start <= 0){
				    	    	Bundle extras = new Bundle();
								extras.putInt("promo_event_id", event_id_facebookShare);
								extras.putString("game_rules", game_rules);
								Intent i = new Intent(CampusGameRules.this, GameMap.class);
								i.putExtras(extras);
								startActivity(i);
			    	    	} else {
			    	    		Bundle extras = new Bundle();
			    				extras.putString("game_video_url", game_video_url);
			    				extras.putString("game_image_url", game_image_url);
			    				extras.putString("game_name", game_name);
			    				extras.putInt("time_until_start", time_until_start);
			    				extras.putString("game_rules", game_rules);
			    				extras.putString("description", description);
			    				extras.putInt("event_id_facebookShare", event_id_facebookShare);
			    				
			    				Intent i = new Intent(CampusGameRules.this, CampusGameWaiting.class);
			    				i.putExtras(extras);
			    				startActivity(i);
			    	    	}
				        } else {
				        	FacebookSessionStart();
				        }
		            }
				}
			}
		});
	}
	
	private void FacebookSessionStart() {
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
	        	shareOnFacebook(caption_facebookShare, 
    					message_facebookShare, 
    					icon_url_facebookShare, 
    					url_facebookShare, 
    					description_facebookShare, 
    					url_name_facebookShare, 
    					event_id_facebookShare);
    	    	
    	    	if (time_until_start <= 0){
	    	    	Bundle extras = new Bundle();
					extras.putInt("promo_event_id", event_id_facebookShare);
					extras.putString("game_rules", game_rules);
					Intent i = new Intent(CampusGameRules.this, GameMap.class);
					i.putExtras(extras);
					startActivity(i);
    	    	} else {
    	    		Bundle extras = new Bundle();
    				extras.putString("game_video_url", game_video_url);
    				extras.putString("game_image_url", game_image_url);
    				extras.putString("game_name", game_name);
    				extras.putInt("time_until_start", time_until_start);
    				extras.putString("game_rules", game_rules);
    				extras.putString("description", description);
    				extras.putInt("event_id_facebookShare", event_id_facebookShare);
    				
    				Intent i = new Intent(CampusGameRules.this, CampusGameWaiting.class);
    				i.putExtras(extras);
    				startActivity(i);
    	    	}
	        } else {
	        	FacebookSessionStart();
	        }
        }
    }
	
	public void shareOnFacebook(String caption, String message, String icon_url, String url, String description, String url_name, final int promoEventId) {
	    
		Session session = Session.getActiveSession();
	    if (session != null){
	        // Check for publish permissions    
	        List<String> permissions = session.getPermissions();
	        if (!isSubsetOf(PERMISSIONS, permissions)) {
	            Session.NewPermissionsRequest newPermissionsRequest = new Session
	                    .NewPermissionsRequest(this, PERMISSIONS);
	            //session.requestNewPublishPermissions(newPermissionsRequest);
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
	        				result = new Rest.requestBody().execute(Rest.GAME + promoEventId, Rest.OSESS + Profile.sk, Rest.PUT, "1", "fb_share", "1").get();
	        			} catch (InterruptedException e1) {
	        				// TODO Auto-generated catch block
	        				e1.printStackTrace();
	        			} catch (ExecutionException e1) {
	        				// TODO Auto-generated catch block
	        				e1.printStackTrace();
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
	
	public void onResume() {
		super.onResume();
		
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
