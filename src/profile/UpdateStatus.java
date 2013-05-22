package profile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import network.GPSLocationChanged;
import network.RetrieveData;

import org.json.JSONException;
import org.json.JSONObject;

import profile.Badge;

import user.Profile;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import campuswall.CampusWallPostInterface;

import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;

import datastorage.Base64;
import datastorage.CacheInternalStorage;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;

public class UpdateStatus extends Activity {
	
	TextView tvWordsLeft;
	EditText etPostThread;
	TextView bPostThread;
	int is_anonymous, type;
	
    public static UpdateStatusInterface act;
    
    Handler mHandler = new Handler(Looper.getMainLooper());
    
    int wordsLeft;
    String looking_for;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.updatestatus);
		
		Bundle b = this.getIntent().getExtras();
		looking_for = b.getString("looking_for");
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(UpdateStatus.this));
		
		tvWordsLeft = (TextView) findViewById(R.id.tvWordsLeft);
		tvWordsLeft.setText("128");
		
		etPostThread = (EditText) findViewById(R.id.etPostThread);
		etPostThread.setText(looking_for);
		etPostThread.addTextChangedListener(new TextWatcher() {
			  public void afterTextChanged(Editable s) {
			       //do something
			  }

			  public void beforeTextChanged(CharSequence s, int start, int count, int after){
			       //do something
			  }

			  public void onTextChanged(CharSequence s, int start, int before, int count) { 
				  wordsLeft = 128 - etPostThread.getText().toString().length();
				  tvWordsLeft.setText(String.valueOf(wordsLeft));
				  
				  if (etPostThread.getText().toString().trim().length() > 0){
					  bPostThread.setBackgroundDrawable(getResources().getDrawable(R.drawable.compose_share_button_bg));
					  bPostThread.setClickable(true);
					  //bPostThread.setTextColor(getResources().getColorStateList(R.color.white1));
					  
					  if (wordsLeft < 0){
						  bPostThread.setBackgroundDrawable(getResources().getDrawable(R.drawable.compose_share_button_bg2));
						  bPostThread.setClickable(false);
					  }
				  } else {
					  bPostThread.setBackgroundDrawable(getResources().getDrawable(R.drawable.compose_share_button_bg2));
					  bPostThread.setClickable(false);
					  //bPostThread.setTextColor(getResources().getColorStateList(R.color.dimgrey3));
				  } 
			  }
		});
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
				
				InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etPostThread.getWindowToken(), 0);
			}
	    }); 

		bPostThread = (TextView) findViewById(R.id.bPostThread);
		if (etPostThread.getText().toString().trim().length() > 0){
			  bPostThread.setBackgroundDrawable(getResources().getDrawable(R.drawable.compose_share_button_bg));
			  bPostThread.setClickable(true);
		}
		bPostThread.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (etPostThread.getText().toString().trim().length() > 0){
					bPostThread.setClickable(false);

					String text = etPostThread.getText().toString();
	                
					RestClient result = null;
					try {
						result = new Rest.requestBody().execute(Rest.USER, Rest.OSESS + Profile.sk, Rest.PUT, "1", "looking_for", text).get();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Log.i("update status: ", result.getResponse());
	            	
	            	if (result.getResponseCode() == 200){
		    			mHandler.post(new Runnable() {
		            		public void run() {
    					    	Profile.updateUserProfile();
    					    }
    	    			});
		    			
		    			act.refreshAfterUpdateStatus(text);
						onBackPressed();
	            	} 
				} else {
					Toast.makeText(getApplicationContext(),
							getString(R.string.You_can_not_update_an_empty_status), 
							Toast.LENGTH_SHORT).show();
				}
			}
	    }); 
		
	}
    
}
