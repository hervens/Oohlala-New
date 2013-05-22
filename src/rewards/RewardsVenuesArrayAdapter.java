package rewards;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import profile.Friends;

import network.RetrieveData;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.gotoohlala.R;

import datastorage.CacheInternalStorage;
import datastorage.DeviceDimensions;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.MemoryStorage;
import datastorage.RoundedCornerImage;
import datastorage.StringLanguageSelector;

public class RewardsVenuesArrayAdapter extends ArrayAdapter<RewardsVenuesModel> {
	private final Context context;
	private final List<RewardsVenuesModel> list;
	
	static class ViewHolder {
		public ImageView ivThumb;
		public TextView name;
		public TextView itemName;
		public TextView number;
	}

	public RewardsVenuesArrayAdapter(Context context, List<RewardsVenuesModel> list) {
		super(context, R.layout.rewardsvenuesrow, list);
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.rewardsvenuesrow, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.ivThumb = (ImageView) rowView.findViewById(R.id.ivThumb);
			viewHolder.name = (TextView) rowView.findViewById(R.id.name);
			viewHolder.itemName = (TextView) rowView.findViewById(R.id.itemName);
			viewHolder.number = (TextView) rowView.findViewById(R.id.number);
			
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		holder.ivThumb.setImageBitmap(null);
		holder.name.setText("");	
		holder.itemName.setText("");	
		holder.number.setText("");	
		
		if (list.get(position).image_bitmap != null){
			//image save into the memory and cache
			holder.ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(list.get(position).image_bitmap, 8));
		}
		
		holder.name.setTypeface(Fonts.getOpenSansBold(context));
		holder.name.setText(StringLanguageSelector.retrieveString(list.get(position).name));
		holder.itemName.setText(list.get(position).itemName);
		holder.itemName.setMaxWidth((DeviceDimensions.getWidth(context)*13)/20);
		holder.number.setText(list.get(position).number);
		
		return rowView;
	}
	
	
}
