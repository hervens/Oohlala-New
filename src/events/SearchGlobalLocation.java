package events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;

import profile.UpdateStatusInterface;

import user.Profile;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import campusmap.CampusMapArrayAdapter;
import campusmap.CampusMapItemizedOverlay;
import campusmap.CampusMapModel;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.gotoohlala.R;

import datastorage.Fonts;
import datastorage.Rest;
import datastorage.Rest.request;
import datastorage.RestClient;

public class SearchGlobalLocation extends MapActivity {
	MapView map;
	MapController controller;
	Geocoder geoCoder;
	
	List<Overlay> overlayList;
	private Drawable drawable;
    private CampusMapItemizedOverlay itemizedOverlay;
    
	EditText etSearchBuildings;
	
	CampusMapArrayAdapter adapter;
	ListView listView;
	
	final List<CampusMapModel> list = new ArrayList<CampusMapModel>();
	
	Handler mHandler = new Handler(Looper.getMainLooper());
	
	TextView tvSearchResult;
	
	public static SearchGlobalLocationInterface act;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campusmap);
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(getApplicationContext()));
		
		//content
		map = (MapView) findViewById(R.id.mvCampusMap);
		map.setBuiltInZoomControls(true);
		
		geoCoder = new Geocoder(this, Locale.getDefault()); //define Geocoder
		
		overlayList = map.getOverlays();
		
		controller = map.getController();
		GeoPoint startPoint = new GeoPoint((int)(Profile.school_latitude *1E6), (int)(Profile.school_longitude *1E6));
		Log.i("school loc", String.valueOf(Profile.school_latitude) + "/" + String.valueOf(Profile.school_longitude));
		controller.setCenter(startPoint);
		controller.setZoom(17);
		
		drawable = this.getResources().getDrawable(R.drawable.mapmarker);
		itemizedOverlay = new CampusMapItemizedOverlay(drawable, getBaseContext(), 20);//text size: 30
		
		listView = (ListView) findViewById(R.id.lvSearchBuildings);
		adapter = new CampusMapArrayAdapter(this, list);
		listView.setAdapter(adapter);
			
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				
				act.UpdateLocation(list.get(position).name, list.get(position).longitude, list.get(position).latitude);
				onBackPressed();
				
				/*
				tvSearchResult.setVisibility(View.GONE);
				listView.setVisibility(View.GONE);
				map.setVisibility(View.VISIBLE);
					
				overlayList.clear();
				itemizedOverlay.clear();
					
				GeoPoint gPointBuilding = new GeoPoint((int)(list.get(position).latitude  * 1E6),(int)(list.get(position).longitude  * 1E6));
			    OverlayItem overlayItem = new OverlayItem(gPointBuilding, list.get(position).name, list.get(position).name);
			    itemizedOverlay.addOverlay(overlayItem);
			    overlayList.add(itemizedOverlay);
			        
			    controller.setCenter(gPointBuilding); //center to the building on the map
			    map.postInvalidate();
			    */
				
			    //hide keyboard
			    InputMethodManager imm = (InputMethodManager) SearchGlobalLocation.this.getSystemService(Context.INPUT_METHOD_SERVICE);
			    imm.hideSoftInputFromWindow(etSearchBuildings.getWindowToken(), 0);		  
			}
		});
			
		etSearchBuildings = (EditText) findViewById(R.id.etSearchBuildings);
	
		etSearchBuildings.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (etSearchBuildings.getText().toString().length() < 2){
					
				} else {
					Log.i("test2", "-------------");
					map.setVisibility(View.GONE);
					listView.setVisibility(View.VISIBLE);
					tvSearchResult.setVisibility(View.VISIBLE);
					reloadView(s.toString());
				}
		    }
			
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		     
			}
		 
			public void afterTextChanged(Editable s) {
		    	 
		    }
		});
		
		etSearchBuildings.setOnKeyListener(new EditText.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				//This is the filter
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
                
				if (keyCode == KeyEvent.KEYCODE_ENTER){
					if (etSearchBuildings.getText().toString().length() < 2){
						Toast.makeText(v.getContext(), getString(R.string.search_buildings_dialog_alert), Toast.LENGTH_SHORT).show();
					} else {
						
					}
					
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		    		imm.hideSoftInputFromWindow(etSearchBuildings.getWindowToken(), 0);
		    		etSearchBuildings.clearFocus();
		    		
		    		return true;
				} else if (keyCode == KeyEvent.KEYCODE_BACK){
					onBackPressed();
					return true;
				}
				return false;				
			}
		});
		
		tvSearchResult = (TextView) findViewById(R.id.tvSearchResult);
	}
	
	public void reloadView(final String name) {
		mHandler.post(new Runnable() {
    		public void run() {
		    	list.clear();
		    	
		    	if (list.isEmpty()){
		    		Log.i("test1", "-------------");
					new updateListThread().execute(name);
				}
		    }
		});
	}
	
	class updateListThread extends AsyncTask<String, Void, List<CampusMapModel>> {
	    // This method is called when the thread runs
		List<Address> addresses = null;
		List<CampusMapModel> listTemp = null;
	
		protected List<CampusMapModel> doInBackground(String... names) {
	    	//get the all the current games
			addresses = new ArrayList<Address>();
			listTemp = new ArrayList<CampusMapModel>();
			
			RestClient result = null;
			try {
				result = new request().execute(Rest.CAMPUS_BUILDING + "?name=" + names[0].replace(" ", "%20"), Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray buildings = new JSONArray(result.getResponse());
				Log.i("buildings: ", buildings.toString());
						
				for (int i = 0; i < buildings.length(); i++){
					int id = buildings.getJSONObject(i).getInt("id");
					String name = buildings.getJSONObject(i).getString("name");
					String short_name = buildings.getJSONObject(i).getString("short_name");
					double longitude = buildings.getJSONObject(i).getDouble("longitude");
					double latitude = buildings.getJSONObject(i).getDouble("latitude");
					String address = buildings.getJSONObject(i).getString("address");
					
					listTemp.add(new CampusMapModel(id, name, short_name, longitude, latitude, address));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (listTemp.isEmpty()){
				try {
	                addresses = geoCoder.getFromLocationName(names[0], 100);
	            } catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
				 
				for (int i = 0; i < addresses.size(); i++){
					int id = 0;
					String name = addresses.get(0).getFeatureName();
					String short_name = addresses.get(0).getFeatureName();
					double longitude = addresses.get(0).getLongitude();
					double latitude = addresses.get(0).getLatitude();
					String address = addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1);
						
					listTemp.add(new CampusMapModel(id, name, short_name, longitude, latitude, address));
				}
			}
			
			return listTemp;
	    }
		
		protected void onPostExecute(List<CampusMapModel> result) {
			tvSearchResult.setText(getString(R.string.searchresults) + String.valueOf(result.size()) + getString(R.string._buildings));
			
			//listView.setVisibility(View.GONE);
	    	list.addAll(result);
	    	listView.invalidateViews();
	    	adapter.notifyDataSetChanged();
	    	//listView.setVisibility(View.VISIBLE);
	    	
	    	listTemp.clear();
			listTemp = null;
			
			addresses.clear();
			addresses = null;
			
			if (list.isEmpty()){
				//showNoResults();
			}
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void showNoResults(){
		mHandler.post(new Runnable() {
    		public void run() {
    			Toast.makeText(SearchGlobalLocation.this, getString(R.string.No_results_are_found), Toast.LENGTH_SHORT).show();
    		}
		});
	}
	
}
