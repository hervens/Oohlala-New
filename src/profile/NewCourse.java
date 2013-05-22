package profile;

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
import profile.SearchFriend;
import profile.UpdateStatus;
import profile.UpdateStatusInterface;
import profile.UserProfile;
import user.Profile;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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

import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;

import datastorage.DeviceDimensions;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.TimeCounter;

public class NewCourse extends Activity {
	TextView bNewCourse;
	EditText etCourseCode, etCourseName, etProgramName;
	
	Handler mHandler = new Handler(Looper.getMainLooper());
	
	int daySelected;
	String course_code;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newcourse);
		
		Bundle b = this.getIntent().getExtras();
		if (b != null){
			course_code = b.getString("course_code");
			daySelected = b.getInt("daySelected");
		}
		
		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		}); 
				
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(NewCourse.this));
		
		bNewCourse = (TextView) findViewById(R.id.bNewCourse);
		bNewCourse.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (etCourseCode.getText().toString().trim().length() > 0){
					String course_code = etCourseCode.getText().toString();
					String course_name = etCourseName.getText().toString();
					String program_name = etProgramName.getText().toString();
					String course_description = "";
					int course_id = 0;
					int course_time_id = 0;

					RestClient result = null;
						try {
							result = new Rest.requestBody().execute(Rest.SCHOOL_COURSE, Rest.OSESS + Profile.sk, Rest.POST, "4", "course_code", course_code, "course_name", course_name, "program_name", program_name, "course_description", course_description).get();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Log.i("add new course: ", result.getResponse());
		            	
		            	if (result.getResponseCode() == 201){
		            		try {
		            			JSONObject thread = new JSONObject(result.getResponse());
		        				Log.i("added new course: ", thread.toString());
		        				
		        				course_id = thread.getInt("id");
		        				course_name = thread.getString("course_name");
		        				course_code = thread.getString("course_code");
		        				course_time_id = 0;
		        				
		        				Bundle extras = new Bundle();
								extras.putInt("course_id", course_id);
								extras.putInt("course_time_id", course_time_id);
								extras.putInt("daySelected", daySelected);
								extras.putString("course_code", course_code);
								Intent i = new Intent(NewCourse.this, Course.class);
								i.putExtras(extras);
								startActivity(i);
		        			} catch (JSONException e) {
		        				// TODO Auto-generated catch block
		        				e.printStackTrace();
		        			}
		            	} else {
		            		Toast.makeText(getApplicationContext(),
									getString(R.string.You_cannot_add_a_new_course_without_a_course_code), 
									Toast.LENGTH_SHORT).show();
		            	}
				} else {
					Toast.makeText(getApplicationContext(),
							getString(R.string.You_cannot_add_a_new_course_without_a_course_code), 
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		etCourseCode = (EditText) findViewById(R.id.etCourseCode);
		etCourseName = (EditText) findViewById(R.id.etCourseName);
		etProgramName = (EditText) findViewById(R.id.etProgramName);
		
		if (course_code != null){
			etCourseCode.setText(course_code);
			bNewCourse.setBackgroundDrawable(getResources().getDrawable(R.drawable.compose_share_button_bg));
			bNewCourse.setClickable(true);
		}
		etCourseCode.addTextChangedListener(new TextWatcher() {
			  public void afterTextChanged(Editable s) {
			       //do something
			  }

			  public void beforeTextChanged(CharSequence s, int start, int count, int after){
			       //do something
			  }

			  public void onTextChanged(CharSequence s, int start, int before, int count) { 			  
				  if (etCourseCode.getText().toString().trim().length() > 0){
					  bNewCourse.setBackgroundDrawable(getResources().getDrawable(R.drawable.compose_share_button_bg));
					  bNewCourse.setClickable(true);
					  //bPostThread.setTextColor(getResources().getColorStateList(R.color.white1));
				  } else {
					  bNewCourse.setBackgroundDrawable(getResources().getDrawable(R.drawable.compose_share_button_bg2));
					  bNewCourse.setClickable(false);
					  //bPostThread.setTextColor(getResources().getColorStateList(R.color.dimgrey3));
				  } 
			  }
		});
		
	}

}
