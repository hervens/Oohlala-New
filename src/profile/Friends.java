package profile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.OthersProfile.checkFriendRequest;

import rewards.Explore.ExploreArrayAdapter;
import rewards.ExploreModel;
import rewards.RewardsVenuesStoresContent;
import user.Profile;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.gotoohlala.R;
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
import events.OthersSchedule;

public class Friends extends Activity {
	ListView listView;
	PullToRefreshListView mPullRefreshListView;
	List<FriendsModel> list = new ArrayList<FriendsModel>();
	ExploreArrayAdapter adapter;
	
	loadBitmapTread loadBitmapTread;
	
	Handler mHandler = new Handler();
	
	int user_id = 0;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends);
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		}); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(Friends.this));
		
		Bundle b = this.getIntent().getExtras();
		if (b != null){
			user_id = b.getInt("user_id");
		}
		
		Button bAdd = (Button) findViewById(R.id.bAdd);
		bAdd.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Friends.this, SearchFriend.class);
				startActivity(i);
			}
		}); 
		if (user_id != Profile.userId){
			bAdd.setVisibility(View.GONE);
		}
		
		TextView tvButton = (TextView) findViewById(R.id.tvButton);
		tvButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Friends.this, SuggestedFriends.class);
				startActivity(i);
			}
		}); 

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.lvFriends);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
				reloadView();
			}
		});
		mPullRefreshListView.setRefreshing();
		
		listView = mPullRefreshListView.getRefreshableView();
		adapter = new ExploreArrayAdapter(Friends.this, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				position--;
				
				Bundle extras = new Bundle();
				extras.putInt("user_id", list.get(position).user_id);
				Intent i = new Intent(Friends.this, OthersProfile.class);
				i.putExtras(extras);
				startActivity(i);
			}
		});
		
		TaskQueueImage.addTask(new updateListThread(), Friends.this);
	}
	
	public void reloadView() {
        // Do work to refresh the list here.
		mHandler.post(new Runnable() {
    		public void run() {
		    	list.clear();
		    	
		    	if (list.isEmpty()){
		    		TaskQueueImage.addTask(new updateListThread(), Friends.this);
				}
		    }
		});
    }
	
	class updateListThread extends Thread {
	    // This method is called when the thread runs
		List<FriendsModel> listTemp = new ArrayList<FriendsModel>();
		
	    public void run() {
	    	RestClient result = null;
			try {
				if (user_id == Profile.userId){
					result = new request().execute(Rest.USER + "1;9999" + "?friends_only=1", Rest.OSESS + Profile.sk, Rest.GET).get();
				} else {
					result = new request().execute(Rest.USER + user_id + "?with_friends=1&start=1&end=9999", Rest.OSESS + Profile.sk, Rest.GET).get();
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (user_id == Profile.userId){
				try {
					final JSONArray threads = new JSONArray(result.getResponse());
					Log.i("friends: ", threads.toString());
					
					for (int i = 0; i < threads.length(); i++){
						int user_id = threads.getJSONObject(i).getInt("id");
						String name = threads.getJSONObject(i).getString("firstname");
						String avatar_thumb_url = threads.getJSONObject(i).getString("avatar_thumb_url");
						String looking_for = threads.getJSONObject(i).getString("looking_for");
						//boolean has_schedule = threads.getJSONObject(i).getBoolean("has_schedule");
						
						listTemp.add(new FriendsModel(user_id, name, avatar_thumb_url, looking_for, true, null, null));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					final JSONArray threads = new JSONObject(result.getResponse()).getJSONArray("friend_list");
					Log.i("friends: ", threads.toString());
					
					for (int i = 0; i < threads.length(); i++){
						int user_id = threads.getJSONObject(i).getInt("id");
						String name = threads.getJSONObject(i).getString("firstname");
						String avatar_thumb_url = threads.getJSONObject(i).getString("avatar_thumb_url");
						String looking_for = threads.getJSONObject(i).getString("looking_for");
						//boolean has_schedule = threads.getJSONObject(i).getBoolean("has_schedule");
						
						listTemp.add(new FriendsModel(user_id, name, avatar_thumb_url, looking_for, true, null, null));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
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
	    		if (i < list.size()){
					if (list.get(i).avatar_thumb_url != null && list.get(i).image_bitmap == null){
						list.get(i).image_bitmap = ImageLoader.KembrelImageStoreAndLoad(list.get(i).avatar_thumb_url, Friends.this);
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
	
	public class ExploreArrayAdapter extends ArrayAdapter<FriendsModel> {
		private final List<FriendsModel> list;
		
		class ViewHolder {
			public ImageView ivThumb;
			public TextView name;
			public TextView preview;
		}

		public ExploreArrayAdapter(Context context, List<FriendsModel> list) {
			super(context, R.layout.exploresrow, list);
			this.list = list;
		}
		
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null){
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			
			holder.name.setTextColor(getResources().getColorStateList(R.color.blue0));
			holder.name.setTypeface(Fonts.getOpenSansBold(Friends.this));
			holder.name.setText(StringLanguageSelector.retrieveString(list.get(position).name));
			holder.preview.setText(list.get(position).looking_for);
			
			return rowView;
		}
		
	}
	
}
