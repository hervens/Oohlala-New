package launchOohlala;

import inbox.Inbox;
import inbox.InboxArrayAdapter;
import inbox.InboxModel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import network.ErrorCodeParser;
import network.RetrieveData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookException;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;

import profile.AddCourse;
import profile.BlockListArrayAdapter;
import profile.ProfileSettings;
import profile.SuggestedFriends;
import smackXMPP.XMPPClient;
import user.Profile;

import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import datastorage.CacheInternalStorage;
import datastorage.ConvertDpsToPixels;
import datastorage.Fonts;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.UserLoginInfo;

public class InAppTour extends Activity {
	ViewPager viewPager;  
    ArrayList<View> list;  
    ViewGroup main, group;  
    ImageView imageView;  
    ImageView[] imageViews; 
    FrameLayout flPage;
    
    boolean onResumeable = false;
    
    private Handler mHandler = new Handler();
    
    TextView bStart;
	
	private static final List<String> PERMISSIONS = Arrays.asList("education", "friends_education_history");
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
	ProgressDialog m_ProgressDialog;
	
	BroadcastReceiver brLogout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inapptour);
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(InAppTour.this));

		flPage = (FrameLayout) findViewById(R.id.flPage);
		
		bStart = (TextView) findViewById(R.id.bStart);
    	bStart.setVisibility(View.GONE);
		bStart.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
            	Intent i = new Intent(getApplicationContext(), OohlalaMain.class);
				startActivity(i);
            }
        });
	    
	    LayoutInflater inflater = getLayoutInflater();  
        list = new ArrayList<View>(); 
        list.add(inflater.inflate(R.layout.intour0, null));  
        list.add(inflater.inflate(R.layout.intour1, null));  
        list.add(inflater.inflate(R.layout.intour2, null));  
        
        imageViews = new ImageView[list.size()];  
        // group是R.layou.main中的负责包裹小圆点的LinearLayout.  
        ViewGroup group = (ViewGroup) this.findViewById(R.id.viewGroup);  
  
        viewPager = (ViewPager) this.findViewById(R.id.viewPager);  
  
        for (int i = 0; i < list.size(); i++) {
            imageView = new ImageView(InAppTour.this);  
            LayoutParams lp = new LayoutParams(30, 30);
            lp.setMargins(10, 38, 10, 0);
            imageView.setLayoutParams(lp);  
            imageView.setPadding(10, 0, 10, 0);  
            imageViews[i] = imageView;  
            if (i == 0) {  
                // 默认进入程序后第一张图片被选中;  
                imageViews[i].setBackgroundResource(R.drawable.paging_controls_elected);  
            } else {  
                imageViews[i].setBackgroundResource(R.drawable.paging_control);  
            }  
            group.addView(imageView);  
        }  
  
        viewPager.setAdapter(new MyAdapter());  
        viewPager.setOnPageChangeListener(new MyListener());  
        
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
	    
        Session session = Session.getActiveSession();
        if (session == null) {
        	if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                //session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
        
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.gotoohlala.profile.ProfileSettings");
        brLogout = new BroadcastReceiver() {
        	@Override
         	public void onReceive(Context context, Intent intent) {
        		Log.d("onReceive","Logout in progress");
            	//At this point you should start the login activity and finish this one
        		finish();
        	}
        };
        registerReceiver(brLogout, intentFilter);
	}
	
	public void onDestroy(){
		super.onDestroy();

		unregisterReceiver(brLogout);
	}
	
	class MyAdapter extends PagerAdapter {  
		View view = null;
		View view1 = null;
		View view2 = null;
		  
        public int getCount() {  
            return list.size();  
        }  
  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0 == arg1;  
        }  
  
        public int getItemPosition(Object object) {  
            // TODO Auto-generated method stub  
            return super.getItemPosition(object);  
        }  
  
        public void destroyItem(View arg0, int arg1, Object arg2) {  
            // TODO Auto-generated method stub  
            ((ViewPager) arg0).removeView(list.get(arg1));  
        }  
  
        public Object instantiateItem(View arg0, int position) {  
            // TODO Auto-generated method stub  
        	LayoutInflater inflater = (LayoutInflater) arg0.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View localView = null;
            switch (position) {
                case 0:
                    view = inflater.inflate(R.layout.intour0, null);

                    TextView tvTour0 = (TextView) view.findViewById(R.id.tvTour0);
                    tvTour0.setText(getString(R.string.intour01) + Profile.schoolName + getString(R.string.intour02));
                    tvTour0.setTypeface(Fonts.getOpenSansRegular(InAppTour.this));

                    localView = view;
                    break;
                case 1:
                    view1 = inflater.inflate(R.layout.intour1, null);
                    
                    TextView tvTour1 = (TextView) view1.findViewById(R.id.tvTour1);
                    tvTour1.setText(getString(R.string.intour11) + Profile.schoolName + getString(R.string.intour12));
                    tvTour1.setTypeface(Fonts.getOpenSansRegular(InAppTour.this));
                    
                    TextView tvButton = (TextView) view1.findViewById(R.id.tvButton);
                    tvButton.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v) {
            				final Session session = Session.getActiveSession();
            				if (!session.isOpened()) {
                            	onClickLogin();
                    	    } else if (session.isOpened()) {
                    	    	m_ProgressDialog = ProgressDialog.show(InAppTour.this, getString(R.string.Please_Wait), getString(R.string.loading), true);
                    	    	
            		        	// Check for publish permissions    
            		            Request request = Request.newMeRequest(session, 
            			                new Request.GraphUserCallback() {
            			            public void onCompleted(GraphUser user, Response response) {
            			                // If the response is successful
            			                if (session == Session.getActiveSession()) {
            			                    if (user != null) {
            			                    	String my_uid = null;
            			                    	try {
            				                    	JSONArray myschools = new JSONArray(user.getProperty("education").toString());
            						        		for (int j = 0; j < myschools.length(); j++){
            						        			if (myschools.getJSONObject(j).getString("type").contentEquals("College")){
            						        				my_uid = myschools.getJSONObject(j).getJSONObject("school").getString("id");
            						        				Log.i("my_uid", my_uid);
            						        				
            						        				TaskQueueImage.addTask(new ImportFriendsThread(my_uid), InAppTour.this);
            						        				break;
            						        			}
            						        		}
            			                    	} catch(Exception e) {
            			                    		m_ProgressDialog.dismiss();
            			        	                e.printStackTrace();
            			        	            }
            			                    } else {
            			                    	m_ProgressDialog.dismiss();
            			                    }
            			                }
            			            }
            			       	});
            		            request.executeAsync();
            		        }
                        }
                    });
                    
                    localView = view1;
                    break;
                case 2:
                    view2 = inflater.inflate(R.layout.intour2, null);
                    
                    TextView tvTour2 = (TextView) view2.findViewById(R.id.tvTour2);
                    tvTour2.setTypeface(Fonts.getOpenSansRegular(InAppTour.this));
                    
                    TextView tvButton2 = (TextView) view2.findViewById(R.id.tvButton);
                    tvButton2.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v) {
                        	Intent i = new Intent(getApplicationContext(), FindYourFriends.class);
            				startActivity(i);
                        }
                    });
                    
                    localView = view2;
                    break;
            }
        	
        	
            ((ViewPager) arg0).addView(localView, 0);

            return localView;
        }  
  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
            // TODO Auto-generated method stub  
  
        }  
  
        public Parcelable saveState() {  
            // TODO Auto-generated method stub  
            return null;  
        }  
  
        public void startUpdate(View arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        public void finishUpdate(View arg0) {  
            // TODO Auto-generated method stub  
  
        }  
    }  
  
    class MyListener implements OnPageChangeListener {  
  
        public void onPageScrollStateChanged(int arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
            // TODO Auto-generated method stub  
  
        }  
  
        public void onPageSelected(int arg0) {  
        	if (arg0 == 2){
        		bStart.setVisibility(View.VISIBLE);
        	} else {
        		bStart.setVisibility(View.GONE);
        	}
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[arg0]  
                        .setBackgroundResource(R.drawable.paging_controls_elected);  
                if (arg0 != i) {  
                    imageViews[i]  
                            .setBackgroundResource(R.drawable.paging_control);  
                }  
            }  
        }  
  
    }  
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
	
	private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }
	
	private class SessionStatusCallback implements Session.StatusCallback {
        public void call(final Session session, SessionState state, Exception exception) {
        	if (session.isOpened()){
        		m_ProgressDialog = ProgressDialog.show(InAppTour.this, getString(R.string.Please_Wait), getString(R.string.loading), true);
        		
	        	Request request = Request.newMeRequest(session, 
		                new Request.GraphUserCallback() {
		            public void onCompleted(GraphUser user, Response response) {
		                // If the response is successful
		                if (session == Session.getActiveSession()) {
		                    if (user != null) {
		                    	String my_uid = null;
		                    	try {
			                    	JSONArray myschools = new JSONArray(user.getProperty("education").toString());
					        		for (int j = 0; j < myschools.length(); j++){
					        			if (myschools.getJSONObject(j).getString("type").contentEquals("College")){
					        				my_uid = myschools.getJSONObject(j).getJSONObject("school").getString("id");
					        				Log.i("my_uid", my_uid);
					        				
					        				TaskQueueImage.addTask(new ImportFriendsThread(my_uid), InAppTour.this);
					        				break;
					        			}
					        		}
		                    	} catch(Exception e) {
		                    		m_ProgressDialog.dismiss();
		        	                e.printStackTrace();
		        	            }  	        				
		                    } else {
		                    	m_ProgressDialog.dismiss();
		                    }
		                }
		            }
		       	});
	            request.executeAsync();
        	}
        }
    }
    
    class ImportFriendsThread extends Thread {
	    // This method is called when the thread runs
		String[] fb_uids = new String[50];
		String my_uid;
		
		public ImportFriendsThread(String my_uid){
			this.my_uid = my_uid;
		}
		
	    public void run() {
			final Session session = Session.getActiveSession();
    	 	if (session.isOpened()) {
    	 		//AddPermissions(session);    
	        	try {
					 HttpClient httpClient = new DefaultHttpClient();
		             HttpContext localContext = new BasicHttpContext();
		             HttpGet httpGet = new HttpGet(
		                     "https://graph.facebook.com/me/friends?fields=education,name&access_token="
		                             + session.getAccessToken());
		             HttpResponse httpResponse = httpClient.execute(httpGet,
		                     localContext);
		             
		             String response = null;
		             HttpEntity entity2 = httpResponse.getEntity();
		 	     	 if (entity2 != null) {
		 	     		InputStream instream = entity2.getContent();
		 	     		response = RestClient.convertStreamToString(instream);
		 	     		instream.close();
		 	      	 }
	
		        	 //Log.i("response", response);
		        	 if (response == null || response.equals("") || 
		        			response.equals("false")) {
		        		 	Log.v("Error", "Blank response");
		        	 } else {
		        		 JSONObject json = new JSONObject(response);
				         final JSONArray friends = json.getJSONArray("data");
				         Log.i("university friends: ", friends.toString());
				    				
				         int k = 0;
				         for (int i = 0; i < friends.length(); i++){
				        	 if (k < 50){
				        		 if (friends.getJSONObject(i).has("education")){
					        		 JSONArray schools = friends.getJSONObject(i).getJSONArray("education");
					        		 for (int j = 0; j < schools.length(); j++){
					        			 if (schools.getJSONObject(j).getString("type").contentEquals("College")){
					        				 if (schools.getJSONObject(j).getJSONObject("school").getString("id").contentEquals(my_uid)){
					        					 //Log.i("school id", schools.getJSONObject(j).getJSONObject("school").getString("id"));
					        					 //Log.i("name", friends.getJSONObject(i).getString("name"));
					        					 fb_uids[k] = friends.getJSONObject(i).getString("id"); 
					        					 //Log.i("fb_uids", fb_uids[k] + "    " + k);
					        					 k++;
					        					 
					        					 break;
					        				 }
					        			 }
					        		 }
				        		 }
				        	 } else {
				        		 break;
				        	 }
				         } 	
		        	 }
	        	} catch(Exception e) {
	                e.printStackTrace();
	            }  
	        }
    	 	
    	 	mHandler.post(new Runnable() {
        		public void run() {
        			m_ProgressDialog.dismiss();
        			
		    	 	 Bundle params = new Bundle();
				   	 params.putString("message", getString(R.string.hey_give_OOHLALA_a_try));
				   	 String fb_uids_to = fb_uids[0];
				   	 for (int i = 1; i < fb_uids.length; i++){
				   		 if(fb_uids[i] != null){
				   			 fb_uids_to += "," + fb_uids[i];
				   		 }
				   	 }
				   	 //Log.i("fb_uids_to", fb_uids_to);
				   	 params.putString("to", fb_uids_to);
				   	 WebDialog requestsDialog = (
			   	        new WebDialog.RequestsDialogBuilder(InAppTour.this,
			   	            Session.getActiveSession(),
			   	            params))
			   	            .setOnCompleteListener(new OnCompleteListener() {
			   	                public void onComplete(Bundle values,
			   	                    FacebookException error) {
			   	                	if (values != null){
				   	                    final String requestId = values.getString("request");
				   	                    if (requestId != null) {
				   	                        Toast.makeText(getApplicationContext(), 
				   	                        	getString(R.string.Request_Sent),  
				   	                            Toast.LENGTH_SHORT).show();
				   	                    } else {
				   	                        Toast.makeText(getApplicationContext(), 
				   	                        	getString(R.string.Request_Cancelled), 
				   	                            Toast.LENGTH_SHORT).show();
				   	                    }
			   	                	}
			   	                }
			   	            }).build();
				   	 
				   	 requestsDialog.show();
        		}
    	 	});
	    }
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (onResumeable){
			//LayoutInflater inflater = getLayoutInflater();  
			//list.add(inflater.inflate(R.layout.intour0, null));  
	        //list.add(inflater.inflate(R.layout.intour1, null));  
	        //list.add(inflater.inflate(R.layout.intour2, null));  
	        
	        //viewPager.setAdapter(new MyAdapter()); 
	        //bStart.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		//viewPager.setAdapter(null);  
		//list.clear();
		onResumeable = true;
	}
	
}
