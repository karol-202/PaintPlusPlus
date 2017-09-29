package pl.karol202.paintplus.color.picker;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class BottomBarBehaviour extends CoordinatorLayout.Behavior<View>
{
	public BottomBarBehaviour() { }
	
	public BottomBarBehaviour(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	@Override
	public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency)
	{
		return dependency instanceof ViewPager;
	}
	
	@Override
	public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency)
	{
		if(!ViewCompat.isLaidOut(parent)) return false;
		child.setY(parent.getBottom() - child.getHeight());
		dependency.setPadding(dependency.getPaddingLeft(), dependency.getPaddingTop(),
							  dependency.getPaddingRight(), child.getHeight());
		return false;
	}
}