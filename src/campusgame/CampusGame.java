package campusgame;

import inbox.Inbox;
import inbox.InboxModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import network.RetrieveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.Friends;

import user.Profile;

import ManageThreads.TaskQueue;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import campuswall.CampusWall;
import campuswall.CampusWallPostInterface;
import campuswall.CampusWallPostThread;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.TopMenuNavbar;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import datastorage.CpuUsage;
import datastorage.FB;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.TimeCounter;
import datastorage.Rest.request;
import events.EventsEventsModel;
import events.EventsEvents.TimeComparator;

public class CampusGame extends FrameLayout implements CampusWallPostInterface {

	String FILENAME = "AndroidSSO_data";
    
    boolean DetailedActivityStarted = false;
    
	List<CampusGameModel> list = new ArrayList<CampusGameModel>();
	ListView listView;
	PullToRefreshListView mPullRefreshListView;
	CampusGameArrayAdapter adapter;
	
	Handler mHandler = new Handler(Looper.getMainLooper());
	View v = null;
	
	loadBitmapThread loadBitmapThread;
	TextView tvCampusGame;
	ImageView ivCampusGame;
	
	String caption_facebookShare, message_facebookShare, icon_url_facebookShare, url_facebookShare, description_facebookShare, url_name_facebookShare;
	int event_id_facebookShare;
	
	TextView headerGames;

	boolean currentView = false;
	
	public TopMenuNavbar TopMenuNavbar;
    
