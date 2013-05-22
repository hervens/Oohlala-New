package events;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.AddCourse;
import profile.BlockList;
import profile.Friends;
import profile.SearchCourse;
import profile.SearchFriend;
import profile.UpdateStatus;
import profile.UpdateStatusInterface;
import profile.UserProfile;
import smackXMPP.XMPPClient;
import user.Profile;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;

import datastorage.Database;
import datastorage.DeviceDimensions;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.TimeCounter;

public class AddEvent extends Activity implements SearchGlobalLocationInterface {
	TextView bAddEvent;
	EditText etTitle;
	Button bStartDate, bStartTime, bEndDate, bEndTime, bLocation, bReminder;
	ToggleButton tbVisible;
	
	Handler mHandler = new Handler(Looper.getMainLooper());
	
	int sYear, eYear, YearNow;
	int sMonth, eMonth, MonthNow;
	int sDay, eDay, DayNow;
	int sHour, sMinute, eHour, eMinute;
	int sSecondDate, eSecondDate, sSecondTime, eSecondTime;

	static final int START_DATE_DIALOG_ID = 0;
	static final int END_DATE_DIALOG_ID = 1;
	static final int START_TIME_DIALOG_ID = 2;
	static final int END_TIME_DIALOG_ID = 3;
	
	boolean startDate = false;
	boolean startTime = false;
	
	public static AddEventInterface act;
	
	double longitude, latitude;
	
	String title_edit, location_edit, start_time_text, end_time_text;
	int start_time_edit, end_time_edit;
	
	Calendar start_time_calendar, end_time_calendar;
	
	int event_id;
	
