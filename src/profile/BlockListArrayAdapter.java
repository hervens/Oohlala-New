package profile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;

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
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.gotoohlala.R;

import datastorage.CacheInternalStorage;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.MemoryStorage;
import datastorage.RoundedCornerImage;

public class BlockListArrayAdapter extends ArrayAdapter<BlockListModel> {
	private final Context context;
	private final Activity parentAct;
	private final List<BlockListModel> list;
	
	static class ViewHolder {
		public ImageView ivThumb;
		public TextView tvUserName;
	}

	public BlockListArrayAdapter(Context context, Activity parentAct, List<BlockListModel> list) {
		super(context, R.layout.blocklistrow, list);
		this.parentAct = parentAct;
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.blocklistrow, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.ivThumb = (ImageView) rowView.findViewById(R.id.ivThumb);
			viewHolder.tvUserName = (TextView) rowView.findViewById(R.id.tvUserName);
			
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		holder.ivThumb.setImageBitmap(null);
		holder.tvUserName.setText(null);
		
		if (list.get(position).avatar != null){
			//image save into the memory and cache
			holder.ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.KembrelImageStoreAndLoad(list.get(position).avatar, context), 8));
		} 
		
		holder.tvUserName.setTypeface(Fonts.getOpenSansBold(context));
		holder.tvUserName.setText(list.get(position).first_name + " " + list.get(position).last_name);
		
		//Log.i("display thread: ", rowView.toString());
		return rowView;
	}
	
	
}
