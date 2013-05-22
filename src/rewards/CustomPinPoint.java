package rewards;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class CustomPinPoint extends ItemizedOverlay<OverlayItem>{
	
	private ArrayList<OverlayItem> pinpoints = new ArrayList<OverlayItem>();
	private Context c;
	
	public CustomPinPoint(Drawable m, Context context) {
		this(m);
		c = context;
		// TODO Auto-generated constructor stub
	}
	
	public CustomPinPoint(Drawable defaultMarker) {
		super(boundCenter(defaultMarker));
		// TODO Auto-generated constructor stub
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return pinpoints.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return pinpoints.size();
	}
	
	public void insertPinPoint(OverlayItem item){
		pinpoints.add(item);
		this.populate();
	}
	
	public void clearPinPoint(){
		pinpoints.clear();
		this.populate();
	}

}
