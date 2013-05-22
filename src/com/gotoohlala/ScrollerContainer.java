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
 * 功能描述：手指在屏幕上左右滑动时，该类的实例负责让其子View根据用户的手势左右偏移（滚动）
 * 
 * @author android_ls
 */
public class ScrollerContainer extends RelativeLayout {

    private static final String TAG = "ScrollerContainer";

    private Scroller mScroller;

    private VelocityTracker mVelocityTracker;

    /**
     * 手柄（手把）的宽度
     */
    private int mHandlebarWidth;

    /**
     * 在偏移过程中，动画持续的时间
     */
    private static final int ANIMATION_DURATION_TIME = 300;
    
    /**
     * 记录当前的滑动结束后的状态，左侧面板是否可见
     * true  向右滑动（左侧面板处于可见）
     * false 向左滑动（左侧面板处于不可见）
     */
    public static boolean mPanelInvisible;
    
    /**
     * 是否已滑动结束
     */
    private boolean mFinished;
    
    /**
     * 是否允许滚动
     * 满足的条件：
     *     左侧面板可见，当前手指按下的坐标x值 ，是在手柄宽度范围内；
     *     左侧面板不可见，当前手指按下的坐标x值 < 手柄宽度
     */
    private boolean mAllowScroll;
    
    /**
     * 是否满足响应单击事件的条件
     * 满足的条件：左侧面板可见，当前手指按下的坐标x值 ，是在手柄宽度范围内
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
                
                if(mPanelInvisible)// 左侧面板可见
                {
                    if(x > (width - mHandlebarWidth)){ // 当前手指按下的坐标x值 ，是在手柄宽度范围内
                        isClick = true;
                        mAllowScroll = true;
                        return true;
                    } else {
                        isClick = false;
                        mAllowScroll = false;
                    }
                } else { // 左侧面板不可见
                    if(x < mHandlebarWidth ){ // 当前手指按下的坐标x值 < 手柄宽度 （也就是说在手柄宽度范围内，是可以相应用户的向右滑动手势）
                        mAllowScroll = true;
                    }else{
                        mAllowScroll = false;
                    }
                }
                
            } else {
                // 当前正在滚动子View，其它的事不响应
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
            
            // 一秒时间内移动了多少个像素
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
           // 响应滚动子View的临界值，若觉得响应过于灵敏，可以将只改大些。
           // 比如：criticalWidth = width / 3或criticalWidth = width / 2，看情况而定，呵呵。
           float criticalWidth = width / 5;
           
           //Log.i(TAG, "onTouchEvent():  ACTION_UP x = " + x + "\t criticalWidth = " + criticalWidth);
           
           int scrollX = getChildAt(1).getScrollX();
           
           if ( x < criticalWidth) {
               //Log.i(TAG, "onTouchEvent():  ACTION_UP 向左滑动");
               
                mPanelInvisible = false;
               
                mScroller.startScroll(scrollX, 0, -scrollX, 0, ANIMATION_DURATION_TIME);
                invalidate();
            } else if ( x > criticalWidth){
                //Log.i(TAG, "onTouchEvent():  ACTION_UP 向右滑动");
              
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
     * 向右滑动View，让左侧操作面饭可见
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
     * View滑动事件监听器
     * @author android_ls
     */
    public interface OnSlideListener {
        /**
         * 向左滑动子View
         */
        public abstract void toLeft();
        
        /**
         * 向右滑动子View
         */
        public abstract void toRight();
    }
    
    /**
     * 切换视图
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

