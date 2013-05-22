package launchOohlala;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.ProfileSettings;
import profile.SearchCourseModel;

import rewards.RewardsLocationModel;

import user.Profile;
import campusmap.CampusMap;
import campusmap.CampusMapArrayAdapter;
import campusmap.CampusMapModel;
import campuswall.CampusWallInviteFacebook;
import campuswall.CampusWallModel;

import com.facebook.Session;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;

import datastorage.CacheInternalStorage;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.LoginState;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import datastorage.UserLoginInfo;
import datastorage.Rest.request;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

public class SearchSchool extends Activity {
	EditText etSearchFriend;
	ListView lvSearchFriend;
	SearchCourseArrayAdapter adapter;
	final List<CheckEmailMultiSchoolsModel> list = new ArrayList<CheckEmailMultiSchoolsModel>();
	
	Handler mHandler = new Handler(Looper.getMainLooper());
	
	TextView tvSearchResult;
	
	updateListThread updateListThread = new updateListThread();
	
	BroadcastReceiver brLogout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchschool);
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(SearchSchool.this));
		header.setText(getString(R.string.SELECT_YOUR_SCHOOL).toUpperCase());
		
		etSearchFriend = (EditText) findViewById(R.id.etSearchFriend);
		lvSearchFriend = (ListView) findViewById(R.id.lvSearchFriend);
		tvSearchResult = (TextView) findViewById(R.id.tvSearchResult);
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
				
				InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etSearchFriend.getWindowToken(), 0);
			}
		}); 
		
		tvSearchResult.setText(getString(R.string.searchschools));
		
		adapter = new SearchCourseArrayAdapter(SearchSchool.this, list);
		lvSearchFriend.setAdapter(adapter);
		lvSearchFriend.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				if (list.get(position).schoolid != 0){
					Bundle extras = new Bundle();
					extras.putInt("school_id", list.get(position).schoolid);
					extras.putString("school_name", list.get(position).schoolname);
					Intent i = new Intent(SearchSchool.this, Register.class);
					i.putExtras(extras);
					startActivity(i);
				} else {
					AlertDialog alert = new AlertDialog.Builder(SearchSchool.this).create();
					alert.setMessage(getString(R.string.Type_in_your_school_name));
					final EditText input = new EditText(SearchSchool.this);
					input.setText(list.get(position).schoolname);
					alert.setView(input);
					
					alert.setButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					alert.setButton2(getString(R.string.Add), new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							String value = input.getText().toString();
							
							RestClient result = null;
    						try {
    							result = new Rest.requestBody().execute(Rest.SCHOOL, Rest.OTOKE + Rest.accessCode2, Rest.POST, "1", "name", value).get();
    						} catch (InterruptedException e1) {
    							// TODO Auto-generated catch block
    							e1.printStackTrace();
    						} catch (ExecutionException e1) {
    							// TODO Auto-generated catch block
    							e1.printStackTrace();
    						}
    						Log.i("add school result", result.getResponse());
    								
    						if (result.getResponseCode() == 201) {
    							try {
									int school_id = (new JSONObject(result.getResponse())).getInt("id");
									Log.i("school_id", String.valueOf(school_id));
									
									Bundle extras = new Bundle();
									extras.putInt("school_id", school_id);
									extras.putString("school_name", value);
									Intent i = new Intent(SearchSchool.this, Register.class);
									i.putExtras(extras);
									startActivity(i);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
    						} else if (result.getResponseCode() == 409){
    							Toast.makeText(getApplicationContext(), getString(R.string.Add_School_Conflict_message), Toast.LENGTH_SHORT).show();
    						}
						}
					});
					alert.show();
				}
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
		
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.gotoohlala.profile.ProfileSettings");
        brLogout = new BroadcastReceiver() {
        	@Override
         	public void onReceive(Context context, Intent intent) {
        		Log.d("onReceive","Logout in progress");
            	//At this point you should start the login activity and finish this one
        		finish();
        	}
        };
        registerReceiver(brLogout, intentFilter);
	}
	
	public void onDestroy(){
		super.onDestroy();

		unregisterReceiver(brLogout);
	}
	
	public void reloadView(final String name) {
		mHandler.post(new Runnable() {
    		public void run() {
		    	list.clear();
		    	
		    	if (list.isEmpty()){
		    		updateListThread.cancel(true);
		    		updateListThread = new updateListThread();
		    		updateListThread.execute(name);
				}
		    }
		});
	}

	class updateListThread extends AsyncTask<String, Void, List<CheckEmailMultiSchoolsModel>> {
	    // This method is called when the thread runs
		List<CheckEmailMultiSchoolsModel> listTemp = null;
		String school_name;
	
		protected List<CheckEmailMultiSchoolsModel> doInBackground(String... names) {
	    	//get the all the current games
			listTemp = new ArrayList<CheckEmailMultiSchoolsModel>();
			school_name = names[0];
			
			RestClient result = null;
			try {
				result = new request().execute(Rest.SCHOOL + "?name=" + names[0].replace(" ", "%20"), Rest.OTOKE + Rest.accessCode2, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				final JSONArray schools = new JSONArray(result.getResponse());
				Log.i("schools: ", schools.toString());
				
				for (int i = 0; i < schools.length(); i++){
					String schoolname = null;
					int schoolid = 0;
					try {
						schoolname = schools.getJSONObject(i).getString("name");
						schoolid = schools.getJSONObject(i).getInt("id");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if (schoolname != null){
						listTemp.add(new CheckEmailMultiSchoolsModel(schoolname, schoolid));
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			if(isCancelled()){
    			// Do your stuff
				listTemp = null;
    		}
			
			return listTemp;
	    }
		
		protected void onPostExecute(List<CheckEmailMultiSchoolsModel> result) {
			tvSearchResult.setText(getString(R.string.searchresults) + String.valueOf(result.size()) + getString(R.string._schools));
			
			lvSearchFriend.setVisibility(View.GONE);
			if (result.isEmpty()){
				list.add(new CheckEmailMultiSchoolsModel(school_name, 0));
			}
	    	list.addAll(result);
	    	lvSearchFriend.invalidateViews();
	    	adapter.notifyDataSetChanged();
	    	lvSearchFriend.setVisibility(View.VISIBLE);
	    	
	    	listTemp.clear();
			listTemp = null;
			
			if (list.isEmpty()){
				//showNoResults();
			}
		}

	}
	
	public class SearchCourseArrayAdapter extends ArrayAdapter<CheckEmailMultiSchoolsModel> {
		private final List<CheckEmailMultiSchoolsModel> list;
		
		class ViewHolder {
			public ImageView ivThumb, ivAvatarFrame;
			public TextView name;
			public TextView preview;
		}

		public SearchCourseArrayAdapter(Context context, List<CheckEmailMultiSchoolsModel> list) {
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
				viewHolder.ivAvatarFrame = (ImageView) rowView.findViewById(R.id.ivAvatarFrame);
				viewHolder.name = (TextView) rowView.findViewById(R.id.name);
				viewHolder.preview = (TextView) rowView.findViewById(R.id.preview);
				
				rowView.setTag(viewHolder);
			}
			
			ViewHolder holder = (ViewHolder) rowView.getTag();
			
			holder.ivThumb.setImageBitmap(null);
			holder.ivThumb.setVisibility(View.GONE);
			holder.ivAvatarFrame.setVisibility(View.GONE);
			holder.name.setText("");	
			holder.preview.setText("");	
			
			holder.name.setTypeface(Fonts.getOpenSansBold(SearchSchool.this));
			if (list.get(position).schoolid != 0){
				holder.name.setText(list.get(position).schoolname);
			} else {
				holder.name.setText(getString(R.string.Add_School_) + list.get(position).schoolname);
			}
			
			return rowView;
		}
		
	}

	public void showNoResults(){
		mHandler.post(new Runnable() {
    		public void run() {
    			Toast.makeText(SearchSchool.this, getString(R.string.No_results_are_found), Toast.LENGTH_SHORT).show();
    		}
		});
	}
	
}
