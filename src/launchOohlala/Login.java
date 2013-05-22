package launchOohlala;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import launchOohlala.SessionEvents.AuthListener;
import launchOohlala.SessionEvents.LogoutListener;

import network.ErrorCodeParser;
import network.RetrieveData;

import org.json.JSONException;
import org.json.JSONObject;

import profile.Friends;
import profile.ProfileSettings;

import session.SessionStore;
import user.Profile;
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
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import campusgame.MapTesting;
import campuswall.CampusWallImage;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.Response;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.model.GraphUser;
import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;

import datastorage.CacheInternalStorage;
import datastorage.FB;
import datastorage.FacebookReauthorize;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.LoginState;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.UserLoginInfo;
import datastorage.Rest.request;
import events.EventsEventsModel;

public class Login extends Activity {
    /** Called when the activity is first created. */
	EditText etPassword, etEmail;
	
	String userAccount, userPW;
	ImageView ivAvatar;
	boolean facebook_Connected = false;
	private ProgressDialog m_ProgressDialog;
	
	private int code;
	//private String r;
    
    private Handler mHandler = new Handler(Looper.getMainLooper());
    
    private Button bLoginFacebook;
    private static final List<String> PERMISSIONS = Arrays.asList("user_about_me", "user_birthday", "email");
    
