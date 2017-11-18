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

package pl.karol202.paintplus.tool.pickcolor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.ToolProperties;
import pl.karol202.paintplus.util.SeekBarTouchListener;

public class ColorPickProperties extends ToolProperties implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener
{
	private ToolColorPick colorPick;
	
	private View view;
	private CheckBox checkAverage;
	private SeekBar seekBarSize;
	private TextView textSize;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_color_pick, container, false);
		colorPick = (ToolColorPick) tool;
		
		checkAverage = view.findViewById(R.id.check_pick_average);
		checkAverage.setChecked(colorPick.getSize() != 0);
		checkAverage.setOnCheckedChangeListener(this);
		
		seekBarSize = view.findViewById(R.id.seekBar_pick_size);
		seekBarSize.setProgress(Math.max(0, colorPick.getSize() - 1));
		seekBarSize.setOnSeekBarChangeListener(this);
		seekBarSize.setOnTouchListener(new SeekBarTouchListener());
		
		textSize = view.findViewById(R.id.pick_size);
		textSize.setText(String.valueOf(seekBarSize.getProgress() + 1));
		
		return view;
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		seekBarSize.setProgress(isChecked ? 1 : 0);
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		setSize(progress + 1);
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
	
	private void setSize(int size)
	{
		colorPick.setSize(size);
		textSize.setText(String.valueOf(size));
	}
}