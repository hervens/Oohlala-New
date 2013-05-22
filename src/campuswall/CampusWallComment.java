package campuswall;

import inbox.InboxModel;
import inbox.Inbox.TimeComparator;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.OthersProfile;

import user.Profile;

import campuswall.CampusWall.likeThread;
import campuswall.CampusWall.loadBitmapThread;
import campuswall.CampusWall.loadThumbBitmapThread;
import campuswall.CampusWall.updateListThread;

import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.R.drawable;
import com.gotoohlala.R.id;
import com.gotoohlala.R.layout;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;


import datastorage.CacheInternalStorage;
import datastorage.DeviceDimensions;
import datastorage.DrawableToBitmap;
import datastorage.Fonts;
import datastorage.ImageCache;
import datastorage.ImageLoader;
import datastorage.MemoryStorage;
import datastorage.NinePatch;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.TimeCounter;
import datastorage.Rest.request;

import network.ErrorCodeParser;
import network.RetrieveData;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class CampusWallComment extends Activity implements CampusWallPostInterface {
	
	int school_thread_id;
	Button bPostComment;
	List<CampusWallModel> list = new ArrayList<CampusWallModel>();
	ListView listView;
	PullToRefreshListView mPullRefreshListView;
	CampusWallCommentArrayAdapter adapter;
	
	boolean refresh = false;
	
	loadBitmapThreadAgain loadBitmapThreadAgain;
	loadThumbBitmapThread loadThumbBitmapThread;
	loadBitmapThread loadBitmapThread;
	
	Handler mHandler = new Handler();
	
	Drawable imageBg;
	
	int cut = 0;
	int number = 0;
	boolean lastcut = false;
	LinearLayout llLoading;
	
	RelativeLayout rlBigImage;
	ImageView ivBigImage;
	ProgressBar pbImageLoading;
	TextView tvWords;
	
	int thread_type;
	
	boolean currentView = false;
	
	int picWidth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campuswallcomment);
		picWidth = DeviceDimensions.getWidth(getApplicationContext());
		
		currentView = true;
		
		Bundle b = this.getIntent().getExtras();
		school_thread_id = b.getInt("school_thread_id");
		thread_type = b.getInt("thread_type");
		
		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		}); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(CampusWallComment.this));
		
		bPostComment = (Button) findViewById(R.id.bPostComment);
		bPostComment.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CampusWallCommentPostComment.act = CampusWallComment.this;
				Bundle extras = new Bundle();
				extras.putInt("school_thread_id", school_thread_id);
				extras.putInt("thread_type", thread_type);
				Intent i = new Intent(getApplicationContext(), CampusWallCommentPostComment.class);
				i.putExtras(extras);
				startActivity(i);
			}
	    }); 
		
		llLoading = (LinearLayout) findViewById(R.id.llLoading);
		
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.llScrollViewCampusWallComment);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
				//mPullRefreshListView.setShowViewWhileRefreshing(false);
				// Do work to refresh the list here.
				//new GetDataTask().execute();
				reloadView();
			}
		});
		mPullRefreshListView.setRefreshing();
		
		listView = mPullRefreshListView.getRefreshableView();
		adapter = new CampusWallCommentArrayAdapter(getApplicationContext(), list);
		listView.setOnScrollListener(new OnScrollListener(){
			
			public int first, last;
			
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		        // TODO Auto-generated method stub
		    	this.first = firstVisibleItem;
		    	this.last = firstVisibleItem + visibleItemCount - 1;
		    }
		 	
		    public void onScrollStateChanged(AbsListView view, int scrollState) {
		    	// TODO Auto-generated method stub
			   	if(scrollState == 0 && first != 0){
			   		if (last >= number && !lastcut){
			   			//listView.scrollBy(0, 120);
			   			llLoading.setVisibility(View.VISIBLE);
				    	
			    		cut++;
			    		TaskQueueImage.addTask(new updateListThread2(), CampusWallComment.this);
			    	} 
			   		
			   		if (last <= list.size()){
			   			loadBitmapThreadAgain = new loadBitmapThreadAgain();
			   			loadBitmapThreadAgain.execute(first - 1, last);		
			   		}
			    }
			}
		});
		// Assign adapter to ListView
		listView.setAdapter(adapter);
		
		rlBigImage = (RelativeLayout) findViewById(R.id.rlBigImage);
		ivBigImage = (ImageView) findViewById(R.id.ivBigImage);
		pbImageLoading = (ProgressBar) findViewById(R.id.pbImageLoading);
		tvWords = (TextView) findViewById(R.id.tvWords);
		
		TaskQueueImage.addTask(new updateListThread(), CampusWallComment.this);
		
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
	
	public void reloadView() {
        // Do work to refresh the list here.
		if (loadBitmapThread != null){
			loadBitmapThread.cancel(true);
		}
		if (loadThumbBitmapThread != null){
			loadThumbBitmapThread.cancel(true);
		}
		if (loadBitmapThreadAgain != null){
			loadBitmapThreadAgain.cancel(true);
		}
		
		mHandler.post(new Runnable() {
    		public void run() {
		    	refresh = true;
		    	cut = 0;
		    	number = 0;
		    	lastcut = false;
		    	list.clear();
		    	
		    	if (list.isEmpty()){
		    		TaskQueueImage.addTask(new updateListThread(), CampusWallComment.this);
				}
		    }
		});
    }
	
	public class CampusWallCommentArrayAdapter extends ArrayAdapter<CampusWallModel> {
		private final List<CampusWallModel> list;
		
		class ViewHolder {
			public ImageView ivThumb;
			public TextView display_name;
			public ImageView ivImage;
			public ImageView ivImageBackground;
			public TextView time;
			public TextView message;
			public Button bComment;
			public Button bLike;
			public Button bMore;
			public ProgressBar pbLoading;
			public RelativeLayout bg;
		}

		public CampusWallCommentArrayAdapter(Context context, List<CampusWallModel> list) {
			super(context, R.layout.campuswallrow, list);
			this.list = list;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null){
				LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.campuswallrow, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.ivThumb = (ImageView) rowView.findViewById(R.id.ivThumb);
				viewHolder.display_name = (TextView) rowView.findViewById(R.id.display_name);
				viewHolder.ivImage = (ImageView) rowView.findViewById(R.id.ivImage);
				viewHolder.ivImageBackground = (ImageView) rowView.findViewById(R.id.ivImageBackground);
				viewHolder.time = (TextView) rowView.findViewById(R.id.time);
				viewHolder.message = (TextView) rowView.findViewById(R.id.message);
				viewHolder.bComment = (Button) rowView.findViewById(R.id.bComment);
				viewHolder.bLike = (Button) rowView.findViewById(R.id.bLike);
				viewHolder.bMore = (Button) rowView.findViewById(R.id.bMore);
				viewHolder.pbLoading = (ProgressBar) rowView.findViewById(R.id.pbLoading);
				viewHolder.bg = (RelativeLayout) rowView.findViewById(R.id.bg);
				
				rowView.setTag(viewHolder);
			}
			
			final ViewHolder holder = (ViewHolder) rowView.getTag();
		
			holder.pbLoading.setVisibility(View.GONE);
			holder.ivThumb.setImageBitmap(null);
			holder.ivThumb.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.avatar));
			holder.ivImage.setImageBitmap(null);
			holder.ivImageBackground.setBackgroundDrawable(null);
			holder.time.setText("");
			holder.message.setText("");
			holder.bLike.setText("");
			holder.bComment.setText("");
			holder.bComment.setVisibility(View.GONE);
			holder.bComment.setClickable(false);
			holder.display_name.setText("");
			holder.bLike.setVisibility(View.VISIBLE);
			holder.bLike.setClickable(true);
			holder.bLike.setTextColor(getResources().getColorStateList(R.color.dimgrey3));
			holder.bLike.setBackgroundResource(R.drawable.button_like);
			holder.bMore.setVisibility(View.VISIBLE);
			
			if (list.get(position).display_name == null){
				holder.pbLoading.setVisibility(View.VISIBLE);
				holder.bLike.setVisibility(View.GONE);
				holder.bLike.setClickable(false);
				holder.bComment.setVisibility(View.GONE);
				holder.bComment.setClickable(false);
			} else {
				holder.bLike.setTypeface(Fonts.getOpenSansBold(getApplicationContext()));
				holder.bLike.setText(String.valueOf(list.get(position).likes));
				if (list.get(position).user_like == -1 || list.get(position).user_like == 0){
					holder.bLike.setOnClickListener(new Button.OnClickListener() {
						public void onClick(View v) {
							list.get(position).likes++;
							list.get(position).user_like = 1;
							holder.bLike.setClickable(false);
							holder.bLike.setText(String.valueOf(list.get(position).likes));
							holder.bLike.setTextColor(getResources().getColorStateList(R.color.white1));
							holder.bLike.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.button_liked));
							listView.invalidateViews();
							
							if (list.get(position).thread){
								new likeThread(list.get(position).school_thread_id).start();
								/*
								if (result.getResponseCode() == 200) {
									list.get(position).likes++;
									list.get(position).user_like = 1;
									holder.bLike.setClickable(false);
									holder.bLike.setText(String.valueOf(list.get(position).likes));
									holder.bLike.setTextColor(getResources().getColorStateList(R.color.white1));
									holder.bLike.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.button_liked));
									listView.invalidateViews();
								}
								*/
							} else {
								new likeComment(list.get(position).school_thread_id).start();
								/*
								if (result.getResponseCode() == 200) {
									list.get(position).likes++;
									list.get(position).user_like = 1;
									holder.bLike.setClickable(false);
									holder.bLike.setText(String.valueOf(list.get(position).likes));
									holder.bLike.setTextColor(getResources().getColorStateList(R.color.white1));
									holder.bLike.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.button_liked));
									listView.invalidateViews();
								}
								*/
							}
						}
				    }); 
				} else if (list.get(position).user_like == 1){
					holder.bLike.setClickable(false);
					holder.bLike.setTextColor(getResources().getColorStateList(R.color.white1));
					holder.bLike.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.button_liked));
				}
				
				if (list.get(position).thumb_bitmap != null){
					//image save into the memory and cache
					holder.ivThumb.setImageDrawable(null);
					holder.ivThumb.setImageBitmap(list.get(position).thumb_bitmap);
					holder.ivThumb.setOnClickListener(new View.OnClickListener() {
			            public void onClick(View view) {
			            	if (!list.get(position).is_anonymous){
				            	Bundle extras = new Bundle();
								extras.putInt("user_id", list.get(position).user_id);
								Intent i = new Intent(getApplicationContext(), OthersProfile.class);
								i.putExtras(extras);
								startActivity(i);
			            	}
			            }
			        }); 
				} 
				
				if (list.get(position).is_anonymous){
					holder.display_name.setTextColor(getResources().getColorStateList(R.color.black1));
				} else {
					holder.display_name.setTextColor(getResources().getColorStateList(R.color.blue0));
				}
				holder.display_name.setTypeface(Fonts.getOpenSansBold(getApplicationContext()));
				holder.display_name.setText(list.get(position).display_name);
				holder.display_name.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View view) {
		            	if (!list.get(position).is_anonymous){
			            	Bundle extras = new Bundle();
							extras.putInt("user_id", list.get(position).user_id);
							Intent i = new Intent(getApplicationContext(), OthersProfile.class);
							i.putExtras(extras);
							startActivity(i);
		            	}
		            }
		        }); 
				
				if (list.get(position).image_url != null){
					holder.pbLoading.setVisibility(View.VISIBLE);
					
					if (picWidth > 0){
						int y = picWidth/2;
						if (imageBg == null){
							imageBg = new BitmapDrawable(getResources(), NinePatch.getNinePatch(R.drawable.backgroundimage, picWidth, y, getApplicationContext()));
						}
						holder.ivImageBackground.setBackgroundDrawable(imageBg);
					}
					
					if (list.get(position).bitmap != null){
						if (picWidth > 0){
							holder.pbLoading.setVisibility(View.GONE);
							holder.ivImage.setImageBitmap(list.get(position).RecBitmap);
							if (!list.get(position).loaded){
								//holder.ivImage.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade)); //Set animation to your ImageView
								list.get(position).loaded = true;
							}
						}
						
						holder.ivImage.setOnClickListener(new Button.OnClickListener() {
							public void onClick(View v) {
								//OohlalaMain.tabs.setVisibility(View.GONE);
								rlBigImage.setVisibility(View.VISIBLE);
								//tvWords.setText(list.get(position).message);
								if (list.get(position).bitmap != null){
									ivBigImage.setImageBitmap(Bitmap.createScaledBitmap(list.get(position).bitmap, picWidth, (int) (((double) picWidth/list.get(position).bitmap.getWidth())*list.get(position).bitmap.getHeight()), false));			
									rlBigImage.setOnClickListener(new Button.OnClickListener() {
										public void onClick(View v) {
											//OohlalaMain.tabs.setVisibility(View.VISIBLE);
											rlBigImage.setVisibility(View.GONE);
										}
									});
									pbImageLoading.setVisibility(View.GONE);
								}
								/*
								Bundle extras = new Bundle();
								extras.putString("image_url", list.get(position).image_url);
								extras.putString("image_words", list.get(position).message);
								extras.putInt("picWidth", picWidth);
								Intent i = new Intent(getApplicationContext(), CampusWallImage.class);
								i.putExtras(extras);
								startActivity(i);
								*/
							}
					    }); 
					} else {
						if (picWidth > 0){
							list.get(position).loaded = false;
						}
					}	
				} else {
					holder.ivImageBackground.setBackgroundDrawable(null);
					holder.ivImage.setImageBitmap(null);
				}
				
				holder.time.setTypeface(Fonts.getOpenSansLightItalic(getApplicationContext()));
				holder.time.setText(list.get(position).added_time);
				holder.message.setTypeface(Fonts.getOpenSansRegular(getApplicationContext()));
				holder.message.setText(list.get(position).message);
				
				//Log.i("display thread: ", rowView.toString());
				
				if (Profile.userId != list.get(position).user_id){
					holder.bMore.setVisibility(View.GONE);
				}
				holder.bMore.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						if (Profile.userId == list.get(position).user_id){
							showAlertDialog(list.get(position).school_thread_id, list.get(position).thread);
						}
					}
			    }); 
			}
			
			return rowView;
		}
		
		private void showAlertDialog(final String thread_id, final boolean thread) {
			// TODO Auto-generated method stub
	    	AlertDialog alert = new AlertDialog.Builder(CampusWallComment.this).create();
	    	alert.setTitle(getString(R.string.Delete_Comment));
			alert.setMessage(getString(R.string.Warning_This_cannot_be_undone));
			alert.setButton(getString(R.string.No), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			});
			alert.setButton2(getString(R.string.Yes), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					RestClient result = null;
					try {
						if (thread_type == 0){
							if (thread){
								result = new Rest.request().execute(Rest.CAMPUS_THREAD + thread_id, Rest.OSESS + Profile.sk, Rest.DELETE).get();
							} else {
								result = new Rest.request().execute(Rest.CAMPUS_COMMENT + thread_id, Rest.OSESS + Profile.sk, Rest.DELETE).get();
							}
						} else if (thread_type == 1){
							if (thread){
								result = new Rest.request().execute(Rest.GROUP_THREAD + thread_id, Rest.OSESS + Profile.sk, Rest.DELETE).get();
							} else {
								result = new Rest.request().execute(Rest.GROUP_COMMENT + thread_id, Rest.OSESS + Profile.sk, Rest.DELETE).get();
							}
						}
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Log.i("delete comment", result.getResponse());
	            	
	            	if (result.getResponseCode() == 204){
	            		Toast.makeText(getApplicationContext(), getString(R.string.The_comment_is_deleted), Toast.LENGTH_SHORT).show();
	            		refreshAfterPost();
	            	}
				}
			});
			alert.show();
		}
		
	}
	
	class updateListThread2 extends Thread {
	    // This method is called when the thread runs
		List<CampusWallModel> listTemp = new ArrayList<CampusWallModel>();
	
	    public void run() {
			int start = cut*25 + 1;
			int end = cut*25 + 25;
			RestClient result2 = null;
			try {
				if (thread_type == 0){
					result2 = new request().execute(Rest.CAMPUS_COMMENT + start + ";" + end + "?thread_id=" + school_thread_id, Rest.OSESS + Profile.sk, Rest.GET).get();
				} else if (thread_type == 1){
					result2 = new request().execute(Rest.GROUP_COMMENT + start + ";" + end + "?thread_id=" + school_thread_id, Rest.OSESS + Profile.sk, Rest.GET).get();
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray comments = new JSONArray(result2.getResponse());
				Log.i("comments :", comments.toString());
				for (int i = 0; i < comments.length(); i++){
					boolean has_image = comments.getJSONObject(i).getBoolean("has_image");
					String avatar_thumb = comments.getJSONObject(i).getString("avatar_thumb");
					String message = comments.getJSONObject(i).getString("comment");
					String display_name = comments.getJSONObject(i).getString("display_name");
					String image_url = null;
					if(has_image){
						image_url = comments.getJSONObject(i).getString("image_url");
					}
					int added_time = comments.getJSONObject(i).getInt("added_time");
					int likes = comments.getJSONObject(i).getInt("likes");
					int user_like = comments.getJSONObject(i).getInt("user_like"); // -1: you haven't vote, 0: disliked it, 1: liked it
					int post_type = comments.getJSONObject(i).getInt("post_type"); // 1: normal post, 2: question post, 3: image post
					int school_buzz_comment_id = comments.getJSONObject(i).getInt("id");
					int user_id = 0;
					if (!comments.getJSONObject(i).getBoolean("is_anonymous")){
						user_id = comments.getJSONObject(i).getInt("user_id");
					}
					
					boolean is_anonymous = comments.getJSONObject(i).getBoolean("is_anonymous");
					
					listTemp.add(new CampusWallModel(avatar_thumb, display_name, image_url, message, TimeCounter.convertDate(added_time), 0, likes, 0, String.valueOf(school_buzz_comment_id), null, null, null, user_id, user_like, true, is_anonymous, false, false, String.valueOf(added_time)));
					number++;
				}
				
				if (comments.length() == 25){
					
				} else {
					lastcut = true;
				}	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Collections.sort(listTemp, new TimeComparator());
			
			if (currentView){
				mHandler.post(new Runnable() {
	        		public void run() {
	        			refresh = false; //set the refresh to false after the refresh
	        	    	
	        			llLoading.setVisibility(View.GONE);
	        			
	        	    	//listView.setVisibility(View.GONE);
	        	    	list.addAll(listTemp);
	        	    	listView.invalidateViews();
	        	    	adapter.notifyDataSetChanged();
	        	    	//listView.setVisibility(View.VISIBLE);
	        	    	
	        	    	preLoadImages();
	        	    	preLoadThumbImages();
	        	    	
	        	    	mPullRefreshListView.onRefreshComplete();
	        	    	
	        	    	listTemp.clear();
	        			listTemp = null;
	        		}
				});
			}
		}
	}
	
	class updateListThread extends Thread {
	    // This method is called when the thread runs
		List<CampusWallModel> listTemp = new ArrayList<CampusWallModel>();
		List<CampusWallModel> listTemp_comment = new ArrayList<CampusWallModel>();
		
	    public void run() {
			RestClient result = null;
			try {
				if (thread_type == 0){
					result = new request().execute(Rest.CAMPUS_THREAD + school_thread_id, Rest.OSESS + Profile.sk, Rest.GET).get();
				} else if (thread_type == 1){
					result = new request().execute(Rest.GROUP_THREAD + school_thread_id, Rest.OSESS + Profile.sk, Rest.GET).get();
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONObject thread = new JSONObject(result.getResponse());
				Log.i("school thread: ", thread.toString());

				boolean has_image = thread.getBoolean("has_image");
				String avatar_thumb = thread.getString("avatar_thumb");
				String message = thread.getString("message");
				String display_name = thread.getString("display_name");
				String image_url = null;
				if(has_image){
					image_url = thread.getString("image_url");
				}
				int added_time = thread.getInt("added_time");
				int likes = thread.getInt("likes");
				int user_like = thread.getInt("user_like"); // -1: you haven't vote, 0: disliked it, 1: liked it
				int post_type = thread.getInt("post_type"); // 1: normal post, 2: question post, 3: image post
				int user_id = 0;
				if (!thread.getBoolean("is_anonymous")){
					user_id = thread.getInt("user_id");
				}
				
				boolean is_anonymous = thread.getBoolean("is_anonymous");
						
				listTemp.add(new CampusWallModel(avatar_thumb, display_name, image_url, message, TimeCounter.convertDate(added_time), 0, likes, 0, String.valueOf(school_thread_id), null, null, null, user_id, user_like, true, is_anonymous, false, true, String.valueOf(added_time)));		
						
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int start = cut*25 + 1;
			int end = cut*25 + 25;
			RestClient result2 = null;
			try {
				if (thread_type == 0){
					result2 = new request().execute(Rest.CAMPUS_COMMENT + start + ";" + end + "?thread_id=" + school_thread_id, Rest.OSESS + Profile.sk, Rest.GET).get();
				} else if (thread_type == 1){
					result2 = new request().execute(Rest.GROUP_COMMENT + start + ";" + end + "?thread_id=" + school_thread_id, Rest.OSESS + Profile.sk, Rest.GET).get();
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray comments = new JSONArray(result2.getResponse());
				Log.i("comments :", comments.toString());
				for (int i = 0; i < comments.length(); i++){
					boolean has_image = comments.getJSONObject(i).getBoolean("has_image");
					String avatar_thumb = comments.getJSONObject(i).getString("avatar_thumb");
					String message = comments.getJSONObject(i).getString("comment");
					String display_name = comments.getJSONObject(i).getString("display_name");
					String image_url = null;
					if(has_image){
						image_url = comments.getJSONObject(i).getString("image_url");
					}
					int added_time = comments.getJSONObject(i).getInt("added_time");
					int likes = comments.getJSONObject(i).getInt("likes");
					int user_like = comments.getJSONObject(i).getInt("user_like"); // -1: you haven't vote, 0: disliked it, 1: liked it
					int post_type = comments.getJSONObject(i).getInt("post_type"); // 1: normal post, 2: question post, 3: image post
					int school_buzz_comment_id = comments.getJSONObject(i).getInt("id");
					int user_id = 0;
					if (!comments.getJSONObject(i).getBoolean("is_anonymous")){
						user_id = comments.getJSONObject(i).getInt("user_id");
					}
					
					boolean is_anonymous = comments.getJSONObject(i).getBoolean("is_anonymous");
					
					listTemp_comment.add(new CampusWallModel(avatar_thumb, display_name, image_url, message, TimeCounter.convertDate(added_time), 0, likes, 0, String.valueOf(school_buzz_comment_id), null, null, null, user_id, user_like, true, is_anonymous, false, false, String.valueOf(added_time)));
					number++;
				}
				
				if (comments.length() == 25){
					
				} else {
					lastcut = true;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Collections.sort(listTemp_comment, new TimeComparator());
			listTemp.addAll(listTemp_comment);
			
			if (currentView){
				mHandler.post(new Runnable() {
	        		public void run() {
	        			refresh = false; //set the refresh to false after the refresh
	        	    	
	        			llLoading.setVisibility(View.GONE);
	        			
	        	    	//listView.setVisibility(View.GONE);
	        	    	list.addAll(listTemp);
	        	    	listView.invalidateViews();
	        	    	adapter.notifyDataSetChanged();
	        	    	//listView.setVisibility(View.VISIBLE);
	        	    	
	        	    	preLoadImages();
	        	    	preLoadThumbImages();
	        	    	
	        	    	mPullRefreshListView.onRefreshComplete();
	        	    	
	        	    	listTemp.clear();
	        			listTemp = null;
	        			listTemp_comment.clear();
	        			listTemp_comment = null;
	        		}
				});
			}
		}
	}
	
	public class TimeComparator implements Comparator<CampusWallModel> {
        public int compare(CampusWallModel o1, CampusWallModel o2) {
            return o1.added_time_compare.compareTo(o2.added_time_compare);
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
	    	//Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	for (int i = start; i < stop; i++){
	    		if (i < list.size() && currentView){
					if (list.get(i).image_url != null && (list.get(i).bitmap == null || list.get(i).RecBitmap == null)){
						list.get(i).bitmap = ImageLoader.campusWallImageStoreAndLoad(list.get(i).image_url, getApplicationContext(), 512);
						int y2 = picWidth/2;
						if (list.get(i).bitmap != null){
							list.get(i).RecBitmap = Bitmap.createScaledBitmap(ImageLoader.ImageCropRectangular(list.get(i).bitmap, picWidth, y2), picWidth, y2, false);
						}
						
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
	
	class loadBitmapThreadAgain extends AsyncTask<Integer, Void, Void> {
	    // This method is called when the thread runs
		public int start, stop;
		boolean invalidateViews = false;

		protected Void doInBackground(Integer... num) {
	    	//get the all the current games
			this.start = num[0];
			this.stop = num[1];
	    	//Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	for (int i = start; i < stop; i++){
	    		if (i < list.size() && currentView){
					if (list.get(i).image_url != null && (list.get(i).bitmap == null || list.get(i).RecBitmap == null)){
						//Log.i("image", list.get(i).image_url);
						list.get(i).first_load = false;
						list.get(i).bitmap = ImageLoader.campusWallImageStoreAndLoad(list.get(i).image_url, getApplicationContext(), 512);
						int y2 = picWidth/2;
						if (list.get(i).bitmap != null){
							list.get(i).RecBitmap = Bitmap.createScaledBitmap(ImageLoader.ImageCropRectangular(list.get(i).bitmap, picWidth, y2), picWidth, y2, false);
						}
						
						invalidateViews = true;
						mHandler.post(new Runnable() {
			        		public void run() {
								if (invalidateViews){
									listView.invalidateViews();
								}
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
	
	public void preLoadThumbImages() {
		// TODO Auto-generated method stub		
		loadThumbBitmapThread = new loadThumbBitmapThread();
    	loadThumbBitmapThread.execute(0, list.size());
	}
	
	class loadThumbBitmapThread extends AsyncTask<Integer, Void, Void> {
	    // This method is called when the thread runs
		public int start, stop;

		protected Void doInBackground(Integer... num) {
	    	//get the all the current games
			this.start = num[0];
			this.stop = num[1]; 
	    	//Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	for (int i = start; i < stop; i++){
	    		if (i < list.size() && currentView){
					if (list.get(i).avatar_thumb != null && list.get(i).avatar_thumb.contains(".png") && list.get(i).thumb_bitmap == null){
						//Log.i("image", list.get(i).image_url);
						list.get(i).thumb_bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(list.get(i).avatar_thumb, getApplicationContext(), 60);
						if (list.get(i).thumb_bitmap != null){
							list.get(i).thumb_bitmap = RoundedCornerImage.getRoundedCornerBitmap(list.get(i).thumb_bitmap, 8);
						}
						
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
	
	public void onResume() {
		super.onResume();
		
		currentView = true;
	
		new resumeBitmap().execute();
	}
	
	class resumeBitmap extends AsyncTask<Void, Void, Void> {
	    // This method is called when the thread runs
		protected Void doInBackground(Void... params) {
			for (int i = 0; i < list.size(); i++){
				if (i < list.size() && currentView){
					if (list.get(i).image_url != null && list.get(i).bitmap == null){
						list.get(i).bitmap = ImageLoader.campusWallImageStoreAndLoad(list.get(i).image_url, getApplicationContext(), 512);
						mHandler.post(new Runnable() {
			        		public void run() {
						    	listView.invalidateViews();
						    }
						});
					}
					if (list.get(i).avatar_thumb != null && list.get(i).avatar_thumb.contains(".png") && list.get(i).thumb_bitmap == null){
						list.get(i).thumb_bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(list.get(i).avatar_thumb, getApplicationContext(), 60);
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
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		currentView = false;
		
		for (int i = 0; i < list.size(); i++){
			if (i < list.size()){
				if (list.get(i).bitmap != null){
					list.get(i).bitmap.recycle();
					list.get(i).bitmap = null;
				}
				if (list.get(i).thumb_bitmap != null){
					list.get(i).thumb_bitmap.recycle();
					list.get(i).thumb_bitmap = null;
				}
			} else {
				break;
			}
		}	
	}
	
	class likeThread extends Thread {
	    // This method is called when the thread runs
		String school_thread_id;
		
		public likeThread(String school_thread_id){
			this.school_thread_id = school_thread_id;
		}
		
	    public void run() {
	    	RestClient result = null;
			try {
				if (thread_type == 0){
					result = new Rest.requestBody().execute(Rest.CAMPUS_THREAD + school_thread_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "like", "1").get();
				} else if (thread_type == 1){
					result = new Rest.requestBody().execute(Rest.GROUP_THREAD + school_thread_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "like", "1").get();
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("like thread", result.getResponse());
	    }
	}
	
	class likeComment extends Thread {
	    // This method is called when the thread runs
		String school_thread_id;
		
		public likeComment(String school_thread_id){
			this.school_thread_id = school_thread_id;
		}
		
	    public void run() {
	    	RestClient result = null;
			try {
				if (thread_type == 0){
					result = new Rest.requestBody().execute(Rest.CAMPUS_COMMENT + school_thread_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "like", "1").get();
				} else if (thread_type == 1){
					result = new Rest.requestBody().execute(Rest.GROUP_COMMENT + school_thread_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "like", "1").get();
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("like comment", result.getResponse());
	    }
	}
	
}	
