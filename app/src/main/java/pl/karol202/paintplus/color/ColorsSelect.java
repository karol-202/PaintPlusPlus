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

package pl.karol202.paintplus.color;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.color.picker.ActivityColorSelect;
import pl.karol202.paintplus.image.Image;

public class ColorsSelect extends Fragment implements View.OnClickListener, ColorsSet.OnColorsChangeListener
{
	private static final int REQUEST_COLOR_PICK = 0;
	private static final int TARGET_FIRST = 0;
	private static final int TARGET_SECOND = 1;
	
	private ActivityPaint activityPaint;
	private Image image;
	private ColorsSet colors;

	private View colorFirst;
	private View colorSecond;
	private ImageButton buttonSwap;
	private int target;
	
	@Override
	@SuppressWarnings("deprecation")
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		init(activity);
	}
	
	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		init(context);
	}
	
	private void init(Context context)
	{
		if(!(context instanceof ActivityPaint))
			throw new RuntimeException("ColorsSelect fragment can only be attached to ActivityPaint.");
		activityPaint = (ActivityPaint) context;
	}
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.colors, container, false);
		image = activityPaint.getImage();
		colors = image.getColorsSet();
		colors.setListener(this);
		
		colorFirst = view.findViewById(R.id.view_color_first);
		colorFirst.setOnClickListener(this);

		colorSecond = view.findViewById(R.id.view_color_second);
		colorSecond.setOnClickListener(this);

		buttonSwap = view.findViewById(R.id.button_colors_swap);
		buttonSwap.setOnClickListener(this);

		updateColors();
		return view;
	}
	
	private void updateColors()
	{
		colorFirst.setBackgroundColor(colors.getFirstColor());
		colorSecond.setBackgroundColor(colors.getSecondColor());
		image.updateImage();
	}
	
	@Override
	public void onClick(View v)
	{
		if(v == buttonSwap) colors.revert();
		else if(v == colorFirst) pickColor(TARGET_FIRST);
		else if(v == colorSecond) pickColor(TARGET_SECOND);
		updateColors();
	}

	private void pickColor(int target)
	{
		this.target = target;
		@ColorInt int color = target == TARGET_FIRST ? colors.getFirstColor() : colors.getSecondColor();
		
		Intent intent = new Intent(getActivity(), ActivityColorSelect.class);
		intent.putExtra(ActivityColorSelect.ALPHA_KEY, false);
		intent.putExtra(ActivityColorSelect.COLOR_KEY, color);
		startActivityForResult(intent, REQUEST_COLOR_PICK);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode != REQUEST_COLOR_PICK || data == null) return;
		int color = data.getIntExtra(ActivityColorSelect.COLOR_KEY, Color.BLACK) | 0xFF000000;
		
		if(target == TARGET_FIRST) colors.setFirstColor(color);
		else if(target == TARGET_SECOND) colors.setSecondColor(color);
		updateColors();
	}
	
	@Override
	public void onColorsChanged()
	{
		updateColors();
	}
}
