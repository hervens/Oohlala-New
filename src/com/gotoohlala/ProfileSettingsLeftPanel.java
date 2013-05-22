package com.gotoohlala;

import inbox.Inbox;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import network.RetrieveData;

import org.json.JSONException;
import org.json.JSONObject;

import profile.BlockList;
import profile.ChangePassword;
import profile.ModifyProfile;

import launchOohlala.CheckEmail;
import launchOohlala.Login;
import launchOohlala.SessionEvents;
import rewards.Rewards;
import user.Profile;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import campusmap.CampusMap;

import com.facebook.Session;
import com.facebook.android.AsyncFacebookRunner;
import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.UnreadNumCheck;

import datastorage.CacheInternalStorage;
import datastorage.Fonts;
import datastorage.LoginState;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.UserLoginInfo;
import datastorage.Rest.request;

public class ProfileSettingsLeftPanel extends FrameLayout {
	
	private Handler mHandler = new Handler(Looper.getMainLooper());
	
	View v = null;
	 
	public TopMenuNavbar TopMenuNavbar;
	
	public ProfileSettingsLeftPanel(Context context) {
		super(context);
		
		final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
		v = (RelativeLayout) mLayoutInflater.inflate(R.layout.profilesettingsleftpanel, null);  
	    addView(v);  
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(getContext()));
		
		TopMenuNavbar = (TopMenuNavbar) v.findViewById(R.id.bTopMenuNavBar);

		//content
		//CheckBox cbGlobalPushNotifications = (CheckBox) findViewById(R.id.cbGlobalPushNotifications);
		Button bModifyProfile = (Button) findViewById(R.id.bModifyProfile);
		Button bChangePassword = (Button) findViewById(R.id.bChangePassword);
		Button bLogout = (Button) findViewById(R.id.bLogout);
		
		//Button bFacebookLogout = (Button) findViewById(R.id.bFacebookLogout);
		Button bBlockList = (Button) findViewById(R.id.bBlockList);
		Button bWriteAReview = (Button) findViewById(R.id.bWriteAReview);
		Button bSendFeedback = (Button) findViewById(R.id.bSendFeedback);
		//CheckBox cbShowMy = (CheckBox) findViewById(R.id.cbShowMy);
		//CheckBox cbShowPointsAnimation = (CheckBox) findViewById(R.id.cbShowPointsAnimation);
		
		bModifyProfile.setTypeface(Fonts.getOpenSansBold(getContext()));
		bModifyProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent i = new Intent(getContext(), ModifyProfile.class);
            	getContext().startActivity(i);
            }
        }); 
		
		if (Profile.isFacebook){
			bChangePassword.setVisibility(View.GONE);
		}
		bChangePassword.setTypeface(Fonts.getOpenSansBold(getContext()));
		bChangePassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent i = new Intent(getContext(), ChangePassword.class);
            	getContext().startActivity(i);
            }
        }); 
		
		bBlockList.setTypeface(Fonts.getOpenSansBold(getContext()));
		bBlockList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent i = new Intent(getContext(), BlockList.class);
            	getContext().startActivity(i);
            }
        }); 
		
		bLogout.setTypeface(Fonts.getOpenSansBold(getContext()));
		bLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	showLogoutAlertDialog();
            }
        }); 
		
		bWriteAReview.setTypeface(Fonts.getOpenSansBold(getContext()));
		bWriteAReview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	String url = "market://details?id=com.gotoohlala";
	    		Intent i = new Intent(Intent.ACTION_VIEW);
	    		i.setData(Uri.parse(url));
	    		getContext().startActivity(i);
	    		//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }); 
		
		bSendFeedback.setTypeface(Fonts.getOpenSansBold(getContext()));
		bSendFeedback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	String email = "support@gotoohlala.com";
            	Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                getContext().startActivity(Intent.createChooser(emailIntent, getContext().getString(R.string.Send_email_via)));
            }
        }); 
	}
	
	private void showLogoutAlertDialog() {
		// TODO Auto-generated method stub
    	AlertDialog alert = new AlertDialog.Builder(getContext()).create();
		alert.setMessage(getContext().getString(R.string.Are_you_sure));
		alert.setButton(getContext().getString(R.string.No), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		alert.setButton2(getContext().getString(R.string.Yes), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				Intent broadcastIntent = new Intent();
				broadcastIntent.setAction("com.gotoohlala.profile.ProfileSettings");
				getContext().sendBroadcast(broadcastIntent);
				
				// TODO Auto-generated method stub
				RestClient result = null;
				try {
					result = new request().execute(Rest.RENEW_SESSION, Rest.OSESS + Profile.sk, Rest.DELETE).get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.i("logout result", String.valueOf(result.getResponseCode()));
				
				if (Profile.isFacebook){
					 Session session = Session.getActiveSession();
					 if (session != null){
					     if (!session.isClosed()) {
					    	 session.closeAndClearTokenInformation();
					     }
					 }
				}
				
				LoginState.modifyLoginState(getContext().getDir("user", 1), true);
				
				Profile.setSk("");
				CacheInternalStorage.cacheUserLoginInfo(new UserLoginInfo("", "", false), getContext()); //cache an empty userLogin info
				
				SetXMPPConnection.connection.disconnect(); //disconnect xmpp server
				
				OohlalaMain.campusWallUnlockedCheck = false; //make the campus wall unlocked check again for the next login user
				
				/*
            	Intent i = new Intent(getBaseContext(), CheckEmail.class);
            	startActivity(i);
            	*/
				//onBackPressed();
			}
		});
		alert.show();
	}

}
