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

package pl.karol202.paintplus.image.layer;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import pl.karol202.paintplus.util.BlockableLinearLayoutManager;

public class LayersRecyclerView extends RecyclerView
{
	private int maxHeight;
	private BlockableLinearLayoutManager manager;

	public LayersRecyclerView(Context context)
	{
		this(context, null);
	}

	public LayersRecyclerView(Context context, @Nullable AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public LayersRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		manager = new BlockableLinearLayoutManager(context);
		setLayoutManager(manager);
		addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));
	}

	@Override
	protected void onMeasure(int widthSpec, int heightSpec)
	{
		super.onMeasure(widthSpec, heightSpec);
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();

		if(height > maxHeight && maxHeight != 0) height = maxHeight;
		setMeasuredDimension(width, height);
	}

	public void setMaxHeight(int maxHeight)
	{
		if(maxHeight >= 0) this.maxHeight = maxHeight;
	}

	public void setAllowScrolling(boolean allowScrolling)
	{
		manager.setAllowScrolling(allowScrolling);
	}
}