	Button bDelete;
	int reminder = 5;
	int visibleFriends = 1;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addevent);
		
		Bundle b = this.getIntent().getExtras();
		if (b != null){
			title_edit = b.getString("title");
			start_time_edit = b.getInt("start_time");
			end_time_edit = b.getInt("end_time");
			location_edit = b.getString("location");
			start_time_text = b.getString("start_time_text");
			end_time_text = b.getString("end_time_text");
			event_id = b.getInt("event_id");
			
			start_time_calendar = Calendar.getInstance(Locale.getDefault());
			start_time_calendar.setTimeInMillis(Long.parseLong(String.valueOf(start_time_edit)) * 1000);
			end_time_calendar = Calendar.getInstance(Locale.getDefault());
			end_time_calendar.setTimeInMillis(Long.parseLong(String.valueOf(end_time_edit)) * 1000);
		}
		
		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		}); 
				
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(AddEvent.this));
		
		bAddEvent = (TextView) findViewById(R.id.bAddEvent);
		bAddEvent.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (etTitle.getText().toString().trim().length() > 0){
					TaskQueueImage.addTask(new SyncEventThread(), AddEvent.this);
				} else {
					Toast.makeText(getApplicationContext(),
							getString(R.string.You_cannot_add_an_event_without_a_title), 
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		etTitle = (EditText) findViewById(R.id.etTitle);
		bStartDate = (Button) findViewById(R.id.bStartDate);
		bStartTime = (Button) findViewById(R.id.bStartTime);
		bEndDate = (Button) findViewById(R.id.bEndDate);
		bEndTime = (Button) findViewById(R.id.bEndTime);
		
		bLocation = (Button) findViewById(R.id.bLocation);
		if (location_edit != null){
			bLocation.setText(location_edit);
		}
		bLocation.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				SearchGlobalLocation.act = AddEvent.this;
				
				Intent i = new Intent(AddEvent.this, SearchGlobalLocation.class);
				startActivity(i);			
			}
		}); 
		
		if (title_edit.trim().length() > 0){
			etTitle.setText(title_edit);
			bAddEvent.setBackgroundDrawable(getResources().getDrawable(R.drawable.compose_share_button_bg));
			bAddEvent.setClickable(true);
		}
		etTitle.addTextChangedListener(new TextWatcher() {
			  public void afterTextChanged(Editable s) {
			       //do something
			  }

			  public void beforeTextChanged(CharSequence s, int start, int count, int after){
			       //do something
			  }

			  public void onTextChanged(CharSequence s, int start, int before, int count) { 			  
				  if (etTitle.getText().toString().trim().length() > 0){
					  bAddEvent.setBackgroundDrawable(getResources().getDrawable(R.drawable.compose_share_button_bg));
					  bAddEvent.setClickable(true);
					  //bPostThread.setTextColor(getResources().getColorStateList(R.color.white1));
				  } else {
					  bAddEvent.setBackgroundDrawable(getResources().getDrawable(R.drawable.compose_share_button_bg2));
					  bAddEvent.setClickable(false);
					  //bPostThread.setTextColor(getResources().getColorStateList(R.color.dimgrey3));
				  } 
			  }
		});
		
		// get the current date
		final Calendar c = Calendar.getInstance();
		YearNow = c.get(Calendar.YEAR);
		MonthNow = c.get(Calendar.MONTH);
		DayNow = c.get(Calendar.DAY_OF_MONTH);
		
		sSecondDate = TimeCounter.getEpochTimeOnDate(DayNow, MonthNow, YearNow);
		eSecondDate = TimeCounter.getEpochTimeOnDate(DayNow, MonthNow, YearNow);
		
		bStartDate.setText(TimeCounter.getUserEventDate(sSecondDate));
		bEndDate.setText(TimeCounter.getUserEventDate(eSecondDate));
		
		sSecondTime = 32400;
		eSecondTime = 36000;
		
		bStartTime.setText(TimeCounter.getClassTime(sSecondTime));
		bEndTime.setText(TimeCounter.getClassTime(eSecondTime));
		
		bStartDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startDate = true;
				
				showDialog(START_DATE_DIALOG_ID);
			}
		});
		
		bEndDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startDate = false;
				
				showDialog(END_DATE_DIALOG_ID);
			}
		});
		
		bStartTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startTime = true;
				
				showDialog(START_TIME_DIALOG_ID);
			}
		});
		
		bEndTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startTime = false;
				
				showDialog(END_TIME_DIALOG_ID);
			}
		});
		
		bReminder = (Button) findViewById(R.id.bReminder);
		bReminder.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v2) {
				final CharSequence[] items = {getString(R.string.None), getString(R.string.When_event_starts), getString(R.string.five_min_before), 
						getString(R.string.ten_min_before), getString(R.string.thirty_min_before), getString(R.string.one_hour_before),
						getString(R.string.two_hours_before), getString(R.string.one_day_before)};
				
				AlertDialog.Builder builder = new AlertDialog.Builder(AddEvent.this);
				builder.setTitle(getString(R.string.Set_Reminder));
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	int start = sSecondDate + sSecondTime;
				    	
				    	switch(item){
				    	case 0:
				    		bReminder.setText(items[0]);
				    		reminder = 0;
				    		
				    		break;
				    	case 1:
				    		bReminder.setText(items[1]);
				    		reminder = 1;
				    		
				    		break;
				    	case 2:
				    		bReminder.setText(items[2]);
				    		reminder = 2;
				    		
				    		break;
				    	case 3:
				    		bReminder.setText(items[3]);
				    		reminder = 3;
				    		
				    		break;
				    	case 4:
				    		bReminder.setText(items[4]);
				    		reminder = 4;
				    		
				    		break;
				    	case 5:
				    		bReminder.setText(items[5]);
				    		reminder = 5;
				    		
				    		break;
				    	case 6:
				    		bReminder.setText(items[6]);
				    		reminder = 6;
				    		
				    		break;
				    	case 7:
				    		bReminder.setText(items[7]);
				    		reminder = 7;
				    		
				    		break;
				    	}
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
		
		tbVisible = (ToggleButton) findViewById(R.id.tbVisible);
		tbVisible.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        if (tbVisible.isChecked()) {
		        	visibleFriends = 1;
		        } else {
		        	visibleFriends = 0;
		        }
		        //Log.i("visibleFriends", String.valueOf(visibleFriends));
		    }
		});
		
		bDelete = (Button) findViewById(R.id.bDelete);
		if (start_time_edit == 0 && end_time_edit == 0){
			bDelete.setVisibility(View.GONE);
		}
		bDelete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog alert = new AlertDialog.Builder(AddEvent.this).create();
	    		alert.setMessage(getString(R.string.Delete_user_event_alert));
	    		alert.setButton(getString(R.string.No), new DialogInterface.OnClickListener() {
	    			
	    			public void onClick(DialogInterface dialog, int which) {
	    				// TODO Auto-generated method stub
	    			}
	    		});
	    		alert.setButton2(getString(R.string.Yes), new DialogInterface.OnClickListener() {
	    			
	    			public void onClick(DialogInterface dialog, int which) {
	    				TaskQueueImage.addTask(new DeleteEventThread(), AddEvent.this);
	    			}
	    		});
	    		alert.show();
			}
		});
		
		if (start_time_edit > 0){
			bStartDate.setText(TimeCounter.getUserEventDate(start_time_edit));
			bStartTime.setText(start_time_text);
			
			sSecondDate = TimeCounter.getEpochTimeOnDate(start_time_calendar.get(Calendar.DAY_OF_MONTH), 
					start_time_calendar.get(Calendar.MONTH), start_time_calendar.get(Calendar.YEAR));
			sSecondTime = start_time_calendar.get(Calendar.HOUR_OF_DAY)*3600 + start_time_calendar.get(Calendar.MINUTE)*60;
		}
		
		if (end_time_edit > 0){
			bEndDate.setText(TimeCounter.getUserEventDate(end_time_edit));
			bEndTime.setText(end_time_text);
			
			eSecondDate = TimeCounter.getEpochTimeOnDate(end_time_calendar.get(Calendar.DAY_OF_MONTH), 
					end_time_calendar.get(Calendar.MONTH), end_time_calendar.get(Calendar.YEAR));
			eSecondTime = end_time_calendar.get(Calendar.HOUR_OF_DAY)*3600 + end_time_calendar.get(Calendar.MINUTE)*60;
		}
	}
	
	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListenerStart = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			sYear = year;
			sMonth = monthOfYear;
			sDay = dayOfMonth;
			updateDisplay1();
		}
	};
	
	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListenerEnd = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				if(isEndDateAfterStartDate(view)){
					eYear = year;
					eMonth = monthOfYear;
					eDay = dayOfMonth;
					updateDisplay1();
				} else {
					Toast.makeText(getBaseContext(),
							getString(R.string.Event_end_date_cannot_be_earlier_than_start_date),
							Toast.LENGTH_SHORT).show();
				}
			}
					
			private boolean isEndDateAfterStartDate(DatePicker tempView) {
				Calendar sCalendar = Calendar.getInstance();
				sCalendar.set(Calendar.YEAR, sYear);
				sCalendar.set(Calendar.MONTH, sMonth);
				sCalendar.set(Calendar.DATE, sDay - 1);
				        
				Calendar eCalendar = Calendar.getInstance();
				eCalendar.set(tempView.getYear(), tempView.getMonth(), tempView.getDayOfMonth(), 0, 0, 0);
				if(eCalendar.after(sCalendar))
					return true;
				else 
					return false;
			}
	};
		
	// the callback received when the user "sets" the time in the dialog
	TimePickerDialog.OnTimeSetListener mTimeSetListenerStart = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			sHour = hourOfDay;
			sMinute = minute;
			//Log.i("sSecond", String.valueOf(sHour*3600 + sMinute*60));
			updateDisplay2();
		}
	};
		
	TimePickerDialog.OnTimeSetListener mTimeSetListenerEnd = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			eHour = hourOfDay;
			eMinute = minute;
			//Log.i("eSecond", String.valueOf(eHour*3600 + eMinute*60));
			updateDisplay2();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case START_DATE_DIALOG_ID:
			if (start_time_edit > 0){
				return new DatePickerDialog(this, mDateSetListenerStart, start_time_calendar.get(Calendar.YEAR), start_time_calendar.get(Calendar.MONTH), start_time_calendar.get(Calendar.DAY_OF_MONTH));
			} else {
				return new DatePickerDialog(this, mDateSetListenerStart, YearNow, MonthNow, DayNow);
			}
		case END_DATE_DIALOG_ID:
			if (end_time_edit > 0){
				return new DatePickerDialog(this, mDateSetListenerEnd, end_time_calendar.get(Calendar.YEAR), end_time_calendar.get(Calendar.MONTH), end_time_calendar.get(Calendar.DAY_OF_MONTH));
			} else {
				return new DatePickerDialog(this, mDateSetListenerEnd, YearNow, MonthNow, DayNow);
			}
		case START_TIME_DIALOG_ID:
			if (start_time_edit > 0){
				return new TimePickerDialog(this, mTimeSetListenerStart, start_time_calendar.get(Calendar.HOUR_OF_DAY), start_time_calendar.get(Calendar.MINUTE), false);
			} else {
				return new TimePickerDialog(this, mTimeSetListenerStart, 9, 0, false);
			}
		case END_TIME_DIALOG_ID:
			if (end_time_edit > 0){
				return new TimePickerDialog(this, mTimeSetListenerEnd, end_time_calendar.get(Calendar.HOUR_OF_DAY), end_time_calendar.get(Calendar.MINUTE), false);
			} else {
				return new TimePickerDialog(this, mTimeSetListenerEnd, 10, 0, false);
			}
		}
		return null;
	}
			
	// updates the date in the TextView
	private void updateDisplay1() {
		if (startDate){
			sSecondDate = TimeCounter.getEpochTimeOnDate(sDay, sMonth, sYear);
			//Log.i("date", String.valueOf(sSecondDate));
			bStartDate.setText(TimeCounter.getUserEventDate(TimeCounter.getEpochTimeOnDate(sDay, sMonth, sYear)));
		} else {
			eSecondDate = TimeCounter.getEpochTimeOnDate(eDay, eMonth, eYear);
			//Log.i("date", String.valueOf(eSecondDate));
			bEndDate.setText(TimeCounter.getUserEventDate(TimeCounter.getEpochTimeOnDate(eDay, eMonth, eYear)));
		}
	}
	
	public void updateDisplay2() {
		if (startTime){
			sSecondTime = sHour*3600 + sMinute*60;
			//Log.i("time", String.valueOf(sSecondTime));
			bStartTime.setText(TimeCounter.getClassTime(sSecondTime));
		} else {
			eSecondTime = eHour*3600 + eMinute*60;
			//Log.i("time", String.valueOf(sSecondTime));
			bEndTime.setText(TimeCounter.getClassTime(eSecondTime));
		}
		
	}
	
	public void setReminder(String message, int start){
		/*
		Intent activate = new Intent(AddEvent.this, OohlalaMain.class);
		AlarmManager alarams;
		PendingIntent alarmIntent = PendingIntent.getBroadcast(AddEvent.this, 0, activate, 0);
		alarams = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarams.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, alarmIntent);
		*/
		
		Log.i("start1", String.valueOf(Long.parseLong(String.valueOf(start)) * 1000));
		Log.i("start2", String.valueOf(System.currentTimeMillis()));
		
		CharSequence tickerText = message;              // ticker-text
    	long when = Long.parseLong(String.valueOf(start)) * 1000;         // notification time
    	CharSequence contentTitle = message;  // message title
    	CharSequence contentText = message + " will start soon";      // message text
    	
    	Bundle extras = new Bundle();
		
		Intent notificationIntent = new Intent(AddEvent.this, OohlalaMain.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	notificationIntent.putExtras(extras);
    	
    	PendingIntent contentIntent = PendingIntent.getActivity(AddEvent.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    	// the next two lines initialize the Notification, using the configurations above
    	Notification notification = new Notification(R.drawable.ic_launcher, tickerText, when);
    	notification.setLatestEventInfo(AddEvent.this, contentTitle, contentText, contentIntent);
    	notification.defaults = Notification.DEFAULT_ALL;
    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
    	
    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	mNotificationManager.notify(1, notification);
	}
	
	public void chooseReminder(int start){
		switch(reminder){
    	case 0:
    		
    		break;
    	case 1:
    		setReminder(etTitle.getText().toString(), start);
    		
    		break;
    	case 2:
    		setReminder(etTitle.getText().toString(), start - 300);
    		
    		break;
    	case 3:
    		setReminder(etTitle.getText().toString(), start - 600);
    		
    		break;
    	case 4:
    		setReminder(etTitle.getText().toString(), start - 1800);
    		
    		break;
    	case 5:
    		setReminder(etTitle.getText().toString(), start - 3600);
    		
    		break;
    	case 6:
    		setReminder(etTitle.getText().toString(), start - 7200);
    		
    		break;
    	case 7:
    		setReminder(etTitle.getText().toString(), start - 86400);
    		
    		break;
    	}
	}

	@Override
	public void UpdateLocation(String location, double longitude,
			double latitude) {
		// TODO Auto-generated method stub
		if (location != null){
			bLocation.setText(location);
		}
		
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	class DeleteEventThread extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	RestClient result = null;
			try {
				result = new Rest.request().execute(Rest.USER_EVENT + event_id, Rest.OSESS + Profile.sk, Rest.DELETE).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("delete user event: ", result.getResponse());
        	
        	if (result.getResponseCode() == 204){
        		Database entry = new Database(AddEvent.this);
		    	try {
					entry.open();
				} catch (Exception e){
					
				}
		    	
		    	entry.deleteEvent(event_id);
		    	
		    	try {
					entry.close();
				} catch (Exception e){
					
				}
        		
		    	mHandler.post(new Runnable() {
	        		public void run() {
	        			if (act != null){
	        				act.reSyncFromServer();
	        			}
						onBackPressed();
						Toast.makeText(getApplicationContext(),
								getString(R.string.Event_is_deleted), 
								Toast.LENGTH_SHORT).show();
	        		}
		    	});
        	} 
	    }
	}
	
	class SyncEventThread extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	String title = etTitle.getText().toString();
			int start = sSecondDate + sSecondTime;
			int end = eSecondDate + eSecondTime;
			String location = bLocation.getText().toString();
            
			if (event_id != 0){
				RestClient result = null;
				try {
					result = new Rest.requestBody().execute(Rest.USER_EVENT + event_id, Rest.OSESS + Profile.sk, Rest.PUT, "7", "title", title, "start", String.valueOf(start), "end", String.valueOf(end), "location", location, "latitude", String.valueOf(latitude), "longitude", String.valueOf(longitude), "status", String.valueOf(visibleFriends)).get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.i("update user event: ", result.getResponse());
				
            	if (result.getResponseCode() == 200){
            		Database entry = new Database(AddEvent.this);
			    	try {
						entry.open();
					} catch (Exception e){
						
					}
			    	
			    	try {
			    		JSONObject thread = new JSONObject(result.getResponse());
						Log.i("user event: ", thread.toString());
						
						int user_event_id = thread.getInt("id");
						int user_id = thread.getInt("user_id");
						int start_time = thread.getInt("start");
						int end_time = thread.getInt("end");
						String title_event = thread.getString("title");
						String description = thread.getString("description");
						String location_event = thread.getString("location");
						double latitude = thread.getDouble("latitude");
						double longitude = thread.getDouble("longitude");
						int alert_time = thread.getInt("alert_time");
						int status = thread.getInt("status");
						int type = thread.getInt("type");
						int extra_id = thread.getInt("extra_id");
							
						entry.findEventID(user_id, TimeCounter.getUserEventTime(start_time), TimeCounter.getUserEventTime(end_time), 
								0, location_event, latitude, longitude, alert_time, start_time, end_time, status, description, user_event_id, 
								extra_id, title_event, type);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
			    	try {
						entry.close();
					} catch (Exception e){
						
					}
			    	
			    	mHandler.post(new Runnable() {
		        		public void run() {	
		        			if (act != null){
		        				act.reSyncFromServer();
		        			}
			    			Toast.makeText(getApplicationContext(),
									getString(R.string.Event_is_updated), 
									Toast.LENGTH_SHORT).show();
			    			
			    			try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							onBackPressed();
						
							//chooseReminder(start);
		        		}
			    	});
            	} else {
            		mHandler.post(new Runnable() {
		        		public void run() {
		            		Toast.makeText(getApplicationContext(),
									getString(R.string.Event_end_time_cannot_be_earlier_than_start_time), 
									Toast.LENGTH_SHORT).show();
		        		}
            		});
            	}
			} else {
				RestClient result = null;
				try {
					result = new Rest.requestBody().execute(Rest.USER_EVENT, Rest.OSESS + Profile.sk, Rest.POST, "7", "title", title, "start", String.valueOf(start), "end", String.valueOf(end), "location", location, "latitude", String.valueOf(latitude), "longitude", String.valueOf(longitude), "status", String.valueOf(visibleFriends)).get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.i("post user event: ", result.getResponse());
            	
            	if (result.getResponseCode() == 201){
            		Database entry = new Database(AddEvent.this);
			    	try {
						entry.open();
					} catch (Exception e){
						
					}
			    	
			    	try {
			    		JSONObject thread = new JSONObject(result.getResponse());
						Log.i("user event: ", thread.toString());
						
						int user_event_id = thread.getInt("id");
						int user_id = thread.getInt("user_id");
						int start_time = thread.getInt("start");
						int end_time = thread.getInt("end");
						String title_event = thread.getString("title");
						String description = thread.getString("description");
						String location_event = thread.getString("location");
						double latitude = thread.getDouble("latitude");
						double longitude = thread.getDouble("longitude");
						int alert_time = thread.getInt("alert_time");
						int status = thread.getInt("status");
						int type = thread.getInt("type");
						int extra_id = thread.getInt("extra_id");
							
						entry.findEventID(user_id, TimeCounter.getUserEventTime(start_time), TimeCounter.getUserEventTime(end_time), 
								0, location_event, latitude, longitude, alert_time, start_time, end_time, status, description, user_event_id, 
								extra_id, title_event, type);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
			    	try {
						entry.close();
					} catch (Exception e){
						
					}
            		
            		mHandler.post(new Runnable() {
		        		public void run() {
		        			if (act != null){
		        				act.reSyncFromServer();
		        			}
							Toast.makeText(getApplicationContext(),
									getString(R.string.Event_is_added), 
									Toast.LENGTH_SHORT).show();
							
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							onBackPressed();
							
							//chooseReminder(start);
		        		}
            		});
            	} else {
            		mHandler.post(new Runnable() {
		        		public void run() {
		            		Toast.makeText(getApplicationContext(),
									getString(R.string.Event_end_time_cannot_be_earlier_than_start_time), 
									Toast.LENGTH_SHORT).show();
		        		}
            		});
            	}
			}
	    }
	}

}
