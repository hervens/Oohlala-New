package discoverMyCampus;
import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.gotoohlala.R;
import com.gotoohlala.TopMenuNavbar;

import discoverMyCampus.DiscoverMap.BuildingInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


/**
 * Represents the first screen user sees, when they select discover my campus
 * We display some basic rules, as well as a listview displaying the tours the user can select
 * @author frantz
 *
 */
@SuppressLint("ValidFragment")
public class RulesFragment extends FrameLayout
{
	View v = null;
	FrameLayout myLayout = this;
	Context myContext;
	public TopMenuNavbar TopMenuNavbar;
	RelativeLayout rlHeader;
	
	@SuppressLint("ValidFragment")
	public RulesFragment( final Context context ) 
	{
		super(context);
		myContext = context;
		
		final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
		 v = (RelativeLayout) mLayoutInflater.inflate(R.layout.rules_fragment, null);  
	     addView(v);  
	     TopMenuNavbar = (TopMenuNavbar) v.findViewById(R.id.bTopMenuNavBar);
	     rlHeader = (RelativeLayout) v.findViewById(R.id.rlHeader);
	     
	     GetTourData tourData = new GetTourData();
	     final TourListAdapter adapter = new TourListAdapter(context,tourData.getTourList());
	      ListView listview = (ListView) v.findViewById(R.id.myTourList);
	     listview.setAdapter(adapter);
	     
	     if(listview != null)
	     {
	   //btn listener for listview
	      listview.setOnItemClickListener(new OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	            {
	            	Log.d("IN RULESFRAGMENT", "listview item has been clicked");
	                String myId = adapter.getItem(position).getId();
	                
		            // TODO Auto-generated method stub
		        	Intent i = new Intent(context, DiscoverMap.class); 
		        	i.putExtra("selectedTourId", myId);
		        	//replaceContentView("Discover Map",i);
		        	myContext.startActivity(i);
	            }
	        });
	     }
	     else
	     {
	    	 Log.d("IN RULESFRAGMENT", "listview IS NULL!! NO GOOD NO GOOD!!");
	     }
	     
	}
	
	public void replaceContentView(String id, Intent newIntent) 
	{
	    View view = ((ActivityGroup) myContext)
	            .getLocalActivityManager()
	            .startActivity(id,
	                    newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
	            .getDecorView();
	    ((Activity) myContext).setContentView(view);

	}
	
	
	/**
	 * Adapter class for the listview
	 */
	private class TourListAdapter extends ArrayAdapter<MyTour>
	{
		private final  Context context;
		 private final ArrayList<MyTour> value;
		 
		  public TourListAdapter(Context context, ArrayList<MyTour> tourList) 
		  { 
			    super(context, R.layout.discover_tour_selection_list, tourList);
			    this.context = context;
			    this.value = tourList;
		  }
	  
		  
		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) 
		  {
		    View rowView = convertView;
		    
		    if(rowView == null)
		    {
		    	LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    	rowView = inflater.inflate(R.layout.discover_tour_selection_list, parent, false);
		    }
		   
		    
		    TextView myTour = (TextView) rowView.findViewById(R.id.txtTourName);
		    myTour.setText(value.get(position).getName());
		    
		    TextView txtId = (TextView) rowView.findViewById(R.id.txtTourId);
		    txtId.setText(value.get(position).getId());
		    
		    TextView txtDescription = (TextView) rowView.findViewById(R.id.txtTourDescription);
		    txtDescription.setText(value.get(position).getDescription());

		    return rowView;
		  }
	  
	}

	
}
