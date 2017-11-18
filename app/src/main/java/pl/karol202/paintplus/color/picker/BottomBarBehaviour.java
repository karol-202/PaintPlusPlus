/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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