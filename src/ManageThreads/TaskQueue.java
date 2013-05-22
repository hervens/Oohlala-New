package ManageThreads;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import campusgame.CampusGameModel;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class TaskQueue {
	private static LinkedList<Runnable> tasks = new LinkedList<Runnable>();
    private static Thread thread;
    private static boolean running;
    private static Runnable internalRunnable = new InternalRunnable();
    
    private final static String ME = "TaskQueue";
    
    static Handler mHandler = new Handler(Looper.getMainLooper());
   
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
  
   public static void addTask(Runnable task) {
     Log.d(ME, "addTask-------------------------------");
      synchronized(tasks) {
          tasks.addFirst(task); //stack
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
        return tasks.removeFirst();
      }
    }
   
   
    private static void internalRun() {
    	while(running) {
    		Runnable task = getNextTask();
    		runThread(task);
    		task = null;
    	}
    }
    
    private static void runThread(final Runnable task) {
    	mHandler.post(new Runnable() {
    		public void run() {
		        try {
		        	task.run();
		        	Thread.yield();
		        } catch (Throwable t) {
		          Log.e(ME, "Task threw an exception", t);
		        }
    		}
	  	});
    }
 }

  