package launchOohlala;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import profile.Badge;
import profile.ModifyProfile;

import user.Profile;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.R.id;
import com.gotoohlala.R.layout;

import datastorage.CacheInternalStorage;
import datastorage.Fonts;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.UserFirstLogin;
import datastorage.UserLoginInfo;
import datastorage.Rest.request;

import network.ErrorCodeParser;
import network.RetrieveData;

import ManageThreads.TaskQueue;
import ManageThreads.TaskQueueImage;
import ManageThreads.TaskQueueImage2;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends Activity {

	int schoolid = 0;
	String birthdate, first_name, last_name, password, userPW, fb_uid, email;
	String gender = "";

	EditText etEmail, etFirstName, etLastName, etPassword;
	Button createAccountB, bBirthday, bGender;
	private int mYear, YearNow;
	private int mMonth, MonthNow;
	private int mDay, DayNow;

	static final int DATE_DIALOG_ID = 0;
	
	TextView tvSchoolName, tvOr;
	private Button bLoginFacebook;
	private static final List<String> PERMISSIONS = Arrays.asList("user_about_me", "user_birthday", "email");
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
	BroadcastReceiver brLogout;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(Register.this));

		Bundle b = this.getIntent().getExtras();
		schoolid = b.getInt("school_id");
		String schoolname = b.getString("school_name");
		
		tvSchoolName = (TextView) findViewById(R.id.tvSchoolName);
		tvSchoolName.setTypeface(Fonts.getOpenSansBold(Register.this));
		tvSchoolName.setText(schoolname);
		
		tvOr = (TextView) findViewById(R.id.tvOr);
		tvOr.setTypeface(Fonts.getOpenSansBold(Register.this));
		
		bLoginFacebook = (Button) findViewById(R.id.bLoginFacebook);
	    
	    Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
	    
        Session session = Session.getActiveSession();
        if (session == null) {
        	if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
        
        //bLoginFacebook.setText(R.string.SIGN_UP_WITH_FACEBOOK);
    	bLoginFacebook.setOnClickListener(new OnClickListener() {
            public void onClick(View view) { onClickLogin(); }
        });
		
    	etEmail = (EditText) findViewById(R.id.etEmail);
    	etEmail.setTypeface(Fonts.getOpenSansRegular(Register.this));
		etFirstName = (EditText) findViewById(R.id.etFirstName);
		etFirstName.setTypeface(Fonts.getOpenSansRegular(Register.this));
		etLastName = (EditText) findViewById(R.id.etLastName);
		etLastName.setTypeface(Fonts.getOpenSansRegular(Register.this));
		etPassword = (EditText) findViewById(R.id.etPassword);
		etPassword.setTypeface(Fonts.getOpenSansRegular(Register.this));		
		etPassword.setOnFocusChangeListener(new OnFocusChangeListener() {          
			
	        public void onFocusChange(View v, boolean hasFocus) {
	            if(hasFocus){
	            	//do job here owhen Edittext lose focus 
	            	etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
	            } else {
	            	etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
	            }
	        }
	    });
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
				
				InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
			}
	    }); 

		bBirthday = (Button) findViewById(R.id.bBirthday);
		bBirthday.setTypeface(Fonts.getOpenSansRegular(Register.this));
		bBirthday.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				etPassword.clearFocus();
				
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		createAccountB = (Button) findViewById(R.id.bCreateAccount);
		//createAccountB.setTypeface(Fonts.getOpenSansRegular(Register.this));
		createAccountB.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				email = etEmail.getText().toString().trim();
				password = etPassword.getText().toString().trim();
				first_name = etFirstName.getText().toString().trim();
				last_name = etLastName.getText().toString().trim();
				birthdate = bBirthday.getText().toString().trim();
				
				if(!email.contentEquals("") && !password.contentEquals("") && !first_name.contentEquals("") && !last_name.contentEquals("") && !birthdate.contentEquals("") && !gender.contentEquals("")){
					if (password.length() < 6){
						showErrorAlertDialog(getString(R.string.Passwords_need_to_be_at_least_6_characters_long));
					} else if (first_name.length() > 60){
						showErrorAlertDialog(getString(R.string.Firstname_need_to_be_no_more_than_60_characters_long));
					} else if (last_name.length() > 60){
						showErrorAlertDialog(getString(R.string.Lastname_need_to_be_no_more_than_60_characters_long));
					} else if (!checkEmailValidation(email)){
						showErrorAlertDialog(getString(R.string.Invalid_Email_Address_Format));
					} else if (!checkDateValidation(birthdate)){
						showErrorAlertDialog(getString(R.string.Invalid_Birthday_Date_Format));
					} else {
						RestClient result = null;
						try {
							result = new Rest.requestBody().execute(Rest.USER, Rest.OTOKE + Rest.accessCode2, Rest.POST, "7", 
									"email", email, "password", RetrieveData.hash(password).toLowerCase(), "firstname", first_name,
									"lastname", last_name, "date_of_birth", birthdate, "gender", gender, "school_id", String.valueOf(schoolid)).get();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Log.i("create account result", result.getResponse());
								
						if (result.getResponseCode() == 201) {
							showAlertDialog();
						} else if (result.getResponseCode() == 403){
							showErrorAlertDialog(getString(R.string.Registration_failed_The_user_does_not_satisfy_the_age_requirements_of_the_app));
						} else if (result.getResponseCode() == 409){
							showErrorAlertDialog(getString(R.string.Registration_failed_The_email_has_been_used_in_another_account));
						} else if (result.getResponseCode() == 409 && result.getResponse().contains("facebook_uid")){
							showErrorAlertDialog(getString(R.string.Registration_failed_Facebook_account_already_has_an_account_on_OOHLALA));
						} 
					}
				} else {
					showErrorAlertDialog(getString(R.string.Incomplete_Field));
				}
			}
		});
		
		// Spinner Gender
		bGender = (Button) findViewById(R.id.bGender);
		bGender.setTypeface(Fonts.getOpenSansRegular(Register.this));
		bGender.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	etPassword.clearFocus();
            	
            	final CharSequence[] items = {getString(R.string.Male), getString(R.string.Female), getString(R.string.Other), getString(R.string.Cancel)};
				
				AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
				builder.setTitle(getString(R.string.Gender));
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	switch(item){
				    	case 0:
				    		gender = "M";
				    		bGender.setText(getString(R.string.Male));
				    		break;
				    	case 1:
				    		gender = "F";
				    		bGender.setText(getString(R.string.Female));
				    		break;
				    	case 2:
				    		gender = "O";
				    		bGender.setText(getString(R.string.Other));
				    		break;
				    	case 3:
				    		
				    		break;
				    	}
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
            }
        });

		// get the current date
		final Calendar c = Calendar.getInstance();
		YearNow = c.get(Calendar.YEAR);
		MonthNow = c.get(Calendar.MONTH);
		DayNow = c.get(Calendar.DAY_OF_MONTH);
		
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

	// updates the date in the TextView
	private void updateDisplay() {
		if (mDay < 10 && mMonth+1 < 10){
			bBirthday.setText(new StringBuilder()
			// Month is 0 based so add 1
			.append(mYear).append("-0")
			.append(mMonth + 1).append("-0")
			.append(mDay).append(""));
		} else if (mDay < 10){
			bBirthday.setText(new StringBuilder()
			// Month is 0 based so add 1
			.append(mYear).append("-")
			.append(mMonth + 1).append("-0")
			.append(mDay).append(""));
		} else if (mMonth+1 < 10){
			bBirthday.setText(new StringBuilder()
			// Month is 0 based so add 1
			.append(mYear).append("-0")
			.append(mMonth + 1).append("-")
			.append(mDay).append(""));
		} else {
			bBirthday.setText(new StringBuilder()
			// Month is 0 based so add 1
			.append(mYear).append("-")
			.append(mMonth + 1).append("-")
			.append(mDay).append(""));
		}
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			if(isDateBefore(view)){
				mYear = year;
				mMonth = monthOfYear;
				mDay = dayOfMonth;
				updateDisplay();
			} else {
				showErrorAlertDialog(getString(R.string.Pick_Birthday_Error));
			}
		}
		
		private boolean isDateBefore(DatePicker tempView) {
	        Calendar mCalendar = Calendar.getInstance();
	        mCalendar.set(Calendar.YEAR, YearNow - 13);
	        
	        Calendar tempCalendar = Calendar.getInstance();
	        tempCalendar.set(tempView.getYear(), tempView.getMonth(), tempView.getDayOfMonth(), 0, 0, 0);
	        if(tempCalendar.before(mCalendar))
	            return true;
	        else 
	            return false;
	    }
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, YearNow - 13, MonthNow, DayNow);
		}
		return null;
	}
	
	public boolean checkEmailValidation(String email){
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher m = p.matcher(email);
		return m.matches();
	}
	
	public boolean checkDateValidation(String date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date testdate = null;
		try {
			testdate = sdf.parse(date);
			if (sdf.format(testdate).equals(date)){
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	private void showAlertDialog() {
		// TODO Auto-generated method stub
    	AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle(getString(R.string.Registration_Successful));
		alert.setMessage(getString(R.string.Check_your_email_to_verify_your_account));
		alert.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				/*
				Bundle extras = new Bundle();
				extras.putString("registered_email", email);
				Intent i = new Intent(getApplicationContext(), CheckEmail.class);
				i.putExtras(extras);				
				startActivity(i);
				*/
				trialLogin();
			}
		});
		
		alert.show();
	}
	
	public void trialLogin() {
		// TODO Auto-generated method stub
		RestClient result = null;
		try {
			result = new Rest.renewSession().execute(Rest.RENEW_SESSION, Rest.OTOKE + Rest.accessCode + email + ":" + RetrieveData.hash(password).toLowerCase(), Rest.POST).get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (result.getResponseCode() == 201) {
			RestClient result3 = null;
			try {
				result3 = new request().execute(Rest.USER, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (result3 != null){
				Profile.setProfile(result3.getResponse(), RetrieveData.hash(password).toLowerCase());
				CacheInternalStorage.cacheUserLoginInfo(new UserLoginInfo(email, RetrieveData.hash(password).toLowerCase(), false), getApplicationContext()); //cache user info
					
				TaskQueue.start();
				TaskQueueImage.start(getApplicationContext());
				TaskQueueImage2.start();
				
				CacheInternalStorage.cacheUserFirstLogin(new UserFirstLogin(email, false, true, true), getApplicationContext()); //cache user first login
				Intent i = new Intent(getApplicationContext(), InAppTour.class);
				startActivity(i);
			} else {
				trialLogin();
			}
		}
	}
	
	public void showErrorAlertDialog(String error_message) {
		// TODO Auto-generated method stub
    	AlertDialog alert = new AlertDialog.Builder(Register.this).create();
		alert.setTitle(getString(R.string.Registration_Error));
		alert.setMessage(error_message);
		alert.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		alert.show();
	}
	
	public String formatDate(String date){
		Date testdate;
		try {
			testdate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
			String newdate = new SimpleDateFormat("yyyy-MM-dd").format(testdate);
			return newdate;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private void showAlertDialog2() {
		// TODO Auto-generated method stub
    	AlertDialog alert = new AlertDialog.Builder(Register.this).create();
		alert.setTitle(getString(R.string.Registration_through_Facebook_is_successful));
		alert.setMessage(getString(R.string.Check_your_email_to_verify_your_account));
		alert.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				/*
				Bundle extras = new Bundle();
				extras.putString("registered_email", email);
				Intent i = new Intent(getApplicationContext(), CheckEmail.class);
				i.putExtras(extras);				
				startActivity(i);
				*/
				
				trialLogin2();
			}
		});
		
		alert.show();
	}
	
	public void trialLogin2() {
		// TODO Auto-generated method stub
		RestClient result2 = null;
		try {
			result2 = new Rest.renewSession().execute(Rest.RENEW_SESSION, Rest.OTOKE + Rest.accessCode + email + ":" + userPW, Rest.POST).get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (result2.getResponseCode() == 201) {
			RestClient result4 = null;
			try {
				result4 = new request().execute(Rest.USER, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (result4 != null){
				Profile.setProfile(result4.getResponse(), userPW);
				CacheInternalStorage.cacheUserLoginInfo(new UserLoginInfo(email, userPW, true), getApplicationContext()); //cache user info
				
				TaskQueue.start();
				TaskQueueImage.start(getApplicationContext());
				TaskQueueImage2.start();
				
				CacheInternalStorage.cacheUserFirstLogin(new UserFirstLogin(email, false, true, true), getApplicationContext()); //cache user first login
				Intent i = new Intent(getApplicationContext(), InAppTour.class);
				startActivity(i);
			} else {
				trialLogin2();
			}
		}
	}
	
	public void setUserPW(String fb_uid) {
		// TODO Auto-generated method stub
    	RestClient result = null;
 		try {
 			result = new Rest.request().execute(Rest.SCHOOL + "?email=" + email, Rest.OTOKE + Rest.accessCode2, Rest.GET).get();
 		} catch (InterruptedException e1) {
 			// TODO Auto-generated catch block
 			e1.printStackTrace();
 		} catch (ExecutionException e1) {
 			// TODO Auto-generated catch block
 			e1.printStackTrace();
 		}
 		Log.i("set userPW: ", result.getResponse());
		
 		JSONObject user_info = null;
 		int user_id = -1;
 		try {
 			user_info = (new JSONObject(result.getResponse())).getJSONObject("user_info");
 			user_id = (new JSONObject(result.getResponse())).getJSONObject("user_info").getInt("user_id");
 		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	 	if (user_id != -1) {
			String user_fb_token = null;
			try {
				user_fb_token = user_info.getString("fb_token");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			userPW = RetrieveData.hash(email + user_fb_token + fb_uid).trim();
			Log.i("userPW: ", userPW);
		}
	}
	
    public void showErrorAlertDialog2(String error_message) {
		// TODO Auto-generated method stub
    	AlertDialog alert = new AlertDialog.Builder(Register.this).create();
		alert.setTitle(getString(R.string.Registration_Error));
		alert.setMessage(error_message);
		alert.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		
		alert.show();
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
    
    private void updateView() {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
        	// Check for publish permissions    
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(PERMISSIONS, permissions)) {
                Session.NewPermissionsRequest newPermissionsRequest = new Session
                        .NewPermissionsRequest(this, PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
            }
            
        	makeMeRequest(session);
        	
        	//bLoginFacebook.setText(R.string.SIGN_UP_WITH_FACEBOOK);
        	bLoginFacebook.setOnClickListener(new OnClickListener() {
                public void onClick(View view) { onClickLogout(); }
            });
        } else {
        	//bLoginFacebook.setText(R.string.SIGN_UP_WITH_FACEBOOK);
        	bLoginFacebook.setOnClickListener(new OnClickListener() {
                public void onClick(View view) { onClickLogin(); }
            });
        }
    }
    
    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a 
        // new callback to handle the response.
        Request request = Request.newMeRequest(session, 
                new Request.GraphUserCallback() {
            public void onCompleted(GraphUser user, Response response) {
                // If the response is successful
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                    	email = user.getProperty("email").toString();
                    	
                        fb_uid = user.getId();
                        Log.i ("facebook uid: ", fb_uid.toString());
                        first_name = user.getFirstName();
        				last_name = user.getLastName();
        				
        				if (user.getBirthday() != null){
        					birthdate = formatDate(user.getBirthday());
        					//Log.i ("facebook info birthday: ", birthdate);
        				}

        				gender = "M";
        				if (user.getProperty("gender").toString().contentEquals("male")){
        					gender = "M";
        				} else if (user.getProperty("gender").toString().contentEquals("female")){
        					gender = "F";
        				}
        				//Log.i ("facebook info gender: ", gender);
        				
        				/*
        				email_is_verified = 1;
        				if ((Boolean) user.getProperty("verified")){
        					email_is_verified = 1;
        				} else {
        					email_is_verified = 0;
        				}
        				*/
        				//Log.i ("facebook info verified: ", String.valueOf(email_is_verified));
        				
        				mHandler.post(new Runnable() {
        		            public void run() {
        		            	RestClient result = null;
        						try {
        							result = new Rest.requestBody().execute(Rest.USER, Rest.OTOKE + Rest.accessCode2, Rest.POST, "7", 
        									"email", email, "firstname", first_name, "lastname", last_name, 
        									"date_of_birth", birthdate, "gender", gender, "school_id", String.valueOf(schoolid),
        									"facebook_uid", fb_uid).get();
        						} catch (InterruptedException e1) {
        							// TODO Auto-generated catch block
        							e1.printStackTrace();
        						} catch (ExecutionException e1) {
        							// TODO Auto-generated catch block
        							e1.printStackTrace();
        						}
        						Log.i("create account facebook result", result.getResponse());
        								
        						if (result.getResponseCode() == 201) {
        							setUserPW(fb_uid);
        							showAlertDialog2();
        						} else if (result.getResponseCode() == 403 && result.getResponse().contains("age")){
        							onClickLogout();
        							showErrorAlertDialog2(getString(R.string.Registration_failed_The_user_does_not_satisfy_the_age_requirements_of_the_app));
        						} else if (result.getResponseCode() == 409 && result.getResponse().contains("email")){
        							onClickLogout();
        							showErrorAlertDialog2(getString(R.string.Registration_failed_The_email_has_been_used_in_another_account));
        						} else if (result.getResponseCode() == 409 && result.getResponse().contains("facebook_uid")){
        							onClickLogout();
        							showErrorAlertDialog2(getString(R.string.Registration_failed_Facebook_account_already_has_an_account_on_OOHLALA));
        						} 
        		            }
        				});
                    }
                }
                if (response.getError() != null) {
                    // Handle errors, will do so later.
                	Log.i ("facebook response errorMessage: ", response.getError().getErrorMessage());
                	makeMeRequest(session);
                }
            }
        });
        request.executeAsync();
    } 
    
    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }

    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }
	
	private class SessionStatusCallback implements Session.StatusCallback {
        public void call(Session session, SessionState state, Exception exception) {
            updateView();
        }
    }
	
	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
	    for (String string : subset) {
	        if (!superset.contains(string)) {
	            return false;
	        }
	    }
	    return true;
	}
	
}
