package rewards;

import inbox.Inbox;

import java.util.Hashtable;

import network.RetrieveData;

import org.json.JSONException;
import org.json.JSONObject;

import profile.UserProfile;
import user.Profile;
import campusmap.CampusMap;

import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.UnreadNumCheck;

import datastorage.StringLanguageSelector;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class RewardsCategoryStores extends TabActivity {
	
	private String name, category_id; 
	private double lat, longi;
	private TabHost tabHost;
	private RadioGroup rg;
	private TextView tvRewardsCategoryStoresName;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rewardscategorystores);
		
		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
			
		//content
		Bundle b = this.getIntent().getExtras();
		name = StringLanguageSelector.retrieveString(b.getString("name"));
		category_id = b.getString("category_id");
		lat = b.getDouble("lat");
		longi = b.getDouble("longi");
		
		tvRewardsCategoryStoresName = (TextView) findViewById(R.id.tvRewardsCategoryStoresName);
		tvRewardsCategoryStoresName.setText(name);
        
        tabHost = getTabHost();
		Intent intent = new Intent().setClass(this, RewardsVenues.class);
		Bundle extras = new Bundle();
		extras.putString("category_id", category_id);
		extras.putString("card_code_name", name);
		extras.putDouble("lat", lat);
		extras.putDouble("longi", longi);
		intent.putExtras(extras);
		
		TabSpec spec = tabHost.newTabSpec("Venues").setIndicator("",
				getResources().getDrawable(R.drawable.ic_launcher)).setContent(intent);

		tabHost.addTab(spec);
		
		/*
		intent = new Intent().setClass(this, RewardsEvents.class);
		spec = tabHost.newTabSpec("Events").setIndicator("",
				getResources().getDrawable(R.drawable.ic_launcher)).setContent(intent);

		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, RewardsMap.class);
		spec = tabHost.newTabSpec("Map").setIndicator("",
				getResources().getDrawable(R.drawable.ic_launcher)).setContent(intent);

		tabHost.addTab(spec);
		*/
		
		tabHost.setCurrentTab(0);
		
		rg = (RadioGroup) findViewById(R.id.rewardsTabs);
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, final int checkedId) {
				switch (checkedId) {
				case R.id.venues:
					getTabHost().setCurrentTab(0);
					break;
				/*
				case R.id.events:
					getTabHost().setCurrentTab(1);
					break;
				case R.id.map:
					getTabHost().setCurrentTab(2);
					break;
				*/
				}
			}
		});
	}

	public void onResume() {
		super.onResume();
		
	}
	
}
