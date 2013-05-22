package profile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import launchOohlala.FindYourFriends;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.OthersProfile.courseScheduleRequest;
import profile.Schedule.reloadViewThread;
import rewards.ScheduleModel;

import network.ErrorCodeParser;
import user.Profile;

import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gotoohlala.R;

import datastorage.Database;
import datastorage.Fonts;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.Rest.request;
import datastorage.TimeCounter;
import events.AddEvent;
import events.AddEventInterface;
import events.SearchGlobalLocation;
import events.SearchGlobalLocationInterface;
import events.SocialSchedule;

public class AddCourse extends Activity implements SearchGlobalLocationInterface {
	TextView tvStartTime, tvEndTime, bSave;
	Button bLocation;
	RelativeLayout rlStartTime, rlEndTime;
	Button bDelete;
	
	static final int TIME_DIALOG_ID_START = 0;
	static final int TIME_DIALOG_ID_END = 1;
	static final int TIME_PICKER_INTERVAL = 5;
	final int PICK_CONTACT = 1;
	
	int sHour, sMinute, eHour, eMinute;
	int sSecond = 32400;
	int eSecond = 36000;
	
	int course_id, daySelected;
	int course_time_id = -1;
	
	String course_code;
	
	int start_time = -1;
	int end_time = -1;
	int course_id_num = 0;
	
	Calendar start_time_calendar, end_time_calendar;
	
	public static AddEventInterface act;
	public static AddCourseInterface act2;
	
	Boolean from_social_schedule = false;
	String location;
	
