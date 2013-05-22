package com.gotoohlala;

import inbox.InboxModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.FriendsModel;
import profile.OthersProfile;
import profile.SearchFriend;
import profile.SuggestedFriends;

import rewards.Explore.ExploreArrayAdapter;
import rewards.ExploreModel;
import rewards.RewardsVenuesStoresContent;
import smackXMPP.XMPPClient;
import user.Profile;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.gotoohlala.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import datastorage.DeviceDimensions;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import datastorage.Rest.request;
import events.EventsCampusStoresDetail;
import events.EventsEventsMap;
import events.OthersSchedule;

public class FriendsLeftPanel extends FrameLayout {
	ListView listView;
	PullToRefreshListView mPullRefreshListView;
	List<InboxModel> list = new ArrayList<InboxModel>();
	InboxArrayAdapter adapter;
	
	loadBitmapTread loadBitmapTread;
	
	Handler mHandler = new Handler();
	
	int user_id = Profile.userId;
	
	View v = null;
	 
	public TopMenuNavbar TopMenuNavbar;
	
	public FriendsLeftPanel(Context context) {
		super(context);
		
		final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
		v = (RelativeLayout) mLayoutInflater.inflate(R.layout.friendsleftpanel, null);  
	    addView(v);  
		
		TextView header = (TextView) v.findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(getContext()));
		
		TopMenuNavbar = (TopMenuNavbar) v.findViewById(R.id.bTopMenuNavBar);
		
