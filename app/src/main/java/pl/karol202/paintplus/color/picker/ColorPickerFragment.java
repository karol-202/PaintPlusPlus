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
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public abstract class ColorPickerFragment extends Fragment
{
	private ActivityColorSelect activityColorSelect;
	private boolean useAlpha;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(getArguments() != null) useAlpha = getArguments().getBoolean("useAlpha");
	}

	@Override
	public void onAttach(@NonNull Context context)
	{
		super.onAttach(context);
		init(context);
	}

	private void init(Context context)
	{
		if(!(context instanceof ActivityColorSelect))
			throw new IllegalStateException("ColorPickerFragment must be attached to ActivityColorSelect.");
		activityColorSelect = (ActivityColorSelect) context;
	}

	protected abstract void onColorModeSelected(int actionId);

	protected abstract boolean isColorModeSupported(int actionId);

	protected abstract void onTabSelected();

	public boolean isUsingAlpha()
	{
		return useAlpha;
	}

	public int getCurrentColor()
	{
		return activityColorSelect.getCurrentColor();
	}

	protected void setCurrentColor(int color)
	{
		activityColorSelect.setCurrentColor(color);
	}
}