	Handler mHandler = new Handler(Looper.getMainLooper());
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcourse);
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		}); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(AddCourse.this));
		
		Bundle b = this.getIntent().getExtras();
		course_id = b.getInt("course_id");
		if (b.containsKey("course_time_id")){
			course_time_id = b.getInt("course_time_id");
			start_time = b.getInt("start_time");
			end_time = b.getInt("end_time");
			location = b.getString("location");
			if (b.containsKey("from_social_schedule")){
				from_social_schedule = b.getBoolean("from_social_schedule");
			}
			
			start_time_calendar = Calendar.getInstance(Locale.getDefault());
			end_time_calendar = Calendar.getInstance(Locale.getDefault());
			if (from_social_schedule){
				start_time_calendar.setTimeInMillis(Long.parseLong(String.valueOf(start_time)) * 1000);
				end_time_calendar.setTimeInMillis(Long.parseLong(String.valueOf(end_time)) * 1000);
			} else {
				start_time_calendar.setTimeInMillis(Long.parseLong(String.valueOf(TimeCounter.getEpochClassTime(start_time))) * 1000);
				end_time_calendar.setTimeInMillis(Long.parseLong(String.valueOf(TimeCounter.getEpochClassTime(end_time))) * 1000);
			}
		}
		daySelected = b.getInt("daySelected");
		course_code = b.getString("course_code");
		
		header.setText(course_code.toUpperCase());
		
		tvStartTime = (TextView) findViewById(R.id.tvStartTime);
		tvEndTime = (TextView) findViewById(R.id.tvEndTime);
		
		bLocation = (Button) findViewById(R.id.bLocation);
		if (location != null){
			bLocation.setText(location);
		}
		bLocation.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				SearchGlobalLocation.act = AddCourse.this;
				
				Intent i = new Intent(AddCourse.this, SearchGlobalLocation.class);
				startActivity(i);			
			}
		}); 
		
		rlStartTime = (RelativeLayout) findViewById(R.id.rlStartTime);
		rlEndTime = (RelativeLayout) findViewById(R.id.rlEndTime);
		
		rlStartTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID_START);
			}
		});
		
		rlEndTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID_END);
			}
		});
		
		bDelete = (Button) findViewById(R.id.bDelete);
		if (course_time_id == -1){
			bDelete.setVisibility(View.GONE);
		}
		bDelete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v2) {
				AlertDialog alert = new AlertDialog.Builder(AddCourse.this).create();
	    		alert.setMessage(getString(R.string.Delete_course_alert));
	    		alert.setButton(getString(R.string.No), new DialogInterface.OnClickListener() {
	    			
	    			public void onClick(DialogInterface dialog, int which) {
	    				// TODO Auto-generated method stub
	    			}
	    		});
	    		alert.setButton2(getString(R.string.Yes), new DialogInterface.OnClickListener() {
	    			
	    			public void onClick(DialogInterface dialog, int which) {
	    				TaskQueueImage.addTask(new DeleteClassThread(), AddCourse.this);
	    			}
	    		});
	    		alert.show();
			}
		});
		
		bSave = (TextView) findViewById(R.id.bSave);
		bSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v2) {
				if (sSecond >= eSecond){
					AlertDialog alert = new AlertDialog.Builder(AddCourse.this).create();
		    		alert.setMessage(getString(R.string.Add_course_alert));
		    		alert.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
		    			
		    			public void onClick(DialogInterface dialog, int which) {
		    				// TODO Auto-generated method stub
		    			}
		    		});
		    		alert.show();
				} else {
					TaskQueueImage.addTask(new SyncClassThread(), AddCourse.this);
				}
			}
		});
		
		if (start_time != -1 && end_time != -1){
			if (from_social_schedule){
				sSecond = start_time_calendar.get(Calendar.HOUR_OF_DAY)*3600 + start_time_calendar.get(Calendar.MINUTE)*60;
				eSecond = end_time_calendar.get(Calendar.HOUR_OF_DAY)*3600 + end_time_calendar.get(Calendar.MINUTE)*60;
			} else {
				sSecond = start_time;
				eSecond = end_time;
			}
			
			tvStartTime.setText(TimeCounter.getClassTime(sSecond));
			tvEndTime.setText(TimeCounter.getClassTime(eSecond));
		}
		
		TaskQueueImage.addTask(new getCouseIdNum(), AddCourse.this);
	}
	
	class getCouseIdNum extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
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
			Log.i("course id num1: ", result.getResponse());
			
			try {
				JSONArray threads = new JSONArray(result.getResponse());
				Log.i("school courses: ", threads.toString());
				
				for (int i = 0; i < threads.length(); i++){
					if (course_id == threads.getJSONObject(i).getInt("id")){
						JSONArray time_info = threads.getJSONObject(i).getJSONArray("time_info");
						course_id_num = time_info.length(); 
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("course id num2: ", String.valueOf(course_id_num));
	    }
	}

	// the callback received when the user "sets" the time in the dialog
	TimePickerDialog.OnTimeSetListener mTimeSetListenerStart =
	new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			sHour = hourOfDay;
			sMinute = minute;
			//Log.i("sSecond", String.valueOf(sHour*3600 + sMinute*60));
			updateDisplay1();
		}
	};
	
	TimePickerDialog.OnTimeSetListener mTimeSetListenerEnd =
	new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			eHour = hourOfDay;
			eMinute = minute;
			//Log.i("eSecond", String.valueOf(eHour*3600 + eMinute*60));
			updateDisplay2();
		}
	};
					
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID_START:
			if (start_time != -1){
				return new TimePickerDialog(this, mTimeSetListenerStart, start_time_calendar.get(Calendar.HOUR_OF_DAY), start_time_calendar.get(Calendar.MINUTE), false);
			} else {
				return new TimePickerDialog(this, mTimeSetListenerStart, 9, 0, false);
			}
		case TIME_DIALOG_ID_END:
			if (end_time != -1){
				return new TimePickerDialog(this, mTimeSetListenerEnd, end_time_calendar.get(Calendar.HOUR_OF_DAY), end_time_calendar.get(Calendar.MINUTE), false);
			} else {
				return new TimePickerDialog(this, mTimeSetListenerEnd, 10, 0, false);
			}
		}
		return null;
	}
			
	public void updateDisplay1() {
		sSecond = sHour*3600 + sMinute*60;
		
		tvStartTime.setText(TimeCounter.getClassTime(sSecond));
	}
	
	public void updateDisplay2() {
		eSecond = eHour*3600 + eMinute*60;
		
		tvEndTime.setText(TimeCounter.getClassTime(eSecond));
	}

	@Override
	public void UpdateLocation(String location, double longitude,
			double latitude) {
		// TODO Auto-generated method stub
		if (location != null){
			bLocation.setText(location);
		}
	}
	
	class DeleteClassThread extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	RestClient result = null;
			try {
				if (course_id_num == 1){
					result = new Rest.requestBody().execute(Rest.SCHOOL_COURSE + course_id, Rest.OSESS + Profile.sk, Rest.PUT3, "3", "course_time_ids", null, "remove_from_timetable", "1", "auto_leave_group", "1").get();
				} else {
					result = new Rest.requestBody().execute(Rest.SCHOOL_COURSE + course_id, Rest.OSESS + Profile.sk, Rest.PUT3, "3", "course_time_ids", String.valueOf(course_time_id), "remove_from_timetable", "1", "auto_leave_group", "1").get(); 
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("remove course", result.getResponse());
			
			int code = result.getResponseCode();
			
			if (code == 200) {
				Database entry = new Database(AddCourse.this);
		    	try {
					entry.open();
				} catch (Exception e){
					
				}
		    	
		    	if (course_id_num == 1){
		    		entry.deleteClasses(course_id);
		    	} else {
		    		entry.deleteClass(course_time_id);
		    	}
		    	
		    	try {
					entry.close();
				} catch (Exception e){
					
				}
		    	
				Log.i("delete course2", "++++++++++++++++++++++++");
				mHandler.post(new Runnable() {
	        		public void run() {
						if (act != null){
							act.reSyncFromServer();
						}
						onBackPressed();
						Toast.makeText(AddCourse.this, getString(R.string.Class_is_deleted), Toast.LENGTH_SHORT).show();
						
						/*
						Bundle extras = new Bundle();
						extras.putInt("daySelected", daySelected);
						extras.putInt("back", 1);
						Intent i = new Intent(AddCourse.this, Schedule.class);
						i.putExtras(extras);
						startActivity(i);
						*/
	        		}
				});
			}
	    }
	}
	
	class SyncClassThread extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	if (course_time_id == -1){
				String loc = bLocation.getText().toString();
				Log.i("course_id", String.valueOf(course_id));
				
				RestClient result = null;
				try {
					result = new Rest.requestBody().execute(Rest.SCHOOL_COURSE + course_id, Rest.OSESS + Profile.sk, Rest.PUT2, "6", "times", String.valueOf(daySelected), "times", String.valueOf(sSecond), "times", String.valueOf(eSecond), "times", String.valueOf(loc), "add_to_timetable", "1", "auto_join_group", "1").get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.i("add course", result.getResponse());
				
				int code = result.getResponseCode();
				
				if (code == 200) {
					Database entry = new Database(AddCourse.this);
			    	try {
						entry.open();
					} catch (Exception e){
						
					}
					
					try {
						JSONObject thread = new JSONObject(result.getResponse());
						Log.i("school course: ", thread.toString());
						
						int course_id = 0;
						if (thread.has("id")){
							course_id = thread.getInt("id");
						}
						String course_name = null;
						if (thread.has("course_name")){
							course_name = thread.getString("course_name");
						}
						String course_code = null;
						if (thread.has("course_code")){
							course_code = thread.getString("course_code");
						}
						int status = 0;
						if (thread.has("status")){
							status = thread.getInt("status");
						}
						String course_description = null;
						if (thread.has("course_description")){
							course_description = thread.getString("course_description");
						}
						JSONArray time_info = null;
						if (thread.has("time_info")){
							time_info = thread.getJSONArray("time_info");
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
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						entry.close();
					} catch (Exception e){
						
					}
					
					//allow user to invite friends using SMS
					mHandler.post(new Runnable() {
		        		public void run() {
							final CharSequence[] items = {getString(R.string.Add_Another_Class), getString(R.string.Invite_Friends), getString(R.string.Done)};
							
							AlertDialog.Builder builder = new AlertDialog.Builder(AddCourse.this);
							builder.setTitle(getString(R.string.You_just_joined) + course_code);
							builder.setItems(items, new DialogInterface.OnClickListener() {
							    public void onClick(DialogInterface dialog, int item) {
							    	switch(item){
							    	case 0:
							    		Log.i("add course2", "++++++++++++++++++++++++");
										if (act != null){
											act.reSyncFromServer();
										}
										if (act2 != null){
											act2.addCourseOnBackPressed();
										}
										Toast.makeText(AddCourse.this, getString(R.string.Class_is_added), Toast.LENGTH_SHORT).show();
										onBackPressed();
										
							    		break;
							    	case 1:
							    		Log.i("add course2", "++++++++++++++++++++++++");
										if (act != null){
											act.reSyncFromServer();
										}
										Toast.makeText(AddCourse.this, getString(R.string.Class_is_added), Toast.LENGTH_SHORT).show();
										
										Bundle extras = new Bundle();
										extras.putInt("course_id", course_id);
										extras.putString("sms_msg", getString(R.string.You_just_joined) + course_code);
										Intent i = new Intent(getApplicationContext(), SMSInvite.class);
										i.putExtras(extras);
			            				startActivity(i);
										
							    		break;
							    	case 2:
							    		Log.i("add course2", "++++++++++++++++++++++++");
										if (act != null){
											act.reSyncFromServer();
										}
										Toast.makeText(AddCourse.this, getString(R.string.Class_is_added), Toast.LENGTH_SHORT).show();
										onBackPressed();
										
							    		break;
							    	}
							    }
							});
					
		        			AlertDialog alert = builder.create();
		        			alert.show();
		        		}
					});
				}
			} else {
				RestClient result = null;
				try {
					if (course_id_num == 1){
						result = new Rest.requestBody().execute(Rest.SCHOOL_COURSE + course_id, Rest.OSESS + Profile.sk, Rest.PUT3, "3", "course_time_ids", null, "remove_from_timetable", "1", "auto_leave_group", "1").get();
					} else {
						result = new Rest.requestBody().execute(Rest.SCHOOL_COURSE + course_id, Rest.OSESS + Profile.sk, Rest.PUT3, "3", "course_time_ids", String.valueOf(course_time_id), "remove_from_timetable", "1", "auto_leave_group", "1").get(); 
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.i("remove course", result.getResponse());
				
				int code = result.getResponseCode();
				
				if (code == 200) {
					String loc = bLocation.getText().toString();
					Log.i("course_id", String.valueOf(course_id));
					
					RestClient result2 = null;
					try {
						result2 = new Rest.requestBody().execute(Rest.SCHOOL_COURSE + course_id, Rest.OSESS + Profile.sk, Rest.PUT2, "6", "times", String.valueOf(daySelected), "times", String.valueOf(sSecond), "times", String.valueOf(eSecond), "times", String.valueOf(loc), "add_to_timetable", "1", "auto_join_group", "1").get();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Log.i("update course", result2.getResponse());
					
					int code2 = result2.getResponseCode();
					
					if (code2 == 200) {
						Database entry = new Database(AddCourse.this);
				    	try {
							entry.open();
						} catch (Exception e){
							
						}
						
				    	try {
							JSONObject thread = new JSONObject(result2.getResponse());
							Log.i("school course: ", thread.toString());
							
							int course_id = 0;
							if (thread.has("id")){
								course_id = thread.getInt("id");
							}
							String course_name = null;
							if (thread.has("course_name")){
								course_name = thread.getString("course_name");
							}
							String course_code = null;
							if (thread.has("course_code")){
								course_code = thread.getString("course_code");
							}
							int status = 0;
							if (thread.has("status")){
								status = thread.getInt("status");
							}
							String course_description = null;
							if (thread.has("course_description")){
								course_description = thread.getString("course_description");
							}
							JSONArray time_info = null;
							if (thread.has("time_info")){
								time_info = thread.getJSONArray("time_info");
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
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						try {
							entry.close();
						} catch (Exception e){
							
						}
						
						//allow user to invite friends using SMS
						mHandler.post(new Runnable() {
			        		public void run() {
								final CharSequence[] items = {getString(R.string.Invite_Friends), getString(R.string.Done)};
								
								AlertDialog.Builder builder = new AlertDialog.Builder(AddCourse.this);
								builder.setTitle(getString(R.string.You_just_updated) + course_code);
								builder.setItems(items, new DialogInterface.OnClickListener() {
								    public void onClick(DialogInterface dialog, int item) {
								    	switch(item){
								    	case 0:
								    		Log.i("update course2", "++++++++++++++++++++++++");
											if (act != null){
												act.reSyncFromServer();
											}
											Toast.makeText(AddCourse.this, getString(R.string.Class_is_updated), Toast.LENGTH_SHORT).show();
											
											Bundle extras = new Bundle();
											extras.putInt("course_id", course_id);
											extras.putString("sms_msg", getString(R.string.You_just_joined) + course_code);
											Intent i = new Intent(getApplicationContext(), SMSInvite.class);
											i.putExtras(extras);
				            				startActivity(i);
											
								    		break;
								    	case 1:
								    		Log.i("update course2", "++++++++++++++++++++++++");
											if (act != null){
												act.reSyncFromServer();
											}
											//onBackPressed();
											Toast.makeText(AddCourse.this, getString(R.string.Class_is_updated), Toast.LENGTH_SHORT).show();
											
								    		break;
								    	}
								    }
								});
								
								AlertDialog alert = builder.create();
			        			alert.show();
			        		}
						});
					}
				}
			}
	    }
	}

}
