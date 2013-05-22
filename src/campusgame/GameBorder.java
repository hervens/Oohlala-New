package campusgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class GameBorder extends Overlay{
	
	private GeoPoint gp1;
    private GeoPoint gp2;

    public GameBorder(GeoPoint gp1, GeoPoint gp2) {
        this.gp1 = gp1;
        this.gp2 = gp2;
    }

    public void draw(Canvas canvas, MapView mapv, boolean shadow){
        super.draw(canvas, mapv, shadow);

        Paint mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
        
        Point p1 = new Point();
        Point p2 = new Point();
        Path path = new Path();

        mapv.getProjection().toPixels(gp1, p1);
        mapv.getProjection().toPixels(gp2, p2);

        path.moveTo(p2.x, p2.y);
        path.lineTo(p1.x, p1.y);

        canvas.drawPath(path, mPaint);
    }

}
