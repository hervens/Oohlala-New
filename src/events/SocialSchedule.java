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

import launchOohlala.CheckEmail;
import launchOohlala.InAppTour;

import network.ErrorCodeParser;

import org.json.JSONArray;
import org.json.JSONException;

import profile.AddCourse;
import profile.Course;
import profile.Friends;
import profile.ProfileSettings;
import profile.SMSInvite;
import profile.Schedule;
import profile.SearchCourse;
import profile.UpdateStatus;
import profile.UserProfile;

import rewards.ScheduleModel;
import user.Profile;

import com.facebook.Session;
import com.facebook.SessionState;
import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.TopMenuNavbar;
import com.gotoohlala.ScrollerContainer.OnSlideListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import datastorage.CacheInternalStorage;
import datastorage.ConvertDpsToPixels;
import datastorage.Database;
import datastorage.DeviceDimensions;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import datastorage.UserFirstLogin;
import datastorage.Rest.request;

import ManageThreads.TaskQueueImage;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import events.MonthAdapter;
import events.EventsEvents.EventsEventsArrayAdapter;
import events.EventsEvents.TimeComparator;
import events.EventsEvents.getEventThread;
import events.EventsEvents.EventsEventsArrayAdapter.ViewHolder;

public class SocialSchedule extends FrameLayout implements AddEventInterface {

	boolean currentView = false;
	
	View v = null;
	
	private ImageView calendarToJournalButton;
	private TextView tvDayMonthYear;
	private Button currentMonth;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Calendar _calendar;
	private int month, year, daySelected;
	private final DateFormat dateFormatter = new DateFormat();
	private static final String dateTemplate = "MMM yyyy";
	
	SocialScheduleArrayAdapter listViewAdapter;
	ListView listView;
	PullToRefreshListView mPullRefreshListView;
	List<SocialScheduleModel> list_schedule = new ArrayList<SocialScheduleModel>();
	
	Handler mHandler = new Handler(Looper.getMainLooper());
	
	int dayOfWeek;
	int epoch_start_day;
	int epoch_end_day;
	
	ImageView ivFriendsThumb1, ivFriendsThumb2, ivFriendsThumb3, ivFriendsThumb4, ivEventsThumb1, ivEventsThumb2, ivEventsThumb3;
	RelativeLayout rlHeader, rlDayPanel2, rlDayPanel3, rlInAppTour;
	
	public final String[] weekdays_full = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	public final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	public final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	
	ImageView ivToggle;
	boolean monthToggle = false;
	LinearLayout llWeekDay;
	
	Button bToggleDay, bToggleMonth, bToday;
	TextView tvDate;
	
	boolean loadDots = true;
	boolean tellFriendsYouUpdatedSchedule = false;
	
	private OnSlideListener mOnSlideListener;  
	
	public TopMenuNavbar TopMenuNavbar;
	
	boolean NoScrollAnimation = false;
	
