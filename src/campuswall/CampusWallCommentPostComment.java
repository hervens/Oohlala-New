package campuswall;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.R.id;
import com.gotoohlala.R.layout;

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
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import datastorage.Base64;
import datastorage.CacheInternalStorage;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;

public class CampusWallCommentPostComment extends Activity{
	
	TextView tvSchoolName, tvWordsLeft;
	ImageView ivPreview;
	EditText etPostThread;
	CheckBox rbAnonymous;
	Button bPostThreadLocation, bPostThreadPicture;
	TextView bPostThread;
	int is_anonymous, type, school_thread_id;
	boolean postImage = false;
	
	 //YOU CAN EDIT THIS TO WHATEVER YOU WANT
    private static final int SELECT_PICTURE = 1;
    private static final int CROP_PICTURE = 2;
    private static final int CAMERA_PIC_REQUEST = 1337;  

    private String selectedImagePath;
    //ADDED
    private String filemanagerstring;
    
    private Uri mImageCaptureUri;
    
    String image_url;
    
    public static CampusWallPostInterface act;
    
    Handler mHandler = new Handler(Looper.getMainLooper());
    int wordsLeft;
    
    int thread_type;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campuswallpostthread);
		
		Bundle b = this.getIntent().getExtras();
		school_thread_id = b.getInt("school_thread_id");
		thread_type = b.getInt("thread_type");
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(CampusWallCommentPostComment.this));
		header.setText(null);
		
		tvWordsLeft = (TextView) findViewById(R.id.tvWordsLeft);
		tvWordsLeft.setText(String.valueOf(Profile.post_char_limit));
		tvSchoolName = (TextView) findViewById(R.id.tvSchoolName);
		ivPreview = (ImageView) findViewById(R.id.ivPreview);
		etPostThread = (EditText) findViewById(R.id.etPostThread);
		rbAnonymous = (CheckBox) findViewById(R.id.rbAnonymous);
		if (Profile.post_anonymous){
			rbAnonymous.setChecked(true);
		} else {
			rbAnonymous.setChecked(false);
		}
		
		etPostThread.addTextChangedListener(new TextWatcher() {
			  public void afterTextChanged(Editable s) {
			       //do something
			  }

			  public void beforeTextChanged(CharSequence s, int start, int count, int after){
			       //do something
			  }

			  public void onTextChanged(CharSequence s, int start, int before, int count) { 
				  wordsLeft = Profile.post_char_limit - etPostThread.getText().toString().length();
				  tvWordsLeft.setText(String.valueOf(wordsLeft));
				  
				  if (etPostThread.getText().toString().trim().length() > 0 || postImage){
					  bPostThread.setBackgroundDrawable(getResources().getDrawable(R.drawable.compose_share_button_bg));
					  bPostThread.setClickable(true);
					  //bPostThread.setTextColor(getResources().getColorStateList(R.color.white1));
					  
					  if (wordsLeft < 0){
						  bPostThread.setBackgroundDrawable(getResources().getDrawable(R.drawable.compose_share_button_bg2));
						  bPostThread.setClickable(false);
					  }
				  } else {
					  bPostThread.setBackgroundDrawable(getResources().getDrawable(R.drawable.compose_share_button_bg2));
					  bPostThread.setClickable(false);
					  //bPostThread.setTextColor(getResources().getColorStateList(R.color.dimgrey3));
				  }
			  }
		});
		
		//bPostThreadLocation = (Button) findViewById(R.id.bPostThreadLocation);
		bPostThreadPicture = (Button) findViewById(R.id.bPostThreadPicture);
		bPostThreadPicture.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				getPicture();
			}
		});
		
		tvSchoolName.setText(Profile.getSchoolName());
		
		bPostThread = (TextView) findViewById(R.id.bPostThread);
		bPostThread.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (postImage && image_url == null){
					Toast.makeText(getApplicationContext(),
							getString(R.string.post_image_error), 
							Toast.LENGTH_SHORT).show();
				} else if (etPostThread.getText().toString().trim().length() > 0 || postImage){
					bPostThread.setClickable(false);
					
					//for post with picture
					if(postImage){
						type = 3; 
					} else {
						type = 1; 
					}
					
					if (rbAnonymous.isChecked()){
						is_anonymous = 1;
						Profile.post_anonymous = true;
					} else {
						is_anonymous = 0;
						Profile.post_anonymous = false;
					}
					
					// ---------------------- new api for post ---------------------------------
					RestClient result = null;
					try {
						if (thread_type == 0){
							if (postImage){
								result = new Rest.requestBody().execute(Rest.CAMPUS_COMMENT, Rest.OSESS + Profile.sk, Rest.POST, "5", "comment", etPostThread.getText().toString(), "thread_id", String.valueOf(school_thread_id), "is_anonymous", String.valueOf(is_anonymous), "post_type", String.valueOf(type), "comment_image_url", image_url).get();
							} else {
								result = new Rest.requestBody().execute(Rest.CAMPUS_COMMENT, Rest.OSESS + Profile.sk, Rest.POST, "4", "comment", etPostThread.getText().toString(), "thread_id", String.valueOf(school_thread_id), "is_anonymous", String.valueOf(is_anonymous), "post_type", String.valueOf(type)).get();
							}
						} else if (thread_type == 1){
							if (postImage){
								result = new Rest.requestBody().execute(Rest.GROUP_COMMENT, Rest.OSESS + Profile.sk, Rest.POST, "5", "comment", etPostThread.getText().toString(), "thread_id", String.valueOf(school_thread_id), "is_anonymous", String.valueOf(is_anonymous), "post_type", String.valueOf(type), "comment_image_url", image_url).get();
							} else {
								result = new Rest.requestBody().execute(Rest.GROUP_COMMENT, Rest.OSESS + Profile.sk, Rest.POST, "4", "comment", etPostThread.getText().toString(), "thread_id", String.valueOf(school_thread_id), "is_anonymous", String.valueOf(is_anonymous), "post_type", String.valueOf(type)).get();
							}
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.i("post comment", result.getResponse());
					Log.i("post comment", String.valueOf(result.getResponseCode()));
					
					act.refreshAfterPost();
					onBackPressed();
				} else {
					Toast.makeText(getApplicationContext(),
							getString(R.string.You_can_not_post_a_blank_message), 
							Toast.LENGTH_SHORT).show();
				}
			}
	    }); 
		
	}
	
	//UPDATED
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                int selectedImageWidth = 0;
                int selectedImageHeight = 0;
                
                Bitmap bitmapSizeTest = CacheInternalStorage.decodeCampusWallBitmapFromInputStream(selectedImageUri, CampusWallCommentPostComment.this, 512);
                if (bitmapSizeTest != null) {
                	bPostThread.setClickable(false);
                	
                	selectedImageWidth = bitmapSizeTest.getWidth();
    				selectedImageHeight = bitmapSizeTest.getHeight();
    				selectedImageHeight = 512*selectedImageHeight/selectedImageWidth;
    				selectedImageWidth = 512;
    					
    				Log.i("selectedImageWidth", String.valueOf(selectedImageWidth));
    				Log.i("selectedImageHeight", String.valueOf(selectedImageHeight));
    				
                	Bitmap photo = Bitmap.createScaledBitmap(bitmapSizeTest, selectedImageWidth, selectedImageHeight, false);             
   					
   					String encodedImage = BitMapToString(photo);
   					encodedImage = encodedImage.replace("+", "%2B");
   					//Log.i("encoded bitmap", encodedImage);
   					
   					new uploadImage().execute(SaveToPNG(photo));
   					
   					ivPreview.setVisibility(View.VISIBLE);
   					ivPreview.setImageBitmap(photo); 
   					
   					postImage = true;	
   				} //if cancelled the crop
                else { 
   					postImage = false;
   				}
            }
            
            if (requestCode == CROP_PICTURE) {
                Bundle extras = data.getExtras();  
                if (extras != null) {
                	bPostThread.setClickable(false);
                	
                	Bitmap photo = Bitmap.createScaledBitmap((Bitmap) extras.getParcelable("data"), 512, 512, false);           
   					
   					String encodedImage = BitMapToString(photo);
   					encodedImage = encodedImage.replace("+", "%2B");
   					//Log.i("encoded bitmap", encodedImage);
   					
   					new uploadImage().execute(SaveToPNG(photo));
   					
   					ivPreview.setVisibility(View.VISIBLE);
   					ivPreview.setImageBitmap(photo); 	
   						
   					postImage = true; 		
   				} 
                //if cancelled the crop
                else { 
   					postImage = false;
   				}
            }
            
            if (requestCode == CAMERA_PIC_REQUEST) {
            	int selectedImageWidth = 0;
                int selectedImageHeight = 0;
    	
                if (mImageCaptureUri != null) {
                	bPostThread.setClickable(false);
                	
                	Bitmap bitmapSizeTest = CacheInternalStorage.decodeCampusWallBitmapFromInputStream(mImageCaptureUri, CampusWallCommentPostComment.this, 512);
                	selectedImageWidth = bitmapSizeTest.getWidth();
    				selectedImageHeight = bitmapSizeTest.getHeight();
    				selectedImageHeight = 512*selectedImageHeight/selectedImageWidth;
    				selectedImageWidth = 512;
    				
    				Log.i("selectedImageWidth", String.valueOf(selectedImageWidth));
    				Log.i("selectedImageHeight", String.valueOf(selectedImageHeight));
    				
                	Bitmap photo = Bitmap.createScaledBitmap(bitmapSizeTest, selectedImageWidth, selectedImageHeight, false);       
   					
   					String encodedImage = BitMapToString(photo);
   					encodedImage = encodedImage.replace("+", "%2B");
   					//Log.i("encoded bitmap", encodedImage);
   					
   					new uploadImage().execute(SaveToPNG(photo));
   						
   					ivPreview.setVisibility(View.VISIBLE);
   					ivPreview.setImageBitmap(photo); 
   					
   					postImage = true;	
   				} 
                //if cancelled the crop
                else { 
   					postImage = false;
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
    	ContextWrapper cw = new ContextWrapper(CampusWallCommentPostComment.this);
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
        if(cursor!=null){
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
        startActivityForResult(Intent.createChooser(intent, getString(R.string.Crop_Picture)), CROP_PICTURE);  
    }  
    
    public void getPicture(){
    	final CharSequence[] items = {getString(R.string.Upload_Picture), getString(R.string.Take_Picture), getString(R.string.Cancel)};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(CampusWallCommentPostComment.this);
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
					bPostThreadPicture.setClickable(false);
					AnimationDrawable animationDrawable = (AnimationDrawable) getApplicationContext().getResources().getDrawable(R.drawable.loading);
					bPostThreadPicture.setBackgroundDrawable(animationDrawable);
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
			
			runOnUiThread(new Runnable() {
				public void run() {
					bPostThreadPicture.setClickable(true);
					bPostThreadPicture.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_attach_photo_attached));
					
					bPostThread.setBackgroundDrawable(getResources().getDrawable(R.drawable.compose_share_button_bg));
					bPostThread.setClickable(true);
					//bPostThread.setTextColor(getResources().getColorStateList(R.color.white1));
					
					ivPreview.setOnClickListener(new Button.OnClickListener() {
						public void onClick(View v) {
							Bundle extras = new Bundle();
							extras.putString("image_url", image_url);
							extras.putInt("picWidth", 512);
							Intent i = new Intent(getApplicationContext(), CampusWallImage.class);
							i.putExtras(extras);
							startActivity(i);
						}
				    }); 
				}
			});
			
			return null;
		}
    }
    
}

