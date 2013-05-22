package launchOohlala;

import inbox.Inbox;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rewards.Rewards;
import user.Profile;

import network.RetrieveData;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import campusmap.CampusMap;

import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.UnreadNumCheck;

import datastorage.CacheInternalStorage;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.UserLoginInfo;
import datastorage.Rest.request;

public class Recover extends Activity {
	
	String email;
	
	BroadcastReceiver brLogout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recover);

		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
		
		//content
		Bundle b = this.getIntent().getExtras();
		email = b.getString("email").toLowerCase();
		
		final EditText etEmail = (EditText) findViewById(R.id.etEmail);
		Button bSend = (Button) findViewById(R.id.bSend);
		
		etEmail.setText(email);
		
		bSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	String userEmail = etEmail.getText().toString().trim();
            
            	if (userEmail.length() > 0 && checkEmailValidation(userEmail)){
            		RestClient result = null;
    				try {
    					result = new Rest.requestBody().execute(Rest.APP_CONFIG, Rest.OTOKE + Rest.accessCode2, Rest.PUT, "2", 
								"reset_password", "1", "email", userEmail).get();
    				} catch (InterruptedException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				} catch (ExecutionException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
    				Log.i("reset password", result.getResponse());
	
		            if (result.getResponseCode() == 200){
		            	showEmailSentDialog();
		            } else if (result.getResponseCode() == 404){
		            	Toast.makeText(getBaseContext(), getString(R.string.No_such_account_exists_with_the_specified_email), Toast.LENGTH_SHORT).show();
		            } else if (result.getResponseCode() == 403){
		            	Toast.makeText(getBaseContext(), getString(R.string.Can_not_reset_password_for_a_Facebook_Connect_account), Toast.LENGTH_SHORT).show();
		            } 
				} else {
					Toast.makeText(getBaseContext(), getString(R.string.Invalid_School_Email), Toast.LENGTH_SHORT).show();
				}
            }
        }); 
		
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
	
	public boolean checkEmailValidation(String email){
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher m = p.matcher(email);
		return m.matches();
	}
	
	public void showEmailSentDialog(){
		AlertDialog alert = new AlertDialog.Builder(Recover.this).create();
		alert.setMessage(getString(R.string.An_email_has_been_sent_with_a_link_to_reset_your_password));
		alert.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		alert.show();
	}

}
