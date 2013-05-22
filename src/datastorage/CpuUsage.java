package datastorage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Debug;
import android.util.Log;

public class CpuUsage {
	
	public static int MAX_USED_MEMORY_SIZE = 45;
	
	public static float readUsage() {
	    try {
	        RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
	        String load = reader.readLine();

	        String[] toks = load.split(" ");

	        long idle1 = Long.parseLong(toks[5]);
	        long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
	              + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

	        try {
	            Thread.sleep(360);
	        } catch (Exception e) {}

	        reader.seek(0);
	        load = reader.readLine();
	        reader.close();

	        toks = load.split(" ");

	        long idle2 = Long.parseLong(toks[5]);
	        long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
	            + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

	        return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }

	    return 0;
	} 
	
	public static long readRam(Context c){
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) c.getSystemService(c.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		long availableMegs = mi.availMem / 1048576L;
		
		return availableMegs;
	}
	
	public static void logHeap() {
        Double allocated = new Double(Debug.getNativeHeapAllocatedSize())/new Double((1048576));
        Double available = new Double(Debug.getNativeHeapSize())/1048576.0;
        Double free = new Double(Debug.getNativeHeapFreeSize())/1048576.0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        Log.d("tag", "debug. =================================");
        Log.d("tag", "debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free)");
        Log.d("tag", "debug.memory: allocated: " + df.format(new Double(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(new Double(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(new Double(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");
        
        if ((int)(Runtime.getRuntime().totalMemory()/1048576) > 40){
        	System.gc();
        }
    }
	
	public static int getUsedMemorySize() {
	    long freeSize = 0L;
	    long totalSize = 0L;
	    long usedSize = -1L;
	    
	    try {
	        Runtime info = Runtime.getRuntime();
	        freeSize = info.freeMemory();
	        totalSize = info.totalMemory();
	        usedSize = totalSize - freeSize;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    Log.d("tag", "debug. =================================");
        Log.d("tag", "debug.memory used: " + usedSize/1048576 + "MB");
        
	    return (int)usedSize/1048576;
	}
	
}
