package com.hit56.android.widget;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.hit56.android.utils.DensityUtil;

public class RLView extends TextView{

	public RLView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		Drawable leftDrawable = getCompoundDrawables()[0];
		Drawable rightDrawable = getCompoundDrawables()[2];
		
		int rightSize = DensityUtil.sp2px(context, 12);
		int leftSize = DensityUtil.sp2px(context, 24);
		
		
		rightDrawable.setBounds(0, 0, (int)(rightSize*(15.0/26.0)), rightSize);
		leftDrawable.setBounds(0, 0,  leftSize, leftSize);
		
		setCompoundDrawables(leftDrawable, null, rightDrawable, null);
		
	}

}
