package campusgame;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import network.RetrieveData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import session.SessionStore;
import user.Profile;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import campuswall.CampusWallImage;
import campuswall.CampusWallInviteFacebook;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.gotoohlala.R;

import datastorage.Base64;
import datastorage.CacheInternalStorage;
import datastorage.ConvertDpsToPixels;
import datastorage.FB;
import datastorage.FacebookReauthorize;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;

public class CampusGameType3 extends Activity {
	ImageView ivThumb, ivImage, bgFeaturedBlur;
	TextView tvTitle, tvName, tvAddress, tvDescription, tvStartTime, tvEndTime;
	Button bAddCalendar, bCheckIn, bMore;
	int user_like, user_attend;
	
	private static final List<String> PERMISSIONS = Arrays.asList("user_about_me", "user_birthday", "email", "publish_stream");
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
	Handler mHandler = new Handler();
	
	RelativeLayout content1, content2, content3, content4;
	boolean descrMaxLine = false;
	
	int badge_id, badge_id_unlock, user_checkin, attends;
	String badge_icon_thumb_url, badge_name_unlock;
	JSONObject badge_info, badge_info_unlock;
	
	ImageView ivBadgeImage, ivBadgeIcon;
	TextView tvBadgeName;
	RelativeLayout rlBadgeBg;
	
	int start, end, user_submissions, max_submissions, submissions_left;
	
	//YOU CAN EDIT THIS TO WHATEVER YOU WANT
    private static final int SELECT_PICTURE = 1;
    private static final int CROP_PICTURE = 2;
    private static final int CAMERA_PIC_REQUEST = 1337;
    
    private Uri mImageCaptureUri;
    
    String image_url;
    
    String caption_facebookShare, message_facebookShare, icon_url_facebookShare, url_facebookShare, description_facebookShare, url_name_facebookShare, game_image_url, game_name, game_rules, description;
	int event_id_facebookShare;
	
	Bitmap photo;
	
