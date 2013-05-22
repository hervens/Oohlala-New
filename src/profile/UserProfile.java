package profile;

import inbox.Inbox;
import inbox.InboxModel;
import inbox.Inbox.TimeComparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.OthersProfile.checkCourseAvail;
import profile.OthersProfile.checkCurrentCourse;

import network.RetrieveData;

import rewards.ExploreModel;
import rewards.Rewards;
import rewards.RewardsVenuesStoresDeal;
import studentsnearby.Picture;

import user.Profile;
import ManageThreads.TaskQueue;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import campusmap.CampusMap;
import campuswall.CampusWall;
import campuswall.CampusWallComment;
import campuswall.CampusWallCommentPostComment;
import campuswall.CampusWallImage;
import campuswall.CampusWallInviteFacebook;
import campuswall.CampusWallPostInterface;

import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.TopMenuNavbar;
import com.gotoohlala.UnreadNumCheck;

import datastorage.Base64;
import datastorage.CacheInternalStorage;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import datastorage.Rest.request;

public class UserProfile extends FrameLayout implements UpdateStatusInterface {

	static TextView tvRelationship;
	static TextView tvEducation;
	static TextView tvProfileName;
	static ImageView ivThumb;
	static EditText etStatus;
	RelativeLayout rlPbox1, rlPbox2, rlPbox3, rlPbox4;
	TextView tvPbox1, tvPbox2, tvPbox3, tvPbox4;
	ImageView ivBadge1, ivBadge2, ivBadge3, ivClub1, ivClub2, ivClub3, ivClass1, ivClass2, ivClass3, ivFriend1, ivFriend2, ivFriend3;
	Button bSettings;
	
	//YOU CAN EDIT THIS TO WHATEVER YOU WANT
    private static final int SELECT_PICTURE = 1;
    private static final int CROP_PICTURE = 2;
    private static final int CAMERA_PIC_REQUEST = 1337;  

    private String selectedImagePath;
    //ADDED
    private String filemanagerstring;
	
    static View v = null;
	private static Handler mHandler = new Handler();
	
	private Uri mImageCaptureUri;
	
	static TextView headerUserProfile;
	boolean currentView = false;
	
	TextView tvCurrentClass;
	
	public TopMenuNavbar TopMenuNavbar;
	
