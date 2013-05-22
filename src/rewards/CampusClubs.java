package rewards;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;

import profile.Friends;
import profile.ProfileSettings;

import rewards.Explore.ExploreArrayAdapter;
import rewards.Explore.loadBitmapTread;
import rewards.Explore.updateListThread;
import rewards.Explore.ExploreArrayAdapter.ViewHolder;
import user.Profile;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.ScrollerContainer.OnSlideListener;
import com.gotoohlala.TopMenuNavbar;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import datastorage.Rest.request;
import events.EventsCampusStoresDetail;
import events.EventsEventsMap;

public class CampusClubs extends FrameLayout {
	ListView listView;
	PullToRefreshListView mPullRefreshListView;
	List<ExploreModel> list = new ArrayList<ExploreModel>();
	ExploreArrayAdapter adapter;
	
	loadBitmapTread loadBitmapTread;
	
	Handler mHandler = new Handler();
	
	boolean currentView = false;
	
	View v = null;
	 
	public TopMenuNavbar TopMenuNavbar;
	 
	public CampusClubs(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
		v = (RelativeLayout) mLayoutInflater.inflate(R.layout.campusclubs, null);  
	    addView(v);  
		
		currentView = true;
		
		TextView headerUserProfile = (TextView) v.findViewById(R.id.header);
		headerUserProfile.setTypeface(Fonts.getOpenSansBold(getContext()));
		
		TopMenuNavbar = (TopMenuNavbar) v.findViewById(R.id.bTopMenuNavBar);

		mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.lvCampusClubs);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(getContext().getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
				reloadView();
			}
		});
		mPullRefreshListView.setRefreshing();
		
		listView = mPullRefreshListView.getRefreshableView();
		adapter = new ExploreArrayAdapter(getContext(), list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				position--;
				if (list.get(position).total_item_count == 0){
					Bundle extras = new Bundle();
					extras.putString("name", list.get(position).name);
					extras.putString("store_id", String.valueOf(list.get(position).featured_club_id));
					if (list.get(position).image_bitmap != null){
						extras.putString("image_url", list.get(position).logo_url);
					}
					extras.putDouble("lat", list.get(position).lat);
					extras.putDouble("longi", list.get(position).longi);
					extras.putString("card_code_name", "");
					extras.putBoolean("user_fav", list.get(position).user_fav);
					
					Intent i = new Intent(getContext(), RewardsVenuesStoresContent.class);
					i.putExtras(extras);
					getContext().startActivity(i);
				} else {
					Bundle extras = new Bundle();
					extras.putString("name", list.get(position).name);
					extras.putString("store_id", String.valueOf(list.get(position).featured_club_id));
					if (list.get(position).image_bitmap != null){
						extras.putString("image_url", list.get(position).logo_url);
					}
					extras.putDouble("lat", list.get(position).lat);
					extras.putDouble("longi", list.get(position).longi);
					extras.putString("card_code_name", "");
					extras.putBoolean("user_fav", list.get(position).user_fav);
					
					Intent i = new Intent(getContext(), RewardsVenuesStoresContent.class);
					i.putExtras(extras);
					getContext().startActivity(i);
				}
			}
		});
		
		TaskQueueImage.addTask(new updateListThread(), getContext());
	}
	
	public void reloadView() {
        // Do work to refresh the list here.
		mHandler.post(new Runnable() {
    		public void run() {
		    	list.clear();
		    	
		    	if (list.isEmpty()){
		    		TaskQueueImage.addTask(new updateListThread(), getContext());
				}
		    }
		});
    }
	
	class updateListThread extends Thread {
	    // This method is called when the thread runs
		List<ExploreModel> listTemp = new ArrayList<ExploreModel>();
		
	    public void run() {
			RestClient result = null;
			try {
				result = new request().execute(Rest.STORE + "?category_id=-1", Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray threads = new JSONArray(result.getResponse());
				Log.i("campus clubs: ", threads.toString());
				
				for (int i = 0; i < threads.length(); i++){
					int featured_club_id = threads.getJSONObject(i).getInt("id");
					int franchise_id = 0;
					if (threads.getJSONObject(i).has("franchise_id")){
						franchise_id = threads.getJSONObject(i).getInt("franchise_id");
					}
					String franchise_logo_url = null;
					if (threads.getJSONObject(i).has("franchise_logo_url")){
						franchise_logo_url = threads.getJSONObject(i).getString("franchise_logo_url");
					}
					int total_item_count = threads.getJSONObject(i).getInt("total_item_count");
					int category_id = threads.getJSONObject(i).getInt("category_id");
					String name = threads.getJSONObject(i).getString("name");
					String franchise_name = null;
					if (threads.getJSONObject(i).has("franchise_name")){
						franchise_name = threads.getJSONObject(i).getString("franchise_name");
					}
					double lat = threads.getJSONObject(i).getDouble("latitude");
					double longi = threads.getJSONObject(i).getDouble("longitude");
					boolean user_fav = threads.getJSONObject(i).getBoolean("user_fav");
					String item_preview = threads.getJSONObject(i).getString("item_preview");
					
					String logo_url = null;
					if (threads.getJSONObject(i).has("logo_url")){
						logo_url = threads.getJSONObject(i).getString("logo_url");
					}
					
					listTemp.add(new ExploreModel(featured_club_id, franchise_id, total_item_count, category_id, franchise_logo_url, name, franchise_name, null, lat, longi, user_fav, logo_url, item_preview));
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (currentView){
				mHandler.post(new Runnable() {
		        	public void run() {
				    	//listView.setVisibility(View.GONE);
				    	list.addAll(listTemp);
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
	
	public void preLoadImages() {
		// TODO Auto-generated method stub
		loadBitmapTread = new loadBitmapTread();
		loadBitmapTread.execute(0, list.size());
	}
	
	class loadBitmapTread extends AsyncTask<Integer, Void, Void> {
	    // This method is called when the thread runs
		public int start, stop;

		protected Void doInBackground(Integer... num) {
	    	//get the all the current games
			this.start = num[0];
			this.stop = num[1];
	    	Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	for (int i = start; i < stop; i++){
	    		if (i < list.size() && currentView){
					if (list.get(i).logo_url != null && list.get(i).image_bitmap == null){
						list.get(i).image_bitmap = ImageLoader.KembrelImageStoreAndLoad(list.get(i).logo_url, getContext());
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
	
	public class ExploreArrayAdapter extends ArrayAdapter<ExploreModel> {
		private final List<ExploreModel> list;
		
		class ViewHolder {
			public ImageView ivThumb;
			public TextView name;
			public TextView preview;
		}

		public ExploreArrayAdapter(Context context, List<ExploreModel> list) {
			super(context, R.layout.exploresrow, list);
			this.list = list;
		}
		
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null){
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.exploresrow, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.ivThumb = (ImageView) rowView.findViewById(R.id.ivThumb);
				viewHolder.name = (TextView) rowView.findViewById(R.id.name);
				viewHolder.preview = (TextView) rowView.findViewById(R.id.preview);
				
				rowView.setTag(viewHolder);
			}
			
			ViewHolder holder = (ViewHolder) rowView.getTag();
			
			holder.ivThumb.setImageBitmap(null);
			holder.name.setText("");	
			holder.preview.setText("");	
			
			if (list.get(position).image_bitmap != null){
				//image save into the memory and cache
				holder.ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(list.get(position).image_bitmap, 8));
			}
			
			holder.name.setTypeface(Fonts.getOpenSansBold(getContext()));
			holder.name.setText(StringLanguageSelector.retrieveString(list.get(position).name));
			holder.preview.setText(list.get(position).item_preview);
			
			return rowView;
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
    		intent.addCategory(Intent.CATEGORY_HOME);
    		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		getContext().startActivity(intent);
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void onPause() {
		// TODO Auto-generated method stub
		
		currentView = false;
	}
	
}
