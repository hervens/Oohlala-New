package com.gotoohlala;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * ������������ָ����Ļ�����һ���ʱ�������ʵ������������View�����û�����������ƫ�ƣ�������
 * 
 * @author android_ls
 */
public class ScrollerContainer extends RelativeLayout {

    private static final String TAG = "ScrollerContainer";

    private Scroller mScroller;

    private VelocityTracker mVelocityTracker;

    /**
     * �ֱ����ְѣ��Ŀ��
     */
    private int mHandlebarWidth;

    /**
     * ��ƫ�ƹ����У�����������ʱ��
     */
    private static final int ANIMATION_DURATION_TIME = 300;
    
    /**
     * ��¼��ǰ�Ļ����������״̬���������Ƿ�ɼ�
     * true  ���һ����������崦�ڿɼ���
     * false ���󻬶��������崦�ڲ��ɼ���
     */
    public static boolean mPanelInvisible;
    
    /**
     * �Ƿ��ѻ�������
     */
    private boolean mFinished;
    
    /**
     * �Ƿ��������
     * �����������
     *     ������ɼ�����ǰ��ָ���µ�����xֵ �������ֱ���ȷ�Χ�ڣ�
     *     �����岻�ɼ�����ǰ��ָ���µ�����xֵ < �ֱ����
     */
    private boolean mAllowScroll;
    
    /**
     * �Ƿ�������Ӧ�����¼�������
     * �����������������ɼ�����ǰ��ָ���µ�����xֵ �������ֱ���ȷ�Χ��
     */
    private boolean isClick;
    
    public ScrollerContainer(Context context) {
        super(context);

        mScroller = new Scroller(context);
        mHandlebarWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //Log.e(TAG, "dispatchTouchEvent()");
        
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            //Log.i(TAG, "dispatchTouchEvent():  ACTION_DOWN");
            
            mFinished = mScroller.isFinished();
            if(mFinished){
                int x = (int) ev.getX();
                int width = getWidth();
                
                if(mPanelInvisible)// ������ɼ�
                {
                    if(x > (width - mHandlebarWidth)){ // ��ǰ��ָ���µ�����xֵ �������ֱ���ȷ�Χ��
                        isClick = true;
                        mAllowScroll = true;
                        return true;
                    } else {
                        isClick = false;
                        mAllowScroll = false;
                    }
                } else { // �����岻�ɼ�
                    if(x < mHandlebarWidth ){ // ��ǰ��ָ���µ�����xֵ < �ֱ���� ��Ҳ����˵���ֱ���ȷ�Χ�ڣ��ǿ�����Ӧ�û������һ������ƣ�
                        mAllowScroll = true;
                    }else{
                        mAllowScroll = false;
                    }
                }
                
            } else {
                // ��ǰ���ڹ�����View���������²���Ӧ
                return false;
            }
            
            break;

        case MotionEvent.ACTION_MOVE:
            //Log.i(TAG, "dispatchTouchEvent():  ACTION_MOVE");
            int margin = getWidth() - (int) ev.getX();
            if (margin < mHandlebarWidth && mAllowScroll) {
                
                //Log.e(TAG, "dispatchTouchEvent ACTION_MOVE margin = " + margin + "\t mHandlebarWidth = " + mHandlebarWidth);
                return true;
            }
            
            break;
        case MotionEvent.ACTION_UP:
            //Log.i(TAG, "dispatchTouchEvent():  ACTION_UP");
            
            if (isClick && mPanelInvisible && mAllowScroll) {
                isClick = false;
                mPanelInvisible = false;
                
                int scrollX = getChildAt(1).getScrollX();
                mScroller.startScroll(scrollX, 0, -scrollX, 0, ANIMATION_DURATION_TIME);
                invalidate();
                
                return true;
            }
            
            break;
        case MotionEvent.ACTION_CANCEL:
        	//Log.i(TAG, "dispatchTouchEvent():  ACTION_CANCEL");
            break;
        default:
            break;
        }
        
        return super.dispatchTouchEvent(ev);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //Log.e(TAG, "onInterceptTouchEvent()");
        
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            //Log.i(TAG, "onInterceptTouchEvent():  ACTION_DOWN");
            mFinished = mScroller.isFinished();
            if(!mFinished){
                return false;
            }
            
            break;
        case MotionEvent.ACTION_MOVE:
            //Log.i(TAG, "onInterceptTouchEvent():  ACTION_MOVE");
            
