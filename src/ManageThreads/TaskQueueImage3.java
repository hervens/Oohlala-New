package ManageThreads;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class TaskQueueImage3 {
    private static LinkedList<Runnable> tasks1 = new LinkedList<Runnable>();
    private static LinkedList<Runnable> tasks2 = new LinkedList<Runnable>();
    private static LinkedList<Runnable> tasks3 = new LinkedList<Runnable>();
    private static LinkedList<Runnable> tasks4 = new LinkedList<Runnable>();
    private static LinkedList<Runnable> tasks5 = new LinkedList<Runnable>();
    private static int num;
    private static Thread thread;
    private static boolean running;
    private static Runnable internalRunnable = new InternalRunnable();
    
    private final static String ME = "TaskQueueImage3";
    
    static Handler mHandler = new Handler(Looper.getMainLooper());
   
    private static class InternalRunnable implements Runnable {
    	public void run() {
    		internalRun();
    	}
    }
   
    public static void start() {
      if (!running) {
    	  	num = 1;
	        thread = new Thread(internalRunnable);
	        thread.setDaemon(true);
	        running = true;
	        thread.start();
      	}
    }
   
    public static void stop() {
    	running = false;
    }
    
    public static int getNum(){
    	return num;
    }
  
   public static void addTask(Runnable task) {
	   Log.d(ME, "addTask------------------------------- " + getNum());
	   if (num == 1){ 
			num = 2;
			Log.d(ME, "change num to " + num);
			synchronized(tasks1) {
				tasks1.addFirst(task); //turn the queue into a stack
				tasks1.notify(); // notify any waiting threads           
			}
	   } else if (num == 2){
			num = 3;
			synchronized(tasks2) {
				tasks2.addFirst(task); //turn the queue into a stack
				tasks2.notify(); // notify any waiting threads           
			}
	   } else if (num == 3){
			num = 1;
			/*
			synchronized(tasks3) {
				tasks3.addFirst(task); //turn the queue into a stack
				tasks3.notify(); // notify any waiting threads           
			}
			*/
	   } 
	   /*
	   else if (num == 4){
			num = 5;
			synchronized(tasks4) {
				tasks4.addFirst(task); //turn the queue into a stack
				tasks4.notify(); // notify any waiting threads           
			}
	   } else if (num == 5){
			num = 1;
			synchronized(tasks5) {
				tasks5.addFirst(task); //turn the queue into a stack
				tasks5.notify(); // notify any waiting threads           
			}
		}
		*/
   }
   
    private static Runnable getNextTask1() {
		Log.d(ME, "getNextTask------------------------------- tasks1");
	 	synchronized(tasks1) {
	        if (tasks1.isEmpty()) {
	          try {
	            tasks1.wait();
	          } catch (InterruptedException e) {
	            Log.e(ME, "Task interrupted", e);
	            //stop();
	          }
	          
	          return null;
	        }
	        return tasks1.removeFirst();
	  	}
    }
   
    private static Runnable getNextTask2() {
		Log.d(ME, "getNextTask------------------------------- tasks2");
	 	synchronized(tasks2) {
	        if (tasks2.isEmpty()) {
	          try {
	            tasks2.wait();
	          } catch (InterruptedException e) {
	            Log.e(ME, "Task interrupted", e);
	            //stop();
	          }
	          
	          return null;
	        }
	        return tasks2.removeFirst();
	  	}
    }
    
    private static Runnable getNextTask3() {
		Log.d(ME, "getNextTask------------------------------- tasks3");
	 	synchronized(tasks3) {
	        if (tasks3.isEmpty()) {
	          try {
	            tasks3.wait();
	          } catch (InterruptedException e) {
	            Log.e(ME, "Task interrupted", e);
	            //stop();
	          }
	          
	          return null;
	        }
	        return tasks3.removeFirst();
	  	}
    }
    
    private static Runnable getNextTask4() {
		Log.d(ME, "getNextTask------------------------------- tasks4");
	 	synchronized(tasks4) {
	        if (tasks4.isEmpty()) {
	          try {
	            tasks4.wait();
	          } catch (InterruptedException e) {
	            Log.e(ME, "Task interrupted", e);
	            //stop();
	          }
	          
	          return null;
	        }
	        return tasks4.removeFirst();
	  	}
    }
    
    private static Runnable getNextTask5() {
		Log.d(ME, "getNextTask------------------------------- tasks5");
	 	synchronized(tasks5) {
	        if (tasks5.isEmpty()) {
	          try {
	            tasks5.wait();
	          } catch (InterruptedException e) {
	            Log.e(ME, "Task interrupted", e);
	            //stop();
	          }
	          
	          return null;
	        }
	        return tasks5.removeFirst();
	  	}
    }
    
    private static void internalRun() {
    	while(running) {
    		Runnable task1 = getNextTask1();
    		Runnable task2 = getNextTask2();
    		//Runnable task3 = getNextTask3();
    		//Runnable task4 = getNextTask4();
    		//Runnable task5 = getNextTask5();
    		
	  	  	try {
	  	  		if (task1 != null){
	  	  			task1.run();
	  	  		}
		  	  	if (task2 != null){
	  	  			task2.run();
	  	  		}
		  	  	/*
		  	  	if (task3 != null){
	  	  			task3.run();
	  	  		}
		  	  	if (task4 != null){
	  	  			task4.run();
	  	  		}
		  	  	if (task5 != null){
		  	  		task5.run();
		  	  	}
	  			*/
				//Thread.yield();
			} catch (Throwable t) {
				Log.e(ME, "Task threw an exception", t);
			}	
	  	  	
	  	  	task1 = null;
	  	  	task2 = null;
	  	  	//task3 = null;
	  	  	//task4 = null;
	  	  	//task5 = null;
    	}
    }
 }

  