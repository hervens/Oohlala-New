package campuswall;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import network.ErrorCodeParser;
import network.RetrieveData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import session.SessionStore;

import launchOohlala.BaseDialogListener;
import launchOohlala.BaseRequestListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.facebook.FacebookException;
import com.gotoohlala.R;

import datastorage.FB;
import datastorage.FacebookReauthorize;
import datastorage.ImageLoader;
import datastorage.RestClient;

public class CampusWallInviteFacebook extends Activity {
	ListView lvInviteFacebook;
	List<InviteFacebookModel> list = new ArrayList<InviteFacebookModel>();
	InviteFacebookArrayAdapter adapter;
	EditText etSearchFacebookFriends;
	
	public int invite_id = 0;
	public String invitee_name;
	private boolean searchFriends = false;
	
	private static final List<String> PERMISSIONS = Arrays.asList("user_about_me", "user_birthday", "email", "publish_stream");
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campuswallinvitefacebook);
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
		 
		lvInviteFacebook = (ListView) findViewById(R.id.lvInviteFacebook);
		lvInviteFacebook.setTextFilterEnabled(true);
	   	lvInviteFacebook.setOnItemClickListener(new OnItemClickListener() {
	   		public void onItemClick(AdapterView<?> parent, View view,
	   				int position, long id) {
	   			
		   			invitee_name = list.get(position).name;
		   			
		   			final String[] names = {"", ""};
		   			
		   			if (list.get(position).name.contains(" ")){
		   				String delimiter = " ";
		   				names[0] = list.get(position).name.split(delimiter)[0];
		   				names[1] = list.get(position).name.split(delimiter)[1];
		   			} else {
		   				names[0] = list.get(position).name;
		   				names[1] = "";
		   			}
	   			
			   		 Bundle params = new Bundle();
				   	 params.putString("message", getString(R.string.hey_give_OOHLALA_a_try));
				   	 params.putString("to", list.get(position).id);
				   	 WebDialog requestsDialog = (
			   	        new WebDialog.RequestsDialogBuilder(CampusWallInviteFacebook.this,
			   	            Session.getActiveSession(),
			   	            params))
			   	            .setOnCompleteListener(new OnCompleteListener() {
			   	                public void onComplete(Bundle values,
			   	                    FacebookException error) {
			   	                	if (values != null){
				   	                    final String requestId = values.getString("request");
				   	                    if (requestId != null) {
				   	                        Toast.makeText(getApplicationContext(), 
				   	                        	getString(R.string.Request_Sent),  
				   	                            Toast.LENGTH_SHORT).show();
				   	                    } else {
				   	                        Toast.makeText(getApplicationContext(), 
				   	                        	getString(R.string.Request_Cancelled), 
				   	                            Toast.LENGTH_SHORT).show();
				   	                    }
			   	                	}
			   	                }
			   	            }).build();
				   	 
				   	 	requestsDialog.show();
		   	    
	   			/*
			    String result = null;
			    String sms_message = null;
				int code;
				
				Hashtable<String, String> params = new Hashtable<String, String>();
				if (params != null){
					params.put("email", "");
					params.put("first_name", names[0]);
					params.put("last_name", names[1]);
					params.put("is_sms", String.valueOf(2));
					params.put("invitee_extern_id", list.get(position).id);
					//result = RetrieveData.requestMethod(RetrieveData.EXTERNAL_INVITE, params);
					//Log.i("External Friend Invite", result.toString());
				}
					
				code = ErrorCodeParser.parser(result);
					
				if (code == 0) {
					try {
						sms_message = (new JSONObject(result)).getString("sms_message");
						invite_id = (new JSONObject(result)).getInt("invite_id");
							
						Log.i("sms_message", sms_message.toString());
						Log.i("invite_id", String.valueOf(invite_id));
					} catch (JSONException e) {
							// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			   		//send request to a particular friend.
			   		Bundle b = new Bundle();
			   		b.putString("to", list.get(position).id);
			   		b.putString("message", sms_message);
			   		//mFacebook.dialog(CampusWallInviteFacebook.this, "apprequests", b, new AppRequestsListener());
			   		
				} else if (code == 1001) {
						Toast.makeText(CampusWallInviteFacebook.this, getString(R.string.The_invitee_is_already_a_friend_of_you), Toast.LENGTH_SHORT).show();
				} else if (code == 1002) {
						Toast.makeText(CampusWallInviteFacebook.this, getString(R.string.There_is_already_an_in_app_friend_invite_pending_between_you_and_the_invitee), Toast.LENGTH_SHORT).show();
				} else if (code == 1003) {
						Toast.makeText(CampusWallInviteFacebook.this, getString(R.string.An_external_friend_invitation_has_already_been_sent_to_the_invitee), Toast.LENGTH_SHORT).show();
				}
				*/
	   		}
	   	});
		 
		etSearchFacebookFriends = (EditText) findViewById(R.id.etSearchFacebookFriends);
		etSearchFacebookFriends.addTextChangedListener(new TextWatcher() {
			 public void onTextChanged(CharSequence s, int start, int before, int count) {
				 if (searchFriends){
					 adapter.getFilter().filter(s.toString());
				 }
			 }
				
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			     
			}
			 
			public void afterTextChanged(Editable s) {
			    	 
			}
		});
		 
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
	    
        Session session = Session.getActiveSession();
        if (session == null) {
        	if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
        
        if (!session.isOpened()) {
        	onClickLogin();
	    } else {
			Thread updateListThread = new updateListThread();
			updateListThread.start();	
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
	
	private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }
	
	private class SessionStatusCallback implements Session.StatusCallback {
        public void call(Session session, SessionState state, Exception exception) {
        	Thread updateListThread = new updateListThread();
			updateListThread.start();	
        }
    }
	 
	class updateListThread extends Thread {
		 public void run() {
			 Session session = Session.getActiveSession();
		        if (session.isOpened()) {
		        	//AddPermissions(session);    
		        	try {	
						 HttpClient httpClient = new DefaultHttpClient();
			             HttpContext localContext = new BasicHttpContext();
			             HttpGet httpGet = new HttpGet(
			                     "https://graph.facebook.com/me/friends?access_token="
			                             + session.getAccessToken());
			             HttpResponse httpResponse = httpClient.execute(httpGet,
			                     localContext);
			             
			             String response = null;
			             HttpEntity entity2 = httpResponse.getEntity();
			 	     	 if (entity2 != null) {
			 	     		InputStream instream = entity2.getContent();
			 	     		response = RestClient.convertStreamToString(instream);
			 	     		instream.close();
			 	      	 }
		
			        	 Log.i("response", response);
			        	 if (response == null || response.equals("") || 
				        	response.equals("false")) {
			        		 Log.v("Error", "Blank response");
			        	 } else {
			        		 JSONObject json = new JSONObject(response);
					         final JSONArray friends = json.getJSONArray("data");
					         Log.i("facebook friends: ", friends.toString());
					    				
					         for (int i = 0; i < friends.length(); i++){
					        	 String id = friends.getJSONObject(i).getString("id");
					        	 String name = friends.getJSONObject(i).getString("name");
					        	 list.add(new InviteFacebookModel(id, name));
					         }
					    		
					         runOnUiThread(new Runnable() {
					        	 public void run() {
					        		 adapter = new InviteFacebookArrayAdapter(CampusWallInviteFacebook.this, CampusWallInviteFacebook.this, list);
					        		 lvInviteFacebook.setAdapter(adapter);
					        		 searchFriends = true;
					        	 }
					         });	
			        	 }
		        	} catch(Exception e) {
		                e.printStackTrace();
		            }  
		        }
		 }	
	}
	
	public void AddPermissions(Session session){
		List<String> permissions = session.getPermissions();
        if (!isSubsetOf(PERMISSIONS, permissions)) {
            Session.NewPermissionsRequest newPermissionsRequest = new Session
                    .NewPermissionsRequest(this, PERMISSIONS);
            session.requestNewPublishPermissions(newPermissionsRequest);
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
}
