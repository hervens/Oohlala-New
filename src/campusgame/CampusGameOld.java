package campusgame;

import com.gotoohlala.R;

import datastorage.DeviceDimensions;
import datastorage.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CampusGameOld extends Activity {
	
	public ImageView ivGameImage;
	public TextView tvGameName;
	public TextView tvWinners;
	public RelativeLayout rlFirstPlace, rlSecondPlace, rlThirdPlace, rlForthPlace, rlFifthPlace;
	public ImageView ivFirstPlace, ivSecondPlace, ivThirdPlace, ivForthPlace, ivFifthPlace;
	public TextView tvFirstPlaceTitle, tvSecondPlaceTitle, tvThirdPlaceTitle, tvForthPlaceTitle, tvFifthPlaceTitle;
	public TextView tvFirstPlaceName, tvSecondPlaceName, tvThirdPlaceName, tvForthPlaceName, tvFifthPlaceName;
	public TextView tvGameCategory;
	TextView bGameRule;
	
	String game_image_url, game_name, game_video_url, game_rules;
	String[] winner_avatar, prize_name, winner_name;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campusgameold);
		
		Bundle b = this.getIntent().getExtras();
		game_image_url = b.getString("game_image_url");
		game_name = b.getString("game_name");
		game_video_url = b.getString("game_video_url");
		winner_avatar = b.getStringArray("winner_avatar");
		prize_name = b.getStringArray("prize_name");
		winner_name = b.getStringArray("winner_name");
		game_rules = b.getString("game_rules");
		
		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		}); 
		
		ivGameImage = (ImageView) findViewById(R.id.ivGameImage);
		tvGameName = (TextView) findViewById(R.id.tvGameName);
		tvWinners = (TextView) findViewById(R.id.tvWinners);
		rlFirstPlace = (RelativeLayout) findViewById(R.id.rlFirstPlace);
		rlSecondPlace = (RelativeLayout) findViewById(R.id.rlSecondPlace);
		rlThirdPlace = (RelativeLayout) findViewById(R.id.rlThirdPlace);
		rlForthPlace = (RelativeLayout) findViewById(R.id.rlForthPlace);
		rlFifthPlace = (RelativeLayout) findViewById(R.id.rlFifthPlace);
		ivFirstPlace = (ImageView) findViewById(R.id.ivFirstPlace);
		ivSecondPlace = (ImageView) findViewById(R.id.ivSecondPlace);
		ivThirdPlace = (ImageView) findViewById(R.id.ivThirdPlace);
		ivForthPlace = (ImageView) findViewById(R.id.ivForthPlace);
		ivFifthPlace = (ImageView) findViewById(R.id.ivFifthPlace);
		tvFirstPlaceTitle = (TextView) findViewById(R.id.tvFirstPlaceTitle);
		tvSecondPlaceTitle = (TextView) findViewById(R.id.tvSecondPlaceTitle);
		tvThirdPlaceTitle = (TextView) findViewById(R.id.tvThirdPlaceTitle);
		tvForthPlaceTitle = (TextView) findViewById(R.id.tvForthPlaceTitle);
		tvFifthPlaceTitle = (TextView) findViewById(R.id.tvFifthPlaceTitle);
		tvFirstPlaceName = (TextView) findViewById(R.id.tvFirstPlaceName);
		tvSecondPlaceName = (TextView) findViewById(R.id.tvSecondPlaceName);
		tvThirdPlaceName = (TextView) findViewById(R.id.tvThirdPlaceName);
		tvForthPlaceName = (TextView) findViewById(R.id.tvForthPlaceName);
		tvFifthPlaceName = (TextView) findViewById(R.id.tvFifthPlaceName);
		tvGameCategory = (TextView) findViewById(R.id.tvGameCategory);
		bGameRule = (TextView) findViewById(R.id.bGameRule);
		
		Bitmap image_bitmap = ImageLoader.thumbImageStoreAndLoad(game_image_url, CampusGameOld.this);
		if (image_bitmap != null){
			//int picWidth = DeviceDimensions.getWidth(getApplicationContext());
			//int picHeight = (int) (((double)image_bitmap.getHeight()/image_bitmap.getWidth())*picWidth);
			//ivGameImage.setImageBitmap(Bitmap.createScaledBitmap(image_bitmap, picWidth, picHeight, false));
			ivGameImage.setImageBitmap(image_bitmap);
		}
		ivGameImage.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (game_video_url.contains("youtube")){
					Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
					websiteIntent.setData(Uri.parse(game_video_url));
					startActivity(websiteIntent);
				}
			}
		});
		
		tvGameName.setText(game_name);
		
		for (int i = 0; i < winner_avatar.length; i++){
			if (winner_avatar[i] != null && i == 0){
				rlFirstPlace.setVisibility(View.VISIBLE);
				ivFirstPlace.setImageBitmap(ImageLoader.thumbImageStoreAndLoad(winner_avatar[i], CampusGameOld.this));
				tvFirstPlaceTitle.setText(prize_name[i] + " : ");
				tvFirstPlaceName.setText(winner_name[i]);
			} else if (winner_avatar[i] != null && i == 1){
				rlSecondPlace.setVisibility(View.VISIBLE);
				ivSecondPlace.setImageBitmap(ImageLoader.thumbImageStoreAndLoad(winner_avatar[i], CampusGameOld.this));
				tvSecondPlaceTitle.setText(prize_name[i] + " : ");
				tvSecondPlaceName.setText(winner_name[i]);
			} else if (winner_avatar[i] != null && i == 2){
				rlThirdPlace.setVisibility(View.VISIBLE);
				ivThirdPlace.setImageBitmap(ImageLoader.thumbImageStoreAndLoad(winner_avatar[i], CampusGameOld.this));
				tvThirdPlaceTitle.setText(prize_name[i] + " : ");
				tvThirdPlaceName.setText(winner_name[i]);
			} else if (winner_avatar[i] != null && i == 3){
				rlForthPlace.setVisibility(View.VISIBLE);
				ivForthPlace.setImageBitmap(ImageLoader.thumbImageStoreAndLoad(winner_avatar[i], CampusGameOld.this));
				tvForthPlaceTitle.setText(prize_name[i] + " : ");
				tvForthPlaceName.setText(winner_name[i]);
			} else if (winner_avatar[i] != null && i == 4){
				rlFifthPlace.setVisibility(View.VISIBLE);
				ivFifthPlace.setImageBitmap(ImageLoader.thumbImageStoreAndLoad(winner_avatar[i], CampusGameOld.this));
				tvFifthPlaceTitle.setText(prize_name[i] + " : ");
				tvFifthPlaceName.setText(winner_name[i]);
			}
		}
		
		bGameRule.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Bundle extras = new Bundle();
				extras.putString("game_rules", game_rules);
				Intent i = new Intent(CampusGameOld.this, CampusGameShowRule.class);
				i.putExtras(extras);
				startActivity(i);
			}
		});
		
	}

}