    //public boolean overrideCache = false;
    public boolean Cached = false;
    
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    
    BroadcastReceiver brLogout;
    Button bLogin, bForgetMyPassword;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        
        TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(Login.this));
		header.setText(header.getText().toString().toUpperCase());
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
				
				InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
			}
	    }); 
		
			//check if the userLogin is cached
	        if (CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()) != null){
	      		if (CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()).userAccount.trim().length() > 0){
	      			Cached = true;
	      			
	      			userAccount = CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()).userAccount;
	      			userPW = CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()).userPassword;
	      			
	      			if (CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()).facebookUser){
	      				/*
		      			mFacebook = new Facebook(APP_ID);
	      				mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		      		    SessionStore.restore(mFacebook, getApplicationContext());
		  
		      		    if (mFacebook.getAccessToken() == null || !mFacebook.isSessionValid()){  	
		      		    	LoginButton.act = Login.this;
		      		    	new LoginButton(getApplicationContext()).reauthorize(mFacebook, Login.this, permissions);	
		      		    } else {
		      		    	if (!Login.this.isFinishing()){
		      		    		m_ProgressDialog = ProgressDialog.show(Login.this, getString(R.string.Please_Wait), getString(R.string.loading), true);
		      		    	}
		      		    	loginTrigger(userAccount, userPW);
		      		    }
		      		    */
	      			} else {
	      				if (!Login.this.isFinishing()){
	      					m_ProgressDialog = ProgressDialog.show(Login.this, getString(R.string.Please_Wait), getString(R.string.loading), true);
	      				}
	      				
	      				loginTrigger(userAccount, userPW);
	      			}
	      		}
	      	}
		
		
		if (!Cached){
			
    	}
		
		bLoginFacebook = (Button) findViewById(R.id.FacebookLogin);
		
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
    	
        
        bLoginFacebook.setOnClickListener(new OnClickListener() {
            public void onClick(View view) { onClickLogin(); }
        });
        
		bLogin = (Button) findViewById(R.id.bLogin);
		bForgetMyPassword = (Button) findViewById(R.id.bForgetMyPassword);
	    etPassword = (EditText) findViewById(R.id.user_pw_entry);
	    etPassword.setTypeface(Fonts.getOpenSansRegular(Login.this));
			
	    bLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				userPW = RetrieveData.hash(etPassword.getText().toString().trim());
				userAccount = etEmail.getText().toString().trim();
				Log.i("userAccount", userAccount);
				Log.i("userPW", userPW);
					
				if (!Login.this.isFinishing()){
					m_ProgressDialog = ProgressDialog.show(Login.this, getString(R.string.Please_Wait), getString(R.string.loading), true);
				}
				loginTrigger(userAccount, userPW);	
			}
		});
		
		ivAvatar = (ImageView) findViewById(R.id.avatar);
		
		etEmail = (EditText) findViewById(R.id.user_account_entry);
		etEmail.setTypeface(Fonts.getOpenSansRegular(Login.this));
		
		bForgetMyPassword.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Bundle extras = new Bundle();
				extras.putString("email", etEmail.getText().toString().trim());
				Intent i = new Intent(getApplicationContext(), Recover.class);
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

    public void loginTrigger(final String userAccount, final String userPW) {

		if (userAccount.length() > 0 && userPW.length() > 0) {
			Runnable viewLogin = new Runnable() {
				public void run() {
					getLogin(userAccount, userPW);
				}
			};
			
			Thread thread = new Thread(null, viewLogin, "MagentoBackground");
			thread.start();
		} else {
			mHandler.post(new Runnable() {
        		public void run() {
        			m_ProgressDialog.dismiss();
        		}
			});
		}
	}
    
    private Runnable returnRes = new Runnable() {
    	
		public void run() {
			if (code == 201) {
				InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etEmail.getWindowToken(), 0);
				
				TaskQueue.start();
				TaskQueueImage.start(getApplicationContext());
				TaskQueueImage2.start();
				Bundle extras = new Bundle();
				extras.putBoolean("Login", true);
				Intent i = new Intent(getApplicationContext(), OohlalaMain.class);
				i.putExtras(extras);
				startActivity(i);
			} else {
				//showErrorAlertDialog(getString(R.string.Login_failed));
				//onClickLogout();
			}
			
			mHandler.post(new Runnable() {
        		public void run() {
        			m_ProgressDialog.dismiss();
        		}
			});
		}
	};
	
	
    public void getLogin(final String userAccountLogin, final String userPWLogin) {
    	mHandler.post(new Runnable() {
    		public void run() {
    			RestClient result = null;
				try {
					result = new Rest.renewSession().execute(Rest.RENEW_SESSION, Rest.OTOKE + Rest.accessCode + userAccountLogin + ":" + userPWLogin, Rest.POST).get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if (result.getResponse() != null){
					Log.i("login result", result.getResponse());
				}
				
				code = result.getResponseCode();
				Log.i("login code", String.valueOf(result.getResponseCode()));
				
				if (code == 201){
					String result2 = null;
					try {
						result2 = new request().execute(Rest.APP_CONFIG + "?ver=" + RetrieveData.version, Rest.OSESS + Profile.sk, Rest.GET).get().getResponse();
						Profile.setAppConfiguration(result2);
						if (Profile.has_update){
							RetrieveData.showUpdateDialog = true;
						}
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
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
						Profile.setProfile(result3.getResponse(), userPWLogin);
						CacheInternalStorage.cacheUserLoginInfo(new UserLoginInfo(userAccountLogin, userPWLogin, facebook_Connected), getApplicationContext()); //cache user info
					
						runOnUiThread(returnRes);
					} else {
						//get user failed
						//getLogin(userAccount, userPW);
					}
				} else if (code == 401){
					mHandler.post(new Runnable() {
		        		public void run() {
		        			if (facebook_Connected){
		        				showErrorAlertDialog(getString(R.string.You_are_logged_into_the_wrong_facebook_account), facebook_Connected);
		        				onClickLogout();
							} else {
								showErrorAlertDialog(getString(R.string.Wrong_Password), facebook_Connected);
							}
		        			
		        			m_ProgressDialog.dismiss();
		        		}
					});
				} else if (code == 403){
					mHandler.post(new Runnable() {
		        		public void run() {
		        			showErrorAlertDialog(getString(R.string.User_account_has_been_banned_or_deactivated), false);
		        			if (facebook_Connected){
		        				onClickLogout();
							}
		        			
		        			m_ProgressDialog.dismiss();
		        		}
					});
				} else {
					mHandler.post(new Runnable() {
		        		public void run() {
		        			m_ProgressDialog.dismiss();
		        		}
					});
				}
    		}
    	});
	}
    
    /*
    public void startFacebookSignIn(){
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        if(access_token != null) {
        	mFacebook.setAccessToken(access_token);
        }
        if(expires != 0) {
        	mFacebook.setAccessExpires(expires);
        }
		 
        if(!mFacebook.isSessionValid()) {
        	mFacebook.authorize(this, new String[] { "user_about_me", "user_birthday", "email"},
					
				new DialogListener() {
					public void onComplete(Bundle values) {
						SharedPreferences.Editor editor = mPrefs.edit();
						editor.putString("access_token", mFacebook.getAccessToken());
						editor.putLong("access_expires", mFacebook.getAccessExpires());
						editor.commit();
					}
					public void onFacebookError(FacebookError error) {}
					public void onError(DialogError e) {}
					public void onCancel() {}
		        });
			
        }
    }
    */
    
    public void showErrorAlertDialog(String error_message, final boolean forceReauthorize) {
		// TODO Auto-generated method stub
    	if (!Login.this.isFinishing()){
	    	AlertDialog alert = new AlertDialog.Builder(Login.this).create();
			alert.setTitle(getString(R.string.Login_Error));
			alert.setMessage(error_message);
			alert.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if (forceReauthorize){
						//LoginButton.act = Login.this;
						//new LoginButton(getApplicationContext()).reauthorize(mFacebook, Login.this, permissions);
					}
				}
			});
			alert.show();
    	}
	}
	
    /*
	@Override
    public void onStart() {
        super.onStart();
        if (facebook_Connected){
        	Session.getActiveSession().addCallback(statusCallback);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (facebook_Connected){
        	Session.getActiveSession().removeCallback(statusCallback);
        }
    }
	*/
    
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
    	Log.i("updateView", "-------------------------");
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
        	m_ProgressDialog = ProgressDialog.show(Login.this, getString(R.string.Please_Wait), getString(R.string.loading), true);
        	
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
                    	facebook_Connected = true;
                    	
                    	if (user.getProperty("email") != null){
                    		userAccount = user.getProperty("email").toString();
                    	}
                        final String fb_uid =  user.getId();
            			
            			mHandler.post(new Runnable() {
                    		public void run() {
                    			loginTrigger(userAccount, fb_uid);	
                    		}
            			});
                    }
                }
                
                if (response.getError() != null) {
                    // Handle errors, will do so later.
                	mHandler.post(new Runnable() {
                		public void run() {
                			m_ProgressDialog.dismiss();
                		}
        			});
                	
                	//makeMeRequest(session);
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