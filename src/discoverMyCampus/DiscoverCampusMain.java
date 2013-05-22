package discoverMyCampus;
import com.gotoohlala.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.support.v4.app.FragmentActivity;

public class DiscoverCampusMain extends FrameLayout {

	public static TextProgressBar textProgressBar;
	View v = null;
	
	
	public DiscoverCampusMain( Context context) 
	{
		super(context);
		
		final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
		 v = (RelativeLayout) mLayoutInflater.inflate(R.layout.activity_discover_campus_main, null);  
	     addView(v);  
	     
		
		//Set up ProgressBar
		 textProgressBar = (TextProgressBar) findViewById(R.id.progressBarWithText);
		    textProgressBar.setText("Progress");
		    textProgressBar.setProgress(10);
		    textProgressBar.setTextSize(18);
		    
		 new RulesFragment(context);
		
		
	}
	
	public void onDiscoverClick(View view)
	{

	}
	
	
}
