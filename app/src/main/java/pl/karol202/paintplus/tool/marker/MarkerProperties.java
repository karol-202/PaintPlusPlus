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

package pl.karol202.paintplus.tool.marker;

import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.Locale;

public class MarkerProperties extends ToolProperties implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener
{
	private ToolMarker marker;

	private View view;
	private SeekBar seekMarkerSize;
	private TextView textMarkerSize;
	private SeekBar seekMarkerTranslucency;
	private TextView textMarkerTranslucency;
	private CheckBox checkSmoothEdge;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_marker, container, false);
		marker = (ToolMarker) tool;
		
		seekMarkerSize = view.findViewById(R.id.seekBar_marker_size);
		seekMarkerSize.setProgress((int) (marker.getSize() - 1));
		seekMarkerSize.setOnSeekBarChangeListener(this);
		seekMarkerSize.setOnTouchListener(new SeekBarTouchListener());
		
		textMarkerSize = view.findViewById(R.id.marker_size);
		textMarkerSize.setText(String.valueOf(seekMarkerSize.getProgress() + 1));
		
		seekMarkerTranslucency = view.findViewById(R.id.seekBar_marker_translucency);
		seekMarkerTranslucency.setProgress((int) ((1 - marker.getOpacity()) * 100));
		seekMarkerTranslucency.setOnSeekBarChangeListener(this);
		seekMarkerTranslucency.setOnTouchListener(new SeekBarTouchListener());
		
		textMarkerTranslucency = view.findViewById(R.id.marker_translucency);
		textMarkerTranslucency.setText(String.format(Locale.US, "%1$d%%", seekMarkerTranslucency.getProgress()));
		
		checkSmoothEdge = view.findViewById(R.id.check_smooth_edge);
		checkSmoothEdge.setChecked(marker.isSmoothEdge());
		checkSmoothEdge.setOnCheckedChangeListener(this);
		return view;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		if(seekBar == seekMarkerSize) setMarkerSize(progress);
		else if(seekBar == seekMarkerTranslucency) setMarkerTranslucency(progress);
	}
	
	private void setMarkerSize(int size)
	{
		marker.setSize(size);
		textMarkerSize.setText(String.valueOf(size + 1));
	}
	
	private void setMarkerTranslucency(int translucency)
	{
		marker.setOpacity(1 - (translucency / 100f));
		textMarkerTranslucency.setText(String.format(Locale.US, "%1$d%%", translucency));
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if(buttonView == checkSmoothEdge) marker.setSmoothEdge(isChecked);
	}
}
