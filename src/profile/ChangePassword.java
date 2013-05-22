package profile;

import inbox.Inbox;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import launchOohlala.Register;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rewards.Rewards;
import user.Profile;

import network.RetrieveData;
import android.app.Activity;
import android.content.Intent;
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
import datastorage.Fonts;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.UserLoginInfo;
import datastorage.Rest.request;

public class ChangePassword extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changepassword);
		
		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(ChangePassword.this));
		
		//content
		final EditText etCurrentPassword = (EditText) findViewById(R.id.etCurrentPassword);
		final EditText etNewPassword = (EditText) findViewById(R.id.etNewPassword);
		final EditText etNewPasswordAgain = (EditText) findViewById(R.id.etNewPasswordAgain);
		Button bSave = (Button) findViewById(R.id.bSave);
		
		etCurrentPassword.setTypeface(Fonts.getOpenSansRegular(ChangePassword.this));
		etNewPassword.setTypeface(Fonts.getOpenSansRegular(ChangePassword.this));
		etNewPasswordAgain.setTypeface(Fonts.getOpenSansRegular(ChangePassword.this));
		
		bSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	String currentPassword = etCurrentPassword.getText().toString().trim();
            	String newPassword = etNewPassword.getText().toString().trim();
            	String reNewPassword = etNewPasswordAgain.getText().toString().trim();
            	
            	if (newPassword.contentEquals(reNewPassword)){
            		if (newPassword.length() < 6){
						Toast.makeText(getBaseContext(),
								getString(R.string.Passwords_need_to_be_at_least_6_characters_long),
								Toast.LENGTH_SHORT).show();
					} else {
						RestClient result = null;
						try {
							result = new Rest.requestBody().execute(Rest.USER, Rest.OSESS + Profile.sk, Rest.PUT, "2", "current_password", RetrieveData.hash(currentPassword), "new_password", RetrieveData.hash(newPassword)).get();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		            	
		            	if (result.getResponseCode() == 200){
		            		Toast.makeText(getBaseContext(),
									getString(R.string.Change_password_successfully),
									Toast.LENGTH_SHORT).show();
		            		
		            		RestClient result2 = null;
		    				try {
		    					result2 = new Rest.renewSession().execute(Rest.RENEW_SESSION, Rest.OTOKE + Rest.accessCode + Profile.email + ":" + RetrieveData.hash(newPassword), Rest.POST).get();
		    				} catch (InterruptedException e1) {
		    					// TODO Auto-generated catch block
		    					e1.printStackTrace();
		    				} catch (ExecutionException e1) {
		    					// TODO Auto-generated catch block
		    					e1.printStackTrace();
		    				}
	    				
		    				if (result2.getResponseCode() == 201){
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
		    						Profile.updateProfile(result3.getResponse(), RetrieveData.hash(newPassword));
		    						CacheInternalStorage.cacheUserLoginInfo(new UserLoginInfo(Profile.email, RetrieveData.hash(newPassword), false), ChangePassword.this); //cache the new changed password userLogin info
		    					
		    						Intent i = new Intent(getBaseContext(), OohlalaMain.class);
					            	startActivity(i);
		    					}
		    				}
		            	} else if (result.getResponseCode() == 403 && result.getResponse().contains("password")){
		            		Toast.makeText(getBaseContext(), getString(R.string.Old_password_is_incorrect), Toast.LENGTH_SHORT).show();
		            	} else if (result.getResponseCode() == 403 && result.getResponse().contains("is_facebook")){
		            		Toast.makeText(getBaseContext(), getString(R.string.Facebook_connect_account_password_can_not_be_changed), Toast.LENGTH_SHORT).show();
		            	} 
					}
	            } else {
	            	Toast.makeText(getBaseContext(), getString(R.string.Retyped_new_password_does_not_match), Toast.LENGTH_SHORT).show();
	            }  	 
            }
        }); 
	}

	public void onResume() {
		super.onResume();
		
	}

}
