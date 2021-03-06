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

package pl.karol202.paintplus.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class LayersSheetBehavior<V extends View> extends BottomSheetBehavior<V>
{
	private boolean allowDragging;

	public LayersSheetBehavior(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.allowDragging = true;
	}

	@Override
	public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull V child, @NonNull MotionEvent event)
	{
		return allowDragging && super.onInterceptTouchEvent(parent, child, event);
	}

	public void setAllowDragging(boolean allowDragging)
	{
		this.allowDragging = allowDragging;
	}
}
