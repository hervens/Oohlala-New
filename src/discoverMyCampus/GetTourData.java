package discoverMyCampus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import campusmap.CampusMapModel;

import user.Profile;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.Rest.request;


/**
 * Gets information regarding to what tours are available at a university,
 * and wheter or not the user completed them, and populates them in an MyTour ArrayList
 * @author frantz
 *
 */
public class GetTourData 
{
	RestClient tourInfo = null; //data about all the tours for the university
	ArrayList<MyTour> tourList;
	
	public GetTourData()
	{
		tourList = new ArrayList<MyTour>();
		
		//Retrieves tour information
		try 
		{
			tourInfo = new request().execute(Rest.CAMPUS_TOUR, Rest.OSESS + Profile.sk, Rest.GET).get();
		} 
		catch (InterruptedException e1) 
		{
			e1.printStackTrace();
		} 
		catch (ExecutionException e1) 
		{
			e1.printStackTrace();
		}
		try 
		{
			JSONArray tour = new JSONArray(tourInfo.getResponse());
			Log.i("tour: ", tour.toString());
					
			for (int i = 0; i < tour.length(); i++)
			{
				String id = String.valueOf(tour.getJSONObject(i).getInt("id") );
				String name = tour.getJSONObject(i).getString("name");
				String description = tour.getJSONObject(i).getString("description");
				tourList.add(new MyTour(id,name,description));
			}
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}

	}
	
	/**
	 * 
	 * @return Returns a list of MyTour objects that are part of this university
	 */
	public ArrayList<MyTour> getTourList()
	{
		return tourList;
	}
}



