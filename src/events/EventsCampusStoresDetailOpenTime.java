package events;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gotoohlala.R;

public class EventsCampusStoresDetailOpenTime extends Activity {

	String[] open_time;
	TextView tvOpentime0, tvOpentime1, tvOpentime2, tvOpentime3, tvOpentime4, tvOpentime5, tvOpentime6;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventscampusstoresdetailopentime);
		
		Bundle b = this.getIntent().getExtras();
		open_time = b.getStringArray("open_time");
		
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
	    }); 
		
		tvOpentime0 = (TextView) findViewById(R.id.tvOpentime0);
		tvOpentime1 = (TextView) findViewById(R.id.tvOpentime1);
		tvOpentime2 = (TextView) findViewById(R.id.tvOpentime2);
		tvOpentime3 = (TextView) findViewById(R.id.tvOpentime3);
		tvOpentime4 = (TextView) findViewById(R.id.tvOpentime4);
		tvOpentime5 = (TextView) findViewById(R.id.tvOpentime5);
		tvOpentime6 = (TextView) findViewById(R.id.tvOpentime6);
		
		tvOpentime0.setText(getString(R.string.Monday_) + open_time[0]);
		tvOpentime1.setText(getString(R.string.Tuesday_) + open_time[1]);
		tvOpentime2.setText(getString(R.string.Wednesday_) + open_time[2]);
		tvOpentime3.setText(getString(R.string.Thursday_) + open_time[3]);
		tvOpentime4.setText(getString(R.string.Friday_) + open_time[4]);
		tvOpentime5.setText(getString(R.string.Saturday_) + open_time[5]);
		tvOpentime6.setText(getString(R.string.Sunday_) + open_time[6]);
	}
	
}
