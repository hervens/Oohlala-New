package profile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.Schedule.reloadViewThread;
import profile.Schedule.updateListThread;

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
import events.AddEventInterface;
import events.SocialSchedule;

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

public class Course extends Activity implements AddCourseInterface {
	
	TextView bDay0, bDay1, bDay2, bDay3, bDay4, bDay5, bDay6, tvCourseCode, tvDay0, tvDay1, tvDay2, tvDay3, tvDay4, tvDay5, tvDay6, tvAddClass;
	
	ListView listView;
	List<ScheduleModel> list = new ArrayList<ScheduleModel>();
	List<ScheduleModel> listShow = new ArrayList<ScheduleModel>();
	ScheduleArrayAdapter adapter;
	
	Handler mHandler = new Handler();
	
	int daySelected = 1;
	
	int course_id, course_time_id;
	String course_name;
	String course_code;
	int[] days = new int[7];
	int days_num = 0;
	
	RelativeLayout rlShareClass, rlAddClass;
	
	boolean backPressed = false;
	boolean addCourseOnBackPressed = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course);
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		}); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(Course.this));
		
		Bundle b = this.getIntent().getExtras();
		course_id = b.getInt("course_id");
		course_time_id = b.getInt("course_time_id");
		daySelected = b.getInt("daySelected");
		course_code = b.getString("course_code");
		
		header.setText(course_code.toUpperCase());
		
		tvCourseCode = (TextView) findViewById(R.id.tvCourseCode);
		tvCourseCode.setText(course_code);
		
		tvAddClass = (TextView) findViewById(R.id.tvAddClass);
		
		tvDay0 = (TextView) findViewById(R.id.tvDay0);
		tvDay1 = (TextView) findViewById(R.id.tvDay1);
		tvDay2 = (TextView) findViewById(R.id.tvDay2);
		tvDay3 = (TextView) findViewById(R.id.tvDay3);
		tvDay4 = (TextView) findViewById(R.id.tvDay4);
		tvDay5 = (TextView) findViewById(R.id.tvDay5);
		tvDay6 = (TextView) findViewById(R.id.tvDay6);
		
		bDay0 = (TextView) findViewById(R.id.bDayBg0);
		bDay1 = (TextView) findViewById(R.id.bDayBg1);
		bDay2 = (TextView) findViewById(R.id.bDayBg2);
		bDay3 = (TextView) findViewById(R.id.bDayBg3);
		bDay4 = (TextView) findViewById(R.id.bDayBg4);
		bDay5 = (TextView) findViewById(R.id.bDayBg5);
		bDay6 = (TextView) findViewById(R.id.bDayBg6);
		
		/*
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.DAY_OF_WEEK) == 1){
			daySelected = 7;
		} else {
			daySelected = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		}
		Log.i("daySelected--------------------", String.valueOf(daySelected));
		*/
		
		switch(daySelected){
		case 7:
			bDay0.setBackgroundResource(R.drawable.weekday_selected);
        	bDay1.setBackgroundDrawable(null);
        	bDay2.setBackgroundDrawable(null);
        	bDay3.setBackgroundDrawable(null);
        	bDay4.setBackgroundDrawable(null);
        	bDay5.setBackgroundDrawable(null);
        	bDay6.setBackgroundDrawable(null);
        	
        	bDay0.setClickable(false);
        	bDay1.setClickable(true);
        	bDay2.setClickable(true);
        	bDay3.setClickable(true);
        	bDay4.setClickable(true);
        	bDay5.setClickable(true);
        	bDay6.setClickable(true);
        	
        	tvAddClass.setText(getString(R.string.Add_class_for_) + getString(R.string.Sunday_));
			break;
		case 1:
			bDay0.setBackgroundDrawable(null);
        	bDay1.setBackgroundResource(R.drawable.weekday_selected);
        	bDay2.setBackgroundDrawable(null);
        	bDay3.setBackgroundDrawable(null);
        	bDay4.setBackgroundDrawable(null);
        	bDay5.setBackgroundDrawable(null);
        	bDay6.setBackgroundDrawable(null);
        	
        	bDay0.setClickable(true);
        	bDay1.setClickable(false);
        	bDay2.setClickable(true);
        	bDay3.setClickable(true);
        	bDay4.setClickable(true);
        	bDay5.setClickable(true);
        	bDay6.setClickable(true);
        	
        	tvAddClass.setText(getString(R.string.Add_class_for_) + getString(R.string.Monday_));
			break;
		case 2:
			bDay0.setBackgroundDrawable(null);
        	bDay1.setBackgroundDrawable(null);
        	bDay2.setBackgroundResource(R.drawable.weekday_selected);
        	bDay3.setBackgroundDrawable(null);
        	bDay4.setBackgroundDrawable(null);
        	bDay5.setBackgroundDrawable(null);
        	bDay6.setBackgroundDrawable(null);
        	
        	bDay0.setClickable(true);
        	bDay1.setClickable(true);
        	bDay2.setClickable(false);
        	bDay3.setClickable(true);
        	bDay4.setClickable(true);
        	bDay5.setClickable(true);
        	bDay6.setClickable(true);
        	
        	tvAddClass.setText(getString(R.string.Add_class_for_) + getString(R.string.Tuesday_));
			break;
		case 3:
			bDay0.setBackgroundDrawable(null);
        	bDay1.setBackgroundDrawable(null);
        	bDay2.setBackgroundDrawable(null);
        	bDay3.setBackgroundResource(R.drawable.weekday_selected);
        	bDay4.setBackgroundDrawable(null);
        	bDay5.setBackgroundDrawable(null);
        	bDay6.setBackgroundDrawable(null);
        	
        	bDay0.setClickable(true);
        	bDay1.setClickable(true);
        	bDay2.setClickable(true);
        	bDay3.setClickable(false);
        	bDay4.setClickable(true);
        	bDay5.setClickable(true);
        	bDay6.setClickable(true);
        	
        	tvAddClass.setText(getString(R.string.Add_class_for_) + getString(R.string.Wednesday_));
			break;
		case 4:
			bDay0.setBackgroundDrawable(null);
        	bDay1.setBackgroundDrawable(null);
        	bDay2.setBackgroundDrawable(null);
        	bDay3.setBackgroundDrawable(null);
        	bDay4.setBackgroundResource(R.drawable.weekday_selected);
        	bDay5.setBackgroundDrawable(null);
        	bDay6.setBackgroundDrawable(null);
        	
        	bDay0.setClickable(true);
        	bDay1.setClickable(true);
        	bDay2.setClickable(true);
        	bDay3.setClickable(true);
        	bDay4.setClickable(false);
        	bDay5.setClickable(true);
        	bDay6.setClickable(true);
        	
        	tvAddClass.setText(getString(R.string.Add_class_for_) + getString(R.string.Thursday_));
			break;
		case 5:
			bDay0.setBackgroundDrawable(null);
        	bDay1.setBackgroundDrawable(null);
        	bDay2.setBackgroundDrawable(null);
        	bDay3.setBackgroundDrawable(null);
        	bDay4.setBackgroundDrawable(null);
        	bDay5.setBackgroundResource(R.drawable.weekday_selected);
        	bDay6.setBackgroundDrawable(null);
        	
        	bDay0.setClickable(true);
        	bDay1.setClickable(true);
        	bDay2.setClickable(true);
        	bDay3.setClickable(true);
        	bDay4.setClickable(true);
        	bDay5.setClickable(false);
        	bDay6.setClickable(true);
        	
        	tvAddClass.setText(getString(R.string.Add_class_for_) + getString(R.string.Friday_));
			break;
		case 6:
			bDay0.setBackgroundDrawable(null);
        	bDay1.setBackgroundDrawable(null);
        	bDay2.setBackgroundDrawable(null);
        	bDay3.setBackgroundDrawable(null);
        	bDay4.setBackgroundDrawable(null);
        	bDay5.setBackgroundDrawable(null);
        	bDay6.setBackgroundResource(R.drawable.weekday_selected);
        	
        	bDay0.setClickable(true);
        	bDay1.setClickable(true);
        	bDay2.setClickable(true);
        	bDay3.setClickable(true);
        	bDay4.setClickable(true);
        	bDay5.setClickable(true);
        	bDay6.setClickable(false);
        	
        	tvAddClass.setText(getString(R.string.Add_class_for_) + getString(R.string.Saturday_));
			break;
		}
    	
    	rlAddClass = (RelativeLayout) findViewById(R.id.rlAddClass);
    	rlAddClass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	AddCourse.act2 = Course.this;
            	
            	Bundle extras = new Bundle();
				extras.putInt("course_id", course_id);
				extras.putInt("daySelected", daySelected);
				extras.putString("course_code", course_code);
				Intent i = new Intent(Course.this, AddCourse.class);
				i.putExtras(extras);
				startActivity(i);
            }
    	});
    	
		bDay0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	bDay0.setBackgroundResource(R.drawable.weekday_selected);
            	bDay1.setBackgroundDrawable(null);
            	bDay2.setBackgroundDrawable(null);
            	bDay3.setBackgroundDrawable(null);
            	bDay4.setBackgroundDrawable(null);
            	bDay5.setBackgroundDrawable(null);
            	bDay6.setBackgroundDrawable(null);
            	
            	bDay0.setClickable(false);
            	bDay1.setClickable(true);
            	bDay2.setClickable(true);
            	bDay3.setClickable(true);
            	bDay4.setClickable(true);
            	bDay5.setClickable(true);
            	bDay6.setClickable(true);
            	
            	tvAddClass.setText(getString(R.string.Add_class_for_) + getString(R.string.Sunday_));
            	
            	daySelected = 7;
            	reloadView();
            }
        }); 
		
		bDay1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	bDay0.setBackgroundDrawable(null);
            	bDay1.setBackgroundResource(R.drawable.weekday_selected);
            	bDay2.setBackgroundDrawable(null);
            	bDay3.setBackgroundDrawable(null);
            	bDay4.setBackgroundDrawable(null);
            	bDay5.setBackgroundDrawable(null);
            	bDay6.setBackgroundDrawable(null);
            	
            	bDay0.setClickable(true);
            	bDay1.setClickable(false);
            	bDay2.setClickable(true);
            	bDay3.setClickable(true);
            	bDay4.setClickable(true);
            	bDay5.setClickable(true);
            	bDay6.setClickable(true);
            	
            	tvAddClass.setText(getString(R.string.Add_class_for_) + getString(R.string.Monday_));
            	
            	daySelected = 1;
            	reloadView();
            }
        }); 
		
		bDay2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	bDay0.setBackgroundDrawable(null);
            	bDay1.setBackgroundDrawable(null);
            	bDay2.setBackgroundResource(R.drawable.weekday_selected);
            	bDay3.setBackgroundDrawable(null);
            	bDay4.setBackgroundDrawable(null);
            	bDay5.setBackgroundDrawable(null);
            	bDay6.setBackgroundDrawable(null);
            	
            	bDay0.setClickable(true);
            	bDay1.setClickable(true);
            	bDay2.setClickable(false);
            	bDay3.setClickable(true);
            	bDay4.setClickable(true);
            	bDay5.setClickable(true);
            	bDay6.setClickable(true);
            	
            	tvAddClass.setText(getString(R.string.Add_class_for_) + getString(R.string.Tuesday_));
            	
            	daySelected = 2;
            	reloadView();
            }
        }); 
		
		bDay3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	bDay0.setBackgroundDrawable(null);
            	bDay1.setBackgroundDrawable(null);
            	bDay2.setBackgroundDrawable(null);
            	bDay3.setBackgroundResource(R.drawable.weekday_selected);
            	bDay4.setBackgroundDrawable(null);
            	bDay5.setBackgroundDrawable(null);
            	bDay6.setBackgroundDrawable(null);
            	
            	bDay0.setClickable(true);
            	bDay1.setClickable(true);
            	bDay2.setClickable(true);
            	bDay3.setClickable(false);
            	bDay4.setClickable(true);
            	bDay5.setClickable(true);
            	bDay6.setClickable(true);
            	
            	tvAddClass.setText(getString(R.string.Add_class_for_) + getString(R.string.Wednesday_));
            	
            	daySelected = 3;
            	reloadView();
            }
        }); 
		
		bDay4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	bDay0.setBackgroundDrawable(null);
            	bDay1.setBackgroundDrawable(null);
            	bDay2.setBackgroundDrawable(null);
            	bDay3.setBackgroundDrawable(null);
            	bDay4.setBackgroundResource(R.drawable.weekday_selected);
            	bDay5.setBackgroundDrawable(null);
            	bDay6.setBackgroundDrawable(null);
            	
            	bDay0.setClickable(true);
            	bDay1.setClickable(true);
            	bDay2.setClickable(true);
            	bDay3.setClickable(true);
            	bDay4.setClickable(false);
            	bDay5.setClickable(true);
            	bDay6.setClickable(true);
            	
            	tvAddClass.setText(getString(R.string.Add_class_for_) + getString(R.string.Thursday_));
            	
            	daySelected = 4;
            	reloadView();
            }
        }); 
		
		bDay5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	bDay0.setBackgroundDrawable(null);
            	bDay1.setBackgroundDrawable(null);
            	bDay2.setBackgroundDrawable(null);
            	bDay3.setBackgroundDrawable(null);
            	bDay4.setBackgroundDrawable(null);
            	bDay5.setBackgroundResource(R.drawable.weekday_selected);
            	bDay6.setBackgroundDrawable(null);
            	
            	bDay0.setClickable(true);
            	bDay1.setClickable(true);
            	bDay2.setClickable(true);
            	bDay3.setClickable(true);
            	bDay4.setClickable(true);
            	bDay5.setClickable(false);
            	bDay6.setClickable(true);
            	
            	tvAddClass.setText(getString(R.string.Add_class_for_) + getString(R.string.Friday_));
            	
            	daySelected = 5;
            	reloadView();
            }
        }); 
		
		bDay6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	bDay0.setBackgroundDrawable(null);
            	bDay1.setBackgroundDrawable(null);
            	bDay2.setBackgroundDrawable(null);
            	bDay3.setBackgroundDrawable(null);
            	bDay4.setBackgroundDrawable(null);
            	bDay5.setBackgroundDrawable(null);
            	bDay6.setBackgroundResource(R.drawable.weekday_selected);
            	
            	bDay0.setClickable(true);
            	bDay1.setClickable(true);
            	bDay2.setClickable(true);
            	bDay3.setClickable(true);
            	bDay4.setClickable(true);
            	bDay5.setClickable(true);
            	bDay6.setClickable(false);
            	
            	tvAddClass.setText(getString(R.string.Add_class_for_) + getString(R.string.Saturday_));
            	
            	daySelected = 6;
            	reloadView();
            }
        }); 
		
		listView = (ListView) findViewById(R.id.lvCourse);
		adapter = new ScheduleArrayAdapter(Course.this, listShow);
		listView.setAdapter(adapter);
		
		rlShareClass = (RelativeLayout) findViewById(R.id.rlShareClass);
		rlShareClass.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Course.this, CampusWallInviteFacebook.class);
				startActivity(i);
			}
		}); 
		
		new updateListThread().execute();
	}
	
	public synchronized void reloadView() {
        // Do work to refresh the list here.
		mHandler.post(new Runnable() {
    		public void run() {
    			listShow.clear();
		    	
		    	if (listShow.isEmpty()){
		    		TaskQueueImage.addTask(new reloadViewThread(), Course.this);
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
        			listView.setVisibility(View.GONE);
        			listShow.addAll(listTemp);
        	    	listView.invalidateViews();
        	    	adapter.notifyDataSetChanged();
        	    	listView.setVisibility(View.VISIBLE);
        	    	
        	    	listTemp.clear();
        			listTemp = null;
        		}
			});
	    }
	}
	
	class updateListThread extends AsyncTask<Void, Void, List<ScheduleModel>> {
	    // This method is called when the thread runs
		List<ScheduleModel> listTemp = null;
		
		protected List<ScheduleModel> doInBackground(Void... nothing) {
	    	//get the all the current games
			listTemp = new ArrayList<ScheduleModel>();

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
			Log.i("my courses", result.getResponse());
			
			try {
				JSONArray threads = new JSONArray(result.getResponse());
				Log.i("school courses: ", threads.toString());
				
				for (int i = 0; i < threads.length(); i++){
					if (course_id == threads.getJSONObject(i).getInt("id")){
						JSONArray time_info = threads.getJSONObject(i).getJSONArray("time_info");
						for (int j = 0; j < time_info.length(); j++){
							int course_time_id = time_info.getJSONObject(j).getInt("id");
							String location = time_info.getJSONObject(j).getString("location");
							boolean is_biweekly = time_info.getJSONObject(j).getBoolean("is_biweekly");
							int day_of_week = time_info.getJSONObject(j).getInt("day_of_week");
							int start_time = time_info.getJSONObject(j).getInt("start_time");
							int end_time = time_info.getJSONObject(j).getInt("end_time");
							
							listTemp.add(new ScheduleModel(course_id, course_name, course_code, course_time_id, location, is_biweekly, day_of_week, start_time, end_time));
							
							if (!checkDaysExist(day_of_week)){
								days[days_num] = day_of_week;
								days_num ++;
							}
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return listTemp;
	    }

		protected void onPostExecute(List<ScheduleModel> result) {
	    	listView.setVisibility(View.GONE);
	    	list.addAll(result);
	    	listView.invalidateViews();
	    	adapter.notifyDataSetChanged();
	    	listView.setVisibility(View.VISIBLE);
	    	
	    	listTemp.clear();
			listTemp = null;
			
			reloadView();
			
			for (int i = 0; i < days_num;  i++){
				if (days[i] == 7){
					tvDay0.setBackgroundResource(R.drawable.weekday_toggled);
					tvDay0.setTextColor(getResources().getColorStateList(R.color.white1));
				} else if (days[i] == 1){
					tvDay1.setBackgroundResource(R.drawable.weekday_toggled);
					tvDay1.setTextColor(getResources().getColorStateList(R.color.white1));
				} else if (days[i] == 2){
					tvDay2.setBackgroundResource(R.drawable.weekday_toggled);
					tvDay2.setTextColor(getResources().getColorStateList(R.color.white1));
				} else if (days[i] == 3){
					tvDay3.setBackgroundResource(R.drawable.weekday_toggled);
					tvDay3.setTextColor(getResources().getColorStateList(R.color.white1));
				} else if (days[i] == 4){
					tvDay4.setBackgroundResource(R.drawable.weekday_toggled);
					tvDay4.setTextColor(getResources().getColorStateList(R.color.white1));
				} else if (days[i] == 5){
					tvDay5.setBackgroundResource(R.drawable.weekday_toggled);
					tvDay5.setTextColor(getResources().getColorStateList(R.color.white1));
				} else if (days[i] == 6){
					tvDay6.setBackgroundResource(R.drawable.weekday_toggled);
					tvDay6.setTextColor(getResources().getColorStateList(R.color.white1));
				} 
			}
	    }

	}
	
	public boolean checkDaysExist(int day_of_week){
		for (int i = 0; i < days_num;  i++){
			if (days[i] == day_of_week){
				return true;
			}
		}
		
		return false;
	}
	
	public class ScheduleArrayAdapter extends ArrayAdapter<ScheduleModel> {
		private final List<ScheduleModel> list;
		
		class ViewHolder {
			public TextView tvStart, tvStop;
			public RelativeLayout bg;
		}

		public ScheduleArrayAdapter(Context context, List<ScheduleModel> list) {
			super(context, R.layout.courserow, list);
			this.list = list;
		}
		
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null){
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.courserow, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.tvStart = (TextView) rowView.findViewById(R.id.tvStart);
				viewHolder.tvStop = (TextView) rowView.findViewById(R.id.tvStop);
				viewHolder.bg = (RelativeLayout) rowView.findViewById(R.id.bg);
				
				rowView.setTag(viewHolder);
			}
			
			ViewHolder holder = (ViewHolder) rowView.getTag();
			
			holder.tvStart.setText("");	
			holder.tvStop.setText("");	
			
			holder.tvStart.setText(TimeCounter.getClassTime(list.get(position).start_time));	
			holder.tvStop.setText(TimeCounter.getClassTime(list.get(position).end_time));	
			
			holder.bg.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	Bundle extras = new Bundle();
					extras.putInt("course_id", course_id);
					extras.putInt("course_time_id", list.get(position).course_time_id);
					extras.putInt("start_time", list.get(position).start_time);
					extras.putInt("end_time", list.get(position).end_time);
					extras.putInt("daySelected", daySelected);
					extras.putString("course_code", course_code);
					extras.putString("location", list.get(position).location);
					Intent i = new Intent(Course.this, AddCourse.class);
					i.putExtras(extras);
					startActivity(i);
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
		
		if (addCourseOnBackPressed){
			onBackPressed();
		}
		
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
			    		new updateListThread().execute();
					}
			    }
			});
		}
	}

	@Override
	public void addCourseOnBackPressed() {
		// TODO Auto-generated method stub
		addCourseOnBackPressed = true;
	}

}
