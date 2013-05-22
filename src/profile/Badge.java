package profile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;

import user.Profile;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import campuswall.CampusWallModel;

import com.gotoohlala.R;

import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.Rest.request;
import events.EventsEventsDetail;

public class Badge extends Activity {
	ImageView ivBadgeImage, ivBadge1, ivBadge2, ivBadge3, ivBadge4, ivBadge5, ivBadge6, ivBadge7, ivBadge8, ivBadge9, ivBadge10, ivBadge11, ivBadge12;
	TextView tvBadgeName;
	RelativeLayout rlBadgeBg;
	
	List<BadgeModel> list = new ArrayList<BadgeModel>();
	
	int user_id = 0;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.badgeview);
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(Badge.this));
		
		Bundle b = this.getIntent().getExtras();
		if (b != null){
			user_id = b.getInt("user_id");
		}
		
		ivBadgeImage = (ImageView) findViewById(R.id.ivBadgeImage);
		tvBadgeName = (TextView) findViewById(R.id.tvBadgeName);
		rlBadgeBg = (RelativeLayout) findViewById(R.id.rlBadgeBg);
		
		ivBadge1 = (ImageView) findViewById(R.id.ivBadge1);
		ivBadge2 = (ImageView) findViewById(R.id.ivBadge2);
		ivBadge3 = (ImageView) findViewById(R.id.ivBadge3);
		ivBadge4 = (ImageView) findViewById(R.id.ivBadge4);
		ivBadge5 = (ImageView) findViewById(R.id.ivBadge5);
		ivBadge6 = (ImageView) findViewById(R.id.ivBadge6);
		ivBadge7 = (ImageView) findViewById(R.id.ivBadge7);
		ivBadge8 = (ImageView) findViewById(R.id.ivBadge8);
		ivBadge9 = (ImageView) findViewById(R.id.ivBadge9);
		ivBadge10 = (ImageView) findViewById(R.id.ivBadge10);
		ivBadge11 = (ImageView) findViewById(R.id.ivBadge11);
		ivBadge12 = (ImageView) findViewById(R.id.ivBadge12);
		
		new UserProfileBadges().execute();
	}
	
	class UserProfileBadges extends AsyncTask<Void, Void, List<BadgeModel>> {
	    // This method is called when the thread runs
		
		List<BadgeModel> listTemp = null;

		protected List<BadgeModel> doInBackground(Void... cuts) {
			listTemp = new ArrayList<BadgeModel>();
			
	    	//get the all the current games			
			RestClient result = null;
			try {
				if (user_id != 0){
					result = new request().execute(Rest.BADGE + "?unlocked_only=1&user_id=" + user_id, Rest.OSESS + Profile.sk, Rest.GET).get();
				} else {
					result = new request().execute(Rest.BADGE + "?unlocked_only=1&user_id=" + Profile.userId, Rest.OSESS + Profile.sk, Rest.GET).get();
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray threads = new JSONArray(result.getResponse());
				Log.i("badges: ", threads.toString());
				
				for (int i = 0; i < threads.length(); i++){
					int id = threads.getJSONObject(i).getInt("id");
					String name = threads.getJSONObject(i).getString("name");
					String badge_icon_url = threads.getJSONObject(i).getString("badge_icon_url");
					String badge_icon_thumb_url = threads.getJSONObject(i).getString("badge_icon_thumb_url");
					
					listTemp.add(new BadgeModel(id, name, badge_icon_thumb_url, badge_icon_url));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return listTemp;
	    }
		
		protected void onPostExecute(List<BadgeModel> result) {
			list.addAll(result);
			
			if (list.size() > 0){
				if (list.get(0).badge_icon_thumb_url != null){
					ivBadge1.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(list.get(0).badge_icon_thumb_url, Badge.this, 47));
					ivBadge1.setOnClickListener(new Button.OnClickListener() {
						public void onClick(View v) {
							rlBadgeBg.setVisibility(View.VISIBLE);
							rlBadgeBg.setOnClickListener(new Button.OnClickListener() {
								public void onClick(View v) {
									rlBadgeBg.setVisibility(View.GONE);
									tvBadgeName.setText(null);
									ivBadgeImage.setImageBitmap(null);
								}
							});
							
							tvBadgeName.setText(list.get(0).name);
							new loadOriginalImage().execute(list.get(0).badge_icon_url);
						}
					});
	        	}
			}
			if (list.size() > 1){
	        	if (list.get(1).badge_icon_thumb_url != null){
	        		ivBadge2.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(list.get(1).badge_icon_thumb_url, Badge.this, 47));
	        		ivBadge2.setOnClickListener(new Button.OnClickListener() {
						public void onClick(View v) {
							rlBadgeBg.setVisibility(View.VISIBLE);
							rlBadgeBg.setOnClickListener(new Button.OnClickListener() {
								public void onClick(View v) {
									rlBadgeBg.setVisibility(View.GONE);
									tvBadgeName.setText(null);
									ivBadgeImage.setImageBitmap(null);
								}
							});
							
							tvBadgeName.setText(list.get(1).name);
							new loadOriginalImage().execute(list.get(1).badge_icon_url);
						}
					});
	        	}
			}
        	if (list.size() > 2){
	        	if (list.get(2).badge_icon_thumb_url != null){
	        		ivBadge3.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(list.get(2).badge_icon_thumb_url, Badge.this, 47));
	        		ivBadge3.setOnClickListener(new Button.OnClickListener() {
						public void onClick(View v) {
							rlBadgeBg.setVisibility(View.VISIBLE);
							rlBadgeBg.setOnClickListener(new Button.OnClickListener() {
								public void onClick(View v) {
									rlBadgeBg.setVisibility(View.GONE);
									tvBadgeName.setText(null);
									ivBadgeImage.setImageBitmap(null);
								}
							});
							
							tvBadgeName.setText(list.get(2).name);
							new loadOriginalImage().execute(list.get(2).badge_icon_url);
						}
					});
	        	}
        	}
        	if (list.size() > 3){
	        	if (list.get(3).badge_icon_thumb_url != null){
					ivBadge4.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(list.get(3).badge_icon_thumb_url, Badge.this, 47));
					ivBadge4.setOnClickListener(new Button.OnClickListener() {
						public void onClick(View v) {
							rlBadgeBg.setVisibility(View.VISIBLE);
							rlBadgeBg.setOnClickListener(new Button.OnClickListener() {
								public void onClick(View v) {
									rlBadgeBg.setVisibility(View.GONE);
									tvBadgeName.setText(null);
									ivBadgeImage.setImageBitmap(null);
								}
							});
							
							tvBadgeName.setText(list.get(3).name);
							new loadOriginalImage().execute(list.get(3).badge_icon_url);
						}
					});
	        	}
        	}
        	if (list.size() > 4){
	        	if (list.get(4).badge_icon_thumb_url != null){
	        		ivBadge5.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(list.get(4).badge_icon_thumb_url, Badge.this, 47));
	        	}
        	}
        	if (list.size() > 5){
	        	if (list.get(5).badge_icon_thumb_url != null){
	        		ivBadge6.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(list.get(5).badge_icon_thumb_url, Badge.this, 47));
	        	}
        	}
        	if (list.size() > 6){
	        	if (list.get(6).badge_icon_thumb_url != null && list.size() > 6){
					ivBadge7.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(list.get(6).badge_icon_thumb_url, Badge.this, 47));
	        	}
        	}
        	if (list.size() > 7){
	        	if (list.get(7).badge_icon_thumb_url != null && list.size() > 7){
	        		ivBadge8.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(list.get(7).badge_icon_thumb_url, Badge.this, 47));
	        	}
        	}
        	if (list.size() > 8){
	        	if (list.get(8).badge_icon_thumb_url != null && list.size() > 8){
	        		ivBadge9.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(list.get(8).badge_icon_thumb_url, Badge.this, 47));
	        	}
        	}
        	if (list.size() > 9){
	        	if (list.get(9).badge_icon_thumb_url != null && list.size() > 9){
					ivBadge10.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(list.get(9).badge_icon_thumb_url, Badge.this, 47));
	        	}
        	}
        	if (list.size() > 10){
	        	if (list.get(10).badge_icon_thumb_url != null && list.size() > 10){
	        		ivBadge11.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(list.get(10).badge_icon_thumb_url, Badge.this, 47));
	        	}
        	}
        	if (list.size() > 11){
	        	if (list.get(11).badge_icon_thumb_url != null && list.size() > 11){
	        		ivBadge12.setImageBitmap(ImageLoader.studentsNearbyImageStoreAndLoad(list.get(11).badge_icon_thumb_url, Badge.this, 47));
	        	}
        	}
        	
        	listTemp.clear();
			listTemp = null;
	    }
	}
	
	private class loadOriginalImage extends AsyncTask<String, Void, Void> {
    	
		@Override
		protected Void doInBackground(String... badge_icon_url) {
			if (badge_icon_url[0] != null){
				final Bitmap bitmap = ImageLoader.originalImageStoreAndLoad(badge_icon_url[0], Badge.this);
				
				runOnUiThread(new Runnable() {
					public void run() {
						if (bitmap != null){
							ivBadgeImage.setImageBitmap(bitmap);
						}
					}
				});
			}
			
			return null;
		}
    }

}
