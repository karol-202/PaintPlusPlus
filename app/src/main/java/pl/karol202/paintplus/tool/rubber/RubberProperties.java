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

package pl.karol202.paintplus.tool.rubber;

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

public class RubberProperties extends ToolProperties implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener
{
	private ToolRubber rubber;
	
	private View view;
	private SeekBar seekRubberSize;
	private TextView textRubberSize;
	private CheckBox checkSmooth;
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_rubber, container, false);
		rubber = (ToolRubber) tool;
		
		seekRubberSize = view.findViewById(R.id.seekBar_rubber_size);
		seekRubberSize.setProgress((int) rubber.getSize() - 1);
		seekRubberSize.setOnSeekBarChangeListener(this);
		seekRubberSize.setOnTouchListener(new SeekBarTouchListener());
		
		textRubberSize = view.findViewById(R.id.rubber_size);
		textRubberSize.setText(String.valueOf(seekRubberSize.getProgress() + 1));
		
		checkSmooth = view.findViewById(R.id.check_rubber_smooth);
		checkSmooth.setChecked(rubber.isSmooth());
		checkSmooth.setOnCheckedChangeListener(this);
		return view;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		setRubberSize(progress);
	}
	
	private void setRubberSize(int size)
	{
		rubber.setSize(size + 1);
		textRubberSize.setText(String.valueOf(size + 1));
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		rubber.setSmooth(isChecked);
	}
}