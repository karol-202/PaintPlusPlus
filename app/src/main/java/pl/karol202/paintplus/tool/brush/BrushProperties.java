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

package pl.karol202.paintplus.tool.brush;

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

public class BrushProperties extends ToolProperties implements SeekBar.OnSeekBarChangeListener
{
	private ToolBrush brush;
	
	private View view;
	private SeekBar seekBrushSize;
	private TextView textBrushSize;
	private SeekBar seekBrushShapeOffset;
	private TextView textBrushShapeOffset;
	private SeekBar seekBrushTranslucency;
	private TextView textBrushTranslucency;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_brush, container, false);
		brush = (ToolBrush) tool;
		
		seekBrushSize = view.findViewById(R.id.seekBar_brush_size);
		seekBrushSize.setProgress((int) brush.getSize() - 1);
		seekBrushSize.setOnSeekBarChangeListener(this);
		seekBrushSize.setOnTouchListener(new SeekBarTouchListener());
		
		textBrushSize = view.findViewById(R.id.brush_size);
		textBrushSize.setText(String.valueOf(seekBrushSize.getProgress() + 1));
		
		seekBrushShapeOffset = view.findViewById(R.id.seek_brush_shape_offset);
		seekBrushShapeOffset.setProgress((int) brush.getShapeOffset() - 1);
		seekBrushShapeOffset.setOnSeekBarChangeListener(this);
		seekBrushShapeOffset.setOnTouchListener(new SeekBarTouchListener());
		
		textBrushShapeOffset = view.findViewById(R.id.brush_shape_offset);
		textBrushShapeOffset.setText(String.valueOf(seekBrushShapeOffset.getProgress() + 1));
		
		seekBrushTranslucency = view.findViewById(R.id.seekBar_brush_translucency);
		seekBrushTranslucency.setProgress((int) ((1 - brush.getOpacity()) * 100));
		seekBrushTranslucency.setOnSeekBarChangeListener(this);
		seekBrushTranslucency.setOnTouchListener(new SeekBarTouchListener());
		
		textBrushTranslucency = view.findViewById(R.id.brush_translucency);
		textBrushTranslucency.setText(String.format(Locale.US, "%1$d%%", seekBrushTranslucency.getProgress()));
		return view;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		if(seekBar == seekBrushSize) setBrushSize(progress + 1);
		else if(seekBar == seekBrushShapeOffset) setBrushShapeOffset(progress + 1);
		else if(seekBar == seekBrushTranslucency) setBrushTranslucency(progress);
	}
	
	private void setBrushSize(int size)
	{
		brush.setSize(size);
		textBrushSize.setText(String.valueOf(size));
	}
	
	private void setBrushShapeOffset(int offset)
	{
		brush.setShapeOffset(offset);
		textBrushShapeOffset.setText(String.valueOf(offset));
	}
	
	private void setBrushTranslucency(int translucency)
	{
		brush.setOpacity(1 - (translucency / 100f));
		textBrushTranslucency.setText(String.format(Locale.US, "%d%%", translucency));
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
}
