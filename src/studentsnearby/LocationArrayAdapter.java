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
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.gotoohlala.R;

import datastorage.CacheInternalStorage;
import datastorage.ImageLoader;
import datastorage.MemoryStorage;
import datastorage.StringLanguageSelector;

public class LocationArrayAdapter extends ArrayAdapter<nearbyLocationModel> {
	private final Context context;
	private final Activity Act;
	private final List<nearbyLocationModel> list;
	
	static class ViewHolder {
		public TextView tvLocationName;
	}

	public LocationArrayAdapter(Context context, Activity Act, List<nearbyLocationModel> list) {
		super(context, R.layout.locationrow, list);
		this.Act = Act;
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.locationrow, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.tvLocationName = (TextView) rowView.findViewById(R.id.tvLocationName);

			rowView.setTag(viewHolder);
		}
		
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		holder.tvLocationName.setText("");
		
		holder.tvLocationName.setText(list.get(position).name);
		
		//Log.i("display thread: ", rowView.toString());
		
		/*
		if (Act != null){
			rowView.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					Hashtable<String, String> params = new Hashtable<String, String>();
	    			params.put("location_id", String.valueOf(list.get(position).location_id));
	    			String result = RetrieveData.requestMethod(RetrieveData.UPDATE_USER_LOCATION, params);
	    			Log.i("update user location: ", result);
				}
		    }); 
		}
		*/
		return rowView;
	}
	
}
