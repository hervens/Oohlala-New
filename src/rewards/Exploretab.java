package rewards;

import inbox.Inbox;

import java.util.Hashtable;

import network.ErrorCodeParser;
import network.RetrieveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.UserProfile;
import rewards.Rewards;
import studentsnearby.studentsNearby;
import user.Profile;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import campusgame.CampusGame;
import campusmap.CampusMap;
import campuswall.CampusWall;

import com.gotoohlala.DialogConnectThread;
import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.TopMenuNavbar;
import com.gotoohlala.UnreadNumCheck;

import datastorage.Fonts;

public class Exploretab extends FrameLayout {
	
	TabHost tabHost;
	RelativeLayout rlFilter, rlHeader;
	TextView headerExplore, filterHeader, filterRow1, filterRow2, filterRow3, filterRow4, filterFooter;
	int pos = 0;
	
	View v = null;
	 
	public TopMenuNavbar TopMenuNavbar;
	
	public Exploretab(Context context, Bundle instance) {
		super(context);
		
		final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
		v = (TabHost) mLayoutInflater.inflate(R.layout.exploretab, null);  
	    addView(v);  

	    TopMenuNavbar = (TopMenuNavbar) v.findViewById(R.id.bTopMenuNavBar);
	    rlHeader = (RelativeLayout) v.findViewById(R.id.rlHeader);
	    
		tabHost = (TabHost) v.findViewById(R.id.thSocial);
		
		LocalActivityManager mLocalActivityManager = new LocalActivityManager((Activity) getContext(), false);
		mLocalActivityManager.dispatchCreate(instance);
		tabHost.setup(mLocalActivityManager);
		
		Intent intent = new Intent().setClass((Activity) getContext(), Explore.class);
		TabSpec spec = tabHost.newTabSpec("Explore").setIndicator("",
				getResources().getDrawable(R.drawable.ic_launcher)).setContent(intent);

		tabHost.addTab(spec);
		
		intent = new Intent().setClass((Activity) getContext(), CampusMap.class);
		spec = tabHost.newTabSpec("CampusMap").setIndicator("",
				getResources().getDrawable(R.drawable.ic_launcher)).setContent(intent);

		tabHost.addTab(spec);
		
		intent = new Intent().setClass((Activity) getContext(), CampusClubs.class);
		spec = tabHost.newTabSpec("CampusClubs").setIndicator("",
				getResources().getDrawable(R.drawable.ic_launcher)).setContent(intent);

		tabHost.addTab(spec);
		
		intent = new Intent().setClass((Activity) getContext(), Rewards.class);
		spec = tabHost.newTabSpec("Rewards").setIndicator("",
				getResources().getDrawable(R.drawable.ic_launcher)).setContent(intent);

		tabHost.addTab(spec);
		
		rlFilter = (RelativeLayout) v.findViewById(R.id.rlFilter);
		
		filterHeader = (TextView) v.findViewById(R.id.filterHeader);
		filterHeader.setTypeface(Fonts.getOpenSansBold(v.getContext()));
		filterHeader.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rlFilter.setVisibility(View.GONE);
				
				filterHeader.setBackgroundDrawable(null);
				filterRow1.setBackgroundDrawable(null);
				filterRow2.setBackgroundDrawable(null);
				filterRow3.setBackgroundDrawable(null);
				filterRow4.setBackgroundDrawable(null);
				filterFooter.setBackgroundDrawable(null);
			}
	    }); 
		
		filterRow1 = (TextView) v.findViewById(R.id.filterRow1);
		filterRow1.setTypeface(Fonts.getOpenSansBold(v.getContext()));
		filterRow1.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				headerExplore.setText(getContext().getString(R.string.EXPLORE));
				filterHeader.setText(getContext().getString(R.string.EXPLORE));
				pos = 0;
				tabHost.setCurrentTab(pos);
				rlFilter.setVisibility(View.GONE);	
				
				filterHeader.setBackgroundDrawable(null);
				filterRow1.setBackgroundDrawable(null);
				filterRow2.setBackgroundDrawable(null);
				filterRow3.setBackgroundDrawable(null);
				filterRow4.setBackgroundDrawable(null);
				filterFooter.setBackgroundDrawable(null);
			}
	    }); 
		
		filterRow2 = (TextView) v.findViewById(R.id.filterRow2);
		filterRow2.setTypeface(Fonts.getOpenSansBold(v.getContext()));
		filterRow2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				headerExplore.setText(getContext().getString(R.string.CAMPUS_BUILDINGS));
				filterHeader.setText(getContext().getString(R.string.CAMPUS_BUILDINGS));
				pos = 1;
				tabHost.setCurrentTab(pos);
				rlFilter.setVisibility(View.GONE);	
				
				filterHeader.setBackgroundDrawable(null);
				filterRow1.setBackgroundDrawable(null);
				filterRow2.setBackgroundDrawable(null);
				filterRow3.setBackgroundDrawable(null);
				filterRow4.setBackgroundDrawable(null);
				filterFooter.setBackgroundDrawable(null);
			}
	    }); 
		
		filterRow3 = (TextView) v.findViewById(R.id.filterRow3);
		filterRow3.setTypeface(Fonts.getOpenSansBold(v.getContext()));
		filterRow3.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				headerExplore.setText(getContext().getString(R.string.CAMPUS_CLUBS));
				filterHeader.setText(getContext().getString(R.string.CAMPUS_CLUBS));
				pos = 2;
				tabHost.setCurrentTab(pos);
				rlFilter.setVisibility(View.GONE);	
				
				filterHeader.setBackgroundDrawable(null);
				filterRow1.setBackgroundDrawable(null);
				filterRow2.setBackgroundDrawable(null);
				filterRow3.setBackgroundDrawable(null);
				filterRow4.setBackgroundDrawable(null);
				filterFooter.setBackgroundDrawable(null);
			}
	    }); 
		
		filterRow4 = (TextView) v.findViewById(R.id.filterRow4);
		filterRow4.setTypeface(Fonts.getOpenSansBold(v.getContext()));
		filterRow4.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				headerExplore.setText(getContext().getString(R.string.DEALS));
				filterHeader.setText(getContext().getString(R.string.DEALS));
				pos = 3;
				tabHost.setCurrentTab(pos);
				rlFilter.setVisibility(View.GONE);
				
				filterHeader.setBackgroundDrawable(null);
				filterRow1.setBackgroundDrawable(null);
				filterRow2.setBackgroundDrawable(null);
				filterRow3.setBackgroundDrawable(null);
				filterRow4.setBackgroundDrawable(null);
				filterFooter.setBackgroundDrawable(null);
			}
	    }); 
		
		filterFooter = (TextView) v.findViewById(R.id.filterFooter);
		filterFooter.setTypeface(Fonts.getOpenSansBold(v.getContext()));
		filterFooter.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rlFilter.setVisibility(View.GONE);	
				
				filterHeader.setBackgroundDrawable(null);
				filterRow1.setBackgroundDrawable(null);
				filterRow2.setBackgroundDrawable(null);
				filterRow3.setBackgroundDrawable(null);
				filterRow4.setBackgroundDrawable(null);
				filterFooter.setBackgroundDrawable(null);
			}
	    }); 
		
		headerExplore = (TextView) v.findViewById(R.id.headerExplore);
		headerExplore.setTypeface(Fonts.getOpenSansBold(v.getContext()));
		headerExplore.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				/*
				 * Hide the deals tab for now, maybe will need it for later versions
				 * 
				rlFilter.setVisibility(View.VISIBLE);	
				
				filterHeader.setBackgroundResource(R.drawable.filter_header);
				filterRow1.setBackgroundResource(R.drawable.filter_row);
				filterRow2.setBackgroundResource(R.drawable.filter_row);
				filterRow3.setBackgroundResource(R.drawable.filter_row);
				filterRow4.setBackgroundResource(R.drawable.filter_row);
				filterFooter.setBackgroundResource(R.drawable.filter_footer);
				*/
			}
	    });
		
		switch(pos){
		case 0:
			headerExplore.setText(getContext().getString(R.string.EXPLORE));
			filterHeader.setText(getContext().getString(R.string.EXPLORE));
			tabHost.setCurrentTab(pos);
			break;
		case 1:
			headerExplore.setText(getContext().getString(R.string.CAMPUS_BUILDINGS));
			filterHeader.setText(getContext().getString(R.string.CAMPUS_BUILDINGS));
			tabHost.setCurrentTab(pos);
			break;
		case 2:
			headerExplore.setText(getContext().getString(R.string.CAMPUS_CLUBS));
			filterHeader.setText(getContext().getString(R.string.CAMPUS_CLUBS));
			tabHost.setCurrentTab(pos);
			break;
		case 3:
			headerExplore.setText(getContext().getString(R.string.DEALS));
			filterHeader.setText(getContext().getString(R.string.DEALS));
			tabHost.setCurrentTab(pos);
			break;
		}
	}
	
}
