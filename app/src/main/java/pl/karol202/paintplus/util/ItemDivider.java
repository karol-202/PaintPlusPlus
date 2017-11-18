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
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import pl.karol202.paintplus.R;

public class ItemDivider extends RecyclerView.ItemDecoration
{
	private Drawable divider;
	
	public ItemDivider(Context context)
	{
		divider = ContextCompat.getDrawable(context, R.drawable.divider);
	}
	
	@Override
	public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state)
	{
		int left = parent.getPaddingLeft();
		int right = parent.getWidth() - parent.getPaddingRight();
		
		int childCount = parent.getChildCount();
		for(int i = 0; i < childCount; i++)
		{
			View child = parent.getChildAt(i);
			RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
			int top = child.getBottom() + params.bottomMargin;
			int bottom = top + divider.getIntrinsicHeight();
			divider.setBounds(left, top, right, bottom);
			divider.draw(canvas);
		}
	}
}