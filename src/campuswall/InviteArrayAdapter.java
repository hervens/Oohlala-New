package campuswall;

import java.util.Hashtable;
import java.util.List;

import network.ErrorCodeParser;
import network.RetrieveData;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gotoohlala.R;

public class InviteArrayAdapter extends ArrayAdapter<InviteModel> {
	private final Context context;
	private final Activity Act;
	private final List<InviteModel> list;
	
	static class ViewHolder {
		public ImageView pic;
		public TextView name;
	}

	public InviteArrayAdapter(Context context, Activity Act, List<InviteModel> list) {
		super(context, R.layout.campuswallinviterow, list);
		this.Act = Act;
		this.context = context;
		this.list = list;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.campuswallinviterow, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.pic = (ImageView) rowView.findViewById(R.id.ivThumb);
			viewHolder.name = (TextView) rowView.findViewById(R.id.tvName);
			
			rowView.setTag(viewHolder);
		}
		
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		holder.pic.setBackgroundDrawable(null);
		holder.name.setText("");
		
		holder.pic.setBackgroundDrawable(list.get(position).pic);
		holder.name.setText(list.get(position).name);
		
		return rowView;
	}
	
}
