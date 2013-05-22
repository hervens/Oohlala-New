package campuswall;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import launchOohlala.CheckEmail;

import network.ErrorCodeParser;
import network.RetrieveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.OthersProfile;
import smackXMPP.XMPPClient;
import studentsnearby.studentsNearby;
import studentsnearby.studentsNearbyModel;
import user.Profile;
import ManageThreads.TaskQueue;
import ManageThreads.TaskQueueImage;
import ManageThreads.TaskQueueImage2;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import campusgame.CampusGameType3;
import campuswall.CampusWallComment.CampusWallCommentArrayAdapter.ViewHolder;

import com.gotoohlala.OohlalaMain;
import com.gotoohlala.R;
import com.gotoohlala.TabInterface;
import com.gotoohlala.TopMenuNavbar;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import datastorage.CacheInternalStorage;
import datastorage.ConvertDpsToPixels;
import datastorage.CpuUsage;
import datastorage.DeviceDimensions;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.NinePatch;
import datastorage.Rest;
import datastorage.UserFirstLogin;
import datastorage.Rest.request;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.TimeCounter;

public class CampusWall extends FrameLayout implements CampusWallPostInterface, TabInterface {

	Button bInvite, bScrollToTop, bPostThread, bStudents;
	List<CampusWallModel> list = new ArrayList<CampusWallModel>();
	List<SocialGroupModel> list_SocialGroup = new ArrayList<SocialGroupModel>();
	ListView listView, lvSocialGroups;
	PullToRefreshListView mPullRefreshListView;
	CampusWallArrayAdapter adapter;
	SocialGroupAdapter adapter_SocialGroup;
	int cut = 0;
	int number = 0;
	boolean lastcut = false;
	
	ProgressBar pbPeopleToUnlock;
	TextView tvPeopleToUnlock, tvStudents, headerCampusWall, filterHeader, filterRow1, filterRow2, filterFooter;
	
	LinearLayout llLoading;
	RelativeLayout rlFilter, rlStudents, rlHeader, rlUserSearch, rlInAppTour;
	
	boolean refresh = false;
	
	loadBitmapThread loadBitmapThread = new loadBitmapThread();
	loadBitmapThreadAgain loadBitmapThreadAgain = new loadBitmapThreadAgain();
	loadThumbBitmapThread loadThumbBitmapThread = new loadThumbBitmapThread();
	loadThumbBitmapThreadAgain loadThumbBitmapThreadAgain = new loadThumbBitmapThreadAgain();
	freeBitmapThread freeBitmapThread = new freeBitmapThread();
	
	Handler mHandler = new Handler(Looper.getMainLooper());
	
	Thread cacheBitmap, cacheThumbBitmap;
	
	View v = null;
	
	Drawable imageBg;
	Boolean filter = false;
	
	double myLat, myLongi;
	
	ImageView ivStudentsThumb1, ivStudentsThumb2, ivStudentsThumb3, ivStudentsThumb4, ivStudentsThumb5;
	
	Bitmap[] studentsImage = {null, null, null, null, null};
	
	boolean tabloaded = false;
	
	RelativeLayout rlBigImage;
	ImageView ivBigImage;
	ProgressBar pbImageLoading;
	TextView tvWords;
	
	int group_id = 0;
	String group_name = "";
	boolean allow_thread_user_search = false;
	
	EditText etSearchUserName;
	
	String userName = null;
	
	Button bToggleLeft, bToggleRight;
	boolean most_liked = false;
	boolean currentView = false;
	
	int picWidth;
	
	RelativeLayout rllockedbox;
	
	public TopMenuNavbar TopMenuNavbar;
	
	public CampusWall(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		if (Profile.has_update){
			RetrieveData.showUpdateDialog = true;
		}
		
		if (!OohlalaMain.campusWallUnlockedCheck){
			// -------------------------------------------------------------------
			RestClient result = null;
			try {
				result = new Rest.request().execute(Rest.SCHOOL + Profile.schoolId, Rest.OTOKE + Rest.accessCode2, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				if ((new JSONObject(result.getResponse())).has("wall_unlock_num")){
					OohlalaMain.target_student_count = (new JSONObject(result.getResponse())).getInt("wall_unlock_num");
				}
				if ((new JSONObject(result.getResponse())).has("user_num")){
					OohlalaMain.current_student_count = (new JSONObject(result.getResponse())).getInt("user_num");
				}
				OohlalaMain.campusWallUnlocked = (new JSONObject(result.getResponse())).getBoolean("wall_unlocked");
				if ((new JSONObject(result.getResponse())).has("latitude")){
					Profile.setSchoolLatitude((new JSONObject(result.getResponse())).getDouble("latitude"));
				}
				if ((new JSONObject(result.getResponse())).has("longitude")){
					Profile.setSchoolLongitude((new JSONObject(result.getResponse())).getDouble("longitude"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Log.i("campus wall user radio", String.valueOf(OohlalaMain.current_student_count) + "/" + String.valueOf(OohlalaMain.target_student_count));
			
			OohlalaMain.campusWallUnlockedCheck = true;
		}
		
		 final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
		 v = (RelativeLayout) mLayoutInflater.inflate(R.layout.campuswall, null);  
	     addView(v);  
	     
	     TopMenuNavbar = (TopMenuNavbar) v.findViewById(R.id.bTopMenuNavBar);
	     
			View headerView = View.inflate(getContext(), R.layout.campuswallheaderview, null);
			
			picWidth = DeviceDimensions.getWidth(getContext());
			
			currentView = true;
			
			pbPeopleToUnlock = (ProgressBar) v.findViewById(R.id.pbPeopleToUnlock);
			tvPeopleToUnlock = (TextView) v.findViewById(R.id.tvPeopleToUnlock);
			bInvite = (Button) v.findViewById(R.id.bInvite);
			rllockedbox = (RelativeLayout) v.findViewById(R.id.rllockedbox);
			
			pbPeopleToUnlock.setMax(OohlalaMain.target_student_count);
			pbPeopleToUnlock.setProgress(OohlalaMain.current_student_count);
			tvPeopleToUnlock.setText(String.valueOf(OohlalaMain.current_student_count) + "/" + String.valueOf(OohlalaMain.target_student_count) + getContext().getString(R.string._students_to_unlock));
			
			rllockedbox.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(v.getContext(),
							getContext().getString(R.string.Campus_wall_will_be_unlocked_when_more_than_25_students_registered_in_this_campus_please_click_the_invite_button_to_invite_your_friends_to_join), Toast.LENGTH_SHORT)
							.show();
				}
		    }); 
			
			bInvite.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(v.getContext(), CampusWallInvite.class);
					getContext().startActivity(i);
				}
		    }); 

			bToggleLeft = (Button) headerView.findViewById(R.id.bToggleLeft);
			bToggleLeft.setTypeface(Fonts.getOpenSansBold(v.getContext()));
			bToggleLeft.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					most_liked = false;
					bToggleLeft.setBackgroundResource(R.drawable.toggle_left2);
					bToggleLeft.setClickable(false);
					bToggleRight.setBackgroundResource(R.drawable.toggle_right);
					bToggleRight.setClickable(true);
					
