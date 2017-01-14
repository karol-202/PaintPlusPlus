package pl.karol202.paintplus.util;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LayersSheetBehavior<V extends View> extends BottomSheetBehavior<V>
{
	private boolean allowDragging;
	
	public LayersSheetBehavior(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.allowDragging = true;
	}
	
	@Override
	public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event)
	{
		return allowDragging && super.onInterceptTouchEvent(parent, child, event);
	}
	
	public void setAllowDragging(boolean allowDragging)
	{
		this.allowDragging = allowDragging;
	}
}