package com.gotoohlala;

import inbox.InboxModel;
import inbox.Inbox.TimeComparator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import network.ErrorCodeParser;
import network.RetrieveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.Badge;
import profile.Friends;
import profile.Schedule;
import profile.SearchCourse;

import rewards.RewardsModel;
import user.Profile;

import ManageThreads.TaskQueue;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import campuswall.CampusWall;
import campuswall.CampusWallComment;

import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.TopMenuNavbar;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import datastorage.CacheInternalStorage;
import datastorage.ConvertDpsToPixels;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import datastorage.Rest.request;
import events.EventsEventsDetail;
import events.EventsEventsModel;

public class EventsLeftPanel extends FrameLayout {
	EventsEventsArrayAdapter adapter;
	ListView listView;
	PullToRefreshListView mPullRefreshListView;
	List<EventsEventsModel> list = new ArrayList<EventsEventsModel>();
	
	private Handler mHandler = new Handler();
	
	loadThumbBitmapThread loadThumbBitmapThread;
	
	int cut = 0;
	int number = 0;
	boolean lastcut = false;
	LinearLayout llLoading;

	boolean currentView = false;
	
	int epoch_start_day, epoch_end_day;
	
	View v = null;
	 
	public TopMenuNavbar TopMenuNavbar;
	
	public EventsLeftPanel(Context context) {
		super(context);
		
		final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
		v = (RelativeLayout) mLayoutInflater.inflate(R.layout.eventsleftpanel, null);  
	    addView(v);  
		
		currentView = true;
		
		TextView header = (TextView) v.findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(getContext()));
		
		TopMenuNavbar = (TopMenuNavbar) v.findViewById(R.id.bTopMenuNavBar);
		
