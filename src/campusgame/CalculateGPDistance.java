package campusgame;

public class CalculateGPDistance {
	double d2r = Math.PI / 180;
	double edLongi = 0;
	double edLat = 0;
	double stLongi = 0;
	double stLat = 0;
	
	public CalculateGPDistance(double edla, double edlo, double stla, double stlo){
		edLat = edla;
		edLongi = edlo;
		stLat = stla;
		stLongi = stlo;
	}
	
	public double Calulate(){
		try{
		    double dlong = (edLongi - stLongi) * d2r;
		    double dlat = (edLat - stLat) * d2r;
		    double a =
		        Math.pow(Math.sin(dlat / 2.0), 2)
		            + Math.cos(stLat * d2r)
		            * Math.cos(edLat * d2r)
		            * Math.pow(Math.sin(dlong / 2.0), 2);
		    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		    double d = 6367 * c;
	
		    return d;
	
		} catch(Exception e){
		    e.printStackTrace();
		    return 0;
		}
	}
	
}
