package ManageThreads;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import datastorage.CacheInternalStorage;
import datastorage.UserLoginInfo;
import datastorage.UserStartTime;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class TaskQueueImage {
    private static LinkedList<Runnable> tasks = new LinkedList<Runnable>();
    private static Thread thread;
    private static boolean running;
    private static Runnable internalRunnable = new InternalRunnable();
    
    private final static String ME = "TaskQueueImage";
    
    static Handler mHandler = new Handler(Looper.getMainLooper());
    
    public static int num3 = 1;
    static Runnable task_now = new Thread();
    
    static Context ctx;
   
    private static class InternalRunnable implements Runnable {
    	public void run() {
    		internalRun();
    	}
    }
   
    public static void start(Context c) {
    	if (!running) {
	        thread = new Thread(internalRunnable);
	        thread.setDaemon(true);
	        running = true;
	        thread.start();
      	}
    	
    	ctx = c;
    }
   
    public static void stop() {
    	running = false;
    }
  
    public static void addTask(Runnable task, Context c) {
    	Log.d(ME, "addTask-------------------------------" + String.valueOf(running));
    	running = true;
    	ctx = c;
    	
    	if (ctx != null){
	    	UserStartTime ust = CacheInternalStorage.getCacheStartTime(ctx);
	    	if (ust != null){
		      	if (ust.startTime > 0){
		      		if (System.currentTimeMillis() - ust.startTime > 5000){
			  			Log.d("startTime", "-------------too long--------");
			  			if (task_now != null){
			  				Log.d("task_now", "----------start---stop-----------");
			  				stop();
			  				start(ctx);
			  			}
			    	}
		      	}
	    	}
    	}
     
    	synchronized(tasks) {
    		tasks.addFirst(task); //turn the queue into a stack
    		tasks.notify(); // notify any waiting threads           
    	}
    }
   
    private static Runnable getNextTask() {
		Log.d(ME, "getNextTask-------------------------------");
	 	synchronized(tasks) {
	        if (tasks.isEmpty()) {
	        	try {
	        		tasks.wait();
	        	} catch (InterruptedException e) {
	        		Log.e(ME, "Task interrupted", e);
	        		stop();
	        	}
	        }
	        
	        if (tasks.isEmpty()) {
	        	return null;
	        } else {
	        	return tasks.removeFirst();
	        }
	  	}
    }
   
   
    private static void internalRun() {
    	while(running) {
    		task_now = getNextTask();
	  	  	try {
	  	  		CacheInternalStorage.cacheStartTime(new UserStartTime(System.currentTimeMillis()), ctx); //cache user info
			
	  	  		task_now.run();
				Thread.yield();
			} catch (Throwable t) {
				Log.e(ME, "Task threw an exception", t);
			}	
	  	  	task_now = null;
    	}
    }
 }

  