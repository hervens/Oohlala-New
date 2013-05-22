package campuswall;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;

import datastorage.DeviceDimensions;
import datastorage.ImageLoader;

public class CampusWallImage extends Activity {
	
	String image_url, image_words;
	int picWidth;
	ImageView ivImage;
	ProgressBar pbLoading;
	TextView tvWords;
	RelativeLayout rlImage;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campuswallimage);
		
		Bundle b = this.getIntent().getExtras();
		image_url = b.getString("image_url");
		picWidth = DeviceDimensions.getWidth(getApplicationContext());
		if (b.containsKey("image_words")){
			image_words = b.getString("image_words");
		}
		
		tvWords = (TextView) findViewById(R.id.tvWords);
		tvWords.setText(image_words);
		tvWords.setVisibility(View.GONE);
		
		pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
		ivImage = (ImageView) findViewById(R.id.ivImage);
		rlImage = (RelativeLayout) findViewById(R.id.rlImage);
		
		new loadOriginalImage().execute();
		
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.gotoohlala.profile.ProfileSettings");
        registerReceiver(new BroadcastReceiver() {
        	@Override
         	public void onReceive(Context context, Intent intent) {
        		Log.d("onReceive","Logout in progress");
            	//At this point you should start the login activity and finish this one
        		finish();
        	}
        }, intentFilter);
	}
	
	private class loadOriginalImage extends AsyncTask<Void, Void, Void> {
    	
		@Override
		protected Void doInBackground(Void... params) {
			if (image_url != null){
				Log.i("image_url", image_url);
				final Bitmap bitmap = ImageLoader.originalImageStoreAndLoad(image_url, CampusWallImage.this);
				
				runOnUiThread(new Runnable() {
					public void run() {
						if (bitmap != null){
							if (picWidth == 0){
								ivImage.setImageBitmap(bitmap);
							} else {
								ivImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, picWidth, (int) (((double) picWidth/bitmap.getWidth())*bitmap.getHeight()), false));			
							}
							rlImage.setOnClickListener(new Button.OnClickListener() {
								public void onClick(View v) {
									onBackPressed();
								}
							});
							pbLoading.setVisibility(View.GONE);
							
							bitmap.recycle();
						}
					}
				});
			}
			
			return null;
		}
    }

}
