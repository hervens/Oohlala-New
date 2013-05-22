package inbox;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;

import profile.Friends;

import network.RetrieveData;
import smackXMPP.XMPPClient;
import android.app.Activity;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.gotoohlala.R;

import datastorage.CacheInternalStorage;
import datastorage.DeviceDimensions;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.MemoryStorage;
import datastorage.RoundedCornerImage;

public class InboxArrayAdapter extends ArrayAdapter<InboxModel> {
	private final Context context;
	private final List<InboxModel> list;
	
	static class ViewHolder {
		public ImageView ivThumb;
		public TextView tvUserName;
		public TextView tvTime;
		public TextView tvLastMsg;
		public TextView tvUnreadMsg;
		public TextView tvInboxCategory;
	}

	public InboxArrayAdapter(Context context, List<InboxModel> list) {
		super(context, R.layout.inboxrow, list);
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.inboxrow, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.ivThumb = (ImageView) rowView.findViewById(R.id.ivThumb);
			viewHolder.tvUserName = (TextView) rowView.findViewById(R.id.tvUserName);
			viewHolder.tvTime = (TextView) rowView.findViewById(R.id.tvTime);
			viewHolder.tvLastMsg = (TextView) rowView.findViewById(R.id.tvLastMsg);
			viewHolder.tvUnreadMsg = (TextView) rowView.findViewById(R.id.tvUnreadMsg);
			viewHolder.tvInboxCategory = (TextView) rowView.findViewById(R.id.tvInboxCategory);
			
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		holder.ivThumb.setImageBitmap(null);
		holder.ivThumb.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar));
		holder.tvUserName.setText(null);
		holder.tvTime.setText(null);
		holder.tvLastMsg.setText(null);
		holder.tvUnreadMsg.setText(null);
		holder.tvUnreadMsg.setBackgroundDrawable(null);
		holder.tvInboxCategory.setText("");
		holder.tvInboxCategory.setVisibility(View.GONE);
		
		if (list.get(position).thumb_bitmap != null){
			//image save into the memory and cache
			holder.ivThumb.setImageDrawable(null);
			holder.ivThumb.setImageBitmap(list.get(position).thumb_bitmap);
		} 
		
		holder.tvUserName.setTypeface(Fonts.getOpenSansBold(context));
		holder.tvUserName.setText(list.get(position).first_name + " " + list.get(position).last_name);
		holder.tvTime.setTypeface(Fonts.getOpenSansLightItalic(context));
		if (list.get(position).friend_request_id != 0){
			holder.tvTime.setText(list.get(position).friend_request_time_sent);
			holder.tvLastMsg.setText(list.get(position).friend_request_message);
			holder.tvLastMsg.setMaxWidth((DeviceDimensions.getWidth(context)*13)/20);
		} else {
			holder.tvTime.setText(list.get(position).time);
			holder.tvLastMsg.setText(list.get(position).last_message);
			holder.tvLastMsg.setMaxWidth((DeviceDimensions.getWidth(context)*13)/20);
			
			if (list.get(position).num_unread > 0){
				holder.tvUnreadMsg.setText(String.valueOf(list.get(position).num_unread));
				holder.tvUnreadMsg.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.buttonlike));
			}
		}
		
		/*
		if (list.get(position).friend_request_id != 0 && position == 0){
			holder.tvInboxCategory.setVisibility(View.VISIBLE);
			holder.tvInboxCategory.setText(context.getString(R.string.FRIEND_REQUESTS).toUpperCase());
		}
		
		if (list.get(position).friend_request_id == 0 && (position == 0 || list.get(position-1).friend_request_id != 0)){
			holder.tvInboxCategory.setVisibility(View.VISIBLE);
			holder.tvInboxCategory.setText(context.getString(R.string.MESSAGES).toUpperCase());
		}
		*/
		
		return rowView;
	}
	
	
}
