package rewards;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gotoohlala.R;

import datastorage.StringLanguageSelector;

public class RewardsArrayAdapter extends ArrayAdapter<RewardsModel> {
	private final Context context;
	private final Activity parentAct;
	private final List<RewardsModel> list;
	
	static class ViewHolder {
		public ImageView ivRewardsCategories;
		public TextView tvRewardsCategoriesName;
		public RelativeLayout bg;
	}

	public RewardsArrayAdapter(Context context, Activity parentAct, List<RewardsModel> list) {
		super(context, R.layout.rewardsrow, list);
		this.parentAct = parentAct;
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.rewardsrow, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.ivRewardsCategories = (ImageView) rowView.findViewById(R.id.ivRewardsCategories);
			viewHolder.tvRewardsCategoriesName = (TextView) rowView.findViewById(R.id.tvRewardsCategoriesName);
			viewHolder.bg = (RelativeLayout) rowView.findViewById(R.id.bg);
			
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		holder.ivRewardsCategories.setImageBitmap(null);
		holder.tvRewardsCategoriesName.setText("");	
		
		if (list.get(position).image_bitmap != null){
			//image save into the memory and cache
			holder.ivRewardsCategories.setImageBitmap(list.get(position).image_bitmap);
		}
		
		//Log.i("language of the phone", Locale.getDefault().getDisplayLanguage());
		holder.tvRewardsCategoriesName.setText(StringLanguageSelector.retrieveString(list.get(position).name));
		
		//Log.i("display thread: ", rowView.toString());
		
		if (parentAct != null){
			holder.bg.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					Bundle extras = new Bundle();
					extras.putString("name", String.valueOf((list.get(position).name)));
					extras.putString("category_id", String.valueOf((list.get(position).category_id)));
					extras.putDouble("lat", list.get(position).lat);
					extras.putDouble("longi", list.get(position).longi);
					Intent i = new Intent(parentAct, RewardsCategoryStores.class);
					i.putExtras(extras);
					parentAct.startActivity(i);
					parentAct.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
		    }); 
		}
		
		return rowView;
	}
	
	
}
