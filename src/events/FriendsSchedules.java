package events;

import java.util.ArrayList;
import java.util.Iterator;
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
import user.Profile;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.gotoohlala.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

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

public class FriendsSchedules extends Activity {
	ListView listView;
	PullToRefreshListView mPullRefreshListView;
	List<FriendsModel> list = new ArrayList<FriendsModel>();
	FriendsSchedulesArrayAdapter adapter;
	
	loadBitmapTread loadBitmapTread;
	
	Handler mHandler = new Handler();
	
	int user_id = 0;
	int dayOfWeek, epoch_start_day, epoch_end_day;
	
	BroadcastReceiver brLogout;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends);
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		}); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setText(getString(R.string.FRIENDS_SCHEDULES));
		header.setTypeface(Fonts.getOpenSansBold(FriendsSchedules.this));
		
		Bundle b = this.getIntent().getExtras();
		if (b != null){
			user_id = b.getInt("user_id");
			dayOfWeek = b.getInt("dayOfWeek");
			epoch_start_day = b.getInt("epoch_start_day");
			epoch_end_day = b.getInt("epoch_end_day");
		}
		
		Button bAdd = (Button) findViewById(R.id.bAdd);
		bAdd.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(FriendsSchedules.this, SearchFriend.class);
				startActivity(i);
			}
		}); 
		if (user_id != Profile.userId){
			bAdd.setVisibility(View.GONE);
		}
		if (epoch_start_day != 0){
			bAdd.setVisibility(View.GONE);
		}
		
		TextView tvButton = (TextView) findViewById(R.id.tvButton);
		tvButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(FriendsSchedules.this, SuggestedFriends.class);
				startActivity(i);
			}
		}); 

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.lvFriends);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
				reloadView();
			}
		});
		mPullRefreshListView.setRefreshing();
		
		listView = mPullRefreshListView.getRefreshableView();
		adapter = new FriendsSchedulesArrayAdapter(FriendsSchedules.this, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				position--;
				
				if (list.get(position).has_schedule){
					Bundle extras = new Bundle();
					extras.putInt("user_id", list.get(position).user_id);
					extras.putInt("dayOfWeek", dayOfWeek);
					extras.putInt("epoch_start_day", epoch_start_day);
					extras.putInt("epoch_end_day", epoch_end_day);
					extras.putString("user_name", list.get(position).name);
					Intent i = new Intent(FriendsSchedules.this, OthersSchedule.class);
					i.putExtras(extras);
					startActivity(i);
				} else {
					
				}
			}
		});
		
		TaskQueueImage.addTask(new updateListThread(), FriendsSchedules.this);
		
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
	
	public void reloadView() {
        // Do work to refresh the list here.
		mHandler.post(new Runnable() {
    		public void run() {
		    	list.clear();
		    	
		    	if (list.isEmpty()){
		    		TaskQueueImage.addTask(new updateListThread(), FriendsSchedules.this);
				}
		    }
		});
    }
	
	class updateListThread extends Thread {
	    // This method is called when the thread runs
		List<FriendsModel> listTemp = new ArrayList<FriendsModel>();
		
	    public void run() {
	    	RestClient result = null;
			try {
				if (user_id == Profile.userId){
					result = new request().execute(Rest.USER + "1;9999" + "?friends_only=1&with_day_schedule=1&schedule_day_of_week=" + dayOfWeek + 
							"&with_user_events=1&user_event_start=" + epoch_start_day + "&user_event_end=" + epoch_end_day, Rest.OSESS + Profile.sk, Rest.GET).get();
				} else {
					result = new request().execute(Rest.USER + user_id + "?start=1&end=9999with_friends=1&with_day_schedule=1&schedule_day_of_week=" + dayOfWeek + 
							"&with_user_events=1&user_event_start=" + epoch_start_day + "&user_event_end=" + epoch_end_day, Rest.OSESS + Profile.sk, Rest.GET).get();
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
						String name = threads.getJSONObject(i).getString("firstname");
						String avatar_thumb_url = threads.getJSONObject(i).getString("avatar_thumb_url");
						String looking_for = threads.getJSONObject(i).getString("looking_for");
						boolean has_schedule = threads.getJSONObject(i).getBoolean("has_schedule");
						int timezone =  threads.getJSONObject(i).getJSONArray("daily_course_schedule").getInt(0);
						JSONObject daily_course_schedule = threads.getJSONObject(i).getJSONArray("daily_course_schedule").getJSONObject(1);
						JSONArray user_event_times = threads.getJSONObject(i).getJSONArray("user_event_times");
						
						List<ClassEventsTimesModel> class_times = new ArrayList<ClassEventsTimesModel>();
						Iterator itr = daily_course_schedule.keys();
					    while(itr.hasNext()) {
					    	JSONArray times = daily_course_schedule.getJSONArray(itr.next().toString());
					    	for (int j = 0; j < times.length(); j++){
					    		class_times.add(new ClassEventsTimesModel(times.getJSONArray(j).getInt(0), times.getJSONArray(j).getInt(1)));
					    	}
					    }
					    
					    List<ClassEventsTimesModel> event_times = new ArrayList<ClassEventsTimesModel>();
				    	for (int j = 0; j < user_event_times.length(); j++){
				    		event_times.add(new ClassEventsTimesModel(user_event_times.getJSONArray(j).getInt(0) - epoch_start_day, user_event_times.getJSONArray(j).getInt(1) - epoch_start_day));
				    	}
					    
						listTemp.add(new FriendsModel(user_id, name, avatar_thumb_url, looking_for, has_schedule, class_times, event_times));
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
						String name = threads.getJSONObject(i).getString("firstname");
						String avatar_thumb_url = threads.getJSONObject(i).getString("avatar_thumb_url");
						String looking_for = threads.getJSONObject(i).getString("looking_for");
						boolean has_schedule = threads.getJSONObject(i).getBoolean("has_schedule");
						int timezone =  threads.getJSONObject(i).getJSONArray("daily_course_schedule").getInt(0);
						JSONObject daily_course_schedule = threads.getJSONObject(i).getJSONArray("daily_course_schedule").getJSONObject(1);
						JSONArray user_event_times = threads.getJSONObject(i).getJSONArray("user_event_times");
						
						List<ClassEventsTimesModel> class_times = new ArrayList<ClassEventsTimesModel>();
						Iterator itr = daily_course_schedule.keys();
					    while(itr.hasNext()) {
					    	JSONArray times = daily_course_schedule.getJSONArray(itr.next().toString());
					    	for (int j = 0; j < times.length(); j++){
					    		class_times.add(new ClassEventsTimesModel(times.getJSONArray(j).getInt(0), times.getJSONArray(j).getInt(1)));
					    	}
					    }
					    
					    List<ClassEventsTimesModel> event_times = new ArrayList<ClassEventsTimesModel>();
					    for (int j = 0; j < user_event_times.length(); j++){
					    	event_times.add(new ClassEventsTimesModel(user_event_times.getJSONArray(j).getInt(0) - epoch_start_day, user_event_times.getJSONArray(j).getInt(1) - epoch_start_day));
				    	}
					    
						listTemp.add(new FriendsModel(user_id, name, avatar_thumb_url, looking_for, has_schedule, class_times, event_times));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
				mHandler.post(new Runnable() {
	        		public void run() {
	        			//listView.setVisibility(View.GONE);
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
						list.get(i).image_bitmap = ImageLoader.KembrelImageStoreAndLoad(list.get(i).avatar_thumb_url, FriendsSchedules.this);
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
	
	public class FriendsSchedulesArrayAdapter extends ArrayAdapter<FriendsModel> {
		private final List<FriendsModel> list;
		
		class ViewHolder {
			public ImageView ivThumb, ivScheduleBg, ivBlock1, ivBlock2, ivBlock3, ivBlock4, 
				ivBlock5, ivBlock6, ivBlock7, ivBlock8, ivBlock9, ivBlock10, ivBlock11, 
				ivBlock12, ivBlock13, ivStatus, ivTimeNow1, ivTimeNow2, ivTimeNow3, ivTimeNow4, 
				ivTimeNow5, ivTimeNow6, ivTimeNow7, ivTimeNow8, ivTimeNow9, ivTimeNow10, ivTimeNow11, 
				ivTimeNow12, ivTimeNow13;
			public TextView name, tvStatus;
			public Button bRequest;
		}

		public FriendsSchedulesArrayAdapter(Context context, List<FriendsModel> list) {
			super(context, R.layout.friendsschedulesrow, list);
			this.list = list;
		}
		
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null){
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.friendsschedulesrow, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.ivThumb = (ImageView) rowView.findViewById(R.id.ivThumb);
				viewHolder.name = (TextView) rowView.findViewById(R.id.name);
				viewHolder.ivScheduleBg = (ImageView) rowView.findViewById(R.id.ivScheduleBg);
				viewHolder.ivBlock1 = (ImageView) rowView.findViewById(R.id.ivBlock1);
				viewHolder.ivBlock2 = (ImageView) rowView.findViewById(R.id.ivBlock2);
				viewHolder.ivBlock3 = (ImageView) rowView.findViewById(R.id.ivBlock3);
				viewHolder.ivBlock4 = (ImageView) rowView.findViewById(R.id.ivBlock4);
				viewHolder.ivBlock5 = (ImageView) rowView.findViewById(R.id.ivBlock5);
				viewHolder.ivBlock6 = (ImageView) rowView.findViewById(R.id.ivBlock6);
				viewHolder.ivBlock7 = (ImageView) rowView.findViewById(R.id.ivBlock7);
				viewHolder.ivBlock8 = (ImageView) rowView.findViewById(R.id.ivBlock8);
				viewHolder.ivBlock9 = (ImageView) rowView.findViewById(R.id.ivBlock9);
				viewHolder.ivBlock10 = (ImageView) rowView.findViewById(R.id.ivBlock10);
				viewHolder.ivBlock11 = (ImageView) rowView.findViewById(R.id.ivBlock11);
				viewHolder.ivBlock12 = (ImageView) rowView.findViewById(R.id.ivBlock12);
				viewHolder.ivBlock13 = (ImageView) rowView.findViewById(R.id.ivBlock13);
				viewHolder.ivStatus = (ImageView) rowView.findViewById(R.id.ivStatus);
				viewHolder.tvStatus = (TextView) rowView.findViewById(R.id.tvStatus);
				viewHolder.ivTimeNow1 = (ImageView) rowView.findViewById(R.id.ivTimeNow1);
				viewHolder.ivTimeNow2 = (ImageView) rowView.findViewById(R.id.ivTimeNow2);
				viewHolder.ivTimeNow3 = (ImageView) rowView.findViewById(R.id.ivTimeNow3);
				viewHolder.ivTimeNow4 = (ImageView) rowView.findViewById(R.id.ivTimeNow4);
				viewHolder.ivTimeNow5 = (ImageView) rowView.findViewById(R.id.ivTimeNow5);
				viewHolder.ivTimeNow6 = (ImageView) rowView.findViewById(R.id.ivTimeNow6);
				viewHolder.ivTimeNow7 = (ImageView) rowView.findViewById(R.id.ivTimeNow7);
				viewHolder.ivTimeNow8 = (ImageView) rowView.findViewById(R.id.ivTimeNow8);
				viewHolder.ivTimeNow9 = (ImageView) rowView.findViewById(R.id.ivTimeNow9);
				viewHolder.ivTimeNow10 = (ImageView) rowView.findViewById(R.id.ivTimeNow10);
				viewHolder.ivTimeNow11 = (ImageView) rowView.findViewById(R.id.ivTimeNow11);
				viewHolder.ivTimeNow12 = (ImageView) rowView.findViewById(R.id.ivTimeNow12);
				viewHolder.ivTimeNow13 = (ImageView) rowView.findViewById(R.id.ivTimeNow13);
				viewHolder.bRequest = (Button) rowView.findViewById(R.id.bRequest);
				
				rowView.setTag(viewHolder);
			}
			
			ViewHolder holder = (ViewHolder) rowView.getTag();
			
			holder.ivThumb.setImageBitmap(null);
			holder.name.setText("");	
			holder.ivScheduleBg.setVisibility(View.VISIBLE);
			holder.ivBlock1.setBackgroundDrawable(null);
			holder.ivBlock2.setBackgroundDrawable(null);
			holder.ivBlock3.setBackgroundDrawable(null);
			holder.ivBlock4.setBackgroundDrawable(null);
			holder.ivBlock5.setBackgroundDrawable(null);
			holder.ivBlock6.setBackgroundDrawable(null);
			holder.ivBlock7.setBackgroundDrawable(null);
			holder.ivBlock8.setBackgroundDrawable(null);
			holder.ivBlock9.setBackgroundDrawable(null);
			holder.ivBlock10.setBackgroundDrawable(null);
			holder.ivBlock11.setBackgroundDrawable(null);
			holder.ivBlock12.setBackgroundDrawable(null);
			holder.ivBlock13.setBackgroundDrawable(null);
			holder.ivStatus.setBackgroundDrawable(null);
			holder.tvStatus.setText("");	
			holder.ivTimeNow1.setBackgroundDrawable(null);
			holder.ivTimeNow2.setBackgroundDrawable(null);
			holder.ivTimeNow3.setBackgroundDrawable(null);
			holder.ivTimeNow4.setBackgroundDrawable(null);
			holder.ivTimeNow5.setBackgroundDrawable(null);
			holder.ivTimeNow6.setBackgroundDrawable(null);
			holder.ivTimeNow7.setBackgroundDrawable(null);
			holder.ivTimeNow8.setBackgroundDrawable(null);
			holder.ivTimeNow9.setBackgroundDrawable(null);
			holder.ivTimeNow10.setBackgroundDrawable(null);
			holder.ivTimeNow11.setBackgroundDrawable(null);
			holder.ivTimeNow12.setBackgroundDrawable(null);
			holder.ivTimeNow13.setBackgroundDrawable(null);
			holder.bRequest.setVisibility(View.GONE);
			
			if (list.get(position).image_bitmap != null){
				//image save into the memory and cache
				holder.ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(list.get(position).image_bitmap, 8));
			}
			
			holder.name.setTextColor(getResources().getColorStateList(R.color.blue0));
			holder.name.setTypeface(Fonts.getOpenSansBold(FriendsSchedules.this));
			holder.name.setText(StringLanguageSelector.retrieveString(list.get(position).name));
			
			holder.bRequest.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					TaskQueueImage.addTask(new courseScheduleRequest(list.get(position).user_id), FriendsSchedules.this);
				}
			});
			
			//---------------------class_times---------------------------
			if (!list.get(position).class_times.isEmpty()){
				if (list.get(position).timesClass[0]){
					holder.ivBlock1.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_block));
				} 
				if (list.get(position).timesClass[1]){
					holder.ivBlock2.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_block));
				} 
				if (list.get(position).timesClass[2]){
					holder.ivBlock3.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_block));
				} 
				if (list.get(position).timesClass[3]){
					holder.ivBlock4.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_block));
				} 
				if (list.get(position).timesClass[4]){
					holder.ivBlock5.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_block));
				} 
				if (list.get(position).timesClass[5]){
					holder.ivBlock6.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_block));
				} 
				if (list.get(position).timesClass[6]){
					holder.ivBlock7.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_block));
				} 
				if (list.get(position).timesClass[7]){
					holder.ivBlock8.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_block));
				} 
				if (list.get(position).timesClass[8]){
					holder.ivBlock9.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_block));
				} 
				if (list.get(position).timesClass[9]){
					holder.ivBlock10.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_block));
				} 
				if (list.get(position).timesClass[10]){
					holder.ivBlock11.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_block));
				} 
				if (list.get(position).timesClass[11]){
					holder.ivBlock12.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_block));
				} 
				if (list.get(position).timesClass[12]){
					holder.ivBlock13.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_block));
				} 
			} 
			
			//------------------------------------event_times---------------------------------------
			if (!list.get(position).event_times.isEmpty()){
				if (list.get(position).timesEvent[0]){
					holder.ivBlock1.setBackgroundDrawable(getResources().getDrawable(R.drawable.friends_schedule_block_red));
				} 
				if (list.get(position).timesEvent[1]){
					holder.ivBlock2.setBackgroundDrawable(getResources().getDrawable(R.drawable.friends_schedule_block_red));
				} 
				if (list.get(position).timesEvent[2]){
					holder.ivBlock3.setBackgroundDrawable(getResources().getDrawable(R.drawable.friends_schedule_block_red));
				} 
				if (list.get(position).timesEvent[3]){
					holder.ivBlock4.setBackgroundDrawable(getResources().getDrawable(R.drawable.friends_schedule_block_red));
				} 
				if (list.get(position).timesEvent[4]){
					holder.ivBlock5.setBackgroundDrawable(getResources().getDrawable(R.drawable.friends_schedule_block_red));
				} 
				if (list.get(position).timesEvent[5]){
					holder.ivBlock6.setBackgroundDrawable(getResources().getDrawable(R.drawable.friends_schedule_block_red));
				} 
				if (list.get(position).timesEvent[6]){
					holder.ivBlock7.setBackgroundDrawable(getResources().getDrawable(R.drawable.friends_schedule_block_red));
				} 
				if (list.get(position).timesEvent[7]){
					holder.ivBlock8.setBackgroundDrawable(getResources().getDrawable(R.drawable.friends_schedule_block_red));
				} 
				if (list.get(position).timesEvent[8]){
					holder.ivBlock9.setBackgroundDrawable(getResources().getDrawable(R.drawable.friends_schedule_block_red));
				} 
				if (list.get(position).timesEvent[9]){
					holder.ivBlock10.setBackgroundDrawable(getResources().getDrawable(R.drawable.friends_schedule_block_red));
				} 
				if (list.get(position).timesEvent[10]){
					holder.ivBlock11.setBackgroundDrawable(getResources().getDrawable(R.drawable.friends_schedule_block_red));
				} 
				if (list.get(position).timesEvent[11]){
					holder.ivBlock12.setBackgroundDrawable(getResources().getDrawable(R.drawable.friends_schedule_block_red));
				} 
				if (list.get(position).timesEvent[12]){
					holder.ivBlock13.setBackgroundDrawable(getResources().getDrawable(R.drawable.friends_schedule_block_red));
				} 
			}
			
			if (list.get(position).busyNow){
				holder.ivStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.label_busy));
				holder.tvStatus.setText(getString(R.string.BUSY));
			} else if (list.get(position).inClass){
				holder.ivStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.label_inclass));
				holder.tvStatus.setText(getString(R.string.IN_CLASS));
			} else {
				holder.ivStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.label_free));
				holder.tvStatus.setText(getString(R.string.FREE));
			}
			
			if (!list.get(position).has_schedule){
				holder.ivScheduleBg.setVisibility(View.GONE);
				holder.bRequest.setVisibility(View.VISIBLE);
				holder.ivStatus.setBackgroundDrawable(null);
				holder.tvStatus.setText("");
			} else {
				if (list.get(position).timesNow[0]){
					holder.ivTimeNow1.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_now));
				} else if (list.get(position).timesNow[1]){
					holder.ivTimeNow2.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_now));
				} else if (list.get(position).timesNow[2]){
					holder.ivTimeNow3.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_now));
				} else if (list.get(position).timesNow[3]){
					holder.ivTimeNow4.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_now));
				} else if (list.get(position).timesNow[4]){
					holder.ivTimeNow5.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_now));
				} else if (list.get(position).timesNow[5]){
					holder.ivTimeNow6.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_now));
				} else if (list.get(position).timesNow[6]){
					holder.ivTimeNow7.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_now));
				} else if (list.get(position).timesNow[7]){
					holder.ivTimeNow8.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_now));
				} else if (list.get(position).timesNow[8]){
					holder.ivTimeNow9.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_now));
				} else if (list.get(position).timesNow[9]){
					holder.ivTimeNow10.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_now));
				} else if (list.get(position).timesNow[10]){
					holder.ivTimeNow11.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_now));
				} else if (list.get(position).timesNow[11]){
					holder.ivTimeNow12.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_now));
				} else if (list.get(position).timesNow[12]){
					holder.ivTimeNow13.setBackgroundDrawable(getResources().getDrawable(R.drawable.social_schedule_now));
				} 
			}
			
			return rowView;
		}
	}
	
	 	class courseScheduleRequest extends Thread {
	 		// This method is called when the thread runs
	 		int userid;
		 
	 		public courseScheduleRequest(int userid){
	 			this.userid = userid;
	 		}
			
		    public void run() {
		    	RestClient result = null;
				try {
					result = new Rest.requestBody().execute(Rest.USER_REQUEST, Rest.OSESS + Profile.sk, Rest.POST, "2", "recipient_user_id", String.valueOf(userid), "request_type", "1").get();
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
		        			AlertDialog alert = new AlertDialog.Builder(FriendsSchedules.this).create();
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
	
}
