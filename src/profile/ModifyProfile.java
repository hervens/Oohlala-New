package profile;

import inbox.Inbox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import profile.OthersProfile.checkFriendRequest;
import profile.UserProfile.loadThumbImage;

import network.RetrieveData;
import rewards.Rewards;
import studentsnearby.Picture;
import user.Profile;
import ManageThreads.TaskQueueImage;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import campusmap.CampusMap;

import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;
import com.gotoohlala.UnreadNumCheck;

import datastorage.Base64;
import datastorage.CacheInternalStorage;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;

public class ModifyProfile extends Activity {
	
	private int mYear, YearNow, YearBirth;
	private int mMonth, MonthNow, MonthBirth;
	private int mDay, DayNow, DayBirth;

	static final int DATE_DIALOG_ID = 0;
	
	TextView tvBirthday, tvRelationship;
	RelativeLayout rlBirthday, rlRelationship, rlThumb;
	int relationship_status = -1;
	ImageView ivThumb;
	
	//YOU CAN EDIT THIS TO WHATEVER YOU WANT
    private static final int SELECT_PICTURE = 1;
    private static final int CROP_PICTURE = 2;
    private static final int CAMERA_PIC_REQUEST = 1337;  
    
    private Uri mImageCaptureUri;
    private String selectedImagePath;
    //ADDED
    private String filemanagerstring;
    private static Handler mHandler = new Handler();
    
