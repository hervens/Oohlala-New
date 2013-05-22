package launchOohlala;

import inbox.Inbox;
import inbox.InboxArrayAdapter;
import inbox.InboxModel;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import network.ErrorCodeParser;
import network.RetrieveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gotoohlala.R;

import profile.BlockListArrayAdapter;
import profile.ProfileSettings;

import smackXMPP.XMPPClient;
import user.Profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.Keyboard;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import datastorage.CacheInternalStorage;
import datastorage.ConvertDpsToPixels;
import datastorage.Fonts;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.UserLoginInfo;

public class CheckEmail extends Activity {
	
	RelativeLayout bCreateAccount, bLogin, bg;
	private int code;
	String email, registered_email;
	LinearLayout bottom;
	
	ViewPager viewPager;  
    ArrayList<View> list;  
    ViewGroup main, group;  
    ImageView imageView;  
    ImageView[] imageViews; 
    FrameLayout flPage;
    
    ListView lvMultiSchools;
    List<CheckEmailMultiSchoolsModel> Schoolslist = new ArrayList<CheckEmailMultiSchoolsModel>();
    
    boolean onResumeable = false;
    
    private Handler mHandler = new Handler();
    
    BroadcastReceiver brLogout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkemail);
		
		if (this.getIntent().getExtras() != null){
			Bundle b = this.getIntent().getExtras();
			registered_email = b.getString("registered_email");
		}
		
		flPage = (FrameLayout) findViewById(R.id.flPage);
		bottom = (LinearLayout) findViewById(R.id.bottom);
		bCreateAccount = (RelativeLayout) findViewById(R.id.bCreateAccount);
		bLogin = (RelativeLayout) findViewById(R.id.bLogin);
		lvMultiSchools = (ListView) findViewById(R.id.lvMultiSchools);
		
		if (registered_email != null){
			//etCheckEmail.setText(registered_email);
		}
	    
	    LayoutInflater inflater = getLayoutInflater();  
        list = new ArrayList<View>(); 
        list.add(inflater.inflate(R.layout.logintour0, null));  
        list.add(inflater.inflate(R.layout.logintour1, null));  
        list.add(inflater.inflate(R.layout.logintour2, null));  
        list.add(inflater.inflate(R.layout.logintour3, null));  
        list.add(inflater.inflate(R.layout.logintour4, null)); 
        
        imageViews = new ImageView[list.size()];  
        // group是R.layou.main中的负责包裹小圆点的LinearLayout.  
        ViewGroup group = (ViewGroup) this.findViewById(R.id.viewGroup);  
        group.setVisibility(View.GONE);
  
        viewPager = (ViewPager) this.findViewById(R.id.viewPager);  
  
        for (int i = 0; i < list.size(); i++) { 
            imageView = new ImageView(CheckEmail.this);  
            LayoutParams lp = new LayoutParams(10, 10);
            lp.setMargins(8, 0, 8, 0);
            imageView.setLayoutParams(lp);  
            imageView.setPadding(10, 0, 10, 0);  
            imageViews[i] = imageView;  
            if (i == 0) {  
                // 默认进入程序后第一张图片被选中;  
                imageViews[i].setBackgroundResource(R.drawable.ui_page_active);  
            } else {  
                imageViews[i].setBackgroundResource(R.drawable.ui_page_inactive);  
            }  
            group.addView(imageView);  
        }  
  
        viewPager.setAdapter(new MyAdapter());  
        viewPager.setOnPageChangeListener(new MyListener());  
        
		//check if the userLogin is cached
		if (CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()) != null){
			if (CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()).userAccount.trim().length() > 0){
				//new CheckEmailCache().execute();
			}
		}
		
		/*
		bCheckEmail.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				email = etCheckEmail.getText().toString().toLowerCase().trim();
				
				Profile.setSk("");
				CacheInternalStorage.cacheUserLoginInfo(new UserLoginInfo("", "", false), CheckEmail.this); //cache an empty userLogin info
				
				CheckEmailButton();	
			}
	    }); 
		*/
		
		bCreateAccount.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), SearchSchool.class);
				startActivity(i);
			}
		});
		
		bLogin.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), Login.class);
				startActivity(i);
			}
		});
		
		bg = (RelativeLayout) findViewById(R.id.bg);
		bg.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		    public void onGlobalLayout() {
		        int heightDiff = bg.getRootView().getHeight() - bg.getHeight();
		        
		        if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
		        	RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
		        			RelativeLayout.LayoutParams.FILL_PARENT,
		        			RelativeLayout.LayoutParams.FILL_PARENT);
		        	param.addRule(RelativeLayout.ABOVE, bottom.getId());
		        	
		        	//llHeader.setLayoutParams(param);
		        	
		        	RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(
			        		RelativeLayout.LayoutParams.FILL_PARENT,
			        		RelativeLayout.LayoutParams.FILL_PARENT);
			        param2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			        	
			        bottom.setLayoutParams(param2); 
		        	
		        	lvMultiSchools.setVisibility(View.GONE);
		        } else {
		        	RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
		        			RelativeLayout.LayoutParams.FILL_PARENT,
		        			ConvertDpsToPixels.getPixels(120, CheckEmail.this));
		        	
		        	//llHeader.setLayoutParams(param);
		        	
		        	if (!Schoolslist.isEmpty()){
		        		RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(
			        			RelativeLayout.LayoutParams.FILL_PARENT,
			        			RelativeLayout.LayoutParams.FILL_PARENT);
			        	param2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			        	
			        	bottom.setLayoutParams(param2);
			        	
			        	lvMultiSchools.setVisibility(View.VISIBLE);
		        	} else {
		        		RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(
				        		RelativeLayout.LayoutParams.FILL_PARENT,
				        		ConvertDpsToPixels.getPixels(180, CheckEmail.this));
				        param2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				        	
				        bottom.setLayoutParams(param2); 
		        		
		        		lvMultiSchools.setVisibility(View.GONE);
		        	}
		        }
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
	
	public boolean checkEmailValidation(String email){
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher m = p.matcher(email);
		return m.matches();
	}
	
	class MyAdapter extends PagerAdapter {  
		  
        public int getCount() {  
            return list.size();  
        }  
  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0 == arg1;  
        }  
  
        public int getItemPosition(Object object) {  
            // TODO Auto-generated method stub  
            return super.getItemPosition(object);  
        }  
  
        public void destroyItem(View arg0, int arg1, Object arg2) {  
            // TODO Auto-generated method stub  
            ((ViewPager) arg0).removeView(list.get(arg1));  
        }  
  
        public Object instantiateItem(View arg0, int arg1) {  
            // TODO Auto-generated method stub  
            ((ViewPager) arg0).addView(list.get(arg1));  
            return list.get(arg1);  
        }  
  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
            // TODO Auto-generated method stub  
  
        }  
  
        public Parcelable saveState() {  
            // TODO Auto-generated method stub  
            return null;  
        }  
  
        public void startUpdate(View arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        public void finishUpdate(View arg0) {  
            // TODO Auto-generated method stub  
  
        }  
    }  
  
    class MyListener implements OnPageChangeListener {  
  
        public void onPageScrollStateChanged(int arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
            // TODO Auto-generated method stub  
  
        }  
  
        public void onPageSelected(int arg0) {  
            for (int i = 0; i < imageViews.length; i++) {  
                imageViews[arg0]  
                        .setBackgroundResource(R.drawable.ui_page_active);  
                if (arg0 != i) {  
                    imageViews[i]  
                            .setBackgroundResource(R.drawable.ui_page_inactive);  
                }  
            }  
  
        }  
  
    }  
	
	private void showAlertDialog(final String email) {
		// TODO Auto-generated method stub
    	AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle(getString(R.string.School_Not_Found));
		alert.setMessage(email + getString(R.string.is_not_a_school_email_we_currently_support_would_you_like_to_use_OOHLALA_at_your_school));
		alert.setButton(getString(R.string.No), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		alert.setButton2(getString(R.string.Yes), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Bundle extras = new Bundle();
				extras.putString("email", email);
				//Log.i ("email: ", email);
				Intent i = new Intent(getApplicationContext(), NoSchoolEmail.class);
				i.putExtras(extras);
				startActivity(i);
			}
		});
		alert.show();
	}
	
	private class CheckEmailCache extends AsyncTask<Void, Void, Void> {
		
	     protected Void doInBackground(Void... param) {
	    	 	RestClient result = null;
		 		try {
		 			result = new Rest.request().execute(Rest.SCHOOL + "?email=" + CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()).userAccount, Rest.OTOKE + Rest.accessCode2, Rest.GET).get();
		 		} catch (InterruptedException e1) {
		 			// TODO Auto-generated catch block
		 			e1.printStackTrace();
		 		} catch (ExecutionException e1) {
		 			// TODO Auto-generated catch block
		 			e1.printStackTrace();
		 		}
	 		
		 		JSONArray schools = null;
		 		JSONObject user_info = null;
		 		int user_id = -1;
		 		try {
		 			schools = (new JSONObject(result.getResponse())).getJSONArray("schools");
		 			user_info = (new JSONObject(result.getResponse())).getJSONObject("user_info");
		 			user_id = (new JSONObject(result.getResponse())).getJSONObject("user_info").getInt("user_id");
		 		} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
	    	 	if (user_id != -1) {
					boolean facebook_Connected = false;
					String user_fb_token = null;
					String avatar = null;
					try {
						facebook_Connected = user_info.getBoolean("is_facebook");
						user_fb_token = user_info.getString("fb_token");
						avatar = user_info.getString("avatar_thumb_url");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
							
					Bundle extras = new Bundle();
					extras.putString("email", CacheInternalStorage.getCacheUserLoginInfo(getApplicationContext()).userAccount);
					extras.putBoolean("facebook_Connected", facebook_Connected);
					extras.putString("user_fb_token", user_fb_token);
					extras.putString("avatar", avatar);
					extras.putBoolean("overrideCache", false);
					Intent i = new Intent(getApplicationContext(), Login.class);
					i.putExtras(extras);
					startActivity(i);
				}
		
	    	 	return null;
	     }
	}
	
	public void CheckEmailButton() {
			if(!checkEmailValidation(email)){
					Toast.makeText(getBaseContext(),
							getString(R.string.Invalid_Email_Address_Format),
							Toast.LENGTH_SHORT).show();
			} else {
	    	 	RestClient result = null;
		 		try {
		 			result = new Rest.request().execute(Rest.SCHOOL + "?email=" + email, Rest.OTOKE + Rest.accessCode2, Rest.GET).get();
		 		} catch (InterruptedException e1) {
		 			// TODO Auto-generated catch block
		 			e1.printStackTrace();
		 		} catch (ExecutionException e1) {
		 			// TODO Auto-generated catch block
		 			e1.printStackTrace();
		 		}
		 		
		 		JSONArray schools = null;
		 		JSONObject user_info = null;
		 		int user_id = -1;
		 		try {
		 			schools = (new JSONObject(result.getResponse())).getJSONArray("schools");
		 			user_info = (new JSONObject(result.getResponse())).getJSONObject("user_info");
		 			user_id = (new JSONObject(result.getResponse())).getJSONObject("user_info").getInt("user_id");
		 		} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
	    	 	if (user_id != -1) {
	    	 		boolean facebook_Connected = false;
					String user_fb_token = null;
					String avatar = null;
					try {
						facebook_Connected = user_info.getBoolean("is_facebook");
						user_fb_token = user_info.getString("fb_token");
						avatar = user_info.getString("avatar_thumb_url");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
								
					Bundle extras = new Bundle();
					extras.putString("email", email);
					extras.putBoolean("facebook_Connected", facebook_Connected);
					extras.putString("user_fb_token", user_fb_token);
					extras.putString("avatar", avatar);
					extras.putBoolean("overrideCache", true);
					Intent i = new Intent(getApplicationContext(), Login.class);
					i.putExtras(extras);
					startActivity(i);
								
				} else if (schools != null && schools.length() == 0) {
					Toast.makeText(getBaseContext(),
							getString(R.string.No_School_Email_Found),
							Toast.LENGTH_SHORT).show();
								
					//ask to type your school so that you can just use whatever email for that school
					showAlertDialog(email);
				} else if (user_id == -1){
					if (schools != null){
							Log.i("school list: ", schools.toString());
							if (schools.length() == 1){
								lvMultiSchools.setVisibility(View.GONE);
											
								Schoolslist = new ArrayList<CheckEmailMultiSchoolsModel>();
								//Log.i("school list: ", schools.toString());
								String schoolname = null;
								int schoolid = 0;
								try {
									schoolname = schools.getJSONObject(0).getString("name");
									schoolid = schools.getJSONObject(0).getInt("id");
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								if (schoolname != null){
									Bundle extras = new Bundle();
									extras.putString("school_name", schoolname);
									extras.putInt("school_id", schoolid);
									extras.putString("email", email);
											
									Intent i = new Intent(getApplicationContext(), Register.class);
									i.putExtras(extras);
									startActivity(i);
								}
							} else if (schools.length() > 1){
								lvMultiSchools.setVisibility(View.VISIBLE);
											
								Schoolslist = new ArrayList<CheckEmailMultiSchoolsModel>();
								//Log.i("school list: ", schools.toString());
								for (int i = 0; i < schools.length(); i++){
									String schoolname = null;
									int schoolid = 0;
									try {
										schoolname = schools.getJSONObject(i).getString("name");
										schoolid = schools.getJSONObject(i).getInt("id");
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									if (schoolname != null){
										Schoolslist.add(new CheckEmailMultiSchoolsModel(schoolname, schoolid));
									}
								}
											
								CheckEmailMultiSchoolsArrayAdapter adapter = new CheckEmailMultiSchoolsArrayAdapter(CheckEmail.this, CheckEmail.this, Schoolslist);
								lvMultiSchools.setAdapter(adapter);
								lvMultiSchools.setOnItemClickListener(new OnItemClickListener() {
									public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
											Bundle extras = new Bundle();
											extras.putString("school_name", Schoolslist.get(position).schoolname);
											extras.putInt("school_id", Schoolslist.get(position).schoolid);
											extras.putString("email", email);
														
											Intent i = new Intent(getApplicationContext(), Register.class);
											i.putExtras(extras);
											startActivity(i);
									}
								});
											
								InputMethodManager imm = (InputMethodManager) CheckEmail.this.getSystemService(Context.INPUT_METHOD_SERVICE);
							    imm.hideSoftInputFromWindow(bCreateAccount.getWindowToken(), 0);
											
							} else {
								Toast.makeText(getBaseContext(),
										getString(R.string.No_School_Email_Found),
										Toast.LENGTH_SHORT).show();
											
								//ask to type your school so that you can just use whatever email for that school
								showAlertDialog(email);
							}	
					} else {
						Toast.makeText(getBaseContext(),
								getString(R.string.No_School_Email_Found),
								Toast.LENGTH_SHORT).show();
									
						//ask to type your school so that you can just use whatever email for that school
						showAlertDialog(email);
					}
				}
			} 
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (onResumeable){
			bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.checkemailbg));
			
			LayoutInflater inflater = getLayoutInflater();  
			list.add(inflater.inflate(R.layout.logintour0, null)); 
			list.add(inflater.inflate(R.layout.logintour1, null));  
	        list.add(inflater.inflate(R.layout.logintour2, null));  
	        list.add(inflater.inflate(R.layout.logintour3, null)); 
	        list.add(inflater.inflate(R.layout.logintour4, null)); 
	        
	        viewPager.setAdapter(new MyAdapter());  
		}
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		//bottom.setBackgroundDrawable(null);
		//bCheckEmail.setBackgroundDrawable(null);
		//llHeader.setBackgroundDrawable(null);
		bg.setBackgroundDrawable(null);
		
		viewPager.setAdapter(null);  
		list.clear();
		Schoolslist.clear();
		onResumeable = true;
	}
	
}
