package ManageThreads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.CountDownTimer;
import android.util.Log;

public class TaskQueueThread extends Thread {
	
	long startTime;
	TimeCounter counter;
	ExecutorService executor = Executors.newSingleThreadExecutor();
	
	public TaskQueueThread(){
		startTime = System.currentTimeMillis();
		Log.d("startTime", "-------------startTime--------");
		
		counter = new TimeCounter(10000, 1000);
		counter.start();
		
		executor.submit(Thread.currentThread());
	}
	
	public void run() {
		
	}
	
	class TimeCounter extends CountDownTimer {
        public TimeCounter(long millisInFuture, long countDownInterval) {
        	super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            //dialog.dismiss();
           // Use Intent to Navigate from this activity to another
        }

        @Override
        public void onTick(long millisUntilFinished) {
        	if (System.currentTimeMillis() - startTime > 5000){
	  			Log.d("startTime", "-------------too long--------");
	  			Thread.currentThread().interrupt();
	  			
	  			Log.d("currentThread", "isInterrupted: " + Thread.currentThread().isInterrupted());
	  			Log.d("currentThread", "isAlive: " + Thread.currentThread().isAlive());
	  			Log.d("currentThread", "state: " + Thread.currentThread().getState());
	  			return;
	  		}
        }
	}
}
