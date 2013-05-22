package datastorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import smackXMPP.XMPPChatModel;

import network.RetrieveData;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class CacheInternalStorage {

	public static void cache(String imageUrl, Context ctx){
		ContextWrapper cw = new ContextWrapper(ctx);
		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
		File mypath = new File(directory, RetrieveData.hash(imageUrl) + ".png");
		FileOutputStream fos = null;
	    try {
	        fos = new FileOutputStream(mypath);
	        // Use the compress method on the BitMap object to write image to the OutputStream
	        Bitmap bitmap = decodeBitmapFromInputStream(imageUrl);
	        if (bitmap != null){
	        	//new BitmapCompress(bitmap, fos).execute();
	            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
	            
	            bitmap.recycle();
            	bitmap = null;
	        } 
	    } catch (FileNotFoundException e) {
	    	e.printStackTrace();
	    }
	    
	    cw = null;
	    directory = null;
	    mypath = null;
	    fos = null;
    }
	
	public static Bitmap decodeBitmapFromInputStream(String imageUrl) {
		Options ThumbOpts = new Options();
		
	    // Calculate inSampleSize
	    ThumbOpts.inSampleSize = 1;
	    
	    // Decode bitmap with inSampleSize set
		ThumbOpts.inJustDecodeBounds = false;
		ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
		ThumbOpts.inInputShareable=true; 
		
		try {
			return new decodeStream(imageUrl, ThumbOpts).execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static void cacheStudentsNearby(String imageUrl, Context ctx, int dps) {		
		ContextWrapper cw = new ContextWrapper(ctx);
        File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
        File mypath = new File(directory, RetrieveData.hash(imageUrl) + ".png");

        FileOutputStream fos = null;
        try {
        	//fos = openFileOutput(filename, Context.MODE_PRIVATE);
            //System.out.println("mypath = " + mypath);
            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            Bitmap bitmap = decodeStudentsNearbyBitmapFromInputStream(imageUrl, ctx, dps);
            if (bitmap != null){
            	//new BitmapCompress(bitmap, fos).execute();
            	bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            	//System.out.println("cache kembrel image = " + mypath);
            	
            	bitmap.recycle();
            	bitmap = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        cw = null;
	    directory = null;
	    mypath = null;
	    fos = null;
    }
	
	private static class BitmapNetwork extends Thread
	{
		private String imageUrl;
		Options ThumbOpts;
		public BitmapNetwork(String imgUrl,Options ThumbOpts)
		{
			imageUrl = imgUrl;
			this.ThumbOpts = ThumbOpts;
		}
		
		public void run()
		{
			try {
				BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent(), null, ThumbOpts);
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public static Bitmap decodeStudentsNearbyBitmapFromInputStream(String imageUrl, Context ctx, int dps) {
		Options ThumbOpts = new Options();
		ThumbOpts.inJustDecodeBounds = true;
		BitmapNetwork net = new BitmapNetwork(imageUrl,ThumbOpts);
		net.start();

	    // Calculate inSampleSize
	    ThumbOpts.inSampleSize = calculateInSampleSize(ThumbOpts, ConvertDpsToPixels.getPixels(dps, ctx), ConvertDpsToPixels.getPixels(dps, ctx));
	    
	    // Decode bitmap with inSampleSize set
		ThumbOpts.inJustDecodeBounds = false;
		ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
		ThumbOpts.inInputShareable=true; 
		
		try {
			return new decodeStream(imageUrl, ThumbOpts).execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static void cacheKembrel(String imageUrl, Context ctx) {
		ContextWrapper cw = new ContextWrapper(ctx);
        File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
        File mypath = new File(directory, RetrieveData.hash(imageUrl) + ".png");

        FileOutputStream fos = null;
        try {
        	//fos = openFileOutput(filename, Context.MODE_PRIVATE);
            //System.out.println("mypath = " + mypath);
            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            Bitmap bitmap = decodeKembrelBitmapFromInputStream(imageUrl, ctx);
            if (bitmap != null){
            	//new BitmapCompress(bitmap, fos).execute();
            	bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            	//System.out.println("cache kembrel image = " + mypath);
            	
            	bitmap.recycle();
            	bitmap = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        cw = null;
	    directory = null;
	    mypath = null;
	    fos = null;
    }
	
	public static Bitmap decodeKembrelBitmapFromInputStream(String imageUrl, Context ctx) {
		Options ThumbOpts = new Options();
		ThumbOpts.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent(), null, ThumbOpts);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	    // Calculate inSampleSize
	    ThumbOpts.inSampleSize = calculateInSampleSize(ThumbOpts, ConvertDpsToPixels.getPixels(50, ctx), ConvertDpsToPixels.getPixels(50, ctx));
	    
	    // Decode bitmap with inSampleSize set
		ThumbOpts.inJustDecodeBounds = false;
		ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
		ThumbOpts.inInputShareable=true; 
		
		try {
			return new decodeStream(imageUrl, ThumbOpts).execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static void cacheChatImage(String imageUrl, Context ctx, int type){
		ContextWrapper cw = new ContextWrapper(ctx);
        File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
        File mypath = new File(directory, RetrieveData.hash(imageUrl) + ".png");

        FileOutputStream fos = null;
        try {
        	//fos = openFileOutput(filename, Context.MODE_PRIVATE);
            //System.out.println("mypath = " + mypath);
            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            Bitmap bitmap = decodeChatImageBitmapFromInputStream(imageUrl, ctx, type);
            if (bitmap != null){
            	//new BitmapCompress(bitmap, fos).execute();
            	bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            	//System.out.println("cache kembrel image = " + mypath);
            	
            	bitmap.recycle();
            	bitmap = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        cw = null;
	    directory = null;
	    mypath = null;
	    fos = null;
    }
	
	public static Bitmap decodeChatImageBitmapFromInputStream(String imageUrl, Context ctx, int type) {
		Options ThumbOpts = new Options();
		ThumbOpts.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent(), null, ThumbOpts);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	    // Calculate inSampleSize
		if (type == 0){
			ThumbOpts.inSampleSize = calculateInSampleSize(ThumbOpts, ConvertDpsToPixels.getPixels(30, ctx), ConvertDpsToPixels.getPixels(30, ctx));
		} else if (type == 1){
			ThumbOpts.inSampleSize = calculateInSampleSize(ThumbOpts, (DeviceDimensions.getWidth(ctx)*4)/10, (DeviceDimensions.getWidth(ctx)*4)/10);
		}
	    
	    // Decode bitmap with inSampleSize set
		ThumbOpts.inJustDecodeBounds = false;
		ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
		ThumbOpts.inInputShareable=true; 
		
		try {
			return new decodeStream(imageUrl, ThumbOpts).execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static Bitmap decodeAvatarBitmapFromInputStream(Uri imageUrl, Context ctx, int dps){
		Options ThumbOpts = new Options();
		ThumbOpts.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(imageUrl), null, ThumbOpts);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	    // Calculate inSampleSize
	    ThumbOpts.inSampleSize = calculateInSampleSize(ThumbOpts, ConvertDpsToPixels.getPixels(dps, ctx), ConvertDpsToPixels.getPixels(dps, ctx));
	    
	    // Decode bitmap with inSampleSize set
		ThumbOpts.inJustDecodeBounds = false;
		ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
		ThumbOpts.inInputShareable=true; 
		
		try {
			return new decodeStreamCtx(imageUrl, ThumbOpts, ctx).execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}	
	}
	
	public static Bitmap decodeCampusWallBitmapFromInputStream(Uri imageUrl, Context ctx, int px){
		Options ThumbOpts = new Options();
		ThumbOpts.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(imageUrl), null, ThumbOpts);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	    // Calculate inSampleSize
	    ThumbOpts.inSampleSize = calculateInSampleSize(ThumbOpts, px, px);
	    
	    // Decode bitmap with inSampleSize set
		ThumbOpts.inJustDecodeBounds = false;
		ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
		ThumbOpts.inInputShareable=true; 
		
		try {
			return new decodeStreamCtx(imageUrl, ThumbOpts, ctx).execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}	
	}
	
	public static class decodeStream extends AsyncTask<Void, Void, Bitmap> {
		String imageUrl;
		Options ops;
		
		public decodeStream(String imageUrl, Options ops){
			this.imageUrl = imageUrl;
			this.ops = ops;
		}
    	
		@Override
		protected Bitmap doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				return BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent(), null, ops);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				return null;
			}
		}
    }
	
	public static class decodeStreamCtx extends AsyncTask<Void, Void, Bitmap> {
		Uri imageUrl;
		Options ops;
		Context ctx;
		
		public decodeStreamCtx(Uri imageUrl, Options ops, Context ctx){
			this.imageUrl = imageUrl;
			this.ops = ops;
			this.ctx = ctx;
		}
    	
		@Override
		protected Bitmap doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				return BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(imageUrl), null, ops);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				return null;
			}	
		}
    }
	
	public static class BitmapCompress extends AsyncTask<Void, Void, Void> {
		Bitmap bitmap;
		FileOutputStream fos;
		
		public BitmapCompress(Bitmap bitmap, FileOutputStream fos){
			this.bitmap = bitmap;
			this.fos = fos;
		}
    	
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				
			return null;
		}
    }
	
	public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        
        //Log.i("InSampleSize", String.valueOf(inSampleSize));
        return inSampleSize;
    }
	
	public static String HashedImageName(String imageUrl){
		return RetrieveData.hash(imageUrl) + ".png";
	}
	
	public static String HashedImageDir(String imageUrl, Context ctx){
		ContextWrapper cw = new ContextWrapper(ctx);
        File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
        File mypath = new File(directory, HashedImageName(imageUrl));
        return mypath.toString();
	}
	
	public static Boolean isImageExist(String imageName, Context ctx){
		ContextWrapper cw = new ContextWrapper(ctx);
		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
		File f = new File(directory, HashedImageName(imageName));
		
		return f.exists();
	}
	
	public static Boolean isImageValid(String imageName, Context ctx){
		ContextWrapper cw = new ContextWrapper(ctx);
		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
		File f = new File(directory, HashedImageName(imageName));
		
		return f.length() > 0 ? true : false;
	}
	
	public static void deleteCache(Context ctx){
		ContextWrapper cw = new ContextWrapper(ctx);
		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
		for (int i = 0; i < directory.listFiles().length; i++){
			Log.i("file name", String.valueOf(directory.listFiles()[i].getName()));
			Log.i("file deleted", String.valueOf(directory.listFiles()[i].delete()));
		}
	}
	
	public static boolean saveObject(List<XMPPChatModel> obj, Context ctx, String name) {
		List<XMPPChatModel> saveList = null;
		ContextWrapper cw = new ContextWrapper(ctx);
		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
        final File suspend_f = new File(directory, name);

        FileOutputStream   fos  = null;
        ObjectOutputStream oos  = null;
        boolean            keep = true;
        
        List<XMPPChatModel> cachedList = null;
        if (CacheInternalStorage.getObject(ctx, name) != null){
      		Log.i("is old object", "yes");
      		cachedList = CacheInternalStorage.getObject(ctx, name);
 	        Log.i("save old object", "yes"); 
      	}
        
        try {
        	fos = new FileOutputStream(suspend_f);
          	oos = new ObjectOutputStream(fos);
          	
          	if (cachedList != null){
          		cachedList.addAll(obj);
              	saveList = cachedList;
          	} else {
          		saveList = obj;
          	}
          	
          	oos.writeObject(saveList);
        	oos.flush();
       	} catch (Exception e) {
       		Log.i("save object", e.toString());
         	keep = false;
       	} finally {
           	try {
           		if (oos != null)   oos.close();
           		if (fos != null)   fos.close();
	        	if (keep == false) suspend_f.delete();
	        	Log.i("save new object", "yes");
	        } catch (Exception e) { 
	        	Log.i("save new object", "no");
	        }         
       	}

        return keep;
	}

	public static List<XMPPChatModel> getObject(Context ctx, String name){
		ContextWrapper cw = new ContextWrapper(ctx);
 		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
 		final File suspend_f = new File(directory, name);

 		List<XMPPChatModel> cachedList = null;
        FileInputStream fis = null;
        ObjectInputStream is = null;
  
        try {
        	fis = new FileInputStream(suspend_f);
        	is = new ObjectInputStream(fis);
        	cachedList = (List<XMPPChatModel>) is.readObject();
        } catch(Exception e) {
            String val= e.getMessage();
        } finally {
        	try {
            	if (fis != null)   fis.close();
            	if (is != null)   is.close();
         	}
        	catch (Exception e) { }
        }

        return cachedList;
	}
	
	public static boolean isObjectExisted(Context ctx, String name){
		boolean found = false;
		ContextWrapper cw = new ContextWrapper(ctx);
 		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
 		final File suspend_f = new File(directory, name);

 		List<XMPPChatModel> cachedList = null;
        FileInputStream fis = null;
        ObjectInputStream is = null;
  
        try {
        	fis = new FileInputStream(suspend_f);
        	is = new ObjectInputStream(fis);
        	cachedList = (List<XMPPChatModel>) is.readObject();
        	found = true;
        } catch(Exception e) {
        } finally {
        	try {
            	if (fis != null)   fis.close();
            	if (is != null)   is.close();
         	}
        	catch (Exception e) { }
        }

        return found;
	}
	
	public static boolean cacheUserLoginInfo(UserLoginInfo obj, Context ctx) {
		ContextWrapper cw = new ContextWrapper(ctx);
		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
        final File suspend_f = new File(directory, "Oohlala_User_Login.cache");

        FileOutputStream   fos  = null;
        ObjectOutputStream oos  = null;
        boolean            keep = true;
        
        UserLoginInfo cachedUser = null;
        
        try {
        	fos = new FileOutputStream(suspend_f);
          	oos = new ObjectOutputStream(fos);
          	
          	cachedUser = obj;
          	
          	oos.writeObject(cachedUser);
        	oos.flush();
       	} catch (Exception e) {
       		Log.i("save user login cache problem", e.toString());
         	keep = false;
       	} finally {
           	try {
           		if (oos != null)   oos.close();
           		if (fos != null)   fos.close();
	        	if (keep == false) suspend_f.delete();
	        	Log.i("save user login cache", "yes");
	        } catch (Exception e) { 
	        	Log.i("save user login cache", "no");
	        }         
       	}

        return keep;
	}

	public static UserLoginInfo getCacheUserLoginInfo(Context ctx){
		ContextWrapper cw = new ContextWrapper(ctx);
 		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
 		final File suspend_f = new File(directory, "Oohlala_User_Login.cache");
 		
 		if (suspend_f.exists()){
	 		UserLoginInfo cachedUser = null;
	        FileInputStream fis = null;
	        ObjectInputStream is = null;
	        
	        try {
	        	fis = new FileInputStream(suspend_f);
	        	is = new ObjectInputStream(fis);
	        	cachedUser = (UserLoginInfo) is.readObject();
	        } catch(Exception e) {
	            String val= e.getMessage();
	        } finally {
	        	try {
	            	if (fis != null)   fis.close();
	            	if (is != null)   is.close();
	         	}
	        	catch (Exception e) { }
	        }
	
	        return cachedUser;
 		} else {
 			return new UserLoginInfo("", "", false);
 		}
	}
	
	public static boolean cacheUserLocation(UserLocation obj, Context ctx) {
		
		ContextWrapper cw = new ContextWrapper(ctx);
		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
        final File suspend_f = new File(directory, "Oohlala_User_Location.cache");

        FileOutputStream   fos  = null;
        ObjectOutputStream oos  = null;
        boolean            keep = true;
        
        UserLocation cachedUser = null;
        
        try {
        	fos = new FileOutputStream(suspend_f);
          	oos = new ObjectOutputStream(fos);
          	
          	cachedUser = obj;
          	
          	oos.writeObject(cachedUser);
        	oos.flush();
       	} catch (Exception e) {
       		Log.i("save user location cache problem", e.toString());
         	keep = false;
       	} finally {
           	try {
           		if (oos != null)   oos.close();
           		if (fos != null)   fos.close();
	        	if (keep == false) suspend_f.delete();
	        	Log.i("save user location cache", "yes");
	        } catch (Exception e) { 
	        	Log.i("save user location cache", "no");
	        }         
       	}

        return keep;
	}

	public static UserLocation getCacheUserLocation(Context ctx){
		ContextWrapper cw = new ContextWrapper(ctx);
 		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
 		final File suspend_f = new File(directory, "Oohlala_User_Location.cache");
 		
 		if (suspend_f.exists()){
 			UserLocation cachedUser = null;
	        FileInputStream fis = null;
	        ObjectInputStream is = null;
	        
	        try {
	        	fis = new FileInputStream(suspend_f);
	        	is = new ObjectInputStream(fis);
	        	cachedUser = (UserLocation) is.readObject();
	        } catch(Exception e) {
	            String val= e.getMessage();
	        } finally {
	        	try {
	            	if (fis != null)   fis.close();
	            	if (is != null)   is.close();
	         	}
	        	catch (Exception e) { }
	        }
	
	        return cachedUser;
 		} else {
 			return new UserLocation(0, 0);
 		}
	}
	
	public static boolean cacheUserFirstLogin(UserFirstLogin obj, Context ctx) {
		ContextWrapper cw = new ContextWrapper(ctx);
		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
        final File suspend_f = new File(directory, "Oohlala_First_Login.cache");

        FileOutputStream   fos  = null;
        ObjectOutputStream oos  = null;
        boolean            keep = true;
        
        UserFirstLogin cachedUser = null;
        
        try {
        	fos = new FileOutputStream(suspend_f);
          	oos = new ObjectOutputStream(fos);
          	
          	cachedUser = obj;
          	
          	oos.writeObject(cachedUser);
        	oos.flush();
       	} catch (Exception e) {
       		Log.i("save user login cache problem", e.toString());
         	keep = false;
       	} finally {
           	try {
           		if (oos != null)   oos.close();
           		if (fos != null)   fos.close();
	        	if (keep == false) suspend_f.delete();
	        	Log.i("save user login cache", "yes");
	        } catch (Exception e) { 
	        	Log.i("save user login cache", "no");
	        }         
       	}

        return keep;
	}

	public static UserFirstLogin getCacheUserFirstLogin(Context ctx){
		ContextWrapper cw = new ContextWrapper(ctx);
 		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
 		final File suspend_f = new File(directory, "Oohlala_First_Login.cache");
 		
 		if (suspend_f.exists()){
	 		UserFirstLogin cachedUser = null;
	        FileInputStream fis = null;
	        ObjectInputStream is = null;
	        
	        try {
	        	fis = new FileInputStream(suspend_f);
	        	is = new ObjectInputStream(fis);
	        	cachedUser = (UserFirstLogin) is.readObject();
	        } catch(Exception e) {
	            String val= e.getMessage();
	        } finally {
	        	try {
	            	if (fis != null)   fis.close();
	            	if (is != null)   is.close();
	         	}
	        	catch (Exception e) { }
	        }
	
	        return cachedUser;
 		} else {
 			return new UserFirstLogin("", true, true, true);
 		}
	}
	
	public static boolean cacheStartTime(UserStartTime obj, Context ctx) {
		ContextWrapper cw = new ContextWrapper(ctx);
		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
        final File suspend_f = new File(directory, "Oohlala_Start_Time.cache");

        FileOutputStream   fos  = null;
        ObjectOutputStream oos  = null;
        boolean            keep = true;
        
        UserStartTime cachedUser = null;
        
        try {
        	fos = new FileOutputStream(suspend_f);
          	oos = new ObjectOutputStream(fos);
          	
          	cachedUser = obj;
          	
          	oos.writeObject(cachedUser);
        	oos.flush();
       	} catch (Exception e) {
       		Log.i("save startTime cache problem", e.toString());
         	keep = false;
       	} finally {
           	try {
           		if (oos != null)   oos.close();
           		if (fos != null)   fos.close();
	        	if (keep == false) suspend_f.delete();
	        	Log.i("save startTime cache", "yes");
	        } catch (Exception e) { 
	        	Log.i("save startTime cache", "no");
	        }         
       	}

        return keep;
	}

	public static UserStartTime getCacheStartTime(Context ctx){
		ContextWrapper cw = new ContextWrapper(ctx);
 		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
 		final File suspend_f = new File(directory, "Oohlala_Start_Time.cache");
 		
 		if (suspend_f.exists()){
 			UserStartTime cachedUser = null;
	        FileInputStream fis = null;
	        ObjectInputStream is = null;
	        
	        try {
	        	fis = new FileInputStream(suspend_f);
	        	is = new ObjectInputStream(fis);
	        	cachedUser = (UserStartTime) is.readObject();
	        } catch(Exception e) {
	            String val= e.getMessage();
	        } finally {
	        	try {
	            	if (fis != null)   fis.close();
	            	if (is != null)   is.close();
	         	}
	        	catch (Exception e) { }
	        }
	
	        return cachedUser;
 		} else {
 			return new UserStartTime(0);
 		}
	}

}
