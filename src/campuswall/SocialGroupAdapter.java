package campuswall;

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
import datastorage.DeviceDimensions;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.MemoryStorage;
import datastorage.RoundedCornerImage;

public class SocialGroupAdapter extends ArrayAdapter<SocialGroupModel> {
	private final Context context;
	private final List<SocialGroupModel> list;
	
	static class ViewHolder {
		public TextView tvName;
	}

	public SocialGroupAdapter(Context context, List<SocialGroupModel> list) {
		super(context, R.layout.socialgrouprow, list);
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.socialgrouprow, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.tvName = (TextView) rowView.findViewById(R.id.tvName);
			
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
	
		holder.tvName.setText(null);

		holder.tvName.setTypeface(Fonts.getOpenSansBold(context));
		holder.tvName.setText(list.get(position).name.toUpperCase());

		//Log.i("display thread: ", rowView.toString());
		return rowView;
	}
	
	
}
