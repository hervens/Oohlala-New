package com.gotoohlala;

import inbox.Inbox;
import inbox.InboxArrayAdapter;
import inbox.InboxModel;

import java.util.ArrayList;
import java.util.List;

import launchOohlala.CheckEmail;
import user.Profile;

import campuswall.CampusWallImage;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import datastorage.ConvertDpsToPixels;
import datastorage.DeviceDimensions;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.RoundedCornerImage;

import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LeftPanelLayout extends FrameLayout {

    public static ImageView ivProfilePic;
    public TextView tvName;
    public static ListView listView;
    public static List<LeftPanelModel> list = new ArrayList<LeftPanelModel>();
	static LeftPanelArrayAdapter adapter;

    private onSeletedListener mOnSeletedListener;
    
    static Handler mHandler = new Handler(Looper.getMainLooper());
    
    static View v = null;
    
    public LeftPanelLayout(Context context) {
        super(context);
        setupViews();
    }

    public LeftPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupViews();
    }

    private void setupViews() {
        final LayoutInflater mInflater = LayoutInflater.from(getContext());
        v = (LinearLayout) mInflater.inflate(R.layout.left_panel, null);
        addView(v);

        ivProfilePic = (ImageView) v.findViewById(R.id.ivProfilePic);
		if (Profile.avatar_thumb_url != null && Profile.avatar_thumb_url.contains(".png")){
			TaskQueueImage.addTask(new loadThumbImage(), getContext());
		}
		ivProfilePic.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (ScrollerContainer.mPanelInvisible){
					mOnSeletedListener.seletedChildView(0, 10);
				}
			}
	    }); 
		
		tvName = (TextView) v.findViewById(R.id.tvName);
		tvName.setTypeface(Fonts.getOpenSansBold(getContext()));
		tvName.setText(Profile.firstName.toUpperCase() + " " + Profile.lastName.toUpperCase());
		tvName.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (ScrollerContainer.mPanelInvisible){
					//mOnSeletedListener.seletedChildView(0, 11);
				}
			}
	    }); 
        
		if (list.isEmpty()){
	        list.add(new LeftPanelModel(0, getContext().getString(R.string.Schedule), 0));
	        list.add(new LeftPanelModel(1, getContext().getString(R.string.friends), 0));
	        list.add(new LeftPanelModel(2, getContext().getString(R.string.Campus_Feed), 0));
	        list.add(new LeftPanelModel(3, getContext().getString(R.string.Photos), 0));
	        list.add(new LeftPanelModel(4, getContext().getString(R.string.Events), 0));
	        list.add(new LeftPanelModel(5, getContext().getString(R.string.Explore), 0));
	        list.add(new LeftPanelModel(6, getContext().getString(R.string.Challenges), 0));
	        list.add(new LeftPanelModel(7, getContext().getString(R.string.Nearby_Students), 0));
	        list.add(new LeftPanelModel(8, getContext().getString(R.string.Inbox), 0));
	        list.add(new LeftPanelModel(9, getContext().getString(R.string.Campus_Clubs), 0));
	        list.add(new LeftPanelModel(12, "Discover My Campus", 0));
		}
        		
        listView = (ListView) v.findViewById(R.id.lvLeftPanel);
        adapter = new LeftPanelArrayAdapter(getContext(), list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
			 public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {		
				 
                mOnSeletedListener.seletedChildView(0, list.get(position).id);
            }
        });
    }

    /**
     * ����������Item����������
     * @param seletedListener
     */
    public void setOnSeletedListener(onSeletedListener seletedListener) {
        mOnSeletedListener = seletedListener;
    }

    /**
     * ������Item����������
     * @author android_ls
     */
    public interface onSeletedListener {
        /**
         * ����������Item����������
         * @param groupPosition ������Id
         * @param childPosition ����������������
         */
        public abstract void seletedChildView(int groupPosition, int childPosition);
    }
    
    public static void ChangeLeftPanelPositionNumber(int position, int number){
    	list.get(position).number = number;
    	
    	mHandler.post(new Runnable() {
    		public void run() {
		    	listView.invalidateViews();
		    	//adapter.notifyDataSetChanged();
		    }
		});
    }
    
    public class LeftPanelArrayAdapter extends ArrayAdapter<LeftPanelModel> {
    	private final Context context;
    	private final List<LeftPanelModel> list;
    	
    	class ViewHolder {
    		public ImageView ivImage;
    		public TextView tvName;
    		public TextView tvNumber;
    	}

    	public LeftPanelArrayAdapter(Context context, List<LeftPanelModel> list) {
    		super(context, R.layout.leftpanelrow, list);
    		this.context = context;
    		this.list = list;
    	}

    	@Override
    	public View getView(final int position, View convertView, ViewGroup parent) {
    		View rowView = convertView;
    		if (rowView == null){
    			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			rowView = inflater.inflate(R.layout.leftpanelrow, null);
    			ViewHolder viewHolder = new ViewHolder();
    			viewHolder.ivImage = (ImageView) rowView.findViewById(R.id.ivImage);
    			viewHolder.tvName = (TextView) rowView.findViewById(R.id.tvName);
    			viewHolder.tvNumber = (TextView) rowView.findViewById(R.id.tvNumber);
    			
    			rowView.setTag(viewHolder);
    		}
    		
    		ViewHolder holder = (ViewHolder) rowView.getTag();
    		
    		holder.ivImage.setBackgroundDrawable(null);
    		holder.tvName.setText(null);
    		holder.tvNumber.setText(null);
    		holder.tvNumber.setVisibility(View.GONE);
    		
    		if (list.get(position).id == 0){
    			holder.ivImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.menusocialschedule));
    		} else if (list.get(position).id == 1){
    			holder.ivImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.menufriends));
    		} else if (list.get(position).id == 2){
    			holder.ivImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.menufeed));
    		} else if (list.get(position).id == 3){
    			holder.ivImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.menuphotos));
    		} else if (list.get(position).id == 4){
    			holder.ivImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.menuevents));
    		} else if (list.get(position).id == 5){
    			holder.ivImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.menumap));
    		} else if (list.get(position).id == 6){
    			holder.ivImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.menugames));
    		} else if (list.get(position).id == 7){
    			holder.ivImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.menunearby));
    		} else if (list.get(position).id == 8){
    			holder.ivImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.menuinbox));
    		} else if (list.get(position).id == 9){
    			holder.ivImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.menuclubs));
    		} else if (list.get(position).id == 12){
    			holder.ivImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.menuclubs));
    		} 
    		
    		holder.tvName.setTypeface(Fonts.getOpenSansRegular(context));
    		holder.tvName.setText(list.get(position).name);
    		
    		holder.tvNumber.setTypeface(Fonts.getOpenSansBold(context));
    		if (list.get(position).number > 0){
    			if (list.get(position).number > 10){
    				holder.tvNumber.setText("10+");
    			} else {
    				holder.tvNumber.setText(String.valueOf(list.get(position).number));
    			}
    			
    			RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
    					ConvertDpsToPixels.getPixels(38, context),
		        		ConvertDpsToPixels.getPixels(25, context));
    			param.addRule(RelativeLayout.CENTER_VERTICAL);
		        param.setMargins(DeviceDimensions.getWidth(getContext())*60/100, 0, 0, 0);
		        	
		        holder.tvNumber.setLayoutParams(param); 
		        
		        holder.tvNumber.setVisibility(View.VISIBLE);
    		} 
    		
    		return rowView;
    	}
    }
    
    public static class loadThumbImage extends Thread {
	    // This method is called when the thread runs
	    public void run() {
			mHandler.post(new Runnable() {
	        	public void run() {
	        		Bitmap roundImage = ImageLoader.studentsNearbyImageStoreAndLoad(Profile.avatar_thumb_url, v.getContext(), 60);
	        		if (roundImage != null){
	        			roundImage = RoundedCornerImage.getCircleBitmap(roundImage);
	        			ivProfilePic.setImageBitmap(roundImage);
	        		}
				}
			});
		}
    }

}