	public CampusGame(Context context) {
		super(context);
	
		final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
		v = (RelativeLayout) mLayoutInflater.inflate(R.layout.campusgame, null);  
	    addView(v);  
		
		currentView = true;
		
		GameMap.act = CampusGame.this;
		
		headerGames = (TextView) v.findViewById(R.id.headerGames);
		headerGames.setTypeface(Fonts.getOpenSansBold(v.getContext()));
		
		TopMenuNavbar = (TopMenuNavbar) v.findViewById(R.id.bTopMenuNavBar);
		
		//content
		mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.lvCampusGame);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(getContext().getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
					
				reloadView();
			}
		});
			
		listView = mPullRefreshListView.getRefreshableView();		
		adapter = new CampusGameArrayAdapter(getContext(), list);
		listView.setAdapter(adapter);
		
		tvCampusGame = (TextView) v.findViewById(R.id.tvCampusGame);
		tvCampusGame.setTypeface(Fonts.getOpenSansBold(v.getContext()));
		ivCampusGame = (ImageView) v.findViewById(R.id.ivCampusGame);
		
		TaskQueueImage.addTask(new loadNoGameImage(), v.getContext());
		refreshAfterPost();
	}
	
	class loadNoGameImage extends Thread {
		// This method is called when the thread runs
		public void run() {
			if (currentView){
				mHandler.post(new Runnable() {
					public void run() {
						ivCampusGame.setImageBitmap(ImageLoader.thumbImageStoreAndLoad("https://d38h7mnlv8qddx.cloudfront.net/gamevideo.png", v.getContext()));
						ivCampusGame.setOnClickListener(new Button.OnClickListener() {
							public void onClick(View v) {
								Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
								websiteIntent.setData(Uri.parse("http://www.youtube.com/watch?v=MOkClwS0uzI"));
								getContext().startActivity(websiteIntent);
							}
						});
					}
				});
			}
		}
	}
	
	public void reloadView() {
	    // Do work to refresh the list here.
		if (loadBitmapThread != null){
			loadBitmapThread.cancel(true);
		}
		
		mHandler.post(new Runnable() {
    		public void run() {
    			list.clear();
    			
    			if (list.isEmpty()){
    				TaskQueueImage.addTask(new updateListThread(), v.getContext());
				}
			}
		});
	}
	
	public class CampusGameArrayAdapter extends ArrayAdapter<CampusGameModel> {
		private final Context context;
		private final List<CampusGameModel> list;
		
		class ViewHolder {
			public ImageView ivThumb;
			public TextView tvGameName, tvEndTime;
			public RelativeLayout bg;
			public ImageView ivFirstPlace, ivSecondPlace, ivThirdPlace, ivForthPlace, ivFifthPlace;
			public TextView tvGameCategory;
			public Button bPlaying;
		}

		public CampusGameArrayAdapter(Context context, List<CampusGameModel> list) {
			super(context, R.layout.campusgamerow, list);
			this.context = context;
			this.list = list;
		}
		
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null){
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.campusgamerow, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.ivThumb = (ImageView) rowView.findViewById(R.id.ivThumb);
				viewHolder.tvGameName = (TextView) rowView.findViewById(R.id.tvGameName);
				viewHolder.bg = (RelativeLayout) rowView.findViewById(R.id.bg);
				viewHolder.tvEndTime = (TextView) rowView.findViewById(R.id.tvEndTime);
				viewHolder.tvGameCategory = (TextView) rowView.findViewById(R.id.tvGameCategory);
				viewHolder.bPlaying = (Button) rowView.findViewById(R.id.bPlaying);
				viewHolder.ivFirstPlace = (ImageView) rowView.findViewById(R.id.ivFirstPlace);
				viewHolder.ivSecondPlace = (ImageView) rowView.findViewById(R.id.ivSecondPlace);
				viewHolder.ivThirdPlace = (ImageView) rowView.findViewById(R.id.ivThirdPlace);
				viewHolder.ivForthPlace = (ImageView) rowView.findViewById(R.id.ivForthPlace);
				viewHolder.ivFifthPlace = (ImageView) rowView.findViewById(R.id.ivFifthPlace);
				
				rowView.setTag(viewHolder);
			}
			
			final ViewHolder holder = (ViewHolder) rowView.getTag();
			
			holder.ivThumb.setImageBitmap(null);
			holder.tvGameName.setText("");
			holder.ivFirstPlace.setImageBitmap(null);
			holder.ivSecondPlace.setImageBitmap(null);
			holder.ivThirdPlace.setImageBitmap(null);
			holder.ivForthPlace.setImageBitmap(null);
			holder.ivFifthPlace.setImageBitmap(null);
			holder.tvEndTime.setText("");
			holder.bPlaying.setText("");
			holder.tvGameCategory.setText("");
			holder.tvGameCategory.setVisibility(View.GONE);
			
			if (list.get(position).image_bitmap != null){
				//image save into the memory and cache
				holder.ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(list.get(position).image_bitmap), 8));
			}
			
			holder.tvGameName.setTypeface(Fonts.getOpenSansBold(context));
			holder.tvGameName.setText(list.get(position).gameName);

			holder.tvEndTime.setTypeface(Fonts.getOpenSansLightItalic(v.getContext()));
			holder.tvEndTime.setText(TimeCounter.EventDate(Integer.valueOf(list.get(position).start)));
			
			holder.bPlaying.setText(String.valueOf(list.get(position).player_count) + getContext().getString(R.string._Playing));
			
			if (list.get(position).type == 0){
				holder.bPlaying.setVisibility(View.VISIBLE);
				holder.ivFirstPlace.setVisibility(View.GONE);
				holder.ivSecondPlace.setVisibility(View.GONE);
				holder.ivThirdPlace.setVisibility(View.GONE);
				holder.ivForthPlace.setVisibility(View.GONE);
				holder.ivFifthPlace.setVisibility(View.GONE);
				
				holder.bg.setOnClickListener(new Button.OnClickListener(){
					public void onClick(View v) {
						if (list.get(position).game_type != 1 && list.get(position).game_type != 2 && list.get(position).game_type != 3){
							AlertDialog alert = new AlertDialog.Builder(v.getContext()).create();
							alert.setMessage(getContext().getString(R.string.This_game_requires_a_newer_version_of_OOHLALA_to_play_Please_update_your_app_to_the_newest_version));
							alert.setButton(getContext().getString(R.string.OK), new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									/*
									String url = "market://details?id=com.gotoohlala";
						    		Intent i = new Intent(Intent.ACTION_VIEW);
						    		i.setData(Uri.parse(url));
						    		startActivity(i);
						    		*/
								}
							});
							
							alert.show();
						} else {
							// TODO Auto-generated method stub
							if (list.get(position).game_type == 3){
								if (list.get(position).user_submissions == 0){
									//agree to the rules and upload images
									try {
										caption_facebookShare = list.get(position).shareinfo.getString("caption");
						    			message_facebookShare = list.get(position).shareinfo.getString("message");
						    			icon_url_facebookShare = list.get(position).shareinfo.getString("icon_url"); 
						    			url_facebookShare = list.get(position).shareinfo.getString("url");
						    			description_facebookShare = list.get(position).shareinfo.getString("description"); 
						    			url_name_facebookShare = list.get(position).shareinfo.getString("url_name");
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									Bundle extras = new Bundle();
									extras.putString("caption_facebookShare", caption_facebookShare);
									extras.putString("message_facebookShare", message_facebookShare);
									extras.putString("icon_url_facebookShare", icon_url_facebookShare);
									extras.putString("url_facebookShare", url_facebookShare);
									extras.putString("description_facebookShare", description_facebookShare);
									extras.putString("url_name_facebookShare", url_name_facebookShare);
									extras.putInt("event_id_facebookShare", list.get(position).promo_event_id);
									extras.putInt("start", list.get(position).start);
									extras.putInt("end", list.get(position).end);
									extras.putString("game_video_url", list.get(position).game_video_url);
				    				extras.putString("game_image_url", list.get(position).game_image_url);
				    				extras.putString("game_name", list.get(position).gameName);
				    				extras.putInt("user_submissions", list.get(position).user_submissions);
				    				extras.putInt("max_submissions", list.get(position).max_submissions);
				    				extras.putString("game_rules", list.get(position).game_rules);
				    				extras.putString("description", list.get(position).description);
									extras.putInt("game_type", list.get(position).game_type);
									
									Intent i = new Intent(getContext(), CampusGameInfo.class);
									i.putExtras(extras);
									getContext().startActivity(i);
								} else if (list.get(position).user_submissions > 0 && list.get(position).user_submissions <= list.get(position).max_submissions){
									//upload images
									try {
										caption_facebookShare = list.get(position).shareinfo.getString("caption");
						    			message_facebookShare = list.get(position).shareinfo.getString("message");
						    			icon_url_facebookShare = list.get(position).shareinfo.getString("icon_url"); 
						    			url_facebookShare = list.get(position).shareinfo.getString("url");
						    			description_facebookShare = list.get(position).shareinfo.getString("description"); 
						    			url_name_facebookShare = list.get(position).shareinfo.getString("url_name");
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									Bundle extras = new Bundle();
									extras.putString("caption_facebookShare", caption_facebookShare);
									extras.putString("message_facebookShare", message_facebookShare);
									extras.putString("icon_url_facebookShare", icon_url_facebookShare);
									extras.putString("url_facebookShare", url_facebookShare);
									extras.putString("description_facebookShare", description_facebookShare);
									extras.putString("url_name_facebookShare", url_name_facebookShare);
									extras.putInt("event_id_facebookShare", list.get(position).promo_event_id);
									extras.putInt("start", list.get(position).start);
									extras.putInt("end", list.get(position).end);
				    				extras.putString("game_image_url", list.get(position).game_image_url);
				    				extras.putString("game_name", list.get(position).gameName);
				    				extras.putInt("user_submissions", list.get(position).user_submissions);
				    				extras.putInt("max_submissions", list.get(position).max_submissions);
				    				extras.putString("game_rules", list.get(position).game_rules);
				    				extras.putString("description", list.get(position).description);
				    				
				    				Intent i = new Intent(getContext(), CampusGameType3.class);
				    				i.putExtras(extras);
				    				getContext().startActivity(i);
								}
							} else if (list.get(position).game_type == 1 || list.get(position).game_type == 2){
								if (!list.get(position).has_shared){
									try {
										caption_facebookShare = list.get(position).shareinfo.getString("caption");
						    			message_facebookShare = list.get(position).shareinfo.getString("message");
						    			icon_url_facebookShare = list.get(position).shareinfo.getString("icon_url"); 
						    			url_facebookShare = list.get(position).shareinfo.getString("url");
						    			description_facebookShare = list.get(position).shareinfo.getString("description"); 
						    			url_name_facebookShare = list.get(position).shareinfo.getString("url_name");
						    			event_id_facebookShare = list.get(position).promo_event_id;
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									Bundle extras = new Bundle();
									extras.putString("caption_facebookShare", caption_facebookShare);
									extras.putString("message_facebookShare", message_facebookShare);
									extras.putString("icon_url_facebookShare", icon_url_facebookShare);
									extras.putString("url_facebookShare", url_facebookShare);
									extras.putString("description_facebookShare", description_facebookShare);
									extras.putString("url_name_facebookShare", url_name_facebookShare);
									extras.putInt("event_id_facebookShare", event_id_facebookShare);
									extras.putString("game_video_url", list.get(position).game_video_url);
									extras.putString("game_image_url", list.get(position).game_image_url);
									extras.putString("game_name", list.get(position).gameName);
									extras.putInt("start", list.get(position).start);
									extras.putString("game_rules", list.get(position).game_rules);
									extras.putString("description", list.get(position).description);
									
									Intent i = new Intent(getContext(), CampusGameInfo.class);
									i.putExtras(extras);
									getContext().startActivity(i);
								} else {
									if (isGameOn(list.get(position).start, list.get(position).end)){
										Bundle extras = new Bundle();
										extras.putInt("promo_event_id", list.get(position).promo_event_id);
										extras.putString("game_rules", list.get(position).game_rules);
										Intent i = new Intent(getContext(), GameMap.class);
										i.putExtras(extras);
										getContext().startActivity(i);
									} else {
										int now = (int) (System.currentTimeMillis()/1000);
										int time_until_start = list.get(position).start - now;
										
										Bundle extras = new Bundle();
					    				extras.putString("game_video_url", list.get(position).game_video_url);
					    				extras.putString("game_image_url", list.get(position).game_image_url);
					    				extras.putString("game_name", list.get(position).gameName);
					    				extras.putInt("time_until_start", time_until_start);
					    				extras.putString("game_rules", list.get(position).game_rules);
					    				extras.putString("description", list.get(position).description);
					    				extras.putInt("event_id_facebookShare", list.get(position).promo_event_id);
					    				
					    				Intent i = new Intent(getContext(), CampusGameWaiting.class);
					    				i.putExtras(extras);
					    				getContext().startActivity(i);
									}
								}
							}
						}
					}
			    }); 
			} else {
				holder.bPlaying.setVisibility(View.GONE);
				holder.ivFirstPlace.setVisibility(View.VISIBLE);
				holder.ivSecondPlace.setVisibility(View.VISIBLE);
				holder.ivThirdPlace.setVisibility(View.VISIBLE);
				holder.ivForthPlace.setVisibility(View.VISIBLE);
				holder.ivFifthPlace.setVisibility(View.VISIBLE);
				
				holder.bg.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						Bundle extras = new Bundle();
	    				extras.putString("game_video_url", list.get(position).game_video_url);
	    				extras.putString("game_image_url", list.get(position).game_image_url);
	    				extras.putString("game_name", list.get(position).gameName);
	    				extras.putStringArray("winner_avatar", list.get(position).winner_avatar);
	    				extras.putStringArray("prize_name", list.get(position).prize_name);
	    				extras.putStringArray("winner_name", list.get(position).winner_name);
	    				extras.putString("game_rules", list.get(position).game_rules);
	    				
	    				Intent i = new Intent(getContext(), CampusGameOld.class);
	    				i.putExtras(extras);
	    				getContext().startActivity(i);
					}
				});
				
				for (int i = 0; i < list.get(position).winner_avatar.length; i++){
					if (list.get(position).winner_avatar[i] != null && i == 0){
						holder.ivFirstPlace.setImageBitmap(ImageLoader.thumbImageStoreAndLoad(list.get(position).winner_avatar[i], v.getContext()));
					} else if (list.get(position).winner_avatar[i] != null && i == 1){
						holder.ivSecondPlace.setImageBitmap(ImageLoader.thumbImageStoreAndLoad(list.get(position).winner_avatar[i], v.getContext()));
					} else if (list.get(position).winner_avatar[i] != null && i == 2){
						holder.ivThirdPlace.setImageBitmap(ImageLoader.thumbImageStoreAndLoad(list.get(position).winner_avatar[i], v.getContext()));
					} else if (list.get(position).winner_avatar[i] != null && i == 3){
						holder.ivForthPlace.setImageBitmap(ImageLoader.thumbImageStoreAndLoad(list.get(position).winner_avatar[i], v.getContext()));
					} else if (list.get(position).winner_avatar[i] != null && i == 4){
						holder.ivFifthPlace.setImageBitmap(ImageLoader.thumbImageStoreAndLoad(list.get(position).winner_avatar[i], v.getContext()));
					}
				}
			}
			
			if (list.get(position).type == 0 && position == 0){
				holder.tvGameCategory.setVisibility(View.VISIBLE);
				holder.tvGameCategory.setText(getContext().getString(R.string.Current_Games).toUpperCase());
			}
			
			if (list.get(position).type == 1 && (position == 0 || list.get(position-1).type == 0)){
				holder.tvGameCategory.setVisibility(View.VISIBLE);
				holder.tvGameCategory.setText(getContext().getString(R.string.Previous_Games).toUpperCase());
			}
		
			return rowView;
		}

	}
	
	class updateListThread extends Thread {
	    // This method is called when the thread runs
		List<CampusGameModel> listTemp_current = new ArrayList<CampusGameModel>();
		List<CampusGameModel> listTemp_previous = new ArrayList<CampusGameModel>();
		
	    public void run() {
	    	RestClient result = null;
			try {
				result = new request().execute(Rest.GAME, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray games = new JSONArray(result.getResponse());
				Log.i("games: ", games.toString());
				//Log.i("games error: ", result.getErrorMessage());
				
				for (int i = 0; i < games.length(); i++){
					final int promo_event_id = games.getJSONObject(i).getInt("id");
					String gameName = null;
					if (games.getJSONObject(i).has("name")){
						gameName = games.getJSONObject(i).getString("name");	
					}
					String description = null;
					if (games.getJSONObject(i).has("description")){
						description = games.getJSONObject(i).getString("description");
					}
					int game_type = 0;
					if (games.getJSONObject(i).has("game_type")){
						game_type = games.getJSONObject(i).getInt("game_type");
					}
					String game_rules = null;
					if (games.getJSONObject(i).has("game_rules")){
						game_rules = games.getJSONObject(i).getString("game_rules");	
					}
					int start = games.getJSONObject(i).getInt("start");	
					int end = games.getJSONObject(i).getInt("end");	
					String game_image_url = games.getJSONObject(i).getString("game_image_url");
					String game_video_url = games.getJSONObject(i).getString("game_video_url");
					String game_icon_url = games.getJSONObject(i).getString("game_icon_url");
					String game_url = games.getJSONObject(i).getString("game_url");
					int steal_timeout = 0;
					if (games.getJSONObject(i).has("steal_timeout")){
					 	steal_timeout = games.getJSONObject(i).getInt("steal_timeout");	
					}
					int steal_distance = 0;
					if (games.getJSONObject(i).has("steal_distance")){
						steal_distance = games.getJSONObject(i).getInt("steal_distance");	
					}
					JSONArray area_boundary_coord_array = null;
					if (games.getJSONObject(i).has("area_boundary_coord_array")){
						area_boundary_coord_array = games.getJSONObject(i).getJSONArray("area_boundary_coord_array");	
					}
					final JSONObject shareinfo = games.getJSONObject(i).getJSONObject("sharing_info");
					final boolean has_shared = games.getJSONObject(i).getBoolean("user_shared");
					JSONArray winner_info = null;
					if (games.getJSONObject(i).has("winner_info")){
						winner_info = games.getJSONObject(i).getJSONArray("winner_info");
					}
					
					int max_submissions = 0;
					if (games.getJSONObject(i).has("max_submissions")){
						max_submissions = games.getJSONObject(i).getInt("max_submissions");
					}
					int invites_required = 0;
					if (games.getJSONObject(i).has("invites_required")){
						invites_required = games.getJSONObject(i).getInt("invites_required");
					}
					int user_submissions = 0;
					if (games.getJSONObject(i).has("user_submissions")){
						user_submissions = games.getJSONObject(i).getInt("user_submissions");
					}
					
					int player_count = 0;
					if (!isGameOld(end)){
						if (games.getJSONObject(i).has("player_count")){
							player_count = games.getJSONObject(i).getInt("player_count");	
						}
						
						if (gameName != null){ //only when the gameName exist = have not been deleted
							listTemp_current.add(new CampusGameModel(promo_event_id, has_shared, shareinfo, game_image_url, gameName, null, game_video_url, null, null, null, null, 0, start, end, game_rules, description, game_type, game_icon_url, game_url, steal_timeout, steal_distance, area_boundary_coord_array, player_count, null, null, max_submissions, invites_required, user_submissions));
							Log.i("current game", String.valueOf(0));
						}
					} else {
						int[] winner_id = new int[winner_info.length()];
						String[] winner_name = new String[winner_info.length()];
						String[] winner_avatar = new String[winner_info.length()];
						String[] prize_name = new String[winner_info.length()];
						int[] prize_rank = new int[winner_info.length()];
						int[] prize_type = new int[winner_info.length()];
						
						Log.i("winner_info length", String.valueOf(winner_info.length()));
						for (int j = 0; j < winner_info.length(); j++){
							winner_id[j] = winner_info.getJSONObject(j).getInt("user_id");
							winner_name[j] = winner_info.getJSONObject(j).getString("username");
							prize_name[j] = winner_info.getJSONObject(j).getString("prize_name");
							winner_avatar[j] = winner_info.getJSONObject(j).getString("avatar_thumb_url");
							prize_rank[j] = winner_info.getJSONObject(j).getInt("prize_rank");
							prize_type[j] = winner_info.getJSONObject(j).getInt("prize_type");
						}
						
						if (gameName != null){ //only when the gameName exist = have not been deleted
							listTemp_previous.add(new CampusGameModel(0, has_shared, shareinfo, game_image_url, gameName, null, game_video_url, winner_id, winner_name, prize_name, winner_avatar, 1, start, end, game_rules, null, game_type, game_icon_url, game_url, steal_timeout, steal_distance, area_boundary_coord_array, player_count, prize_rank, prize_type, max_submissions, invites_required, user_submissions));
							Log.i("old game", String.valueOf(1));
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
			if (currentView){
				mHandler.post(new Runnable() {
	        		public void run() {			
						if (listTemp_current.isEmpty() && listTemp_previous.isEmpty()){
							ivCampusGame.setVisibility(View.VISIBLE);
							tvCampusGame.setVisibility(View.VISIBLE);
						} else {
							ivCampusGame.setVisibility(View.GONE);
							tvCampusGame.setVisibility(View.GONE);
						}
						
						//listView.setVisibility(View.GONE);
						
	    				Collections.sort(listTemp_current, new TimeComparator());
	    				list.addAll(listTemp_current);
	    				Collections.sort(listTemp_previous, new TimeComparator());
	    				list.addAll(listTemp_previous);
						
						listView.invalidateViews();
				    	adapter.notifyDataSetChanged();
				    	//listView.setVisibility(View.VISIBLE);
				    	
				    	preLoadImages();
				    	
				    	mPullRefreshListView.onRefreshComplete();
				    	
				    	listTemp_current.clear();
				    	listTemp_current = null;
				    	listTemp_previous.clear();
				    	listTemp_previous = null;
	        		}
				});
			}
	    }
	}
	
	public void preLoadImages() {
		// TODO Auto-generated method stub	
		loadBitmapThread = new loadBitmapThread();
		loadBitmapThread.execute(0, list.size());
	}
	
	class loadBitmapThread extends AsyncTask<Integer, Void, Void> {
	    // This method is called when the thread runs
		public int start, stop;

		protected Void doInBackground(Integer... num) {
	    	//get the all the current games
			this.start = num[0];
			this.stop = num[1];
	    	Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	for (int i = start; i < stop; i++){
	    		if (i < list.size() && currentView){
					if (list.get(i).game_image_url != null && list.get(i).game_image_url.contains(".png") && list.get(i).image_bitmap == null){
						//Log.i("image", list.get(i).image_url);
						list.get(i).image_bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(list.get(i).game_image_url, v.getContext(), 50);
						mHandler.post(new Runnable() {
			        		public void run() {
						    	listView.invalidateViews();
						    }
						});
					}
	    		} else {
	    			break;
	    		}
			}
	    	
	    	return null;
	    }
	}

	public void refreshAfterPost() {
		// TODO Auto-generated method stub
		mPullRefreshListView.setRefreshing();
 		reloadView();
	}
	
	public boolean isGameOn(int start, int end){
		int now = (int) (System.currentTimeMillis()/1000);
		if (start <= now && end > now){
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isGameOld(int end){
		int now = (int) (System.currentTimeMillis()/1000);
		Log.i("end", String.valueOf(end));
		Log.i("now", String.valueOf(now));
		if (end <= now){
			return true;
		} else {
			return false;
		}
	}
	
	public class TimeComparator implements Comparator<CampusGameModel> {
        public int compare(CampusGameModel o1, CampusGameModel o2) {
            return String.valueOf(o2.start).compareTo(String.valueOf(o1.start));
        }
	}
	
	public void onPause() {
		// TODO Auto-generated method stub
		
		currentView = false;
	}
	
	public void onResume() {
		// TODO Auto-generated method stub
		
		currentView = true;
	}

}
