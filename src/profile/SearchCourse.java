package profile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rewards.RewardsLocationModel;

import user.Profile;
import campusmap.CampusMap;
import campusmap.CampusMapArrayAdapter;
import campusmap.CampusMapModel;
import campuswall.CampusWallModel;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.gotoohlala.R;

import datastorage.Fonts;
import datastorage.Rest;
import datastorage.RestClient;
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

public class SearchCourse extends Activity {
	EditText etSearchCourses;
	ListView lvSearchCourses;
	SearchCourseArrayAdapter adapter;
	final List<SearchCourseModel> list = new ArrayList<SearchCourseModel>();
	
	Handler mHandler = new Handler(Looper.getMainLooper());
	int daySelected = 1;
	
	TextView tvSearchResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchcourse);
		
		Bundle b = this.getIntent().getExtras();
		daySelected = b.getInt("daySelected");
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		}); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(SearchCourse.this));
		
		etSearchCourses = (EditText) findViewById(R.id.etSearchCourses);
		lvSearchCourses = (ListView) findViewById(R.id.lvSearchCourses);
		tvSearchResult = (TextView) findViewById(R.id.tvSearchResult);
		
		tvSearchResult.setText(getString(R.string.searchcourses));
		
		adapter = new SearchCourseArrayAdapter(SearchCourse.this, list);
		lvSearchCourses.setAdapter(adapter);
		lvSearchCourses.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				if (list.get(position).course_id != 0){
					Bundle extras = new Bundle();
					extras.putInt("course_id", list.get(position).course_id);
					extras.putInt("course_time_id", list.get(position).course_time_id);
					extras.putInt("daySelected", daySelected);
					extras.putString("course_code", list.get(position).course_code);
					Intent i = new Intent(SearchCourse.this, Course.class);
					i.putExtras(extras);
					startActivity(i);
				} else {
					Bundle extras = new Bundle();
					extras.putInt("daySelected", daySelected);
					extras.putString("course_code", list.get(position).course_code);
					Intent i = new Intent(SearchCourse.this, NewCourse.class);
					i.putExtras(extras);
					startActivity(i);
				}
			}
		});
		
		etSearchCourses.addTextChangedListener(new TextWatcher() {
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
		
		etSearchCourses.setOnKeyListener(new EditText.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
				
				if (keyCode == KeyEvent.KEYCODE_ENTER){
					String name = etSearchCourses.getText().toString();
					if (name.length() >= 3){
						//reloadView(name);
					} else {
						Toast.makeText(v.getContext(), getString(R.string.search_name_dialog_alert), Toast.LENGTH_SHORT).show();
					}
					
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		    		imm.hideSoftInputFromWindow(etSearchCourses.getWindowToken(), 0);
		    		etSearchCourses.clearFocus();
		    		
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

	class updateListThread extends AsyncTask<String, List<SearchCourseModel>, List<SearchCourseModel>> {
	    // This method is called when the thread runs
		List<SearchCourseModel> listTemp = null;
		String course_code;
	
		protected List<SearchCourseModel> doInBackground(String... name) {
	    	//get the all the current games
			listTemp = new ArrayList<SearchCourseModel>();
			course_code = name[0];
			
			RestClient result = null;
			try {
				result = new request().execute(Rest.SCHOOL_COURSE + "?name=" + name[0].replace(" ", "%20"), Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray threads = new JSONArray(result.getResponse());
				Log.i("school courses: ", threads.toString());
				
				for (int i = 0; i < threads.length(); i++){
					int course_id = threads.getJSONObject(i).getInt("id");
					String course_name = threads.getJSONObject(i).getString("course_name");
					String course_code = threads.getJSONObject(i).getString("course_code");
					int course_time_id = 0;
					
					listTemp.add(new SearchCourseModel(course_id, course_name, course_code, course_time_id));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return listTemp;
	    }
		
		protected void onPostExecute(List<SearchCourseModel> result) {
			tvSearchResult.setText(getString(R.string.searchresults) + String.valueOf(result.size()) + getString(R.string._courses));
			
			lvSearchCourses.setVisibility(View.GONE);
			if (result.isEmpty()){
				list.add(new SearchCourseModel(0, "", course_code, 0));
			}
	    	list.addAll(result);
	    	lvSearchCourses.invalidateViews();
	    	adapter.notifyDataSetChanged();
	    	lvSearchCourses.setVisibility(View.VISIBLE);
	    	
	    	listTemp.clear();
			listTemp = null;
			
			if (list.isEmpty()){
				//showNoResults();
			}
		}

	}
	
	public class SearchCourseArrayAdapter extends ArrayAdapter<SearchCourseModel> {
		private final Context context;
		private final List<SearchCourseModel> list;
		
		class ViewHolder {
			public TextView tvCourseCode, tvCourseName;
		}

		public SearchCourseArrayAdapter(Context context, List<SearchCourseModel> list) {
			super(context, R.layout.searchcourserow, list);
			this.context = context;
			this.list = list;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null){
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.searchcourserow, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.tvCourseCode = (TextView) rowView.findViewById(R.id.tvCourseCode);
				viewHolder.tvCourseName = (TextView) rowView.findViewById(R.id.tvCourseName);

				rowView.setTag(viewHolder);
			}
			
			final ViewHolder holder = (ViewHolder) rowView.getTag();			
			holder.tvCourseCode.setText("");
			holder.tvCourseName.setText("");
			
			if (list.get(position).course_id == 0){
				holder.tvCourseCode.setText(list.get(position).course_code);
				holder.tvCourseName.setText("add " + list.get(position).course_code);
			} else {
				holder.tvCourseCode.setText(list.get(position).course_code);
				holder.tvCourseName.setText(list.get(position).course_name);
			}
			
			return rowView;
		}
		
	}
	
	public void showNoResults(){
		mHandler.post(new Runnable() {
    		public void run() {
    			Toast.makeText(SearchCourse.this, getString(R.string.No_results_are_found), Toast.LENGTH_SHORT).show();
    		}
		});
	}

}
