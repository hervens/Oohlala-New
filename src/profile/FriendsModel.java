package profile;

import java.util.List;

import datastorage.TimeCounter;

import events.ClassEventsTimesModel;
import android.graphics.Bitmap;

public class FriendsModel {
	public int user_id;
	public String name, avatar_thumb_url, looking_for;
	public Bitmap image_bitmap;
	public boolean has_schedule;
	public List<ClassEventsTimesModel> class_times, event_times;
	public ClassEventsTimesModel class_times_show = new ClassEventsTimesModel(0, 0);
	int time1 = 28800;
	int time2 = 32400;
	int time3 = 36000;
	int time4 = 39600;
	int time5 = 43200;
	int time6 = 46800;
	int time7 = 50400;
	int time8 = 54000;
	int time9 = 57600;
	int time10 = 61200;
	int time11 = 64800;
	int time12 = 68400;
	int time13 = 72000;
	int time14 = 75600;
	
	public boolean[] timesClass = {false, false, false, false, false, false, false, false, false, false, false, false, false};
	public boolean[] timesEvent = {false, false, false, false, false, false, false, false, false, false, false, false, false};
	public boolean[] timesNow = {false, false, false, false, false, false, false, false, false, false, false, false, false};
	
	public boolean inClass = false;
	public boolean busyNow = false;
	
