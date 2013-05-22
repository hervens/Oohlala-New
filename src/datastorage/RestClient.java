package datastorage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

public class RestClient {

    private ArrayList <NameValuePair> params;
    private ArrayList <NameValuePair> headers;

    private String url;

    private int responseCode;
    private String message;

    private String response;
    
    public enum RequestMethod {
    	GET,
    	POST,
    	PUT,
    	DELETE,
    	PUT2,
    	PUT3,
    	PUT4,
    	POST2,
    	POST3
    }

    public String getResponse() {
        return response;
    }

    public String getErrorMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public RestClient(String url) {
        this.url = url;
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
    }

    public void AddParam(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }

    public void AddHeader(String name, String value) {
        headers.add(new BasicNameValuePair(name, value));
    }

    public void Execute(RequestMethod method) throws Exception {
        switch(method) {
            case GET:
                //add parameters
                String combinedParams = "";
                if(!params.isEmpty()){
                    combinedParams += "?";
                    for(NameValuePair p : params){
                        String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
                        if(combinedParams.length() > 1){
                            combinedParams  +=  "&" + paramString;
                        }
                        else{
                            combinedParams += paramString;
                        }
                    }
                }

                HttpGet request = new HttpGet(url + combinedParams);

                //add headers
                for(NameValuePair h : headers){
                    request.addHeader(h.getName(), h.getValue());
                }

                executeRequest(request, url);
                break;
            case POST:
            	HttpPost request2 = new HttpPost(url);

                //add headers
                for(NameValuePair h : headers){
                    request2.addHeader(h.getName(), h.getValue());
                }

                if(!params.isEmpty()){
                	JSONStringer json = new JSONStringer();
                	json.object();
                	for(NameValuePair p : params){
                		json.key(p.getName()).value(p.getValue());
                	}
                	json.endObject();
                	request2.setEntity(new StringEntity(json.toString(), HTTP.UTF_8));
                }

                executeRequest(request2, url); 
                break;
            case PUT:
            	HttpPut request3 = new HttpPut(url);

                //add headers
                for(NameValuePair h : headers){
                	request3.addHeader(h.getName(), h.getValue());
                }

                if(!params.isEmpty()){
                	JSONStringer json = new JSONStringer();
                	json.object();
                	for(NameValuePair p : params){
                		json.key(p.getName()).value(p.getValue());
                	}
                	json.endObject();
                	request3.setEntity(new StringEntity(json.toString(), HTTP.UTF_8));
                }

                executeRequest(request3, url); 
                break;
            case DELETE:
            	HttpDelete request4 = new HttpDelete(url);

                //add headers
                for(NameValuePair h : headers){
                	request4.addHeader(h.getName(), h.getValue());
                }

                executeRequest(request4, url); 
                break;
            case PUT2:
            	HttpPut request5 = new HttpPut(url);

                //add headers
                for(NameValuePair h : headers){
                	request5.addHeader(h.getName(), h.getValue());
                }

                if(!params.isEmpty()){
                	JSONStringer json = new JSONStringer();
                	json.object();
                	
	                	JSONStringer json2 = new JSONStringer();
	                	json2.array();
	                	for(int i = 0; i < 3; i++){
	                		json2.value(Integer.valueOf(params.get(i).getValue()));
	                    }
	                	if (params.get(3).getValue().length() != 0){
	                		json2.value(params.get(3).getValue());
                		} else {
                			json2.value("");
                		}
	                	json2.endArray();
	                	Log.i("json2", json2.toString());
	                	
	                	JSONStringer json3 = new JSONStringer();
	                	json3.array();
	                	json3.value(new JSONArray(json2.toString()));
	                	json3.endArray();
	                	Log.i("json3", json3.toString());
	                	
                	json.key(params.get(0).getName()).value(new JSONArray(json3.toString()));
                	
                	for(int i = 4; i < params.size(); i++){
                		json.key(params.get(i).getName()).value(params.get(i).getValue());
                	}
                	
                	json.endObject();
                	Log.i("PUT2", json.toString());
                	request5.setEntity(new StringEntity(json.toString(), HTTP.UTF_8));
                }

                executeRequest(request5, url); 
                break;
            case PUT3:
            	HttpPut request7 = new HttpPut(url);

                //add headers
                for(NameValuePair h : headers){
                	request7.addHeader(h.getName(), h.getValue());
                }

                if(!params.isEmpty()){
                	JSONStringer json = new JSONStringer();
                	json.object();

	                	JSONStringer json3 = new JSONStringer();
	                	json3.array();
	                	json3.value(params.get(0).getValue());
	                	json3.endArray();
	                	Log.i("json3", json3.toString());
	                	
	                if (params.get(0).getValue() != null){
	                	json.key(params.get(0).getName()).value(new JSONArray(json3.toString()));
	                }
                	json.key(params.get(1).getName()).value(params.get(1).getValue());
                	json.key(params.get(2).getName()).value(params.get(2).getValue());
                	
                	json.endObject();
                	Log.i("PUT3", json.toString());
                	request7.setEntity(new StringEntity(json.toString(), HTTP.UTF_8));
                }

                executeRequest(request7, url); 
                break;
            case PUT4:
            	HttpPut request8 = new HttpPut(url);

                //add headers
                for(NameValuePair h : headers){
                	request8.addHeader(h.getName(), h.getValue());
                }

                if(!params.isEmpty()){
                	JSONStringer json = new JSONStringer();
                	json.object();
                	
	                if (params.get(0).getValue() != null){
	                	json.key(params.get(0).getName()).value(new JSONArray(params.get(0).getValue()));
	                }
                	
                	json.endObject();
                	Log.i("PUT4", json.toString());
                	request8.setEntity(new StringEntity(json.toString(), HTTP.UTF_8));
                }

                executeRequest(request8, url); 
                break;
            case POST2:
            	HttpPost request6 = new HttpPost(url);

                //add first header
                request6.addHeader(headers.get(0).getName(), headers.get(0).getValue());
                
                Log.i("file___path", params.get(0).getValue());
                File file = new File(params.get(0).getValue());
                
                MultipartEntity multipartContent = new MultipartEntity();
                multipartContent.addPart("file", new FileBody(file, "image/png"));
                request6.setEntity(multipartContent);
                
                executeRequest(request6, url); 
                break;
            case POST3:
            	HttpPost request9 = new HttpPost(url);

                //add headers
                for(NameValuePair h : headers){
                	request9.addHeader(h.getName(), h.getValue());
                }
                
                if(!params.isEmpty()){
                	JSONStringer json = new JSONStringer();
                	json.object();
                	
                	json.key(params.get(0).getName()).value(params.get(0).getValue());
	                
	                	JSONStringer json2 = new JSONStringer();
	                	json2.array();
	                	for(int i = 1; i < 3; i++){
	                		json2.value(params.get(i).getValue());
	                    }
	                	json2.endArray();
	                	Log.i("json2", json2.toString());
	                	
	                	JSONStringer json3 = new JSONStringer();
	                	json3.array();
	                	json3.value(new JSONArray(json2.toString()));
	                	json3.endArray();
	                	Log.i("json3", json3.toString());
	                	
	            	json.key(params.get(1).getName()).value(new JSONArray(json3.toString()));
                	
                	json.endObject();
                	Log.i("POST3", json.toString());
                	request9.setEntity(new StringEntity(json.toString(), HTTP.UTF_8));
                }

                executeRequest(request9, url); 
                break;
        }
    }

