package discoverMyCampus;


/**
 * Object that contains information about the tours
 */
public class MyTour
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