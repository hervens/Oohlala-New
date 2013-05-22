package profile;

import inbox.Inbox;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import launchOohlala.CheckEmail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.UserProfile.UserProfileBadges;
import profile.UserProfile.UserProfileClubs;
import profile.UserProfile.UserProfileFriends;
import profile.UserProfile.checkCourseAvail;
import profile.UserProfile.loadThumbImage;

import network.ErrorCodeParser;
import network.RetrieveData;

import rewards.Rewards;
import rewards.RewardsVenuesStoresDeal;
import rewards.ScheduleModel;
import smackXMPP.XMPPClient;

import user.Profile;
import ManageThreads.TaskQueue;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import campusmap.CampusMap;
import campuswall.CampusWallComment;
import campuswall.CampusWallImage;

import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.UnreadNumCheck;

import datastorage.CacheInternalStorage;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.LoginState;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import datastorage.UserLoginInfo;
import datastorage.Rest.request;

public class OthersProfile extends ActivityGroup {
	int user_id;
	String jid;
	
	static TextView tvRelationship;
	static TextView tvEducation;
	static TextView tvProfileName;
	static ImageView ivThumb;
	static EditText etStatus;
	RelativeLayout rlPbox1, rlPbox2, rlPbox3, rlPbox4, rlButtons;
	TextView tvPbox1, tvPbox2, tvPbox3, tvPbox4;
	ImageView ivBadge1, ivBadge2, ivBadge3, ivClub1, ivClub2, ivClub3, ivClass1, ivClass2, ivClass3, ivFriend1, ivFriend2, ivFriend3, ivRedDot;
	Button bChat, bSettings, bMore, bAddFriend;
	
	TextView headerUserProfile;
	
	Handler mHandler = new Handler();
	
	String avatar, avatar_big, first_name, last_name, school_full_name, looking_for, birthdate;
	int relationship_status;
	
	boolean currentView = false;
	
	boolean is_friend = false;
	
	LinearLayout llbox1;
	
