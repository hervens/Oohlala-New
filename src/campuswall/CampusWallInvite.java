package campuswall;

import inbox.InboxArrayAdapter;
import inbox.InboxModel;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import network.ErrorCodeParser;
import network.RetrieveData;

import smackXMPP.XMPPClient;

import com.gotoohlala.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CampusWallInvite extends Activity {
	
	ListView lvInvite;
	List<InviteModel> list = new ArrayList<InviteModel>();
	InviteArrayAdapter adapter;
	
	final int PICK_CONTACT = 1;
	
	 @Override
	 public void onCreate(Bundle icicle) {
		 super.onCreate(icicle);
		 setContentView(R.layout.campuswallinvite);
		 
		 Button back = (Button) findViewById(R.id.back);
		 back.setOnClickListener(new Button.OnClickListener() {
			 public void onClick(View v) {
				onBackPressed();
			 }
		 }); 
		 
		 lvInvite = (ListView) findViewById(R.id.lvInvite);
		 adapter = new InviteArrayAdapter(CampusWallInvite.this, CampusWallInvite.this, list);
		 
		 lvInvite.setAdapter(adapter);
		 lvInvite.setOnItemClickListener(new OnItemClickListener() {
			 public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				switch(position){
				case 0:
					Intent i = new Intent(getApplicationContext(), CampusWallInviteFacebook.class);
					startActivity(i);
					break;
				case 1:
					Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
					startActivityForResult(intent, PICK_CONTACT);
					break;
				}
			 }
		 });
		 
		 Drawable social_fb = CampusWallInvite.this.getResources().getDrawable(R.drawable.social_fb);
		 Drawable social_contact = CampusWallInvite.this.getResources().getDrawable(R.drawable.social_contact);
		 //Drawable social_sms = CampusWallInvite.this.getResources().getDrawable(R.drawable.social_sms);
		 
		 list.add(new InviteModel(social_fb, getString(R.string.Facebook)));
		 //list.add(new InviteModel(social_contact, getString(R.string.Contact_List)));
		 //list.add(new InviteModel(social_sms, "Send email/SMS"));
		 
		 runOnUiThread(new Runnable() {
			 public void run() {
				 lvInvite.invalidateViews();
			 }
		});
	 }
	 
	 @Override
	 public void onActivityResult(int reqCode, int resultCode, Intent data) {
	   super.onActivityResult(reqCode, resultCode, data);

	   switch (reqCode) {
	     case (PICK_CONTACT) :
	       if (resultCode == Activity.RESULT_OK) {
	    	   Uri contactData = data.getData();
	    	   Cursor cursor =  managedQuery(contactData, null, null, null, null);
	    	   if (cursor.moveToNext()) {      
	    	       String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
	    	       String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 
	    	       //Toast.makeText(CampusWallInvite.this, name, Toast.LENGTH_SHORT).show();
	    	       
	    	       String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

	    	       if (hasPhone.equalsIgnoreCase("1"))
	    	           hasPhone = "true";
	    	       else
	    	           hasPhone = "false" ;
	    	       
	    	       String phoneNumber = null;
	    	       if (Boolean.parseBoolean(hasPhone)) {
	    	    	   Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null);
	    	    	   while (phones.moveToNext()) {
	    	    		   phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	    	    	   }
	    	    	   phones.close();
	    	       }

	    	       // Find Email Addresses
	    	       String emailAddress = null;
	    	       Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
	    	       if (emails.moveToNext()) {
	    	    	   emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
	    	       }
	    	       emails.close();  
	    	       
	    	       //showInviteOptionDialog(name, emailAddress, phoneNumber);
	    	   }  
	       }
	       break;
	   }
	}
	 
	/*
	public void showInviteOptionDialog(String name, final String emailAddress, final String phoneNumber){
		//show up the invite dialog
		final String[] names = {"", ""};
		
		if (name.contains(" ")){
			String delimiter = " ";
			names[0] = name.split(delimiter)[0];
			names[1] = name.split(delimiter)[1];
		} else {
			names[0] = name;
			names[1] = "";
		}
		
		final String emailBody = "Hey! Add me as a friend on OOHLALA, my pincode is: umsmm (to get the free app: http://ohl.me/students)";
		
		if (phoneNumber != null && emailAddress != null){
 	       	final CharSequence[] items = {emailAddress, phoneNumber};
			
	   			AlertDialog.Builder builder = new AlertDialog.Builder(CampusWallInvite.this);
	   			builder.setTitle(getString(R.string.Invite_) + name + getString(R.string._by));
	   			builder.setItems(items, new DialogInterface.OnClickListener() {
	   			    public void onClick(DialogInterface dialog, int item) {
	   			    	Hashtable<String, String> params = new Hashtable<String, String>();
	   			    	String result;
	   			    	String sms_message = null;
   						int invite_id = 0;
   						int code;
   						
	   			    	switch(item){
	   			    	case 0:
	   			    		params = new Hashtable<String, String>();
	   						params.put("email", emailAddress);
	   						params.put("first_name", names[0]);
	   						params.put("last_name", names[1]);
	   						result = RetrieveData.requestMethod(RetrieveData.EXTERNAL_INVITE, params);
	   						Log.i("External Friend Invite", result.toString());
	   						
	   						code = ErrorCodeParser.parser(result);
	   						
	   						if (code == 0) {
	   							Toast.makeText(CampusWallInvite.this, getString(R.string.Invite_has_been_sent_through_email), Toast.LENGTH_SHORT).show();
	   							/*
		   						try {
		   							sms_message = (new JSONObject(result)).getString("sms_message");
		   							invite_id = (new JSONObject(result)).getInt("invite_id");
		   							
		   							Log.i("sms_message", sms_message.toString());
		   							Log.i("invite_id", String.valueOf(invite_id));
		   						} catch (JSONException e) {
		   							// TODO Auto-generated catch block
		   							e.printStackTrace();
		   						}
	                            
	                            Intent email = new Intent(Intent.ACTION_SEND);
	                        	email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});		  
	                        	email.putExtra(Intent.EXTRA_SUBJECT, "Join us in Oohlala");
	                        	email.putExtra(Intent.EXTRA_TEXT, emailBody);
	                        	email.setType("message/rfc822");
	                        	startActivity(Intent.createChooser(email, "Choose an Email client :"));
	                        	
	                        	params = new Hashtable<String, String>();
		   						params.put("invite_id", String.valueOf(invite_id));
		   						result = RetrieveData.requestMethod(RetrieveData.CONFIRM_EXTERNAL_INVITE, params);
		   						*/
	 
	 						/*
	   						} else if (code == 1001) {
	   							Toast.makeText(CampusWallInvite.this, getString(R.string.The_invitee_is_already_a_friend_of_you), Toast.LENGTH_SHORT).show();
	   						} else if (code == 1002) {
	   							Toast.makeText(CampusWallInvite.this, getString(R.string.There_is_already_an_in_app_friend_invite_pending_between_you_and_the_invitee), Toast.LENGTH_SHORT).show();
	   						} else if (code == 1003) {
	   							Toast.makeText(CampusWallInvite.this, getString(R.string.An_external_friend_invitation_has_already_been_sent_to_the_invitee), Toast.LENGTH_SHORT).show();
	   						}
	   									   						
	   			    		break;
	   			    	case 1:
	   			    		params = new Hashtable<String, String>();
	   						params.put("email", "");
	   						params.put("first_name", names[0]);
	   						params.put("last_name", names[1]);
	   						params.put("is_sms", String.valueOf(1));
	   						params.put("phone", phoneNumber);
	   						result = RetrieveData.requestMethod(RetrieveData.EXTERNAL_INVITE, params);
	   						Log.i("External Friend Invite", result.toString());
	   						
	   						code = ErrorCodeParser.parser(result);
	   						
	   						if (code == 0) {
		   						try {
		   							sms_message = (new JSONObject(result)).getString("sms_message");
		   							invite_id = (new JSONObject(result)).getInt("invite_id");
		   							
		   							Log.i("sms_message", sms_message.toString());
		   							Log.i("invite_id", String.valueOf(invite_id));
		   						} catch (JSONException e) {
		   							// TODO Auto-generated catch block
		   							e.printStackTrace();
		   						}
		   						
		   						Intent sms = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber)); 
		   						sms.putExtra("sms_body", sms_message); 
		   						startActivity(Intent.createChooser(sms, getString(R.string.Choose_a_SMS_client)));
		   						
		   						params = new Hashtable<String, String>();
		   						params.put("invite_id", String.valueOf(invite_id));
		   						result = RetrieveData.requestMethod(RetrieveData.CONFIRM_EXTERNAL_INVITE, params);
	   						} else if (code == 1001) {
	   							Toast.makeText(CampusWallInvite.this, getString(R.string.The_invitee_is_already_a_friend_of_you), Toast.LENGTH_SHORT).show();
	   						} else if (code == 1002) {
	   							Toast.makeText(CampusWallInvite.this, getString(R.string.There_is_already_an_in_app_friend_invite_pending_between_you_and_the_invitee), Toast.LENGTH_SHORT).show();
	   						} else if (code == 1003) {
	   							Toast.makeText(CampusWallInvite.this, getString(R.string.An_external_friend_invitation_has_already_been_sent_to_the_invitee), Toast.LENGTH_SHORT).show();
	   						}
	   						
	   			    		break;
	   			    	}
	   			    }
	   			});
	   			AlertDialog alert = builder.create();
	   			alert.show();
	       } else if (phoneNumber != null){
	    	   	final CharSequence[] items = {phoneNumber};
	   			
	   			AlertDialog.Builder builder = new AlertDialog.Builder(CampusWallInvite.this);
	   			builder.setTitle(getString(R.string.Invite_) + name + getString(R.string._by));
	   			builder.setItems(items, new DialogInterface.OnClickListener() {
	   			    public void onClick(DialogInterface dialog, int item) {	
	   			    	Hashtable<String, String> params = new Hashtable<String, String>();
	   			    	String result;
	   			    	String sms_message = null;
   						int invite_id = 0;
   						int code;
   						
	   			    	switch(item){
	   			    	case 0:
	   			    		params = new Hashtable<String, String>();
	   						params.put("email", "");
	   						params.put("first_name", names[0]);
	   						params.put("last_name", names[1]);
	   						params.put("is_sms", String.valueOf(1));
	   						params.put("phone", phoneNumber);
	   						result = RetrieveData.requestMethod(RetrieveData.EXTERNAL_INVITE, params);
	   						Log.i("External Friend Invite", result.toString());
	   						
	   						code = ErrorCodeParser.parser(result);
	   						
	   						if (code == 0) {
		   						try {
		   							sms_message = (new JSONObject(result)).getString("sms_message");
		   							invite_id = (new JSONObject(result)).getInt("invite_id");
		   							
		   							Log.i("sms_message", sms_message.toString());
		   							Log.i("invite_id", String.valueOf(invite_id));
		   						} catch (JSONException e) {
		   							// TODO Auto-generated catch block
		   							e.printStackTrace();
		   						}
		   						
		   						Intent sms = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber)); 
		   						sms.putExtra("sms_body", sms_message); 
		   						startActivity(Intent.createChooser(sms, getString(R.string.Choose_a_SMS_client)));
		   						
		   						params = new Hashtable<String, String>();
		   						params.put("invite_id", String.valueOf(invite_id));
		   						result = RetrieveData.requestMethod(RetrieveData.CONFIRM_EXTERNAL_INVITE, params);
	   						} else if (code == 1001) {
	   							Toast.makeText(CampusWallInvite.this, getString(R.string.The_invitee_is_already_a_friend_of_you), Toast.LENGTH_SHORT).show();
	   						} else if (code == 1002) {
	   							Toast.makeText(CampusWallInvite.this, getString(R.string.There_is_already_an_in_app_friend_invite_pending_between_you_and_the_invitee), Toast.LENGTH_SHORT).show();
	   						} else if (code == 1003) {
	   							Toast.makeText(CampusWallInvite.this, getString(R.string.An_external_friend_invitation_has_already_been_sent_to_the_invitee), Toast.LENGTH_SHORT).show();
	   						}
	   						
	   			    		break;
	   			    	}
	   			    }
	   			});
	   			AlertDialog alert = builder.create();
	   			alert.show();
	       } else if (emailAddress != null){
	    	   	final CharSequence[] items = {emailAddress};
	   			
	   			AlertDialog.Builder builder = new AlertDialog.Builder(CampusWallInvite.this);
	   			builder.setTitle(getString(R.string.Invite_) + name + getString(R.string._by));
	   			builder.setItems(items, new DialogInterface.OnClickListener() {
	   			    public void onClick(DialogInterface dialog, int item) {	
	   			    	Hashtable<String, String> params = new Hashtable<String, String>();
	   			    	String result;
	   			    	String sms_message = null;
   						int invite_id = 0;
   						int code;
   						
	   			    	switch(item){
	   			    	case 0:
	   			    		params = new Hashtable<String, String>();
	   						params.put("email", emailAddress);
	   						params.put("first_name", names[0]);
	   						params.put("last_name", names[1]);
	   						result = RetrieveData.requestMethod(RetrieveData.EXTERNAL_INVITE, params);
	   						Log.i("External Friend Invite", result.toString());
	   						
	   						code = ErrorCodeParser.parser(result);
	   						
	   						if (code == 0) {
	   							Toast.makeText(CampusWallInvite.this, getString(R.string.Invite_has_been_sent_through_email), Toast.LENGTH_SHORT).show();
	   							/*
		   						try {
		   							sms_message = (new JSONObject(result)).getString("sms_message");
		   							invite_id = (new JSONObject(result)).getInt("invite_id");
		   							
		   							Log.i("sms_message", sms_message.toString());
		   							Log.i("invite_id", String.valueOf(invite_id));
		   						} catch (JSONException e) {
		   							// TODO Auto-generated catch block
		   							e.printStackTrace();
		   						}
	                            
	                            Intent email = new Intent(Intent.ACTION_SEND);
	                        	email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});		  
	                        	email.putExtra(Intent.EXTRA_SUBJECT, "Join us in Oohlala");
	                        	email.putExtra(Intent.EXTRA_TEXT, emailBody);
	                        	email.setType("message/rfc822");
	                        	startActivity(Intent.createChooser(email, "Choose an Email client :"));
	                        	
	                        	params = new Hashtable<String, String>();
		   						params.put("invite_id", String.valueOf(invite_id));
		   						result = RetrieveData.requestMethod(RetrieveData.CONFIRM_EXTERNAL_INVITE, params);
		   						*/
	 
	 						/*
	   						} else if (code == 1001) {
	   							Toast.makeText(CampusWallInvite.this, getString(R.string.The_invitee_is_already_a_friend_of_you), Toast.LENGTH_SHORT).show();
	   						} else if (code == 1002) {
	   							Toast.makeText(CampusWallInvite.this, getString(R.string.There_is_already_an_in_app_friend_invite_pending_between_you_and_the_invitee), Toast.LENGTH_SHORT).show();
	   						} else if (code == 1003) {
	   							Toast.makeText(CampusWallInvite.this, getString(R.string.An_external_friend_invitation_has_already_been_sent_to_the_invitee), Toast.LENGTH_SHORT).show();
	   						}
	   						
	   			    		break;
	   			    	}
	   			    }
	   			});
	   			AlertDialog alert = builder.create();
	   			alert.show();
	       } else {
	    	   final CharSequence[] items = {getString(R.string.No_Contact_Data)};
	   			
	   			AlertDialog.Builder builder = new AlertDialog.Builder(CampusWallInvite.this);
	   			builder.setTitle(getString(R.string.Invite_) + name + getString(R.string._by));
	   			builder.setItems(items, new DialogInterface.OnClickListener() {
	   			    public void onClick(DialogInterface dialog, int item) {		   			    	
	   			    	switch(item){
	   			    	case 0:
	   			    		Toast.makeText(CampusWallInvite.this, getString(R.string.No_contact_data_for_this_person), Toast.LENGTH_SHORT).show();
	   			    		break;
	   			    	}
	   			    }
	   			});
	   			AlertDialog alert = builder.create();
	   			alert.show();
	       }
	}
	*/

}
