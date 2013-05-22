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
 * Gets the JSON data from the web, regarding the universities and their points of interest
 * @author frantz
 *
 */
public class GetJsonData 
{
	RestClient uniInfo = null;
	RestClient mainTourInfo = null; //Data about the current tour for the university
	RestClient buildingDataInfo = null;
	JSONArray buildinglist = null;
	String tourId;
	
	/**
	 * 
	 * @param tourId - the id of the tour in question (get using GetTourData object)
	 */
	public GetJsonData(String tourId)
	{
		this.tourId = tourId;
		
		//Retrieves university info
		try {
			uniInfo = new Rest.request().execute(Rest.SCHOOL + Profile.schoolId, Rest.OTOKE + Rest.accessCode2, Rest.GET).get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		//Retrieves information about a specific tour
			try {
				mainTourInfo = new request().execute(Rest.CAMPUS_TOUR +tourId, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
	}
	
	/**
	 * returns a list of buildings that are part of the current tour
	 */
	public JSONArray getBuildingList()
	{
		try 
		{
			if ((new JSONObject(mainTourInfo.getResponse())).has("school_locations"))
			{
				return new JSONObject(mainTourInfo.getResponse()).getJSONArray("school_locations") ;
			}
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the center latitude of the university
	 * @return
	 */
	public String getUniLat()
	{
		try 
		{
			if ((new JSONObject(uniInfo.getResponse())).has("latitude"))
			{
				return String.valueOf( new JSONObject(uniInfo.getResponse()).getDouble("latitude")) ;
			}
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return "0";
	}
	
	/**
	 * returns the center longitude of the university
	 * @return
	 */
	public String getUniLo()
	{
		try 
		{
			if ((new JSONObject(uniInfo.getResponse())).has("longitude"))
			{
				return String.valueOf( new JSONObject(uniInfo.getResponse()).getDouble("longitude")) ;
			}
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return "0";
	}
	
	/**
	 * returns all the points of interest, as well as their data that belongs to a particular university
	 * @return
	 */
	public ArrayList<InterestPoint> getUniPOI()
	{
		ArrayList myList = new ArrayList<InterestPoint>();
		try {
			JSONArray buildings = getBuildingList();
			Log.i("buildings: ", buildings.toString());
					
			for (int i = 0; i < buildings.length(); i++)
			{
				int id = buildings.getJSONObject(i).getInt("id");
				String name = buildings.getJSONObject(i).getString("name");
				String short_name = buildings.getJSONObject(i).getString("short_name");
				double longitude = buildings.getJSONObject(i).getDouble("longitude");
				double latitude = buildings.getJSONObject(i).getDouble("latitude");
				String address = buildings.getJSONObject(i).getString("address");
				
				Log.d("IN GETJSONDATA" , "Id is: " + id);
				//Retrieves building data for each building
				//retrieves building information, given the building id
				try {
					buildingDataInfo = new request().execute(Rest.CAMPUS_BUILDING + id + "?campus_tour_id=" + tourId, Rest.OSESS + Profile.sk, Rest.GET).get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.d("IN GETJSONDATA" , "SO FAR SO GOOD HERE: i is: " + i);
				JSONObject myBuilding = null;
				JSONObject tour_data = null;
				try 
				{
					if ((new JSONObject(buildingDataInfo.getResponse())).has("tour_data"))
					{
						myBuilding = new JSONObject(buildingDataInfo.getResponse());
						tour_data = myBuilding.getJSONObject("tour_data");
					}
					else
					{
						Log.d("IN GETJSONDATA", "tour_data does not exist. NO GOOD! NO GOOD!");
						new JSONObject(buildingDataInfo.getResponse()).toString();
					}
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
				Log.d("IN GETJSONDATA", "value of tour_data is: " + tour_data.toString());
				Log.d("IN GETJSONDATA, VALUE OF ARRAY " , getBuildingList().toString());
				JSONArray txtData = new JSONArray( tour_data.getString("txt_data") );
				JSONArray imgData =  tour_data.getJSONArray("img_data");
				JSONArray vidData =  tour_data.getJSONArray("vid_data");
				
				String myText = "";
				for(int j = 0; j < txtData.length(); j++)
				{
					myText += txtData.getString(j);
					myText += " \n ";
				}
				
				//decodes the image data
				Log.d("IN GETJSONDATA" , "Img data is: " + imgData.toString());
				ArrayList<String> pictures = new ArrayList<String>();
				ArrayList<String> video = new ArrayList<String>();
				ArrayList<String> picturesCaption = new ArrayList<String>();
				for(int j=0; j<imgData.length(); j++ )
				{
					pictures.add(imgData.getJSONArray(j).getString(0));
					
				}
				
				for(int j=0; j<imgData.length(); j++ )
				{
					picturesCaption.add(imgData.getJSONArray(j).getString(1));
					
				}
				
				for(int j=0; j<vidData.length(); j++ )
				{
					video.add(vidData.getString(j));
					
				}
				
				myList.add(new InterestPoint(String.valueOf(latitude), String.valueOf(longitude),pictures,picturesCaption,video,myText,"id" + i,short_name));
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("IN GETJSONDATA", "ERROR... IN CATCH...");
			e.printStackTrace();
		}
		
		
		return myList;
	}
	
	/**
	 * Object that contains information about the tours
	 */
	private class MyTour
	{
		String tourId, tourDescription, tourName;
		public MyTour(String id, String description, String name)
		{
			tourId = id;
			tourDescription = description;
			tourName = name;
		}
		
		//Returns the tour information
		public String getId() { return tourId; }
		public String getDescription() { return tourDescription; }
		public String getName() { return tourName; }
	}
}

