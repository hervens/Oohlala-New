package datastorage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;

public class NinePatch {
	
	public static Bitmap getNinePatch(int id, int x, int y, Context context){
        // id is a resource id for a valid ninepatch
		
        byte[] chunk = BitmapFactory.decodeResource(context.getResources(), id).getNinePatchChunk();
        NinePatchDrawable np_drawable = new NinePatchDrawable(context.getResources(), BitmapFactory.decodeResource(context.getResources(), id), chunk, new Rect(), null);
	    np_drawable.setBounds(0, 0, x, y);
		
	    Bitmap output_bitmap = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(output_bitmap);
	    np_drawable.draw(canvas);
	    
	    np_drawable = null;
	    return output_bitmap;
    }

}
