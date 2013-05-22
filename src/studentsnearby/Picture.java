package studentsnearby;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import network.RetrieveData;
import user.Profile;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import campuswall.CampusWallPostThread;

import com.gotoohlala.R;

import datastorage.Base64;
import datastorage.CacheInternalStorage;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;

public class Picture extends Activity {
	
	//YOU CAN EDIT THIS TO WHATEVER YOU WANT
    private static final int SELECT_PICTURE = 1;
    private static final int CROP_PICTURE = 2;
    private static final int CAMERA_PIC_REQUEST = 1337;
    
    private String selectedImagePath;
    //ADDED
    private String filemanagerstring;
    public static PictureUpdate p;
    
    private Uri mImageCaptureUri;
    
    String avatar_url, avatar_thumb_url;
    
    protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		final CharSequence[] items = {getString(R.string.Upload_Picture), getString(R.string.Take_Picture), getString(R.string.Cancel)};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.You_need_to_upload_your_profile_picture_to_see_nearby_students));
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	switch(item){
		    	case 0:
		    		Intent intent = new Intent();
	                intent.setType("image/*");
	                intent.setAction(Intent.ACTION_GET_CONTENT);
	                startActivityForResult(Intent.createChooser(intent, getString(R.string.Select_Picture)), SELECT_PICTURE);
	                //onBackPressed();
		    		break;
		    	case 1:
		    		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
		    		mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
		    				"tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".png"));

		    		cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
		    		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);  
		    		//onBackPressed();
		    		break;
		    	case 2:
		    		onBackPressed();
		    		break;
		    	}
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
		
    }

  //UPDATED
  	public void onActivityResult(int requestCode, int resultCode, Intent data) {
          if (resultCode == RESULT_OK) {
              if (requestCode == SELECT_PICTURE) {
                  Uri selectedImageUri = data.getData();
                  int selectedImageWidth = 0;
                  int selectedImageHeight = 0;
                  
                  Bitmap bitmapSizeTest = CacheInternalStorage.decodeAvatarBitmapFromInputStream(selectedImageUri, Picture.this, 90);
  				  selectedImageWidth = bitmapSizeTest.getWidth();
  				  selectedImageHeight = bitmapSizeTest.getHeight();
  				
                  Log.i("select picture: ", "aa");
                  // crop the image
                  startPhotoCrop(selectedImageUri, selectedImageWidth, selectedImageHeight);  

                  //OI FILE Manager
                  filemanagerstring = selectedImageUri.getPath();

                  //MEDIA GALLERY
                  selectedImagePath = getPath(selectedImageUri);

                  //DEBUG PURPOSE - you can delete this if you want
                  if(selectedImagePath!=null)
                  	System.out.println(selectedImagePath);
                  else System.out.println("selectedImagePath is null");
                  if(filemanagerstring!=null)
                      System.out.println(filemanagerstring);
                  else System.out.println("filemanagerstring is null");

                  //NOW WE HAVE OUR WANTED STRING
                  if(selectedImagePath!=null)
                      System.out.println("selectedImagePath is the right one for you!");
                  else
                      System.out.println("filemanagerstring is the right one for you!");
              }
              
              if (requestCode == CROP_PICTURE) {
                  Bundle extras = data.getExtras();  
                  if (extras != null) {
                	  	final Bitmap photo = Bitmap.createScaledBitmap((Bitmap) extras.getParcelable("data"), 512, 512, false);           
     					
     					String encodedImage = BitMapToString(photo);
     					encodedImage = encodedImage.replace("+", "%2B");
     					//Log.i("encoded bitmap", encodedImage);
     					
     					new uploadAvatar().execute(SaveToPNG(photo));
     					
     					//reset the avatar imageView
     					if (p != null){
     						p.pictureUpdate(photo);
     					}
  					    onBackPressed(); 
     				}
              }
              
              if (requestCode == CAMERA_PIC_REQUEST) {
                  int selectedImageWidth = 0;
                  int selectedImageHeight = 0;
      	
                  if (mImageCaptureUri != null) {            	
                  		Bitmap bitmapSizeTest = CacheInternalStorage.decodeAvatarBitmapFromInputStream(mImageCaptureUri, Picture.this, 90);
                  		selectedImageWidth = bitmapSizeTest.getWidth();
                  		selectedImageHeight = bitmapSizeTest.getHeight();
                  		selectedImageHeight = 512*selectedImageHeight/selectedImageWidth;
                  		selectedImageWidth = 512;
      				
                  		Log.i("selectedImageWidth", String.valueOf(selectedImageWidth));
                  		Log.i("selectedImageHeight", String.valueOf(selectedImageHeight));
      				
                  		Bitmap photo = ImageLoader.ImageCrop(Bitmap.createScaledBitmap(bitmapSizeTest, selectedImageWidth, selectedImageHeight, false));       
     					
     					String encodedImage = BitMapToString(photo);
     					encodedImage = encodedImage.replace("+", "%2B");
     					//Log.i("encoded bitmap", encodedImage);
     					
     					new uploadAvatar().execute(SaveToPNG(photo));
     					
     					//reset the avatar imageView
     					if (p != null){
     						p.pictureUpdate(photo);
     					}
					    onBackPressed();						
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
      	ContextWrapper cw = new ContextWrapper(Picture.this);
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
    
      private class uploadAvatar extends AsyncTask<String, Void, Void> {
      	
  			@Override
  			protected Void doInBackground(String... filepath) {
  				RestClient result = null;
  				try {
  					result = new Rest.requestBody().execute(Rest.IMAGE + "?image_type=2", Rest.OSESS + Profile.sk, Rest.POST2, "1", "path", filepath[0]).get();
  				} catch (InterruptedException e1) {
  					// TODO Auto-generated catch block
  					e1.printStackTrace();
  				} catch (ExecutionException e1) {
  					// TODO Auto-generated catch block
  					e1.printStackTrace();
  				}
  				Log.i("upload picture: ", result.getResponse());
  				
  				try {
  					avatar_url = (new JSONObject(result.getResponse())).getString("image_url");
  					avatar_thumb_url = (new JSONObject(result.getResponse())).getString("image_thumb_url");
  				} catch (JSONException e) {
  					// TODO Auto-generated catch block
  					e.printStackTrace();
  				}
  				
  				RestClient result2 = null;
				try {
					result2 = new Rest.requestBody().execute(Rest.USER, Rest.OSESS + Profile.sk, Rest.PUT, "2", "avatar_url", avatar_url, "avatar_thumb_url", avatar_thumb_url).get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.i("edit profile", result2.getResponse());
            	
            	if (result2.getResponseCode() == 200){
	  				//update profile
	  				runOnUiThread(new Runnable() {
	  					public void run() {
	  						Profile.updateUserProfile();
	  					}
	  				});
            	}
            	
  				return null;
  			}
      }
      
}
