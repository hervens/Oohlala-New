package campusgame;

import inbox.Inbox;

import java.util.Hashtable;

import network.RetrieveData;

import org.json.JSONException;
import org.json.JSONObject;

import session.SessionStore;

import campusmap.CampusMap;
import campuswall.CampusWallImage;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.UnreadNumCheck;

import datastorage.FB;
import datastorage.FacebookReauthorize;
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

public class CampusGameShowRule extends Activity {

	String game_rules;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campusgamerules);
		
		Bundle b = this.getIntent().getExtras();
		game_rules = b.getString("game_rules");

		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
        
        //content
        TextView tvGameRules = (TextView) findViewById(R.id.tvGameRules);
        Button bAgree = (Button) findViewById(R.id.bAgree);
        
        tvGameRules.setText(game_rules);
        bAgree.setVisibility(View.GONE);
        bAgree.setClickable(false);     
	}
	
	public void onResume() {
		super.onResume();
		
	}

}
