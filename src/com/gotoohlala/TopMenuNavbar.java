package com.gotoohlala;

import com.gotoohlala.LeftPanelLayout.onSeletedListener;
import com.gotoohlala.ScrollerContainer.OnSlideListener;

import datastorage.Fonts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 功能描述：自定义顶部菜单栏
 * @author android_ls
 */
public class TopMenuNavbar extends FrameLayout {

	private OnSlideListener mOnSlideListener;  
	View v = null;
	public TextView tvNumber;
	public RelativeLayout rlMenu;

    public TopMenuNavbar(Context context) {
        super(context);
        setupViews();
    }

    public TopMenuNavbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupViews();
    }
    
    public void setOnSlideListener(OnSlideListener onSlideListener) {  
		mOnSlideListener = onSlideListener;  
	}  
    
    private void setupViews() {
    	final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
		v = (RelativeLayout) mLayoutInflater.inflate(R.layout.top_menu_navbar, null);  
	    addView(v);  
	    
	    rlMenu = (RelativeLayout) v.findViewById(R.id.rlMenu);
	    rlMenu.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (mOnSlideListener != null) {  
                    mOnSlideListener.toRight();  
                }  
			}
	    }); 
	    
	    Button bMenu = (Button) v.findViewById(R.id.bMenu);
	    bMenu.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (mOnSlideListener != null) {  
                    mOnSlideListener.toRight();  
                }  
			}
	    }); 

	    tvNumber = (TextView) v.findViewById(R.id.tvNumber);
	    tvNumber.setTypeface(Fonts.getOpenSansBold(getContext()));
    }

}
