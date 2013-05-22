package datastorage;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import network.ErrorCodeParser;
import network.RetrieveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import user.Profile;

import com.gotoohlala.R;

import datastorage.RestClient.RequestMethod;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class Rest {
	
	public static final String accessCode = "EWc07IT2fmoaOvmwjeVufgO7HrgkCqiA:";
	public static final String accessCode2 = "EWc07IT2fmoaOvmwjeVufgO7HrgkCqiA";
	public static final String OURL = "https://api.studentlifemobile.com/v1/";
	public static final int deviceType = 2;
	public static final String OSESS = "Osess ";
	public static final String OTOKE = "OToke ";
	public static final String GET = "0";
	public static final String POST = "1";
	public static final String PUT = "2";
	public static final String DELETE = "3";
	public static final String PUT2 = "4";
	public static final String POST2 = "5";
	public static final String PUT3 = "6";
	public static final String PUT4 = "7";
	public static final String POST3 = "8";
	
	/***
	 * Session login, logout
	 */
	public static final String RENEW_SESSION = "session/";
	public static final String CAMPUS_THREAD = "campus_thread/";
	public static final String CAMPUS_COMMENT = "campus_comment/";
	public static final String EVENT = "event/";
	public static final String STORE = "store/";
	public static final String DEAL = "deal/";
	public static final String CARD = "card/";
	public static final String STORE_CATEGORY = "store_category/";
	public static final String INBOX = "inbox/";
	public static final String REGISTRATION = "closedregistration";
	public static final String RESET_PASSWORD = "requestresetpass";
	public static final String APP_CONFIG = "app_config/";
	public static final String SCHOOL_COURSE = "school_course/";
	public static final String USER = "user/";
	public static final String CHAT_MESSAGE = "chat_message/";
	public static final String GAME = "game/";
	public static final String CAMPUS_BUILDING = "campus_building/";
	public static final String CAMPUS_TOUR = "campus_tour/";
	public static final String SCHOOL = "school/";
	public static final String SCHOOL2 = "school";
	public static final String IMAGE = "image/";
	public static final String BADGE = "badge/";
	public static final String GROUP = "group/";
	public static final String GROUP_THREAD = "group_thread/";
	public static final String GROUP_COMMENT = "group_comment/";
	public static final String FRIEND_REQUEST = "friend_request/";
	public static final String USER_REQUEST = "user_request/";
	public static final String USER_EVENT = "user_event/";
	public static final String USER_INVITE = "user_invite/";

	public static Context client;
	public static boolean showUpdateDialog = false;
	public static boolean showUpdateDialogOnce = false;
	private static Handler mHandler = new Handler();

       
        //new renewSession().execute(RENEW_SESSION, "OToke " + accessCode + "jameswang@gotoohlala.com:d8578edf8458ce06fbc5bb76a58c5ca4", "1");
        //new UploadPicThread().execute("user", "Osess NxcfkCXB6vISmYClsLdWFTyMS78meowy", "0");
        //new UploadPicThread().execute("school/2", "OToke EWc07IT2fmoaOvmwjeVufgO7HrgkCqiA", "0");
        //new UploadPicThread().execute("campus_thread", "Osess NxcfkCXB6vISmYClsLdWFTyMS78meowy", "0");
        //new UploadPicThread().execute("campus_building/1", "Osess NxcfkCXB6vISmYClsLdWFTyMS78meowy", "0");
        //new UploadPicThread().execute(APP_CONFIG, "OToke EWc07IT2fmoaOvmwjeVufgO7HrgkCqiA", "0");
        //new requestMethod().execute(SCHOOL_COURSE, "Osess " + Profile.sk, "0");
        

    public static class renewSession extends AsyncTask<String, Void, RestClient> {
		
		@Override
		protected RestClient doInBackground(String... params) {
			String result = "";
			
			RestClient client = new RestClient(OURL + params[0]);
			client.AddHeader("Authorization", params[1]);

	        try {
	        	switch(Integer.valueOf(params[2])){
	        	case 0:
	        		client.Execute(RequestMethod.GET);
	        		break;
	        	case 1:
	        		client.Execute(RequestMethod.POST);
	        		break;
	        	case 2:
	        		client.Execute(RequestMethod.PUT);
	        		break;
	        	case 3:
	        		client.Execute(RequestMethod.DELETE);
	        		break;
	        	}
	            
	            result = client.getResponse();
		        
		        try {
					JSONObject j = new JSONObject(result);
					j.getString("");
				} catch (Exception e) {

				}
				if (params[0].contains(RENEW_SESSION)) {
					Profile.setKey(result);
					Log.i("sk------------------", Profile.sk);
				}
				
				Log.i("expiry", String.valueOf(ErrorCodeParser.parseExpiry(result)));
				Log.i("now", String.valueOf(System.currentTimeMillis()/1000));
		        //Log.i("hash", hash("qwerty"));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			
			return client;
		}
    }
    
    public static class request extends AsyncTask<String, Void, RestClient> {
		
		@Override
		protected RestClient doInBackground(String... params) {
			if (Profile.expiry <= System.currentTimeMillis()/1000 + 3600 && Profile.expiry > System.currentTimeMillis()/1000) {
				RestClient resultRenewSession = null;
				if (Profile.email != null && Profile.pass != null){
					try {
						resultRenewSession = new Rest.renewSession().execute(Rest.RENEW_SESSION, "OToke " + Rest.accessCode + Profile.email + ":" + Profile.pass, Rest.POST).get();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				String result2 = null;
				try {
					result2 = new request().execute(Rest.APP_CONFIG, Rest.OSESS + Profile.sk, Rest.GET).get().getResponse();
					Profile.setAppConfiguration(result2);
					if (Profile.has_update){
						showUpdateDialog = true;
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				/*
				try {
					return new request().execute(params).get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
				*/
				
				return null;
			} else {
				String result = "";
				
				RestClient client = new RestClient(OURL + params[0]);
				client.AddHeader("Authorization", params[1]);
	
		        try {
		        	switch(Integer.valueOf(params[2])){
		        	case 0:
		        		client.Execute(RequestMethod.GET);
		        		break;
		        	case 1:
		        		client.Execute(RequestMethod.POST);
		        		break;
		        	case 2:
		        		client.Execute(RequestMethod.PUT);
		        		break;
		        	case 3:
		        		client.Execute(RequestMethod.DELETE);
		        		break;
		        	}
		            
		            result = client.getResponse();
		            
		            try {
						JSONObject j = new JSONObject(result);
						j.getString("");
					} catch (Exception e) {
						
					}
		            
		            if (client.getResponseCode() == 401){
		            	RestClient resultRenewSession = null;
						if (Profile.email != null && Profile.pass != null){
							try {
								resultRenewSession = new Rest.renewSession().execute(Rest.RENEW_SESSION, "OToke " + Rest.accessCode + Profile.email + ":" + Profile.pass, Rest.POST).get();
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (ExecutionException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						
						String result2 = null;
						try {
							result2 = new request().execute(Rest.APP_CONFIG, Rest.OSESS + Profile.sk, Rest.GET).get().getResponse();
							Profile.setAppConfiguration(result2);
							if (Profile.has_update){
								showUpdateDialog = true;
							}
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						/*
						try {
							return new request().execute(params).get();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return null;
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return null;
						}
						*/
		            }
			        //Log.i("hash", hash("qwerty"));
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
				
				return client;
			}
		}
    }
    
    public static class requestBody extends AsyncTask<String, Void, RestClient> {
		
		@Override
		protected RestClient doInBackground(String... params) {
			if (Profile.expiry <= System.currentTimeMillis()/1000 + 3600 && Profile.expiry > System.currentTimeMillis()/1000) {
				RestClient resultRenewSession = null;
				if (Profile.email != null && Profile.pass != null){
					try {
						resultRenewSession = new renewSession().execute(Rest.RENEW_SESSION, "OToke " + Rest.accessCode + Profile.email + ":" + Profile.pass, Rest.POST).get();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				String result2 = null;
				try {
					result2 = new request().execute(Rest.APP_CONFIG, Rest.OSESS + Profile.sk, Rest.GET).get().getResponse();
					Profile.setAppConfiguration(result2);
					if (Profile.has_update){
						showUpdateDialog = true;
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				/*
				try {
					return new requestBody().execute(params).get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
				*/
				return null;
			} else {
				String result = "";
				RestClient client = new RestClient(OURL + params[0]);
				client.AddHeader("Authorization", params[1]);
				client.AddHeader("Content-Type", "application/json; charset=utf-8");
				
				for (int i = 0; i < Integer.valueOf(params[3]); i++){
					client.AddParam(params[4+i*2], params[5+i*2]);
				}
	
		        try {
		        	switch(Integer.valueOf(params[2])){
		        	case 0:
		        		client.Execute(RequestMethod.GET);
		        		break;
		        	case 1:
		        		client.Execute(RequestMethod.POST);
		        		break;
		        	case 2:
		        		client.Execute(RequestMethod.PUT);
		        		break;
		        	case 3:
		        		client.Execute(RequestMethod.DELETE);
		        		break;
		        	case 4:
		        		client.Execute(RequestMethod.PUT2);
		        		break;
		        	case 5:
		        		client.Execute(RequestMethod.POST2);
		        		break;
		        	case 6:
		        		client.Execute(RequestMethod.PUT3);
		        		break;
		        	case 7:
		        		client.Execute(RequestMethod.PUT4);
		        		break;
		        	case 8:
		        		client.Execute(RequestMethod.POST3);
		        		break;
		        	}
		            
		            result = client.getResponse();
		            try {
						JSONObject j = new JSONObject(result);
						j.getString("");
					} catch (Exception e) {
						
					}
		            
		            if (client.getResponseCode() == 401){
		            	RestClient resultRenewSession = null;
						if (Profile.email != null && Profile.pass != null){
							try {
								resultRenewSession = new Rest.renewSession().execute(Rest.RENEW_SESSION, "OToke " + Rest.accessCode + Profile.email + ":" + Profile.pass, Rest.POST).get();
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (ExecutionException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						
						String result2 = null;
						try {
							result2 = new request().execute(Rest.APP_CONFIG, Rest.OSESS + Profile.sk, Rest.GET).get().getResponse();
							Profile.setAppConfiguration(result2);
							if (Profile.has_update){
								showUpdateDialog = true;
							}
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						/*
						try {
							return new requestBody().execute(params).get();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return null;
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return null;
						}
						*/
		            }
			        //Log.i("hash", hash("qwerty"));
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
				
				return client;
			}
		}
    }
    
    public static String hash(String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest
	                .getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < messageDigest.length; i++) {
	            String h = Integer.toHexString(0xFF & messageDigest[i]);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}
    
}
