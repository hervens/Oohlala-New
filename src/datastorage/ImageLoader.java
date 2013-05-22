package datastorage;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import user.Profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.util.Log;

public class ImageLoader {
	
	public static Context context;
	public static int loaderRange = 3;
	
	/*
	 * big size Image
	 * 
	 */
	public static Bitmap imageStoreAndLoad(String imageUrl, Context c){
		context = c;
		ImageStoreInCacheAndMemory(imageUrl);
		return getBitmapFromImageUrl(imageUrl);
	}
	
	public static Bitmap getBitmapFromImageUrl(String imageUrl) {
		// TODO Auto-generated method stub
		try {
			if (MemoryStorage.findKey(imageUrl)){
				return MemoryStorage.returnImage(imageUrl);
			} else {
				if(CacheInternalStorage.isImageExist(imageUrl, context) && CacheInternalStorage.isImageValid(imageUrl, context)){
					return BitmapFactory.decodeFile(CacheInternalStorage.HashedImageDir(imageUrl, context), null);
				} else {
					return BitmapFactory.decodeStream((InputStream)new URL(imageUrl).getContent());
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	}

	public static void ImageStoreInCacheAndMemory(String image) {
		// TODO Auto-generated method stub
		if (!MemoryStorage.findKey(image)){
			MemoryStorage.insert(image, image);
		}
		if(!CacheInternalStorage.isImageExist(image, context) || !CacheInternalStorage.isImageValid(image, context)){
			CacheInternalStorage.cache(image, context); //cache the thumb images
		}
	}
	
	/*
	 * original size Image 
	 * 
	 */
	
	public static Bitmap originalImageStoreAndLoad(String imageUrl, Context c){
		context = c;
		originalImageStoreInCache(imageUrl);
		//Log.i("thumb bitmap exist 2", String.valueOf(bitmap.getHeight()));
		return getOriginalBitmapFromImageUrl(imageUrl);
	}
	
	public static Bitmap getOriginalBitmapFromImageUrl(String imageUrl) {
		// TODO Auto-generated method stub
		try {
			if(CacheInternalStorage.isImageExist(imageUrl, context) && CacheInternalStorage.isImageValid(imageUrl, context)){
				Options ThumbOpts = new Options();
				ThumbOpts.inSampleSize = 1;
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true; 
				
				File file = new File(CacheInternalStorage.HashedImageDir(imageUrl, context));
				FileInputStream fs = new FileInputStream(file);
			    try {
			    	return new decodeFileDescriptor(fs.getFD(), ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block

					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
						
					return null;
				}
			} else {
				Options ThumbOpts = new Options();
				ThumbOpts.inSampleSize = 1;
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true; 

				try {
					return new decodeStream(imageUrl, ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					
					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block					
					
					return null;
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			
			
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			
			return null;
		}
	}

	public static void originalImageStoreInCache(String image) {
		// TODO Auto-generated method stub
		if(!CacheInternalStorage.isImageExist(image, context) || !CacheInternalStorage.isImageValid(image, context)){
			//Log.i("thumb image cache", image);
			CacheInternalStorage.cache(image, context); //cache the thumb images
		}
	}
	
	/*
	 * normal Image 
	 * 
	 */
	public static Bitmap thumbImageStoreAndLoad(String imageUrl, Context c){
		context = c;
		thumbImageStoreInCache(imageUrl);
		//Log.i("thumb bitmap exist 2", String.valueOf(bitmap.getHeight()));
		return getThumbBitmapFromImageUrl(imageUrl);
	}
	
	public static Bitmap getThumbBitmapFromImageUrl(String imageUrl) {
		// TODO Auto-generated method stub
		try {
			if(CacheInternalStorage.isImageExist(imageUrl, context) && CacheInternalStorage.isImageValid(imageUrl, context)){
				Options ThumbOpts = new Options();
				ThumbOpts.inSampleSize = 1;
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true;   
				
				File file = new File(CacheInternalStorage.HashedImageDir(imageUrl, context));
			    FileInputStream fs = new FileInputStream(file);
			    try {
			    	return new decodeFileDescriptor(fs.getFD(), ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
						
					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
						
					return null;
				}
			} else {
				Options ThumbOpts = new Options();
				ThumbOpts.inSampleSize = 1;
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true;   

				try {
					return new decodeStream(imageUrl, ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					
					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					
					return null;
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block

			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	}

	public static void thumbImageStoreInCache(String image) {
		// TODO Auto-generated method stub
		if(!CacheInternalStorage.isImageExist(image, context) || !CacheInternalStorage.isImageValid(image, context)){
			//Log.i("thumb image cache", image);
			CacheInternalStorage.cache(image, context); //cache the thumb images
		}
	}
	
	/*
	 * All the studentsNearby Row Image 
	 * 
	 */
	public static Bitmap studentsNearbyImageStoreAndLoad(String imageUrl, Context c, int dps){
		context = c;
		studentsNearbyImageStoreInCache(imageUrl, dps);
		//Log.i("thumb bitmap exist 2", String.valueOf(bitmap.getHeight()));
		return getStudentsNearbyBitmapFromImageUrl(imageUrl);
	}
	
	public static Bitmap getStudentsNearbyBitmapFromImageUrl(String imageUrl) {
		// TODO Auto-generated method stub
		try {
			if(CacheInternalStorage.isImageExist(imageUrl, context) && CacheInternalStorage.isImageValid(imageUrl, context)){
				Options ThumbOpts = new Options();
				ThumbOpts.inSampleSize = 1;
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true;   
				
				File file = new File(CacheInternalStorage.HashedImageDir(imageUrl, context));
				FileInputStream fs = new FileInputStream(file);
			    try {
			    	return new decodeFileDescriptor(fs.getFD(), ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
	
					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block

					return null;
				}
			} else {
				Options ThumbOpts = new Options();
				ThumbOpts.inSampleSize = 1;
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true;   
				
				try {
					return new decodeStream(imageUrl, ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					
					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					
					return null;
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block

			return null;
		}
	}

	public static void studentsNearbyImageStoreInCache(String image, int dps) {
		// TODO Auto-generated method stub
		if(!CacheInternalStorage.isImageExist(image, context) || !CacheInternalStorage.isImageValid(image, context)){
			//Log.i("Kembrel image cache", image);
			CacheInternalStorage.cacheStudentsNearby(image, context, dps); //cache the thumb images
		}
	}
	
	/*
	 * All the Listview Row Image 
	 * 
	 */
	public static Bitmap KembrelImageStoreAndLoad(String imageUrl, Context c){
		context = c;
		KembrelImageStoreInCache(imageUrl);
		//Log.i("thumb bitmap exist 2", String.valueOf(bitmap.getHeight()));
		return getKembrelBitmapFromImageUrl(imageUrl);
	}
	
	public static Bitmap getKembrelBitmapFromImageUrl(String imageUrl) {
		// TODO Auto-generated method stub
		try {
			if(CacheInternalStorage.isImageExist(imageUrl, context) && CacheInternalStorage.isImageValid(imageUrl, context)){
				Options ThumbOpts = new Options();
				ThumbOpts.inSampleSize = 1;
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true;   
				
				File file = new File(CacheInternalStorage.HashedImageDir(imageUrl, context));
				FileInputStream fs = new FileInputStream(file);
			    try {
			    	return new decodeFileDescriptor(fs.getFD(), ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
	
					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block

					return null;
				}
			} else {
				Options ThumbOpts = new Options();
				ThumbOpts.inSampleSize = 1;
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true;   
				
				try {
					return new decodeStream(imageUrl, ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					
					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					
					return null;
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block

			return null;
		}
	}

	public static void KembrelImageStoreInCache(String image) {
		// TODO Auto-generated method stub
		if(!CacheInternalStorage.isImageExist(image, context) || !CacheInternalStorage.isImageValid(image, context)){
			//Log.i("Kembrel image cache", image);
			CacheInternalStorage.cacheKembrel(image, context); //cache the thumb images
		}
	}
	
	/*
	 * All the profile thumb Image: 2 times inSampleSize
	 * 
	 */
	public static Bitmap profileThumbImageStoreAndLoad(String imageUrl, Context c){
		context = c;
		ProfileThumbImageStoreInCache(imageUrl);
		//Log.i("thumb bitmap exist 2", String.valueOf(bitmap.getHeight()));
		return  getProfileThumbBitmapFromImageUrl(imageUrl);
	}
	
	public static Bitmap getProfileThumbBitmapFromImageUrl(String imageUrl) {
		// TODO Auto-generated method stub
		try {
			if(CacheInternalStorage.isImageExist(imageUrl, context) && CacheInternalStorage.isImageValid(imageUrl, context)){
				Options ThumbOpts = new Options();
				ThumbOpts.inSampleSize = 4;
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true;   
				
				File file = new File(CacheInternalStorage.HashedImageDir(imageUrl, context));
				FileInputStream fs = new FileInputStream(file);
			    try {
			    	return new decodeFileDescriptor(fs.getFD(), ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
						
					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
	
					return null;
				}
			} else {
				Options ThumbOpts = new Options();
				ThumbOpts.inSampleSize = 4;
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true;   

				try {
					return new decodeStream(imageUrl, ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block

					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block

					return null;
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block

			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block

			return null;
		}
	}

	public static void ProfileThumbImageStoreInCache(String image) {
		// TODO Auto-generated method stub
		if(!CacheInternalStorage.isImageExist(image, context) || !CacheInternalStorage.isImageValid(image, context)){
			//Log.i("Kembrel image cache", image);
			CacheInternalStorage.cacheKembrel(image, context); //cache the thumb images
		}
	}
	
	/*
	 * xmppChat Image 
	 * 
	 */
	public static Bitmap chatImageStoreAndLoad(String imageUrl, Context c, int type){
		context = c;
		chatImageStoreInCache(imageUrl, type);
		//Log.i("thumb bitmap exist 2", String.valueOf(bitmap.getHeight()));
		return getChatBitmapFromImageUrl(imageUrl);
	}
	
	public static Bitmap getChatBitmapFromImageUrl(String imageUrl) {
		// TODO Auto-generated method stub
		try {
			if(CacheInternalStorage.isImageExist(imageUrl, context) && CacheInternalStorage.isImageValid(imageUrl, context)){
				Options ThumbOpts = new Options();
				ThumbOpts.inSampleSize = 1;
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true;   
				
				File file = new File(CacheInternalStorage.HashedImageDir(imageUrl, context));
				FileInputStream fs = new FileInputStream(file);
			    try {
			    	return new decodeFileDescriptor(fs.getFD(), ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
	
					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
	
					return null;
				}
			} else {
				Options ThumbOpts = new Options();
				ThumbOpts.inSampleSize = 1;
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true;   

				try {
					return new decodeStream(imageUrl, ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block

					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block

					return null;
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	}

	public static void chatImageStoreInCache(String image, int type) {
		// TODO Auto-generated method stub
		if(!CacheInternalStorage.isImageExist(image, context) || !CacheInternalStorage.isImageValid(image, context)){
			//Log.i("Kembrel image cache", image);
			CacheInternalStorage.cacheChatImage(image, context, type); //cache the thumb images
		}
	}
	
	/*
	 * campus wall resized Image 
	 * 
	 */
	
	public static Bitmap campusWallImageStoreAndLoad(String imageUrl, Context c, int dps){
		context = c;
		campusWallImageStoreInCache(imageUrl, dps);
		return getCampusWallBitmapFromImageUrl(imageUrl, dps);
	}
	
	public static Bitmap getCampusWallBitmapFromImageUrl(String imageUrl, int dps) {
		// TODO Auto-generated method stub
		try {
			if(CacheInternalStorage.isImageExist(imageUrl, context) && CacheInternalStorage.isImageValid(imageUrl, context)){
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
			    ThumbOpts.inSampleSize = CacheInternalStorage.calculateInSampleSize(ThumbOpts, ConvertDpsToPixels.getPixels(dps, context), ConvertDpsToPixels.getPixels(dps, context));
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true; 
				
				File file = new File(CacheInternalStorage.HashedImageDir(imageUrl, context));
				FileInputStream fs = new FileInputStream(file);
			    try {
			    	return new decodeFileDescriptor(fs.getFD(), ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
	
					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					
					return null;
				}
			} else {
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
			    ThumbOpts.inSampleSize = CacheInternalStorage.calculateInSampleSize(ThumbOpts, ConvertDpsToPixels.getPixels(dps, context), ConvertDpsToPixels.getPixels(dps, context));
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true; 

				try {
					return new decodeStream(imageUrl, ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block

					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block

					return null;
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block

			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block

			return null;
		}
	}

	public static void campusWallImageStoreInCache(String imageUrl, int dps) {
		// TODO Auto-generated method stub
		if(!CacheInternalStorage.isImageExist(imageUrl, context) || !CacheInternalStorage.isImageValid(imageUrl, context)){
			//Log.i("CampusWall image cache", image);
			CacheInternalStorage.cache(imageUrl, context); //cache the thumb images
		}
	}
	
	/*
	 * 4 times smaller size thumb Image 
	 * 
	 */
	
	public static Bitmap smallThumbImageStoreAndLoad(String imageUrl, Context c){
		context = c;
		smallThumbImageStoreInCache(imageUrl);
		//Log.i("thumb bitmap exist 2", String.valueOf(bitmap.getHeight()));
		return getSmallThumbBitmapFromImageUrl(imageUrl);
	}
	
	public static Bitmap getSmallThumbBitmapFromImageUrl(String imageUrl) {
		// TODO Auto-generated method stub
		try {
			if (CacheInternalStorage.isImageExist(imageUrl, context) && CacheInternalStorage.isImageValid(imageUrl, context)){
				Options ThumbOpts = new Options();
				ThumbOpts.inSampleSize = 4;
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true; 
				
				File file = new File(CacheInternalStorage.HashedImageDir(imageUrl, context));
				FileInputStream fs = new FileInputStream(file);
			    try {
			    	return new decodeFileDescriptor(fs.getFD(), ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
	
					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
	
					return null;
				}
			} else {
				Options ThumbOpts = new Options();
				ThumbOpts.inSampleSize = 4;
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true; 

				try {
					return new decodeStream(imageUrl, ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block

					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block

					return null;
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block

			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
						
			return null;
		}
	}

	public static void smallThumbImageStoreInCache(String image) {
		// TODO Auto-generated method stub
		if(!CacheInternalStorage.isImageExist(image, context) || !CacheInternalStorage.isImageValid(image, context)){
			//Log.i("thumb image cache", image);
			CacheInternalStorage.cache(image, context); //cache the thumb images
		}
	}
	
	/*
	 * 8 times smaller size thumb Image 
	 * 
	 */
	
	public static Bitmap superSmallThumbImageStoreAndLoad(String imageUrl, Context c){
		context = c;
		superSmallThumbImageStoreInCache(imageUrl);
		//Log.i("thumb bitmap exist 2", String.valueOf(bitmap.getHeight()));
		return getSuperSmallThumbBitmapFromImageUrl(imageUrl);
	}
	
	public static Bitmap getSuperSmallThumbBitmapFromImageUrl(String imageUrl) {
		// TODO Auto-generated method stub
		try {
			if (CacheInternalStorage.isImageExist(imageUrl, context) && CacheInternalStorage.isImageValid(imageUrl, context)){
				Options ThumbOpts = new Options();
				ThumbOpts.inSampleSize = 8;
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true; 
				
				File file = new File(CacheInternalStorage.HashedImageDir(imageUrl, context));
				FileInputStream fs = new FileInputStream(file);
			    try {
			    	return new decodeFileDescriptor(fs.getFD(), ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
	
					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
	
					return null;
				}
			} else {
				Options ThumbOpts = new Options();
				ThumbOpts.inSampleSize = 8;
				ThumbOpts.inJustDecodeBounds = false;
				ThumbOpts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				ThumbOpts.inInputShareable=true; 
				
				try {
					return new decodeStream(imageUrl, ThumbOpts).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block

					return null;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block

					return null;
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block

			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block			
			
			return null;
		}
	}

	public static void superSmallThumbImageStoreInCache(String image) {
		// TODO Auto-generated method stub
		if(!CacheInternalStorage.isImageExist(image, context) || !CacheInternalStorage.isImageValid(image, context)){
			//Log.i("thumb image cache", image);
			CacheInternalStorage.cache(image, context); //cache the thumb images
		}
	}
	
	/**
     * 按正方形裁切图片
     */
    public static Bitmap ImageCrop(Bitmap bitmap) {
    	if (bitmap != null){
	        int w = bitmap.getWidth(); // 得到图片的宽，高
	        int h = bitmap.getHeight();
	
	        int wh = w > h ? h : w;// 裁切后所取的正方形区域边长
	
	        int retX = w > h ? (w - h) / 2 : 0;//基于原图，取正方形左上角x坐标
	        int retY = w > h ? 0 : (h - w) / 2;
	
	        //下面这句是关键
	        return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
    	} else {
    		return null;
    	}
    }
    
    public static Bitmap ImageCropRectangular(Bitmap bitmap, int picWidth, int picHeight) {
    	if (bitmap != null){
	        int w = bitmap.getWidth(); // 得到图片的宽，高
	        int h = bitmap.getHeight();
	
	        int w2, h2, retX, retY;
	        if (w > h){
	        	if (picWidth < picHeight){
	        		h2 = h;
	        		w2 = (int) (((double)picWidth/picHeight)*h);
	        		retX = (w - w2) / 2;
	        		retY = 0;
	        	} else {
	        		if (picWidth/picHeight > w/h){
	        			w2 = w;
	        			h2 = (int) (((double)picHeight/picWidth)*w);
	        			retX = 0;
		        		retY = (h - h2) / 2;
	        		} else {
	        			h2 = h;
	        			w2 = (int) (((double)picWidth/picHeight)*h);
	        			retX = (w - w2) / 2;
		        		retY = 0;
	        		}
	        	}
	        } else {
	        	if (picWidth > picHeight){
	        		w2 = w;
        			h2 = (int) (((double)picHeight/picWidth)*w);
        			retX = 0;
	        		retY = (h - h2) / 2;
	        	} else {
	        		if (picWidth/picHeight > w/h){
	        			w2 = w;
	        			h2 = (int) (((double)picHeight/picWidth)*w);
	        			retX = 0;
		        		retY = (h - h2) / 2;
	        		} else {
	        			h2 = h;
	        			w2 = (int) (((double)picWidth/picHeight)*h);
	        			retX = (w - w2) / 2;
		        		retY = 0;
	        		}
	        	}
	        }
	        
	        //Log.i("ImageCropRectangular", String.valueOf(w) + "/" + String.valueOf(h) + "/" + String.valueOf(w2) + "/" + String.valueOf(h2) + "/" + String.valueOf(retX) + "/" + String.valueOf(retY));
	        //下面这句是关键
	        return Bitmap.createBitmap(bitmap, retX, retY, w2, h2, null, false);
    	} else {
    		return null;
    	}
    }
    
    public static class decodeFileDescriptor extends AsyncTask<Void, Void, Bitmap> {
		FileDescriptor fd = null;
		Options ops = null;
		
		public decodeFileDescriptor(FileDescriptor fd, Options ops){
			this.fd = fd;
			this.ops = ops;
		}
    	
		@Override
		protected Bitmap doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return BitmapFactory.decodeFileDescriptor(fd, null, ops);
		}
    }
	
	public static class decodeStream extends AsyncTask<Void, Void, Bitmap> {
		String imageUrl = null;
		Options ops = null;
		
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

				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block

				return null;
			} catch (OutOfMemoryError e) {
				return null;
			}
		}		
    }

}
