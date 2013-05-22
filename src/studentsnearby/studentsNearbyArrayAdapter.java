package studentsnearby;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;

import profile.OthersProfile;

import network.ErrorCodeParser;
import network.RetrieveData;
import rewards.RewardsVenuesStoresDeal;
import smackXMPP.XMPPClient;
import user.Profile;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import campuswall.CampusWall;

import com.gotoohlala.R;

import datastorage.CacheInternalStorage;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.MemoryStorage;
import datastorage.StringLanguageSelector;

public class studentsNearbyArrayAdapter extends ArrayAdapter<studentsNearbyModel> {
	private final Context context;
	private final List<studentsNearbyModel> list;
	
	static class ViewHolder {
		public ImageView ivThumb;
		public TextView tvFirstname;
		public TextView tvLookingFor;
		public TextView tvLocationName;
		public TextView tvSchoolName;
		public Button bVote;
		public ProgressBar pbLoading;
		public RelativeLayout bg;
	}

	public studentsNearbyArrayAdapter(Context context, List<studentsNearbyModel> list) {
		super(context, R.layout.studentsnearbyrow, list);
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.studentsnearbyrow, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.ivThumb = (ImageView) rowView.findViewById(R.id.ivThumb);
			viewHolder.tvFirstname = (TextView) rowView.findViewById(R.id.tvFirstname);
			viewHolder.tvLookingFor = (TextView) rowView.findViewById(R.id.tvLookingFor);
			viewHolder.tvLocationName = (TextView) rowView.findViewById(R.id.tvLocationName);
			viewHolder.tvSchoolName = (TextView) rowView.findViewById(R.id.tvSchoolName);
			viewHolder.bVote = (Button) rowView.findViewById(R.id.bVote);
			viewHolder.pbLoading = (ProgressBar) rowView.findViewById(R.id.pbLoading);
			viewHolder.bg = (RelativeLayout) rowView.findViewById(R.id.bg);
			
			rowView.setTag(viewHolder);
		}
		
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		LayoutParams param1 = new LinearLayout.LayoutParams(
				0,
                0);
		
		holder.pbLoading.setVisibility(View.GONE);
		if (Profile.allow_ratings) {
			//holder.bVote.setVisibility(View.VISIBLE);
			//holder.bVote.setClickable(true);
			
			holder.bVote.setVisibility(View.GONE);
			holder.bVote.setClickable(false);
		} else {
			holder.bVote.setVisibility(View.GONE);
			holder.bVote.setClickable(false);
		}
		
		holder.bVote.setText(context.getString(R.string.vote));
		holder.bVote.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.buttonlike));
		holder.ivThumb.setImageBitmap(null);
		holder.ivThumb.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar));
		holder.tvFirstname.setText("");
		holder.tvLookingFor.setText("");
		holder.tvLocationName.setText("");
		holder.tvSchoolName.setText("");
		
		if (list.get(position).first_name == null){
			holder.pbLoading.setVisibility(View.VISIBLE);
			holder.bVote.setVisibility(View.GONE);
			holder.bVote.setClickable(false);
		}
		
		/*
		if (!list.get(position).has_liked){
			holder.bVote.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					final CharSequence[] items = {context.getString(R.string.Likes) + list.get(position).profile_likes, context.getString(R.string.Hots) + list.get(position).profile_hots, context.getString(R.string.Crushes) + list.get(position).profile_crushes, context.getString(R.string.Cancel)};
					
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle(context.getString(R.string.Vote_for) + list.get(position).first_name + " " + list.get(position).last_name);
					builder.setItems(items, new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int item) {
					    	Hashtable<String, String> params;
					    	String result;
					    	
					    	switch(item){
					    	case 0:
					    		params = new Hashtable<String, String>();
								params.put("like_user_id", String.valueOf(list.get(position).user_id));
								params.put("like_type", String.valueOf(0));
								result = RetrieveData.requestMethod(RetrieveData.LIKE_PROFILE, params);
								Log.i("profile like: ", result);
										
								if (ErrorCodeParser.parser(result) == 0) {
									list.get(position).has_liked = true;
									holder.bVote.setClickable(false);
									holder.bVote.setText(context.getString(R.string.voted));
									holder.bVote.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button2));
								}
					    		Toast.makeText(context, context.getString(R.string.Voted_like_for) + list.get(position).first_name + " " + list.get(position).last_name, Toast.LENGTH_SHORT).show();
					    		break;
					    	case 1:
					    		params = new Hashtable<String, String>();
								params.put("like_user_id", String.valueOf(list.get(position).user_id));
								params.put("like_type", String.valueOf(1));
								result = RetrieveData.requestMethod(RetrieveData.LIKE_PROFILE, params);
								Log.i("profile hot: ", result);
										
								if (ErrorCodeParser.parser(result) == 0) {
									list.get(position).has_liked = true;
									holder.bVote.setClickable(false);
									holder.bVote.setText(context.getString(R.string.voted));
									holder.bVote.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button2));
								}
					    		Toast.makeText(context, context.getString(R.string.Voted_hot_for) + list.get(position).first_name + " " + list.get(position).last_name, Toast.LENGTH_SHORT).show();
					    		break;
					    	case 2:
					    		params = new Hashtable<String, String>();
								params.put("like_user_id", String.valueOf(list.get(position).user_id));
								params.put("like_type", String.valueOf(2));
								result = RetrieveData.requestMethod(RetrieveData.LIKE_PROFILE, params);
								Log.i("profile crush: ", result);
										
								if (ErrorCodeParser.parser(result) == 0) {
									list.get(position).has_liked = true;
									holder.bVote.setClickable(false);
									holder.bVote.setText(context.getString(R.string.voted));
									holder.bVote.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button2));
								}
					    		Toast.makeText(context, context.getString(R.string.Voted_crush_for) + list.get(position).first_name + " " + list.get(position).last_name, Toast.LENGTH_SHORT).show();
					    		break;
					    	case 3:
					    		break;
					    	}
					    }
					});
					AlertDialog alert = builder.create();
					alert.show();
				}
		    }); 
		} else {
			holder.bVote.setClickable(false);
			holder.bVote.setText(context.getString(R.string.voted));
			holder.bVote.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button2));
		}
		*/
		
		if (list.get(position).thumb_bitmap != null){
			holder.ivThumb.setImageDrawable(null);
			holder.ivThumb.setImageBitmap(list.get(position).thumb_bitmap);
			//Log.i("nearby students avatar", bitmap.toString());
		} 
		
		holder.tvFirstname.setTypeface(Fonts.getOpenSansBold(context));
		holder.tvFirstname.setText(list.get(position).first_name);
		holder.tvFirstname.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Bundle extras = new Bundle();
				extras.putInt("user_id", list.get(position).user_id);
				Intent i = new Intent(context, OthersProfile.class);
				i.putExtras(extras);
				context.startActivity(i);
            }
        }); 
		
		if (list.get(position).looking_for.trim().length() == 0){
			holder.tvLookingFor.setVisibility(View.GONE);
		}
		holder.tvLookingFor.setText(list.get(position).looking_for);
		
		if (list.get(position).location_name != null){
			holder.tvLocationName.setText(list.get(position).location_name);
		}
		
		holder.tvSchoolName.setText(list.get(position).school_name);
		
		//Log.i("display thread: ", rowView.toString());
		
		if (context != null){
			holder.bg.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					Bundle extras = new Bundle();
					extras.putInt("user_id", list.get(position).user_id);
					Intent i = new Intent(context, OthersProfile.class);
					i.putExtras(extras);
					context.startActivity(i);
				}
		    }); 
		}
		
		return rowView;
	}
	
}
