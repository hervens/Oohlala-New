package profile;

import inbox.Inbox;
import inbox.InboxArrayAdapter;
import inbox.InboxModel;
import inbox.Inbox.TimeComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import launchOohlala.CheckEmail;

import network.RetrieveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rewards.Rewards;
import smackXMPP.XMPPClient;
import studentsnearby.studentsNearbyModel;
import user.Profile;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import datastorage.LoginState;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.TimeCounter;
import datastorage.Rest.request;

public class BlockList extends Activity {
	
	ListView listView;
	PullToRefreshListView mPullRefreshListView;
	List<BlockListModel> list = new ArrayList<BlockListModel>();
	BlockListArrayAdapter adapter;
	
	Handler mHandler = new Handler(Looper.getMainLooper());
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blocklist);
		
		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(BlockList.this));
		
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.lvBlockList);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
					
				reloadView();
			}
		});
		mPullRefreshListView.setRefreshing();
			
		listView = mPullRefreshListView.getRefreshableView();
		
		adapter = new BlockListArrayAdapter(this, this, list);
		
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			 public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				position--;
				showAlertDialog(list.get(position).user_id);	
			 }
		 });
		 
		 TaskQueueImage.addTask(new getInboxTask(), BlockList.this);
	}
	
	public void reloadView() {
		runOnUiThread(new Runnable() {
			public void run() {
				list.clear();
			}
		});
		    
		if (list.isEmpty()){
			TaskQueueImage.addTask(new getInboxTask(), BlockList.this);
		}
	}
	
	class getInboxTask extends Thread {
	    // This method is called when the thread runs
		List<BlockListModel> listTemp = new ArrayList<BlockListModel>();

	    public void run() {
			//Get User Chat History
			RestClient result = null;
			try {
				result = new request().execute(Rest.INBOX + "?blocked_only=1&with_other_user_resource=1", Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray blocklists = new JSONArray(result.getResponse());
				Log.i("blocklists: ", blocklists.toString());
				
				for (int i = 0; i < blocklists.length(); i++){
					JSONObject other_user = blocklists.getJSONObject(i).getJSONObject("other_user");
					//Log.i("other_user: ", other_user.toString());
					String avatar = other_user.getString("avatar_thumb_url");
					String first_name = other_user.getString("firstname");
					String last_name = other_user.getString("lastname");
					int user_id = blocklists.getJSONObject(i).getInt("other_user_id");
					
					listTemp.add(new BlockListModel(avatar, first_name, last_name, user_id));
				}
			} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
	        
			mHandler.post(new Runnable() {
        		public void run() {
        			//listView.setVisibility(View.GONE);
        			list.addAll(listTemp);
        			
        			listView.invalidateViews();
        	    	adapter.notifyDataSetChanged();
        	    	//listView.setVisibility(View.VISIBLE);
        	    	
        	    	mPullRefreshListView.onRefreshComplete();
        	    	
        	    	listTemp.clear();
        	    	listTemp = null;
        		}
			});
		}
	}
	
	private void showAlertDialog(final int userid) {
		// TODO Auto-generated method stub
    	AlertDialog alert = new AlertDialog.Builder(BlockList.this).create();
		alert.setTitle(getString(R.string.Unblock_User));
		alert.setMessage(getString(R.string.Unblock_this_user));
		alert.setButton(getString(R.string.No), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		alert.setButton2(getString(R.string.Yes), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				RestClient result = null;
				try {
					result = new Rest.requestBody().execute(Rest.USER + userid, Rest.OSESS + Profile.sk, Rest.PUT, "1", "block", "0").get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	
            	if (result.getResponseCode() == 200){
            		reloadView();
            	}
			}
		});
		alert.show();
	}
	
	public void onResume() {
		super.onResume();
		
	}

}
