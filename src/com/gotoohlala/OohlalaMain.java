package com.gotoohlala;

import inbox.Inbox;
import inbox.InboxModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import launchOohlala.CheckEmail;
import launchOohlala.InAppTour;
import launchOohlala.Login;

import network.GPS;
import network.RetrieveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.gotoohlala.LeftPanelLayout.loadThumbImage;
import com.gotoohlala.LeftPanelLayout.onSeletedListener;
import com.gotoohlala.R;
import com.gotoohlala.ScrollerContainer.OnSlideListener;

import campusgame.CampusGame;
import campusgame.CampusGameWaiting;
import campusgame.GameMap;
import campusmap.CampusMap;
import campuswall.CampusWall;
import campuswall.CampusWallModel;
import datastorage.CacheInternalStorage;
import datastorage.DeviceDimensions;
import datastorage.FB;
import datastorage.FacebookReauthorize;
import datastorage.LoginState;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.UserFirstLogin;
import datastorage.UserLoginInfo;
import datastorage.Rest.request;
import discoverMyCampus.RulesFragment;

import profile.ProfileSettings;
import profile.UserProfile;
import events.EventsEvents;
import events.SocialSchedule;

import rewards.Exploretab;
import rewards.Rewards;
import session.SessionStore;
import user.Profile;

