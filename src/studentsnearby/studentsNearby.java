package studentsnearby;

import inbox.Inbox;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;


import network.ErrorCodeParser;
import network.GPS;
import network.GPSLocationChanged;
import network.RetrieveData;

import org.jivesoftware.smack.packet.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.Badge;
import profile.UserProfile;

import smackXMPP.NamePacketExtension;
import smackXMPP.UserIDPacketExtension;
import smackXMPP.XMPPChatModel;
import smackXMPP.XMPPClient;
import user.Profile;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import campusmap.CampusMap;
import campuswall.CampusWall;
import campuswall.CampusWallModel;

import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.TopMenuNavbar;
import com.gotoohlala.UnreadNumCheck;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import datastorage.Base64;
import datastorage.CacheInternalStorage;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.TimeCounter;
import datastorage.UserLocation;
import datastorage.UserLoginInfo;
import datastorage.Rest.request;

public class studentsNearby extends FrameLayout implements GPSLocationChanged, PictureUpdate {
	Button bGoOnline, bCheckin;
	ImageView ivThumb;
	TextView tvProfileName, tvCheckinStatus, tvNoStudents;
	EditText etStatus;
	
	double myLat, myLongi;
	
	List<studentsNearbyModel> list = new ArrayList<studentsNearbyModel>();
	List<nearbyLocationModel> locationlist = new ArrayList<nearbyLocationModel>();
	
	ListView listView, listView2;
	PullToRefreshListView mPullRefreshListView;
	studentsNearbyArrayAdapter adapter;
	LocationArrayAdapter Locationadapter;
	
	int cut = 0;
	int number = 0;
	boolean lastcut = false;
	boolean checkinClicked = false;
    
    boolean is_on_nearby = false;
    
    LinearLayout llLoading;
	
	boolean refresh = false;
	
	loadThumbBitmapThread loadThumbBitmapThread;
	loadThumbBitmapThreadAgain loadThumbBitmapThreadAgain;
	
	boolean firstTimeUploadPicture = false;
	
	private Handler mHandler = new Handler();
	
	boolean currentView = false;
	
	View v = null;
	
	public TopMenuNavbar TopMenuNavbar;
	
	public studentsNearby(Context context) {
		super(context);
		
		final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
		v = (RelativeLayout) mLayoutInflater.inflate(R.layout.studentsnearby, null);  
	    addView(v);  
		
		currentView = true;
		
		GPS.act = studentsNearby.this;
		GPS.getMyLocation(getContext());
		
		TopMenuNavbar = (TopMenuNavbar) v.findViewById(R.id.bTopMenuNavBar);
		
		TextView header = (TextView) v.findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(getContext()));
		
		bGoOnline = (Button) v.findViewById(R.id.bGoOnline);
		//bCheckin = (Button) v.findViewById(R.id.bCheckin);
		ivThumb = (ImageView) v.findViewById(R.id.ivThumb);
		tvProfileName = (TextView) v.findViewById(R.id.tvProfileName);
		//tvCheckinStatus = (TextView) v.findViewById(R.id.tvCheckinStatus);
		etStatus = (EditText) v.findViewById(R.id.etStatus);
		//listView2 = (ListView) v.findViewById(R.id.lvLocationList);
		llLoading = (LinearLayout) v.findViewById(R.id.llLoading);
		tvNoStudents = (TextView) v.findViewById(R.id.tvNoStudents);
		
		mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.lvStudentsNearby);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(getContext().getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());

				// Do work to refresh the list here.
				//new GetDataTask().execute();
				if (is_on_nearby){
					reloadView();
				} else {
					mPullRefreshListView.onRefreshComplete();
				}
			}
		});
		mPullRefreshListView.setRefreshing();
		
		listView = mPullRefreshListView.getRefreshableView();
		
		bGoOnline.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (is_on_nearby){
					RestClient result = null;
					try {
						result = new Rest.requestBody().execute(Rest.USER, Rest.OSESS + Profile.sk, Rest.PUT, "3", "latitude", String.valueOf(myLat), "longitude", String.valueOf(myLongi), "nearby_online", "0").get();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Log.i("get offline", result.getResponse());
							
					if (result.getResponseCode() == 200) {
						bGoOnline.setBackgroundDrawable(getResources().getDrawable(R.drawable.ui_offline));
						
						list.clear();
						listView.invalidateViews();
						number = 0;
						cut = 0;
						lastcut = false;
						is_on_nearby = false;
						
						tvNoStudents.setVisibility(View.VISIBLE);
					}
				} else {
					if (Profile.has_avatar){
						RestClient result = null;
						try {
							result = new Rest.requestBody().execute(Rest.USER, Rest.OSESS + Profile.sk, Rest.PUT, "3", "latitude", String.valueOf(myLat), "longitude", String.valueOf(myLongi), "nearby_online", "1").get();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Log.i("get Online", result.getResponse());
								
						if (result.getResponseCode() == 200) {
							bGoOnline.setBackgroundDrawable(getResources().getDrawable(R.drawable.ui_online));
							
							mPullRefreshListView.setRefreshing();
							showStudentsNearbyList();
							TaskQueueImage.addTask(new updateListThread(), getContext());
							
							is_on_nearby = true;
							
							tvNoStudents.setVisibility(View.GONE);
						}
					} else {
						firstTimeUploadPicture = true;
						
						Picture.p = studentsNearby.this;
						Intent i = new Intent(getContext(), Picture.class);
						getContext().startActivity(i);
					}
				}
			}
		});
		
		//set up profile picture
		if (Profile.avatar_thumb_url != null && Profile.avatar_thumb_url.contains(".png")){
			TaskQueueImage.addTask(new loadThumbImage(), getContext());
		}
		
		ivThumb.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Picture.p = studentsNearby.this;
				Intent i = new Intent(getContext(), Picture.class);
				getContext().startActivity(i);
			}
		});
		
		tvProfileName.setTypeface(Fonts.getOpenSansBold(getContext()));
		tvProfileName.setText(Profile.firstName + " " + Profile.lastName);
		//tvCheckinStatus.setText(Profile.locationName);
		etStatus.setText(Profile.looking_for);
		
		/*
		bCheckin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	if (!checkinClicked){
            		listView.setVisibility(View.GONE);
            		listView2.setVisibility(View.VISIBLE);
            		
            		checkinClicked = true;
            		bCheckin.setBackgroundDrawable(studentsNearby.this.getResources().getDrawable(R.drawable.buttonlikepressed));
            		
	            	Locationadapter = new LocationArrayAdapter(studentsNearby.this, studentsNearby.this, locationlist);
	            	listView2.setAdapter(Locationadapter);
	            	listView2.setOnItemClickListener(new OnItemClickListener() {
	        			public void onItemClick(AdapterView<?> parent, View view,
	        				int position, long id) {
		        				Hashtable<String, String> params = new Hashtable<String, String>();
		    	    			params.put("location_id", String.valueOf(locationlist.get(position).location_id));
		    	    			String result = RetrieveData.requestMethod(RetrieveData.UPDATE_USER_LOCATION, params);
		    	    			Log.i("update user location: ", result);
		    	    			
		    	    			listView.setVisibility(View.VISIBLE);
		                		listView2.setVisibility(View.GONE);
		    	    			showStudentsNearbyList();
		    	    			checkinClicked = false;
		    	    			bCheckin.setBackgroundDrawable(studentsNearby.this.getResources().getDrawable(R.drawable.buttonlike));
		    	    			tvCheckinStatus.setText(locationlist.get(position).name);
		    	    			//Profile.locationName = locationlist.get(position).name;
		    	    			runOnUiThread(new Runnable() {
		    					    public void run() {
		    					    	Profile.updateUserProfile();
		    					    }
		    	    			});
	        			}
	        		});
	        		
	            	if (getGPS){
		            	Hashtable<String, String> params = new Hashtable<String, String>();
		    			params.put("latitude", String.valueOf(myLat));
		    			params.put("longitude", String.valueOf(myLongi));
		    			params.put("location_type", String.valueOf(1));
		    			String result = RetrieveData.requestMethod(RetrieveData.GETNEARBY_LOCATION, params);
		    			Log.i("nearby_campus_locations: ", result);
		    			try {
		    				JSONArray users = (new JSONObject(result)).getJSONArray("nearby_campus_locations");
		    				
		    				for (int i = 0; i < users.length(); i++){
		    					int location_id = users.getJSONObject(i).getInt("location_id");
		    					String name = users.getJSONObject(i).getString("name");
		    					double longitude = users.getJSONObject(i).getDouble("longitude");
		    					double latitude = users.getJSONObject(i).getDouble("latitude");
		    					locationlist.add(new nearbyLocationModel(location_id, name, latitude, longitude));
		    				}
		    			} catch (JSONException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			}
	            	} else {
						Toast.makeText(getApplicationContext(), "Gps disabled, please turn it on to see nearby students", Toast.LENGTH_SHORT).show();
					}
            	} else {
            		listView.setVisibility(View.VISIBLE);
            		listView2.setVisibility(View.GONE);
            		
            		checkinClicked = false;
            		bCheckin.setBackgroundDrawable(studentsNearby.this.getResources().getDrawable(R.drawable.buttonlike));
            		
            		if (getGPS){
            			showStudentsNearbyList();
	            	} else {
						Toast.makeText(getApplicationContext(), "Gps disabled, please turn it on to see nearby students", Toast.LENGTH_SHORT).show();
					}
            	}
            }
        });
		*/
		
		etStatus.setOnKeyListener(new EditText.OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
				
				if (keyCode == KeyEvent.KEYCODE_ENTER){
					String text = etStatus.getText().toString();
		                
					RestClient result = null;
					try {
						result = new Rest.requestBody().execute(Rest.USER, Rest.OSESS + Profile.sk, Rest.PUT, "1", "looking_for", text).get();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Log.i("update status: ", result.getResponse());
							
					if (result.getResponseCode() == 200) {
						mHandler.post(new Runnable() {
	    					public void run() {
	    						Profile.updateUserProfile();
	    					}
	    	    		});
			    					    		
			    		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			    		imm.hideSoftInputFromWindow(etStatus.getWindowToken(), 0);
			    		etStatus.clearFocus();
			    		
			        	return true;
					} else {
						return false;
					}
				}
				
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					//onBackPressed();
				}
				return false;				
			}
		});
		
		if (CacheInternalStorage.getCacheUserLocation(getContext()) != null){
      		if (CacheInternalStorage.getCacheUserLocation(getContext()).lat != 0){
      			myLat = CacheInternalStorage.getCacheUserLocation(getContext()).lat;
				myLongi = CacheInternalStorage.getCacheUserLocation(getContext()).longi;
      		}
		}
		
		TaskQueueImage.addTask(new getSelfProfile(), getContext());
	}
	
	class getSelfProfile extends Thread {
		// This method is called when the thread runs
		
		public void run() {
			RestClient result = null;
			try {
				result = new Rest.request().execute(Rest.USER, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("self profile: ", result.getResponse());
					
			if (result.getResponseCode() == 200) {
				try {
					JSONObject profile = new JSONObject(result.getResponse());
					if (Profile.has_avatar){
						is_on_nearby = profile.getBoolean("nearby_online");
					} else {
						firstTimeUploadPicture = true;
						
						Picture.p = studentsNearby.this;
						Intent i = new Intent(getContext(), Picture.class);
						getContext().startActivity(i);
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				mHandler.post(new Runnable() {
            		public void run() {
						if (is_on_nearby){
							bGoOnline.setBackgroundDrawable(getResources().getDrawable(R.drawable.ui_online));
							tvNoStudents.setVisibility(View.GONE);
							
							//only when you are logged in, you can see the nearby students list
							showStudentsNearbyList();
					      	TaskQueueImage.addTask(new updateListThread(), getContext());
					      	
							Toast.makeText(getContext(), getContext().getString(R.string.Enabling_GPS), Toast.LENGTH_SHORT).show();
						} else {
							bGoOnline.setBackgroundDrawable(getResources().getDrawable(R.drawable.ui_offline));
							tvNoStudents.setVisibility(View.VISIBLE);
							
							mPullRefreshListView.onRefreshComplete();
						}
            		}
				});
			}
		}
	}
	
	class loadThumbImage extends Thread {
	    // This method is called when the thread runs
	    public void run() {
			mHandler.post(new Runnable() {
        		public void run() {
        			ivThumb.setImageBitmap(ImageLoader.thumbImageStoreAndLoad(Profile.avatar_thumb_url, getContext()));
				}
			});
		}
    }
	
	public void reloadView() {
        // Do work to refresh the list here.
		mHandler.post(new Runnable() {
		    public void run() {
		    	refresh = true;
		    	cut = 0;
		    	number = 0;
		    	lastcut = false;
		    	list.clear();
		    }
		});
    	
		if (list.isEmpty()){
			TaskQueueImage.addTask(new updateListThread(), getContext());
		}
    }
	
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		
		@Override
        protected void onPreExecute() {
			refresh = true;
	    	cut = 0;
	    	number = 0;
	    	lastcut = false;
	    	list.clear();
        } 
		
		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			TaskQueueImage.addTask(new updateListThread(), getContext());
			//adapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}
	
	public void showStudentsNearbyList(){
		adapter = new studentsNearbyArrayAdapter(getContext(), list);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new OnScrollListener(){
			
			public int first, last;
			
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		        // TODO Auto-generated method stub
		    	this.first = firstVisibleItem;
		    	this.last = firstVisibleItem + visibleItemCount - 1;
		    }
		 	
		    public void onScrollStateChanged(AbsListView view, int scrollState) {
		    	// TODO Auto-generated method stub
			   	if(scrollState == 0){
			   		
			   		if (last >= number && !lastcut){
			   			//listView.scrollBy(0, 120);
			   			llLoading.setVisibility(View.VISIBLE);
			   			
			    		cut++;
			    		TaskQueueImage.addTask(new updateListThread(), getContext());
			    	} 
			   		
			   		if (last <= list.size()){
			   			/*
				   		//free the older bitmaps in the list
				   		Thread freeBitmapThread = new freeBitmapThread(first, last);
				   		freeBitmapThread.setPriority(Thread.MIN_PRIORITY);
				   		freeBitmapThread.start();	
				   		*/
			   			
				   		if (first > 0){
				   			for (int i = first - 1; i < last; i++){
					    		if (i < list.size() && currentView){
									if (list.get(i).avatar != null && list.get(i).avatar.contains(".png") && list.get(i).thumb_bitmap == null){
										new loadThumbBitmapThreadAgain(i).start();
									}
					    		} else {
					    			break;
					    		}
							}
				   		} else if (first == 0){
				   			for (int i = 0; i < last; i++){
					    		if (i < list.size() && currentView){
									if (list.get(i).avatar != null && list.get(i).avatar.contains(".png") && list.get(i).thumb_bitmap == null){
										new loadThumbBitmapThreadAgain(i).start();
									}
					    		} else {
					    			break;
					    		}
							}
				   		}
			   		}
			    }
			}
		});
	}	
	
	class updateListThread extends Thread {
	    // This method is called when the thread runs
		List<studentsNearbyModel> listTemp = new ArrayList<studentsNearbyModel>();

	    public void run() {
	    	int start = cut*25 + 1;
			int end = cut*25 + 25;
			RestClient result = null;
			try {
				result = new request().execute(Rest.USER + start + ";" + end + "?latitude=" + myLat + "&longitude=" + myLongi, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray users = new JSONArray(result.getResponse());
				Log.i("nearby users: ", users.toString());
				
				for (int i = 0; i < users.length(); i++){
					boolean has_avatar = users.getJSONObject(i).getBoolean("has_avatar");
					String avatar = users.getJSONObject(i).getString("avatar_url");
					String first_name = users.getJSONObject(i).getString("firstname");
					String looking_for = users.getJSONObject(i).getString("looking_for");
					String location_name = null;
					if (users.getJSONObject(i).has("location_name")){
						location_name = users.getJSONObject(i).getString("location_name");
					}
					String school_name = users.getJSONObject(i).getString("school_name");
					String jid = users.getJSONObject(i).getString("jid");
					String last_name = users.getJSONObject(i).getString("lastname");
					int user_id = users.getJSONObject(i).getInt("id");
					
					boolean has_liked = false;
					int profile_likes = users.getJSONObject(i).getInt("profile_likes");
					int profile_hots = users.getJSONObject(i).getInt("profile_hots");
					int profile_crushes = users.getJSONObject(i).getInt("profile_crushes");
					
					if (has_avatar){
						if (!checkDuplicateJid(jid)){
							listTemp.add(new studentsNearbyModel(avatar, first_name, looking_for, location_name, school_name, jid, last_name, user_id, null, has_avatar, has_liked, profile_likes, profile_hots, profile_crushes));
							number++;
						}
					}
				}
				
				/*
				if (users.length() == 25 || users.length() == 10){
					//list.add(new studentsNearbyModel(null, null, null, null, null, null, null, 0, null, false, false, 0, 0, 0));
					//number++;
				} else {
					lastcut = true;
				}
				*/
				
				if (users.length() == 0){
					lastcut = true;
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (currentView){
				mHandler.post(new Runnable() {
	        		public void run() {
	        			refresh = false; //set the refresh to false after the refresh
	        	    	
	        			llLoading.setVisibility(View.GONE);
	        	    	
	        			//listView.setVisibility(View.GONE);
	        	    	list.addAll(listTemp);
	        	    	listView.invalidateViews();
	        	    	adapter.notifyDataSetChanged();
	        	    	//listView.setVisibility(View.VISIBLE);
	        			
	        	    	preLoadThumbImages();
	        	    	
	        	    	mPullRefreshListView.onRefreshComplete();
	        	    	
	        	    	listTemp.clear();
	        	    	listTemp = null;
	        		}
				});
			}
	    }
	}
	
	public void preLoadThumbImages() {
		// TODO Auto-generated method stub
		if (list.size() > 25){
	    	for (int i = list.size()-26; i < list.size(); i++){
	    		if (i < list.size() && currentView){
					if (list.get(i).avatar != null && list.get(i).avatar.contains(".png") && list.get(i).thumb_bitmap == null){
						new loadThumbBitmapThread(i).start();			
					}
	    		} else {
	    			break;
	    		}
			}
		} else {
	    	for (int i = 0; i < list.size(); i++){
	    		if (i < list.size() && currentView){
					if (list.get(i).avatar != null && list.get(i).avatar.contains(".png") && list.get(i).thumb_bitmap == null){
						new loadThumbBitmapThread(i).start();
					}
	    		} else {
	    			break;
	    		}
			}
		}	
	}
	
	class loadThumbBitmapThread extends Thread {
	    // This method is called when the thread runs
		int i = 0;
		
		public loadThumbBitmapThread(int i){
			this.i = i;
		}

		public void run() {
	    	//get the all the current games
			list.get(i).thumb_bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(list.get(i).avatar, getContext(), 80);
			mHandler.post(new Runnable() {
        		public void run() {
			    	listView.invalidateViews();
			    }
			});
	    }
	}
	
	class loadThumbBitmapThreadAgain extends Thread {
	    // This method is called when the thread runs
		int i = 0;
		
		public loadThumbBitmapThreadAgain(int i){
			this.i = i;
		}

		public void run() {
	    	//get the all the current games
			list.get(i).thumb_bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(list.get(i).avatar, getContext(), 80);
			mHandler.post(new Runnable() {
        		public void run() {
			    	listView.invalidateViews();
			    }
			});
	    }
	}
	
	public class freeBitmapThread extends Thread {
	    // This method is called when the thread runs
		public int start, stop;
		
		public freeBitmapThread(int start, int stop){
			this.start = start;
			this.stop = stop;
		}
		
	    public void run() { 
	    	//Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	if (start > ImageLoader.loaderRange){
		    	for (int i = 0; i < start - ImageLoader.loaderRange; i++){
		    		if (i < list.size() && currentView){
						if (list.get(i).thumb_bitmap != null){
							list.get(i).thumb_bitmap.recycle();
							list.get(i).thumb_bitmap = null;
						}
		    		} else {
		    			break;
		    		}
				}
	    	} 
	    	if (stop + ImageLoader.loaderRange < list.size()){
		    	for (int i = stop + ImageLoader.loaderRange; i < list.size(); i++){
		    		if (i < list.size() && currentView){
						if (list.get(i).thumb_bitmap != null){
							list.get(i).thumb_bitmap.recycle();
							list.get(i).thumb_bitmap = null;
						}
		    		} else {
		    			break;
		    		}
				}
	    	} 
	    }
	}  
    
    public boolean checkDuplicateJid(String jid){
    	for (int i = 0; i < list.size(); i++){
			if (list.get(i).jid.contentEquals(jid)){
				return true;
			}
    	}
    	return false;
    }

	public void locationChanged() {
		// TODO Auto-generated method stub
		if (is_on_nearby){
			
			if (GPS.getGPS || GPS.getServiceProvider){
				myLat = GPS.myLat;
				myLongi = GPS.myLongi;
				
				list.clear();
				listView.invalidateViews();
				number = 0;
				cut = 0;
				lastcut = false;
				
				mPullRefreshListView.setRefreshing();
				showStudentsNearbyList();
				
				TaskQueueImage.addTask(new updateListThread(), getContext());
			}
			
			//GPS.stopGPS();
		}
	}

	public void pictureUpdate(Bitmap bitmap) {
		// TODO Auto-generated method stub
		ivThumb.setImageBitmap(bitmap);
		
		if (firstTimeUploadPicture){
			RestClient result = null;
			try {
				result = new Rest.requestBody().execute(Rest.USER, Rest.OSESS + Profile.sk, Rest.PUT, "3", "latitude", String.valueOf(myLat), "longitude", String.valueOf(myLongi), "nearby_online", "1").get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("get Online", result.getResponse());
					
			if (result.getResponseCode() == 200) {
				bGoOnline.setBackgroundDrawable(getResources().getDrawable(R.drawable.ui_online));
				
				mPullRefreshListView.setRefreshing();
				showStudentsNearbyList();
				TaskQueueImage.addTask(new updateListThread(), getContext());
				
				is_on_nearby = true;
				
				tvNoStudents.setVisibility(View.GONE);
				
				firstTimeUploadPicture = false;
			}
		}
	}

	public void onPause() {
		// TODO Auto-generated method stub
		
		currentView = false;
	}

	protected void onResume() {
		// TODO Auto-generated method stub
		
		currentView = true;
	}
    
}
