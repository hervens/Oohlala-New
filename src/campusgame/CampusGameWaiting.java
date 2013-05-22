package campusgame;

import inbox.Inbox;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

import network.RetrieveData;

import org.json.JSONException;
import org.json.JSONObject;

import campusmap.CampusMap;

import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.UnreadNumCheck;

import datastorage.ImageLoader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CampusGameWaiting extends Activity {
	
	TextView tvGameBeginTime;
	String game_video_url, game_image_url, game_name, game_rules, description;
	int time_until_start, event_id_facebookShare;
	
	ImageView ivGameImage;
	
	updateTimeCounter counter1;
	boolean counter1on = true;
	
	BroadcastReceiver brLogout;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campusgamewaiting);
		
		Bundle b = this.getIntent().getExtras();
		time_until_start = b.getInt("time_until_start");
		event_id_facebookShare = b.getInt("event_id_facebookShare");
		game_video_url = b.getString("game_video_url");
		game_image_url = b.getString("game_image_url");
		game_name = b.getString("game_name");
		game_rules = b.getString("game_rules");
		description = b.getString("description");
		
		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 

        //content
        TextView tvGameName = (TextView) findViewById(R.id.tvGameName);
        ivGameImage = (ImageView) findViewById(R.id.ivGameImage);
        TextView tvGameDescr = (TextView) findViewById(R.id.tvGameDescr);
        tvGameBeginTime = (TextView) findViewById(R.id.tvGameBeginTime);
        
        tvGameName.setText(game_name);
       
        ivGameImage.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
				websiteIntent.setData(Uri.parse(game_video_url));
				startActivity(websiteIntent);
			}
		});
        
        tvGameDescr.setText(description);
        
        new loadGameImage().execute();
        
        counter1 = new updateTimeCounter(time_until_start*1000, 1000);
        counter1.start(); 
        
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
	
	class updateTimeCounter extends CountDownTimer {
        public updateTimeCounter(long millisInFuture, long countDownInterval) {
          super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            //dialog.dismiss();
           // Use Intent to Navigate from this activity to another
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub
        	getWaitingTime();
        	//setupOverlayList();
        }
    }
	
	public synchronized void getWaitingTime() {
		int days = time_until_start/86400;
		
		long millis = time_until_start*1000L;
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(date);
       
		Bundle b = new Bundle();
		b.putString("timeleft", getString(R.string.Game_begins_in_) + days + getString(R.string._days_) + formattedDate);
		Message m = new Message();
		m.setData(b);
		this.updateTimeHandler.sendMessage(m);
		
		if (time_until_start <= 0){
			Bundle extras = new Bundle();
			extras.putInt("promo_event_id", event_id_facebookShare);
			extras.putString("game_rules", game_rules);
			Intent i = new Intent(CampusGameWaiting.this, GameMap.class);
			i.putExtras(extras);
			startActivity(i);
		}
			
		time_until_start--;
	}

	Handler updateTimeHandler = new Handler(){
		public synchronized void handleMessage(Message msg){
			tvGameBeginTime.setText(msg.getData().getString("timeleft"));
			//super.handleMessage(msg);
	    }
	};
	
	private class loadGameImage extends AsyncTask<Void, Void, Void> {
    	
		@Override
		protected Void doInBackground(Void... params) {
			
			runOnUiThread(new Runnable() {
				public void run() {
					ivGameImage.setImageBitmap(ImageLoader.thumbImageStoreAndLoad(game_image_url, CampusGameWaiting.this));	
				}
			});
			
			return null;
		}
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
	}

}
