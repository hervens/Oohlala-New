package inbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gotoohlala.R;
import com.gotoohlala.TopMenuNavbar;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RoundedCornerImage;
import datastorage.Rest.request;
import datastorage.RestClient;
import datastorage.TimeCounter;

public class Inbox extends FrameLayout {
	ListView listView;
	PullToRefreshListView mPullRefreshListView;
	List<InboxModel> list = new ArrayList<InboxModel>();
	InboxArrayAdapter adapter;
	
	loadBitmapThread loadBitmapThread;
	loadBitmapThreadAgain loadBitmapThreadAgain;
	
	boolean refresh = false;
	
	Handler mHandler = new Handler(Looper.getMainLooper());
	
	int cut = 0;
	int number = 0;
	boolean lastcut = false;
	LinearLayout llLoading;
	
	boolean currentView = false;
	
	public TopMenuNavbar TopMenuNavbar;
	
	public Inbox(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
		View v = (RelativeLayout) mLayoutInflater.inflate(R.layout.inbox, null);  
	    addView(v);  
		
		currentView = true;
		 
		TopMenuNavbar = (TopMenuNavbar) v.findViewById(R.id.bTopMenuNavBar);
			
		TextView header = (TextView) v.findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(getContext()));
		
		llLoading = (LinearLayout) v.findViewById(R.id.llLoading);
		 
		mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.lvInbox);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(getContext().getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
					
				reloadView();
			}
		});
		mPullRefreshListView.setRefreshing();
			
		listView = mPullRefreshListView.getRefreshableView();
		
		adapter = new InboxArrayAdapter(getContext(), list);
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
				    		TaskQueueImage.addTask(new getInboxTask(), getContext());
				    	} 
				   		
				   		if (last <= list.size()){
				   			/*
					   		//free the older bitmaps in the list
					   		Thread freeBitmapThread = new freeBitmapThread(first, last);
					   		freeBitmapThread.setPriority(Thread.MIN_PRIORITY);
					   		freeBitmapThread.start();	
					    	*/
				   			
					   		loadBitmapThreadAgain = new loadBitmapThreadAgain();
					    	if (first > 0){
					    		loadBitmapThreadAgain.execute(first - 1, last);
					    	} else if (first == 0){
					    		loadBitmapThreadAgain.execute(0, last);
					    	}
				   		}
				    }
				}
			});
		 
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			 public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				position--;
				if (list.get(position).friend_request_id != 0){
					showFriendRequestDialog(list.get(position).friend_request_id);
				} else {
					Bundle extras = new Bundle();
					extras.putString("jid", list.get(position).jid);
					extras.putString("first_name", list.get(position).first_name);
					extras.putString("last_name", list.get(position).last_name);
					extras.putString("avatar", list.get(position).avatar);
					extras.putInt("user_id", list.get(position).user_id);
					
					Intent i = new Intent(getContext(), XMPPClient.class);
					i.putExtras(extras);
					getContext().startActivity(i);
				}
			 }
		 });
		 
		 TaskQueueImage.addTask(new getInboxTask(), getContext());
	 }
	 
	 public void reloadView() {
	    // Do work to refresh the list here.
		if (loadBitmapThread != null){
			loadBitmapThread.cancel(true);
		}

		mHandler.post(new Runnable() {
			public void run() {
				refresh = true;
		    	cut = 0;
		    	number = 0;
		    	lastcut = false;
		    	list.clear();
		    	
		    	if (list.isEmpty()){
		    		TaskQueueImage.addTask(new getInboxTask(), getContext());
				}
			}
		});
	 }
	 
	 class getInboxTask extends Thread {
		    // This method is called when the thread runs
			List<InboxModel> listTemp = new ArrayList<InboxModel>();
			List<InboxModel> listTemp_friend_requests = new ArrayList<InboxModel>();
			
		    public void run() {
		    	/*
		    	if (cut == 0){
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
						
						for (int i = 0; i < requests.length(); i++){
							int friend_request_id = requests.getJSONObject(i).getInt("id");
							int user_id = requests.getJSONObject(i).getInt("user_id");
							JSONObject other_user = requests.getJSONObject(i).getJSONObject("other_user");
							//Log.i("other_user: ", other_user.toString());
							String avatar = other_user.getString("avatar_thumb_url");
							String first_name = other_user.getString("firstname");
							String last_name = other_user.getString("lastname");
							int recipient_user_id = requests.getJSONObject(i).getInt("recipient_user_id");
							String message = requests.getJSONObject(i).getString("message");
							int time_sent = requests.getJSONObject(i).getInt("time_sent");
							
							listTemp_friend_requests.add(new InboxModel(String.valueOf(time_sent), 0, avatar, first_name, last_name, 0, null, 0, null, null, null, friend_request_id, user_id, message, TimeCounter.convertDate(time_sent)));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	}
		    	*/
		    	
		    	int start = cut*25 + 1;
				int end = cut*25 + 25;
				RestClient result = null;
				try {
					result = new request().execute(Rest.INBOX + start + ";" + end + "?with_other_user_resource=1", Rest.OSESS + Profile.sk, Rest.GET).get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try {
					JSONArray inboxMsgs = new JSONArray(result.getResponse());
					Log.i("inboxMsgs: ", inboxMsgs.toString());
					
					for (int i = 0; i < inboxMsgs.length(); i++){
						int last_message_time = inboxMsgs.getJSONObject(i).getInt("last_message_time");
						int num_unread = inboxMsgs.getJSONObject(i).getInt("num_unread");
						JSONObject other_user = inboxMsgs.getJSONObject(i).getJSONObject("other_user");
						//Log.i("other_user: ", other_user.toString());
						String avatar = other_user.getString("avatar_thumb_url");
						String first_name = other_user.getString("firstname");
						String last_name = other_user.getString("lastname");
						int user_id = inboxMsgs.getJSONObject(i).getInt("other_user_id");
						String jid = inboxMsgs.getJSONObject(i).getString("other_user_jid");
						int inbox_id = inboxMsgs.getJSONObject(i).getInt("id");
						String last_message = inboxMsgs.getJSONObject(i).getString("last_message");
						boolean is_blocked = inboxMsgs.getJSONObject(i).getBoolean("is_blocked");
						
						String time = TimeCounter.convertDate(last_message_time);
							
						if(last_message.trim().length() > 0){
							listTemp.add(new InboxModel(String.valueOf(last_message_time), num_unread, avatar, first_name, last_name, user_id, jid, inbox_id, last_message, time, null, 0, 0, null, null));
							number++;
						}
					}
					
					if (inboxMsgs.length() == 25){
						
					} else {
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
		    				
		    				list.addAll(listTemp_friend_requests);
		    				Collections.sort(listTemp, new TimeComparator());
		    				list.addAll(listTemp);
		    				
		    				listView.invalidateViews();
		    		    	adapter.notifyDataSetChanged();
		    		    	//listView.setVisibility(View.VISIBLE);
		    		    	
		    		    	preLoadImages();
		    		    	
		    		    	mPullRefreshListView.onRefreshComplete();
		    		    	
		    		    	listTemp_friend_requests.clear();
		    		    	listTemp_friend_requests = null;
		    		    	listTemp.clear();
		    		    	listTemp = null;
		        		}
					});
				}
			}
	 }
	 
	 public class TimeComparator implements Comparator<InboxModel> {
	        public int compare(InboxModel o1, InboxModel o2) {
	            return o2.last_message_time.compareTo(o1.last_message_time);
	        }
	 }
	 
	 public void preLoadImages() {
		// TODO Auto-generated method stub	
		loadBitmapThread = new loadBitmapThread();
		loadBitmapThread.execute(0, list.size());
	}
	 
	 class loadBitmapThread extends AsyncTask<Integer, Void, Void> {
		    // This method is called when the thread runs
			public int start, stop;

			protected Void doInBackground(Integer... num) {
		    	//get the all the current games
				this.start = num[0];
				this.stop = num[1];
		    	Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
		    	for (int i = start; i < stop; i++){
		    		if (i < list.size() && currentView){
						if (list.get(i).avatar != null && list.get(i).avatar.contains(".png") && list.get(i).thumb_bitmap == null){
							list.get(i).thumb_bitmap = ImageLoader.KembrelImageStoreAndLoad(list.get(i).avatar, getContext());
							if (list.get(i).thumb_bitmap != null){
								list.get(i).thumb_bitmap = RoundedCornerImage.getRoundedCornerBitmap(list.get(i).thumb_bitmap, 8);
							}
							
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
	 
	 class loadBitmapThreadAgain extends AsyncTask<Integer, Void, Void> {
		    // This method is called when the thread runs
			public int start, stop;

			protected Void doInBackground(Integer... num) {
		    	//get the all the current games
				this.start = num[0];
				this.stop = num[1];
		    	Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
		    	
		    	if (start > ImageLoader.loaderRange){
		    		start = start - ImageLoader.loaderRange;
		    	} else {
		    		start = 0;
		    	}
		    	if (stop + ImageLoader.loaderRange <= list.size()){
		    		stop = stop + ImageLoader.loaderRange;
		    	} else {
		    		stop = list.size();
		    	}
		    	
		    	for (int i = start; i < stop; i++){
		    		if (i < list.size() && currentView){
						if (list.get(i).avatar != null && list.get(i).avatar.contains(".png") && list.get(i).thumb_bitmap == null){
							list.get(i).thumb_bitmap = ImageLoader.KembrelImageStoreAndLoad(list.get(i).avatar, getContext());
							if (i < list.size()){
								list.get(i).thumb_bitmap = RoundedCornerImage.getRoundedCornerBitmap(list.get(i).thumb_bitmap, 8);
							} else {
								Log.i("thread again", "i < size");
							}
							
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
			    		if (i < list.size()){
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
			    		if (i < list.size()){
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
	  
	 	public void onResume() {
	 		new resumeBitmap().execute();
	 		
	 		currentView = true;
		}
	 	
	 	class resumeBitmap extends AsyncTask<Void, Void, Void> {
		    // This method is called when the thread runs
			protected Void doInBackground(Void... params) {
				for (int i = 0; i < list.size(); i++){
					if (i < list.size() && currentView){
						if (list.get(i).avatar != null && list.get(i).avatar.contains(".png") && list.get(i).thumb_bitmap == null){
							list.get(i).thumb_bitmap = ImageLoader.KembrelImageStoreAndLoad(list.get(i).avatar, getContext());
							if (list.get(i).thumb_bitmap != null){
								list.get(i).thumb_bitmap = RoundedCornerImage.getRoundedCornerBitmap(list.get(i).thumb_bitmap, 8);
							}
							
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
		
		public void onPause() {
			for (int i = 0; i < list.size(); i++){
				if (i < list.size()){
					if (list.get(i).thumb_bitmap != null){
						list.get(i).thumb_bitmap.recycle();
						list.get(i).thumb_bitmap = null;
					}
				} else {
					break;
				}
			}	
			
			currentView = false;
		}
		
	public void showFriendRequestDialog(final int request_id){
		final CharSequence[] items = {getContext().getString(R.string.Accept), getContext().getString(R.string.Reject), getContext().getString(R.string.Cancel)};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	switch(item){
		    	case 0:
		    		RestClient result1 = null;
					try {
						result1 = new Rest.requestBody().execute(Rest.FRIEND_REQUEST + request_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "answer", "1").get();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (result1.getResponseCode() == 200){
						Toast.makeText(getContext(), getContext().getString(R.string.Friend_request_accept_message), Toast.LENGTH_SHORT).show();
						refreshAfterPost();
					}
		    		break;
		    	case 1:
		    		RestClient result2 = null;
					try {
						result2 = new Rest.requestBody().execute(Rest.FRIEND_REQUEST + request_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "answer", "0").get();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (result2.getResponseCode() == 200){
						Toast.makeText(getContext(), getContext().getString(R.string.Friend_request_reject_message), Toast.LENGTH_SHORT).show(); 
						refreshAfterPost();
					}
		    		break;
		    	case 2:
		    
		    		break;
		    	}
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void refreshAfterPost() {
		// TODO Auto-generated method stub
		mPullRefreshListView.setRefreshing();
 		reloadView();
	}

}