		epoch_start_day = TimeCounter.getEpochTimeOnDate(Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_MONTH), Calendar.getInstance(Locale.getDefault()).get(Calendar.MONTH), Calendar.getInstance(Locale.getDefault()).get(Calendar.YEAR));
		epoch_end_day = TimeCounter.getEpochTimeOnDate(Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_MONTH) + 1, Calendar.getInstance(Locale.getDefault()).get(Calendar.MONTH), Calendar.getInstance(Locale.getDefault()).get(Calendar.YEAR));
		//Log.i("epoch_start_day", String.valueOf(epoch_start_day));
		//Log.i("epoch_end_day", String.valueOf(epoch_end_day));
		
		/*
		Button bAdd = (Button) v.findViewById(R.id.bAdd);
		bAdd.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), AddEvent.class);
				startActivity(i);
			}
		}); 
		*/
		
		llLoading = (LinearLayout) findViewById(R.id.llLoading);
		
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.lvEventsEvents);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(getContext().getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
				
				reloadView();
			}
		});
		
		listView = mPullRefreshListView.getRefreshableView();
		adapter = new EventsEventsArrayAdapter(getContext(), list);
		listView.setOnScrollListener(new OnScrollListener(){
			
			public int first, last;
			
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		        // TODO Auto-generated method stub
		    	this.first = firstVisibleItem;
		    	this.last = firstVisibleItem + visibleItemCount - 1;
		    }
		 	
		    public void onScrollStateChanged(AbsListView view, int scrollState) {
		    	// TODO Auto-generated method stub
			   	if(scrollState == 0){
			   		if (last >= number && !lastcut){
			   			//listView.scrollBy(0, 120);
			   			llLoading.setVisibility(View.VISIBLE);
				    	
			    		cut++;
			    		TaskQueueImage.addTask(new getEventThread(), getContext());
			    	} 
			    }
			}
		});
		
		listView.setAdapter(adapter);
		
		if (!OohlalaMain.t1){
			refreshAfterPost();
			OohlalaMain.t1 = true;
		}
	}
	
	public void reloadView() {
        // Do work to refresh the list here.
		if (loadThumbBitmapThread != null){
			loadThumbBitmapThread.cancel(true);
		}

		mHandler.post(new Runnable() {
    		public void run() {
		    	cut = 0;
		    	number = 0;
		    	lastcut = false;
		    	list.clear();
		    	
		    	if (list.isEmpty()){
		    		TaskQueueImage.addTask(new getEventThread(), getContext());
				}
		    }
		});
    }
	
	public void refreshAfterPost() {
		// TODO Auto-generated method stub
		mPullRefreshListView.setRefreshing();
 		reloadView();
	}
	
	public class EventsEventsArrayAdapter extends ArrayAdapter<EventsEventsModel> {
		private final Context context;
		private final List<EventsEventsModel> list;
		boolean hasFeaturedEvent = false;
		
		class ViewHolder {
			public ImageView ivThumb, bgFeaturedBlur;
			public TextView title, tvTime;
			public Button bAttending;
			public TextView startTime;
			public RelativeLayout bg;
		}

		public EventsEventsArrayAdapter(Context context, List<EventsEventsModel> list) {
			super(context, R.layout.eventseventsrow, list);
			this.context = context;
			this.list = list;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null){
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.eventseventsrow, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.ivThumb = (ImageView) rowView.findViewById(R.id.ivThumb);
				viewHolder.title = (TextView) rowView.findViewById(R.id.title);
				viewHolder.tvTime = (TextView) rowView.findViewById(R.id.tvTime);
				viewHolder.bAttending = (Button) rowView.findViewById(R.id.bAttending);
				viewHolder.startTime = (TextView) rowView.findViewById(R.id.startTime);
				viewHolder.bg = (RelativeLayout) rowView.findViewById(R.id.bg);
					
				rowView.setTag(viewHolder);
			}		
			
			final ViewHolder holder = (ViewHolder) rowView.getTag();
			
			holder.ivThumb.setImageBitmap(null);
			holder.title.setText("");	
			holder.bAttending.setText("");
			holder.bAttending.setPadding(ConvertDpsToPixels.getPixels(20, context), 0, 0, 0);
			holder.bAttending.setBackgroundResource(R.drawable.button_attending);
			holder.startTime.setText("");
			holder.tvTime.setText("");
			holder.tvTime.setVisibility(View.GONE);
			
	
				if (list.get(position).store_bitmap != null){
					//image save into the memory and cache
					holder.ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(list.get(position).store_bitmap), 8));
				}
				
				holder.title.setTypeface(Fonts.getOpenSansBold(context));
				holder.title.setText(StringLanguageSelector.retrieveString(list.get(position).title));	
				
				if (list.get(position).user_attend == 1){
					holder.bAttending.setBackgroundResource(R.drawable.button_attended);
					holder.bAttending.setPadding(ConvertDpsToPixels.getPixels(40, context), 0, 0, 0);
					//holder.bAttending.setText(list.get(position).attends + getString(R.string._Attending));
					holder.bAttending.setText(getContext().getString(R.string._Attending));
				} else {
					//holder.bAttending.setText(list.get(position).attends + getString(R.string._Attending));
					holder.bAttending.setText(getContext().getString(R.string._Attending));
				}
				
				holder.bAttending.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						if (list.get(position).user_attend == 0){
							RestClient result = null;
							try {
								result = new Rest.requestBody().execute(Rest.EVENT + list.get(position).event_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "attend", "1").get();
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (ExecutionException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							//Log.i("user attend event", result.getResponse());
							
							int code = ErrorCodeParser.parser(result.getResponse());
							
							//if (code == 200) {
								list.get(position).attends++;
								holder.bAttending.setBackgroundResource(R.drawable.button_attended);
								holder.bAttending.setPadding(ConvertDpsToPixels.getPixels(40, context), 0, 0, 0);
								//holder.bAttending.setText(list.get(position).attends + getString(R.string._Attending));
								holder.bAttending.setText(getContext().getString(R.string._Attending));
								list.get(position).user_attend = 1;
							//}
						}
					}
				});
				
				holder.startTime.setTypeface(Fonts.getOpenSansLightItalic(getContext()));
				holder.startTime.setText(TimeCounter.EventDate(Integer.valueOf(list.get(position).start_time)));
				
				holder.bg.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						Bundle extras = new Bundle();
						extras.putString("event_id", list.get(position).event_id);
						extras.putString("title", list.get(position).title);
						extras.putString("description", list.get(position).description);
						extras.putString("store_logo", list.get(position).store_logo);
						extras.putString("start_time", list.get(position).start_time);
						extras.putString("end_time", list.get(position).end_time);
						extras.putString("store_id", list.get(position).store_id);
						extras.putInt("user_like", list.get(position).user_like);
						extras.putString("image", list.get(position).image);
						extras.putInt("user_attend", list.get(position).user_attend);
						
						Intent i = new Intent(getContext(), EventsEventsDetail.class);
						i.putExtras(extras);
						getContext().startActivity(i);
					}
			    }); 
	
				//Log.i("start_time", list.get(position).start_time);
				//Log.i("end_time", list.get(position).end_time);
				if (Integer.valueOf(list.get(position).start_time) < epoch_end_day && Integer.valueOf(list.get(position).end_time) > epoch_start_day && position == 0){
					holder.tvTime.setVisibility(View.VISIBLE);
					holder.tvTime.setText(getContext().getString(R.string.Today).toUpperCase());
				}
				
				if (Integer.valueOf(list.get(position).start_time) >= epoch_end_day && 
						(position == 0 || (Integer.valueOf(list.get(position - 1).start_time) < epoch_end_day && Integer.valueOf(list.get(position - 1).end_time) > epoch_start_day))){
					//Log.i("coming soon", "-----------------");
					holder.tvTime.setVisibility(View.VISIBLE);
					holder.tvTime.setText(getContext().getString(R.string.Coming_Soon).toUpperCase());
				}
				
				return rowView;
		}
		
	}
	
	class getEventThread extends Thread {
	    // This method is called when the thread runs
		List<EventsEventsModel> listTemp = new ArrayList<EventsEventsModel>();
		
	    public void run() {
	    	int start = cut*25 + 1;
			int end = cut*25 + 25;
	    	RestClient result = null;
			try {
				result = new request().execute(Rest.EVENT + start + ";" + end, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray events = new JSONArray(result.getResponse());
				Log.i("events: ", events.toString());
				
				for (int i = 0; i < events.length(); i++){
					String event_id = String.valueOf(events.getJSONObject(i).getInt("id"));
					String title = null;
					if (events.getJSONObject(i).has("title")){
						title = events.getJSONObject(i).getString("title");
					}
					String description = null;
					if (events.getJSONObject(i).has("description")){
						description = events.getJSONObject(i).getString("description");
					}
					String store_id = null;
					if (events.getJSONObject(i).has("store_id")){
						store_id = String.valueOf(events.getJSONObject(i).getString("store_id"));
					}
					String store_logo = events.getJSONObject(i).getString("store_logo_url");
					String end_time = String.valueOf(events.getJSONObject(i).getInt("end"));
					String start_time = String.valueOf(events.getJSONObject(i).getInt("start"));
					int user_like = events.getJSONObject(i).getInt("likes");
					String image = events.getJSONObject(i).getString("poster_url");
					String image_thumb = events.getJSONObject(i).getString("poster_thumb_url");
					
					int attends = events.getJSONObject(i).getInt("attends");
					int user_attend = events.getJSONObject(i).getInt("user_attend");
					boolean is_featured = events.getJSONObject(i).getBoolean("is_featured");
					
					listTemp.add(new EventsEventsModel(event_id, title, description, store_id, store_logo, end_time, start_time, null, user_like, image, image_thumb, attends, user_attend, is_featured));
					number++;
				}
				
				if (events.length() == 25){
					
				} else {
					lastcut = true;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			if (currentView){
				mHandler.post(new Runnable() {
	        		public void run() {
						llLoading.setVisibility(View.GONE);
						
				    	//listView.setVisibility(View.GONE);
				    	list.addAll(listTemp);
				    	Collections.sort(list, new TimeComparator());
				    	
				    	listView.invalidateViews();
				    	adapter.notifyDataSetChanged();
				    	//listView.setVisibility(View.VISIBLE);
				    	
				    	preLoadImages();
				    	
				    	mPullRefreshListView.onRefreshComplete();
				    	
				    	listTemp.clear();
						listTemp = null;
	        		}
				});
			}
	    }
	}
	
	public class TimeComparator implements Comparator<EventsEventsModel> {
        public int compare(EventsEventsModel o1, EventsEventsModel o2) {
            return o1.start_time.compareTo(o2.start_time);
        }
	}
	
	public void preLoadImages() {
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
	    	Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	for (int i = start; i < stop; i++){
	    		if (i < list.size() && currentView){
		    		if (list.get(i).image_thumb != null && list.get(i).image_thumb.contains(".png") && list.get(i).store_bitmap == null){
						//Log.i("image", list.get(i).image_url);
						list.get(i).store_bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(list.get(i).image, getContext(), 50);
						mHandler.post(new Runnable() {
			        		public void run() {
						    	listView.invalidateViews();
						    }
						});
					} else if (list.get(i).store_logo != null && list.get(i).store_logo.contains(".png") && list.get(i).store_bitmap == null){
						//Log.i("image", list.get(i).image_url);
						list.get(i).store_bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(list.get(i).store_logo, getContext(), 50);
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
	
	class loadThumbBitmapThread2 extends Thread {
	    // This method is called when the thread runs
		public int start, stop;
		
		public loadThumbBitmapThread2(int start, int stop){
			this.start = start;
			this.stop = stop;
		}
		
	    public void run() {
	    	Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	for (int i = start; i < stop; i++){
	    		if (i < list.size()){
		    		if (list.get(i).image_thumb != null && list.get(i).image_thumb.contains(".png") && list.get(i).store_bitmap == null){
						//Log.i("image", list.get(i).image_url);
						list.get(i).store_bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(list.get(i).image_thumb, getContext(), 50);
						mHandler.post(new Runnable() {
			        		public void run() {
						    	listView.invalidateViews();
						    }
						});
					} else if (list.get(i).store_logo != null && list.get(i).store_logo.contains(".png") && list.get(i).store_bitmap == null){
						//Log.i("image", list.get(i).image_url);
						list.get(i).store_bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(list.get(i).store_logo, getContext(), 50);
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