	boolean has_schedule;
	Button bRequest;
	TextView tvCurrentClass;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.othersprofile);
		
		currentView = true;

		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    });
		
		//content
		Bundle b = this.getIntent().getExtras();
		user_id = b.getInt("user_id");
		
		RestClient result = null;
		try {
			result = new Rest.request().execute(Rest.USER + user_id, Rest.OSESS + Profile.sk, Rest.GET).get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.i("request_profile: ", result.getResponse());
				
		if (result.getResponseCode() == 200) {
			try {
				JSONObject request_profile = new JSONObject(result.getResponse());
				Log.i("request_profile", String.valueOf(request_profile));
				jid = request_profile.getString("jid");
				avatar = request_profile.getString("avatar_thumb_url");
				avatar_big = request_profile.getString("avatar_url");
				first_name = request_profile.getString("firstname");
				last_name = request_profile.getString("lastname");
				relationship_status = request_profile.getInt("relationship_status");
				school_full_name = request_profile.getString("school_name");
				looking_for = request_profile.getString("looking_for");
				is_friend = request_profile.getBoolean("is_friend");
				has_schedule = request_profile.getBoolean("has_schedule");
				//birthdate = request_profile.getString("birthdate");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		headerUserProfile = (TextView) findViewById(R.id.header);
		headerUserProfile.setTypeface(Fonts.getOpenSansBold(OthersProfile.this));
		headerUserProfile.setText(first_name.toUpperCase() + " " + last_name.toUpperCase());
	
		//content
		Button bInvite = (Button) findViewById(R.id.bInvite);
		ivThumb = (ImageView) findViewById(R.id.ivThumb);
		tvProfileName = (TextView) findViewById(R.id.tvProfileName);
		etStatus = (EditText) findViewById(R.id.etStatus);
		tvRelationship = (TextView) findViewById(R.id.tvRelationship);
		tvEducation = (TextView) findViewById(R.id.tvEducation);
		
		rlPbox1 = (RelativeLayout) findViewById(R.id.rlPbox1);
		rlPbox2 = (RelativeLayout) findViewById(R.id.rlPbox2);
		rlPbox3 = (RelativeLayout) findViewById(R.id.rlPbox3);
		rlPbox4 = (RelativeLayout) findViewById(R.id.rlPbox4);
		
		tvPbox1 = (TextView) findViewById(R.id.tvPbox1);
		tvPbox2 = (TextView) findViewById(R.id.tvPbox2);
		tvPbox3 = (TextView) findViewById(R.id.tvPbox3);
		tvPbox4 = (TextView) findViewById(R.id.tvPbox4);
		
		ivBadge1 = (ImageView) findViewById(R.id.ivBadge1);
		ivBadge2 = (ImageView) findViewById(R.id.ivBadge2);
		ivBadge3 = (ImageView) findViewById(R.id.ivBadge3);
		ivClub1 = (ImageView) findViewById(R.id.ivClub1);
		ivClub2 = (ImageView) findViewById(R.id.ivClub2);
		ivClub3 = (ImageView) findViewById(R.id.ivClub3);
		ivClass1 = (ImageView) findViewById(R.id.ivClass1);
		ivClass2 = (ImageView) findViewById(R.id.ivClass2);
		ivClass3 = (ImageView) findViewById(R.id.ivClass3);
		ivFriend1 = (ImageView) findViewById(R.id.ivFriend1);
		ivFriend2 = (ImageView) findViewById(R.id.ivFriend2);
		ivFriend3 = (ImageView) findViewById(R.id.ivFriend3);
		
		bChat = (Button) findViewById(R.id.bChat);
		bMore = (Button) findViewById(R.id.bMore);
		bAddFriend = (Button) findViewById(R.id.bAddFriend);
		rlButtons = (RelativeLayout) findViewById(R.id.rlButtons);
		bRequest = (Button) findViewById(R.id.bRequest);
		tvCurrentClass = (TextView) findViewById(R.id.tvCurrentClass);
		
		llbox1 = (LinearLayout) findViewById(R.id.llbox1);
		
		rlPbox1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Bundle extras = new Bundle();
				extras.putInt("user_id", user_id);
            	Intent i = new Intent(OthersProfile.this, Friends.class);
            	i.putExtras(extras);
				startActivity(i);
            }
        }); 
		
		if (!has_schedule){
			bRequest.setVisibility(View.VISIBLE);
			bRequest.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	AlertDialog alert = new AlertDialog.Builder(OthersProfile.this).create();
		    		alert.setMessage(getString(R.string.Schedule_not_available_alert));
		    		alert.setButton(getString(R.string.No), new DialogInterface.OnClickListener() {
		    			
		    			public void onClick(DialogInterface dialog, int which) {
		    				// TODO Auto-generated method stub
		    			}
		    		});
		    		alert.setButton2(getString(R.string.Yes), new DialogInterface.OnClickListener() {
		    			
		    			public void onClick(DialogInterface dialog, int which) {
		    				TaskQueueImage.addTask(new courseScheduleRequest(), OthersProfile.this);
		    			}
		    		});
		    		alert.show();
	            }
			});
		}
			
		rlPbox2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	if (has_schedule){
	            	Bundle extras = new Bundle();
					extras.putInt("user_id", user_id);
	            	Intent i = new Intent(OthersProfile.this, Schedule.class);
	            	i.putExtras(extras);
					startActivity(i);
            	} 
            }
        }); 
		
		rlPbox3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Bundle extras = new Bundle();
				extras.putInt("user_id", user_id);
            	Intent i = new Intent(OthersProfile.this, Badge.class);
            	i.putExtras(extras);
				startActivity(i);
            }
        }); 
		
		rlPbox4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Bundle extras = new Bundle();
				extras.putInt("user_id", user_id);
            	Intent i = new Intent(OthersProfile.this, FavClubs.class);
            	i.putExtras(extras);
				startActivity(i);
            }
        }); 
		
		//set up profile picture
		if (avatar != null && avatar.contains(".png")){
			TaskQueueImage.addTask(new loadThumbImage(), OthersProfile.this);
		}
		
		tvProfileName.setTypeface(Fonts.getOpenSansBold(getApplicationContext()));
		tvProfileName.setText(first_name + " " + last_name);
		etStatus.setText(looking_for);
		
		String relationship = null;
		switch (relationship_status){
		case 0: 
			relationship = getString(R.string.Single);
			break;
		case 1:
			relationship = getString(R.string.In_a_relationship);
			break;
		case 2: 
			relationship = getString(R.string.Complicated);
			break;
		case 3: 
			relationship = getString(R.string.Available);
			break;
		}
		tvRelationship.setTypeface(Fonts.getOpenSansBold(getApplicationContext()));
		tvRelationship.setText(relationship);
		
		tvEducation.setTypeface(Fonts.getOpenSansBold(getApplicationContext()));
		tvEducation.setText(school_full_name);
		
		if (user_id == Profile.userId){
			rlButtons.setVisibility(View.GONE);
			bMore.setVisibility(View.GONE);
			TaskQueueImage.addTask(new checkCourseAvail(), OthersProfile.this);
		}
		bChat.setTypeface(Fonts.getOpenSansBold(OthersProfile.this));
		bChat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Bundle extras = new Bundle();
				extras.putString("jid", jid);
				extras.putString("first_name", first_name);
				extras.putString("last_name", last_name);
				extras.putString("avatar", avatar);
				extras.putInt("user_id", user_id);
				
				Intent i = new Intent(OthersProfile.this, XMPPClient.class);
				i.setFlags(PendingIntent.FLAG_CANCEL_CURRENT);
				i.putExtras(extras);
				startActivity(i);
            }
        }); 
		
		bAddFriend.setTypeface(Fonts.getOpenSansBold(OthersProfile.this));
		bAddFriend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	showAddFriendAlertDialog();
            }
        }); 
		if (is_friend){
			bAddFriend.setVisibility(View.GONE);
			bChat.setBackgroundResource(R.drawable.button_chat_full);
			
			bMore.setVisibility(View.VISIBLE);
		} else if (user_id != Profile.userId){
			//llbox1.setVisibility(View.GONE);
			rlPbox1.setClickable(false);
			rlPbox2.setClickable(false);
		}
		
		bMore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	final CharSequence[] items = {getString(R.string.Unfriend), getString(R.string.Cancel)};
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(OthersProfile.this);
        		builder.setItems(items, new DialogInterface.OnClickListener() {
        		    public void onClick(DialogInterface dialog, int item) {
        		    	switch(item){
        		    	case 0:
        		    		AlertDialog alert = new AlertDialog.Builder(OthersProfile.this).create();
        		    		alert.setMessage(getString(R.string.Unfriend_Request));
        		    		alert.setButton(getString(R.string.No), new DialogInterface.OnClickListener() {
        		    			
        		    			public void onClick(DialogInterface dialog, int which) {
        		    				// TODO Auto-generated method stub
        		    			}
        		    		});
        		    		alert.setButton2(getString(R.string.Yes), new DialogInterface.OnClickListener() {
        		    			
        		    			public void onClick(DialogInterface dialog, int which) {
        		    				// TODO Auto-generated method stub
        		    				RestClient result2 = null;
        		    				try {
        		    					result2 = new Rest.requestBody().execute(Rest.USER + user_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "unfriend", "1").get();
        		    				} catch (InterruptedException e1) {
        		    					// TODO Auto-generated catch block
        		    					e1.printStackTrace();
        		    				} catch (ExecutionException e1) {
        		    					// TODO Auto-generated catch block
        		    					e1.printStackTrace();
        		    				}
        		    				Log.i("requests: ", String.valueOf(result2.getResponseCode()));
        		    				if (result2.getResponseCode() == 200){
        		    					Toast.makeText(getApplicationContext(), getString(R.string.Unfriend_Successfully), Toast.LENGTH_SHORT).show(); 
        		    					
        		    					bAddFriend.setText(getString(R.string.Add_Friend));
        		    					bAddFriend.setClickable(true);
        		    					bMore.setVisibility(View.GONE);
        		    				}
        		    			}
        		    		});
        		    		alert.show();
        		    		break;
        		    	case 1:
        		    		
        		    		break;
        		    	}
        		    }
        		});
        		AlertDialog alert = builder.create();
        		alert.show();
            }
        }); 
		
		TaskQueueImage.addTask(new checkFriendRequest(), OthersProfile.this);
		TaskQueueImage.addTask(new UserProfileClubs(), OthersProfile.this);
		TaskQueueImage.addTask(new UserProfileBadges(), OthersProfile.this);
		TaskQueueImage.addTask(new UserProfileFriends(), OthersProfile.this);
		if (has_schedule){
			TaskQueueImage.addTask(new checkCurrentCourse(), OthersProfile.this);
		}
	}
	
	class loadThumbImage extends Thread {
	    // This method is called when the thread runs
	    public void run() {
			mHandler.post(new Runnable() {
        		public void run() {
        			if (currentView){
						ivThumb.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(avatar, getBaseContext(), 90));
						ivThumb.setOnClickListener(new View.OnClickListener() {
				            public void onClick(View view) {
				            	Bundle extras = new Bundle();
								extras.putString("image_url", avatar_big);
								extras.putInt("picWidth", 512);
								Intent i = new Intent(OthersProfile.this, CampusWallImage.class);
								i.putExtras(extras);
								startActivity(i);
				            }
				        }); 
        			}
				}
			});
		}
    }
	
	class UserProfileClubs2 extends AsyncTask<Void, Void, String[]> {
	    // This method is called when the thread runs

		protected String[] doInBackground(Void... cuts) {
	    	//get the all the current games			
			RestClient result = null;
			try {
				result = new request().execute(Rest.STORE + "?fav_only=1&user_id=" + user_id, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String[] clubs = {null, null, null};
			
			try {
				JSONArray threads = new JSONArray(result.getResponse());
				Log.i("fav clubs: ", threads.toString());
				
				int k = 0;
				for (int i = 0; i < threads.length(); i++){
					clubs[k] = threads.getJSONObject(i).getString("logo_url");
					if (clubs[k] != null){
						k++;
					}
							
					if (k == 3 || k == clubs.length){
						break;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return clubs;
	    }
		
		protected void onPostExecute(final String[] clubs) {
			if (clubs[0] != null){
        		ivClub1.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(clubs[0], OthersProfile.this, 47));
        	}
        	if (clubs[1] != null){
        		ivClub2.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(clubs[1], OthersProfile.this, 47));
        	}
        	if (clubs[2] != null){
        		ivClub3.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(clubs[2], OthersProfile.this, 47));
        	}
	    }
	}
	
	class UserProfileClubs extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	RestClient result = null;
			try {
				result = new request().execute(Rest.STORE + "?fav_only=1&user_id=" + user_id, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			final String[] clubs = {null, null, null};
			
			try {
				JSONArray threads = new JSONArray(result.getResponse());
				Log.i("fav clubs: ", threads.toString());
				
				int k = 0;
				for (int i = 0; i < threads.length(); i++){
					clubs[k] = threads.getJSONObject(i).getString("logo_url");
					if (clubs[k] != null){
						k++;
					}
							
					if (k == 3 || k == clubs.length){
						break;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (currentView){
				mHandler.post(new Runnable() {
	        		public void run() {
						if (clubs[0] != null){
			        		ivClub1.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(clubs[0], OthersProfile.this, 47));
			        	}
			        	if (clubs[1] != null){
			        		ivClub2.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(clubs[1], OthersProfile.this, 47));
			        	}
			        	if (clubs[2] != null){
			        		ivClub3.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(clubs[2], OthersProfile.this, 47));
			        	}
	        		}
				});
			}
	    }
	}
	
    class UserProfileBadges2 extends AsyncTask<Void, Void, String[]> {
	    // This method is called when the thread runs

		protected String[] doInBackground(Void... cuts) {
	    	//get the all the current games			
			RestClient result = null;
			try {
				result = new request().execute(Rest.BADGE + "?unlocked_only=1&user_id=" + user_id, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String[] badges = {null, null, null};
			
			try {
				JSONArray threads = new JSONArray(result.getResponse());
				Log.i("badges: ", threads.toString());
				
				int k = 0;
				for (int i = 0; i < threads.length(); i++){
					badges[k] = threads.getJSONObject(i).getString("badge_icon_thumb_url");
					if (badges[k] != null){
						k++;
					}
							
					if (k == 3 || k == badges.length){
						break;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return badges;
	    }
		
		protected void onPostExecute(final String[] badges) {
			if (badges[0] != null){
				ivBadge1.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(badges[0], OthersProfile.this, 47));
        	}
        	if (badges[1] != null){
        		ivBadge2.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(badges[1], OthersProfile.this, 47));
        	}
        	if (badges[2] != null){
        		ivBadge3.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(badges[2], OthersProfile.this, 47));
        	}
	    }
	}
    
    class UserProfileBadges extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	RestClient result = null;
			try {
				result = new request().execute(Rest.BADGE + "?unlocked_only=1&user_id=" + user_id, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			final String[] badges = {null, null, null};
			
			try {
				JSONArray threads = new JSONArray(result.getResponse());
				Log.i("badges: ", threads.toString());
				
				int k = 0;
				for (int i = 0; i < threads.length(); i++){
					badges[k] = threads.getJSONObject(i).getString("badge_icon_thumb_url");
					if (badges[k] != null){
						k++;
					}
							
					if (k == 3 || k == badges.length){
						break;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (currentView){
				mHandler.post(new Runnable() {
	        		public void run() {
						if (badges[0] != null){
							ivBadge1.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(badges[0], OthersProfile.this, 47));
			        	}
			        	if (badges[1] != null){
			        		ivBadge2.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(badges[1], OthersProfile.this, 47));
			        	}
			        	if (badges[2] != null){
			        		ivBadge3.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(badges[2], OthersProfile.this, 47));
			        	}
	        		}
				});
			}
	    }
    }
    
    class UserProfileFriends2 extends AsyncTask<Void, Void, String[]> {
	    // This method is called when the thread runs

		protected String[] doInBackground(Void... cuts) {
	    	//get the all the current games			
			RestClient result = null;
			try {
				if (user_id == Profile.userId){
					result = new request().execute(Rest.USER + "1;9999" + "?friends_only=1", Rest.OSESS + Profile.sk, Rest.GET).get();
				} else {
					result = new request().execute(Rest.USER + user_id + "?with_friends=1", Rest.OSESS + Profile.sk, Rest.GET).get();
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String[] friends = {null, null, null};
			
			if (user_id == Profile.userId){
				try {
					final JSONArray threads = new JSONArray(result.getResponse());
					Log.i("friends: ", threads.toString());
					
					mHandler.post(new Runnable() {
		        		public void run() {
		        			tvPbox1.setText(threads.length() + " " + getString(R.string.friends));
		        		}
					});
					
					int k = 0;
					for (int i = 0; i < threads.length(); i++){
						friends[k] = threads.getJSONObject(i).getString("avatar_thumb_url");
						if (friends[k] != null){
							k++;
						}
								
						if (k == 3 || k == friends.length){
							break;
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					final JSONArray threads = new JSONObject(result.getResponse()).getJSONArray("friend_list");
					Log.i("friends: ", threads.toString());
					
					mHandler.post(new Runnable() {
		        		public void run() {
		        			tvPbox1.setText(threads.length() + " " + getString(R.string.friends));
		        		}
					});
					
					int k = 0;
					for (int i = 0; i < threads.length(); i++){
						friends[k] = threads.getJSONObject(i).getString("avatar_thumb_url");
						if (friends[k] != null){
							k++;
						}
								
						if (k == 3 || k == friends.length){
							break;
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return friends;
	    }
		
		protected void onPostExecute(final String[] friends) {
			if (friends[0] != null){
				ivFriend1.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(friends[0], OthersProfile.this, 47));
        	}
        	if (friends[1] != null){
        		ivFriend2.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(friends[1], OthersProfile.this, 47));
        	}
        	if (friends[2] != null){
        		ivFriend3.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(friends[2], OthersProfile.this, 47));
        	}
	    }
	}
    
    class UserProfileFriends extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	RestClient result = null;
			try {
				if (user_id == Profile.userId){
					result = new request().execute(Rest.USER + "1;9999" + "?friends_only=1", Rest.OSESS + Profile.sk, Rest.GET).get();
				} else {
					result = new request().execute(Rest.USER + user_id + "?with_friends=1&start=1&end=9999", Rest.OSESS + Profile.sk, Rest.GET).get();
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			final String[] friends = {null, null, null};
			
			if (user_id == Profile.userId){
				try {
					final JSONArray threads = new JSONArray(result.getResponse());
					Log.i("friends: ", threads.toString());
					
					mHandler.post(new Runnable() {
		        		public void run() {
		        			tvPbox1.setText(threads.length() + " " + getString(R.string.friends));
		        		}
					});
					
					int k = 0;
					for (int i = 0; i < threads.length(); i++){
						friends[k] = threads.getJSONObject(i).getString("avatar_thumb_url");
						if (friends[k] != null){
							k++;
						}
								
						if (k == 3 || k == friends.length){
							break;
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					final JSONArray threads = new JSONObject(result.getResponse()).getJSONArray("friend_list");
					Log.i("friends: ", threads.toString());
					
					mHandler.post(new Runnable() {
		        		public void run() {
		        			tvPbox1.setText(threads.length() + " " + getString(R.string.friends));
		        		}
					});
					
					int k = 0;
					for (int i = 0; i < threads.length(); i++){
						friends[k] = threads.getJSONObject(i).getString("avatar_thumb_url");
						if (friends[k] != null){
							k++;
						}
								
						if (k == 3 || k == friends.length){
							break;
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (currentView){
				mHandler.post(new Runnable() {
	        		public void run() {
						if (friends[0] != null){
							ivFriend1.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(friends[0], OthersProfile.this, 47));
			        	}
			        	if (friends[1] != null){
			        		ivFriend2.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(friends[1], OthersProfile.this, 47));
			        	}
			        	if (friends[2] != null){
			        		ivFriend3.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(friends[2], OthersProfile.this, 47));
			        	}
	        		}
				});
			}
	    }
    }
    
    private void showAddFriendAlertDialog() {
		// TODO Auto-generated method stub
    	AlertDialog alert = new AlertDialog.Builder(OthersProfile.this).create();
		alert.setMessage(getString(R.string.Send_Friend_Request));
		alert.setButton(getString(R.string.No), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		alert.setButton2(getString(R.string.Yes), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				RestClient result2 = null;
				try {
					result2 = new Rest.requestBody().execute(Rest.FRIEND_REQUEST, Rest.OSESS + Profile.sk, Rest.POST, "1", "recipient_user_id", String.valueOf(user_id)).get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.i("requests: ", String.valueOf(result2.getResponseCode()));
				if (result2.getResponseCode() == 201){
					bAddFriend.setText(getString(R.string.Pending));
					bAddFriend.setClickable(false);
				}
			}
		});
		alert.show();
	}
    
    class checkFriendRequest extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	RestClient result = null;
			try {
				result = new request().execute(Rest.FRIEND_REQUEST + "?other_user_id=" + user_id, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray requests = new JSONArray(result.getResponse());
				Log.i("requests: ", requests.toString());
				
				if (requests.length() > 0){
					mHandler.post(new Runnable() {
		        		public void run() {
		        			bAddFriend.setText(getString(R.string.Pending));
		        			bAddFriend.setClickable(false);
		        		}
					});
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
    }
    
    class checkCourseAvail extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
			// TODO Auto-generated method stub
			//Get User Chat History
			RestClient result_checkCourseAvail = null;
			try {
				result_checkCourseAvail = new request().execute(Rest.SCHOOL_COURSE + "?check_course_avail=1", Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				boolean course_available = new JSONObject(result_checkCourseAvail.getResponse()).getBoolean("course_available");
				if (!course_available){
					mHandler.post(new Runnable() {
				        public void run() {
				        	rlPbox2.setOnClickListener(new View.OnClickListener() {
					            public void onClick(View view) {
					            	AlertDialog alert = new AlertDialog.Builder(OthersProfile.this).create();
		        		    		alert.setMessage(getString(R.string.Course_not_available_alert));
		        		    		alert.setButton(getString(R.string.No), new DialogInterface.OnClickListener() {
		        		    			
		        		    			public void onClick(DialogInterface dialog, int which) {
		        		    				// TODO Auto-generated method stub
		        		    			}
		        		    		});
		        		    		alert.setButton2(getString(R.string.Yes), new DialogInterface.OnClickListener() {
		        		    			
		        		    			public void onClick(DialogInterface dialog, int which) {
		        		    				String email = "info@gotoohlala.com";
		        		                	Intent emailIntent = new Intent(Intent.ACTION_SEND);
		        		                    emailIntent.setType("plain/text");
		        		                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
		        		                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Course Request: " + Profile.schoolName);
		        		                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
		        		                    startActivity(Intent.createChooser(emailIntent, getString(R.string.Send_email_via)));
		        		    			}
		        		    		});
		        		    		alert.show();
					            }
					        });
				        }
					});
				}		
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    
    class courseScheduleRequest extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	RestClient result = null;
			try {
				result = new Rest.requestBody().execute(Rest.USER_REQUEST, Rest.OSESS + Profile.sk, Rest.POST, "2", "recipient_user_id", String.valueOf(user_id), "request_type", "1").get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (result.getResponseCode() == 201){
				mHandler.post(new Runnable() {
	        		public void run() {
	        			AlertDialog alert = new AlertDialog.Builder(OthersProfile.this).create();
    		    		alert.setMessage(getString(R.string.Request_Sent));
    		    		alert.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
    		    			
    		    			public void onClick(DialogInterface dialog, int which) {
    		    				// TODO Auto-generated method stub
    		    			}
    		    		});
    		    		alert.show();
	        		}
				});
			}
	    }
	}
    
    class checkCurrentCourse extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	RestClient result = null;
			try {
				if (user_id != Profile.userId){
					result = new request().execute(Rest.SCHOOL_COURSE + "?user_id=" + user_id, Rest.OSESS + Profile.sk, Rest.GET).get();
				} else {
					result = new request().execute(Rest.SCHOOL_COURSE, Rest.OSESS + Profile.sk, Rest.GET).get();
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray threads = new JSONArray(result.getResponse());
				Log.i("school courses: ", threads.toString());
				
				for (int i = 0; i < threads.length(); i++){
					int course_id = threads.getJSONObject(i).getInt("id");
					String course_name = threads.getJSONObject(i).getString("course_name");
					String course_code = threads.getJSONObject(i).getString("course_code");
					JSONArray time_info = threads.getJSONObject(i).getJSONArray("time_info");
					for (int j = 0; j < time_info.length(); j++){
						int course_time_id = time_info.getJSONObject(j).getInt("id");
						String location = time_info.getJSONObject(j).getString("location");
						boolean is_biweekly = time_info.getJSONObject(j).getBoolean("is_biweekly");
						int day_of_week = time_info.getJSONObject(j).getInt("day_of_week");
						int start_time = time_info.getJSONObject(j).getInt("start_time");
						int end_time = time_info.getJSONObject(j).getInt("end_time");
						
						if (TimeCounter.isEventGoingOn(String.valueOf(start_time), String.valueOf(end_time))){
							mHandler.post(new Runnable() {
				        		public void run() {
				        			rlPbox2.setBackgroundResource(R.drawable.bg_profile_box_red);
				        			tvCurrentClass.setVisibility(View.VISIBLE);
				        			tvCurrentClass.setText(getString(R.string.IN_CLASS));
				        			tvCurrentClass.setTypeface(Fonts.getOpenSansBold(OthersProfile.this));
				        		}
							});
						} else {
							mHandler.post(new Runnable() {
				        		public void run() {
				        			rlPbox2.setBackgroundResource(R.drawable.bg_profile_box);
				        			tvCurrentClass.setVisibility(View.VISIBLE);
				        			tvCurrentClass.setText(getString(R.string.FREE));
				        			tvCurrentClass.setTypeface(Fonts.getOpenSansBold(OthersProfile.this));
				        		}
							});
						}
					}
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
    }
    
	public void onResume() {
		super.onResume();
		
		currentView = true;
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		currentView = false;
	}
	
}
