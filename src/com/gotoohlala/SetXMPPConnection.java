package com.gotoohlala;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import network.RetrieveData;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gotoohlala.R;

import datastorage.Rest;
import datastorage.RestClient;

import smackXMPP.ComposingPacketExtensionProvider;
import smackXMPP.ImagePacketExtensionProvider;
import smackXMPP.NamePacketExtensionProvider;
import smackXMPP.PausedPacketExtensionProvider;
import smackXMPP.ReadPacketExtensionProvider;
import smackXMPP.ReceivedPacketExtensionProvider;
import smackXMPP.UserIDPacketExtensionProvider;
import smackXMPP.XMPPClient;
import user.Profile;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class SetXMPPConnection extends Dialog {
	
    private Context client;
    public static XMPPConnection connection;
    
    private int icon;        // icon from resources
	private NotificationManager mNotificationManager;
	
	private static final int HELLO_ID = 1;
	
	public static String jidCheck = "";
	public static int unread_num = 0;
	public static UnreadNumCheck act;

    public SetXMPPConnection(Context client, NotificationManager nm) {
        super(client);
        this.client = client;
        
        icon = R.drawable.ic_launcher; 

    	this.mNotificationManager = nm;
    	
    	ProviderManager.getInstance().addExtensionProvider("image",
				"jabber:client", new ImagePacketExtensionProvider());
        ProviderManager.getInstance().addExtensionProvider("name",
				"jabber:client", new NamePacketExtensionProvider());
        ProviderManager.getInstance().addExtensionProvider("user_id",
				"jabber:client", new UserIDPacketExtensionProvider());
    }
    
    private class XMPPConnectionConnect extends AsyncTask<Void, Void, Void> {
    	
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			//-------------- testing xmpp -------------------
	        String host = "host.oohlaladeals.com";
	        String port = "5222";
	        String service = "host.oohlaladeals.com";
	        String username = Profile.jid + "@" + host;
	        String password = Profile.xmppPass;
	        //------------------------------------------------
	        
	        // Create a connection
	        ConnectionConfiguration connConfig =
	                new ConnectionConfiguration(host, Integer.parseInt(port), service);
	        
	        connection = new XMPPConnection(connConfig);
	        
	        try {
	            connection.connect();
	            Log.i("OohlalaMain", "[SettingsDialog] Connected to " + connection.getHost());
	        } catch (XMPPException ex) {
	            Log.e("OohlalaMain", "[SettingsDialog] Failed to connect to " + connection.getHost());
	            Log.e("OohlalaMain", ex.toString());
	            setConnection(null);
	        }
	        
	        if (connection.isConnected()){
		        try {
		            connection.login(username, password);
		            Log.i("OohlalaMain", "Logged in as " + connection.getUser());
	
		            // Set the status to available
		            Presence presence = new Presence(Presence.Type.available);
		            connection.sendPacket(presence);
		            setConnection(connection);
		        } catch (XMPPException ex) {
		            Log.e("OohlalaMain", "[SettingsDialog] Failed to log in as " + username);
		            
		            connect();
		        }
	        }
	        
	        dismiss();
			
			return null;
		}
		
    }

    public void connect() {
    	new XMPPConnectionConnect().execute();
    }
    
    public void setConnection(final XMPPConnection connection) {
       
        if (connection != null) {
            // Add a packet listener to get messages sent to us
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
                public void processPacket(Packet packet) {
                	Log.i("XMPPClient got packets", packet.getExtensions().toString());
                	
                	final Message message = (Message) packet;
                	//Log.i("message:", message.toXML());
                    if (message.getBody().trim().length() > 0 && !getJid(message).contentEquals(jidCheck)) {
                    	//messages.add(new XMPPChatModel(message.getPacketID(), message.getBody(), null,
                    	
                    	CharSequence tickerText = getName(message) + ": " + message.getBody();              // ticker-text
                    	long when = System.currentTimeMillis();         // notification time
                    	CharSequence contentTitle = getName(message);  // message title
                    	CharSequence contentText = message.getBody();      // message text
                    	
                    	Bundle extras = new Bundle();
    					extras.putString("jid", getJid(message));
    					extras.putString("first_name", getName(message));
    					extras.putString("last_name", "");
    					extras.putString("avatar", getUserAvatar(getUserID(message)));
    					extras.putInt("user_id", Integer.valueOf(getUserID(message)));
    					
    					Intent notificationIntent = new Intent(client, XMPPClient.class);
    					// set intent so it does not start a new activity
    					notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
    			                Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    	notificationIntent.putExtras(extras);
                    	
                    	PendingIntent contentIntent = PendingIntent.getActivity(client, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    	// the next two lines initialize the Notification, using the configurations above
                    	Notification notification = new Notification(icon, tickerText, when);
                    	notification.setLatestEventInfo(client, contentTitle, contentText, contentIntent);
                    	notification.defaults = Notification.DEFAULT_ALL;
                    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    	
                    	mNotificationManager.notify(HELLO_ID, notification);
                    	
                    	//Toast.makeText(client , getName(message) + ": " + message.getBody(), Toast.LENGTH_SHORT).show();
                    	unread_num++;
                    	act.updateView();
                    }
                   
                }
            }, filter);
        }
    }
    
    public String getName(Message message){
    	
    	if(message.toXML().contains("<name")){
        	String nameString = "<name>";
        	String name = null;
        	name = message.toXML().substring(
						message.toXML().indexOf(nameString) + nameString.length());
        	name = name.substring(0, name.indexOf("</name>"));
        	Log.i("name:", name);
			return name;
        } else {
        	return null;
        }
    }
    
    public String getUserID(Message message){
    	
    	if(message.toXML().contains("<user_id")){
        	String useridString = "<user_id>";
        	String userid = null;
        	userid = message.toXML().substring(
						message.toXML().indexOf(useridString) + useridString.length());
        	userid = userid.substring(0, userid.indexOf("</user_id>"));
        	Log.i("user id:", userid);
			return userid;
        } else {
        	return null;
        }
    }
    
    public String getJid(Message message){
    	
    	if(message.toXML().contains("<message")){
        	String jidString = "from=\"";
        	String jid = null;
			if (message.toXML().contains("from=\"")) {
				jid = message.toXML().substring(
						message.toXML().indexOf(jidString) + jidString.length());
				jid = jid.substring(0, jid.indexOf("\""));
				Log.i("jid:", jid);
			}
			return jid;
        } else {
        	return null;
        }
    }

    public String getUserAvatar(String user_id){
    	RestClient result = null;
		try {
			result = new Rest.request().execute(Rest.USER + user_id, Rest.OSESS + Profile.sk, Rest.GET).get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.i("request_profile: ", result.getResponse());
				
		String avatar = null;
		if (result.getResponseCode() == 200) {
			try {
				JSONObject request_profile = new JSONObject(result.getResponse());
				Log.i("request_profile", String.valueOf(request_profile));
				
				avatar = request_profile.getString("avatar_thumb_url");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return avatar;
    }
    
    public static void setJidCheck(String jid){
    	jidCheck = jid;
    } 
	
}