	public FriendsModel(int user_id, String name, String avatar_thumb_url, String looking_for, boolean has_schedule, 
			List<ClassEventsTimesModel> class_times, List<ClassEventsTimesModel> event_times) {
		this.user_id = user_id;
		this.name = name;
		this.avatar_thumb_url = avatar_thumb_url;
		this.looking_for = looking_for;
		this.has_schedule = has_schedule;
		this.class_times = class_times;
		this.event_times = event_times;
		
		//----------------------time now--------------------
		int now = TimeCounter.getSecondsFromMidnightToNow();
		int timeNow = -1;
		
		if (now > time1 && now <= time2){
			timeNow = 0;
		} else if (now > time2 && now <= time3){
			timeNow = 1;
		} else if (now > time3 && now <= time4){
			timeNow = 2;
		} else if (now > time4 && now <= time5){
			timeNow = 3;
		} else if (now > time5 && now <= time6){
			timeNow = 4;
		} else if (now > time6 && now <= time7){
			timeNow = 5;
		} else if (now > time7 && now <= time8){
			timeNow = 6;
		} else if (now > time8 && now <= time9){
			timeNow = 7;
		} else if (now > time9 && now <= time10){
			timeNow = 8;
		} else if (now > time10 && now <= time11){
			timeNow = 9;
		} else if (now > time11 && now <= time12){
			timeNow = 10;
		} else if (now > time12 && now <= time13){
			timeNow = 11;
		} else if (now > time13 && now <= time14){
			timeNow = 12;
		}
		
		if (timeNow != -1){
			timesNow[timeNow] = true;
		}
		
		//-----------------class times-----------------
		if (class_times != null){
			for (int i = 0; i < class_times.size(); i++){
				
				int start = 0;
				int end = 0;
				
				if (class_times.get(i).start_time < time1){
					start = 0;
				} else if (class_times.get(i).start_time >= time1 && 
						class_times.get(i).start_time < time2){
					start = 0;
				} else if (class_times.get(i).start_time >= time2 && 
						class_times.get(i).start_time < time3){
					start = 1;
				} else if (class_times.get(i).start_time >= time3 && 
						class_times.get(i).start_time < time4){
					start = 2;
				} else if (class_times.get(i).start_time >= time4 && 
						class_times.get(i).start_time < time5){
					start = 3;
				} else if (class_times.get(i).start_time >= time5 && 
						class_times.get(i).start_time < time6){
					start = 4;
				} else if (class_times.get(i).start_time >= time6 && 
						class_times.get(i).start_time < time7){
					start = 5;
				} else if (class_times.get(i).start_time >= time7 && 
						class_times.get(i).start_time < time8){
					start = 6;
				} else if (class_times.get(i).start_time >= time8 && 
						class_times.get(i).start_time < time9){
					start = 7;
				} else if (class_times.get(i).start_time >= time9 && 
						class_times.get(i).start_time < time10){
					start = 8;
				} else if (class_times.get(i).start_time >= time10 && 
						class_times.get(i).start_time < time11){
					start = 9;
				} else if (class_times.get(i).start_time >= time11 && 
						class_times.get(i).start_time < time12){
					start = 10;
				} else if (class_times.get(i).start_time >= time12 && 
						class_times.get(i).start_time < time13){
					start = 11;
				} else if (class_times.get(i).start_time >= time13 && 
						class_times.get(i).start_time < time14){
					start = 12;
				} 
				
				if (class_times.get(i).end_time > time1 && 
						class_times.get(i).end_time <= time2){
					end = 0;
				} else if (class_times.get(i).end_time > time2 && 
						class_times.get(i).end_time <= time3){
					end = 1;
				} else if (class_times.get(i).end_time > time3 && 
						class_times.get(i).end_time <= time4){
					end = 2;
				} else if (class_times.get(i).end_time > time4 && 
						class_times.get(i).end_time <= time5){
					end = 3;
				} else if (class_times.get(i).end_time > time5 && 
						class_times.get(i).end_time <= time6){
					end = 4;
				} else if (class_times.get(i).end_time > time6 && 
						class_times.get(i).end_time <= time7){
					end = 5;
				} else if (class_times.get(i).end_time > time7 && 
						class_times.get(i).end_time <= time8){
					end = 6;
				} else if (class_times.get(i).end_time > time8 && 
						class_times.get(i).end_time <= time9){
					end = 7;
				} else if (class_times.get(i).end_time > time9 && 
						class_times.get(i).end_time <= time10){
					end = 8;
				} else if (class_times.get(i).end_time > time10 && 
						class_times.get(i).end_time <= time11){
					end = 9;
				} else if (class_times.get(i).end_time > time11 && 
						class_times.get(i).end_time <= time12){
					end = 10;
				} else if (class_times.get(i).end_time > time12 && 
						class_times.get(i).end_time <= time13){
					end = 11;
				} else if (class_times.get(i).end_time > time13 && 
						class_times.get(i).end_time <= time14){
					end = 12;
				} else if (class_times.get(i).end_time > time14){
					end = 12;
				} 
				
				for (int j = start; j <= end; j++){
					timesClass[j] = true;
					if (timesNow[j]){
						inClass = true;
					}
				}
			}
		}
		
		//--------------------------event time----------------------------
		if (event_times != null){
			for (int i = 0; i < event_times.size(); i++){
				
				int start = 0;
				int end = 0;
				
				if (event_times.get(i).start_time < time1){
					start = 0;
				} else if (event_times.get(i).start_time >= time1 && 
						event_times.get(i).start_time < time2){
					start = 0;
				} else if (event_times.get(i).start_time >= time2 && 
						event_times.get(i).start_time < time3){
					start = 1;
				} else if (event_times.get(i).start_time >= time3 && 
						event_times.get(i).start_time < time4){
					start = 2;
				} else if (event_times.get(i).start_time >= time4 && 
						event_times.get(i).start_time < time5){
					start = 3;
				} else if (event_times.get(i).start_time >= time5 && 
						event_times.get(i).start_time < time6){
					start = 4;
				} else if (event_times.get(i).start_time >= time6 && 
						event_times.get(i).start_time < time7){
					start = 5;
				} else if (event_times.get(i).start_time >= time7 && 
						event_times.get(i).start_time < time8){
					start = 6;
				} else if (event_times.get(i).start_time >= time8 && 
						event_times.get(i).start_time < time9){
					start = 7;
				} else if (event_times.get(i).start_time >= time9 && 
						event_times.get(i).start_time < time10){
					start = 8;
				} else if (event_times.get(i).start_time >= time10 && 
						event_times.get(i).start_time < time11){
					start = 9;
				} else if (event_times.get(i).start_time >= time11 && 
						event_times.get(i).start_time < time12){
					start = 10;
				} else if (event_times.get(i).start_time >= time12 && 
						event_times.get(i).start_time < time13){
					start = 11;
				} else if (event_times.get(i).start_time >= time13 && 
						event_times.get(i).start_time < time14){
					start = 12;
				} 
				
				if (event_times.get(i).end_time > time1 && 
						event_times.get(i).end_time <= time2){
					end = 0;
				} else if (event_times.get(i).end_time > time2 && 
						event_times.get(i).end_time <= time3){
					end = 1;
				} else if (event_times.get(i).end_time > time3 && 
						event_times.get(i).end_time <= time4){
					end = 2;
				} else if (event_times.get(i).end_time > time4 && 
						event_times.get(i).end_time <= time5){
					end = 3;
				} else if (event_times.get(i).end_time > time5 && 
						event_times.get(i).end_time <= time6){
					end = 4;
				} else if (event_times.get(i).end_time > time6 && 
						event_times.get(i).end_time <= time7){
					end = 5;
				} else if (event_times.get(i).end_time > time7 && 
						event_times.get(i).end_time <= time8){
					end = 6;
				} else if (event_times.get(i).end_time > time8 && 
						event_times.get(i).end_time <= time9){
					end = 7;
				} else if (event_times.get(i).end_time > time9 && 
						event_times.get(i).end_time <= time10){
					end = 8;
				} else if (event_times.get(i).end_time > time10 && 
						event_times.get(i).end_time <= time11){
					end = 9;
				} else if (event_times.get(i).end_time > time11 && 
						event_times.get(i).end_time <= time12){
					end = 10;
				} else if (event_times.get(i).end_time > time12 && 
						event_times.get(i).end_time <= time13){
					end = 11;
				} else if (event_times.get(i).end_time > time13 && 
						event_times.get(i).end_time <= time14){
					end = 12;
				} else if (event_times.get(i).end_time > time14){
					end = 12;
				} 
				
				for (int j = start; j <= end; j++){
					timesEvent[j] = true;
					if (timesNow[j]){
						busyNow = true;
					}
				}
			}
		}
	}
	
}
