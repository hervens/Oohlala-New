package launchOohlala;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.Friends.ExploreArrayAdapter;
import profile.SMSInvite.SMSInviteArrayAdapter;
import profile.BlockListModel;
import profile.FriendsModel;
import profile.SMSInvite;
import user.Profile;

import campuswall.CampusWallInviteFacebook;
import campuswall.InviteFacebookArrayAdapter;
import campuswall.InviteFacebookModel;

import com.facebook.FacebookException;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.gotoohlala.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import datastorage.ConvertDpsToPixels;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import datastorage.Rest.request;
import events.OthersSchedule;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FindYourFriends extends Activity {
	ListView listView;
	PullToRefreshListView mPullRefreshListView;
	List<FriendsModel> list = new ArrayList<FriendsModel>();
	ExploreArrayAdapter adapter;
	
	loadBitmapTread loadBitmapTread;
	
	Handler mHandler = new Handler();
	
	private static final List<String> PERMISSIONS = Arrays.asList("education", "friends_education_history");
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
	String numbers = "";
	
	TextView bDone, tvSMSInvite;
	
	ListView listView2;
	List<BlockListModel> list2 = new ArrayList<BlockListModel>();
	SMSInviteArrayAdapter adapter2;
	View footerView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.findyourfriends);
		footerView = View.inflate(FindYourFriends.this, R.layout.smsinvitefooterview, null);
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		}); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(FindYourFriends.this));
		
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.lvFriends);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
				reloadView();
			}
		});
		mPullRefreshListView.setRefreshing();
		
		listView = mPullRefreshListView.getRefreshableView();
		listView.addFooterView(footerView);
		adapter = new ExploreArrayAdapter(FindYourFriends.this, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				/*
				position--;
				
				Bundle extras = new Bundle();
				extras.putInt("user_id", list.get(position).user_id);
				Intent i = new Intent(FindYourFriends.this, OthersProfile.class);
				i.putExtras(extras);
				startActivity(i);
				*/
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
	    	TaskQueueImage.addTask(new updateListThread(), FindYourFriends.this);
	    }
        
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.gotoohlala.profile.ProfileSettings");
        registerReceiver(new BroadcastReceiver() {
        	@Override
         	public void onReceive(Context context, Intent intent) {
        		Log.d("onReceive","Logout in progress");
            	//At this point you should start the login activity and finish this one
        		finish();
        	}
        }, intentFilter);
        
        bDone = (TextView) findViewById(R.id.bDone);
		bDone.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent sms = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + numbers)); 
				sms.putExtra("sms_body", getString(R.string.sms_message)); 
				startActivity(Intent.createChooser(sms, getString(R.string.Choose_a_SMS_client)));
			}
	    }); 
	
		listView2 = (ListView) footerView.findViewById(R.id.lvSMSInvite);
		adapter2 = new SMSInviteArrayAdapter(FindYourFriends.this, list2);
		listView2.setAdapter(adapter2);
		
		tvSMSInvite = (TextView) footerView.findViewById(R.id.tvSMSInvite);
		tvSMSInvite.setText(getString(R.string.Invite_Friends).toUpperCase());
		
		TaskQueueImage.addTask(new getContacts(), FindYourFriends.this);
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
        	TaskQueueImage.addTask(new updateListThread(), FindYourFriends.this);
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
	
	public void reloadView() {
        // Do work to refresh the list here.
		mHandler.post(new Runnable() {
    		public void run() {
		    	list.clear();
		    	
		    	if (list.isEmpty()){
		    		TaskQueueImage.addTask(new updateListThread(), FindYourFriends.this);
				}
		    }
		});
    }
	
	class updateListThread extends Thread {
	    // This method is called when the thread runs
		List<FriendsModel> listTemp = new ArrayList<FriendsModel>();
		List<String> fb_uids = new ArrayList<String>();
		
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
					        	 fb_uids.add(friends.getJSONObject(i).getString("id")); 
					         } 	
			        	 }
		        	} catch(Exception e) {
		                e.printStackTrace();
		            }  
		        }
		        
		    	RestClient result = null;
				try {
					result = new Rest.requestBody().execute(Rest.USER, Rest.OSESS + Profile.sk, Rest.PUT4, "1", "fb_uids", fb_uids.toString()).get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.i("suggested friends: ", result.getResponse());
			
				try {
					final JSONArray threads = new JSONArray(result.getResponse());
					Log.i("friends: ", threads.toString());
					
					for (int i = 0; i < threads.length(); i++){
						int user_id = threads.getJSONObject(i).getInt("id");
						String name = threads.getJSONObject(i).getString("firstname");
						String avatar_thumb_url = threads.getJSONObject(i).getString("avatar_thumb_url");
						String looking_for = threads.getJSONObject(i).getString("looking_for");
						//boolean has_schedule = threads.getJSONObject(i).getBoolean("has_schedule");
						
						if (!threads.getJSONObject(i).getBoolean("is_friend")){
							listTemp.add(new FriendsModel(user_id, name, avatar_thumb_url, looking_for, true, null, null));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				mHandler.post(new Runnable() {
	        		public void run() {
	        			//listView.setVisibility(View.GONE);
	        			list.clear();
	        	    	list.addAll(listTemp);
	        	    	listView.invalidateViews();
	        	    	adapter.notifyDataSetChanged();
	        	    	//listView.setVisibility(View.VISIBLE);
	        	    	
	        	    	preLoadImages();
	        	    	
	        	    	mPullRefreshListView.onRefreshComplete();
	        	    	
	        	    	listTemp.clear();
	        			listTemp = null;
	        		}
				});
				
	    }
    }
	
	public void preLoadImages() {
		// TODO Auto-generated method stub
		loadBitmapTread = new loadBitmapTread();
		loadBitmapTread.execute(0, list.size());
	}
	
	class loadBitmapTread extends AsyncTask<Integer, Void, Void> {
	    // This method is called when the thread runs
		public int start, stop;

		protected Void doInBackground(Integer... num) {
	    	//get the all the current games
			this.start = num[0];
			this.stop = num[1];
	    	Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	for (int i = start; i < stop; i++){
	    		if (i < list.size()){
					if (list.get(i).avatar_thumb_url != null && list.get(i).image_bitmap == null){
						list.get(i).image_bitmap = ImageLoader.KembrelImageStoreAndLoad(list.get(i).avatar_thumb_url, FindYourFriends.this);
		    			mHandler.post(new Runnable() {
		    	        	public void run() {
		    				    listView.invalidateViews();
		    				}
		    			});
					}
	    		} else {
	    			break;
	    		}
			}
	    	
	    	return null;
	    }
	}
	
	public class ExploreArrayAdapter extends ArrayAdapter<FriendsModel> {
		private final List<FriendsModel> list;
		
		class ViewHolder {
			public ImageView ivThumb, ivCheck1, ivCheck2;
			public TextView name;
			public Button bRequest;
			public RelativeLayout bg;
		}

		public ExploreArrayAdapter(Context context, List<FriendsModel> list) {
			super(context, R.layout.suggestedfriendsrow, list);
			this.list = list;
		}
		
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null){
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.suggestedfriendsrow, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.ivThumb = (ImageView) rowView.findViewById(R.id.ivThumb);
				viewHolder.ivCheck1 = (ImageView) rowView.findViewById(R.id.ivCheck1);
				viewHolder.ivCheck2 = (ImageView) rowView.findViewById(R.id.ivCheck2);
				viewHolder.name = (TextView) rowView.findViewById(R.id.name);
				viewHolder.bRequest = (Button) rowView.findViewById(R.id.bRequest);
				viewHolder.bg = (RelativeLayout) rowView.findViewById(R.id.bg);
				
				rowView.setTag(viewHolder);
			}
			
			final ViewHolder holder = (ViewHolder) rowView.getTag();
			
			holder.ivThumb.setImageBitmap(null);
			holder.name.setText("");
			holder.bRequest.setText("");
			holder.ivCheck1.setVisibility(View.VISIBLE);
			holder.ivCheck2.setVisibility(View.GONE);
			
			if (list.get(position).image_bitmap != null){
				//image save into the memory and cache
				holder.ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(list.get(position).image_bitmap, 8));
			}
			
			holder.name.setTextColor(getResources().getColorStateList(R.color.blue0));
			holder.name.setTypeface(Fonts.getOpenSansBold(FindYourFriends.this));
			holder.name.setText(StringLanguageSelector.retrieveString(list.get(position).name));
			
			if (list.get(position).looking_for.contentEquals("classrequestsent")){
				holder.bRequest.setText(getString(R.string.Request_Sent));
				holder.bRequest.setClickable(false);
				
				holder.ivCheck1.setVisibility(View.GONE);
				holder.ivCheck2.setVisibility(View.VISIBLE);
			} else {
				holder.bRequest.setText("Add " + list.get(position).name);
				holder.bRequest.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						TaskQueueImage.addTask(new courseScheduleRequest(position, holder.bRequest, holder.ivCheck1, holder.ivCheck2), FindYourFriends.this);
					}
				}); 
			}
			
			holder.bg.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					/*
					Bundle extras = new Bundle();
					extras.putInt("user_id", list.get(position).user_id);
					Intent i = new Intent(FindYourFriends.this, OthersProfile.class);
					i.putExtras(extras);
					startActivity(i);
					*/
				}
			}); 
			
			return rowView;
		}
		
	}
	
	class courseScheduleRequest extends Thread {
	    // This method is called when the thread runs
		int position;
		Button bRequest;
		ImageView ivCheck1, ivCheck2;
		
		public courseScheduleRequest(int position, Button bRequest, ImageView ivCheck1, ImageView ivCheck2){
			this.position = position;
			this.bRequest = bRequest;
			this.ivCheck1 = ivCheck1;
			this.ivCheck2 = ivCheck2;
		}
		
	    public void run() {
	    	RestClient result = null;
			try {
				result = new Rest.requestBody().execute(Rest.USER_REQUEST, Rest.OSESS + Profile.sk, Rest.POST, "2", "recipient_user_id", String.valueOf(list.get(position).user_id), "request_type", "1").get();
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
	        			bRequest.setText(getString(R.string.Request_Sent));
	        			bRequest.setClickable(false);
	        			
	        			ivCheck1.setVisibility(View.GONE);
	    				ivCheck2.setVisibility(View.VISIBLE);
	        			
	        			list.get(position).looking_for = "classrequestsent";
	        		}
				});
			}
	    }
	}
	
	class ImportFriendsThread extends Thread {
	    // This method is called when the thread runs
		String[] fb_uids = new String[50];
		String my_uid;
		
		public ImportFriendsThread(String my_uid){
			this.my_uid = my_uid;
		}
		
	    public void run() {
			final Session session = Session.getActiveSession();
    	 	if (session.isOpened()) {
    	 		//AddPermissions(session);    
	        	try {
					 HttpClient httpClient = new DefaultHttpClient();
		             HttpContext localContext = new BasicHttpContext();
		             HttpGet httpGet = new HttpGet(
		                     "https://graph.facebook.com/me/friends?fields=education,name&access_token="
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
	
		        	 //Log.i("response", response);
		        	 if (response == null || response.equals("") || 
		        			response.equals("false")) {
		        		 	Log.v("Error", "Blank response");
		        	 } else {
		        		 JSONObject json = new JSONObject(response);
				         final JSONArray friends = json.getJSONArray("data");
				         Log.i("university friends: ", friends.toString());
				    				
				         int k = 0;
				         for (int i = 0; i < friends.length(); i++){
				        	 if (k < 50){
				        		 if (friends.getJSONObject(i).has("education")){
					        		 JSONArray schools = friends.getJSONObject(i).getJSONArray("education");
					        		 for (int j = 0; j < schools.length(); j++){
					        			 if (schools.getJSONObject(j).getString("type").contentEquals("College")){
					        				 if (schools.getJSONObject(j).getJSONObject("school").getString("id").contentEquals(my_uid)){
					        					 //Log.i("school id", schools.getJSONObject(j).getJSONObject("school").getString("id"));
					        					 //Log.i("name", friends.getJSONObject(i).getString("name"));
					        					 fb_uids[k] = friends.getJSONObject(i).getString("id"); 
					        					 //Log.i("fb_uids", fb_uids[k] + "    " + k);
					        					 k++;
					        					 
					        					 break;
					        				 }
					        			 }
					        		 }
				        		 }
				        	 } else {
				        		 break;
				        	 }
				         } 	
		        	 }
	        	} catch(Exception e) {
	                e.printStackTrace();
	            }  
	        }
    	 	
    	 	mHandler.post(new Runnable() {
        		public void run() {
		    	 	 Bundle params = new Bundle();
				   	 params.putString("message", getString(R.string.hey_give_OOHLALA_a_try));
				   	 String fb_uids_to = fb_uids[0];
				   	 for (int i = 1; i < fb_uids.length; i++){
				   		 if(fb_uids[i] != null){
				   			 fb_uids_to += "," + fb_uids[i];
				   		 }
				   	 }
				   	 //Log.i("fb_uids_to", fb_uids_to);
				   	 params.putString("to", fb_uids_to);
				   	 WebDialog requestsDialog = (
			   	        new WebDialog.RequestsDialogBuilder(FindYourFriends.this,
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
        		}
    	 	});
	    }
	}
	
	//------------------------------------- SMS Invite --------------------------
	class getContacts extends Thread {
	    // This method is called when the thread runs
		List<BlockListModel> listTemp = new ArrayList<BlockListModel>();

	    public void run() {
			//Get User Chat History
	    	Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
	        while (phones.moveToNext()){
	        	String Name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
	        	String Number=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	        	
	        	listTemp.add(new BlockListModel(null, Name, Number, 0));
	        }
			
			mHandler.post(new Runnable() {
        		public void run() {
        			if (listTemp != null){
	        			//listView.setVisibility(View.GONE);
	        			Collections.sort(listTemp, new Comparator<BlockListModel>(){
	        				@Override
	        				public int compare(BlockListModel a, BlockListModel b){
	        					return a.first_name.compareToIgnoreCase(b.first_name);
	        				}
	        			});
	        			
	        			list2.clear();
	        			list2.addAll(listTemp);
	        			
	        			listView.invalidateViews();
	        	    	adapter.notifyDataSetChanged();
	        	    	//listView.setVisibility(View.VISIBLE);
	        	    	
	        	    	listTemp.clear();
	        	    	listTemp = null;
	        	    	
	        	    	RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				        		RelativeLayout.LayoutParams.FILL_PARENT,
				        		ConvertDpsToPixels.getPixels(51*list2.size() + 25*26, FindYourFriends.this));
	        	    	param.addRule(RelativeLayout.BELOW, tvSMSInvite.getId());
	        	    	
				        listView2.setLayoutParams(param); 
        			}
        		}
			});
		}
	}
	
	public class SMSInviteArrayAdapter extends ArrayAdapter<BlockListModel> {
		private final Context context;
		private final List<BlockListModel> list;
		
		class ViewHolder {
			public ImageView ivCheck, ivInvite;
			public TextView tvName, tvInvite, tvLetter;
			public RelativeLayout bg;
		}

		public SMSInviteArrayAdapter(Context context, List<BlockListModel> list) {
			super(context, R.layout.smsinviterow, list);
			this.context = context;
			this.list = list;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null){
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.smsinviterow, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.ivCheck = (ImageView) rowView.findViewById(R.id.ivCheck);
				viewHolder.ivInvite = (ImageView) rowView.findViewById(R.id.ivInvite);
				viewHolder.tvName = (TextView) rowView.findViewById(R.id.tvName);
				viewHolder.tvInvite = (TextView) rowView.findViewById(R.id.tvInvite);
				viewHolder.tvLetter = (TextView) rowView.findViewById(R.id.tvLetter);
				viewHolder.bg = (RelativeLayout) rowView.findViewById(R.id.bg);
				
				rowView.setTag(viewHolder);
			}
			
			final ViewHolder holder = (ViewHolder) rowView.getTag();
			
			holder.tvName.setText(null);
			holder.ivCheck.setVisibility(View.GONE);
			holder.ivInvite.setVisibility(View.GONE);
			holder.tvName.setTypeface(Fonts.getOpenSansBold(context));
			holder.tvName.setText(list.get(position).first_name);
			holder.tvInvite.setVisibility(View.VISIBLE);
			holder.tvInvite.setTypeface(Fonts.getOpenSansBold(context));
			holder.tvLetter.setVisibility(View.GONE);
			
			if (position == 0){
				holder.tvLetter.setVisibility(View.VISIBLE);
				holder.tvLetter.setText(list.get(position).first_name.substring(0, 1).toUpperCase());
			} else if (!list.get(position - 1).first_name.substring(0, 1).toUpperCase().contentEquals(list.get(position).first_name.substring(0, 1).toUpperCase())){
				holder.tvLetter.setVisibility(View.VISIBLE);
				holder.tvLetter.setText(list.get(position).first_name.substring(0, 1).toUpperCase());
			}
			
			if (list.get(position).user_id == 1){
				holder.ivCheck.setVisibility(View.VISIBLE);
			}
			
			holder.bg.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					holder.ivInvite.setVisibility(View.GONE);
					holder.tvInvite.setVisibility(View.GONE);
					holder.ivCheck.setVisibility(View.VISIBLE);
					bDone.setVisibility(View.VISIBLE);
					list.get(position).user_id = 1;
					
					if (numbers.trim().length() == 0){
						numbers = list.get(position).last_name;
					} else {
						numbers += ";" + list.get(position).last_name;
					}
				}
			});
			
			return rowView;
		}

	}
	
}
