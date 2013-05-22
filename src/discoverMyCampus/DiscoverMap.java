package discoverMyCampus;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.Provider;

import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;
import android.widget.VideoView;


/**
 * Primary class of discover my campus. This activity inflates a google maps fragment, and contain
 * subclasses that allows a new internal activity to be launched, once a marker has been clicked, or a building
 * has been selected in the listview
 * @author frantz
 *
 */
public class DiscoverMap extends FragmentActivity implements OnMarkerClickListener
{
	private GoogleMap mMap;
	private Context myContext = this;
	String mylatitude = "0";
	String myLong = "0";
	GetJsonData myData;
	UserData usr;
	Bundle mySavedBundle;
	protected static UniversityData uni;
	ArrayList<Marker> myMarkers; //List of all the markers on the map
	private static int markerIndex = 0; //index of a marker that has been clicked
	
	
	
	public DiscoverMap()
	{ 
		super();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        if(extras != null )
        {
        	myData = new GetJsonData(extras.getString("selectedTourId"));
        	Log.d("IN DISCOVERMAP", "Value of selectedTourId is: " + extras.getString("selectedTourId"));
        }
        else
        {
        	myData = new GetJsonData("1");
        	Log.d("IN DISCOVERMAP", "ERROR, bundle value extras is null. NO GOOD!");
        }
        
        checkGooglePlayServicesAvailability();
        
        Log.d("IN DISCOVERMAP", "OK, BEFORE SETCONTENTVIEW");
		setContentView(R.layout.discover_map_fragment);
		Log.d("IN DISCOVERMAP", "OK, AFTER SETCONTENTVIEW");
		
		usr = new UserData(); 
		Log.d("IN DISCOVERMAP", "OK, BEFORE MAPFRAGMENT");
		mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.setMyLocationEnabled(true);
		
		Log.d("IN DISCOVERMAP", "OK, AFTER MAPFRAGMENT");
		
		 uni = new UniversityData(getUniName());
		    
		CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(uni.getLatitude()),Double.parseDouble(uni.getLongtitude())), 18);
		mMap.animateCamera(camera);
		LocationManager lm;
		mMap.setOnMarkerClickListener(this);
		
		//Creates all the marker objects, and store in a list so we can access on click data
		myMarkers = new ArrayList<Marker>();
		
		//create the points of interest as markers
		for(int i = 0; i< uni.getPOIsize(); i++)
		{
			
			 myMarkers.add( mMap.addMarker(new MarkerOptions()
			.position(new LatLng(Double.parseDouble(uni.getPOI(i).getLatitude()),Double.parseDouble(uni.getPOI(i).getLongtitude())))
			.title(uni.getPOI(i).getTitle())
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
			 
			) );
			 
			 //here we draw the route lines between the buildings
			 int currBuilding = i;
			 int nextBuilding = i+1;
			 
			 if(nextBuilding < uni.getPOIsize())
			 {
				 String pathUrl = makeURL(Double.parseDouble(uni.getPOI(currBuilding).getLatitude() ), Double.parseDouble(uni.getPOI(currBuilding).getLongtitude() ),Double.parseDouble(uni.getPOI(nextBuilding).getLatitude() ), Double.parseDouble(uni.getPOI(nextBuilding).getLongtitude() )  );
				 new connectAsyncTask(pathUrl).execute();
			 }
			 
		}
		mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("test"));
		
		
		//Finally, we start the fragment that contains the listview of all the buildings
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		BuildingListFragment frag = new BuildingListFragment(this);
		
		transaction.add(R.id.fragmentContainerLayout, frag);
		transaction.commit();
		
	}
	
	/**
	 * Maps source and destination points. Used to draw a line between 2 points
	 * @param sourcelat
	 * @param sourcelog
	 * @param destlat
	 * @param destlog
	 * @return
	 */
	 public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
	        StringBuilder urlString = new StringBuilder();
	        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
	        urlString.append("?origin=");// from
	        urlString.append(Double.toString(sourcelat));
	        urlString.append(",");
	        urlString
	                .append(Double.toString( sourcelog));
	        urlString.append("&destination=");// to
	        urlString
	                .append(Double.toString( destlat));
	        urlString.append(",");
	        urlString.append(Double.toString( destlog));
	        urlString.append("&sensor=false&mode=walking&alternatives=true");
	        return urlString.toString();
	 }
	 
	 /**
	  * 
	  * @param result - jSON result
	  */
	 public void drawPath(String  result) {

		    try {
		            //Tranform the string into a json object
		           final JSONObject json = new JSONObject(result);
		           JSONArray routeArray = json.getJSONArray("routes");
		           JSONObject routes = routeArray.getJSONObject(0);
		           JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
		           String encodedString = overviewPolylines.getString("points");
		           List<LatLng> list = decodePoly(encodedString);

		           for(int z = 0; z<list.size()-1;z++){
		                LatLng src= list.get(z);
		                LatLng dest= list.get(z+1);
		                Polyline line = mMap.addPolyline(new PolylineOptions()
		                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
		                .width(2)
		                .color(Color.BLUE).geodesic(true));
		            }

		    } 
		    catch (JSONException e) {

		    }
		} 
	 
	 private List<LatLng> decodePoly(String encoded) {

		    List<LatLng> poly = new ArrayList<LatLng>();
		    int index = 0, len = encoded.length();
		    int lat = 0, lng = 0;

		    while (index < len) {
		        int b, shift = 0, result = 0;
		        do {
		            b = encoded.charAt(index++) - 63;
		            result |= (b & 0x1f) << shift;
		            shift += 5;
		        } while (b >= 0x20);
		        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
		        lat += dlat;

		        shift = 0;
		        result = 0;
		        do {
		            b = encoded.charAt(index++) - 63;
		            result |= (b & 0x1f) << shift;
		            shift += 5;
		        } while (b >= 0x20);
		        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
		        lng += dlng;

		        LatLng p = new LatLng( (((double) lat / 1E5)),
		                 (((double) lng / 1E5) ));
		        poly.add(p);
		    }

		    return poly;
		}
	 
	 /**
	  * Starts the task that will draw the lines between the 2 buildings
	  * @author frantz
	  *
	  */
	 private class connectAsyncTask extends AsyncTask<Void, Void, String>{
		    private ProgressDialog progressDialog;
		    String url;
		    connectAsyncTask(String urlPass){
		        url = urlPass;
		    }
		    @Override
		    protected void onPreExecute() {
		        // TODO Auto-generated method stub
		        super.onPreExecute();
		        progressDialog = new ProgressDialog(DiscoverMap.this);
		        progressDialog.setMessage("Fetching route, Please wait...");
		        progressDialog.setIndeterminate(true);
		        progressDialog.show();
		    }
		    @Override
		    protected String doInBackground(Void... params) {
		        JSONParser jParser = new JSONParser();
		        String json = jParser.getJSONFromUrl(url);
		        return json;
		    }
		    @Override
		    protected void onPostExecute(String result) {
		        super.onPostExecute(result);   
		        progressDialog.hide();        
		        if(result!=null){
		            drawPath(result);
		        }
		    }
		}
	
	
	public void checkGooglePlayServicesAvailability()
	{
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if(resultCode != ConnectionResult.SUCCESS)
	    {
	        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 69);
	        if(dialog != null)
	        {
	            dialog.show();                
	        }
	        else
	        {
	            showOkDialogWithText(this, "Something went wrong. Please make sure that you have the Play Store installed and that you are connected to the internet. Contact developer with details if this persists.");
	        }
	    }

	    Log.d("GooglePlayServicesUtil Check", "Result is: " + resultCode);
	}

	public static void showOkDialogWithText(Context context, String messageText)
	{
	    Builder builder = new AlertDialog.Builder(context);
	    builder.setMessage(messageText);
	    builder.setCancelable(true);
	    builder.setPositiveButton("OK", null);
	    AlertDialog dialog = builder.create();
	    dialog.show();
	}
	
	/**
	 * Getters and setters for the marker index
	 */
	public static int getMarkerIndex() {return markerIndex; }
	public static void setMarkerIndex(int index) { markerIndex = index; }
	
	
	/**
	 * Sends the user back to the main page, once back button has been pressed
	 */
	@Override
	  public void onBackPressed() {
		
	    finish();
	  }
	
	
	
		
		@Override
		protected void onResume()
		{
		    super.onResume();
		}
		
		@Override
		protected void onPause()
		{
		    super.onPause();
		}
		
		
		/**
		 * returns a view to be served as the tab header
		 * @param context
		 * @param text
		 * @return
		 */
		private static View createTabView(final Context context, final String text) 
		{
			
			    View view = LayoutInflater.from(context).inflate(R.layout.discover_map_tabview, null);
			
			    TextView tv = (TextView) view.findViewById(R.id.tabsText);
			
			    tv.setText(text);
			
			    return view;
			
			}

	
	/**
	 * Creates the popup window that will be displayed when the user clicks on one of the markers
	 */
		public static class BuildingInfo extends TabActivity
		{
			Activity myContext = this;
			
			@Override
			protected void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        
				//View mView = inflater.inflate(R.layout.discover_map_fragment, container, false);
				setContentView(R.layout.popup_window);
	    try {
	        
	        //initPopup();
	        //get screen width and height to help in the styling
	        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
	        Display display = wm.getDefaultDisplay();  
	        int width = display.getWidth();
	        int height = display.getHeight();
	         
	        
	        //Sets up the activities that will be loaded in each tab
	        
	        Intent intentDescription = new Intent(this, DiscoverMapTab1.class);
	        Intent intentImage = new Intent(this, DiscoverMapTab2.class);
	        Intent intentVideo = new Intent(this, DiscoverMapTab3.class);
	        
     
	        //Sets up the tabs 
	        TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
	        
	        View tab1 = createTabView(this,"Description");
	        View tab2 = createTabView(this,"Images");
	        View tab3 = createTabView(this,"Videos");
	        tabs.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
	        
	        //Description Tab
	        tabs.setup();
	        TabSpec tspec1 = tabs.newTabSpec("First Tab");
	        tspec1.setIndicator(tab1);
	        tspec1.setContent(intentDescription);
	        tabs.addTab(tspec1);
	        
	        //Images Tab
	        TabSpec tspec2 = tabs.newTabSpec("Second Tab");
	        tspec2.setIndicator(tab2);
	        tspec2.setContent(intentImage);
	        tabs.addTab(tspec2);
	        
	        //Videos Tab
	        TabSpec tspec3 = tabs.newTabSpec("Third Tab");
	        tspec3.setIndicator(tab3);
	        tspec3.setContent(intentVideo);
	        tabs.addTab(tspec3);
	        
	        tabs.setCurrentTab(0);
	        
	        
	        //setup the cancel button
	        Button cancelButton = (Button) this.findViewById(R.id.buttonCancel);
	        
	     // cancel button listener
		      cancelButton.setOnClickListener(new View.OnClickListener() {
		         @Override
		         public void onClick(View v) 
		         {
		        	 Log.d("IN DiscoverMAP ", " Button has been clicked!!! ");
		        	// Intent intent = new Intent(BuildingInfo.this, DiscoverMap.class);
		     	    //startActivity(intent);
		        	myContext.finish(); 
		         }
		      }
		      );
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    

	}
	
		}
		
	

		
	/**
	 * returns the name of the university of the currently logged in user
	 */
	private String getUniName()
	{
		return "McGill University";
	}
	
	/**
	 * Stores the position, name, and markers of the university in question
	 */
	private class UniversityData
	{
		private String name;
		String myLat, myLong; //center location of the university
		private ArrayList<InterestPoint> poiList;
		
		public UniversityData(String nm)
		{
			name = nm;
			poiList = new ArrayList<InterestPoint>();
			populateLocation();
			populatePOI();
		}
		
		/**
		 * Gets the center latitude and longtitude of the university from the JSon object
		 */
		private void populateLocation()
		{
			myLat = myData.getUniLat();
			myLong = myData.getUniLo();
		}
		
		public String getLatitude() {return myLat; }
		public String getLongtitude() {return myLong; }
		
		public ArrayList<InterestPoint> getPOIlist()
		{
			return poiList;
		}
		
		
		/**
		 * Fetches all the points of interest that belongs to this university, from the 
		 * Json object and populate the ArrayList
		 */
		public void populatePOI()
		{
			poiList = myData.getUniPOI();
		}
		
		
		/**
		 * @return returns the size of the ArrayList containing the interest points
		 */
		public int getPOIsize()
		{
			return poiList.size();
		}
		
		
		/**
		 * Returns the InterestPoint at the specified location, or null if it does not exist
		 */
		public InterestPoint getPOI(int index)
		{
			return poiList.get(index);
		}
		
	}
	
	
	
	
	/**
	 * Object that stores current location and progress of the user
	 */
	private class UserData
	{
		String username;
		int myProgress = 0;
		ArrayList<String> seenIconId; 
		
		public UserData()
		{
			//myProgress = JSONObject.getUserProgress("username")
		}
		
		public void updateProgress(int progr)
		{
			myProgress += progr;
			//DiscoverCampusMain.textProgressBar.setProgress(myProgress);
			//DiscoverCampusMain.textProgressBar.refreshDrawableState();
			//JSONObject.updateProgress(username,progress)
		}
		
	}

	@Override
	public boolean onMarkerClick(Marker marker) 
	{
		Log.d("IN DISCOVERMAP ","Marker has been clicked");
		usr.updateProgress(10);
		
		int markerIndex = 0;
		
		//Find the index of the marker in the list
		for(int i = 0; i< myMarkers.size(); i++)
		{
			if(myMarkers.get(i).equals(marker) )
			{
				markerIndex = i;
				Log.d("IN DISCOVERMAP", "Marker index is: " + i);
			}
		}
		
		DiscoverMap.setMarkerIndex(markerIndex);
		Intent intent = new Intent(this, BuildingInfo.class);
		startActivity(intent);
		return false;
	}
	
	
	/**
	 * private class that inflates the discover_map_building_list fragment and starts the listview,
	 * where a list of buildings that are part of this tour are displayed
	 */
	@SuppressLint("ValidFragment")
	public static class BuildingListFragment extends Fragment
	{
		DiscoverMap discover;
		ListView listview;
		Context myContext;
		
		public BuildingListFragment()
		{
			
		}
		
		@SuppressLint("ValidFragment")
		public BuildingListFragment(DiscoverMap discover)
		{
			this.discover = discover;
		}
		
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
		{
			View mView = inflater.inflate(R.layout.discover_campus_building_list, container, false);
			listview = (ListView)mView.findViewById(R.id.mylist);
			
			ArrayList bList = new ArrayList<String>();
			for(int i = 0; i< uni.getPOIsize(); i++)
			{
				bList.add(uni.getPOI(i).getTitle());
			}
			BuildingAdapter adapter = new BuildingAdapter(myContext,bList);
			listview.setAdapter(adapter);
			
		      //btn listener for listview
		      listview.setOnItemClickListener(new OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> parent, View view, int position,
		                    long id) 
		            {
		             //Position the camera over the marker, and then open the window
		            	LatLng myPosition = new LatLng(Double.valueOf(uni.getPOI(position).getLatitude() ),Double.valueOf(uni.getPOI(position).getLongtitude() ));
		            	discover.mMap.animateCamera( CameraUpdateFactory.newCameraPosition(new CameraPosition(myPosition,18,0,0)) );
		            	try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            	DiscoverMap.setMarkerIndex(position);
		            	Intent intent = new Intent(myContext, BuildingInfo.class);
		        		startActivity(intent);
		                
		            }
		        });
		      
		      
			return mView;
		}
		
		
		/**
		 * Finds the context of the application, as soon as its been attached
		 */
		@Override
		public void onAttach(Activity activity)
		{
			super.onAttach(activity);
			myContext = activity;
			
		}
		
		/**
		 * Adapter class for the listview
		 */
		private class BuildingAdapter extends ArrayAdapter<String>
		{
			private final  Context context;
			 private final ArrayList<String> values;
			 
			  public BuildingAdapter(Context context, ArrayList<String> bList) 
			  { 
				    super(context, R.layout.discover_campus_list, bList);
				    this.context = context;
				    this.values = bList;
			  }
		  
			  
			  @Override
			  public View getView(int position, View convertView, ViewGroup parent) 
			  {
			    View rowView = convertView;
			    
			    if(rowView == null)
			    {
			    	LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    	rowView = inflater.inflate(R.layout.discover_campus_list, parent, false);
			    }
			   
			    
			    TextView txtName = (TextView) rowView.findViewById(R.id.txtBuildingName);
			    
			    txtName.setText(values.get(position));

			    return rowView;
			  }
		  
		}

	}
	
	
	/**
	 * Description tab 1 of the popup window
	 */
	public static class DiscoverMapTab1 extends Activity
	{
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.discover_map_tab1);
	        
	        
	        int markerIndex = DiscoverMap.getMarkerIndex();
	        Log.d("IN DISCOVERMAPTAB1", "Marker Index is: " + markerIndex);
			TextView contentTab1Title = (TextView) findViewById(R.id.textTitleContents2);
			TextView contentTab1 = (TextView) findViewById(R.id.textContents2);
			contentTab1Title.setText(uni.getPOI(markerIndex).getTitle());
			contentTab1.setText( uni.getPOI(markerIndex).getContents());
		}
	}
	
	
	/**
	 * Description tab 2 of the popup window
	 */
	public static class DiscoverMapTab2 extends Activity
	{
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.discover_map_tab2);
	        
	        //Bundle bundle = savedInstanceState.getExtras();
	        
	        int markerIndex = DiscoverMap.getMarkerIndex();
	        Log.d("IN DISCOVERMAPTAB2", "Marker Index is: " + markerIndex);
	        ListView listview = (ListView) findViewById(R.id.myImglist1);
			TextView contentTab1Title = (TextView) findViewById(R.id.textViewContentsTitle);
			contentTab1Title.setText(uni.getPOI(markerIndex).getTitle());
			
			ImageListAdapter adapter = new ImageListAdapter(this,uni.getPOIlist().get(markerIndex).getPictures(),uni.getPOIlist().get(markerIndex).getTextPictures());
			listview.setAdapter(adapter);
			
		}
		
		
		/**
		 * Adapter class that populates the listview with the images, and their corresponding captions
		 * @author frantz
		 *
		 */
		private class ImageListAdapter extends ArrayAdapter<String>
		{
			private final  Context context;
			 private final ArrayList<String> values;
			 private final ArrayList<String> caption;
			 
			  public ImageListAdapter(Context context, ArrayList<String> arrayList,ArrayList<String> caption ) 
			  {
				    super(context, R.layout.discover_map_tab2_list, arrayList);
				    values = arrayList;
				    this.context = context;
				    this.caption = caption;
			  }
		  
			  
			  @Override
			  public View getView(int position, View convertView, ViewGroup parent) 
			  {
			    View rowView = convertView;
			    
			    if(rowView == null)
			    {
			    	LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    	rowView = inflater.inflate(R.layout.discover_map_tab2_list, parent, false);
			    }
			   
			    
			    TextView txtCaption = (TextView) rowView.findViewById(R.id.txtTab3Caption);
			    ImageView imgView = (ImageView) rowView.findViewById(R.id.imageView1);
			    
			    	UrlImageViewHelper.setUseBitmapScaling(true);
			    	
				    UrlImageViewHelper.setUrlDrawable(imgView, values.get(position).toString(),R.drawable.loading) ;
				    txtCaption.setText(caption.get(position).toString()) ;
				    
				    
			    

				    Log.d("IN ADAPTER OF VIDLISTVIEW" , "POSITION IS: " + position);
			   // Log.d("IN ADAPTER OF IMG LISTVIEW", "Img url data is now: " + values.get(DiscoverMap.getMarkerIndex()).getPictures().get(position).toString());
			    return rowView;
			  }
	}
	}
	
	/**
	 * Description tab 3 of the popup window
	 */
	public static class DiscoverMapTab3 extends Activity
	{
		YouTubePlayer tubePlayer;
		String DEVELOPER_KEY = "AIzaSyDP2zfbRiEH0mNCkn2lpxm92DQHsfIJfPU";
		
		@Override
		protected void onCreate(Bundle savedInstanceState) 
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.discover_map_tab3);
	        
			//ListView listview = (ListView) findViewById(R.id.myVidList1);
	        int markerIndex = DiscoverMap.getMarkerIndex();
	        Log.d("IN DISCOVERMAPTAB3", "Marker Index is: " + markerIndex);
			TextView contentTab1Title = (TextView) findViewById(R.id.textContents3Title);
			contentTab1Title.setText("Videos of " + uni.getPOI(markerIndex).getTitle());
			
			//VideoListAdapter adapter = new VideoListAdapter(this,uni.getPOIlist().get(markerIndex).getVideos());
			//listview.setAdapter(adapter);
			
			WebView vd = (WebView) this.findViewById(R.id.webView1);
		    vd.getSettings().setJavaScriptEnabled(true);
		    vd.getSettings().setPluginsEnabled(true);
		    //vd.setVerticalScrollBarEnabled(true);
		    final String mimeType = "text/html";
		    final String encoding = "UTF-8";
		    vd.setWebChromeClient(new WebChromeClient() {
		    });
		    String myTotalUrl = "<html><body>";
		    ArrayList<String> myVidUrl = uni.getPOIlist().get(markerIndex).getVideos();
		    
		    for(int i = 0; i<myVidUrl.size(); i++) 
		    {
		    	myTotalUrl += getVideoHtml(myVidUrl.get(i).toString());
		    }
		    
		    myTotalUrl += "</body></html>";
		    Log.d("In VIDEO TAB","html code is: " + myTotalUrl);
		    vd.loadData( myTotalUrl, mimeType, encoding);
			
		}
		
		  private String getVideoHtml(String myUrl)
		  {
			//change the default url to an embeded one
			    String defaultUrl = myUrl;
			    String embeddedUrl = "http://www.youtube.com/embed/bIPcobKMB94";
			    if(defaultUrl.contains("youtube"))
			    {
			    	Log.d("IN ADAPTER OF VIDLISTVIEW", "OK, WE GOT A YOUTUBE LINK ");
			    	String vidId = defaultUrl.split("=")[1];
			    	embeddedUrl = "http://www.youtube.com/embed/" + vidId;
			    }
			    
		        //get screen width and height to help in the styling
		        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		        
		        Display display = wm.getDefaultDisplay(); 
		        Point p = new Point();
		        int width = display.getWidth();
		        Log.d("iN LISTVIEW OF VID" , "AND THE REPORTED WIDTH IS.... " + width);
			    String webData = "Youtube <br> <iframe width='300' height='250' class=\"youtube-player\" type=\"text/html\" src='" + embeddedUrl + "' frameborder=\"0\" allowfullscreen></iframe><br/><br/>";
			    
			    return webData;
		  }
		  
		  
		  /**
		   * ViewHolder to contain the view objects in order to try and increase performance
		   */
		  static class ViewHolder
		  {
			  WebView vd;
			  int position;
		  }
		
		/**
		 * Adapter class that populates the listview with the videos, and their corresponding captions
		 * @author frantz
		 *
		 */
		private class VideoListAdapter extends ArrayAdapter<String>
		{
			private final  Context context;
			 private final ArrayList<String> values;
			 
			  public VideoListAdapter(Context context, ArrayList<String> arrayList ) 
			  {
				    super(context, R.layout.discover_map_tab3_list, arrayList);
				    values = arrayList;
				    this.context = context;
				    Log.d("IN VIDEOLISTADAPTER", "GETS CALLED ONCE in constructor");
			  }
			  
			  


			/*  
			  @Override
			  public View getView(final int position, View convertView, ViewGroup parent) 
			  {
			    ViewHolder holder;
			    if(convertView == null)
			    {
			    	LayoutInflater inflater = (LayoutInflater) context
					        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					    	convertView = inflater.inflate(R.layout.discover_map_tab3_list, parent, false);
					    	
					    	holder = new ViewHolder();
					    	holder.vd = (WebView) convertView.findViewById(R.id.webView1);
					    	convertView.setTag(holder);
			    	 
			    }
			    else
			    {
			    	holder = (ViewHolder) convertView.getTag();
			    }
			   
			    
			    //Initialize youtube video player
			    
			    //change the default url to an embeded one
			    String defaultUrl = values.get(position).toString();
			    String embeddedUrl = "http://www.youtube.com/embed/bIPcobKMB94";
			    if(defaultUrl.contains("youtube"))
			    {
			    	Log.d("IN ADAPTER OF VIDLISTVIEW", "OK, WE GOT A YOUTUBE LINK ");
			    	String vidId = defaultUrl.split("=")[1];
			    	embeddedUrl = "http://www.youtube.com/embed/" + vidId;
			    }
			    
		        //get screen width and height to help in the styling
		        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		        Display display = wm.getDefaultDisplay(); 
		        Point p = new Point();
		        int width = display.getWidth();
		        Log.d("iN LISTVIEW OF VID" , "AND THE REPORTED WIDTH IS.... " + width);
			    String webData = "<html><body>Youtube <br> <iframe width='300' height='250' class=\"youtube-player\" type=\"text/html\" src='" + embeddedUrl + "' frameborder=\"0\" allowfullscreen></body></html>";
			    
			    holder.vd.getSettings().setJavaScriptEnabled(true);
			    holder.vd.getSettings().setPluginsEnabled(true);
			    final String mimeType = "text/html";
			    final String encoding = "UTF-8";
			   // String html = getHTML(values.get(position).toString());
			    holder.vd.setWebChromeClient(new WebChromeClient() {
			    });
			    holder.vd.loadData( webData, mimeType, encoding);
			   
			    Log.d("IN ADAPTER OF VIDLISTVIEW" , "POSITION IS: " + position);
			    Log.d("IN ADAPTER OF IMG LISTVIEW", "video url data is now: " + values.get(position).toString());
			    return convertView;
			  }
			  */
			  public String getHTML(String vidPath) {
				    String html = "<iframe class=\"youtube-player\" style=\"border: 0; width: 100%; height: 95%; padding:0px; margin:0px\" id=\"ytplayer\" type=\"text/html\" src=\"" + vidPath
				            
				            + "\" frameborder=\"0\">\n"
				            + "</iframe>\n";
				    return html;
				}
	}
	
	}
}
