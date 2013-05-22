package campusgame;

import inbox.Inbox;

import java.util.Hashtable;

import network.RetrieveData;

import org.json.JSONException;
import org.json.JSONObject;

import campusmap.CampusMap;
import campuswall.CampusWallImage;

import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.UnreadNumCheck;

import datastorage.DeviceDimensions;
import datastorage.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CampusGameInfo extends Activity {
	
	String caption_facebookShare, message_facebookShare, icon_url_facebookShare, url_facebookShare, description_facebookShare, url_name_facebookShare, game_video_url, game_image_url, game_name, game_rules, description;
	int event_id_facebookShare, time_until_start;
	
	ImageView ivGameImage;
	
	int start, end, user_submissions, max_submissions, game_type;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campusgameinfo);
		
		Bundle b = this.getIntent().getExtras();
		event_id_facebookShare = b.getInt("event_id_facebookShare");
		int now = (int) (System.currentTimeMillis()/1000);
		time_until_start = b.getInt("start") - now;
		caption_facebookShare = b.getString("caption_facebookShare");
		message_facebookShare = b.getString("message_facebookShare");
		icon_url_facebookShare = b.getString("icon_url_facebookShare");
		url_facebookShare = b.getString("url_facebookShare");
		description_facebookShare = b.getString("description_facebookShare");
		url_name_facebookShare = b.getString("url_name_facebookShare");
		game_video_url = b.getString("game_video_url");
		game_image_url = b.getString("game_image_url");
		game_name = b.getString("game_name");
		game_rules = b.getString("game_rules");
		description = b.getString("description");
		
		if (b.containsKey("game_type")){
			game_type = b.getInt("game_type");
			start = b.getInt("start");
			end = b.getInt("end");
			user_submissions = b.getInt("user_submissions");
			max_submissions = b.getInt("max_submissions");
		}
		
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
        Button bJoinTheGame = (Button) findViewById(R.id.bJoinTheGame);
        
        tvGameName.setText(game_name);
       
        ivGameImage.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (game_video_url.contains("youtube")){
					Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
					websiteIntent.setData(Uri.parse(game_video_url));
					startActivity(websiteIntent);
				}
			}
		});
        
        tvGameDescr.setText(description);
        
        bJoinTheGame.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Bundle extras = new Bundle();
				extras.putString("caption_facebookShare", caption_facebookShare);
				extras.putString("message_facebookShare", message_facebookShare);
				extras.putString("icon_url_facebookShare", icon_url_facebookShare);
				extras.putString("url_facebookShare", url_facebookShare);
				extras.putString("description_facebookShare", description_facebookShare);
				extras.putString("url_name_facebookShare", url_name_facebookShare);
				extras.putInt("event_id_facebookShare", event_id_facebookShare);
				extras.putString("game_video_url", game_video_url);
				extras.putString("game_image_url", game_image_url);
				extras.putString("game_name", game_name);
				extras.putInt("time_until_start", time_until_start);
				extras.putString("game_rules", game_rules);
				extras.putString("description", description);
				
				if (game_type == 3){
					extras.putInt("start", start);
					extras.putInt("end", end);
    				extras.putInt("user_submissions", user_submissions);
    				extras.putInt("max_submissions", max_submissions);
					extras.putInt("game_type", game_type);
				}
				
				Intent i = new Intent(getBaseContext(), CampusGameRules.class);
				i.putExtras(extras);
				startActivity(i);
			}
		});
        
        new loadGameImage().execute();
	}
	
	private class loadGameImage extends AsyncTask<Void, Void, Void> {
    	
		@Override
		protected Void doInBackground(Void... params) {
			
			runOnUiThread(new Runnable() {
				public void run() {
					Bitmap image_bitmap = ImageLoader.thumbImageStoreAndLoad(game_image_url, CampusGameInfo.this);
					if (image_bitmap != null){
						//int picWidth = DeviceDimensions.getWidth(getApplicationContext());
						//int picHeight = (int) (((double)image_bitmap.getHeight()/image_bitmap.getWidth())*picWidth);
						//ivGameImage.setImageBitmap(Bitmap.createScaledBitmap(image_bitmap, picWidth, picHeight, false));
						ivGameImage.setImageBitmap(image_bitmap);
					}
				}
			});
			
			return null;
		}
    }
	
	public void onResume() {
		super.onResume();
		
	}

}
