package events;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import network.ErrorCodeParser;

import org.json.JSONArray;
import org.json.JSONException;

import profile.AddCourse;
import profile.Course;
import profile.Friends;
import profile.OthersProfile;
import profile.Schedule;
import profile.SearchCourse;
import profile.UpdateStatus;
import profile.UserProfile;

import rewards.ScheduleModel;
import user.Profile;

import com.facebook.Session;
import com.facebook.SessionState;
import com.gotoohlala.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import datastorage.ConvertDpsToPixels;
import datastorage.Database;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import datastorage.Rest.request;

import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class OthersSchedule extends Activity {
	
	boolean currentView = false;
	
	SocialScheduleArrayAdapter listViewAdapter;
	ListView listView;
	PullToRefreshListView mPullRefreshListView;
	List<SocialScheduleModel> list_schedule = new ArrayList<SocialScheduleModel>();
	
	Handler mHandler = new Handler(Looper.getMainLooper());
	
	int dayOfWeek;
	int epoch_start_day;
	int epoch_end_day;
	int user_id;
	String user_name;
	
	BroadcastReceiver brLogout;
	
	public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.othersschedule);
			
			currentView = true;
			
			Button back = (Button) findViewById(R.id.back);
			back.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					onBackPressed();
				}
			}); 
			
			//content
			Bundle b = this.getIntent().getExtras();
			user_id = b.getInt("user_id");
			dayOfWeek = b.getInt("dayOfWeek");
			epoch_start_day = b.getInt("epoch_start_day");
			epoch_end_day = b.getInt("epoch_end_day");
			user_name = b.getString("user_name");
			
			TextView header = (TextView) findViewById(R.id.header);
			header.setTypeface(Fonts.getOpenSansBold(OthersSchedule.this));

			TextView headerName = (TextView) findViewById(R.id.headerName);
			headerName.setTypeface(Fonts.getOpenSansBold(OthersSchedule.this));
			headerName.setText(user_name);
			
			TextView bProfile = (TextView) findViewById(R.id.bProfile);
			bProfile.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v2) {
					Bundle extras = new Bundle();
					extras.putInt("user_id", user_id);
					Intent i = new Intent(OthersSchedule.this, OthersProfile.class);
					i.putExtras(extras);
					startActivity(i);
				}
			}); 
		
			//-------------------------------- ListView ------------------------------------------
			mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.lvSocialschedule);
			mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
				public void onRefresh() {
					mPullRefreshListView.setLastUpdatedLabel(getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
					
					reloadView();
				}
			});
			mPullRefreshListView.setRefreshing();
			
			listView = mPullRefreshListView.getRefreshableView();
			listViewAdapter = new SocialScheduleArrayAdapter(OthersSchedule.this, list_schedule);
			listView.setAdapter(listViewAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
				 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					position--;
					
				 }
			});
			
			TaskQueueImage.addTask(new SyncClassesThread(), OthersSchedule.this);
			//TaskQueueImage.addTask(new SyncEventsThread());
			
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
		    	list_schedule.clear();
		    	
		    	if (list_schedule.isEmpty()){
		    		TaskQueueImage.addTask(new SyncClassesThread(), OthersSchedule.this);
					//TaskQueueImage.addTask(new SyncEventsThread());
				}
		    }
		});
    }
    
    class SyncClassesThread extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
			RestClient result = null;
			try {
				result = new request().execute(Rest.SCHOOL_COURSE + "?user_id=" + user_id, Rest.OSESS + Profile.sk, Rest.GET).get();
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
					int status = threads.getJSONObject(i).getInt("status");
					String course_description = null;
					if (threads.getJSONObject(i).has("course_description")){
						course_description = threads.getJSONObject(i).getString("course_description");
					}
					JSONArray time_info = threads.getJSONObject(i).getJSONArray("time_info");
					for (int j = 0; j < time_info.length(); j++){
						int course_time_id = time_info.getJSONObject(j).getInt("id");
						String location = time_info.getJSONObject(j).getString("location");
						double latitude = time_info.getJSONObject(j).getDouble("latitude");
						double longitude = time_info.getJSONObject(j).getDouble("longitude");
						boolean is_biweekly = time_info.getJSONObject(j).getBoolean("is_biweekly");
						int day_of_week = time_info.getJSONObject(j).getInt("day_of_week");
						int start_time = time_info.getJSONObject(j).getInt("start_time");
						int end_time = time_info.getJSONObject(j).getInt("end_time");
						
						if (day_of_week == dayOfWeek){
							list_schedule.add(new SocialScheduleModel(user_id, TimeCounter.getClassTime(start_time), TimeCounter.getClassTime(end_time), 
									0, location, latitude, longitude, 10, TimeCounter.getEpochClassTime(start_time), TimeCounter.getEpochClassTime(end_time), 
									status, course_description, -1, -1, null, -1, course_id, course_name, course_code, course_time_id, is_biweekly? 1:0, 
									day_of_week, "RED", "icon_drawable", 0, 0, null, 0, 0, 0));
						}
					}
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			RestClient result2 = null;
			try {
				result2 = new request().execute(Rest.USER_EVENT + "?start=" + epoch_start_day + "&end=" + epoch_end_day + "&user_id=" + user_id, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray threads = new JSONArray(result2.getResponse());
				Log.i("user events: ", threads.toString());
				
				for (int i = 0; i < threads.length(); i++){
					int user_event_id = threads.getJSONObject(i).getInt("id");
					int user_id = threads.getJSONObject(i).getInt("user_id");
					int start_time = threads.getJSONObject(i).getInt("start");
					int end_time = threads.getJSONObject(i).getInt("end");
					String title = threads.getJSONObject(i).getString("title");
					String description = threads.getJSONObject(i).getString("description");
					String location = threads.getJSONObject(i).getString("location");
					double latitude = threads.getJSONObject(i).getDouble("latitude");
					double longitude = threads.getJSONObject(i).getDouble("longitude");
					int alert_time = threads.getJSONObject(i).getInt("alert_time");
					int status = threads.getJSONObject(i).getInt("status");
					int type = threads.getJSONObject(i).getInt("type");
					int extra_id = threads.getJSONObject(i).getInt("extra_id");
					
					list_schedule.add(new SocialScheduleModel(user_id, TimeCounter.getUserEventTime(start_time), TimeCounter.getUserEventTime(end_time), 
							0, location, latitude, longitude, alert_time, start_time, end_time, status, description, user_event_id, 
							extra_id, title, type, -1, null, null, -1, -1, -1, null, null, 0, 0, null, 0, 0, 0));
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (currentView){
				mHandler.post(new Runnable() {
	        		public void run() {
	        			//listView.setVisibility(View.GONE);
	        			Collections.sort(list_schedule, new TimeComparator());
				    	listView.invalidateViews();
				    	listViewAdapter.notifyDataSetChanged();
				    	//listView.setVisibility(View.VISIBLE);
				    	
				    	//preLoadImages();
				    	
				    	mPullRefreshListView.onRefreshComplete();
	        		}
				});
			}
	    }
	}
    
    class SyncEventsThread extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
			RestClient result = null;
			try {
				result = new request().execute(Rest.USER_EVENT + "?start=" + epoch_start_day + "&end=" + epoch_end_day + "&user_id=" + user_id, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray threads = new JSONArray(result.getResponse());
				Log.i("user events: ", threads.toString());
				
				for (int i = 0; i < threads.length(); i++){
					int user_event_id = threads.getJSONObject(i).getInt("id");
					int user_id = threads.getJSONObject(i).getInt("user_id");
					int start_time = threads.getJSONObject(i).getInt("start");
					int end_time = threads.getJSONObject(i).getInt("end");
					String title = threads.getJSONObject(i).getString("title");
					String description = threads.getJSONObject(i).getString("description");
					String location = threads.getJSONObject(i).getString("location");
					double latitude = threads.getJSONObject(i).getDouble("latitude");
					double longitude = threads.getJSONObject(i).getDouble("longitude");
					int alert_time = threads.getJSONObject(i).getInt("alert_time");
					int status = threads.getJSONObject(i).getInt("status");
					int type = threads.getJSONObject(i).getInt("type");
					int extra_id = threads.getJSONObject(i).getInt("extra_id");
					
					list_schedule.add(new SocialScheduleModel(user_id, TimeCounter.getUserEventTime(start_time), TimeCounter.getUserEventTime(end_time), 
							0, location, latitude, longitude, alert_time, start_time, end_time, status, description, user_event_id, 
							extra_id, title, type, -1, null, null, -1, -1, -1, null, null, 0, 0, null, 0, 0, 0));
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (currentView){
				mHandler.post(new Runnable() {
	        		public void run() {
	        			//listView.setVisibility(View.GONE);
	        			Collections.sort(list_schedule, new TimeComparator());
				    	listView.invalidateViews();
				    	listViewAdapter.notifyDataSetChanged();
				    	//listView.setVisibility(View.VISIBLE);
				    	
				    	//preLoadImages();
				    	
				    	mPullRefreshListView.onRefreshComplete();
	        		}
				});
			}
	    }
	}

	public class SocialScheduleArrayAdapter extends ArrayAdapter<SocialScheduleModel> {
		private final List<SocialScheduleModel> list;
		
		class ViewHolder {
			public TextView tvStart, tvStop, tvName;
			public ImageView ivIcon, ivIconCircle, ivClock, ivLeft;
			public RelativeLayout bg;
		}

		public SocialScheduleArrayAdapter(Context context, List<SocialScheduleModel> list) {
			super(context, R.layout.courserow, list);
			this.list = list;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null){
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.socialschedulerow, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.tvStart = (TextView) rowView.findViewById(R.id.tvStart);
				viewHolder.tvStop = (TextView) rowView.findViewById(R.id.tvStop);
				viewHolder.tvName = (TextView) rowView.findViewById(R.id.tvName);
				viewHolder.ivIcon = (ImageView) rowView.findViewById(R.id.ivIcon);
				viewHolder.ivIconCircle = (ImageView) rowView.findViewById(R.id.ivIconCircle);
				viewHolder.ivClock = (ImageView) rowView.findViewById(R.id.ivClock);
				viewHolder.ivLeft = (ImageView) rowView.findViewById(R.id.ivLeft);
				viewHolder.bg = (RelativeLayout) rowView.findViewById(R.id.bg);
				
				rowView.setTag(viewHolder);
			}
			
			ViewHolder holder = (ViewHolder) rowView.getTag();
			
			holder.tvStart.setText("");	
			holder.tvStop.setText("");	
			holder.tvName.setText("");	
			holder.ivIcon.setImageDrawable(null);
			holder.ivIconCircle.setImageDrawable(null);
			holder.ivClock.setImageDrawable(null);
			holder.ivLeft.setImageDrawable(null);
			
			String name = null;
			if (list_schedule.get(position).course_name != null){
				name = list_schedule.get(position).course_code;
				holder.ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.course_icon0));
				holder.ivIconCircle.setImageDrawable(getResources().getDrawable(R.drawable.schedule_roundcover));
				holder.ivClock.setImageDrawable(getResources().getDrawable(R.drawable.schedule_clock_icon));
				holder.ivLeft.setImageDrawable(getResources().getDrawable(R.color.red2));
			} else if (list_schedule.get(position).title != null){
				name = list_schedule.get(position).title;
				holder.ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.user_event_placeholder));
				holder.ivIconCircle.setImageDrawable(getResources().getDrawable(R.drawable.schedule_roundcover));
				holder.ivClock.setImageDrawable(getResources().getDrawable(R.drawable.schedule_clock_icon));
				holder.ivLeft.setImageDrawable(getResources().getDrawable(R.color.blue1));
			} else {
				//break
				holder.ivLeft.setImageDrawable(getResources().getDrawable(R.drawable.schedule_break_first_bg));
			}

			holder.tvStart.setText(list_schedule.get(position).start_time_text);	
			holder.tvStop.setText(list_schedule.get(position).end_time_text);	
			holder.tvName.setText(name);	

			return rowView;
		}
		
	}
	
	public class TimeComparator implements Comparator<SocialScheduleModel> {
        public int compare(SocialScheduleModel o1, SocialScheduleModel o2) {
            return String.valueOf(o1.start_time).compareTo(String.valueOf(o2.start_time));
        }
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		currentView = false;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		currentView = true;
	}

}
