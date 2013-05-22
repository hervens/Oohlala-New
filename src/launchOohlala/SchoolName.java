package launchOohlala;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import network.ErrorCodeParser;
import network.RetrieveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.ProfileSettings;

import session.SessionStore;
import user.Profile;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.Util;
import com.facebook.model.GraphUser;
import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.R.id;
import com.gotoohlala.R.layout;

import datastorage.CacheInternalStorage;
import datastorage.FB;
import datastorage.FacebookReauthorize;
import datastorage.Fonts;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.UserLoginInfo;
import datastorage.Rest.request;



import ManageThreads.TaskQueue;
import ManageThreads.TaskQueueImage;
import ManageThreads.TaskQueueImage2;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SchoolName extends Activity {
	
	TextView tvSchoolName, tvOr;
	Button bRegister;
	int schoolid;
	String gender, birthdate, email, first_name, last_name, userPW, fb_uid;

    int email_is_verified;
    
    private Button bLoginFacebook;
    private static final List<String> PERMISSIONS = Arrays.asList("user_about_me", "user_birthday", "email");
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    
    BroadcastReceiver brLogout;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schoolname);
		
		Bundle b = this.getIntent().getExtras();
		schoolid = b.getInt("school_id");
		String schoolname = b.getString("school_name");
		email = b.getString("email");
		
		tvSchoolName = (TextView) findViewById(R.id.tvSchoolName);
		tvSchoolName.setTypeface(Fonts.getOpenSansBold(SchoolName.this));
		tvSchoolName.setText(schoolname);
		
		tvOr = (TextView) findViewById(R.id.tvOr);
		tvOr.setTypeface(Fonts.getOpenSansBold(SchoolName.this));
		
		bLoginFacebook = (Button) findViewById(R.id.bLoginFacebook);
	    
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
        
        //bLoginFacebook.setText(R.string.Log_In);
    	bLoginFacebook.setOnClickListener(new OnClickListener() {
            public void onClick(View view) { onClickLogin(); }
        });
        
		bRegister = (Button) findViewById(R.id.bRegister);
		bRegister.setOnClickListener(new Button.OnClickListener() {  
			public void onClick(View v) {
				Bundle extras = new Bundle();
				extras.putInt("school_id", schoolid);
				extras.putString("email", email);
				
				Intent i = new Intent(getApplicationContext(), Register.class);
				i.putExtras(extras);
				startActivity(i);
			}
		});
		
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
	
	public String formatDate(String date){
		Date testdate;
		try {
			testdate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
			String newdate = new SimpleDateFormat("yyyy-MM-dd").format(testdate);
			return newdate;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private void showAlertDialog() {
		// TODO Auto-generated method stub
    	AlertDialog alert = new AlertDialog.Builder(SchoolName.this).create();
		alert.setTitle(getString(R.string.Registration_through_Facebook_is_successful));
		alert.setMessage(getString(R.string.Check_your_email_to_verify_your_account));
		alert.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				/*
				Bundle extras = new Bundle();
				extras.putString("registered_email", email);
				Intent i = new Intent(getApplicationContext(), CheckEmail.class);
				i.putExtras(extras);				
				startActivity(i);
				*/
				
				trialLogin();
			}
		});
		
		alert.show();
	}
	
	public void trialLogin() {
		// TODO Auto-generated method stub
		RestClient result = null;
		try {
			result = new Rest.renewSession().execute(Rest.RENEW_SESSION, Rest.OTOKE + Rest.accessCode + email + ":" + userPW, Rest.POST).get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (result.getResponseCode() == 201) {
			RestClient result3 = null;
			try {
				result3 = new request().execute(Rest.USER, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (result3 != null){
				Profile.setProfile(result3.getResponse(), userPW);
				CacheInternalStorage.cacheUserLoginInfo(new UserLoginInfo(email, userPW, true), getApplicationContext()); //cache user info
				
				TaskQueue.start();
				TaskQueueImage.start(getApplicationContext());
				TaskQueueImage2.start();
				Intent i = new Intent(getApplicationContext(), OohlalaMain.class);
				startActivity(i);
			} 
		}
	}

    public void setUserPW(String fb_uid) {
		// TODO Auto-generated method stub
    	RestClient result = null;
 		try {
 			result = new Rest.request().execute(Rest.SCHOOL + "?email=" + email, Rest.OTOKE + Rest.accessCode2, Rest.GET).get();
 		} catch (InterruptedException e1) {
 			// TODO Auto-generated catch block
 			e1.printStackTrace();
 		} catch (ExecutionException e1) {
 			// TODO Auto-generated catch block
 			e1.printStackTrace();
 		}
 		Log.i("set userPW: ", result.getResponse());
		
 		JSONObject user_info = null;
 		int user_id = -1;
 		try {
 			user_info = (new JSONObject(result.getResponse())).getJSONObject("user_info");
 			user_id = (new JSONObject(result.getResponse())).getJSONObject("user_info").getInt("user_id");
 		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	 	if (user_id != -1) {
			String user_fb_token = null;
			try {
				user_fb_token = user_info.getString("fb_token");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			userPW = RetrieveData.hash(email + user_fb_token + fb_uid).trim();
			Log.i("userPW: ", userPW);
		}
	}
	
    public void showErrorAlertDialog(String error_message) {
		// TODO Auto-generated method stub
    	AlertDialog alert = new AlertDialog.Builder(SchoolName.this).create();
		alert.setTitle(getString(R.string.Registration_Error));
		alert.setMessage(error_message);
		alert.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		
		alert.show();
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

    private void updateView() {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
        	// Check for publish permissions    
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(PERMISSIONS, permissions)) {
                Session.NewPermissionsRequest newPermissionsRequest = new Session
                        .NewPermissionsRequest(this, PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
            }
            
        	makeMeRequest(session);
        	
        	//bLoginFacebook.setText(R.string.Log_Out);
        	bLoginFacebook.setOnClickListener(new OnClickListener() {
                public void onClick(View view) { onClickLogout(); }
            });
        } else {
        	//bLoginFacebook.setText(R.string.Log_In);
        	bLoginFacebook.setOnClickListener(new OnClickListener() {
                public void onClick(View view) { onClickLogin(); }
            });
        }
    }
    
    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a 
        // new callback to handle the response.
        Request request = Request.newMeRequest(session, 
                new Request.GraphUserCallback() {
            public void onCompleted(GraphUser user, Response response) {
                // If the response is successful
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                        fb_uid = user.getId();
        				
                        first_name = user.getFirstName();
        				last_name = user.getLastName();
        				
        				if (user.getBirthday() != null){
        					birthdate = formatDate(user.getBirthday());
        					//Log.i ("facebook info birthday: ", birthdate);
        				}

        				gender = "M";
        				if (user.getProperty("gender").toString().contentEquals("male")){
        					gender = "M";
        				} else if (user.getProperty("gender").toString().contentEquals("female")){
        					gender = "F";
        				}
        				//Log.i ("facebook info gender: ", gender);
        				
        				/*
        				email_is_verified = 1;
        				if ((Boolean) user.getProperty("verified")){
        					email_is_verified = 1;
        				} else {
        					email_is_verified = 0;
        				}
        				*/
        				//Log.i ("facebook info verified: ", String.valueOf(email_is_verified));
        				
        				mHandler.post(new Runnable() {
        		            public void run() {
        		            	RestClient result = null;
        						try {
        							result = new Rest.requestBody().execute(Rest.USER, Rest.OTOKE + Rest.accessCode2, Rest.POST, "7", 
        									"email", email, "firstname", first_name, "lastname", last_name, 
        									"date_of_birth", birthdate, "gender", gender, "school_id", String.valueOf(schoolid),
        									"facebook_uid", fb_uid).get();
        						} catch (InterruptedException e1) {
        							// TODO Auto-generated catch block
        							e1.printStackTrace();
        						} catch (ExecutionException e1) {
        							// TODO Auto-generated catch block
        							e1.printStackTrace();
        						}
        						Log.i("create account facebook result", result.getResponse());
        								
        						if (result.getResponseCode() == 201) {
        							setUserPW(fb_uid);
        							showAlertDialog();
        						} else if (result.getResponseCode() == 403 && result.getResponse().contains("age")){
        							onClickLogout();
        							showErrorAlertDialog(getString(R.string.Registration_failed_The_user_does_not_satisfy_the_age_requirements_of_the_app));
        						} else if (result.getResponseCode() == 409 && result.getResponse().contains("email")){
        							onClickLogout();
        							showErrorAlertDialog(getString(R.string.Registration_failed_The_email_has_been_used_in_another_account));
        						} else if (result.getResponseCode() == 409 && result.getResponse().contains("facebook_uid")){
        							onClickLogout();
        							showErrorAlertDialog(getString(R.string.Registration_failed_Facebook_account_already_has_an_account_on_OOHLALA));
        						} 
        		            }
        				});
                    }
                }
                if (response.getError() != null) {
                    // Handle errors, will do so later.
                	Log.i ("facebook response errorMessage: ", response.getError().getErrorMessage());
                	makeMeRequest(session);
                }
            }
        });
        request.executeAsync();
    } 

    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }

    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }
	
	private class SessionStatusCallback implements Session.StatusCallback {
        public void call(Session session, SessionState state, Exception exception) {
            updateView();
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