    private void executeRequest(HttpUriRequest request, String url){
    	HttpClient client = getNewHttpClient();
	    HttpResponse httpResponse;

	 	try {
	     	httpResponse = client.execute(request);
	     	responseCode = httpResponse.getStatusLine().getStatusCode();
	     	message = httpResponse.getStatusLine().getReasonPhrase();

	     	HttpEntity entity = httpResponse.getEntity();
	     	if (entity != null) {
	     		InputStream instream = entity.getContent();
	     		response = convertStreamToString(instream);

	          	// Closing the input stream will trigger connection release
	         	instream.close();
	      	}

		} catch (ClientProtocolException e)  {
	    	client.getConnectionManager().shutdown();
	    	e.printStackTrace();
	  	} catch (IOException e) {
	     	client.getConnectionManager().shutdown();
	     	e.printStackTrace();
	 	}
    }
    
    public class executeRequestAsync extends AsyncTask<Void, Void, Void> {
    	HttpUriRequest request;
    	String url;
		
		public executeRequestAsync(HttpUriRequest request, String url){
			this.request = request;
			this.url = url;
		}
    	
		@Override
		protected Void doInBackground(Void... nothing) {
			HttpClient client = getNewHttpClient();
		    HttpResponse httpResponse;

		 	try {
		     	httpResponse = client.execute(request);
		     	responseCode = httpResponse.getStatusLine().getStatusCode();
		     	message = httpResponse.getStatusLine().getReasonPhrase();

		     	HttpEntity entity = httpResponse.getEntity();
		     	if (entity != null) {
		     		InputStream instream = entity.getContent();
		     		response = convertStreamToString(instream);

		          	// Closing the input stream will trigger connection release
		         	instream.close();
		      	}

			} catch (ClientProtocolException e)  {
		    	client.getConnectionManager().shutdown();
		    	e.printStackTrace();
		  	} catch (IOException e) {
		     	client.getConnectionManager().shutdown();
		     	e.printStackTrace();
		 	}
		 	
		 	return null;
		}
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 5000));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }
    
}