            mVelocityTracker = VelocityTracker.obtain();
            mVelocityTracker.addMovement(ev);
            
            // һ��ʱ�����ƶ��˶��ٸ�����
            mVelocityTracker.computeCurrentVelocity(1000, ViewConfiguration.getMaximumFlingVelocity());
            float velocityValue = Math.abs(mVelocityTracker.getXVelocity()) ;
            //Log.d(TAG, "onInterceptTouchEvent():  mVelocityValue = " + velocityValue);
            
            if (velocityValue > 300 && mAllowScroll) {
                return true;
            }
            
            break;
        case MotionEvent.ACTION_UP:
            //Log.i(TAG, "onInterceptTouchEvent():  ACTION_UP");
            
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
            
            break;
        case MotionEvent.ACTION_CANCEL:
            //Log.i(TAG, "onInterceptTouchEvent():  ACTION_CANCEL");
            break;
        default:
            break;
        }
        
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.e(TAG, "onTouchEvent()");

        float x = event.getX();
        
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            //Log.i(TAG, "onTouchEvent():  ACTION_DOWN");
            mFinished = mScroller.isFinished();
            if(!mFinished){
                return false;
            }
            break;

        case MotionEvent.ACTION_MOVE:
            //Log.i(TAG, "onTouchEvent():  ACTION_MOVE");
            getChildAt(1).scrollTo(-(int)x, 0);
            break;

        case MotionEvent.ACTION_UP:
            //Log.i(TAG, "onTouchEvent():  ACTION_UP");
            
            if(!mAllowScroll){
                break;
            }
            
           float width = getWidth();
           // ��Ӧ������View���ٽ�ֵ����������Ӧ�������������Խ�ֻ�Ĵ�Щ��
           // ���磺criticalWidth = width / 3��criticalWidth = width / 2��������������Ǻǡ�
           float criticalWidth = width / 5;
           
           //Log.i(TAG, "onTouchEvent():  ACTION_UP x = " + x + "\t criticalWidth = " + criticalWidth);
           
           int scrollX = getChildAt(1).getScrollX();
           
           if ( x < criticalWidth) {
               //Log.i(TAG, "onTouchEvent():  ACTION_UP ���󻬶�");
               
                mPanelInvisible = false;
               
                mScroller.startScroll(scrollX, 0, -scrollX, 0, ANIMATION_DURATION_TIME);
                invalidate();
            } else if ( x > criticalWidth){
                //Log.i(TAG, "onTouchEvent():  ACTION_UP ���һ���");
              
                mPanelInvisible = true;
                
                int toX = (int)(width - Math.abs(scrollX) - mHandlebarWidth);
                mScroller.startScroll(scrollX, 0, -toX, 0, ANIMATION_DURATION_TIME);
                invalidate();
            }
            
            break;
        case MotionEvent.ACTION_CANCEL:
            //Log.i(TAG, "onTouchEvent():  ACTION_CANCEL");
            break;
        default:
            break;
        }
        
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        // super.computeScroll();
        
        if(mScroller.computeScrollOffset()){
            this.getChildAt(1).scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            this.postInvalidate();
        }
    }

    /**
     * ���һ���View�����������淹�ɼ�
     */
    public void slideToRight() {
        mFinished = mScroller.isFinished();
        if(mFinished && !mPanelInvisible){
            mPanelInvisible = true;
            
            float width = getWidth();
            int scrollX = getChildAt(1).getScrollX();
            int toX = (int)(width - Math.abs(scrollX) - mHandlebarWidth);
            
            mScroller.startScroll(scrollX, 0, -toX, 0, ANIMATION_DURATION_TIME);
            invalidate();
        }
    }
    
    /**
     * View�����¼�������
     * @author android_ls
     */
    public interface OnSlideListener {
        /**
         * ���󻬶���View
         */
        public abstract void toLeft();
        
        /**
         * ���һ�����View
         */
        public abstract void toRight();
    }
    
    /**
     * �л���ͼ
     * @param view
     */
    public void show(View view) {
        mPanelInvisible = false;
        
        int scrollX = getChildAt(1).getScrollX();
        mScroller.startScroll(scrollX, 0, -scrollX, 0, ANIMATION_DURATION_TIME);
        invalidate();
        
        removeViewAt(1);
        addView(view, 1, getLayoutParams());
    }
    
}