    String avatar_url, avatar_thumb_url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modifyprofile);

		//header
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
		
		TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(ModifyProfile.this));

		//content
		final EditText etFirstName = (EditText) findViewById(R.id.etFirstName);
		final EditText etSecondName = (EditText) findViewById(R.id.etSecondName);
		tvBirthday = (TextView) findViewById(R.id.tvBirthday);
		tvRelationship = (TextView) findViewById(R.id.tvRelationship);
		rlBirthday = (RelativeLayout) findViewById(R.id.rlBirthday);
		rlRelationship = (RelativeLayout) findViewById(R.id.rlRelationship);
		ivThumb = (ImageView) findViewById(R.id.ivThumb);
		rlThumb = (RelativeLayout) findViewById(R.id.rlThumb);
		Button bSave = (Button) findViewById(R.id.bSave);
		
		//set up profile picture
		if (Profile.avatar_thumb_url != null && Profile.avatar_thumb_url.contains(".png")){
			TaskQueueImage.addTask(new loadThumbImage(), ModifyProfile.this);
		}
				
		rlThumb.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				getPicture();
			}
		});
		
		String relationship = null;
		switch (Profile.relationStatus){
		case -1:
			relationship = getString(R.string.None);
			break;
		case 0: 
			relationship = getString(R.string.Single);
			break;
		case 1:
			relationship = getString(R.string.In_a_relationship);
			break;
		case 2: 
			relationship = getString(R.string.Complicated);
			break;
		case 3: 
			relationship = getString(R.string.Available);
			break;
		}
		relationship_status = Profile.relationStatus;
		tvRelationship.setText(relationship);
		
		rlRelationship.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	final CharSequence[] items = {getString(R.string.Single), getString(R.string.In_a_relationship), getString(R.string.Complicated), getString(R.string.Available), getString(R.string.None), getString(R.string.Cancel)};
				
				AlertDialog.Builder builder = new AlertDialog.Builder(ModifyProfile.this);
				builder.setTitle(getString(R.string.Relationship_Status));
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	switch(item){
				    	case 0:
				    		relationship_status = 0;
				    		tvRelationship.setText(getString(R.string.Single));
				    		break;
				    	case 1:
				    		relationship_status = 1;
				    		tvRelationship.setText(getString(R.string.In_a_relationship));
				    		break;
				    	case 2:
				    		relationship_status = 2;
				    		tvRelationship.setText(getString(R.string.Complicated));
				    		break;
				    	case 3:
				    		relationship_status = 3;
				    		tvRelationship.setText(getString(R.string.Available));
				    		break;
				    	case 4:
				    		relationship_status = -1;
				    		tvRelationship.setText(getString(R.string.None));
				    		break;
				    	case 5:
				    		break;
				    	}
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
            }
        }); 

		// get the current date
		final Calendar c = Calendar.getInstance();
		YearNow = c.get(Calendar.YEAR);
		MonthNow = c.get(Calendar.MONTH);
		DayNow = c.get(Calendar.DAY_OF_MONTH);
		
		etFirstName.setText(Profile.firstName);
		etSecondName.setText(Profile.lastName);
		
		tvBirthday.setText(Profile.birthdate);
		getIntBirthday();
		
		rlBirthday.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		bSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	String firstName = etFirstName.getText().toString();
            	String lastName = etSecondName.getText().toString();
            	String birthdate = tvBirthday.getText().toString().trim();
            	
            	if (firstName.length() > 60){
					Toast.makeText(getBaseContext(),
							getString(R.string.Firstname_need_to_be_no_more_than_60_characters_long),
							Toast.LENGTH_SHORT).show();
				} else if (lastName.length() > 60){
					Toast.makeText(getBaseContext(),
							getString(R.string.Lastname_need_to_be_no_more_than_60_characters_long),
							Toast.LENGTH_SHORT).show();
				} else if (birthdate.contentEquals("")){
					Toast.makeText(getBaseContext(),
							getString(R.string.Incomplete_Field),
							Toast.LENGTH_SHORT).show();
				} else {
					TaskQueueImage.addTask(new saveProfile(firstName, lastName, birthdate), ModifyProfile.this);
				}
            }
        }); 
	}
	
	class saveProfile extends Thread {
		// This method is called when the thread runs
		String firstName, lastName, birthdate;
		
		public saveProfile(String firstName, String lastName, String birthdate){
			this.firstName = firstName;
			this.lastName = lastName;
			this.birthdate = birthdate;
		}
			
		public void run() {
			RestClient result = null;
			try {
				result = new Rest.requestBody().execute(Rest.USER, Rest.OSESS + Profile.sk, Rest.PUT, "4", "firstname", firstName, "lastname", lastName, "birthdate", birthdate, "relationship_status", String.valueOf(relationship_status)).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("edit profile", result.getResponse());
        	
        	if (result.getResponseCode() == 200){
        		mHandler.post(new Runnable() {
            		public void run() {
		        		Toast.makeText(getBaseContext(),
								getString(R.string.Modify_profile_successfully),
								Toast.LENGTH_SHORT).show();
		        		
		        		UserProfile.updateProfileInfo(firstName, lastName, relationship_status);
            		}
        		});
        	}
		}
	}
	
		// the callback received when the user "sets" the date in the dialog
		private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				if(isDateBefore(view)){
					mYear = year;
					mMonth = monthOfYear;
					mDay = dayOfMonth;
					updateDisplay();
				} else {
					Toast.makeText(getBaseContext(),
							getString(R.string.Pick_Birthday_Error),
							Toast.LENGTH_SHORT).show();
				}
			}
			
			private boolean isDateBefore(DatePicker tempView) {
		        Calendar mCalendar = Calendar.getInstance();
		        mCalendar.set(Calendar.YEAR, YearNow - 13);
		        
		        Calendar tempCalendar = Calendar.getInstance();
		        tempCalendar.set(tempView.getYear(), tempView.getMonth(), tempView.getDayOfMonth(), 0, 0, 0);
		        if(tempCalendar.before(mCalendar))
		            return true;
		        else 
		            return false;
		    }
		};

		@Override
		protected Dialog onCreateDialog(int id) {
			switch (id) {
			case DATE_DIALOG_ID:
				return new DatePickerDialog(this, mDateSetListener, YearBirth, MonthBirth, DayBirth);
			}
			return null;
		}
		
		// updates the date in the TextView
		private void updateDisplay() {
			if (mDay < 10 && mMonth+1 < 10){
				tvBirthday.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mYear).append("-0")
				.append(mMonth + 1).append("-0")
				.append(mDay).append(""));
			} else if (mDay < 10){
				tvBirthday.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mYear).append("-")
				.append(mMonth + 1).append("-0")
				.append(mDay).append(""));
			} else if (mMonth+1 < 10){
				tvBirthday.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mYear).append("-0")
				.append(mMonth + 1).append("-")
				.append(mDay).append(""));
			} else {
				tvBirthday.setText(new StringBuilder()
					// Month is 0 based so add 1
					.append(mYear).append("-")
					.append(mMonth + 1).append("-")
					.append(mDay).append(""));
			}
		}
	
	public void onResume() {
		super.onResume();
		
	}
	
	class loadThumbImage extends Thread {
	    // This method is called when the thread runs
	    public void run() {
			mHandler.post(new Runnable() {
        		public void run() {
        			ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.thumbImageStoreAndLoad(Profile.avatar_thumb_url, ModifyProfile.this), 10));
				}
			});
		}
    }
	
	public void getPicture(){
    	final CharSequence[] items = {getString(R.string.Upload_Picture), getString(R.string.Take_Picture), getString(R.string.Cancel)};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ModifyProfile.this);
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
	
	//UPDATED
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                int selectedImageWidth = 0;
                int selectedImageHeight = 0;
               
				Bitmap bitmapSizeTest = CacheInternalStorage.decodeAvatarBitmapFromInputStream(selectedImageUri, ModifyProfile.this, 90);
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
					ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(photo, 40));  
   				}
            }
            
            if (requestCode == CAMERA_PIC_REQUEST) {
            	/*
            	Bundle extras = data.getExtras();  
                if (extras != null) {
                	final Bitmap photo = Bitmap.createScaledBitmap((Bitmap) extras.getParcelable("data"), 512, 512, false);       
   					
   					String encodedImage = BitMapToString(photo);
   					encodedImage = encodedImage.replace("+", "%2B");
   					//Log.i("encoded bitmap", encodedImage);
   					
   					Hashtable<String, String> params = new Hashtable<String, String>();
   					params.put("image", encodedImage);
   					String result = RetrieveData.requestMethod(RetrieveData.UPLOAD_AVATAR, params);
   					Log.i("update profile picture: ", result);
   					
   					//update profile
   					mHandler.post(new Runnable() {
   	            		public void run() {
					    	Profile.updateUserProfile();
					    	
					    	//reset the avatar imageView
							ivThumb.setImageBitmap(photo);
					    }
	    			});
   				}
                */
                int selectedImageWidth = 0;
                int selectedImageHeight = 0;
    	
                if (mImageCaptureUri != null) {            	
                	Bitmap bitmapSizeTest = CacheInternalStorage.decodeAvatarBitmapFromInputStream(mImageCaptureUri, ModifyProfile.this, 90);
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
   					
   					ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(photo, 40));  						
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
      	ContextWrapper cw = new ContextWrapper(ModifyProfile.this);
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
        
        intent.putExtra("scale", true);
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
  				UserProfile.updateProfileAvatar(avatar_thumb_url);
        	}
				
			return null;
		}
    }
    
    public void getIntBirthday(){
    	if (Profile.birthdate != null){
	    	YearBirth = Integer.valueOf(Profile.birthdate.substring(0, 4));
	    	MonthBirth = Integer.valueOf(Profile.birthdate.substring(5, 7)) - 1;
	    	DayBirth = Integer.valueOf(Profile.birthdate.substring(8));
    	}
    }
	
}
