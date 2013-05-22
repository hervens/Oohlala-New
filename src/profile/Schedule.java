package profile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;

import profile.Friends.updateListThread;

import rewards.CampusClubs;
import rewards.ExploreModel;
import rewards.CampusClubs.ExploreArrayAdapter;
import rewards.ScheduleModel;
import user.Profile;
import campuswall.CampusWallInviteFacebook;

import com.gotoohlala.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import datastorage.Fonts;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import datastorage.Rest.request;
import events.EventsEventsDetail;
import events.EventsEventsModel;

import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Schedule extends Activity {
	
	Button bDay0, bDay1, bDay2, bDay3, bDay4, bDay5, bDay6;
	
	ListView listView;
	PullToRefreshListView mPullRefreshListView;
	List<ScheduleModel> list = new ArrayList<ScheduleModel>();
	List<ScheduleModel> listShow = new ArrayList<ScheduleModel>();
	ScheduleArrayAdapter adapter;
	
	Handler mHandler = new Handler();
	
	int daySelected = -1;
	
	int user_id = 0;
	
	boolean backPressed = false;
	
	RelativeLayout rlEmptySchedule;
	TextView tvNoClass, tvAddClass;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule);
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(Schedule.this));
		
		Bundle b = this.getIntent().getExtras();
		if (b != null){
			user_id = b.getInt("user_id");
			if (b.containsKey("daySelected")){
				daySelected = b.getInt("daySelected");
			}
			if (b.containsKey("back")){
				if (b.getInt("back") == 1){
					//onBackPressed();
				}
			}
		}
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		}); 
		
		Button bAdd = (Button) findViewById(R.id.bAdd);
		bAdd.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Bundle extras = new Bundle();
				extras.putInt("daySelected", daySelected);
				Intent i = new Intent(Schedule.this, SearchCourse.class);
				i.putExtras(extras);
				startActivity(i);
			}
		}); 
		if (user_id != 0){
			bAdd.setVisibility(View.GONE);
		}
		
		bDay0 = (Button) findViewById(R.id.bDay0);
		bDay1 = (Button) findViewById(R.id.bDay1);
		bDay2 = (Button) findViewById(R.id.bDay2);
		bDay3 = (Button) findViewById(R.id.bDay3);
		bDay4 = (Button) findViewById(R.id.bDay4);
		bDay5 = (Button) findViewById(R.id.bDay5);
		bDay6 = (Button) findViewById(R.id.bDay6);
		
		if (daySelected == -1){
			Calendar calendar = Calendar.getInstance();
			if (calendar.get(Calendar.DAY_OF_WEEK) == 1){
				daySelected = 7;
			} else {
				daySelected = calendar.get(Calendar.DAY_OF_WEEK) - 1;
			}
			Log.i("daySelected", String.valueOf(daySelected));
		}
		
		switch(daySelected){
		case 7:
			bDay0.setBackgroundResource(R.drawable.weekday_segmented_selected_left);
        	bDay1.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay2.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay3.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay4.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay5.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay6.setBackgroundResource(R.drawable.weekday_segmented_button_right);
        	
        	bDay0.setClickable(false);
        	bDay1.setClickable(true);
        	bDay2.setClickable(true);
        	bDay3.setClickable(true);
        	bDay4.setClickable(true);
        	bDay5.setClickable(true);
        	bDay6.setClickable(true);
        	break;
		case 1:
			bDay0.setBackgroundResource(R.drawable.weekday_segmented_button_left);
        	bDay1.setBackgroundResource(R.drawable.weekday_segmented_selected_center);
        	bDay2.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay3.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay4.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay5.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay6.setBackgroundResource(R.drawable.weekday_segmented_button_right);
        	
        	bDay0.setClickable(true);
        	bDay1.setClickable(false);
        	bDay2.setClickable(true);
        	bDay3.setClickable(true);
        	bDay4.setClickable(true);
        	bDay5.setClickable(true);
        	bDay6.setClickable(true);
        	break;
		case 2:
			bDay0.setBackgroundResource(R.drawable.weekday_segmented_button_left);
        	bDay1.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay2.setBackgroundResource(R.drawable.weekday_segmented_selected_center);
        	bDay3.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay4.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay5.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay6.setBackgroundResource(R.drawable.weekday_segmented_button_right);
        	
        	bDay0.setClickable(true);
        	bDay1.setClickable(true);
        	bDay2.setClickable(false);
        	bDay3.setClickable(true);
        	bDay4.setClickable(true);
        	bDay5.setClickable(true);
        	bDay6.setClickable(true);
        	break;
		case 3:
			bDay0.setBackgroundResource(R.drawable.weekday_segmented_button_left);
        	bDay1.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay2.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay3.setBackgroundResource(R.drawable.weekday_segmented_selected_center);
        	bDay4.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay5.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay6.setBackgroundResource(R.drawable.weekday_segmented_button_right);
        	
        	bDay0.setClickable(true);
        	bDay1.setClickable(true);
        	bDay2.setClickable(true);
        	bDay3.setClickable(false);
        	bDay4.setClickable(true);
        	bDay5.setClickable(true);
        	bDay6.setClickable(true);
        	break;
		case 4:
			bDay0.setBackgroundResource(R.drawable.weekday_segmented_button_left);
        	bDay1.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay2.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay3.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay4.setBackgroundResource(R.drawable.weekday_segmented_selected_center);
        	bDay5.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay6.setBackgroundResource(R.drawable.weekday_segmented_button_right);
        	
        	bDay0.setClickable(true);
        	bDay1.setClickable(true);
        	bDay2.setClickable(true);
        	bDay3.setClickable(true);
        	bDay4.setClickable(false);
        	bDay5.setClickable(true);
        	bDay6.setClickable(true);
        	break;
		case 5:
			bDay0.setBackgroundResource(R.drawable.weekday_segmented_button_left);
        	bDay1.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay2.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay3.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay4.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay5.setBackgroundResource(R.drawable.weekday_segmented_selected_center);
        	bDay6.setBackgroundResource(R.drawable.weekday_segmented_button_right);
        	
        	bDay0.setClickable(true);
        	bDay1.setClickable(true);
        	bDay2.setClickable(true);
        	bDay3.setClickable(true);
        	bDay4.setClickable(true);
        	bDay5.setClickable(false);
        	bDay6.setClickable(true);
        	break;
		case 6:
			bDay0.setBackgroundResource(R.drawable.weekday_segmented_button_left);
        	bDay1.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay2.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay3.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay4.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay5.setBackgroundResource(R.drawable.weekday_segmented_button_center);
        	bDay6.setBackgroundResource(R.drawable.weekday_segmented_selected_right);
        	
        	bDay0.setClickable(true);
        	bDay1.setClickable(true);
        	bDay2.setClickable(true);
        	bDay3.setClickable(true);
        	bDay4.setClickable(true);
        	bDay5.setClickable(true);
        	bDay6.setClickable(false);
        	break;
		}
		
		bDay0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	bDay0.setBackgroundResource(R.drawable.weekday_segmented_selected_left);
            	bDay1.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay2.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay3.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay4.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay5.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay6.setBackgroundResource(R.drawable.weekday_segmented_button_right);
            	
            	bDay0.setClickable(false);
            	bDay1.setClickable(true);
            	bDay2.setClickable(true);
            	bDay3.setClickable(true);
            	bDay4.setClickable(true);
            	bDay5.setClickable(true);
            	bDay6.setClickable(true);
            	
            	daySelected = 7;
            	//mPullRefreshListView.setRefreshing();
            	reloadView();
            }
        }); 
		
		bDay1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	bDay0.setBackgroundResource(R.drawable.weekday_segmented_button_left);
            	bDay1.setBackgroundResource(R.drawable.weekday_segmented_selected_center);
            	bDay2.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay3.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay4.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay5.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay6.setBackgroundResource(R.drawable.weekday_segmented_button_right);
            	
            	bDay0.setClickable(true);
            	bDay1.setClickable(false);
            	bDay2.setClickable(true);
            	bDay3.setClickable(true);
            	bDay4.setClickable(true);
            	bDay5.setClickable(true);
            	bDay6.setClickable(true);
            	
            	daySelected = 1;
            	//mPullRefreshListView.setRefreshing();
            	reloadView();
            }
        }); 
		
		bDay2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	bDay0.setBackgroundResource(R.drawable.weekday_segmented_button_left);
            	bDay1.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay2.setBackgroundResource(R.drawable.weekday_segmented_selected_center);
            	bDay3.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay4.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay5.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay6.setBackgroundResource(R.drawable.weekday_segmented_button_right);
            	
            	bDay0.setClickable(true);
            	bDay1.setClickable(true);
            	bDay2.setClickable(false);
            	bDay3.setClickable(true);
            	bDay4.setClickable(true);
            	bDay5.setClickable(true);
            	bDay6.setClickable(true);
            	
            	daySelected = 2;
            	//mPullRefreshListView.setRefreshing();
            	reloadView();
            }
        }); 
		
		bDay3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	bDay0.setBackgroundResource(R.drawable.weekday_segmented_button_left);
            	bDay1.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay2.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay3.setBackgroundResource(R.drawable.weekday_segmented_selected_center);
            	bDay4.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay5.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay6.setBackgroundResource(R.drawable.weekday_segmented_button_right);
            	
            	bDay0.setClickable(true);
            	bDay1.setClickable(true);
            	bDay2.setClickable(true);
            	bDay3.setClickable(false);
            	bDay4.setClickable(true);
            	bDay5.setClickable(true);
            	bDay6.setClickable(true);
            	
            	daySelected = 3;
            	//mPullRefreshListView.setRefreshing();
            	reloadView();
            }
        }); 
		
		bDay4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	bDay0.setBackgroundResource(R.drawable.weekday_segmented_button_left);
            	bDay1.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay2.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay3.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay4.setBackgroundResource(R.drawable.weekday_segmented_selected_center);
            	bDay5.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay6.setBackgroundResource(R.drawable.weekday_segmented_button_right);
            	
            	bDay0.setClickable(true);
            	bDay1.setClickable(true);
            	bDay2.setClickable(true);
            	bDay3.setClickable(true);
            	bDay4.setClickable(false);
            	bDay5.setClickable(true);
            	bDay6.setClickable(true);
            	
            	daySelected = 4;
            	//mPullRefreshListView.setRefreshing();
            	reloadView();
            }
        }); 
		
		bDay5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	bDay0.setBackgroundResource(R.drawable.weekday_segmented_button_left);
            	bDay1.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay2.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay3.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay4.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay5.setBackgroundResource(R.drawable.weekday_segmented_selected_center);
            	bDay6.setBackgroundResource(R.drawable.weekday_segmented_button_right);
            	
            	bDay0.setClickable(true);
            	bDay1.setClickable(true);
            	bDay2.setClickable(true);
            	bDay3.setClickable(true);
            	bDay4.setClickable(true);
            	bDay5.setClickable(false);
            	bDay6.setClickable(true);
            	
            	daySelected = 5;
            	//mPullRefreshListView.setRefreshing();
            	reloadView();
            }
        }); 
		
		bDay6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	bDay0.setBackgroundResource(R.drawable.weekday_segmented_button_left);
            	bDay1.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay2.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay3.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay4.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay5.setBackgroundResource(R.drawable.weekday_segmented_button_center);
            	bDay6.setBackgroundResource(R.drawable.weekday_segmented_selected_right);
            	
            	bDay0.setClickable(true);
            	bDay1.setClickable(true);
            	bDay2.setClickable(true);
            	bDay3.setClickable(true);
            	bDay4.setClickable(true);
            	bDay5.setClickable(true);
            	bDay6.setClickable(false);
            	
            	daySelected = 6;
            	//mPullRefreshListView.setRefreshing();
            	reloadView();
            }
        }); 
		
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.lvSchedule);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
				reloadView();
			}
		});
		mPullRefreshListView.setRefreshing();
		
		listView = mPullRefreshListView.getRefreshableView();
		adapter = new ScheduleArrayAdapter(Schedule.this, listShow);
		listView.setAdapter(adapter);
		
		TaskQueueImage.addTask(new updateListThread(), Schedule.this);
		
		rlEmptySchedule = (RelativeLayout) findViewById(R.id.rlEmptySchedule);
		tvNoClass = (TextView) findViewById(R.id.tvNoClass);
		tvNoClass.setTypeface(Fonts.getOpenSansBold(Schedule.this));
		
		tvAddClass = (TextView) findViewById(R.id.tvAddClass);
		tvAddClass.setTypeface(Fonts.getOpenSansBold(Schedule.this));
		tvAddClass.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Bundle extras = new Bundle();
				extras.putInt("daySelected", daySelected);
				Intent i = new Intent(Schedule.this, SearchCourse.class);
				i.putExtras(extras);
				startActivity(i);
			}
		}); 
	}
	
	public synchronized void reloadView() {
        // Do work to refresh the list here.
		mHandler.post(new Runnable() {
    		public void run() {
    			listShow.clear();
		    	
		    	if (listShow.isEmpty()){
		    		TaskQueueImage.addTask(new reloadViewThread(), Schedule.this);
				}
		    }
		});
    }
	
	class reloadViewThread extends Thread {
	    // This method is called when the thread runs
		List<ScheduleModel> listTemp = new ArrayList<ScheduleModel>();
		
	    public void run() {
	    	for (int i = 0; i < list.size(); i++){
    			if (list.get(i).day_of_week == daySelected){
    				listTemp.add(list.get(i));
    			}
    		}
	    	
	    	mHandler.post(new Runnable() {
        		public void run() {
        			if(listTemp.isEmpty() && user_id == 0){
        				rlEmptySchedule.setVisibility(View.VISIBLE);
        				switch(daySelected){
        				case 7:
        					tvNoClass.setText(getString(R.string.You_have_no_class_on_) + getString(R.string.Sunday_));
        					break;
        				case 1:
        					tvNoClass.setText(getString(R.string.You_have_no_class_on_) + getString(R.string.Monday_));
        					break;
        				case 2:
        					tvNoClass.setText(getString(R.string.You_have_no_class_on_) + getString(R.string.Tuesday_));
        					break;
        				case 3:
        					tvNoClass.setText(getString(R.string.You_have_no_class_on_) + getString(R.string.Wednesday_));
        					break;
        				case 4:
        					tvNoClass.setText(getString(R.string.You_have_no_class_on_) + getString(R.string.Thursday_));
        					break;
        				case 5:
        					tvNoClass.setText(getString(R.string.You_have_no_class_on_) + getString(R.string.Friday_));
        					break;
        				case 6:
        					tvNoClass.setText(getString(R.string.You_have_no_class_on_) + getString(R.string.Saturday_));
        					break;
        				}
        			} else {
        				rlEmptySchedule.setVisibility(View.GONE);
        			}
        			
        			listView.setVisibility(View.GONE);
        			listShow.addAll(listTemp);
        	    	listView.invalidateViews();
        	    	adapter.notifyDataSetChanged();
        	    	listView.setVisibility(View.VISIBLE);
        	    	
        	    	mPullRefreshListView.onRefreshComplete();
        	    	
        	    	listTemp.clear();
        			listTemp = null;
        		}
			});
	    }
	}
	
	class updateListThread extends Thread {
	    // This method is called when the thread runs
		List<ScheduleModel> listTemp = new ArrayList<ScheduleModel>();
		
	    public void run() {
			RestClient result = null;
			try {
				if (user_id != 0){
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
						
						listTemp.add(new ScheduleModel(course_id, course_name, course_code, course_time_id, location, is_biweekly, day_of_week, start_time, end_time));
					}
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mHandler.post(new Runnable() {
        		public void run() {
        			listView.setVisibility(View.GONE);
        	    	list.addAll(listTemp);
        	    	listView.invalidateViews();
        	    	adapter.notifyDataSetChanged();
        	    	listView.setVisibility(View.VISIBLE);
        	    	
        	    	mPullRefreshListView.onRefreshComplete();
        	    	
        	    	listTemp.clear();
        			listTemp = null;
        			
        			reloadView();
        		}
			});
	    }
	}
	
	public class ScheduleArrayAdapter extends ArrayAdapter<ScheduleModel> {
		private final List<ScheduleModel> list;
		
		class ViewHolder {
			public RelativeLayout bg;
			public ImageView ivTimebg, ivIcon, ivBg;
			public TextView tvStart, tvStop, tvCode;
		}

		public ScheduleArrayAdapter(Context context, List<ScheduleModel> list) {
			super(context, R.layout.schedulerow, list);
			this.list = list;
		}
		
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null){
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.schedulerow, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.bg = (RelativeLayout) rowView.findViewById(R.id.bg);
				viewHolder.ivBg = (ImageView) rowView.findViewById(R.id.ivBg);
				viewHolder.ivTimebg = (ImageView) rowView.findViewById(R.id.ivTimebg);
				viewHolder.ivIcon = (ImageView) rowView.findViewById(R.id.ivIcon);
				viewHolder.tvStart = (TextView) rowView.findViewById(R.id.tvStart);
				viewHolder.tvStop = (TextView) rowView.findViewById(R.id.tvStop);
				viewHolder.tvCode = (TextView) rowView.findViewById(R.id.tvCode);
				
				rowView.setTag(viewHolder);
			}
			
			ViewHolder holder = (ViewHolder) rowView.getTag();
			
			holder.ivBg.setBackgroundDrawable(null);
			holder.ivTimebg.setBackgroundDrawable(null);
			holder.ivIcon.setBackgroundDrawable(null);
			holder.tvStart.setText("");	
			holder.tvStop.setText("");	
			holder.tvCode.setText("");	
			
			if (position == 0 && list.size() == 1){
				holder.ivBg.setBackgroundResource(R.drawable.edit_row);
			} else if (position == 0){
				holder.ivBg.setBackgroundResource(R.drawable.edit_row_top);
			} else if (position < list.size()-1 && position > 0){
				holder.ivBg.setBackgroundResource(R.drawable.edit_row_middle);
			} else if (position == list.size()-1){
				holder.ivBg.setBackgroundResource(R.drawable.edit_row_bottom);
			}
			
			if (TimeCounter.checkClassStartTimeDay(list.get(position).start_time)){
				holder.ivTimebg.setBackgroundResource(R.drawable.schedule_day_bg);
				holder.ivIcon.setBackgroundResource(R.drawable.schedule_day_icon);
			} else {
				holder.ivTimebg.setBackgroundResource(R.drawable.schedule_night_bg);
				holder.ivIcon.setBackgroundResource(R.drawable.schedule_night_icon);
			}
			holder.tvStart.setText(TimeCounter.getClassTime(list.get(position).start_time));	
			holder.tvStop.setText(TimeCounter.getClassTime(list.get(position).end_time));	
			holder.tvCode.setText(list.get(position).course_code);	
			
			holder.bg.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					if (user_id == 0){
						Bundle extras = new Bundle();
						extras.putInt("course_id", list.get(position).course_id);
						extras.putInt("course_time_id", list.get(position).course_time_id);
						extras.putInt("daySelected", daySelected);
						extras.putString("course_code", list.get(position).course_code);
						Intent i = new Intent(Schedule.this, Course.class);
						i.putExtras(extras);
						startActivity(i);
					}
				}
		    }); 
			
			return rowView;
		}
		
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		backPressed = true;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if (backPressed){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mHandler.post(new Runnable() {
	    		public void run() {
	    			list.clear();
			    	
			    	if (list.isEmpty()){
			    		TaskQueueImage.addTask(new updateListThread(), Schedule.this);
					}
			    }
			});
		}
	}

}
