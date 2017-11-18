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

package pl.karol202.paintplus.tool.shape.circle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.shape.ShapeProperties;
import pl.karol202.paintplus.util.SeekBarTouchListener;

public class CircleProperties extends ShapeProperties implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener
{
	private ShapeCircle circle;
	
	private View view;
	private CheckBox checkFill;
	private SeekBar seekBarWidth;
	private TextView textWidth;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_circle, container, false);
		
		circle = (ShapeCircle) shape;
		
		checkFill = view.findViewById(R.id.check_circle_fill);
		checkFill.setChecked(circle.isFilled());
		checkFill.setOnCheckedChangeListener(this);
		
		seekBarWidth = view.findViewById(R.id.seekBar_circle_width);
		seekBarWidth.setProgress(circle.getCircleWidth() - 1);
		seekBarWidth.setOnSeekBarChangeListener(this);
		seekBarWidth.setOnTouchListener(new SeekBarTouchListener());
		
		textWidth = view.findViewById(R.id.circle_width);
		textWidth.setText(String.valueOf(circle.getCircleWidth()));
		
		return view;
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		circle.setFill(isChecked);
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		circle.setCircleWidth(progress + 1);
		textWidth.setText(String.valueOf(progress + 1));
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
}