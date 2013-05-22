package datastorage;

public interface RestClientInterface {

	public void getResponse(String response);

    public void getErrorMessage(String message);

    public void getResponseCode(int responseCode);
    
}
