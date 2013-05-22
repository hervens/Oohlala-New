package com.gotoohlala;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;

public class DialogConnectThread extends Thread {
    // This method is called when the thread runs
	
	SetXMPPConnection mDialog;
	
	String ns = Context.NOTIFICATION_SERVICE;
	private NotificationManager mNotificationManager;
	private Context client;
	
	private Handler mHandler = new Handler();
	
	public DialogConnectThread(Context client) {
		this.client = client;
		this.mNotificationManager = (NotificationManager) client.getSystemService(ns);
	}
	
    public void run() {
    	// Dialog for getting the xmpp settings
    	mHandler.post(new Runnable() {
    		public void run() {
		    	mDialog = new SetXMPPConnection(client, mNotificationManager);
				mDialog.connect();
    		}
    	});
		
    }
}