	BroadcastReceiver brLogout;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campusgametype3);
		
		Bundle b = this.getIntent().getExtras();
		event_id_facebookShare = b.getInt("event_id_facebookShare");
		caption_facebookShare = b.getString("caption_facebookShare");
		message_facebookShare = b.getString("message_facebookShare");
		icon_url_facebookShare = b.getString("icon_url_facebookShare");
		url_facebookShare = b.getString("url_facebookShare");
		description_facebookShare = b.getString("description_facebookShare");
		url_name_facebookShare = b.getString("url_name_facebookShare");
		game_image_url = b.getString("game_image_url");
		game_name = b.getString("game_name");
		game_rules = b.getString("game_rules");
		description = b.getString("description");
		start = b.getInt("start");
		end = b.getInt("end");
		user_submissions = b.getInt("user_submissions");
		max_submissions = b.getInt("max_submissions");
		submissions_left = max_submissions - user_submissions;

		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(CampusGameType3.this));
		header.setText(game_name);
		
		ivThumb = (ImageView) findViewById(R.id.ivThumb);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvName = (TextView) findViewById(R.id.tvName);
		tvAddress = (TextView) findViewById(R.id.tvAddress);
		tvDescription = (TextView) findViewById(R.id.tvDescription);
		tvStartTime = (TextView) findViewById(R.id.tvStartTime);
		tvEndTime = (TextView) findViewById(R.id.tvEndTime);
		ivImage = (ImageView) findViewById(R.id.ivImage);
		bCheckIn = (Button) findViewById(R.id.bCheckIn);
		bMore = (Button) findViewById(R.id.bMore);
		bgFeaturedBlur = (ImageView) findViewById(R.id.bgFeaturedBlur);
		ivBadgeImage = (ImageView) findViewById(R.id.ivBadgeImage);
		tvBadgeName = (TextView) findViewById(R.id.tvBadgeName);
		rlBadgeBg = (RelativeLayout) findViewById(R.id.rlBadgeBg);
		ivBadgeIcon = (ImageView) findViewById(R.id.ivBadgeIcon);

		content1 = (RelativeLayout) findViewById(R.id.content1);
		content2 = (RelativeLayout) findViewById(R.id.content2);
		content3 = (RelativeLayout) findViewById(R.id.content3);
		content4 = (RelativeLayout) findViewById(R.id.content4);
		
		TaskQueueImage.addTask(new loadThumbImage(), CampusGameType3.this);
		
		tvTitle.setTypeface(Fonts.getOpenSansBold(CampusGameType3.this));
		tvTitle.setText(StringLanguageSelector.retrieveString(game_name));	
		tvStartTime.setText(getString(R.string.Ends_at_) + TimeCounter.getGameTime(end));
		
		tvEndTime.setTypeface(Fonts.getOpenSansLightItalic(getApplicationContext()));
		tvEndTime.setText(getString(R.string.Submissions_Left) + submissions_left);
		
		tvAddress.setTypeface(Fonts.getOpenSansBold(CampusGameType3.this));
		tvAddress.setText(game_name);
		tvDescription.setText(StringLanguageSelector.retrieveString(description));
		
		if (submissions_left == 0){
			// used up all your submissions, now it's a invite button
			bCheckIn.setText(getString(R.string.Invite_Friends));
		} 
		bCheckIn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (isGameOn(start, end)){
					if (submissions_left > 0){
						Session session = Session.getActiveSession();
			            if (session == null) {
			                if (session == null) {
			                    session = new Session(CampusGameType3.this);
			                }
			                
			                Session.setActiveSession(session);
			                if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
			                    session.openForRead(new Session.OpenRequest(CampusGameType3.this).setCallback(statusCallback));
			                }
			            } else {
							if (session.isOpened()) {
								getPicture();
							} else {
						        FacebookSessionStart();
						   	}
			            }
					} else {
						//invite facebook users to join
						Intent i = new Intent(CampusGameType3.this, CampusWallInviteFacebook.class);
						startActivity(i);
					}
				} else {
					//create an alert dialog: game not started yet, can not upload photo
					AlertDialog alert = new AlertDialog.Builder(CampusGameType3.this).create();
					alert.setMessage(getString(R.string.Game_is_not_started_yet_please_wait));
					alert.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					alert.show();
				}
			}
		});
		
		content1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
				Bundle extras = new Bundle();
				extras.putString("image_url", game_image_url);
				extras.putInt("picWidth", 512);
				Intent i = new Intent(CampusGameType3.this, CampusWallImage.class);
				i.putExtras(extras);
				startActivity(i);
			}
	    });
		
		content3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Bundle extras = new Bundle();
				extras.putString("game_rules", game_rules);
				Intent i = new Intent(CampusGameType3.this, CampusGameShowRule.class);
				i.putExtras(extras);
				startActivity(i);
            }
		});
		
		content4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	if (!descrMaxLine){
            		tvDescription.setMaxLines(Integer.MAX_VALUE);
            		descrMaxLine = true;
            	} else {
            		tvDescription.setMaxLines(3);
            		descrMaxLine = false;
            	}
			}
	    }); 
		
		content2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//showCalendarAlertDialog();
			}
	    }); 
		
		bMore.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
			}
		}); 
		
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.gotoohlala.profile.ProfileSettings");
        brLogout = new BroadcastReceiver() {
        	@Override
         	public void onReceive(Context context, Intent intent) {
        		Log.d("onReceive","Logout in progress");
            	//At this point you should start the login activity and finish this one
        		finish();
        	}
        };
        registerReceiver(brLogout, intentFilter);
	}
	
	public void onDestroy(){
		super.onDestroy();

		unregisterReceiver(brLogout);
	}
	
	private void FacebookSessionStart() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
	     	session.openForRead(new Session.OpenRequest(CampusGameType3.this).setCallback(statusCallback));
		} else {
	    	Session.openActiveSession(CampusGameType3.this, true, statusCallback);
		}
	}
	
	private class SessionStatusCallback implements Session.StatusCallback {
        public void call(Session session, SessionState state, Exception exception) {
	        if (session.isOpened()) {
	        	getPicture();
	        } else {
	        	FacebookSessionStart();
	        }
        }
    }
	
	public void shareOnFacebook() {
		//Log.d("Tests", "Testing graph API wall post");
		Session session = Session.getActiveSession();
	    if (session != null){
	        // Check for publish permissions    
	        List<String> permissions = session.getPermissions();
	        if (!isSubsetOf(PERMISSIONS, permissions)) {
	            Session.NewPermissionsRequest newPermissionsRequest = new Session
	                    .NewPermissionsRequest(this, PERMISSIONS);
	            session.requestNewPublishPermissions(newPermissionsRequest);
	            return;
	        }
         
	        try {
	             HttpClient httpClient = new DefaultHttpClient();
	             HttpContext localContext = new BasicHttpContext();
	             HttpPost httpPost = new HttpPost(
	                     "https://graph.facebook.com/me/photos?access_token="
	                             + session.getAccessToken());
	             MultipartEntity entity = new MultipartEntity(
	                     HttpMultipartMode.BROWSER_COMPATIBLE);
	             ByteArrayOutputStream bos = new ByteArrayOutputStream();
	             photo.compress(CompressFormat.PNG, 100, bos);
	             byte[] data = bos.toByteArray();
	             entity.addPart("source", new ByteArrayBody(data, "myImage.png"));
	             entity.addPart("message", new StringBody(description_facebookShare));
	             httpPost.setEntity(entity);
	             HttpResponse httpResponse = httpClient.execute(httpPost,
	                     localContext);
	             
	             String response = null;
	             HttpEntity entity2 = httpResponse.getEntity();
	 	     	 if (entity2 != null) {
	 	     		InputStream instream = entity2.getContent();
	 	     		response = RestClient.convertStreamToString(instream);
	 	     		instream.close();
	 	      	}
	
	        	 Log.i("response", response);
	        	 if (response == null || response.equals("") || 
		        	response.equals("false")) {
	        		 Log.v("Error", "Blank response");
	        	 } else {
	        		//Toast.makeText(getApplicationContext(), getString(R.string.Shared_on_Facebook), Toast.LENGTH_SHORT).show();  
	        		 
	        		RestClient result2 = null;
	     			try {
	     				result2 = new Rest.requestBody().execute(Rest.GAME + event_id_facebookShare, Rest.OSESS + Profile.sk, Rest.PUT, "1", "fb_share", "1").get();
	     			} catch (InterruptedException e1) {
	     				// TODO Auto-generated catch block
	     				e1.printStackTrace();
	     			} catch (ExecutionException e1) {
	     				// TODO Auto-generated catch block
	     				e1.printStackTrace();
	     			}
	     			Log.i("game share on facebook", String.valueOf(result2.getResponse()));
	        	 }
	         } catch(Exception e) {
	             e.printStackTrace();
	         } 
	    }
    }
	
	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
	    for (String string : subset) {
	        if (!superset.contains(string)) {
	            return false;
	        }
	    }
	    return true;
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
	
	public long getCalendarTime(String time){
		long millis = Integer.valueOf(time)*1000L;
		return millis;
	}
	
	private class loadOriginalImage extends AsyncTask<String, Void, Void> {
    	
		@Override
		protected Void doInBackground(String... badge_icon_url) {
			if (badge_icon_url[0] != null){
				final Bitmap bitmap = ImageLoader.originalImageStoreAndLoad(badge_icon_url[0], CampusGameType3.this);
				runOnUiThread(new Runnable() {
					public void run() {
						if (bitmap != null){
							ivBadgeImage.setImageBitmap(bitmap);
						}
					}
				});
			}
			
			return null;
		}
    }
	
	private void showCalendarAlertDialog() {
		// TODO Auto-generated method stub
    	AlertDialog alert = new AlertDialog.Builder(CampusGameType3.this).create();
		alert.setMessage(getString(R.string.Add_into_your_calendar));
		alert.setButton(getString(R.string.No), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		alert.setButton2(getString(R.string.Yes), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Calendar cal = Calendar.getInstance();              
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setType("vnd.android.cursor.item/event");
				intent.putExtra("beginTime", getCalendarTime(String.valueOf(start)));
				intent.putExtra("allDay", false);
				//intent.putExtra("rrule", "FREQ=YEARLY");
				intent.putExtra("endTime", getCalendarTime(String.valueOf(end)));
				intent.putExtra("title", StringLanguageSelector.retrieveString(game_name));
				startActivity(intent);
			}
		});
		alert.show();
	}
	
	class loadThumbImage extends Thread {
	    // This method is called when the thread runs
	    public void run() {
			mHandler.post(new Runnable() {
        		public void run() {
        			if (game_image_url.trim().length() > 0){
        				Bitmap bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(game_image_url, getApplicationContext(), 70);
        				if (bitmap != null){
        					ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(bitmap), 10));	
        					bgFeaturedBlur.setImageBitmap(bitmap);
        				}
        			}
				}
			});
		}
    }
	
	public boolean isGameOn(int start, int end){
		int now = (int) (System.currentTimeMillis()/1000);
		if (start <= now && end > now){
			return true;
		} else {
			return false;
		}
	}
	
	//UPDATED
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                int selectedImageWidth = 0;
                int selectedImageHeight = 0;
                
                Bitmap bitmapSizeTest = CacheInternalStorage.decodeCampusWallBitmapFromInputStream(selectedImageUri, CampusGameType3.this, 512);
                if (bitmapSizeTest != null) {
                	selectedImageWidth = bitmapSizeTest.getWidth();
    				selectedImageHeight = bitmapSizeTest.getHeight();
    				selectedImageHeight = 512*selectedImageHeight/selectedImageWidth;
    				selectedImageWidth = 512;
    					
    				Log.i("selectedImageWidth", String.valueOf(selectedImageWidth));
    				Log.i("selectedImageHeight", String.valueOf(selectedImageHeight));
    				
                	photo = Bitmap.createScaledBitmap(bitmapSizeTest, selectedImageWidth, selectedImageHeight, false);             
   					
   					new uploadImage().execute(SaveToPNG(photo));	
   					
   					shareOnFacebook();   
   				} 
                //if cancelled the crop
                else { 
                	
   				}
            }
            
            if (requestCode == CROP_PICTURE) {
                Bundle extras = data.getExtras();  
                if (extras != null) {
                	photo = Bitmap.createScaledBitmap((Bitmap) extras.getParcelable("data"), 512, 512, false);              
   					
   					new uploadImage().execute(SaveToPNG(photo));	
   				}
                //if cancelled the crop
                else { 
   					
   				}
            }
            
            if (requestCode == CAMERA_PIC_REQUEST) {
            	int selectedImageWidth = 0;
                int selectedImageHeight = 0;
    	
                if (mImageCaptureUri != null) {
                	Bitmap bitmapSizeTest = CacheInternalStorage.decodeCampusWallBitmapFromInputStream(mImageCaptureUri, CampusGameType3.this, 512);
                	selectedImageWidth = bitmapSizeTest.getWidth();
    				selectedImageHeight = bitmapSizeTest.getHeight();
    				selectedImageHeight = 512*selectedImageHeight/selectedImageWidth;
    				selectedImageWidth = 512;
    				
    				Log.i("selectedImageWidth", String.valueOf(selectedImageWidth));
    				Log.i("selectedImageHeight", String.valueOf(selectedImageHeight));
    				
                	photo = Bitmap.createScaledBitmap(bitmapSizeTest, selectedImageWidth, selectedImageHeight, false);       

   					new uploadImage().execute(SaveToPNG(photo));
   					
   					shareOnFacebook();
   				} 
                //if cancelled the crop
                else { 
                	
   				}
            }
        }
    }
    
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encode(b, Base64.DEFAULT, b.length);
        return temp;
    }
    
    public String SaveToPNG(Bitmap bitmap){
    	ContextWrapper cw = new ContextWrapper(CampusGameType3.this);
		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
		File mypath = new File(directory, "temp.png");
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(mypath);
		    // Use the compress method on the BitMap object to write image to the OutputStream
		    if (bitmap != null){
		    	//new BitmapCompress(bitmap, fos).execute();
		    	bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
	
		    	//bitmap.recycle();
		    	//bitmap = null;
		    }
		} catch (FileNotFoundException e) {
		 	e.printStackTrace();
		} 
		
		return mypath.getPath();
    }

    //UPDATED!
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }
    
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
	
    public void startPhotoCrop(Uri uri, int width, int height) {
        Intent intent = new Intent("com.android.camera.action.CROP");  
        intent.setDataAndType(uri, "image/*");  
        intent.putExtra("crop", "true");  
        // aspectX aspectY
        intent.putExtra("aspectX", 1);  
        intent.putExtra("aspectY", 1);  
        // outputX outputY
        intent.putExtra("outputX", 350);  
	    intent.putExtra("outputY", 350);  
        
        intent.putExtra("return-data", true);  
        
        Log.i("crop picture: ", intent.toString());
        startActivityForResult(Intent.createChooser(intent, getString(R.string.Crop_Picture)), CROP_PICTURE);  
    }  
    
    public void getPicture(){
    	final CharSequence[] items = {getString(R.string.Upload_Picture), getString(R.string.Take_Picture), getString(R.string.Cancel)};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(CampusGameType3.this);
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	switch(item){
		    	case 0:
		    		Intent intent = new Intent();
	                intent.setType("image/*");
	                intent.setAction(Intent.ACTION_GET_CONTENT);
	                startActivityForResult(Intent.createChooser(intent, getString(R.string.Select_Picture)), SELECT_PICTURE);
		    		break;
		    	case 1:
		    		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);  
		    		mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
		    				"tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".png"));

		    		cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
		    		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);  
		    		break;
		    	case 2:
		    		
		    		break;
		    	}
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
    }
    
    private class uploadImage extends AsyncTask<String, Void, Void> {
    	
		@Override
		protected Void doInBackground(String... filepath) {
			runOnUiThread(new Runnable() {
				public void run() {
					RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
							ConvertDpsToPixels.getPixels(30, getApplicationContext()), 
							ConvertDpsToPixels.getPixels(30, getApplicationContext()));
					param.addRule(RelativeLayout.RIGHT_OF, ivThumb.getId());
					param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
					param.setMargins(0, ConvertDpsToPixels.getPixels(5, getBaseContext()), 0, ConvertDpsToPixels.getPixels(10, getBaseContext()));
					bCheckIn.setLayoutParams(param);
					bCheckIn.setClickable(false);
					bCheckIn.setText(null);
					AnimationDrawable animationDrawable = (AnimationDrawable) getApplicationContext().getResources().getDrawable(R.drawable.loading);
					bCheckIn.setBackgroundDrawable(animationDrawable);
					animationDrawable.start();
				}
			});
			
			RestClient result = null;
			try {
				result = new Rest.requestBody().execute(Rest.IMAGE + "?image_type=0", Rest.OSESS + Profile.sk, Rest.POST2, "1", "path", filepath[0]).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("upload picture: ", result.getResponse());
			
			try {
				image_url = (new JSONObject(result.getResponse())).getString("image_url");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Log.i("event_id_facebookShare", String.valueOf(event_id_facebookShare));
			RestClient result2 = null;
			try {
				result2 = new Rest.requestBody().execute(Rest.GAME + event_id_facebookShare, Rest.OSESS + Profile.sk, Rest.PUT, "3", "message", "", "message_image_url", image_url, "ver", String.valueOf(RetrieveData.version)).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("game upload photo", String.valueOf(result2.getResponse()));
			
			int code = result2.getResponseCode();

			if (code == 200){
				runOnUiThread(new Runnable() {
					public void run() {
						RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
								ConvertDpsToPixels.getPixels(120, getApplicationContext()), 
								ConvertDpsToPixels.getPixels(30, getApplicationContext()));
						param.addRule(RelativeLayout.RIGHT_OF, ivThumb.getId());
						param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
						param.setMargins(0, ConvertDpsToPixels.getPixels(5, getBaseContext()), 0, ConvertDpsToPixels.getPixels(10, getBaseContext()));
						bCheckIn.setLayoutParams(param);
						bCheckIn.setClickable(true);
						bCheckIn.setText(getString(R.string.Upload_Photo));
						bCheckIn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_blue));
						
						submissions_left--;
						if (submissions_left > 0){
							tvEndTime.setText(getString(R.string.Submissions_Left) + submissions_left);
						} else {
							bCheckIn.setText(getString(R.string.Invite_Friends));
						}
						
						AlertDialog alert = new AlertDialog.Builder(CampusGameType3.this).create();
						alert.setMessage(getString(R.string.halloween_contest) +  game_name + getString(R.string.halloween_contest1) + "\n" + getString(R.string.halloween_contest2));
						alert.setButton(getString(R.string.No), new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						});
						alert.setButton2(getString(R.string.Yes), new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								Intent i = new Intent(CampusGameType3.this, CampusWallInviteFacebook.class);
								startActivity(i);
							}
						});
						alert.show();
					}
				});
			}
			
			return null;
		}
    }

}
