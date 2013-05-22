package ManageThreads;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import datastorage.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class TaskQueueImage2 {
	private static LinkedList<Runnable> tasksDouble = new LinkedList<Runnable>();
	private static LinkedList<Thread> tasksSingle = new LinkedList<Thread>();
    private static Thread thread;
    private static boolean running;
    private static Runnable internalRunnable = new InternalRunnable();
    
    private final static String ME = "TaskQueueImage2";
    
    static Handler mHandler = new Handler(Looper.getMainLooper());
    
    static int max = 5;
   
    private static class InternalRunnable implements Runnable {
    	public void run() {
    		internalRun();
    	}
    }
   
    public static void start() {
      if (!running) {
        thread = new Thread(internalRunnable);
        thread.setDaemon(true);
        running = true;
        thread.start();
      }
    }
   
    public static void stop() {
      running = false;
    }
  
   public static void addTask(Thread task) {
	     Log.d(ME, "addTask2-------------------------------");
	     synchronized(tasksDouble) {
	    	 if (tasksSingle.size() >= max){
	    		 LinkedList<Thread> tasksSingleTemp = new LinkedList<Thread>();
	    		 tasksSingleTemp.addAll(tasksSingle);
	    		 
		    	 tasksDouble.addLast(new loadBitmapThread(tasksSingleTemp));
		    	 tasksDouble.notify(); // notify any waiting threads  
		    	 
		    	 tasksSingle.clear();
		    	 tasksSingle.add(task);
		    	 
		    	 tasksSingleTemp = null;
	    	 } else {
	    		 tasksSingle.add(task);
	    	 }
	     }
    }
   
    private static Runnable getNextTask() {
		Log.d(ME, "getNextTask2 ------------------------------- " + tasksDouble.size());
	 	synchronized(tasksDouble) {
	        if (tasksDouble.isEmpty()) {
	          try {
	        	  tasksDouble.wait();
	          } catch (InterruptedException e) {
	            Log.e(ME, "Task interrupted", e);
	            stop();
	          }
	        }
	        return tasksDouble.removeFirst();
	  	}
    }
   
   
    private static void internalRun() {
    	while(running) {
    		Runnable task = getNextTask();
	  	  	try {
	  	  		task.run();
				Thread.yield();
			} catch (Throwable t) {
				Log.e(ME, "Task threw an exception", t);
			}	
	  	  	task = null;
    	}
    }
    
    static class loadBitmapThread extends Thread {
	    // This method is called when the thread runs
    	LinkedList<Thread> tasks = new LinkedList<Thread>();
		
		public loadBitmapThread(LinkedList<Thread> tasks){
			this.tasks.addAll(tasks);
		}

		public void run() {
			for (int i = 0; i < tasks.size(); i++){
    			tasks.get(i).start();
    		}
			tasks = null;
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
    
    
 }

  