import ManageThreads.TaskQueue;
import ManageThreads.TaskQueueImage;
import ManageThreads.TaskQueueImage2;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class OohlalaMain extends ActivityGroup implements onSeletedListener, OnSlideListener  {

		private static Handler mHandler = new Handler();
		
		//static FragmentActivity c;
		
		updateTimeCounter counter1;
		int trial_time_left  = 0;
		AlertDialog alertTrialTime;
		
		BroadcastReceiver logout;
		
		public static boolean t0 = false;
		public static boolean t1 = false;
		public static boolean t2 = false;
		public static boolean t3 = false;
		public static boolean t4 = false;
		
		public static boolean CampusWall_is_global = false;
		public static int CampusWall_thread_type = 0;
		
		String userAccount, userPW;
	    
	    Bundle instance;
	    
	    boolean Login = false;
	    
	    public static boolean campusWallUnlockedCheck = false;
	    public static boolean campusWallUnlocked = true;
	    public static int target_student_count = 0;
		public static int current_student_count = 0;
		
		BroadcastReceiver brLogout;
		
		public static boolean SyncClassesThreadFirstTime = true;
		
		/** 
	     * ���������������� 
	     */  
	    private ScrollerContainer mSlideContainer;  
	  
	    /** 
	     * �������� 
	     */  
	    private LeftPanelLayout mLeftPanelLayout;  
	  
	    /** 
	     * ������ 
	     */  
	    private SocialSchedule SocialSchedule;  
	    private RulesFragment rulesFragment;
	    private CampusWall CampusWall;
	    private CampusGame CampusGame;
	    private rewards.CampusClubs CampusClubs;
	    private UserProfile UserProfile;
	    private Inbox Inbox;
	    private studentsnearby.studentsNearby studentsNearby;
	    private EventsLeftPanel EventsLeftPanel;
	    private FriendsLeftPanel FriendsLeftPanel;
	    private ProfileSettingsLeftPanel ProfileSettingsLeftPanel;
	    private Exploretab Exploretab;
	    private PhotosLeftPanel PhotosLeftPanel;
	    private FrameLayout emptytab;
	    
	    LinkedList<Integer> tabs = new LinkedList<Integer>();
	    
	    private int old_games_num = 0;
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        instance = savedInstanceState;
	        
			Log.i("OohlalaMain", "==============");
	        
	        Bundle b = this.getIntent().getExtras();
			if (b != null){
				if (b.containsKey("Login")){
					Login = b.getBoolean("Login");
				}
			}

	        if (CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()) != null){
				if (CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()).userAccount.trim().length() > 0){
					userAccount = CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()).userAccount;
	      			userPW = CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()).userPassword;
	      			
	      			if (Login){
	      				if (CacheInternalStorage.getCacheUserFirstLogin(getApplicationContext()) != null){
							if (CacheInternalStorage.getCacheUserFirstLogin(getApplicationContext()).userAccount.trim().length() > 0 &&
									CacheInternalStorage.getCacheUserFirstLogin(getApplicationContext()).userAccount.contentEquals(userAccount)){
								//
								if (CacheInternalStorage.getCacheUserFirstLogin(getApplicationContext()).first_time){
									CacheInternalStorage.cacheUserFirstLogin(new UserFirstLogin(userAccount, false, true, true), getApplicationContext()); //cache user first login
									
									Intent i = new Intent(getApplicationContext(), InAppTour.class);
									startActivity(i);
								} else {
									setNewContentView(instance);
								}
								//
							} else {
								CacheInternalStorage.cacheUserFirstLogin(new UserFirstLogin(userAccount, false, true, true), getApplicationContext()); //cache user first login
								
								Intent i = new Intent(getApplicationContext(), InAppTour.class);
								startActivity(i);
							}
						} else {
							CacheInternalStorage.cacheUserFirstLogin(new UserFirstLogin(userAccount, false, true, true), getApplicationContext()); //cache user first login
							
							Intent i = new Intent(getApplicationContext(), InAppTour.class);
							startActivity(i);
						}
	      			} else {
	      				Log.i("OohlalaMain", "==============2");
	      				getLogin(userAccount, userPW);
	      			}
				} else {
					setNewContentView(instance);
				}
	        } else {
	        	setNewContentView(instance);
	        }
		    
		    /*
		    GCMRegistrar.checkDevice(this);
		    GCMRegistrar.checkManifest(this);
		    final String regId = GCMRegistrar.getRegistrationId(this);
		    if (regId.equals("")) {
		      GCMRegistrar.register(this, "1018376853391");
		      Log.i("new regID", GCMRegistrar.getRegistrationId(this));
		    } else {
		      Log.i("old regID", regId);
		    }
		   
		    registerReceiver(mHandleMessageReceiver,
	                new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
	        */
	        
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
	    
	    public void setNewContentView(Bundle savedInstanceState){
	    	 	mSlideContainer = new ScrollerContainer(OohlalaMain.this);  
		        
		        mLeftPanelLayout = new LeftPanelLayout(OohlalaMain.this);  
		        mLeftPanelLayout.setOnSeletedListener(OohlalaMain.this);
		        
		        SocialSchedule = new SocialSchedule(OohlalaMain.this); 
		        SocialSchedule.TopMenuNavbar.setOnSlideListener(OohlalaMain.this);   
		        tabs.addLast(0);
		        
		        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);  
		        mSlideContainer.addView(mLeftPanelLayout, 0, layoutParams);  
		        mSlideContainer.addView(SocialSchedule, 1, layoutParams); 
		        //ScrollerContainer.mPanelInvisible = false;
		        
	    	 	setContentView(mSlideContainer);
	    	 	TaskQueueImage.addTask(new checkAllNewThreads(), OohlalaMain.this);
	    	 	Log.i("OohlalaMain", "==============3");
	    	 	
		        if (SetXMPPConnection.connection == null){
		        	XMPPConnectionConnect();
		        }
		     
				RetrieveData.client = OohlalaMain.this;
				RetrieveData.showUpdateAlertDialog();
				
				//checkTrialPeriod();
				Log.i("OohlalaMain", "==============4");
	    }
	   
	    public void checkTrialPeriod() {
			// TODO Auto-generated method stub
	    	boolean is_trial = false;
	    	
	    	RestClient result = null;
	    	if (Profile.email != null && Profile.pass != null){
	    		try {
					result = new Rest.renewSession().execute(Rest.RENEW_SESSION, Rest.OTOKE + Rest.accessCode + Profile.email + ":" + Profile.pass, Rest.POST).get();
					if (result != null){
						try {
							is_trial = (new JSONObject(result.getResponse())).getBoolean("is_trial");
							Log.i("is_trial--------------------", String.valueOf(is_trial));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						try {
							trial_time_left = (new JSONObject(result.getResponse())).getInt("trial_time_left");
							Log.i("trial_time_left--------------------", String.valueOf(trial_time_left));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
			//Debug.out (result);
			//Log.i("check trial result", result);
			
			if (is_trial){
				if (trial_time_left > 0){
					int days = trial_time_left/86400;
					long millis = trial_time_left*1000L;
			        Date date = new Date(millis);
			        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			        String formattedDate = sdf.format(date);
			        
			        counter1 = new updateTimeCounter(trial_time_left*1000, 1000);
			        counter1.start(); 
					
					alertTrialTime = new AlertDialog.Builder(OohlalaMain.this).create();
					alertTrialTime.setMessage(getString(R.string.You_have_) + days + getString(R.string._days_) + formattedDate + getString(R.string._to_verify_your_university_email));
					alertTrialTime.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							counter1.cancel();
							Log.i("trial time counter", "end ---------------------------------------");
						}
					});
					alertTrialTime.show();
				} else if (trial_time_left <= 0){
					AlertDialog alert = new AlertDialog.Builder(OohlalaMain.this).create();
					alert.setMessage(getString(R.string.Your_trial_period_has_ended_please_go_to_verify_your_email));
					alert.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					});
					alert.show();
				}
			}
		}
	    
	    class updateTimeCounter extends CountDownTimer {
	        public updateTimeCounter(long millisInFuture, long countDownInterval) {
	          super(millisInFuture, countDownInterval);
	        }

	        public void onFinish() {
	            //dialog.dismiss();
	           // Use Intent to Navigate from this activity to another
	        }

	        @Override
	        public void onTick(long millisUntilFinished) {
	            // TODO Auto-generated method stub
	        	getTrialTime();
	        }
	    }
	    
	    public synchronized void getTrialTime() {
	    	int days = trial_time_left/86400;
			long millis = trial_time_left*1000L;
	        Date date = new Date(millis);
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	        String formattedDate = sdf.format(date);

	        alertTrialTime.setMessage(getString(R.string.You_have_) + days + getString(R.string._days_) + formattedDate + getString(R.string._to_verify_your_university_email));
			
	        if (trial_time_left <= 0){
				AlertDialog alert = new AlertDialog.Builder(OohlalaMain.this).create();
				alert.setMessage(getString(R.string.Your_trial_period_has_ended_please_go_to_verify_your_email));
				alert.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						counter1.cancel();
						Log.i("trial time counter", "end ---------------------------------------");
					}
				});
				alert.show();
			}
	        
	        if (trial_time_left > 0){
	        	trial_time_left--;
	        }
		}

		public void XMPPConnectionConnect(){
	    	Log.i("setup xmpp connection", "------------- yes -----------------");
	    	
	    	String ns = Context.NOTIFICATION_SERVICE;
    		NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(ns);
    		
    		new SetXMPPConnection(getApplicationContext(), mNotificationManager).connect();
	    }
	    
	   
	    @Override
	    protected void onSaveInstanceState(Bundle outState) {
	    	super.onSaveInstanceState(outState);
	        //outState.putString("tab", mTabHost.getCurrentTabTag());
	    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			/*
			final CharSequence[] items = {getString(R.string.Stay_on_Oohlala), getString(R.string.Rate_Us), getString(R.string.Exit_Oohlala)};
			
			AlertDialog.Builder builder = new AlertDialog.Builder(OohlalaMain.this);
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			    	switch(item){
			    	case 0:		    		
			    		break;
			    	case 1:
			    		String url = "market://details?id=com.gotoohlala";
			    		Intent i = new Intent(Intent.ACTION_VIEW);
			    		i.setData(Uri.parse(url));
			    		startActivity(i);
			    		break;
			    	case 2:
			    		Intent intent = new Intent(Intent.ACTION_MAIN);
			    		intent.addCategory(Intent.CATEGORY_HOME);
			    		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    		startActivity(intent);
			    		break;
			    	}
			    }
			});
			AlertDialog alert = builder.create();
			alert.show();
			*/
			
			//moveTaskToBack(true);
			
			Intent intent = new Intent(Intent.ACTION_MAIN);
    		intent.addCategory(Intent.CATEGORY_HOME);
    		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		startActivity(intent);
    		
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
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
						Log.i("login result2", result.getResponse());
					}
					
					int code = result.getResponseCode();
					Log.i("login code2", String.valueOf(result.getResponseCode()));
					
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
							CacheInternalStorage.cacheUserLoginInfo(new UserLoginInfo(userAccountLogin, userPWLogin, CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()).facebookUser), getApplicationContext()); //cache user info
							
							if (CacheInternalStorage.getCacheUserFirstLogin(getApplicationContext()) != null){
								if (CacheInternalStorage.getCacheUserFirstLogin(getApplicationContext()).userAccount.trim().length() > 0 &&
										CacheInternalStorage.getCacheUserFirstLogin(getApplicationContext()).userAccount.contentEquals(userAccountLogin)){
									//
									if (CacheInternalStorage.getCacheUserFirstLogin(getApplicationContext()).first_time){
										CacheInternalStorage.cacheUserFirstLogin(new UserFirstLogin(userAccountLogin, false, true, true), getApplicationContext()); //cache user first login
										
										Intent i = new Intent(getApplicationContext(), InAppTour.class);
										startActivity(i);
									} else {
										setNewContentView(instance);
									}
									//
								} else {
									CacheInternalStorage.cacheUserFirstLogin(new UserFirstLogin(userAccountLogin, false, true, true), getApplicationContext()); //cache user first login
									
									Intent i = new Intent(getApplicationContext(), InAppTour.class);
									startActivity(i);
								}
							} else {
								CacheInternalStorage.cacheUserFirstLogin(new UserFirstLogin(userAccountLogin, false, true, true), getApplicationContext()); //cache user first login
								
								Intent i = new Intent(getApplicationContext(), InAppTour.class);
								startActivity(i);
							}
						} else {
							//get user null
							//getLogin(userAccount, userPW);
						}
					} else if (code == 401){
						mHandler.post(new Runnable() {
			        		public void run() {
			        			if (CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()).facebookUser){
			        				Toast.makeText(getBaseContext(), getString(R.string.You_are_logged_into_the_wrong_facebook_account), Toast.LENGTH_SHORT).show();
			        			} else {
									Toast.makeText(getBaseContext(), getString(R.string.Wrong_Password), Toast.LENGTH_SHORT).show();
								}
			        		}
						});
						
						Intent i = new Intent(getApplicationContext(), CheckEmail.class);
						startActivity(i);
					} else if (code == 403){
						mHandler.post(new Runnable() {
			        		public void run() {
			        			Toast.makeText(getBaseContext(), getString(R.string.User_account_has_been_banned_or_deactivated), Toast.LENGTH_SHORT).show();
			        		}
						});
						
						Intent i = new Intent(getApplicationContext(), CheckEmail.class);
						startActivity(i);
					} else {
						//login failed
						//getLogin(userAccount, userPW);
					}
	    		}
	    	});
		}

	 private final BroadcastReceiver mHandleMessageReceiver =
	            new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
	            GCMIntentService.generateNotification(OohlalaMain.this, newMessage);
	        }
	 };

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		t0 = false;
		t1 = false;
		t2 = false;
		t3 = false;
		t4 = false;
	}
	
	/**
	 * Clear the tab object if more than 5 of them are saved in memory
	 */
	public void clearOneTab(){
		if (tabs.size() > 5){
			switch(tabs.getFirst()){
			case 0:
				SocialSchedule = null;
				break;
			case 1:
				FriendsLeftPanel = null;
				break;
			case 2:
				CampusWall = null;
				break;
			case 3:
				PhotosLeftPanel = null;
				break;
			case 4:
				EventsLeftPanel = null;
				break;	
			case 5:
				Exploretab = null;
				break;
			case 6:
				CampusGame = null;
				break;
			case 7:
				studentsNearby = null;
				break;
			case 8:
				Inbox = null;
				break;
			case 9:
				CampusClubs = null;
				break;
			case 10:
				UserProfile = null;
				break;
			case 11:
				ProfileSettingsLeftPanel = null;
				break;
			case 12:
				rulesFragment = null;
			}
			
			tabs.removeFirst();
		}
	}
  
    @Override  
    public void seletedChildView(int groupPosition, int childPosition) {  
        switch (groupPosition) {  
        case 0: // ������  
            switch (childPosition) {  
            case 0:  
            	if (SocialSchedule == null){
            		clearOneTab();
            		tabs.addLast(0);
            		
            		SocialSchedule = new SocialSchedule(OohlalaMain.this); 
            	}
            	SocialSchedule.TopMenuNavbar.setOnSlideListener(OohlalaMain.this); 
                mSlideContainer.show(SocialSchedule);  
                break;  
            case 1:  
            	if (FriendsLeftPanel == null){
            		clearOneTab();
            		tabs.addLast(1);
            		
            		FriendsLeftPanel = new FriendsLeftPanel(OohlalaMain.this);
            	}
            	FriendsLeftPanel.TopMenuNavbar.setOnSlideListener(OohlalaMain.this); 
            	mSlideContainer.show(FriendsLeftPanel); 
                break;  
            case 2:  
            	if (CampusWall == null){
            		clearOneTab();
            		tabs.addLast(2);
            		
            		CampusWall = new CampusWall(OohlalaMain.this);
            	}
            	CampusWall.TopMenuNavbar.setOnSlideListener(OohlalaMain.this); 
            	mSlideContainer.show(CampusWall); 
                break;  
            case 3:  
            	if (PhotosLeftPanel == null){
            		clearOneTab();
            		tabs.addLast(3);
            		
            		PhotosLeftPanel = new PhotosLeftPanel(OohlalaMain.this);
            	}
            	PhotosLeftPanel.TopMenuNavbar.setOnSlideListener(OohlalaMain.this); 
            	mSlideContainer.show(PhotosLeftPanel); 
                break;  
            case 4:  
            	if (EventsLeftPanel == null){
            		clearOneTab();
            		tabs.addLast(4);
            		
            		EventsLeftPanel = new EventsLeftPanel(OohlalaMain.this);
            	}
            	EventsLeftPanel.TopMenuNavbar.setOnSlideListener(OohlalaMain.this); 
            	mSlideContainer.show(EventsLeftPanel); 
                break;  
            case 5: 
            	if (Exploretab == null){
            		clearOneTab();
            		tabs.addLast(5);
            		
            		Exploretab = new Exploretab(OohlalaMain.this, instance);
            	}
            	Exploretab.TopMenuNavbar.setOnSlideListener(OohlalaMain.this); 
            	mSlideContainer.show(Exploretab); 
            	break;
            case 6: 
            	if (CampusGame == null){
            		clearOneTab();
            		tabs.addLast(6);
            		
            		CampusGame = new CampusGame(OohlalaMain.this);
            	}
            	CampusGame.TopMenuNavbar.setOnSlideListener(OohlalaMain.this); 
            	mSlideContainer.show(CampusGame); 
            	break;
            case 7: 
            	if (studentsNearby == null){
            		clearOneTab();
            		tabs.addLast(7);
            		
            		studentsNearby = new studentsnearby.studentsNearby(OohlalaMain.this);
            	}
            	studentsNearby.TopMenuNavbar.setOnSlideListener(OohlalaMain.this); 
            	mSlideContainer.show(studentsNearby); 
            	break;
            case 8: 
            	if (Inbox == null){
            		clearOneTab();
            		tabs.addLast(8);
            		
            		Inbox = new Inbox(OohlalaMain.this);
            	}
            	Inbox.TopMenuNavbar.setOnSlideListener(OohlalaMain.this); 
            	mSlideContainer.show(Inbox);
            	break;
            case 9: 
            	if (CampusClubs == null){
            		clearOneTab();
            		tabs.addLast(9);
            		
            		CampusClubs = new rewards.CampusClubs(OohlalaMain.this);
            	}
            	CampusClubs.TopMenuNavbar.setOnSlideListener(OohlalaMain.this); 
            	mSlideContainer.show(CampusClubs); 
            	break;
            case 10:
            	if (UserProfile == null){
            		clearOneTab();
            		tabs.addLast(10);
            		
            		UserProfile = new UserProfile(OohlalaMain.this);
            	}
            	UserProfile.TopMenuNavbar.setOnSlideListener(OohlalaMain.this); 
            	mSlideContainer.show(UserProfile);
            	break;
            case 11:
            	if (ProfileSettingsLeftPanel == null){
            		clearOneTab();
            		tabs.addLast(11);
            		
            		ProfileSettingsLeftPanel = new ProfileSettingsLeftPanel(OohlalaMain.this);
            	}
            	ProfileSettingsLeftPanel.TopMenuNavbar.setOnSlideListener(OohlalaMain.this); 
            	mSlideContainer.show(ProfileSettingsLeftPanel);
            	break;
            case 12:
            	if (rulesFragment == null){
            		clearOneTab();
            		tabs.addLast(12);
            		
            		rulesFragment = new RulesFragment(OohlalaMain.this);
            	}
            	rulesFragment.TopMenuNavbar.setOnSlideListener(OohlalaMain.this); 
            	mSlideContainer.show(rulesFragment);
            	break;
  
            default:  
                break;  
            }  
              
            break;  
        case 1: // ������  
            switch (childPosition) {  
            case 0:  
  
                break;  
            case 1:  
  
                break;  
            case 2:  
  
                break;  
            case 3:  
  
                break;  
            default:  
                break;  
            }  
              
            break;  
        case 2: // ������  
            switch (childPosition) {  
            case 0:  
  
                break;  
            case 1:  
  
                break;  
            default:  
                break;  
            }  
              
            break;  
        default:  
            break;  
        }  
  
    }

	@Override
	public void toLeft() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toRight() {
		// TODO Auto-generated method stub
		mSlideContainer.slideToRight();  
		
		if (Profile.avatar_thumb_url != null && Profile.avatar_thumb_url.contains(".png")){
			TaskQueueImage.addTask(new LeftPanelLayout.loadThumbImage(), OohlalaMain.this);
		}
		TaskQueueImage.addTask(new checkAllNewThreads(), OohlalaMain.this);
	}  
	
	public class checkAllNewThreads extends Thread {
	    // This method is called when the thread runs
		int total_num = 0;
		
	    public void run() {
	    	int friends_num = 0;
			int inbox_num = 0;
			int campus_num = 0;
			int games_num = 0;
			
	    	//------------------------------ CAMPUS FEED UNREAD -------------------------------------
	    	RestClient result_Unread_Threads = null;
			try {
				result_Unread_Threads = new request().execute(Rest.CAMPUS_THREAD + "1;25" + "?unread_count_only=1", Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//Log.i("campus wall thread unread", result_UnreadThreads.getResponse());
			
			try {
				JSONObject num_unread_threads = new JSONObject(result_Unread_Threads.getResponse());
				
				int num_unread = num_unread_threads.getInt("num_unread_threads");
				if(num_unread > 0){
					campus_num = num_unread;
					Log.i("campus wall unread", "------------------- " + campus_num);
				}			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//------------------------------ CAMPUS FEED UNREAD -------------------------------------
			LeftPanelLayout.ChangeLeftPanelPositionNumber(2, campus_num);
			
			//------------------------------ GAMES UNREAD -------------------------------------
			RestClient result_Unread_Games = null;
			try {
				result_Unread_Games = new request().execute(Rest.GAME, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray games = new JSONArray(result_Unread_Games.getResponse());
				//Log.i("games: ", games.toString());
				//Log.i("games error: ", result.getErrorMessage());
				
				if (old_games_num > 0){
					if (old_games_num < games.length()){
						games_num = games.length() - old_games_num;
						Log.i("games unread", "------------------- " + games_num);
					}
				}
				old_games_num = games.length();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			//------------------------------ GAMES UNREAD -------------------------------------
			LeftPanelLayout.ChangeLeftPanelPositionNumber(6, games_num);
			
			//------------------------------ INBOX UNREAD -------------------------------------
	    	RestClient result_Unread_Inbox = null;
			try {
				result_Unread_Inbox = new request().execute(Rest.INBOX, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray inboxMsgs = new JSONArray(result_Unread_Inbox.getResponse());
				
				for (int i = 0; i < inboxMsgs.length(); i++){
					int num_unread = inboxMsgs.getJSONObject(i).getInt("num_unread");
					if(num_unread > 0){
						inbox_num = num_unread;
						Log.i("inbox unread", "------------------- " + inbox_num);
						
						break;
					}
				}				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//------------------------------ INBOX UNREAD -------------------------------------
			LeftPanelLayout.ChangeLeftPanelPositionNumber(8, inbox_num);
			
			//------------------------------ FRIENDS UNREAD -------------------------------------
			RestClient result_Unread_Friend_Requests = null;
			try {
				result_Unread_Friend_Requests = new request().execute(Rest.FRIEND_REQUEST + 1 + ";" + 25 + "?with_other_user_resource=1", Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray requests = new JSONArray(result_Unread_Friend_Requests.getResponse());
				//Log.i("requests: ", requests.toString());
				
				if (requests.length() > 0){
					friends_num = requests.length();
					Log.i("friends unread", "------------------- " + friends_num);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//------------------------------ FRIENDS UNREAD -------------------------------------
			LeftPanelLayout.ChangeLeftPanelPositionNumber(1, friends_num);
	    	
			total_num = friends_num + campus_num + games_num + inbox_num;
			Log.i("total unread", "---------        " + total_num);
			
	    	mHandler.post(new Runnable() {
        		public void run() {
        			if (total_num > 0){
        				if (SocialSchedule != null){
        					SocialSchedule.TopMenuNavbar.tvNumber.setVisibility(View.VISIBLE);  
        				}
        				if (CampusWall != null){
        					CampusWall.TopMenuNavbar.tvNumber.setVisibility(View.VISIBLE); 
        				}
        				if (CampusGame != null){
        					CampusGame.TopMenuNavbar.tvNumber.setVisibility(View.VISIBLE); 
        				}
        				if (CampusClubs != null){
        					CampusClubs.TopMenuNavbar.tvNumber.setVisibility(View.VISIBLE); 
        				}
        				if (UserProfile != null){
        					UserProfile.TopMenuNavbar.tvNumber.setVisibility(View.VISIBLE); 
        				}
        				if (Inbox != null){
        					Inbox.TopMenuNavbar.tvNumber.setVisibility(View.VISIBLE); 
        				}
        				if (studentsNearby != null){
        					studentsNearby.TopMenuNavbar.tvNumber.setVisibility(View.VISIBLE); 
        				}
        				if (EventsLeftPanel != null){
        					EventsLeftPanel.TopMenuNavbar.tvNumber.setVisibility(View.VISIBLE); 
        				}
        				if (FriendsLeftPanel != null){
        					FriendsLeftPanel.TopMenuNavbar.tvNumber.setVisibility(View.VISIBLE); 
        				}
        				if (ProfileSettingsLeftPanel != null){
        					ProfileSettingsLeftPanel.TopMenuNavbar.tvNumber.setVisibility(View.VISIBLE); 
        				}
        				if (Exploretab != null){
        					Exploretab.TopMenuNavbar.tvNumber.setVisibility(View.VISIBLE); 
        				}
        				if (PhotosLeftPanel != null){
        					PhotosLeftPanel.TopMenuNavbar.tvNumber.setVisibility(View.VISIBLE); 
        				}
        				if (rulesFragment != null){
        					rulesFragment.TopMenuNavbar.tvNumber.setVisibility(View.VISIBLE); 
        				}
	        		    
	        		    if (total_num > 10){
	        		    	if (SocialSchedule != null){
	        		    		SocialSchedule.TopMenuNavbar.tvNumber.setText("10+");  
	        		    	}
	        		    	if (CampusWall != null){
	        		    		CampusWall.TopMenuNavbar.tvNumber.setText("10+"); 
	        		    	}
	        		    	if (CampusGame != null){
	        		    		CampusGame.TopMenuNavbar.tvNumber.setText("10+"); 
	        		    	}
	        		    	if (CampusClubs != null){
	        		    		CampusClubs.TopMenuNavbar.tvNumber.setText("10+"); 
	        		    	}
	        		    	if (UserProfile != null){
	        		    		UserProfile.TopMenuNavbar.tvNumber.setText("10+"); 
	        		    	}
	        		    	if (Inbox != null){
	        		    		Inbox.TopMenuNavbar.tvNumber.setText("10+"); 
	        		    	}
	        		    	if (studentsNearby != null){
	        		    		studentsNearby.TopMenuNavbar.tvNumber.setText("10+"); 
	        		    	}
	        		    	if (EventsLeftPanel != null){
	        		    		EventsLeftPanel.TopMenuNavbar.tvNumber.setText("10+"); 
	        		    	}
	        		    	if (FriendsLeftPanel != null){
	        		    		FriendsLeftPanel.TopMenuNavbar.tvNumber.setText("10+"); 
	        		    	}
	        		    	if (ProfileSettingsLeftPanel != null){
	        		    		ProfileSettingsLeftPanel.TopMenuNavbar.tvNumber.setText("10+");
	        		    	}
	        		    	if (Exploretab != null){
	        					Exploretab.TopMenuNavbar.tvNumber.setText("10+");
	        				}
	        		    	if (PhotosLeftPanel != null){
	        					PhotosLeftPanel.TopMenuNavbar.tvNumber.setText("10+");
	        				}
	        		    	if (rulesFragment != null){
	        					rulesFragment.TopMenuNavbar.tvNumber.setText("10+");
	        				}
	        		    } else {
	        		    	if (SocialSchedule != null){
	        		    		SocialSchedule.TopMenuNavbar.tvNumber.setText(String.valueOf(total_num));  
	        		    	}
	        		    	if (CampusWall != null){
	        		    		CampusWall.TopMenuNavbar.tvNumber.setText(String.valueOf(total_num)); 
	        		    	}
	        		    	if (CampusGame != null){
	        		    		CampusGame.TopMenuNavbar.tvNumber.setText(String.valueOf(total_num)); 
	        		    	}
	        		    	if (CampusClubs != null){
	        		    		CampusClubs.TopMenuNavbar.tvNumber.setText(String.valueOf(total_num)); 
	        		    	}
	        		    	if (UserProfile != null){
	        		    		UserProfile.TopMenuNavbar.tvNumber.setText(String.valueOf(total_num)); 
	        		    	}
	        		    	if (Inbox != null){
	        		    		Inbox.TopMenuNavbar.tvNumber.setText(String.valueOf(total_num)); 
	        		    	}
	        		    	if (studentsNearby != null){
	        		    		studentsNearby.TopMenuNavbar.tvNumber.setText(String.valueOf(total_num)); 
	        		    	}
	        		    	if (EventsLeftPanel != null){
	        		    		EventsLeftPanel.TopMenuNavbar.tvNumber.setText(String.valueOf(total_num)); 
	        		    	}
	        		    	if (FriendsLeftPanel != null){
	        		    		FriendsLeftPanel.TopMenuNavbar.tvNumber.setText(String.valueOf(total_num)); 
	        		    	}
	        		    	if (ProfileSettingsLeftPanel != null){
	        		    		ProfileSettingsLeftPanel.TopMenuNavbar.tvNumber.setText(String.valueOf(total_num)); 
	        		    	}
	        		    	if (Exploretab != null){
	        					Exploretab.TopMenuNavbar.tvNumber.setText(String.valueOf(total_num));
	        				}
	        		    	if (PhotosLeftPanel != null){
	        					PhotosLeftPanel.TopMenuNavbar.tvNumber.setText(String.valueOf(total_num));
	        				}
	        		    	if (rulesFragment != null){
	        					rulesFragment.TopMenuNavbar.tvNumber.setText(String.valueOf(total_num));
	        				}
	        		    }
        			} else {
        				if (SocialSchedule != null){
        					SocialSchedule.TopMenuNavbar.tvNumber.setVisibility(View.GONE);  
        				}
        				if (CampusWall != null){
        					CampusWall.TopMenuNavbar.tvNumber.setVisibility(View.GONE); 
        				}
        				if (CampusGame != null){
        					CampusGame.TopMenuNavbar.tvNumber.setVisibility(View.GONE); 
        				}
        				if (CampusClubs != null){
	        		    	CampusClubs.TopMenuNavbar.tvNumber.setVisibility(View.GONE);
        				}
        				if (UserProfile != null){
        					UserProfile.TopMenuNavbar.tvNumber.setVisibility(View.GONE); 
        				}
        				if (Inbox != null){
        					Inbox.TopMenuNavbar.tvNumber.setVisibility(View.GONE);
        				}
        				if (studentsNearby != null){
	        		    	studentsNearby.TopMenuNavbar.tvNumber.setVisibility(View.GONE);
        				}
        				if (EventsLeftPanel != null){
        					EventsLeftPanel.TopMenuNavbar.tvNumber.setVisibility(View.GONE); 
        				}
        				if (FriendsLeftPanel != null){
        					FriendsLeftPanel.TopMenuNavbar.tvNumber.setVisibility(View.GONE); 
        				}
        				if (ProfileSettingsLeftPanel != null){
        					ProfileSettingsLeftPanel.TopMenuNavbar.tvNumber.setVisibility(View.GONE); 
        				}
        				if (Exploretab != null){
        					Exploretab.TopMenuNavbar.tvNumber.setVisibility(View.GONE); 
        				}
        				if (PhotosLeftPanel != null){
        					PhotosLeftPanel.TopMenuNavbar.tvNumber.setVisibility(View.GONE); 
        				}
        				if (rulesFragment != null){
        					rulesFragment.TopMenuNavbar.tvNumber.setVisibility(View.GONE); 
        				}
        			}	
        		}
        	});
	    }
    }
	 
}