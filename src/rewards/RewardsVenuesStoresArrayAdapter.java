package rewards;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import profile.Friends;

import user.Profile;

import network.ErrorCodeParser;
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
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.gotoohlala.R;

import datastorage.CacheInternalStorage;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.MemoryStorage;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.RoundedCornerImage;
import datastorage.StringLanguageSelector;
import datastorage.TimeCounter;
import events.EventsEventsDetail;

public class RewardsVenuesStoresArrayAdapter extends ArrayAdapter<RewardsVenuesStoresModel> {
	private final Context context;
	private final List<RewardsVenuesStoresModel> list;
	
	static class ViewHolder {
		public ImageView ivThumb;
		public TextView title, tvTime;
		public Button bAttending;
		public TextView startTime;
		public RelativeLayout bg;
	}

	public RewardsVenuesStoresArrayAdapter(Context context, List<RewardsVenuesStoresModel> list) {
		super(context, R.layout.eventseventsrow, list);
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.eventseventsrow, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.ivThumb = (ImageView) rowView.findViewById(R.id.ivThumb);
			viewHolder.title = (TextView) rowView.findViewById(R.id.title);
			viewHolder.bAttending = (Button) rowView.findViewById(R.id.bAttending);
			viewHolder.startTime = (TextView) rowView.findViewById(R.id.startTime);
			viewHolder.tvTime = (TextView) rowView.findViewById(R.id.tvTime);
			viewHolder.bg = (RelativeLayout) rowView.findViewById(R.id.bg);
			
			rowView.setTag(viewHolder);
		}
		
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		holder.ivThumb.setImageBitmap(null);
		holder.title.setText("");	
		holder.bAttending.setText("");
		holder.bAttending.setPadding(0, 0, 0, 0);
		holder.bAttending.setBackgroundResource(R.drawable.button_attending);
		holder.startTime.setText("");
		holder.tvTime.setVisibility(View.GONE);
		
		if (list.get(position).bitmap != null){
			//image save into the memory and cache
			holder.ivThumb.setImageBitmap(RoundedCornerImage.getRoundedCornerBitmap(ImageLoader.ImageCrop(list.get(position).bitmap), 8));
		}
		
		holder.title.setTypeface(Fonts.getOpenSansBold(context));
		holder.title.setText(StringLanguageSelector.retrieveString(list.get(position).title));	
		
		if (list.get(position).user_attend == 1){
			holder.bAttending.setBackgroundResource(R.drawable.button_attended);
			holder.bAttending.setPadding(70, 0, 0, 0);
			holder.bAttending.setText(list.get(position).attends + context.getString(R.string._Attending));
		} else {
			holder.bAttending.setText(list.get(position).attends + context.getString(R.string._Attending));
		}
		
		holder.bAttending.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (list.get(position).user_attend == 0){
					RestClient result = null;
					try {
						result = new Rest.requestBody().execute(Rest.EVENT + list.get(position).event_id, Rest.OSESS + Profile.sk, Rest.PUT, "1", "attend", "1").get();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//Log.i("user attend event", result.getResponse());
					
					int code = result.getResponseCode();
					
					if (code == 200) {
						list.get(position).attends++;
						holder.bAttending.setBackgroundResource(R.drawable.button_attended);
						holder.bAttending.setPadding(70, 0, 0, 0);
						holder.bAttending.setText(list.get(position).attends + context.getString(R.string._Attending));
						list.get(position).user_attend = 1;
					}
				}
			}
		});
		
		holder.startTime.setTypeface(Fonts.getOpenSansLightItalic(context));
		holder.startTime.setText(TimeCounter.EventDate(Integer.valueOf(list.get(position).start_time)));
		
		holder.bg.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Bundle extras = new Bundle();
				extras.putString("event_id", list.get(position).event_id);
				extras.putString("title", list.get(position).title);
				extras.putString("description", list.get(position).description);
				extras.putString("store_logo", list.get(position).store_logo);
				extras.putString("start_time", list.get(position).start_time);
				extras.putString("end_time", list.get(position).end_time);
				extras.putString("store_id", list.get(position).store_id);
				extras.putInt("user_like", list.get(position).user_like);
				extras.putString("image", list.get(position).image);
				extras.putInt("user_attend", list.get(position).user_attend);
				
				Intent i = new Intent(context, EventsEventsDetail.class);
				i.putExtras(extras);
				context.startActivity(i);
			}
	    }); 
		
		return rowView;
	}
	
	
}
