package profile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.Friends.loadBitmapTread;
import profile.Friends.ExploreArrayAdapter.ViewHolder;

import rewards.RewardsLocationModel;

import user.Profile;
import campusmap.CampusMap;
import campusmap.CampusMapArrayAdapter;
import campusmap.CampusMapModel;
import campuswall.CampusWallInviteFacebook;
import campuswall.CampusWallModel;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.gotoohlala.R;

import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import datastorage.Rest.request;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SearchFriend extends Activity {
	EditText etSearchFriend;
	ListView lvSearchFriend;
	SearchCourseArrayAdapter adapter;
	final List<FriendsModel> list = new ArrayList<FriendsModel>();
	
	Handler mHandler = new Handler(Looper.getMainLooper());
	
	TextView tvSearchResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchfriend);
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		}); 
		
		Button bInvite = (Button) findViewById(R.id.bInvite);
		bInvite.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(SearchFriend.this, CampusWallInviteFacebook.class);
				startActivity(i);
			}
		}); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(SearchFriend.this));
		header.setText(getString(R.string.Add_Friend).toUpperCase());
		
		etSearchFriend = (EditText) findViewById(R.id.etSearchFriend);
		lvSearchFriend = (ListView) findViewById(R.id.lvSearchFriend);
		tvSearchResult = (TextView) findViewById(R.id.tvSearchResult);
		
		tvSearchResult.setText(getString(R.string.searchfriends));
		
		adapter = new SearchCourseArrayAdapter(SearchFriend.this, list);
		lvSearchFriend.setAdapter(adapter);
		lvSearchFriend.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				Bundle extras = new Bundle();
				extras.putInt("user_id", list.get(position).user_id);
				Intent i = new Intent(SearchFriend.this, OthersProfile.class);
				i.putExtras(extras);
				startActivity(i);
			}
		});
			
		etSearchFriend.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() >= 3){
					reloadView(s.toString());
				} 
		    }
			
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		     
			}
		 
			public void afterTextChanged(Editable s) {
		    	 
		    }
		});
		
		etSearchFriend.setOnKeyListener(new EditText.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
				
				if (keyCode == KeyEvent.KEYCODE_ENTER){
					String name = etSearchFriend.getText().toString();
					if (name.length() >= 3){
						//reloadView(name);
					} else {
						Toast.makeText(v.getContext(), getString(R.string.search_name_dialog_alert), Toast.LENGTH_SHORT).show();
					}	
					
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		    		imm.hideSoftInputFromWindow(etSearchFriend.getWindowToken(), 0);
		    		etSearchFriend.clearFocus();
		    		
		    		return true;
				} else if (keyCode == KeyEvent.KEYCODE_BACK){
					onBackPressed();
					return true;
				}
				return false;				
			}
		});
	}
	
	public void reloadView(final String name) {
		mHandler.post(new Runnable() {
    		public void run() {
		    	list.clear();
		    	
		    	if (list.isEmpty()){
					new updateListThread().execute(name);
				}
		    }
		});
	}

	class updateListThread extends AsyncTask<String, Void, List<FriendsModel>> {
	    // This method is called when the thread runs
		List<FriendsModel> listTemp = null;
	
		protected List<FriendsModel> doInBackground(String... names) {
	    	//get the all the current games
			listTemp = new ArrayList<FriendsModel>();
			
			RestClient result = null;
			try {
				result = new request().execute(Rest.USER + "?name=" + names[0].replace(" ", "%20"), Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				final JSONArray threads = new JSONArray(result.getResponse());
				Log.i("friends: ", threads.toString());
					
				for (int i = 0; i < threads.length(); i++){
					int user_id = threads.getJSONObject(i).getInt("id");
					String name = threads.getJSONObject(i).getString("firstname") + " " + threads.getJSONObject(i).getString("lastname");
					String avatar_thumb_url = threads.getJSONObject(i).getString("avatar_thumb_url");
					String looking_for = threads.getJSONObject(i).getString("school_name");
					boolean has_schedule = false;
					if (threads.getJSONObject(i).has("has_schedule")){
						threads.getJSONObject(i).getBoolean("has_schedule");
					}
					
					listTemp.add(new FriendsModel(user_id, name, avatar_thumb_url, looking_for, has_schedule, null, null));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			return listTemp;
	    }
		
		protected void onPostExecute(List<FriendsModel> result) {
			tvSearchResult.setText(getString(R.string.searchresults) + String.valueOf(result.size()) + getString(R.string._people));
			
			lvSearchFriend.setVisibility(View.GONE);
	    	list.addAll(result);
	    	lvSearchFriend.invalidateViews();
	    	adapter.notifyDataSetChanged();
	    	lvSearchFriend.setVisibility(View.VISIBLE);
	    	
	    	preLoadImages();
	    	
	    	listTemp.clear();
			listTemp = null;
			
			if (list.isEmpty()){
				//showNoResults();
			}
		}

	}
	
	public void preLoadImages() {
		// TODO Auto-generated method stub
		new loadBitmapTread().execute(0, list.size());
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
						list.get(i).image_bitmap = ImageLoader.KembrelImageStoreAndLoad(list.get(i).avatar_thumb_url, SearchFriend.this);
		    			mHandler.post(new Runnable() {
		    	        	public void run() {
		    	        		lvSearchFriend.invalidateViews();
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
	
	public class SearchCourseArrayAdapter extends ArrayAdapter<FriendsModel> {
		private final List<FriendsModel> list;
		
		class ViewHolder {
			public ImageView ivThumb;
			public TextView name;
			public TextView preview;
		}

		public SearchCourseArrayAdapter(Context context, List<FriendsModel> list) {
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
			holder.name.setTypeface(Fonts.getOpenSansBold(SearchFriend.this));
			holder.name.setText(StringLanguageSelector.retrieveString(list.get(position).name));
			holder.preview.setText(list.get(position).looking_for);
			
			return rowView;
		}
		
	}

	public void showNoResults(){
		mHandler.post(new Runnable() {
    		public void run() {
    			Toast.makeText(SearchFriend.this, getString(R.string.No_results_are_found), Toast.LENGTH_SHORT).show();
    		}
		});
	}
	
}