	public UserProfile(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
		v = (RelativeLayout) mLayoutInflater.inflate(R.layout.userprofile, null);  
	    addView(v);
	    
	    TopMenuNavbar = (TopMenuNavbar) v.findViewById(R.id.bTopMenuNavBar);
			
			currentView = true;
			
			headerUserProfile = (TextView) v.findViewById(R.id.header);
			headerUserProfile.setTypeface(Fonts.getOpenSansBold(getContext()));
			headerUserProfile.setText(Profile.firstName.toUpperCase() + " " + Profile.lastName.toUpperCase());
		
			//content
			Button bInvite = (Button) v.findViewById(R.id.bInvite);
			ivThumb = (ImageView) v.findViewById(R.id.ivThumb);
			tvProfileName = (TextView) v.findViewById(R.id.tvProfileName);
			etStatus = (EditText) v.findViewById(R.id.etStatus);
			tvRelationship = (TextView) v.findViewById(R.id.tvRelationship);
			tvEducation = (TextView) v.findViewById(R.id.tvEducation);
			
			rlPbox1 = (RelativeLayout) v.findViewById(R.id.rlPbox1);
			rlPbox2 = (RelativeLayout) v.findViewById(R.id.rlPbox2);
			rlPbox3 = (RelativeLayout) v.findViewById(R.id.rlPbox3);
			rlPbox4 = (RelativeLayout) v.findViewById(R.id.rlPbox4);
			
			tvPbox1 = (TextView) v.findViewById(R.id.tvPbox1);
			tvPbox2 = (TextView) v.findViewById(R.id.tvPbox2);
			tvPbox3 = (TextView) v.findViewById(R.id.tvPbox3);
			tvPbox4 = (TextView) v.findViewById(R.id.tvPbox4);
			
			ivBadge1 = (ImageView) v.findViewById(R.id.ivBadge1);
			ivBadge2 = (ImageView) v.findViewById(R.id.ivBadge2);
			ivBadge3 = (ImageView) v.findViewById(R.id.ivBadge3);
			ivClub1 = (ImageView) v.findViewById(R.id.ivClub1);
			ivClub2 = (ImageView) v.findViewById(R.id.ivClub2);
			ivClub3 = (ImageView) v.findViewById(R.id.ivClub3);
			ivClass1 = (ImageView) v.findViewById(R.id.ivClass1);
			ivClass2 = (ImageView) v.findViewById(R.id.ivClass2);
			ivClass3 = (ImageView) v.findViewById(R.id.ivClass3);
			ivFriend1 = (ImageView) v.findViewById(R.id.ivFriend1);
			ivFriend2 = (ImageView) v.findViewById(R.id.ivFriend2);
			ivFriend3 = (ImageView) v.findViewById(R.id.ivFriend3);
			
			tvCurrentClass = (TextView) v.findViewById(R.id.tvCurrentClass);
			
			bSettings = (Button) v.findViewById(R.id.bSettings);
			bSettings.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					Intent i = new Intent(getContext(), ProfileSettings.class);
					getContext().startActivity(i);
				}
		    }); 
	
			rlPbox1.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	Bundle extras = new Bundle();
					extras.putInt("user_id", Profile.userId);
	            	Intent i = new Intent(getContext(), Friends.class);
	            	i.putExtras(extras);
					getContext().startActivity(i);
	            }
	        }); 
			
			rlPbox2.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	Intent i = new Intent(getContext(), Schedule.class);
					getContext().startActivity(i);
	            }
	        }); 
			
			rlPbox3.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	Intent i = new Intent(getContext(), Badge.class);
					getContext().startActivity(i);
	            }
	        }); 
			
			rlPbox4.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	Intent i = new Intent(getContext(), FavClubs.class);
					getContext().startActivity(i);
	            }
	        }); 
			
			//set up profile picture
			if (Profile.avatar_thumb_url != null && Profile.avatar_thumb_url.contains(".png")){
				TaskQueueImage.addTask(new loadThumbImage(), v.getContext());
			}
			ivThumb.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					Bundle extras = new Bundle();
					extras.putString("image_url", Profile.avatar);
					extras.putInt("picWidth", 512);
					Intent i = new Intent(getContext(), CampusWallImage.class);
					i.putExtras(extras);
					getContext().startActivity(i);
				}
		    }); 
			
			tvProfileName.setTypeface(Fonts.getOpenSansBold(getContext()));
			tvProfileName.setText(Profile.firstName + " " + Profile.lastName);
			etStatus.setText(Profile.looking_for);
			etStatus.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					UpdateStatus.act = UserProfile.this;
					
					Bundle extras = new Bundle();
					extras.putString("looking_for", Profile.looking_for);
					Intent i = new Intent(getContext(), UpdateStatus.class);
					i.putExtras(extras);
					getContext().startActivity(i);
				}
			});
			
			String relationship = null;
			switch (Profile.relationStatus){
			case -1:
				relationship = getContext().getString(R.string.None);
				break;
			case 0: 
				relationship = getContext().getString(R.string.Single);
				break;
			case 1:
				relationship = getContext().getString(R.string.In_a_relationship);
				break;
			case 2: 
				relationship = getContext().getString(R.string.Complicated);
				break;
			case 3: 
				relationship = getContext().getString(R.string.Available);
				break;
			}
			tvRelationship.setTypeface(Fonts.getOpenSansBold(getContext()));
			tvRelationship.setText(relationship);
			
			tvEducation.setTypeface(Fonts.getOpenSansBold(getContext()));
			tvEducation.setText(Profile.schoolName);
			
			bInvite.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	Intent i = new Intent(getContext(), CampusWallInviteFacebook.class);
					getContext().startActivity(i);
	            }
	        }); 
					
			TaskQueueImage.addTask(new checkCourseAvail(), v.getContext());
			TaskQueueImage.addTask(new UserProfileClubs(), v.getContext());
			TaskQueueImage.addTask(new UserProfileBadges(), v.getContext());
			TaskQueueImage.addTask(new UserProfileFriends(), v.getContext());
			TaskQueueImage.addTask(new UserProfileInbox(), v.getContext());
			TaskQueueImage.addTask(new checkCurrentCourse(), v.getContext());							
			
			Profile.updateUserProfile();
	}
    
    
    class loadThumbImage extends Thread {
	    // This method is called when the thread runs
	    public void run() {
	    	if (currentView){
				mHandler.post(new Runnable() {
	        		public void run() {
	        			ivThumb.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(Profile.avatar_thumb_url, v.getContext(), 80));
					}
				});
	    	}
		}
    }

    public static void updateProfileAvatar(final String avatar_thumb_url){
    	mHandler.post(new Runnable() {
    		public void run() {
		    	Profile.updateUserProfile();
		    	ivThumb.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(avatar_thumb_url, v.getContext(), 80));
    		}
    	});
    }
    
    public static void updateProfileInfo(final String firstName, final String lastName, final int relationship_status){
    	mHandler.post(new Runnable() {
    		public void run() {
		    	Profile.updateUserProfile();
		    	
		    	headerUserProfile.setText(firstName.toUpperCase() + " " + lastName.toUpperCase());
		    	tvProfileName.setText(firstName + " " + lastName);
				etStatus.setText(Profile.looking_for);
				
				String relationship = null;
				switch (relationship_status){
				case -1:
					relationship = v.getContext().getString(R.string.None);
					break;
				case 0: 
					relationship = v.getContext().getString(R.string.Single);
					break;
				case 1:
					relationship = v.getContext().getString(R.string.In_a_relationship);
					break;
				case 2: 
					relationship = v.getContext().getString(R.string.Complicated);
					break;
				case 3: 
					relationship = v.getContext().getString(R.string.Available);
					break;
				}
				tvRelationship.setText(relationship);
		
				tvEducation.setText(Profile.schoolName);
    		}
    	});
    }
    
    class UserProfileClubs2 extends AsyncTask<Void, Void, String[]> {
	    // This method is called when the thread runs

		protected String[] doInBackground(Void... cuts) {
	    	//get the all the current games			
			RestClient result_UserProfileClubs2 = null;
			try {
				result_UserProfileClubs2 = new request().execute(Rest.STORE + "?fav_only=1", Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String[] clubs = {null, null, null};
			
			try {
				JSONArray threads = new JSONArray(result_UserProfileClubs2.getResponse());
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
        		ivClub1.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(clubs[0], v.getContext(), 47));
        	}
        	if (clubs[1] != null){
        		ivClub2.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(clubs[1], v.getContext(), 47));
        	}
        	if (clubs[2] != null){
        		ivClub3.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(clubs[2], v.getContext(), 47));
        	}
	    }
	}
    
    class UserProfileClubs extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	RestClient result = null;
			try {
				result = new request().execute(Rest.STORE + "?fav_only=1", Rest.OSESS + Profile.sk, Rest.GET).get();
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
			        		ivClub1.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(clubs[0], v.getContext(), 47));
			        	}
			        	if (clubs[1] != null){
			        		ivClub2.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(clubs[1], v.getContext(), 47));
			        	}
			        	if (clubs[2] != null){
			        		ivClub3.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(clubs[2], v.getContext(), 47));
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
			RestClient result_UserProfileBadges2 = null;
			try {
				result_UserProfileBadges2 = new request().execute(Rest.BADGE + "?unlocked_only=1&user_id=" + Profile.userId, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String[] badges = {null, null, null};
			
			try {
				JSONArray threads = new JSONArray(result_UserProfileBadges2.getResponse());
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
				ivBadge1.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(badges[0], v.getContext(), 47));
        	}
        	if (badges[1] != null){
        		ivBadge2.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(badges[1], v.getContext(), 47));
        	}
        	if (badges[2] != null){
        		ivBadge3.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(badges[2], v.getContext(), 47));
        	}
	    }
	}
    
    class UserProfileBadges extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	RestClient result = null;
			try {
				result = new request().execute(Rest.BADGE + "?unlocked_only=1&user_id=" + Profile.userId, Rest.OSESS + Profile.sk, Rest.GET).get();
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
							ivBadge1.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(badges[0], v.getContext(), 47));
			        	}
			        	if (badges[1] != null){
			        		ivBadge2.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(badges[1], v.getContext(), 47));
			        	}
			        	if (badges[2] != null){
			        		ivBadge3.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(badges[2], v.getContext(), 47));
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
			RestClient result_UserProfileFriends2 = null;
			try {
				result_UserProfileFriends2 = new request().execute(Rest.USER + "1;9999" + "?friends_only=1", Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String[] friends = {null, null, null};
			
			try {
				final JSONArray threads = new JSONArray(result_UserProfileFriends2.getResponse());
				Log.i("friends: ", threads.toString());
				
				mHandler.post(new Runnable() {
	        		public void run() {
	        			tvPbox1.setText(threads.length() + " " + getContext().getString(R.string.friends));
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

			return friends;
	    }
		
		protected void onPostExecute(final String[] friends) {
			if (friends[0] != null){
				ivFriend1.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(friends[0], v.getContext(), 47));
        	}
        	if (friends[1] != null){
        		ivFriend2.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(friends[1], v.getContext(), 47));
        	}
        	if (friends[2] != null){
        		ivFriend3.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(friends[2], v.getContext(), 47));
        	}
	    }
	}
    
    class UserProfileFriends extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	RestClient result = null;
			try {
				result = new request().execute(Rest.USER + "1;9999" + "?friends_only=1", Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			final String[] friends = {null, null, null};
			
			try {
				final JSONArray threads = new JSONArray(result.getResponse());
				Log.i("friends: ", threads.toString());
				
				if (currentView){
					mHandler.post(new Runnable() {
		        		public void run() {
		        			tvPbox1.setText(threads.length() + " " + getContext().getString(R.string.friends));
		        		}
					});
				}
				
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
			
			if (currentView){
				mHandler.post(new Runnable() {
	        		public void run() {
						if (friends[0] != null){
							ivFriend1.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(friends[0], v.getContext(), 47));
			        	}
			        	if (friends[1] != null){
			        		ivFriend2.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(friends[1], v.getContext(), 47));
			        	}
			        	if (friends[2] != null){
			        		ivFriend3.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(friends[2], v.getContext(), 47));
			        	}
	        		}
				});
			}
	    }
    }
    
    class UserProfileInbox extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
			// TODO Auto-generated method stub
			//Get User Chat History
			RestClient result_UserProfileInbox = null;
			try {
				result_UserProfileInbox = new request().execute(Rest.INBOX, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray inboxMsgs = new JSONArray(result_UserProfileInbox.getResponse());
				
				for (int i = 0; i < inboxMsgs.length(); i++){
					int num_unread = inboxMsgs.getJSONObject(i).getInt("num_unread");

					if(num_unread > 0){
						Log.i("inbox unread", "-------------------");
						if (currentView){
							mHandler.post(new Runnable() {
				        		public void run() {
				        			//ivRedDot.setVisibility(View.VISIBLE);
				        		}
							});
						}
						
						break;
					}
				}				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			RestClient result2 = null;
			try {
				result2 = new request().execute(Rest.FRIEND_REQUEST + 1 + ";" + 25 + "?with_other_user_resource=1", Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray requests = new JSONArray(result2.getResponse());
				Log.i("requests: ", requests.toString());
				
				if (requests.length() > 0){
					Log.i("inbox unread", "-------------------");
					if (currentView){
						mHandler.post(new Runnable() {
			        		public void run() {
			        			//ivRedDot.setVisibility(View.VISIBLE);
			        		}
						});
					}
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
					            	AlertDialog alert = new AlertDialog.Builder(v.getContext()).create();
		        		    		alert.setMessage(getContext().getString(R.string.Course_not_available_alert));
		        		    		alert.setButton(getContext().getString(R.string.No), new DialogInterface.OnClickListener() {
		        		    			
		        		    			public void onClick(DialogInterface dialog, int which) {
		        		    				// TODO Auto-generated method stub
		        		    			}
		        		    		});
		        		    		alert.setButton2(getContext().getString(R.string.Yes), new DialogInterface.OnClickListener() {
		        		    			
		        		    			public void onClick(DialogInterface dialog, int which) {
		        		    				String email = "info@gotoohlala.com";
		        		                	Intent emailIntent = new Intent(Intent.ACTION_SEND);
		        		                    emailIntent.setType("plain/text");
		        		                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
		        		                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Course Request: " + Profile.schoolName);
		        		                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
		        		                    getContext().startActivity(Intent.createChooser(emailIntent, getContext().getString(R.string.Send_email_via)));
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
    
    class checkCurrentCourse extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	RestClient result = null;
			try {
				result = new request().execute(Rest.SCHOOL_COURSE, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("checkCurrentCourse", result.getResponse());
			
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
				        			tvCurrentClass.setText(getContext().getString(R.string.IN_CLASS));
				        			tvCurrentClass.setTypeface(Fonts.getOpenSansBold(v.getContext()));
				        		}
							});
						} else {
							mHandler.post(new Runnable() {
				        		public void run() {
				        			rlPbox2.setBackgroundResource(R.drawable.bg_profile_box);
				        			tvCurrentClass.setVisibility(View.VISIBLE);
				        			tvCurrentClass.setText(getContext().getString(R.string.FREE));
				        			tvCurrentClass.setTypeface(Fonts.getOpenSansBold(v.getContext()));
				        		}
							});
						}
					}
				}
				
				if (threads.length() == 0){
					mHandler.post(new Runnable() {
		        		public void run() {
		        			rlPbox2.setBackgroundResource(R.drawable.bg_profile_box);
		        			tvCurrentClass.setVisibility(View.VISIBLE);
		        			tvCurrentClass.setText(getContext().getString(R.string.FREE));
		        			tvCurrentClass.setTypeface(Fonts.getOpenSansBold(v.getContext()));
		        		}
					});
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
    }

	public void refreshAfterUpdateStatus(String status) {
		// TODO Auto-generated method stub
		etStatus.setText(status);
	}
	
	public void onPause() {
		// TODO Auto-generated method stub
		
		currentView = false;
	}
	
	public void onResume() {
		// TODO Auto-generated method stub
		
		currentView = true;
	}
	
}