					refreshAfterPost();
				}
			});
			
			bToggleRight = (Button) headerView.findViewById(R.id.bToggleRight);
			bToggleRight.setTypeface(Fonts.getOpenSansBold(v.getContext()));
			bToggleRight.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					most_liked = true;
					bToggleLeft.setBackgroundResource(R.drawable.toggle_left);
					bToggleLeft.setClickable(true);
					bToggleRight.setBackgroundResource(R.drawable.toggle_right2);
					bToggleRight.setClickable(false);
					
					refreshAfterPost();
				}
			});
			
			if (most_liked){
				bToggleLeft.setBackgroundResource(R.drawable.toggle_left);
				bToggleLeft.setClickable(true);
				bToggleRight.setBackgroundResource(R.drawable.toggle_right2);
				bToggleRight.setClickable(false);
			} else {
				bToggleLeft.setBackgroundResource(R.drawable.toggle_left2);
				bToggleLeft.setClickable(false);
				bToggleRight.setBackgroundResource(R.drawable.toggle_right);
				bToggleRight.setClickable(true);
			}
			
			rlStudents = (RelativeLayout) headerView.findViewById(R.id.rlStudents);
			rlUserSearch = (RelativeLayout) headerView.findViewById(R.id.rlUserSearch);
			
			etSearchUserName = (EditText) headerView.findViewById(R.id.etSearchUserName);
			etSearchUserName.setOnKeyListener(new EditText.OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					// TODO Auto-generated method stub
					if (keyCode == KeyEvent.KEYCODE_ENTER){
						userName = etSearchUserName.getText().toString().replace(" ", "%20");
						if (userName.length() >= 3){
							reloadView();
						} else {
							Toast.makeText(v.getContext(), getContext().getString(R.string.search_name_dialog_alert), Toast.LENGTH_SHORT).show();
						}	
						
						InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			    		imm.hideSoftInputFromWindow(etSearchUserName.getWindowToken(), 0);
			    		etSearchUserName.clearFocus();
					}
					return false;				
				}
			});
			
			lvSocialGroups = (ListView) v.findViewById(R.id.lvSocialGroups);
			adapter_SocialGroup = new SocialGroupAdapter(v.getContext(), list_SocialGroup);
			lvSocialGroups.setAdapter(adapter_SocialGroup);
			lvSocialGroups.setOnItemClickListener(new OnItemClickListener() {
				 public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
						headerCampusWall.setText(list_SocialGroup.get(position).name.toUpperCase());
						filterHeader.setText(list_SocialGroup.get(position).name.toUpperCase());
						rlFilter.setVisibility(View.GONE);	
						OohlalaMain.CampusWall_thread_type = 1;
						group_id = list_SocialGroup.get(position).id;
						group_name = list_SocialGroup.get(position).name;
						allow_thread_user_search = list_SocialGroup.get(position).allow_thread_user_search;
						allowUserSearch(allow_thread_user_search);
						refreshAfterPost();
						
						filterHeader.setBackgroundDrawable(null);
						filterRow1.setBackgroundDrawable(null);
						filterRow2.setBackgroundDrawable(null);
						filterFooter.setBackgroundDrawable(null);
						
						if (!OohlalaMain.campusWallUnlocked){
							mPullRefreshListView.setVisibility(View.VISIBLE);
							rllockedbox.setVisibility(View.GONE);
							bInvite.setVisibility(View.GONE);
						}
				 }
			});
			
			llLoading = (LinearLayout) v.findViewById(R.id.llLoading);
			
			rlFilter = (RelativeLayout) v.findViewById(R.id.rlFilter);
			
			filterHeader = (TextView) v.findViewById(R.id.filterHeader);
			filterHeader.setTypeface(Fonts.getOpenSansBold(v.getContext()));
			filterHeader.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					rlFilter.setVisibility(View.GONE);	
					
					filterHeader.setBackgroundDrawable(null);
					filterRow1.setBackgroundDrawable(null);
					filterRow2.setBackgroundDrawable(null);
					filterFooter.setBackgroundDrawable(null);
				}
		    }); 
			
			filterRow1 = (TextView) v.findViewById(R.id.filterRow1);
			filterRow1.setTypeface(Fonts.getOpenSansBold(v.getContext()));
			filterRow1.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					headerCampusWall.setText(getContext().getString(R.string.GLOBAL));
					filterHeader.setText(getContext().getString(R.string.GLOBAL));
					rlFilter.setVisibility(View.GONE);	
					OohlalaMain.CampusWall_is_global = true;
					OohlalaMain.CampusWall_thread_type = 0;
					refreshAfterPost();
					allow_thread_user_search = false;
					allowUserSearch(allow_thread_user_search);
					
					filterHeader.setBackgroundDrawable(null);
					filterRow1.setBackgroundDrawable(null);
					filterRow2.setBackgroundDrawable(null);
					filterFooter.setBackgroundDrawable(null);
					
					if (!OohlalaMain.campusWallUnlocked){
						mPullRefreshListView.setVisibility(View.VISIBLE);
						rllockedbox.setVisibility(View.GONE);
						bInvite.setVisibility(View.GONE);
					}
				}
		    }); 
			
			filterRow2 = (TextView) v.findViewById(R.id.filterRow2);
			filterRow2.setTypeface(Fonts.getOpenSansBold(v.getContext()));
			filterRow2.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					headerCampusWall.setText(getContext().getString(R.string.CAMPUS_WALL));
					filterHeader.setText(getContext().getString(R.string.CAMPUS_WALL));
					rlFilter.setVisibility(View.GONE);	
					OohlalaMain.CampusWall_is_global = false;
					OohlalaMain.CampusWall_thread_type = 0;
					if (OohlalaMain.campusWallUnlocked){
						refreshAfterPost();
					}
					allow_thread_user_search = false;
					allowUserSearch(allow_thread_user_search);
					
					filterHeader.setBackgroundDrawable(null);
					filterRow1.setBackgroundDrawable(null);
					filterRow2.setBackgroundDrawable(null);
					filterFooter.setBackgroundDrawable(null);
					
					if (!OohlalaMain.campusWallUnlocked){
						mPullRefreshListView.setVisibility(View.GONE);
						rllockedbox.setVisibility(View.VISIBLE);
						bInvite.setVisibility(View.VISIBLE);
					}
				}
		    }); 
			
			filterFooter = (TextView) v.findViewById(R.id.filterFooter);
			filterFooter.setTypeface(Fonts.getOpenSansBold(v.getContext()));
			filterFooter.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					rlFilter.setVisibility(View.GONE);	
					
					filterHeader.setBackgroundDrawable(null);
					filterRow1.setBackgroundDrawable(null);
					filterRow2.setBackgroundDrawable(null);
					filterFooter.setBackgroundDrawable(null);
				}
		    }); 
			
			headerCampusWall = (TextView) v.findViewById(R.id.headerCampusWall);
			headerCampusWall.setTypeface(Fonts.getOpenSansBold(v.getContext()));
			/*
			headerCampusWall.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					rlFilter.setVisibility(View.VISIBLE);	
					
					filterHeader.setBackgroundResource(R.drawable.filter_header);
					filterRow1.setBackgroundResource(R.drawable.filter_row);
					filterRow2.setBackgroundResource(R.drawable.filter_row);
					filterFooter.setBackgroundResource(R.drawable.filter_footer);
				}
		    }); 
			*/
			
			bPostThread = (Button) v.findViewById(R.id.bPostThread);
			bPostThread.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CampusWallPostThread.act = CampusWall.this;
					Bundle extras = new Bundle();
					extras.putBoolean("is_global", OohlalaMain.CampusWall_is_global);
					extras.putInt("thread_type", OohlalaMain.CampusWall_thread_type);
					extras.putInt("group_id", group_id);
					extras.putString("group_name", group_name);
					Intent i = new Intent(getContext(), CampusWallPostThread.class);
					i.putExtras(extras);
					getContext().startActivity(i);
				}
		    }); 
			
			rlHeader = (RelativeLayout) v.findViewById(R.id.rlHeader);
			
			if (CacheInternalStorage.getCacheUserLocation(v.getContext()) != null){
	      		if (CacheInternalStorage.getCacheUserLocation(v.getContext()).lat != 0){
	      			myLat = CacheInternalStorage.getCacheUserLocation(v.getContext()).lat;
					myLongi = CacheInternalStorage.getCacheUserLocation(v.getContext()).longi;
	      		}
			}
			
			mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.lvCampusWall);
			mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
				public void onRefresh() {
					mPullRefreshListView.setLastUpdatedLabel(getContext().getString(R.string.Last_Updated) + TimeCounter.getFreshUpdateTime());
					//mPullRefreshListView.setShowViewWhileRefreshing(false);					
					reloadView();
					System.gc();
				}
			});
			if (!tabloaded){
				mPullRefreshListView.setRefreshing();
				tabloaded = true;
			}
			
			listView = mPullRefreshListView.getRefreshableView();
			listView.addHeaderView(headerView);
			listView.setScrollingCacheEnabled(false);
			adapter = new CampusWallArrayAdapter(v.getContext(), list);
			listView.setAdapter(adapter);
			//listView.setSmoothScrollbarEnabled(false);
			
			bScrollToTop = (Button) v.findViewById(R.id.bScrollToTop);
			bScrollToTop.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					listView.setSelectionFromTop(0, 0);
				}
		    }); 
			
			if (OohlalaMain.CampusWall_thread_type == 0){
				if (OohlalaMain.CampusWall_is_global){
					headerCampusWall.setText(getContext().getString(R.string.GLOBAL));
					filterHeader.setText(getContext().getString(R.string.GLOBAL));
					
					if (!OohlalaMain.campusWallUnlocked){
						mPullRefreshListView.setVisibility(View.VISIBLE);
						rllockedbox.setVisibility(View.GONE);
						bInvite.setVisibility(View.GONE);
					}
				} else {
					headerCampusWall.setText(getContext().getString(R.string.CAMPUS_WALL));
					filterHeader.setText(getContext().getString(R.string.CAMPUS_WALL));
					
					if (!OohlalaMain.campusWallUnlocked){
						mPullRefreshListView.setVisibility(View.GONE);
						rllockedbox.setVisibility(View.VISIBLE);
						bInvite.setVisibility(View.VISIBLE);
					}
				}
			} else if (OohlalaMain.CampusWall_thread_type == 1){
				headerCampusWall.setText(group_name.toUpperCase());
				filterHeader.setText(group_name.toUpperCase());
			}
			
			listView.setOnScrollListener(new OnScrollListener(){
				
				public int first, last;
				
			    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			        // TODO Auto-generated method stub
			    	this.first = firstVisibleItem;
			    	this.last = firstVisibleItem + visibleItemCount - 1;
			    	
			    	if (first > 0){
			    		//bScrollToTop.setVisibility(View.VISIBLE);
			    	} else {
			    		//bScrollToTop.setVisibility(View.GONE);
			    	}
			    }
			 	
			    public void onScrollStateChanged(AbsListView view, int scrollState) {
			    	// TODO Auto-generated method stub
				   	if(scrollState == 0){
				   		if (last <= list.size()){
					   		freeBitmapThread.cancel(true);
					   		freeBitmapThread = new freeBitmapThread();
					   		if (first > 0){
					   			freeBitmapThread.execute(first - 1, last);
					   		} else if (first == 0){
					   			freeBitmapThread.execute(0, last);
					    	}
					   		
					   		if (CpuUsage.getUsedMemorySize() < CpuUsage.MAX_USED_MEMORY_SIZE){
						   		//reload the bitmap and thumbBitmap as I scolling the list
					   			loadBitmapThreadAgain.cancel(true);
						   		loadBitmapThreadAgain = new loadBitmapThreadAgain();
						   		if (first > 0){
						   			loadBitmapThreadAgain.execute(first - 1, last);
						   		} else if (first == 0){
						   			loadBitmapThreadAgain.execute(0, last);
						    	}
						   		
						   		loadThumbBitmapThreadAgain.cancel(true);
						   		loadThumbBitmapThreadAgain = new loadThumbBitmapThreadAgain();
						   		if (first > 0){
						   			loadThumbBitmapThreadAgain.execute(first - 1, last);
						   		} else if (first == 0){
						   			loadThumbBitmapThreadAgain.execute(0, last);
						   		}	
					   		} else {
					   			System.gc();
					   		}
				    	}
				   		
				   		if (last >= number && !lastcut){
				   			llLoading.setVisibility(View.VISIBLE);
					    	
				    		cut++;
				    		TaskQueueImage.addTask(new updateListThread(cut), v.getContext());
				    	} 
				    }
				   	
				   	if(scrollState == 1){
					   	if (last <= list.size()){
					   		/*
					   		freeBitmapThread.cancel(true);
					   		freeBitmapThread = new freeBitmapThread();
					   		if (first > 0){
					   			freeBitmapThread.execute(first - 1, last);
					   		} else if (first == 0){
					   			freeBitmapThread.execute(0, last);
					    	}
					   		*/
					   		
					   		if (CpuUsage.getUsedMemorySize() < CpuUsage.MAX_USED_MEMORY_SIZE){
						   		//reload the bitmap and thumbBitmap as I scolling the list
					   			/*
					   			loadBitmapThreadAgain.cancel(true);
						   		loadBitmapThreadAgain = new loadBitmapThreadAgain();
						   		if (first > 0){
						   			loadBitmapThreadAgain.execute(first - 1, last);
						   		} else if (first == 0){
						   			loadBitmapThreadAgain.execute(0, last);
						    	}
						   		*/
					   			
						   		/*
						   		loadThumbBitmapThreadAgain.cancel(true);
						   		loadThumbBitmapThreadAgain = new loadThumbBitmapThreadAgain();
						   		if (first > 0){
						   			loadThumbBitmapThreadAgain.execute(first - 1, last);
						   		} else if (first == 0){
						   			loadThumbBitmapThreadAgain.execute(0, last);
						   		}	
						   		*/
					   		} else {
					   			System.gc();
					   		}
				    	}
				   	}
				}
			});
			
			tvStudents = (TextView) headerView.findViewById(R.id.tvStudents);
			tvStudents.setTypeface(Fonts.getOpenSansLightItalic(v.getContext()));
			
			ivStudentsThumb1 = (ImageView) headerView.findViewById(R.id.ivStudentsThumb1);
			ivStudentsThumb2 = (ImageView) headerView.findViewById(R.id.ivStudentsThumb2);
			ivStudentsThumb3 = (ImageView) headerView.findViewById(R.id.ivStudentsThumb3);
			ivStudentsThumb4 = (ImageView) headerView.findViewById(R.id.ivStudentsThumb4);
			ivStudentsThumb5 = (ImageView) headerView.findViewById(R.id.ivStudentsThumb5);
			
			bStudents = (Button) headerView.findViewById(R.id.bStudents);
			bStudents.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//Intent i = new Intent(getContext(), studentsNearby.class);
					//getContext().startActivity(i);
				}
		    }); 
			
			allowUserSearch(allow_thread_user_search);
			
			rlBigImage = (RelativeLayout) v.findViewById(R.id.rlBigImage);
			ivBigImage = (ImageView) v.findViewById(R.id.ivBigImage);
			pbImageLoading = (ProgressBar) v.findViewById(R.id.pbImageLoading);
			tvWords = (TextView) v.findViewById(R.id.tvWords);
			
			//TaskQueueImage.addTask(new CampusWallStudentsNearby());
			TaskQueueImage.addTask(new getSocialGroup(0), v.getContext());
			new CampusWallStudentsNearby().start();
			/*
			if (!OohlalaMain.t0){
				refreshAfterPost();
				
				OohlalaMain.t0 = true;
			}
			*/
			
			if (OohlalaMain.campusWallUnlocked){
				refreshAfterPost();
			}
			
			rlInAppTour = (RelativeLayout) v.findViewById(R.id.rlInAppTour);
			rlInAppTour.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v2) {
					rlInAppTour.setVisibility(View.GONE);
				}
			});
			
			if (CacheInternalStorage.getCacheUserFirstLogin(v.getContext()) != null){
				if (CacheInternalStorage.getCacheUserFirstLogin(v.getContext()).userAccount != null){
					if (Profile.email != null){
						if (CacheInternalStorage.getCacheUserFirstLogin(v.getContext()).userAccount.contentEquals(Profile.email)){
							
							if (CacheInternalStorage.getCacheUserFirstLogin(v.getContext()).first_time_campuswall_tab){
								CacheInternalStorage.cacheUserFirstLogin(new UserFirstLogin(Profile.email, false, false, false), v.getContext()); //cache user first login
								
								rlInAppTour.setVisibility(View.VISIBLE);
							}
						}
					}
				} else {
					CacheInternalStorage.cacheUserFirstLogin(new UserFirstLogin(Profile.email, false, false, false), v.getContext()); //cache user first login
					
					rlInAppTour.setVisibility(View.VISIBLE);
				}
			} else {
				CacheInternalStorage.cacheUserFirstLogin(new UserFirstLogin(Profile.email, false, false, false), v.getContext()); //cache user first login
				
				rlInAppTour.setVisibility(View.VISIBLE);
			}
		
	}
	
	public void reloadView() {
        // Do work to refresh the list here.
		if (loadBitmapThreadAgain != null){
			loadBitmapThreadAgain.cancel(true);
		}
		if (loadThumbBitmapThreadAgain != null){
			loadThumbBitmapThreadAgain.cancel(true);
		}
		if (freeBitmapThread != null){
			freeBitmapThread.cancel(true);
		}
		
		mHandler.post(new Runnable() {
    		public void run() {
		    	refresh = true;
		    	cut = 0;
		    	number = 0;
		    	lastcut = false;
		    	list.clear();
		    	
		    	if (list.isEmpty()){
		    		TaskQueueImage.addTask(new updateListThread(0), v.getContext());
				}
		    }
		});
    }
	
	public class CampusWallArrayAdapter extends ArrayAdapter<CampusWallModel> {
		private final List<CampusWallModel> list;
		
		class ViewHolder {
			public ImageView ivThumb;
			public TextView display_name;
			public ImageView ivImage;
			public ImageView ivImageBackground;
			public TextView time;
			public TextView message;
			public Button bComment;
			public Button bLike;
			public Button bMore;
			public ImageView ivRedDot;
			public ProgressBar pbLoading;
			public RelativeLayout bg;
		}

		public CampusWallArrayAdapter(Context context, List<CampusWallModel> list) {
			super(context, R.layout.campuswallrow, list);
			this.list = list;
		}
		
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.campuswallrow, null);
				holder = new ViewHolder();
				holder.ivThumb = (ImageView) convertView.findViewById(R.id.ivThumb);
				holder.display_name = (TextView) convertView.findViewById(R.id.display_name);
				holder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
				holder.ivImageBackground = (ImageView) convertView.findViewById(R.id.ivImageBackground);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.message = (TextView) convertView.findViewById(R.id.message);
				holder.bComment = (Button) convertView.findViewById(R.id.bComment);
				holder.bLike = (Button) convertView.findViewById(R.id.bLike);
				holder.bMore = (Button) convertView.findViewById(R.id.bMore);
				holder.ivRedDot = (ImageView) convertView.findViewById(R.id.ivRedDot);
				holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
				holder.bg = (RelativeLayout) convertView.findViewById(R.id.bg);
					
				convertView.setTag(holder);
			} else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }
			
			//final ViewHolder holder = (ViewHolder) rowView.getTag();
				
				holder.pbLoading.setVisibility(View.GONE);
				holder.ivThumb.setImageBitmap(null);
				holder.ivThumb.setImageDrawable(v.getResources().getDrawable(R.drawable.avatar));
				holder.ivImage.setImageBitmap(null);
				holder.ivImageBackground.setBackgroundDrawable(null);
				holder.time.setText("");
				holder.message.setText("");
				holder.bLike.setText("");
				holder.bComment.setText("");
				holder.bComment.setBackgroundResource(R.drawable.button_comment);
				holder.display_name.setText("");
				holder.bLike.setVisibility(View.VISIBLE);
				holder.bLike.setClickable(true);
				holder.bLike.setTextColor(getResources().getColorStateList(R.color.dimgrey3));
				holder.bLike.setBackgroundResource(R.drawable.button_like);
				holder.ivRedDot.setVisibility(View.GONE);
				holder.bMore.setVisibility(View.VISIBLE);
				
				if (list.get(position).display_name == null){
					holder.pbLoading.setVisibility(View.VISIBLE);
					holder.bLike.setVisibility(View.GONE);
					holder.bLike.setClickable(false);
					holder.bComment.setVisibility(View.GONE);
					holder.bComment.setClickable(false);
				} else {
					holder.bLike.setTypeface(Fonts.getOpenSansBold(v.getContext()));
					holder.bLike.setText(String.valueOf(list.get(position).likes));
					if (list.get(position).user_like == -1 || list.get(position).user_like == 0){
						holder.bLike.setOnClickListener(new Button.OnClickListener() {
							public void onClick(View v) {
								list.get(position).likes++;
								list.get(position).user_like = 1;
								holder.bLike.setClickable(false);
								holder.bLike.setText(String.valueOf(list.get(position).likes));
								holder.bLike.setTextColor(getResources().getColorStateList(R.color.white1));
								holder.bLike.setBackgroundResource(R.drawable.button_liked);
								Log.i("like thread invalid", "yes");
								listView.invalidateViews();
								
								// ---------------------- new api for campus wall like ---------------------------------
								new likeThread(list.get(position).school_thread_id).start();
							}
					    }); 
					} else if (list.get(position).user_like == 1){
						holder.bLike.setClickable(false);
						holder.bLike.setTextColor(getResources().getColorStateList(R.color.white1));
						holder.bLike.setBackgroundResource(R.drawable.button_liked);
					}
					
					if (list.get(position).thumb_bitmap != null){
						holder.ivThumb.setImageDrawable(null);
						holder.ivThumb.setImageBitmap(list.get(position).thumb_bitmap);
						
						holder.ivThumb.setOnClickListener(new View.OnClickListener() {
							public void onClick(View view) {
					            if (!list.get(position).is_anonymous){
						            Bundle extras = new Bundle();
									extras.putInt("user_id", list.get(position).user_id);
									Intent i = new Intent(v.getContext(), OthersProfile.class);
									i.putExtras(extras);
									getContext().startActivity(i);
					            }
					        }
					    }); 				
					} 
					
					if (list.get(position).is_anonymous){
						holder.display_name.setTextColor(getResources().getColorStateList(R.color.black1));
					} else {
						holder.display_name.setTextColor(getResources().getColorStateList(R.color.blue0));
					}
					holder.display_name.setTypeface(Fonts.getOpenSansBold(v.getContext()));
					holder.display_name.setText(list.get(position).display_name);
					holder.display_name.setOnClickListener(new View.OnClickListener() {
			            public void onClick(View view) {
			            	if (!list.get(position).is_anonymous){
				            	Bundle extras = new Bundle();
								extras.putInt("user_id", list.get(position).user_id);
								Intent i = new Intent(v.getContext(), OthersProfile.class);
								i.putExtras(extras);
								getContext().startActivity(i);
			            	}
			            }
			        }); 
					
					if (list.get(position).image_url != null){
						holder.pbLoading.setVisibility(View.VISIBLE);
						
						if (picWidth > 0){
							int y = picWidth/2;
							if (imageBg == null){
								imageBg = new BitmapDrawable(getResources(), NinePatch.getNinePatch(R.drawable.backgroundimage, picWidth, y, v.getContext()));
							}
							holder.ivImageBackground.setBackgroundDrawable(imageBg);
						}
						
						if (list.get(position).bitmap != null){
							if (picWidth > 0){
								holder.pbLoading.setVisibility(View.GONE);
								//UrlImageViewHelper.setUrlDrawable(holder.ivImage, list.get(position).image_url, picWidth);
								holder.ivImage.setImageBitmap(list.get(position).RecBitmap);
								
								if (!list.get(position).loaded){
									//holder.ivImage.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade)); //Set animation to your ImageView
									list.get(position).loaded = true;
								}							
							}
							
							holder.ivImage.setOnClickListener(new Button.OnClickListener() {
								public void onClick(View v) {
									//Log.i("getWidth", String.valueOf(list.get(position).bitmap.getWidth()) + "/" + String.valueOf((double) picWidth/list.get(position).bitmap.getWidth()));
									rlBigImage.setVisibility(View.VISIBLE);
									//tvWords.setText(list.get(position).message);
									if (list.get(position).bitmap != null){
										ivBigImage.setImageBitmap(Bitmap.createScaledBitmap(list.get(position).bitmap, picWidth, (int) (((double) picWidth/list.get(position).bitmap.getWidth())*list.get(position).bitmap.getHeight()), false));			
										rlBigImage.setOnClickListener(new Button.OnClickListener() {
											public void onClick(View v) {
												rlBigImage.setVisibility(View.GONE);
											}
										});
										pbImageLoading.setVisibility(View.GONE);
									}
									/*
									Bundle extras = new Bundle();
									extras.putString("image_url", list.get(position).image_url);
									extras.putString("image_words", list.get(position).message);
									extras.putInt("picWidth", picWidth);
									Intent i = new Intent(v.getContext(), CampusWallImage.class);
									i.putExtras(extras);
									startActivity(i);
									*/
								}
						    }); 
						} else {	
							if (picWidth > 0){
								list.get(position).loaded = false;
							}
						}
					} else {
						holder.ivImageBackground.setBackgroundDrawable(null);
					}
					
					holder.time.setTypeface(Fonts.getOpenSansLightItalic(v.getContext()));
					holder.time.setText(list.get(position).added_time);
					holder.message.setTypeface(Fonts.getOpenSansRegular(v.getContext()));
					holder.message.setText(list.get(position).message);
					
					holder.bComment.setText(String.valueOf(list.get(position).comment_count));
					holder.bComment.setTypeface(Fonts.getOpenSansBold(v.getContext()));
					holder.bComment.setOnClickListener(new Button.OnClickListener() {
						public void onClick(View v) {
							Bundle extras = new Bundle();
							extras.putInt("school_thread_id", Integer.parseInt(list.get(position).school_thread_id));
							extras.putInt("thread_type", OohlalaMain.CampusWall_thread_type);
							Intent i = new Intent(getContext(), CampusWallComment.class);
							i.putExtras(extras);
							getContext().startActivity(i);
							
							if (list.get(position).num_unread_comments > 0){
								list.get(position).num_unread_comments = 0;
								holder.ivRedDot.setVisibility(View.GONE);
								holder.bComment.setBackgroundResource(R.drawable.button_comment);
							}
						}
				    }); 
					
					if (list.get(position).num_unread_comments > 0){
						holder.bComment.setBackgroundResource(R.drawable.button_comment_unread);
						holder.ivRedDot.setVisibility(View.VISIBLE);
					}
					
					//Log.i("display thread: ", rowView.toString());
					
					if (Profile.userId != list.get(position).user_id){
						holder.bMore.setVisibility(View.GONE);
					}
					holder.bMore.setOnClickListener(new Button.OnClickListener() {
						public void onClick(View v) {
							if (Profile.userId == list.get(position).user_id){
								showAlertDialog(list.get(position).school_thread_id);
							}
						}
				    }); 
					
					holder.bg.setOnClickListener(new Button.OnClickListener() {
						public void onClick(View v) {
							Bundle extras = new Bundle();
							extras.putInt("school_thread_id", Integer.parseInt(list.get(position).school_thread_id));
							extras.putInt("thread_type", OohlalaMain.CampusWall_thread_type);
							Intent i = new Intent(getContext(), CampusWallComment.class);
							i.putExtras(extras);
							getContext().startActivity(i);
							
							if (list.get(position).num_unread_comments > 0){
								list.get(position).num_unread_comments = 0;
								holder.ivRedDot.setVisibility(View.GONE);
								holder.bComment.setBackgroundResource(R.drawable.button_comment);
							}
						}
				    }); 
				
				}

				return convertView;
		}
	
		public void showAlertDialog(final String thread_id) {
			// TODO Auto-generated method stub
	    	AlertDialog alert = new AlertDialog.Builder(v.getContext()).create();
			alert.setTitle(getContext().getString(R.string.Delete_Thread));
			alert.setMessage(getContext().getString(R.string.Warning_This_cannot_be_undone));
			alert.setButton(getContext().getString(R.string.No), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			});
			alert.setButton2(getContext().getString(R.string.Yes), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					RestClient result = null;
					try {
						if (OohlalaMain.CampusWall_thread_type == 0){
							result = new Rest.request().execute(Rest.CAMPUS_THREAD + thread_id, Rest.OSESS + Profile.sk, Rest.DELETE).get();
						} else if (OohlalaMain.CampusWall_thread_type == 1){
							result = new Rest.request().execute(Rest.GROUP_THREAD + thread_id, Rest.OSESS + Profile.sk, Rest.DELETE).get();
						}
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Log.i("delete thread", result.getResponse());
	            	
	            	if (result.getResponseCode() == 204){
	            		Toast.makeText(v.getContext(), getContext().getString(R.string.The_thread_is_deleted), Toast.LENGTH_SHORT).show();
	            		refreshAfterPost();
	            	}
				}
			});
			alert.show();
		}
		
	}

	class updateListThread extends Thread {
	    // This method is called when the thread runs
		int cut;
		List<CampusWallModel> listTemp = new ArrayList<CampusWallModel>();
		
		public updateListThread(int cut){
			this.cut = cut;
		}
		
	    public void run() {
	    	int start = cut*25 + 1;
			int end = cut*25 + 25;
			RestClient result = null;
			try {
				if (most_liked){
					if (userName != null){
						if (OohlalaMain.CampusWall_thread_type == 1){
							result = new request().execute(Rest.GROUP_THREAD + start + ";" + end + "?group_id=" + group_id + "&username=" + userName + "&order_by=likes", Rest.OSESS + Profile.sk, Rest.GET).get();
						}
						userName = null;
					} else {
						if (OohlalaMain.CampusWall_thread_type == 0){
							if (OohlalaMain.CampusWall_is_global){
								result = new request().execute(Rest.CAMPUS_THREAD + start + ";" + end + "?is_global=1" + "&order_by=likes", Rest.OSESS + Profile.sk, Rest.GET).get();
							} else {
								result = new request().execute(Rest.CAMPUS_THREAD + start + ";" + end + "?order_by=likes", Rest.OSESS + Profile.sk, Rest.GET).get();
							}
						} else if (OohlalaMain.CampusWall_thread_type == 1){
							result = new request().execute(Rest.GROUP_THREAD + start + ";" + end + "?group_id=" + group_id + "&order_by=likes", Rest.OSESS + Profile.sk, Rest.GET).get();
						}
					}
				} else {
					if (userName != null){
						if (OohlalaMain.CampusWall_thread_type == 1){
							result = new request().execute(Rest.GROUP_THREAD + start + ";" + end + "?group_id=" + group_id + "&username=" + userName, Rest.OSESS + Profile.sk, Rest.GET).get();
						}
						userName = null;
					} else {
						if (OohlalaMain.CampusWall_thread_type == 0){
							if (OohlalaMain.CampusWall_is_global){
								result = new request().execute(Rest.CAMPUS_THREAD + start + ";" + end + "?is_global=1", Rest.OSESS + Profile.sk, Rest.GET).get();
							} else {
								result = new request().execute(Rest.CAMPUS_THREAD + start + ";" + end, Rest.OSESS + Profile.sk, Rest.GET).get();
							}
						} else if (OohlalaMain.CampusWall_thread_type == 1){
							result = new request().execute(Rest.GROUP_THREAD + start + ";" + end + "?group_id=" + group_id, Rest.OSESS + Profile.sk, Rest.GET).get();
						}
					}
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("school threads: ", result.getResponse());
			
			try {
				JSONArray threads = new JSONArray(result.getResponse());
				//Log.i("school threads2: ", threads.toString());
				
				for (int i = 0; i < threads.length(); i++){
					int comment_count = 0;
					if (threads.getJSONObject(i).has("comment_count")){
						comment_count = threads.getJSONObject(i).getInt("comment_count");
					}
					boolean has_image = false;
					if (threads.getJSONObject(i).has("has_image")){
						has_image = threads.getJSONObject(i).getBoolean("has_image");
					}
					final int school_buzz_id = threads.getJSONObject(i).getInt("id");
					
					String avatar_thumb = null;
					if (threads.getJSONObject(i).has("avatar_thumb")){
						avatar_thumb = threads.getJSONObject(i).getString("avatar_thumb");
					}
					String message = null;
					if (threads.getJSONObject(i).has("message")){
						message = threads.getJSONObject(i).getString("message");
					}
					
					String display_name = null;
					if (threads.getJSONObject(i).has("display_name")){
						display_name = threads.getJSONObject(i).getString("display_name");
					}
					
					String image_url = null;
					if(has_image){
						image_url = threads.getJSONObject(i).getString("image_url");
					}
					
					int added_time = 0;
					if (threads.getJSONObject(i).has("added_time")){
						added_time = threads.getJSONObject(i).getInt("added_time");
					}
					
					int likes = 0;
					try {
						likes = threads.getJSONObject(i).getInt("likes");
					} catch (JSONException e) {

					}
	
					int user_like = threads.getJSONObject(i).getInt("user_like"); // -1: you haven't vote, 0: disliked it, 1: liked it
					int post_type = threads.getJSONObject(i).getInt("post_type"); // 1: normal post, 2: question post, 3: image post
					int user_id = 0;
					if (!threads.getJSONObject(i).getBoolean("is_anonymous")){
						user_id = threads.getJSONObject(i).getInt("user_id");
					}
					int num_unread_comments = threads.getJSONObject(i).getInt("num_unread_comments");
							
					String school_thread_id = String.valueOf(school_buzz_id);
					
					boolean is_anonymous = threads.getJSONObject(i).getBoolean("is_anonymous");
					
					listTemp.add(new CampusWallModel(avatar_thumb, display_name, image_url, message, TimeCounter.convertDate(added_time), comment_count, likes, num_unread_comments, school_thread_id, null, null, String.valueOf(this.cut), user_id, user_like, true, is_anonymous, false, true, String.valueOf(added_time)));
					number++;
				}
				
				if (threads.length() == 25){
					//list.add(new CampusWallModel(null, null, null, null, null, 0 , 0, null, width, null, null, null, null, 0, 0));
					//number++;
				} else {
					lastcut = true;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
				mHandler.post(new Runnable() {
	        		public void run() {
						refresh = false; //set the refresh to false after the refresh
				    	
				    	llLoading.setVisibility(View.GONE);
				    	
				    	//listView.setVisibility(View.GONE);
				    	list.addAll(listTemp);
				    	listView.invalidateViews();
				    	adapter.notifyDataSetChanged();
				    	//listView.setVisibility(View.VISIBLE);
				    	
				    	if (CpuUsage.getUsedMemorySize() < CpuUsage.MAX_USED_MEMORY_SIZE){
				    		preLoadImages();
					    	preLoadThumbImages();
			    		} else {
			    			System.gc();
			    		}
				    	
				    	mPullRefreshListView.onRefreshComplete();
				    	
				    	listTemp.clear();
						listTemp = null;
						
						if (list.isEmpty()){
							mPullRefreshListView.onRefreshComplete();
							mHandler.post(new Runnable() {
				        		public void run() {
				        			Toast.makeText(v.getContext(), getContext().getString(R.string.No_results_are_found), Toast.LENGTH_SHORT).show();
				        		}
							});
						}
	        		}
				});
				
	    }
	}

	public void preLoadImages() {
		// TODO Auto-generated method stub
		loadBitmapThread.cancel(true);
		
		loadBitmapThread = new loadBitmapThread();
		if (list.size() > 25){
			loadBitmapThread.execute(list.size()-26, list.size());
		} else {
			loadBitmapThread.execute(0, list.size());
		}
	}
	
	class loadBitmapThread extends AsyncTask<Integer, Void, Void> {
	    // This method is called when the thread runs
		public int start, stop;

		protected Void doInBackground(Integer... num) {
	    	//get the all the current games
			this.start = num[0];
			this.stop = num[1];
	    	//Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	for (int i = start; i < stop; i++){
	    		Log.i("current", String.valueOf(i));
	    		if(isCancelled()){
	    			// Do your stuff
	    			break;
	    		}
	    		
	    		if (CpuUsage.getUsedMemorySize() >= CpuUsage.MAX_USED_MEMORY_SIZE){
	    			System.gc();
	    			break;
	    		}
	    		
	    		if (i < list.size() && currentView){
					if (list.get(i).image_url != null && (list.get(i).bitmap == null || list.get(i).RecBitmap == null)){
						list.get(i).bitmap = ImageLoader.campusWallImageStoreAndLoad(list.get(i).image_url, v.getContext(), 512);
						int y2 = picWidth/2;
						if (i < list.size() && currentView){
							if (list.get(i).bitmap != null){
								list.get(i).RecBitmap = Bitmap.createScaledBitmap(ImageLoader.ImageCropRectangular(list.get(i).bitmap, picWidth, y2), picWidth, y2, false);
							}
						} else {
			    			break;
			    		}
						
						mHandler.post(new Runnable() {
			        		public void run() {
						    	listView.invalidateViews();
						    }
						});
					}
	    		} else {
	    			break;
	    		}
	    	}
	    	
	    	return null;
	    }
	}
	
	class loadBitmapThreadAgain extends AsyncTask<Integer, Void, Void> {
	    // This method is called when the thread runs
		public int start, stop;
		boolean invalidateViews = false;
		
		protected Void doInBackground(Integer... num) {
	    	//get the all the current games
			this.start = num[0];
			this.stop = num[1];
	    	//Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	for (int i = start; i < stop; i++){
	    		if(isCancelled()){
	    			// Do your stuff
	    			break;
	    		}
	    		
	    		if (CpuUsage.getUsedMemorySize() >= CpuUsage.MAX_USED_MEMORY_SIZE){
	    			System.gc();
	    			break;
	    		}
	    		
	    		if (i < list.size() && currentView){
					if (list.get(i).image_url != null && (list.get(i).bitmap == null || list.get(i).RecBitmap == null)){
						list.get(i).bitmap = ImageLoader.campusWallImageStoreAndLoad(list.get(i).image_url, v.getContext(), 512);
						int y2 = picWidth/2;
						if (i < list.size() && currentView){
							if (list.get(i).bitmap != null){
								list.get(i).RecBitmap = Bitmap.createScaledBitmap(ImageLoader.ImageCropRectangular(list.get(i).bitmap, picWidth, y2), picWidth, y2, false);
							}
						} else {
							break;
						}
						
						mHandler.post(new Runnable() {
			        		public void run() {
						    	listView.invalidateViews();
						    }
						});
					}
	    		} else {
	    			break;
	    		}
			}
	    
	    	return null;
	    }
	}
	
	public void preLoadThumbImages() {
		// TODO Auto-generated method stub	
		loadThumbBitmapThread.cancel(true);
		
		loadThumbBitmapThread = new loadThumbBitmapThread();
		if (list.size() > 25){
			loadThumbBitmapThread.execute(list.size()-26, list.size());
		} else {
			loadThumbBitmapThread.execute(0, list.size());
		}
	}
	
	class loadThumbBitmapThread extends AsyncTask<Integer, Void, Void> {
	    // This method is called when the thread runs
		public int start, stop;

		protected Void doInBackground(Integer... num) {
	    	//get the all the current games
			this.start = num[0];
			this.stop = num[1];
	    	//Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	for (int i = start; i < stop; i++){
	    		if(isCancelled()){
	    			// Do your stuff
	    			break;
	    		}
	    		
	    		if (i < list.size() && currentView){
					if (list.get(i).avatar_thumb != null && list.get(i).avatar_thumb.contains(".png") && list.get(i).thumb_bitmap == null){
						list.get(i).thumb_bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(list.get(i).avatar_thumb, v.getContext(), 60);
						if (i < list.size() && currentView){
					    	if (list.get(i).thumb_bitmap != null){
					    		list.get(i).thumb_bitmap = RoundedCornerImage.getRoundedCornerBitmap(list.get(i).thumb_bitmap, 8);
					    	}
						} else {
							break;
						}
						
						mHandler.post(new Runnable() {
			        		public void run() {
						    	listView.invalidateViews();
						    }
						});
					}
	    		} else {
	    			break;
	    		}
			}
	    	
	    	return null;
	    }
	}
	
	class loadThumbBitmapThreadAgain extends AsyncTask<Integer, Void, Void> {
	    // This method is called when the thread runs
		public int start, stop;
		boolean invalidateViews = false;

		protected Void doInBackground(Integer... num) {
	    	//get the all the current games
			this.start = num[0];
			this.stop = num[1];
	    	//Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	for (int i = start; i < stop; i++){
	    		if(isCancelled()){
	    			// Do your stuff
	    			break;
	    		}
	    		
	    		if (i < list.size() && currentView){
					if (list.get(i).avatar_thumb != null && list.get(i).avatar_thumb.contains(".png") && list.get(i).thumb_bitmap == null){
						list.get(i).thumb_bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(list.get(i).avatar_thumb, v.getContext(), 60);
						if (i < list.size() && currentView){
							if (list.get(i).thumb_bitmap != null){
					    		list.get(i).thumb_bitmap = RoundedCornerImage.getRoundedCornerBitmap(list.get(i).thumb_bitmap, 8);
					    	}
						} else {
							break;
						}
						
						mHandler.post(new Runnable() {
			        		public void run() {
						    	listView.invalidateViews();
						    }
						});
					}
	    		} else {
	    			break;
	    		}
	    	}
	    	
	    	return null;
	    }
	}
	
	class freeBitmapThread extends AsyncTask<Integer, Void, Void> {
	    // This method is called when the thread runs
		public int start, stop;
		
		protected Void doInBackground(Integer... num) {
			this.start = num[0];
			this.stop = num[1];
			
			if (start > ImageLoader.loaderRange){
		    	for (int i = 0; i < start - ImageLoader.loaderRange; i++){
		    		if(isCancelled()){
		    			// Do your stuff
		    			break;
		    		}
		    		
		    		if (i < list.size() && currentView){
						if (list.get(i).thumb_bitmap != null){
							list.get(i).thumb_bitmap.recycle();
							list.get(i).thumb_bitmap = null;
						}
						if (list.get(i).bitmap != null){
							list.get(i).bitmap.recycle();
							list.get(i).bitmap = null;
						}
						if (list.get(i).RecBitmap != null){
							list.get(i).RecBitmap.recycle();
							list.get(i).RecBitmap = null;
						}
		    		} else {
		    			break;
		    		}
				}
	    	}
			
	    	if (stop + ImageLoader.loaderRange < list.size()){
		    	for (int i = stop + ImageLoader.loaderRange; i < list.size(); i++){
		    		if(isCancelled()){
		    			// Do your stuff
		    			break;
		    		}
		    		
		    		if (i < list.size() && currentView){
						if (list.get(i).thumb_bitmap != null){
							list.get(i).thumb_bitmap.recycle();
							list.get(i).thumb_bitmap = null;
						}
						if (list.get(i).bitmap != null){
							list.get(i).bitmap.recycle();
							list.get(i).bitmap = null;
						}
						if (list.get(i).RecBitmap != null){
							list.get(i).RecBitmap.recycle();
							list.get(i).RecBitmap = null;
						}
		    		} else {
		    			break;
		    		}
				}
	    	}
	    	
	    	return null;
		}
	}
	
	public void refreshAfterPost() {
 		mPullRefreshListView.setRefreshing();
 		reloadView();
	}
	
	class CampusWallStudentsNearby2 extends AsyncTask<Void, Void, Bitmap[]> {
	    // This method is called when the thread runs

		protected Bitmap[] doInBackground(Void... cuts) {
	    	//get the all the current games			
			Log.i("CampusWallStudentsNearby", "-------------------------------------------------------");
			
			RestClient result_CampusWallStudentsNearby2 = null;
			try {
				result_CampusWallStudentsNearby2 = new request().execute(Rest.USER + "1;25" + "?latitude=" + myLat + "&longitude=" + myLongi, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Bitmap[] studentsImage = {null, null, null, null, null};
			
			try {
				JSONArray users = new JSONArray(result_CampusWallStudentsNearby2.getResponse());
				Log.i("nearby users: ", users.toString());
				
				int k = 0;
				for (int i = 0; i < users.length(); i++){
					boolean has_avatar = users.getJSONObject(i).getBoolean("has_avatar");
					String avatar = users.getJSONObject(i).getString("avatar_thumb_url");
					
					if (has_avatar){
						studentsImage[k] = ImageLoader.studentsNearbyImageStoreAndLoad(avatar, v.getContext(), 35);
						k++;
							
						if (k == 5 || k == studentsImage.length){
							break;
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return studentsImage;
	    }
		
		protected void onPostExecute(final Bitmap[] studentsImage) {
			if (currentView){
				if (studentsImage[0] != null){
					ivStudentsThumb1.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(studentsImage[0]), 8));
				}
				if (studentsImage[1] != null){
					ivStudentsThumb2.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(studentsImage[1]), 8));
				}
				if (studentsImage[2] != null){
					ivStudentsThumb3.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(studentsImage[2]), 8));
				}
				if (studentsImage[3] != null){
					ivStudentsThumb4.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(studentsImage[3]), 8));
				}
				if (studentsImage[4] != null){
					ivStudentsThumb5.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(studentsImage[4]), 8));
				}	
			}
	    }
	}
	
	class CampusWallStudentsNearby extends Thread {
	    // This method is called when the thread runs
		
	    public void run() {
	    	//get the all the current games			
			Log.i("CampusWallStudentsNearby", "-------------------------------------------------------");
			
			RestClient result = null;
			try {
				result = new request().execute(Rest.USER + "1;25" + "?latitude=" + myLat + "&longitude=" + myLongi, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray users = new JSONArray(result.getResponse());
				//Log.i("nearby users: ", users.toString());
				
				int k = 0;
				for (int i = 0; i < users.length(); i++){
					boolean has_avatar = false;
					if (users.getJSONObject(i).has("has_avatar")){
						has_avatar = users.getJSONObject(i).getBoolean("has_avatar");
					}
					String avatar = null;
					if (users.getJSONObject(i).has("avatar_thumb_url")){
						avatar = users.getJSONObject(i).getString("avatar_thumb_url");
					}
					
					if (has_avatar){
						studentsImage[k] = ImageLoader.studentsNearbyImageStoreAndLoad(avatar, v.getContext(), 35);
						if (studentsImage[k] != null){
							k++;
						}	
						
						if (k == 5 || k == studentsImage.length){
							break;
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (currentView){
				mHandler.post(new Runnable() {
	        		public void run() {
	        			showStudentsNearby();
	        		}
				});
			}
	    }
	}
	
	public void showStudentsNearby(){
		if (studentsImage[0] != null){
			ivStudentsThumb1.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(studentsImage[0]), 8));
		}
		if (studentsImage[1] != null){
			ivStudentsThumb2.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(studentsImage[1]), 8));
		}
		if (studentsImage[2] != null){
			ivStudentsThumb3.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(studentsImage[2]), 8));
		}
		if (studentsImage[3] != null){
			ivStudentsThumb4.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(studentsImage[3]), 8));
		}
		if (studentsImage[4] != null){
			ivStudentsThumb5.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(studentsImage[4]), 8));
		}	
	}

	public void clearImages() {
		// TODO Auto-generated method stub
		Log.i("clearImages", "-------------------");
		
		for (int i = 0; i < list.size(); i++){
    		if (i < list.size()){
				if (list.get(i).thumb_bitmap != null){
					list.get(i).thumb_bitmap.recycle();
					list.get(i).thumb_bitmap = null;
				}
				if (list.get(i).bitmap != null){
					list.get(i).bitmap.recycle();
					list.get(i).bitmap = null;
				}
    		}
		}
	}

	public void resumeImages() {
		// TODO Auto-generated method stub
		
		new resumeBitmap().execute();
	}
	
	class resumeBitmap extends AsyncTask<Void, Void, Void> {
	    // This method is called when the thread runs
		protected Void doInBackground(Void... params) {
			for (int i = 0; i < list.size(); i++){
	    		if (i < list.size() && currentView){
	    			if (list.get(i).avatar_thumb != null && list.get(i).avatar_thumb.contains(".png") && list.get(i).thumb_bitmap == null){
						list.get(i).thumb_bitmap = ImageLoader.studentsNearbyImageStoreAndLoad(list.get(i).avatar_thumb, v.getContext(), 60);
						
	    			}
	    			if (list.get(i).image_url != null && list.get(i).bitmap == null){
						list.get(i).bitmap = ImageLoader.campusWallImageStoreAndLoad(list.get(i).image_url, v.getContext(), 512);
						
					}
	    		}
			}
			
			return null;
		}
	}
	
	class likeThread extends Thread {
	    // This method is called when the thread runs
		String school_thread_id;
		
		public likeThread(String school_thread_id){
			this.school_thread_id = school_thread_id;
		}
		
	    public void run() {
	    	RestClient result = null;
			try {
				if (OohlalaMain.CampusWall_thread_type == 0){
					result = new Rest.requestBody().execute(Rest.CAMPUS_THREAD + school_thread_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "like", "1").get();
				} else if (OohlalaMain.CampusWall_thread_type == 1){
					result = new Rest.requestBody().execute(Rest.GROUP_THREAD + school_thread_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "like", "1").get();
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("like thread", result.getResponse());
	    }
	}
	
	class getSocialGroup extends Thread {
	    // This method is called when the thread runs
		int cut;
		int num;
		List<SocialGroupModel> listTemp = new ArrayList<SocialGroupModel>();
		
		public getSocialGroup(int cut){
			this.cut = cut;
		}
		
	    public void run() {
	    	int start = cut*25 + 1;
			int end = cut*25 + 25;
			RestClient result = null;
			try {
				result = new request().execute(Rest.GROUP + start + ";" + end, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("social groups: ", result.getResponse());
			
			try {
				JSONArray threads = new JSONArray(result.getResponse());
				//Log.i("school threads2: ", threads.toString());
				
				for (int i = 0; i < threads.length(); i++){
					int id = threads.getJSONObject(i).getInt("id");
					String name = null;
					try {
						name = threads.getJSONObject(i).getString("name");
					} catch (JSONException e) {
						
					}
					int num_unread_threads = 0;
					try {
						num_unread_threads = threads.getJSONObject(i).getInt("num_unread_threads");
					} catch (JSONException e) {
						
					}
					int group_type = 0;
					try {
						group_type = threads.getJSONObject(i).getInt("group_type");
					} catch (JSONException e) {
						
					}
					boolean is_read_only = false;
					try {
						is_read_only = threads.getJSONObject(i).getBoolean("is_read_only");
					} catch (JSONException e) {
						
					}
					boolean allow_thread_user_search = false;
					try {
						allow_thread_user_search = threads.getJSONObject(i).getBoolean("allow_thread_user_search");
					} catch (JSONException e) {
						
					}
					boolean is_member = false;
					try {
						is_member = threads.getJSONObject(i).getBoolean("is_member");
					} catch (JSONException e) {
						
					}
					
					listTemp.add(new SocialGroupModel(id, name, num_unread_threads, group_type, is_read_only, allow_thread_user_search, is_member));
				}
				
				num = threads.length();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
				mHandler.post(new Runnable() {
	        		public void run() {
	        			RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
								ConvertDpsToPixels.getPixels(268, v.getContext()), 
								ConvertDpsToPixels.getPixels(34*num + num/2 + 1, v.getContext()));
						param.addRule(RelativeLayout.BELOW, filterRow2.getId());
	        			lvSocialGroups.setLayoutParams(param);
	        			
	        			//lvSocialGroups.setVisibility(View.GONE);
	        			list_SocialGroup.clear();
				    	list_SocialGroup.addAll(listTemp);
				    	lvSocialGroups.invalidateViews();
				    	adapter_SocialGroup.notifyDataSetChanged();
				    	//lvSocialGroups.setVisibility(View.VISIBLE);
				    	
				    	listTemp.clear();
						listTemp = null;
	        		}
				});
				
	    }
	}
	
	public void allowUserSearch(boolean allow){
		if (allow){
			bStudents.setVisibility(View.GONE);
			ivStudentsThumb5.setVisibility(View.GONE);
			ivStudentsThumb4.setVisibility(View.GONE);
			ivStudentsThumb3.setVisibility(View.GONE);
			ivStudentsThumb2.setVisibility(View.GONE);
			ivStudentsThumb1.setVisibility(View.GONE);
			tvStudents.setVisibility(View.GONE);
			
			rlUserSearch.setVisibility(View.VISIBLE);
		} else {
			bStudents.setVisibility(View.VISIBLE);
			ivStudentsThumb5.setVisibility(View.VISIBLE);
			ivStudentsThumb4.setVisibility(View.VISIBLE);
			ivStudentsThumb3.setVisibility(View.VISIBLE);
			ivStudentsThumb2.setVisibility(View.VISIBLE);
			ivStudentsThumb1.setVisibility(View.VISIBLE);
			tvStudents.setVisibility(View.VISIBLE);
			
			rlUserSearch.setVisibility(View.GONE);
		}
	}
	
	public void onPause() {
		// TODO Auto-generated method stub
		
		currentView = false;
	}
	
	public void onResume() {
		// TODO Auto-generated method stub
		
		currentView = true;
	}
	
}
