package profile;

import inbox.Inbox;
import inbox.InboxArrayAdapter;
import inbox.InboxModel;
import inbox.Inbox.TimeComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import launchOohlala.CheckEmail;

import network.RetrieveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.BlockListArrayAdapter.ViewHolder;

import rewards.Rewards;
import smackXMPP.XMPPClient;
import studentsnearby.studentsNearbyModel;
import user.Profile;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import campusmap.CampusMap;

import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.UnreadNumCheck;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.LoginState;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.TimeCounter;
import datastorage.Rest.request;

public class SMSInvite extends Activity {
	
	ListView listView;
	List<BlockListModel> list = new ArrayList<BlockListModel>();
	SMSInviteArrayAdapter adapter;
	
	Handler mHandler = new Handler(Looper.getMainLooper());
	
	String numbers = "";
	
	TextView bDone;
	
	String sms_msg, user_cal_share_url, user_event_share_url, user_course_share_url;
	int course_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smsinvite);
		
		Bundle b = this.getIntent().getExtras();
		if (b != null){
			sms_msg = b.getString("sms_msg");
			course_id = b.getInt("course_id");
		}
		
		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(SMSInvite.this));
		
		bDone = (TextView) findViewById(R.id.bDone);
		bDone.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent sms = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + numbers)); 
				sms.putExtra("sms_body", sms_msg + getString(R.string.on_OOHLALA) + user_course_share_url.replace("<school_course_id>", String.valueOf(course_id))); 
				startActivity(Intent.createChooser(sms, getString(R.string.Choose_a_SMS_client)));
			}
	    }); 
	
			
		listView = (ListView) findViewById(R.id.lvSMSInvite);
		adapter = new SMSInviteArrayAdapter(this, list);
		listView.setAdapter(adapter);
		listView.setFastScrollEnabled(true);
		
		TaskQueueImage.addTask(new getInboxTask(), SMSInvite.this);
		TaskQueueImage.addTask(new setUserCalShare(), SMSInvite.this);
	}
	
	class setUserCalShare extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	RestClient result = null;
			try {
				result = new Rest.requestBody().execute(Rest.USER, Rest.OSESS + Profile.sk, Rest.PUT, "1", "user_cal_share", String.valueOf(1)).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("update profile: ", result.getResponse());
        	
        	if (result.getResponseCode() == 200){
        		RestClient result2 = null;
    			try {
    				result2 = new Rest.request().execute(Rest.USER, Rest.OSESS + Profile.sk, Rest.GET).get();
    			} catch (InterruptedException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			} catch (ExecutionException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			}
    			Log.i("get share url: ", result2.getResponse());
    			
    			try {
    				user_cal_share_url = (new JSONObject(result2.getResponse())).getString("user_cal_share_url");
    				user_event_share_url = (new JSONObject(result2.getResponse())).getString("user_event_share_url");
    				user_course_share_url = (new JSONObject(result2.getResponse())).getString("user_course_share_url");
    			} catch (JSONException e) {
    	
    			}
        	}
	    }
	}
	
	class getInboxTask extends Thread {
	    // This method is called when the thread runs
		List<BlockListModel> listTemp = new ArrayList<BlockListModel>();

	    public void run() {
			//Get User Chat History
	    	Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
	        while (phones.moveToNext()){
	        	String Name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
	        	String Number=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	        	
	        	listTemp.add(new BlockListModel(null, Name, Number, 0));
	        }
			
			mHandler.post(new Runnable() {
        		public void run() {
        			//listView.setVisibility(View.GONE);
        			Collections.sort(listTemp, new Comparator<BlockListModel>(){
        				@Override
        				public int compare(BlockListModel a, BlockListModel b){
        					return a.first_name.compareToIgnoreCase(b.first_name);
        				}
        			});
        			
        			list.addAll(listTemp);
        			
        			listView.invalidateViews();
        	    	adapter.notifyDataSetChanged();
        	    	//listView.setVisibility(View.VISIBLE);
        	    	
        	    	listTemp.clear();
        	    	listTemp = null;
        		}
			});
		}
	}
	
	public class SMSInviteArrayAdapter extends ArrayAdapter<BlockListModel> {
		private final Context context;
		private final List<BlockListModel> list;
		
		class ViewHolder {
			public ImageView ivCheck, ivInvite;
			public TextView tvName, tvInvite, tvLetter;
			public RelativeLayout bg;
		}

		public SMSInviteArrayAdapter(Context context, List<BlockListModel> list) {
			super(context, R.layout.smsinviterow, list);
			this.context = context;
			this.list = list;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null){
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.smsinviterow, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.ivCheck = (ImageView) rowView.findViewById(R.id.ivCheck);
				viewHolder.ivInvite = (ImageView) rowView.findViewById(R.id.ivInvite);
				viewHolder.tvName = (TextView) rowView.findViewById(R.id.tvName);
				viewHolder.tvInvite = (TextView) rowView.findViewById(R.id.tvInvite);
				viewHolder.tvLetter = (TextView) rowView.findViewById(R.id.tvLetter);
				viewHolder.bg = (RelativeLayout) rowView.findViewById(R.id.bg);
				
				rowView.setTag(viewHolder);
			}
			
			final ViewHolder holder = (ViewHolder) rowView.getTag();
			
			holder.tvName.setText(null);
			holder.ivCheck.setVisibility(View.GONE);
			holder.ivInvite.setVisibility(View.GONE);
			holder.tvName.setTypeface(Fonts.getOpenSansBold(context));
			holder.tvName.setText(list.get(position).first_name);
			holder.tvInvite.setVisibility(View.VISIBLE);
			holder.tvInvite.setTypeface(Fonts.getOpenSansBold(context));
			holder.tvLetter.setVisibility(View.GONE);
			
			if (position == 0){
				holder.tvLetter.setVisibility(View.VISIBLE);
				holder.tvLetter.setText(list.get(position).first_name.substring(0, 1).toUpperCase());
			} else if (!list.get(position - 1).first_name.substring(0, 1).toUpperCase().contentEquals(list.get(position).first_name.substring(0, 1).toUpperCase())){
				holder.tvLetter.setVisibility(View.VISIBLE);
				holder.tvLetter.setText(list.get(position).first_name.substring(0, 1).toUpperCase());
			}
			
			if (list.get(position).user_id == 1){
				holder.ivCheck.setVisibility(View.VISIBLE);
			}
			
			holder.bg.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					holder.ivInvite.setVisibility(View.GONE);
					holder.tvInvite.setVisibility(View.GONE);
					holder.ivCheck.setVisibility(View.VISIBLE);
					bDone.setVisibility(View.VISIBLE);
					list.get(position).user_id = 1;
					
					if (numbers.trim().length() == 0){
						numbers = list.get(position).last_name;
					} else {
						numbers += ";" + list.get(position).last_name;
					}
				}
			});
			
			return rowView;
		}

	}
	
	public void onResume() {
		super.onResume();
		
		TaskQueueImage.addTask(new setUserCalShare(), SMSInvite.this);
	}

}
