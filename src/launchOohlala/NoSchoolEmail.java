package launchOohlala;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gotoohlala.R;
import com.gotoohlala.R.drawable;
import com.gotoohlala.R.id;
import com.gotoohlala.R.layout;

import datastorage.Fonts;
import datastorage.Rest;
import datastorage.RestClient;

import network.RetrieveData;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class NoSchoolEmail extends Activity{
	
	String email;
	EditText etNoSchoolEmailName, etNoSchoolEmail;
	Button bNoSchoolEmail;
	String[] schoolname;
	int[] schoolid;
	TextView tvTitle;
	
	BroadcastReceiver brLogout;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.noschoolemail);
		
		Bundle b = this.getIntent().getExtras();
		email = b.getString("email");
		
		tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setTypeface(Fonts.getOpenSansBold(NoSchoolEmail.this));
		
		etNoSchoolEmailName = (EditText) findViewById(R.id.etNoSchoolEmailName);
		etNoSchoolEmailName.setTypeface(Fonts.getOpenSansRegular(NoSchoolEmail.this));
		
		etNoSchoolEmail = (EditText) findViewById(R.id.etNoSchoolEmail);
		etNoSchoolEmail.setTypeface(Fonts.getOpenSansRegular(NoSchoolEmail.this));
		etNoSchoolEmail.setText(email);
		
		bNoSchoolEmail = (Button) findViewById(R.id.bNoSchoolEmail);
		bNoSchoolEmail.setOnClickListener(new Button.OnClickListener() {  
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String name = etNoSchoolEmailName.getText().toString().replace(" ", "%20");
				Log.i("name", name);
				
				if (name.length() < 4){
					Toast.makeText(getBaseContext(),
							getString(R.string.School_name_must_be_at_least_4_characters_long),
							Toast.LENGTH_SHORT).show();
				} else {
					RestClient result = null;
			 		try {
			 			result = new Rest.request().execute(Rest.SCHOOL + "?name=" + name + "&has_email=0", Rest.OTOKE + Rest.accessCode2, Rest.GET).get();
			 		} catch (InterruptedException e1) {
			 			// TODO Auto-generated catch block
			 			e1.printStackTrace();
			 		} catch (ExecutionException e1) {
			 			// TODO Auto-generated catch block
			 			e1.printStackTrace();
			 		}
			 		Log.i("no school email", result.getResponse());
			 		
			 		JSONArray schools = null;
			 		try {
			 			schools = new JSONArray(result.getResponse());
			 		} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			 		
			 		if (schools != null){
			 			if (schools.length() == 1){
							Log.i("school list: ", schools.toString());
							String schoolname = null;
							int schoolid = 0;
							try {
								schoolname = schools.getJSONObject(0).getString("name");
								schoolid = schools.getJSONObject(0).getInt("id");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							if (schoolname != null){
								Bundle extras = new Bundle();
								extras.putString("school_name", schoolname);
								extras.putInt("school_id", schoolid);
								extras.putString("email", email);
								
								Intent i = new Intent(getApplicationContext(), Register.class);
								i.putExtras(extras);
								startActivity(i);
							}
						} else if (schools.length() > 1){
							Log.i("school list: ", schools.toString());
							for (int i = 0; i < schools.length(); i++){
								LayoutParams paramTV = new LinearLayout.LayoutParams(
					                LayoutParams.WRAP_CONTENT,
					                LayoutParams.WRAP_CONTENT, 0.8f);
								
								TextView tv = new TextView(getBaseContext(), null, android.R.attr.textAppearanceLarge);
								tv.setGravity(Gravity.CENTER_VERTICAL);
								tv.setLayoutParams(paramTV);
								
								try {
									schoolname[i] = schools.getJSONObject(i).getString("name");
									schoolid[i] = schools.getJSONObject(i).getInt("id");
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								final String schooln = schoolname[i];
								final int schooli = schoolid[i];
								
								tv.setText(schooln);
								tv.setOnClickListener(new Button.OnClickListener() {  
	
									public void onClick(View v) {
										// TODO Auto-generated method stub
										//Log.i ("has shared: ----------- ", String.valueOf(has_shared));
										Bundle extras = new Bundle();
										extras.putString("school_name", schooln);
										extras.putInt("school_id", schooli);
										extras.putString("email", email);
										
										Intent i = new Intent(getApplicationContext(), Register.class);
										i.putExtras(extras);
										startActivity(i);
									}
					    	    }); 
								
								LayoutParams paramLL = new LinearLayout.LayoutParams(
					                LayoutParams.FILL_PARENT,
					                LayoutParams.WRAP_CONTENT, 1.0f);
								
								LinearLayout ll = new LinearLayout(getBaseContext());
								ll.setOrientation(LinearLayout.HORIZONTAL);
								ll.setPadding(10, 10, 10, 10);
								paramLL.setMargins(0, 5, 0, 0);
								ll.setLayoutParams(paramLL);
								ll.setBackgroundDrawable(getBaseContext().getResources().getDrawable(R.drawable.textview));
								ll.addView(tv);
								
								LinearLayout sv = (LinearLayout)findViewById(R.id.llScrollView2);
								if (schoolname != null){ //only when the gameName exist = have not been deleted
									sv.addView(ll);
								}
							}
						} else {
							Log.i("no school found", "------------");
							Toast.makeText(getBaseContext(),
									name + getString(R.string.is_not_a_school_we_currently_support_we_have_added_you_to_the_waitlist_and_we_will_let_you_know_when_we_bring_OOHLALA_to_your_school),
									Toast.LENGTH_SHORT).show();
						}
			 		} else {
			 			Toast.makeText(getBaseContext(),
								name + getString(R.string.is_not_a_school_we_currently_support_we_have_added_you_to_the_waitlist_and_we_will_let_you_know_when_we_bring_OOHLALA_to_your_school),
								Toast.LENGTH_SHORT).show();
			 		}
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
}