	public SocialSchedule(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		 final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
		 v = (RelativeLayout) mLayoutInflater.inflate(R.layout.socialschedule, null);  
	     addView(v);  
	     
			View headerView = View.inflate(getContext(), R.layout.socialscheduleheaderview, null);
			
			currentView = true;
			loadDots = true;
			
			TextView header = (TextView) v.findViewById(R.id.header);
			header.setTypeface(Fonts.getOpenSansBold(v.getContext()));
			
			TopMenuNavbar = (TopMenuNavbar) v.findViewById(R.id.bTopMenuNavBar);
			rlHeader = (RelativeLayout) v.findViewById(R.id.rlHeader);
			
			Button bAdd = (Button) v.findViewById(R.id.bAdd);
			bAdd.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v2) {
					final CharSequence[] items = {getContext().getString(R.string.Add_New_Event), getContext().getString(R.string.Add_New_Class_Course), getContext().getString(R.string.Cancel)};
					
					AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
					builder.setItems(items, new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int item) {
					    	switch(item){
					    	case 0:
					    		AddEvent.act = SocialSchedule.this;
					    		
					    		Bundle extras = new Bundle();
								extras.putString("title", "");
								extras.putInt("start_time", epoch_start_day + 32400);
								extras.putInt("end_time", epoch_start_day + 36000);
								extras.putString("location", "");
								extras.putString("start_time_text", "09:00 AM");
								extras.putString("end_time_text", "10:00 AM");
								extras.putInt("event_id", 0);
								Intent i = new Intent(v.getContext(), AddEvent.class);
								i.putExtras(extras);
								getContext().startActivity(i);
					    		break;
					    	case 1:
					    		AddCourse.act = SocialSchedule.this;
					    		
					    		Bundle extras2 = new Bundle();
								extras2.putInt("daySelected", dayOfWeek);
								Intent i2 = new Intent(v.getContext(), SearchCourse.class);
								i2.putExtras(extras2);
								getContext().startActivity(i2);
					    		break;
					    	case 2:
					    		break;
					    	}
					    }
					});
					AlertDialog alert = builder.create();
					alert.show();
				}
			}); 
			
			// get date
			_calendar = Calendar.getInstance(Locale.getDefault());
			month = _calendar.get(Calendar.MONTH) + 1;
			year = _calendar.get(Calendar.YEAR);
			daySelected = _calendar.get(Calendar.DAY_OF_MONTH);
			//Log.d(tag, "Calendar Instance:= " + "Month: " + month + " " + "Year: " + year);

			tvDayMonthYear = (TextView) headerView.findViewById(R.id.tvDayMonthYear);
			
			currentMonth = (Button) v.findViewById(R.id.currentMonth);
			//currentMonth.setText(dateFormatter.format(dateTemplate, _calendar.getTime()));

			prevMonth = (ImageView) v.findViewById(R.id.prevMonth);	
			
			RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(
					ConvertDpsToPixels.getPixels(25, v.getContext()),
        			ConvertDpsToPixels.getPixels(25, v.getContext()));
        	param1.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        	param1.setMargins(ConvertDpsToPixels.getPixels((int) (DeviceDimensions.getWidth(v.getContext())*0.16), v.getContext()), 0, 0, 0);
        	prevMonth.setLayoutParams(param1);
        	
			prevMonth.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					if (monthToggle){
						if (month <= 1){
							month = 12;
							year--;
						} else {
							month--;
						}
						
						daySelected = 1;
						loadDots = true;
						setGridCellAdapterToDate(month, year, daySelected);
						
						tvDate.setText(String.valueOf(daySelected));
						dayOfWeek = getIntDayOfWeek(daySelected);
						//check user events with epoch time
				    	//epoch_start_day = TimeCounter.getEpochTimeOnDate(daySelected, month - 1, year);
						epoch_end_day = TimeCounter.getEpochTimeOnDate(daySelected + 1, month - 1, year);
						
						tvDayMonthYear.setText(TimeCounter.getWeekdayMonthDayYear(epoch_start_day));
						
						listViewScrollTo(TimeCounter.getEpochTimeOnDate(daySelected, month - 1, year));
					} else {
						goToPrevDay();
					}
				}
			}); 

			nextMonth = (ImageView) v.findViewById(R.id.nextMonth);
			
			RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(
					ConvertDpsToPixels.getPixels(25, v.getContext()),
        			ConvertDpsToPixels.getPixels(25, v.getContext()));
        	param2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        	param2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        	param2.setMargins(0, 0, ConvertDpsToPixels.getPixels((int) (DeviceDimensions.getWidth(v.getContext())*0.16), v.getContext()), 0);
        	nextMonth.setLayoutParams(param2);
        	
			nextMonth.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					if (monthToggle){
						if (month > 11){
							month = 1;
							year++;
						} else {
							month++;
						}					
							
						daySelected = 1;
						loadDots = true;
						setGridCellAdapterToDate(month, year, daySelected);
						
						tvDate.setText(String.valueOf(daySelected));
						dayOfWeek = getIntDayOfWeek(daySelected);
						//check user events with epoch time
				    	//epoch_start_day = TimeCounter.getEpochTimeOnDate(daySelected, month - 1, year);
						epoch_end_day = TimeCounter.getEpochTimeOnDate(daySelected + 1, month - 1, year);
						
						tvDayMonthYear.setText(TimeCounter.getWeekdayMonthDayYear(epoch_start_day));
						
						listViewScrollTo(TimeCounter.getEpochTimeOnDate(daySelected, month - 1, year));
					} else {
						goToNextDay();
					}
				}
			}); 

			calendarView = (GridView) v.findViewById(R.id.calendar);

			// Initialised
			adapter = new GridCellAdapter(v.getContext(), R.id.calendar_day_gridcell, month, year);
			adapter.notifyDataSetChanged();
			calendarView.setAdapter(adapter);
			
			//-------------------------------- ListView ------------------------------------------
			mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.lvSocialschedule);
			mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
				public void onRefresh() {
					mPullRefreshListView.setLastUpdatedLabel(getContext().getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
						
					mPullRefreshListView.onRefreshComplete();
				}
			});
			mPullRefreshListView.setRefreshing();
				
			listView = mPullRefreshListView.getRefreshableView();	
			listView.addHeaderView(headerView);
			listViewAdapter = new SocialScheduleArrayAdapter(v.getContext(), list_schedule);
			listView.setAdapter(listViewAdapter);
			
			listView.setOnTouchListener( new OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_UP){
						NoScrollAnimation = false;
					}
					
					return false;
				}
		    } );
			
			listView.setOnScrollListener(new OnScrollListener(){
				
				public int first, last;
				
			    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			        // TODO Auto-generated method stub
			    	this.first = firstVisibleItem;
			    	this.last = firstVisibleItem + visibleItemCount - 1;
			    	
			    	if (first > 0){
			    		//bScrollToTop.setVisibility(View.VISIBLE);
			    	} else {
			    		//bScrollToTop.setVisibility(View.GONE);
			    	}
			    }

				@Override
				public void onScrollStateChanged(AbsListView arg0, int arg1) {
					// TODO Auto-generated method stub
					if (!NoScrollAnimation){
						goToThatDay(list_schedule.get(first).epoch_start_day, list_schedule.get(first).epoch_end_day, 
								list_schedule.get(first).daySelected, list_schedule.get(first).month, list_schedule.get(first).year);
					} 
					
					//Log.i("date", list_schedule.get(first).date);
				}
			});
			
			ivFriendsThumb1 = (ImageView) headerView.findViewById(R.id.ivFriendsThumb1);
			ivFriendsThumb2 = (ImageView) headerView.findViewById(R.id.ivFriendsThumb2);
			ivFriendsThumb3 = (ImageView) headerView.findViewById(R.id.ivFriendsThumb3);
			ivFriendsThumb4 = (ImageView) headerView.findViewById(R.id.ivFriendsThumb4);
			
			ivEventsThumb1 = (ImageView) headerView.findViewById(R.id.ivEventsThumb1);
			ivEventsThumb2 = (ImageView) headerView.findViewById(R.id.ivEventsThumb2);
			ivEventsThumb3 = (ImageView) headerView.findViewById(R.id.ivEventsThumb3);
			
			TaskQueueImage.addTask(new ScheduleFriends(), v.getContext());
			TaskQueueImage.addTask(new ScheduleEvents(), v.getContext());
			
			rlDayPanel2 = (RelativeLayout) headerView.findViewById(R.id.rlDayPanel2);
			rlDayPanel2.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v2) {
					Bundle extras = new Bundle();
					extras.putInt("user_id", Profile.userId);
					extras.putInt("dayOfWeek", dayOfWeek);
					extras.putInt("epoch_start_day", epoch_start_day);
					extras.putInt("epoch_end_day", epoch_end_day);
	            	Intent i = new Intent(getContext(), FriendsSchedules.class);
	            	i.putExtras(extras);
	            	getContext().startActivity(i);
				}
			});
			
			rlDayPanel3 = (RelativeLayout) headerView.findViewById(R.id.rlDayPanel3);
			rlDayPanel3.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v2) {
	            	Intent i = new Intent(getContext(), EventsEvents.class);
	            	getContext().startActivity(i);
				}
			});
			
			llWeekDay = (LinearLayout) v.findViewById(R.id.llWeekDay);
			ivToggle = (ImageView) v.findViewById(R.id.ivToggle);
			
			bToggleDay = (Button) v.findViewById(R.id.bToggleDay);
			bToggleDay.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v2) {
					ivToggle.setImageDrawable(getResources().getDrawable(R.drawable.button_day_toggle));
					calendarView.setVisibility(View.GONE);
					llWeekDay.setVisibility(View.GONE);
					
					monthToggle = false;
					
					currentMonth.setText(TimeCounter.getMonthAndDay(epoch_start_day));
					//tvDayMonthYear.setText(TimeCounter.getWeekdayMonthDayYear(epoch_start_day));
				}
			});
			
			bToggleMonth = (Button) v.findViewById(R.id.bToggleMonth);
			bToggleMonth.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v2) {
					ivToggle.setImageDrawable(getResources().getDrawable(R.drawable.button_month_toggle));
					calendarView.setVisibility(View.VISIBLE);
					llWeekDay.setVisibility(View.VISIBLE);
					
					monthToggle = true;
					
					currentMonth.setText(dateFormatter.format(dateTemplate, new Date(Long.parseLong(String.valueOf(epoch_start_day)) * 1000)));
					//tvDayMonthYear.setText(TimeCounter.getWeekdayMonthDayYear(epoch_start_day));
				}
			});
			
			bToday = (Button) v.findViewById(R.id.bToday);
			bToday.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v2) {
					_calendar = Calendar.getInstance(Locale.getDefault());
					month = _calendar.get(Calendar.MONTH) + 1;
					year = _calendar.get(Calendar.YEAR);
					daySelected = _calendar.get(Calendar.DAY_OF_MONTH);
					tvDate.setText(String.valueOf(daySelected));
					
					dayOfWeek = getIntDayOfWeek(daySelected);
				    int epoch_start_day = TimeCounter.getEpochTimeOnDate(daySelected, month - 1, year);
					epoch_end_day = TimeCounter.getEpochTimeOnDate(daySelected + 1, month - 1, year);
					
					tvDayMonthYear.setText(TimeCounter.getWeekdayMonthDayYear(epoch_start_day));
					
					if (monthToggle){
						currentMonth.setText(dateFormatter.format(dateTemplate, new Date(Long.parseLong(String.valueOf(epoch_start_day)) * 1000)));
					} else {
						currentMonth.setText(TimeCounter.getMonthAndDay(epoch_start_day));
					}
					
					loadDots = true;
					setGridCellAdapterToDate(month, year, daySelected);
					
					listViewScrollTo(epoch_start_day);
				}
			});
			
			tvDayMonthYear.setText(getDayOfWeek(_calendar.get(Calendar.DAY_OF_MONTH)) + "\n" + getMonthAsString(month - 1) + " " + _calendar.get(Calendar.DAY_OF_MONTH) + ", " + year);
				
			dayOfWeek = getIntDayOfWeek(daySelected);
			//check user events with epoch time
		    epoch_start_day = TimeCounter.getEpochTimeOnDate(daySelected, month - 1, year);
			epoch_end_day = TimeCounter.getEpochTimeOnDate(daySelected + 1, month - 1, year);
			
			currentMonth.setText(TimeCounter.getMonthAndDay(epoch_start_day));
			
			tvDate = (TextView) v.findViewById(R.id.tvDate);
			tvDate.setText(String.valueOf(daySelected));
			
			list_schedule.clear();
	    	if (list_schedule.isEmpty()){
	    		TaskQueueImage.addTask(new loadDatabaseThread(), v.getContext());
	    		TaskQueueImage.addTask(new SyncClassesThread(), v.getContext());
			}
	    	listViewScrollTo(epoch_start_day);
			
			rlInAppTour = (RelativeLayout) v.findViewById(R.id.rlInAppTour);
			rlInAppTour.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v2) {
					rlInAppTour.setVisibility(View.GONE);
				}
			});
			
			if (CacheInternalStorage.getCacheUserFirstLogin(v.getContext()) != null){
				if (CacheInternalStorage.getCacheUserFirstLogin(v.getContext()).userAccount != null){
					if (Profile.email != null){
						if (CacheInternalStorage.getCacheUserFirstLogin(v.getContext()).userAccount.contentEquals(Profile.email)){
							
							if (CacheInternalStorage.getCacheUserFirstLogin(v.getContext()).first_time_social_tab){
								CacheInternalStorage.cacheUserFirstLogin(new UserFirstLogin(Profile.email, false, false, true), v.getContext()); //cache user first login
								
								rlInAppTour.setVisibility(View.VISIBLE);
							}
						}
					}
				} else {
					CacheInternalStorage.cacheUserFirstLogin(new UserFirstLogin(Profile.email, false, false, true), v.getContext()); //cache user first login
					
					rlInAppTour.setVisibility(View.VISIBLE);
				}
			} else {
				CacheInternalStorage.cacheUserFirstLogin(new UserFirstLogin(Profile.email, false, false, true), v.getContext()); //cache user first login
				
				rlInAppTour.setVisibility(View.VISIBLE);
			}
    }
	
	public void goToPrevDay(){
		if (dayOfWeek > 1){
			dayOfWeek--;
		} else {
			dayOfWeek = 7;
		}
		int epoch_start_day = this.epoch_start_day - 86400;
		epoch_end_day -= 86400;
		
		currentMonth.setText(TimeCounter.getMonthAndDay(epoch_start_day));
		tvDayMonthYear.setText(TimeCounter.getWeekdayMonthDayYear(epoch_start_day));
		
		if (daySelected > 1){
			daySelected--;
		} else {
			if (month <= 1){
				month = 12;
				year--;
			} else {
				month--;
			}
			
			daySelected = daysOfMonth[month - 1];
			
			loadDots = true;
			setGridCellAdapterToDate(month, year, daySelected);
		}
		adapter.notifyDataSetChanged();
		tvDate.setText(String.valueOf(daySelected));
		
		listViewScrollTo(epoch_start_day);
	}
	
	public void goToNextDay(){
		if (dayOfWeek < 7){
			dayOfWeek++;
		} else {
			dayOfWeek = 1;
		}
		int epoch_start_day = this.epoch_start_day + 86400;
		epoch_end_day += 86400;
		
		currentMonth.setText(TimeCounter.getMonthAndDay(epoch_start_day));
		tvDayMonthYear.setText(TimeCounter.getWeekdayMonthDayYear(epoch_start_day));
		
		if (daySelected < daysOfMonth[month - 1]){
			daySelected++;
		} else {
			daySelected = 1;
			
			if (month > 11){
				month = 1;
				year++;
			} else {
				month++;
			}
				
			loadDots = true;
			setGridCellAdapterToDate(month, year, daySelected);
		}
		adapter.notifyDataSetChanged();
		tvDate.setText(String.valueOf(daySelected));
		
		listViewScrollTo(epoch_start_day);
	}
	
	public void goToThatDay(int epochstartday, int epochendday, int dayselected, int month2, int year2){
		epoch_start_day = epochstartday;
		epoch_end_day = epochendday;
		
		currentMonth.setText(TimeCounter.getMonthAndDay(epoch_start_day));
		tvDayMonthYear.setText(TimeCounter.getWeekdayMonthDayYear(epoch_start_day));
		
		daySelected = dayselected;
		adapter.notifyDataSetChanged();
		tvDate.setText(String.valueOf(daySelected));
		
		if (month != month2){
			loadDots = true;
			setGridCellAdapterToDate(month2, year, daySelected);
		}
		month = month2;
		
		if (year != year2){
			loadDots = true;
			setGridCellAdapterToDate(month, year2, daySelected);
		}
		year = year2;
	}
	
	/**
	 * Smooth scroll to which element in the listview
	 * @param epoch_start_day
	 */
	public void listViewScrollTo(int epoch_start_day){
		NoScrollAnimation = true;
		
		if (epoch_start_day <= this.epoch_start_day){
			for (int i = 0; i < list_schedule.size(); i++){
				if (list_schedule.get(i).epoch_start_day == epoch_start_day){
					//Log.i("back", "------------------");
					listView.smoothScrollToPosition(i+1);
					
					break;
				}
			}
		} else {
			for (int i = list_schedule.size() - 1; i > 0; i--){
				if (list_schedule.get(i).epoch_start_day == epoch_start_day){
					//Log.i("forward", "------------------");
					listView.smoothScrollToPosition(i+1);
					
					break;
				}
			}
		}
		
		this.epoch_start_day = epoch_start_day;
	}
	
	public void setOnSlideListener(OnSlideListener onSlideListener) {  
		mOnSlideListener = onSlideListener;  
	}  
    
    class ScheduleFriends extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	RestClient result = null;
			try {
				result = new request().execute(Rest.USER + "?friends_only=1", Rest.OSESS + Profile.sk, Rest.GET).get();
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
				
				int k = 0;
				for (int i = 0; i < threads.length(); i++){
					if (threads.getJSONObject(i).has("avatar_thumb_url")){
						friends[k] = threads.getJSONObject(i).getString("avatar_thumb_url");
					}
					
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
							ivFriendsThumb1.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(friends[0], v.getContext(), 30));
			        	}
			        	if (friends[1] != null){
			        		ivFriendsThumb2.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(friends[1], v.getContext(), 30));
			        	}
			        	if (friends[2] != null){
			        		ivFriendsThumb3.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(friends[2], v.getContext(), 30));
			        	}
			        	
			        	if (friends[0] == null && friends[1] == null && friends[2] == null){
			        		ivFriendsThumb4.setVisibility(View.VISIBLE);
			        	} else {
			        		ivFriendsThumb4.setVisibility(View.GONE);
			        	}
	        		}
				});
			}
	    }
    }
    
    class ScheduleEvents extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	RestClient result = null;
			try {
				result = new request().execute(Rest.EVENT + "1;25", Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			final String[] events = {null, null, null};
			
			try {
				final JSONArray threads = new JSONArray(result.getResponse());
				Log.i("events: ", threads.toString());
				
				int k = 0;
				for (int i = 0; i < threads.length(); i++){
					if (threads.getJSONObject(i).has("poster_thumb_url")){
						if (threads.getJSONObject(i).getString("poster_thumb_url") != null){
							events[k] = threads.getJSONObject(i).getString("poster_thumb_url");
						} else if (threads.getJSONObject(i).has("store_logo_url")){
							if (threads.getJSONObject(i).getString("store_logo_url") != null){
								events[k] = threads.getJSONObject(i).getString("store_logo_url");
							} 
						}
					} else if (threads.getJSONObject(i).has("store_logo_url")){
						if (threads.getJSONObject(i).getString("store_logo_url") != null){
							events[k] = threads.getJSONObject(i).getString("store_logo_url");
						}
					} 
					
					if (events[k] != null){
						k++;
					}
							
					if (k == 3 || k == events.length){
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
						if (events[0] != null && events[0].contains(".png")){
							ivEventsThumb1.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(events[0], v.getContext(), 30));
			        	}
			        	if (events[1] != null && events[0].contains(".png")){
			        		ivEventsThumb2.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(events[1], v.getContext(), 30));
			        	}
			        	if (events[2] != null && events[0].contains(".png")){
			        		ivEventsThumb3.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(events[2], v.getContext(), 30));
			        	}
	        		}
				});
			}
	    }
    }
    
    public void reloadView() {
        // Do work to refresh the list here.
		mHandler.post(new Runnable() {
    		public void run() {
		    	list_schedule.clear();
		    	
		    	if (list_schedule.isEmpty()){
		    		TaskQueueImage.addTask(new loadDatabaseThread(), v.getContext());
				}
		    }
		});
    }
    
    class loadDatabaseThread extends Thread {
	    // This method is called when the thread runs
    	List<SocialScheduleModel> list_schedule_database = new ArrayList<SocialScheduleModel>();
    	List<SocialScheduleModel> list_schedule_temp = new ArrayList<SocialScheduleModel>();
    	
    	int dayOfWeek = getIntDayOfWeekFirstDayOfYear();
	    int epoch_start_day2 = TimeCounter.getEpochTimeOnDate(1, 0, year);
		int epoch_end_day2 = TimeCounter.getEpochTimeOnDate(2, 0, year);
		int epoch_last_end_day = TimeCounter.getEpochTimeOnDate(daysOfMonth[11], 11, year);
		int daySelected2 = 1;
		int month2 = 1;
		int year2 = year;
    	
	    public void run() {
	    	Database entry = new Database(v.getContext());
	    	try {
				entry.open();
			} catch (Exception e){
				
			}
	    	
	    	Cursor classes = entry.findAllClass();
		    for (classes.moveToFirst(); !classes.isAfterLast(); classes.moveToNext()){
		    	//Log.i("KEY_COURSE_CODE", classes.getString(14));
		    	list_schedule_database.add(new SocialScheduleModel(classes.getInt(1), classes.getString(2), classes.getString(3), classes.getInt(4), 
		    				classes.getString(5), classes.getDouble(6), classes.getDouble(7), classes.getInt(8), classes.getInt(9), classes.getInt(10), 
		    				classes.getInt(11), classes.getString(12), -1, -1, null, -1, classes.getInt(13), classes.getString(14), classes.getString(15), 
		    				classes.getInt(16), classes.getInt(17), classes.getInt(18), classes.getString(19), classes.getString(20), 0, 0, null, 0, 0, 0));
			}
	    	classes.close();
	    	
			Cursor events = entry.findEventOnThatDay(epoch_start_day2, epoch_last_end_day);
		    for (events.moveToFirst(); !events.isAfterLast(); events.moveToNext()){
		    	//Log.i("KEY_TITLE", events.getString(14));
		    	list_schedule_database.add(new SocialScheduleModel(events.getInt(1), events.getString(2), events.getString(3), events.getInt(4), 
		    				events.getString(5), events.getDouble(6), events.getDouble(7), events.getInt(8), events.getInt(9), events.getInt(10), 
		    				events.getInt(11), events.getString(12), events.getInt(13), events.getInt(14), events.getString(15), events.getInt(16), 
		    				-1, null, null, -1, -1, -1, null, null, 0, 0, null, 0, 0, 0));
			}
	    	events.close();
	    	
	    	try {
				entry.close();
			} catch (Exception e){
				
			}
	    	
	    	while (epoch_end_day2 <= epoch_last_end_day){
	    		list_schedule_temp.clear();
	    		
	    		for (int i = 0; i < list_schedule_database.size(); i++){
	    			if (list_schedule_database.get(i).course_name != null){
						if (list_schedule_database.get(i).day_of_week == dayOfWeek){
							
							SocialScheduleModel event_class = new SocialScheduleModel(list_schedule_database.get(i).user_id, 
									list_schedule_database.get(i).start_time_text, list_schedule_database.get(i).end_time_text, 
									list_schedule_database.get(i).check_status, list_schedule_database.get(i).location, list_schedule_database.get(i).latitude, 
									list_schedule_database.get(i).longitude, list_schedule_database.get(i).alert_time, 
									list_schedule_database.get(i).start_time, list_schedule_database.get(i).end_time, 
									list_schedule_database.get(i).status, list_schedule_database.get(i).description, 
									list_schedule_database.get(i).event_id, list_schedule_database.get(i).extra_id, 
									list_schedule_database.get(i).title, list_schedule_database.get(i).type, 
									list_schedule_database.get(i).course_id, list_schedule_database.get(i).course_name, 
									list_schedule_database.get(i).course_code, list_schedule_database.get(i).course_time_id, 
									list_schedule_database.get(i).is_biweekly, list_schedule_database.get(i).day_of_week, 
									list_schedule_database.get(i).color, list_schedule_database.get(i).icon, 
									epoch_start_day2, epoch_end_day2, TimeCounter.getWeekdayMonthDayYear2(epoch_start_day2), daySelected2, 
									month2, year2);
							list_schedule_temp.add(event_class);
						}
					} else if (list_schedule_database.get(i).title != null){
						if (list_schedule_database.get(i).start_time < epoch_end_day2 && list_schedule_database.get(i).end_time > epoch_start_day2){
							
							SocialScheduleModel event_schedule = new SocialScheduleModel(list_schedule_database.get(i).user_id, 
									list_schedule_database.get(i).start_time_text, list_schedule_database.get(i).end_time_text, 
									list_schedule_database.get(i).check_status, list_schedule_database.get(i).location, list_schedule_database.get(i).latitude, 
									list_schedule_database.get(i).longitude, list_schedule_database.get(i).alert_time, 
									list_schedule_database.get(i).start_time, list_schedule_database.get(i).end_time, 
									list_schedule_database.get(i).status, list_schedule_database.get(i).description, 
									list_schedule_database.get(i).event_id, list_schedule_database.get(i).extra_id, 
									list_schedule_database.get(i).title, list_schedule_database.get(i).type, 
									list_schedule_database.get(i).course_id, list_schedule_database.get(i).course_name, 
									list_schedule_database.get(i).course_code, list_schedule_database.get(i).course_time_id, 
									list_schedule_database.get(i).is_biweekly, list_schedule_database.get(i).day_of_week, 
									list_schedule_database.get(i).color, list_schedule_database.get(i).icon, 
									epoch_start_day2, epoch_end_day2, TimeCounter.getWeekdayMonthDayYear2(epoch_start_day2), daySelected2, 
									month2, year2);
							list_schedule_temp.add(event_schedule);
						}
					} 
	    		}
		    	
		    	if (list_schedule_temp.isEmpty()){
		    		list_schedule_temp.add(new SocialScheduleModel(0, getContext().getString(R.string.Free), getContext().getString(R.string.All_Day), 
		    				0, null, 0, 0, 0, 0, 0, 0, null, 0, 0, null, 0, -1, null, null, -1, -1, -1, null, null, epoch_start_day2, epoch_end_day2, 
    	    				TimeCounter.getWeekdayMonthDayYear2(epoch_start_day2), daySelected2, month2, year2));
    			} else {
    				Collections.sort(list_schedule_temp, new TimeComparatorSecond());
    			}
		    	list_schedule.addAll(list_schedule_temp);
		    	
		    	// ----------- repeat -------------------
		    	if (dayOfWeek < 7){
					dayOfWeek++;
				} else {
					dayOfWeek = 1;
				}
				epoch_start_day2 += 86400;
				epoch_end_day2 += 86400;
				
				if (daySelected2 < daysOfMonth[month2 - 1]){
					daySelected2++;
				} else {
					daySelected2 = 1;
					
					if (month2 > 11){
						month2 = 1;
						year2++;
					} else {
						month2++;
					}
				}
				// ---------------- repeat -------------------
	    	}
			
			if (currentView){
				mHandler.post(new Runnable() {
	        		public void run() {
	        			listView.setVisibility(View.GONE);
	        			Collections.sort(list_schedule, new TimeComparatorFirst());
	        			Collections.sort(list_schedule, new TimeComparatorSecond());
				    	listView.invalidateViews();
				    	listViewAdapter.notifyDataSetChanged();
				    	listView.setVisibility(View.VISIBLE);
				    	
				    	//preLoadImages();
				    	mPullRefreshListView.onRefreshComplete();
	        		}
				});
			}
	    }
    }
    
    class SyncClassesThread extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	Database entry = new Database(v.getContext());
	    	try {
				entry.open();
			} catch (Exception e){
				
			}
	    	
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
			
				try {
					JSONArray threads = new JSONArray(result.getResponse());
					Log.i("school courses: ", threads.toString());
					
					for (int i = 0; i < threads.length(); i++){
						int course_id = 0;
						if (threads.getJSONObject(i).has("id")){
							course_id = threads.getJSONObject(i).getInt("id");
						}
						String course_name = null;
						if (threads.getJSONObject(i).has("course_name")){
							course_name = threads.getJSONObject(i).getString("course_name");
						}
						String course_code = null;
						if (threads.getJSONObject(i).has("course_code")){
							course_code = threads.getJSONObject(i).getString("course_code");
						}
						int status = 0;
						if (threads.getJSONObject(i).has("status")){
							status = threads.getJSONObject(i).getInt("status");
						}
						String course_description = null;
						if (threads.getJSONObject(i).has("course_description")){
							course_description = threads.getJSONObject(i).getString("course_description");
						}
						JSONArray time_info = null;
						if (threads.getJSONObject(i).has("time_info")){
							time_info = threads.getJSONObject(i).getJSONArray("time_info");
						}
						if (time_info != null){
							for (int j = 0; j < time_info.length(); j++){
								int course_time_id = time_info.getJSONObject(j).getInt("id");
								String location = time_info.getJSONObject(j).getString("location");
								double latitude = time_info.getJSONObject(j).getDouble("latitude");
								double longitude = time_info.getJSONObject(j).getDouble("longitude");
								boolean is_biweekly = time_info.getJSONObject(j).getBoolean("is_biweekly");
								int day_of_week = time_info.getJSONObject(j).getInt("day_of_week");
								int start_time = time_info.getJSONObject(j).getInt("start_time");
								int end_time = time_info.getJSONObject(j).getInt("end_time");
								
								entry.findCourseTimeID(Profile.userId, TimeCounter.getClassTime(start_time), TimeCounter.getClassTime(end_time),
										0, location, latitude, longitude, 10, TimeCounter.getEpochClassTime(start_time), TimeCounter.getEpochClassTime(end_time), 
										status, course_description, course_id, course_name, course_code, course_time_id, is_biweekly? 1:0, 
										day_of_week, "RED", "icon_drawable");
							}
						}
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			
			RestClient result2 = null;
			try {
				result2 = new request().execute(Rest.USER_EVENT + "?start=1&end=9999999999", Rest.OSESS + Profile.sk, Rest.GET).get();
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
						
						entry.findEventID(user_id, TimeCounter.getUserEventTime(start_time), TimeCounter.getUserEventTime(end_time), 
								0, location, latitude, longitude, alert_time, start_time, end_time, status, description, user_event_id, 
								extra_id, title, type);
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			
			try {
				entry.deleteClasses();
				entry.deleteEvents();
				entry.close();
			} catch (Exception e){
				
			}
	    }
	}
    
    class SyncEventsThread extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	/*
	    	Database entry = new Database(v.getContext());
	    	try {
				entry.open();
			} catch (Exception e){
				
			}
	    	
			RestClient result = null;
			try {
				result = new request().execute(Rest.USER_EVENT + "?start=1&end=9999999999", Rest.OSESS + Profile.sk, Rest.GET).get();
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
					
					entry.findEventID(user_id, TimeCounter.getUserEventTime(start_time), TimeCounter.getUserEventTime(end_time), 
							0, location, latitude, longitude, alert_time, start_time, end_time, status, description, user_event_id, 
							extra_id, title, type);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				entry.deleteEvents();
				entry.close();
			} catch (Exception e){
				
			}
			*/
	    }
	}
    
    /**
	 * 
	 * @param month
	 * @param year
	 */
	private void setGridCellAdapterToDate(int month, int year, int day){
		adapter = new GridCellAdapter(v.getContext(), R.id.calendar_day_gridcell, month, year);
		_calendar.set(year, month - 1, day);
		if (monthToggle){
			currentMonth.setText(dateFormatter.format(dateTemplate, _calendar.getTime()));
		}
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////
	// Inner Class
	public class GridCellAdapter extends BaseAdapter {
			private static final String tag = "GridCellAdapter";
			private final Context _context;

			private final List<String> list;
			private static final int DAY_OFFSET = 1;
			private final String[] weekdays = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
			private final int month, year;
			private int daysInMonth, prevMonthDays;
			private int currentDayOfMonth;
			private int currentWeekDay;
			private int currentMonth;
			private Button gridcell;
			private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
			
			ImageView ivDot1, ivDot2, ivDot3, ivDot4, ivDot5, ivDot6, ivDot7, ivDot8, ivDot9, ivDot10;

			// Days in Current Month
			public GridCellAdapter(Context context, int textViewResourceId, int month, int year){
				super();
				this._context = context;
				this.list = new ArrayList<String>();
				this.month = month;
				this.year = year;

				Log.d(tag, "==> Passed in Date FOR Month: " + month + " " + "Year: " + year);
				Calendar calendar = Calendar.getInstance();
				setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
				setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
				currentMonth = calendar.get(Calendar.MONTH);
				
				Log.d(tag, "New Calendar:= " + calendar.getTime().toString());
				Log.d(tag, "CurrentDayOfWeek :" + getCurrentWeekDay());
				Log.d(tag, "CurrentDayOfMonth :" + getCurrentDayOfMonth());

				// Print Month
				printMonth(month, year);
			}

			private String getWeekDayAsString(int i){
				return weekdays[i];
			}

			private int getNumberOfDaysOfMonth(int i){
				return daysOfMonth[i];
			}

			public String getItem(int position){
				return list.get(position);
			}

			@Override
			public int getCount(){
				return list.size();
			}

			/**
			 * Prints Month
			 * 
			 * @param mm
			 * @param yy
			 */
			private void printMonth(int mm, int yy){
					Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
					// The number of days to leave blank at
					// the start of this month.
					int trailingSpaces = 0;
					int leadSpaces = 0;
					int daysInPrevMonth = 0;
					int prevMonth = 0;
					int prevYear = 0;
					int nextMonth = 0;
					int nextYear = 0;

					int currentMonth = mm - 1;
					String currentMonthName = getMonthAsString(currentMonth);
					daysInMonth = getNumberOfDaysOfMonth(currentMonth);

					Log.d(tag, "Current Month: " + " " + currentMonthName + " having " + daysInMonth + " days.");

					// Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
					GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
					Log.d(tag, "Gregorian Calendar:= " + cal.getTime().toString());

					if (currentMonth == 11){
						prevMonth = currentMonth - 1;
						daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
						nextMonth = 0;
						prevYear = yy;
						nextYear = yy + 1;
						Log.d(tag, "*->PrevYear: " + prevYear + " PrevMonth:" + prevMonth + " NextMonth: " + nextMonth + " NextYear: " + nextYear);
					} else if (currentMonth == 0){
						prevMonth = 11;
						prevYear = yy - 1;
						nextYear = yy;
						daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
						nextMonth = 1;
						Log.d(tag, "**--> PrevYear: " + prevYear + " PrevMonth:" + prevMonth + " NextMonth: " + nextMonth + " NextYear: " + nextYear);
					} else {
						prevMonth = currentMonth - 1;
						nextMonth = currentMonth + 1;
						nextYear = yy;
						prevYear = yy;
						daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
						Log.d(tag, "***---> PrevYear: " + prevYear + " PrevMonth:" + prevMonth + " NextMonth: " + nextMonth + " NextYear: " + nextYear);
					}

					// Compute how much to leave before before the first day of the
					// month.
					// getDay() returns 0 for Sunday.
					int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
					trailingSpaces = currentWeekDay;

					Log.d(tag, "Week Day:" + currentWeekDay + " is " + getWeekDayAsString(currentWeekDay));
					Log.d(tag, "No. Trailing space to Add: " + trailingSpaces);
					Log.d(tag, "No. of Days in Previous Month: " + daysInPrevMonth);

					if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1){
						++daysInMonth;
					}

					// Trailing Month days
					for (int i = 0; i < trailingSpaces; i++){
						Log.d(tag, "PREV MONTH:= " + prevMonth + " => " + getMonthAsString(prevMonth) + " " + String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i));
						//list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i) + "-GREY" + "-" + getMonthAsString(prevMonth) + "-" + prevYear);
						list.add(String.valueOf(0) + "-GREY" + "-" + getMonthAsString(prevMonth) + "-" + prevYear);
					}

					// Current Month Days
					for (int i = 1; i <= daysInMonth; i++){
						Log.d(currentMonthName, String.valueOf(i) + " " + getMonthAsString(currentMonth) + " " + yy);
						if (i == getCurrentDayOfMonth() && currentMonth == this.currentMonth){
							list.add(String.valueOf(i) + "-RED" + "-" + getMonthAsString(currentMonth) + "-" + yy);
						} else {
							list.add(String.valueOf(i) + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
						}
					}

					// Leading Month days
					for (int i = 0; i < list.size() % 7; i++){
						Log.d(tag, "NEXT MONTH:= " + getMonthAsString(nextMonth));
						//list.add(String.valueOf(i + 1) + "-GREY" + "-" + getMonthAsString(nextMonth) + "-" + nextYear);						
					}
				}

			/**
			 * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
			 * ALL entries from a SQLite database for that month. Iterate over the
			 * List of All entries, and get the dateCreated, which is converted into
			 * day.
			 * 
			 * @param year
			 * @param month
			 * @return
			 */
			
			@Override
			public long getItemId(int position){
				return position;
			}

			@Override
			public View getView(final int position, View convertView, ViewGroup parent){
				View row = convertView;
				if (row == null){
					LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					row = inflater.inflate(R.layout.calendar_day_gridcell, parent, false);
				}
					
				// Get a reference to the Day gridcell
				gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);

				// ACCOUNT FOR SPACING
				//Log.d(tag, "Current Day: " + getCurrentDayOfMonth());
				String[] day_color = list.get(position).split("-");
				final String theday = day_color[0];
				final String themonth = day_color[2];
				final String theyear = day_color[3];
				
				ivDot1 = (ImageView) row.findViewById(R.id.ivDot1);
				ivDot2 = (ImageView) row.findViewById(R.id.ivDot2);
				ivDot3 = (ImageView) row.findViewById(R.id.ivDot3);
				ivDot4 = (ImageView) row.findViewById(R.id.ivDot4);
				ivDot5 = (ImageView) row.findViewById(R.id.ivDot5);
				ivDot6 = (ImageView) row.findViewById(R.id.ivDot6);
				ivDot7 = (ImageView) row.findViewById(R.id.ivDot7);
				ivDot8 = (ImageView) row.findViewById(R.id.ivDot8);
				ivDot9 = (ImageView) row.findViewById(R.id.ivDot9);
				ivDot10 = (ImageView) row.findViewById(R.id.ivDot10);
				
				if (loadDots){
					if (Integer.valueOf(theday) > 0){
						getDots(Integer.valueOf(theday));
					}
				}
				if (position == daysInMonth){
					loadDots = false;
				}
				
				if (daySelected == Integer.valueOf(theday)){
					gridcell.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_calendar_cell2));
				} else {
					gridcell.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_calendar_cell));
				}

				// Set the Day GridCell
				gridcell.setText(theday);
				gridcell.setTag(theday + "-" + themonth + "-" + theyear);
				//Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-" + theyear);

				if (day_color[1].equals("GREY")){
					gridcell.setTextColor(Color.DKGRAY);
				}
					
				if (day_color[1].equals("WHITE")){
					gridcell.setTextColor(Color.WHITE);
				}
					
				if (day_color[1].equals("RED")){
					gridcell.setTextColor(Color.RED);
				}
					
				gridcell.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View vie) {
						daySelected = Integer.valueOf(theday);
						tvDate.setText(String.valueOf(daySelected));
						
						tvDayMonthYear.setText(getDayOfWeek(daySelected) + "\n" + themonth + " " + theday + ", " + theyear);
						dayOfWeek = getIntDayOfWeek(daySelected);
				    	//epoch_start_day = TimeCounter.getEpochTimeOnDate(daySelected, month - 1, year);
						epoch_end_day = TimeCounter.getEpochTimeOnDate(daySelected + 1, month - 1, year);
						
						adapter.notifyDataSetChanged();
						
						listViewScrollTo(TimeCounter.getEpochTimeOnDate(daySelected, month - 1, year));
					}
				}); 
				
				if (Integer.valueOf(theday) == 0){
					gridcell.setVisibility(View.GONE);
					gridcell.setClickable(false);
				}
					
				return row;
			}
			
			public void getDots(int theday) {
			    // This method is called when the thread runs
				Database entry = new Database(v.getContext());
			    try {
					entry.open();
				} catch (Exception e){
						
				}
			    
			    //Log.i("theday", String.valueOf(theday));
			    //check class time with day_of_week
			    Cursor classes = null, events = null;
			    if (entry != null){
			    	classes = entry.findClassDayOfWeek(getIntDayOfWeek(theday));
			    	
					    //Log.i("classes", String.valueOf(classes.getCount()));
					    /*
					    for (classes.moveToFirst(); !classes.isAfterLast(); classes.moveToNext()){
							
						}
						*/
			    	events = entry.findEventOnThatDay(TimeCounter.getEpochTimeOnDate(theday, month - 1, year), TimeCounter.getEpochTimeOnDate(theday + 1, month - 1, year));
						//Log.i("events", String.valueOf(events.getCount()));
						/*
						for (events.moveToFirst(); !events.isAfterLast(); events.moveToNext()){
							
						}
						*/
			    	//}
			    }
			    
			    if (classes != null && events != null){
				    if (classes.getCount() == 1){
				    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	
				    	if (events.getCount() == 1){
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 2){
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 3){
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 4){
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 5){
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 6){
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 7){
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 8){
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() >= 9){
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot10.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } 
				    } else if (classes.getCount() == 2){
				    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	
				    	if (events.getCount() == 1){
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 2){
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 3){
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 4){
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 5){
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 6){
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 7){
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() >= 8){
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot10.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } 
				    } else if (classes.getCount() == 3){
				    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	
				    	if (events.getCount() == 1){
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 2){
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 3){
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 4){
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 5){
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 6){
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() >= 7){
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot10.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } 
				    } else if (classes.getCount() == 4){
				    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	
				    	if (events.getCount() == 1){
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 2){
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 3){
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 4){
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 5){
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() >= 6){
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot10.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } 
				    } else if (classes.getCount() == 5){
				    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	
				    	if (events.getCount() == 1){
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 2){
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 3){
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 4){
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() >= 5){
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot10.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } 
				    } else if (classes.getCount() == 6){
				    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	
				    	if (events.getCount() == 1){
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 2){
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 3){
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() >= 4){
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot10.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } 
				    } else if (classes.getCount() == 7){
				    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	
				    	if (events.getCount() == 1){
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 2){
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() >= 3){
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot10.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } 
				    } else if (classes.getCount() == 8){
				    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	
				    	if (events.getCount() == 1){
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() >= 2){
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot10.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    }
				    } else if (classes.getCount() == 9){
				    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	
				    	if (events.getCount() >= 1){
					    	ivDot10.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } 
				    } else if (classes.getCount() >= 10){
				    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    	ivDot10.setBackgroundDrawable(getResources().getDrawable(R.color.red1));
				    } else {
				    	if (events.getCount() == 1){
					    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 2){
					    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 3){
					    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 4){
					    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 5){
					    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 6){
					    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 7){
					    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 8){
					    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() == 9){
					    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    } else if (events.getCount() >= 10){
					    	ivDot1.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot2.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot3.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot4.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot5.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot6.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot7.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot8.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot9.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    	ivDot10.setBackgroundDrawable(getResources().getDrawable(R.color.blue1));
					    }
				    }
			    }
			    
			    classes.close();
			    events.close();
					
				try {
					entry.close();
				} catch (Exception e){
					
				} 
			}

			public int getCurrentDayOfMonth(){
				return currentDayOfMonth;
			}

			private void setCurrentDayOfMonth(int currentDayOfMonth){
				this.currentDayOfMonth = currentDayOfMonth;
			}
			
			public void setCurrentWeekDay(int currentWeekDay){
				this.currentWeekDay = currentWeekDay;
			}
			
			public int getCurrentWeekDay(){
				return currentWeekDay;
			}
	}
	
	public String getDayOfWeek(int day){
		GregorianCalendar calendar = new GregorianCalendar(year, month - 1, day);
		int i = calendar.get(Calendar.DAY_OF_WEEK);
		
		return weekdays_full[i-1];
	}
	
	public String getMonthAsString(int i){
		return months[i];
	}
	
	public class SocialScheduleArrayAdapter extends ArrayAdapter<SocialScheduleModel> {
		private final List<SocialScheduleModel> list;
		
		class ViewHolder {
			public TextView tvStart, tvStop, tvName, tvDate;
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
				LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.socialschedulerow, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.tvStart = (TextView) rowView.findViewById(R.id.tvStart);
				viewHolder.tvStop = (TextView) rowView.findViewById(R.id.tvStop);
				viewHolder.tvName = (TextView) rowView.findViewById(R.id.tvName);
				viewHolder.tvDate = (TextView) rowView.findViewById(R.id.tvDate);
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
			holder.tvDate.setVisibility(View.GONE);
			
			if (position == 0){
				holder.tvDate.setVisibility(View.VISIBLE);
				holder.tvDate.setText(list.get(position).date);
			} else if (!list.get(position - 1).date.contentEquals(list.get(position).date)){
				holder.tvDate.setVisibility(View.VISIBLE);
				holder.tvDate.setText(list.get(position).date);
			}
			
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
				name = getContext().getString(R.string.Add_a_ClassEvent);
				holder.ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.add_user_event_button));
				holder.ivClock.setImageDrawable(getResources().getDrawable(R.drawable.schedule_clock_icon));
				holder.ivLeft.setImageDrawable(getResources().getDrawable(R.color.dimgrey3));
			}

			holder.tvStart.setText(list_schedule.get(position).start_time_text);	
			holder.tvStop.setText(list_schedule.get(position).end_time_text);	
			holder.tvName.setText(name);	

			holder.bg.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v2) {
					String title, location;
					int start_time, end_time;
					
					if (list_schedule.get(position).course_name != null){	
						AddCourse.act = SocialSchedule.this;
						
						Bundle extras = new Bundle();
						extras.putInt("course_id", list_schedule.get(position).course_id);
						extras.putInt("course_time_id", list_schedule.get(position).course_time_id);
						extras.putInt("start_time", list_schedule.get(position).start_time);
						extras.putInt("end_time", list_schedule.get(position).end_time);
						extras.putInt("daySelected", list_schedule.get(position).day_of_week);
						extras.putString("course_code", list_schedule.get(position).course_code);
						extras.putString("location", list_schedule.get(position).location);
						extras.putBoolean("from_social_schedule", true);
						Intent i = new Intent(v.getContext(), AddCourse.class);
						i.putExtras(extras);
						getContext().startActivity(i);
					} else if (list_schedule.get(position).title != null){
						AddEvent.act = SocialSchedule.this;
						
						Bundle extras = new Bundle();
						extras.putString("title", list_schedule.get(position).title);
						extras.putInt("start_time", list_schedule.get(position).start_time);
						extras.putInt("end_time", list_schedule.get(position).end_time);
						extras.putString("location", list_schedule.get(position).location);
						extras.putString("start_time_text", list_schedule.get(position).start_time_text);
						extras.putString("end_time_text", list_schedule.get(position).end_time_text);
						extras.putInt("event_id", list_schedule.get(position).event_id);
						Intent i = new Intent(v.getContext(), AddEvent.class);
						i.putExtras(extras);
						getContext().startActivity(i);
					} else {
						final CharSequence[] items = {getContext().getString(R.string.Add_New_Event), getContext().getString(R.string.Add_New_Class_Course), getContext().getString(R.string.Cancel)};
						
						AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
						builder.setItems(items, new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int item) {
						    	switch(item){
						    	case 0:
						    		AddEvent.act = SocialSchedule.this;
						    		
						    		Bundle extras = new Bundle();
									extras.putString("title", "");
									extras.putInt("start_time", epoch_start_day + 32400);
									extras.putInt("end_time", epoch_start_day + 36000);
									extras.putString("location", "");
									extras.putString("start_time_text", "09:00 AM");
									extras.putString("end_time_text", "10:00 AM");
									extras.putInt("event_id", 0);
									Intent i = new Intent(v.getContext(), AddEvent.class);
									i.putExtras(extras);
									getContext().startActivity(i);
						    		break;
						    	case 1:
						    		AddCourse.act = SocialSchedule.this;
						    		
						    		Bundle extras2 = new Bundle();
									extras2.putInt("daySelected", dayOfWeek);
									Intent i2 = new Intent(v.getContext(), SearchCourse.class);
									i2.putExtras(extras2);
									getContext().startActivity(i2);
						    		break;
						    	case 2:
						    		break;
						    	}
						    }
						});
						AlertDialog alert = builder.create();
						alert.show();
					}
				}
			});
			
			return rowView;
		}
		
	}
	
	public int getIntDayOfWeek(int day){
		GregorianCalendar calendar = new GregorianCalendar(year, month - 1, day);
		if (calendar.get(Calendar.DAY_OF_WEEK) == 1){
			return 7;
		} else {
			return calendar.get(Calendar.DAY_OF_WEEK) - 1;
		}
	}
	
	public int getIntDayOfWeekFirstDayOfYear(){
		GregorianCalendar calendar = new GregorianCalendar(year, 0, 1);
		if (calendar.get(Calendar.DAY_OF_WEEK) == 1){
			return 7;
		} else {
			return calendar.get(Calendar.DAY_OF_WEEK) - 1;
		}
	}
	
	public class TimeComparatorFirst implements Comparator<SocialScheduleModel> {
        public int compare(SocialScheduleModel o1, SocialScheduleModel o2) {
            return String.valueOf(o1.epoch_start_day).compareTo(String.valueOf(o2.epoch_start_day));
        }
	}
	
	public class TimeComparatorSecond implements Comparator<SocialScheduleModel> {
        public int compare(SocialScheduleModel o1, SocialScheduleModel o2) {
        	if (o1.epoch_start_day == o2.epoch_start_day){
        		return String.valueOf(o1.start_time).compareTo(String.valueOf(o2.start_time));
        	} else {
        		return 0;
        	}
        }
	}
	
	public void onPause() {
		// TODO Auto-generated method stub
		
		currentView = false;
	}
	
	public void onResume() {
		// TODO Auto-generated method stub
		
		currentView = true;
		
		if (tellFriendsYouUpdatedSchedule){
			AlertDialog alert = new AlertDialog.Builder(v.getContext()).create();
			alert.setMessage(getContext().getString(R.string.tellFriendsYouUpdatedSchedule));
			alert.setButton(getContext().getString(R.string.No), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			});
			alert.setButton2(getContext().getString(R.string.Tell_Friends), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					Intent i = new Intent(v.getContext(), SMSInvite.class);
					getContext().startActivity(i);
				}
			});
			alert.show();
			
			tellFriendsYouUpdatedSchedule = false;
		}
	}

	@Override
	public void reSyncFromServer() {
		// TODO Auto-generated method stub
		tellFriendsYouUpdatedSchedule = true;
    	
		mPullRefreshListView.setRefreshing();
		reloadView();
		
		loadDots = true;
		setGridCellAdapterToDate(month, year, daySelected);
	}

}
