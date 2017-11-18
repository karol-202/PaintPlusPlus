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

package pl.karol202.paintplus.tool.fill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.ToolProperties;
import pl.karol202.paintplus.util.SeekBarTouchListener;

import java.util.Locale;

public class FillProperties extends ToolProperties implements SeekBar.OnSeekBarChangeListener
{
	private ToolFill fill;
	
	private View view;
	private SeekBar seekBarThreshold;
	private TextView textThreshold;
	private SeekBar seekBarTranslucency;
	private TextView textTranslucency;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_fill, container, false);
		fill = (ToolFill) tool;
		
		seekBarThreshold = view.findViewById(R.id.seekBar_fill_threshold);
		seekBarThreshold.setProgress((int) fill.getFillThreshold());
		seekBarThreshold.setOnSeekBarChangeListener(this);
		seekBarThreshold.setOnTouchListener(new SeekBarTouchListener());
		
		textThreshold = view.findViewById(R.id.fill_threshold);
		textThreshold.setText(String.format(Locale.US, "%d%%", seekBarThreshold.getProgress()));
		
		seekBarTranslucency = view.findViewById(R.id.seekBar_fill_translucency);
		seekBarTranslucency.setProgress((int) ((1 - fill.getOpacity()) * 100));
		seekBarTranslucency.setOnSeekBarChangeListener(this);
		seekBarTranslucency.setOnTouchListener(new SeekBarTouchListener());
		
		textTranslucency = view.findViewById(R.id.fill_translucency);
		textTranslucency.setText(String.format(Locale.US, "%d%%", seekBarTranslucency.getProgress()));
		return view;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		if(seekBar == seekBarThreshold) setFillThreshold(progress);
		else if(seekBar == seekBarTranslucency) setFillTranslucency(progress);
	}
	
	private void setFillThreshold(int threshold)
	{
		fill.setFillThreshold(threshold);
		textThreshold.setText(String.format(Locale.US, "%d%%", threshold));
	}
	
	private void setFillTranslucency(int translucency)
	{
		fill.setOpacity(1 - (translucency / 100f));
		textTranslucency.setText(String.format(Locale.US, "%d%%", translucency));
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
}