		Button bAdd = (Button) v.findViewById(R.id.bAdd);
		bAdd.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getContext(), SearchFriend.class);
				getContext().startActivity(i);
			}
		});
		
		TextView tvButton = (TextView) v.findViewById(R.id.tvButton);
		tvButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getContext(), SuggestedFriends.class);
				getContext().startActivity(i);
			}
		}); 

		mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.lvFriends);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(getContext().getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
				reloadView();
			}
		});
		
		listView = mPullRefreshListView.getRefreshableView();
		adapter = new InboxArrayAdapter(getContext(), list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				position--;
				
				if (list.get(position).friend_request_id != 0){
					showFriendRequestDialog(list.get(position).friend_request_id);
				} else {
					Bundle extras = new Bundle();
					extras.putInt("user_id", list.get(position).user_id);
					Intent i = new Intent(getContext(), OthersProfile.class);
					i.putExtras(extras);
					getContext().startActivity(i);
				}
			}
		});
		
		refreshAfterPost();
	}
	
	public void reloadView() {
        // Do work to refresh the list here.
		mHandler.post(new Runnable() {
    		public void run() {
		    	list.clear();
		    	
		    	if (list.isEmpty()){
		    		TaskQueueImage.addTask(new updateListThread(), getContext());
				}
		    }
		});
    }
	
	class updateListThread extends Thread {
	    // This method is called when the thread runs
		List<InboxModel> listTemp = new ArrayList<InboxModel>();
		List<InboxModel> listTemp_friend_requests = new ArrayList<InboxModel>();
		
	    public void run() {
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
			
			if (user_id == Profile.userId){
				try {
					final JSONArray threads = new JSONArray(result.getResponse());
					Log.i("friends: ", threads.toString());
					
					for (int i = 0; i < threads.length(); i++){
						int user_id = threads.getJSONObject(i).getInt("id");
						String first_name = threads.getJSONObject(i).getString("firstname");
						String last_name = threads.getJSONObject(i).getString("lastname");
						String avatar_thumb_url = threads.getJSONObject(i).getString("avatar_thumb_url");
						String looking_for = threads.getJSONObject(i).getString("looking_for");
						//boolean has_schedule = threads.getJSONObject(i).getBoolean("has_schedule");
						
						listTemp.add(new InboxModel(null, 0, avatar_thumb_url, first_name, last_name, user_id, null, 0, looking_for, null, null, 0, 0, null, null));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					final JSONArray threads = new JSONObject(result.getResponse()).getJSONArray("friend_list");
					Log.i("friends: ", threads.toString());
					
					for (int i = 0; i < threads.length(); i++){
						int user_id = threads.getJSONObject(i).getInt("id");
						String first_name = threads.getJSONObject(i).getString("firstname");
						String last_name = threads.getJSONObject(i).getString("lastname");
						String avatar_thumb_url = threads.getJSONObject(i).getString("avatar_thumb_url");
						String looking_for = threads.getJSONObject(i).getString("looking_for");
						//boolean has_schedule = threads.getJSONObject(i).getBoolean("has_schedule");
						
						listTemp.add(new InboxModel(null, 0, avatar_thumb_url, first_name, last_name, user_id, null, 0, looking_for, null, null, 0, 0, null, null));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
				mHandler.post(new Runnable() {
	        		public void run() {
	        			//listView.setVisibility(View.GONE);
	        			list.addAll(listTemp_friend_requests);
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
	
	public class InboxArrayAdapter extends ArrayAdapter<InboxModel> {
		private final Context context;
		private final List<InboxModel> list;
		
		class ViewHolder {
			public ImageView ivThumb;
			public TextView tvUserName;
			public TextView tvTime;
			public TextView tvLastMsg;
			public TextView tvUnreadMsg;
			public TextView tvInboxCategory;
		}

		public InboxArrayAdapter(Context context, List<InboxModel> list) {
			super(context, R.layout.inboxrow, list);
			this.context = context;
			this.list = list;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null){
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.inboxrow, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.ivThumb = (ImageView) rowView.findViewById(R.id.ivThumb);
				viewHolder.tvUserName = (TextView) rowView.findViewById(R.id.tvUserName);
				viewHolder.tvTime = (TextView) rowView.findViewById(R.id.tvTime);
				viewHolder.tvLastMsg = (TextView) rowView.findViewById(R.id.tvLastMsg);
				viewHolder.tvUnreadMsg = (TextView) rowView.findViewById(R.id.tvUnreadMsg);
				viewHolder.tvInboxCategory = (TextView) rowView.findViewById(R.id.tvInboxCategory);
				
				rowView.setTag(viewHolder);
			}
			
			ViewHolder holder = (ViewHolder) rowView.getTag();
			
			holder.ivThumb.setImageBitmap(null);
			holder.ivThumb.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar));
			holder.tvUserName.setText(null);
			holder.tvTime.setText(null);
			holder.tvLastMsg.setText(null);
			holder.tvUnreadMsg.setText(null);
			holder.tvUnreadMsg.setBackgroundDrawable(null);
			holder.tvInboxCategory.setText("");
			holder.tvInboxCategory.setVisibility(View.GONE);
			
			if (list.get(position).thumb_bitmap != null){
				//image save into the memory and cache
				holder.ivThumb.setImageDrawable(null);
				holder.ivThumb.setImageBitmap(list.get(position).thumb_bitmap);
			} 
			
			holder.tvUserName.setTypeface(Fonts.getOpenSansBold(context));
			holder.tvUserName.setText(list.get(position).first_name + " " + list.get(position).last_name);
			holder.tvTime.setTypeface(Fonts.getOpenSansLightItalic(context));
			if (list.get(position).friend_request_id != 0){
				holder.tvTime.setText(list.get(position).friend_request_time_sent);
				holder.tvLastMsg.setText(list.get(position).friend_request_message);
				holder.tvLastMsg.setMaxWidth((DeviceDimensions.getWidth(context)*13)/20);
			} else {
				holder.tvTime.setText(list.get(position).time);
				holder.tvLastMsg.setText(list.get(position).last_message);
				holder.tvLastMsg.setMaxWidth((DeviceDimensions.getWidth(context)*13)/20);
				
				if (list.get(position).num_unread > 0){
					holder.tvUnreadMsg.setText(String.valueOf(list.get(position).num_unread));
					holder.tvUnreadMsg.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.buttonlike));
				}
			}
			
			if (list.get(position).friend_request_id != 0 && position == 0){
				holder.tvInboxCategory.setVisibility(View.VISIBLE);
				holder.tvInboxCategory.setText(context.getString(R.string.FRIEND_REQUESTS).toUpperCase());
			}
			
			if (list.get(position).friend_request_id == 0 && (position == 0 || list.get(position-1).friend_request_id != 0)){
				holder.tvInboxCategory.setVisibility(View.VISIBLE);
				holder.tvInboxCategory.setText(context.getString(R.string.FRIENDS).toUpperCase());
			}
			
			return rowView;
		}
		
		
	}
	
}
