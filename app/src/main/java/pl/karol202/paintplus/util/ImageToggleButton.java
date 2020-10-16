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
import androidx.appcompat.widget.AppCompatImageButton;
import pl.karol202.paintplus.R;

public class ImageToggleButton extends AppCompatImageButton
{
	public interface OnCheckedChangeListener
	{
		void onCheckedChanged(ImageToggleButton button, boolean checked);
	}

	private static final int[] STATE_CHECKED = { R.attr.checked };

	private OnCheckedChangeListener listener;
	private boolean checked;

	public ImageToggleButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	public boolean performClick()
	{
		checked = !checked;
		if(listener != null) listener.onCheckedChanged(this, checked);
		return super.performClick();
	}

	@Override
	public int[] onCreateDrawableState(int extraSpace)
	{
		int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if(checked) mergeDrawableStates(drawableState, STATE_CHECKED);
		return drawableState;
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener listener)
	{
		this.listener = listener;
	}

	public boolean isChecked()
	{
		return checked;
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
		refreshDrawableState();
	}
}
