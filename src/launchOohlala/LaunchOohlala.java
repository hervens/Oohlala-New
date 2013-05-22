package launchOohlala;

import ManageThreads.TaskQueue;
import ManageThreads.TaskQueueImage;
import ManageThreads.TaskQueueImage2;
import ManageThreads.TaskQueueImage3;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;

import datastorage.CacheInternalStorage;

public class LaunchOohlala extends Activity {
	
	Button bRetryConnection;
	ProgressBar pbLaunching;
	
	boolean resumeable = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launching);
		
		if (isOnline()){
			//check if the userLogin is cached
			if (CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()) != null){
				if (CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()).userAccount.trim().length() > 0){
					TaskQueue.start();
					TaskQueueImage.start(getApplicationContext());
					TaskQueueImage2.start();
					//TaskQueueImage3.start();
					
					Intent i = new Intent(getApplicationContext(), OohlalaMain.class);
					startActivity(i);
				} else {
					Intent i = new Intent(getApplicationContext(), CheckEmail.class);
					startActivity(i);
				}
			} else {
				Intent i = new Intent(getApplicationContext(), CheckEmail.class);
				startActivity(i);
			}
		} else {
			Toast.makeText(getApplicationContext(), 
					getString(R.string.No_internet_connection_please_try_again), 
					Toast.LENGTH_SHORT).show();
		}
		
		pbLaunching = (ProgressBar) this.findViewById(R.id.pbLaunching);
		
		bRetryConnection = (Button) this.findViewById(R.id.bRetryConnection);
		bRetryConnection.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {	
				if (isOnline()){
					bRetryConnection.setVisibility(View.GONE);
					pbLaunching.setVisibility(View.VISIBLE);
					
					Intent i = new Intent(getApplicationContext(), CheckEmail.class);
					startActivity(i);
					
					bRetryConnection.setVisibility(View.VISIBLE);
					pbLaunching.setVisibility(View.GONE);	
				} else {
					Toast.makeText(getApplicationContext(), 
							getString(R.string.No_internet_connection_please_try_again), 
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

	    return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		resumeable = true;
	}
	
	public void onResume() {
		super.onResume();
		
		if (resumeable){
			if (isOnline()){
				//check if the userLogin is cached
				if (CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()) != null){
					if (CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()).userAccount.trim().length() > 0){
						TaskQueue.start();
						TaskQueueImage.start(getApplicationContext());
						TaskQueueImage2.start();
						//TaskQueueImage3.start();
						
						Intent i = new Intent(getApplicationContext(), OohlalaMain.class);
						startActivity(i);
					} else {
						Intent i = new Intent(getApplicationContext(), CheckEmail.class);
						startActivity(i);
					}
				} else {
					Intent i = new Intent(getApplicationContext(), CheckEmail.class);
					startActivity(i);
				}
			} else {
				Toast.makeText(getApplicationContext(), 
						getString(R.string.No_internet_connection_please_try_again), 
						Toast.LENGTH_SHORT).show();
			}
			
			resumeable = false;
		}
	}

}
