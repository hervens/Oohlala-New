package campuswall;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.gotoohlala.R;

public class InviteFacebookArrayAdapter extends ArrayAdapter<InviteFacebookModel> implements Filterable {
	private final Context context;
	private final Activity Act;
	private List<InviteFacebookModel> list = new ArrayList<InviteFacebookModel>();
	private List<InviteFacebookModel> templist = new ArrayList<InviteFacebookModel>();
	private List<InviteFacebookModel> filtered = new ArrayList<InviteFacebookModel>();
	private Filter filter;
	
	static class ViewHolder {
		public TextView name;
	}

	public InviteFacebookArrayAdapter(Context context, Activity Act, List<InviteFacebookModel> list) {
		super(context, R.layout.campuswallinvitefacebookrow, list);
		this.Act = Act;
		this.context = context;
		this.filtered.addAll(list);
		this.templist = list;
		this.list.addAll(list);
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
			rowView = inflater.inflate(R.layout.campuswallinvitefacebookrow, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = (TextView) rowView.findViewById(R.id.tvName);
			
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		holder.name.setText("");
		
		holder.name.setText(templist.get(position).name);
		
		return rowView;
	}
	
	private class listFilter extends Filter{
		
		protected FilterResults performFiltering(CharSequence constraint) {
            // NOTE: this function is *always* called from a background thread, and
            // not the UI thread.
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0) {
            	List<InviteFacebookModel> filt = new ArrayList<InviteFacebookModel>();
            	List<InviteFacebookModel> lItems = new ArrayList<InviteFacebookModel>();
                synchronized (this){
                	lItems.addAll(list);
                }
                for(int i = 0, l = lItems.size(); i < l; i++){
                	InviteFacebookModel m = lItems.get(i);
                    if(m.name.toLowerCase().contains(constraint)){
                        filt.add(m);
                    }
                }
                result.count = filt.size();
                result.values = filt;
            } else {
            	//List<InviteFacebookModel> emptyList = new ArrayList<InviteFacebookModel>();
                synchronized(this){
                	result.count = list.size();
                    result.values = list;
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.
            filtered = (List<InviteFacebookModel>)results.values;
            templist.clear();
            
            for(int i = 0, l = filtered.size(); i < l; i++){
            	templist.add(filtered.get(i));
            	//Log.i("templist name", filtered.get(i).name);
            }
            
            notifyDataSetChanged();
        }

    }
	
}
