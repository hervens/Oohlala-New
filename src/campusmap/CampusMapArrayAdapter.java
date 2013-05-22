package campusmap;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.gotoohlala.R;

import datastorage.CacheInternalStorage;
import datastorage.Fonts;
import datastorage.MemoryStorage;

public class CampusMapArrayAdapter extends ArrayAdapter<CampusMapModel> implements Filterable {
	private final Context context;
	private List<CampusMapModel> list = new ArrayList<CampusMapModel>();
	private List<CampusMapModel> templist = new ArrayList<CampusMapModel>();
	private List<CampusMapModel> filtered = new ArrayList<CampusMapModel>();
	private Filter filter;
	
	static class ViewHolder {
		public TextView tvLocationName;
	}

	public CampusMapArrayAdapter(Context context, List<CampusMapModel> list) {
		super(context, R.layout.locationrow, list);
		this.filtered.addAll(list);
		this.templist = list;
		this.list.addAll(list);
		this.context = context;
		this.filter = new listFilter();
	}
	
	public Filter getFilter() {
        if (filter == null) {
        	filter = new listFilter();
        }
        return filter;
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
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		holder.tvLocationName.setText("");
			
		holder.tvLocationName.setTypeface(Fonts.getOpenSansBold(context));
		holder.tvLocationName.setText(templist.get(position).name);
		
		return rowView;
	}
	
	private class listFilter extends Filter{
		
		protected FilterResults performFiltering(CharSequence constraint) {
            // NOTE: this function is *always* called from a background thread, and
            // not the UI thread.
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0) {
            	List<CampusMapModel> filt = new ArrayList<CampusMapModel>();
            	List<CampusMapModel> lItems = new ArrayList<CampusMapModel>();
                synchronized (this){
                    lItems.addAll(list);
                }
                for(int i = 0, l = lItems.size(); i < l; i++){
                	CampusMapModel m = lItems.get(i);
                    if(m.name.toLowerCase().contains(constraint)){
                        filt.add(m);
                    }
                }
                result.count = filt.size();
                result.values = filt;
            } else {
            	List<CampusMapModel> emptyList = new ArrayList<CampusMapModel>();
                synchronized(this){
                	result.count = emptyList.size();
                    result.values = emptyList;
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.
            filtered = (List<CampusMapModel>)results.values;
            templist.clear();
            
            for(int i = 0, l = filtered.size(); i < l; i++){
            	templist.add(filtered.get(i));
            	//Log.i("templist name", filtered.get(i).name);
            }
            //notifyDataSetInvalidated();
            notifyDataSetChanged();
        }

    }
        
}
