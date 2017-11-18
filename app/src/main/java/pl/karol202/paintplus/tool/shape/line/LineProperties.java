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

package pl.karol202.paintplus.tool.shape.line;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.shape.ShapeProperties;
import pl.karol202.paintplus.util.SeekBarTouchListener;

public class LineProperties extends ShapeProperties implements SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener
{
	private ShapeLine line;
	private CapAdapter capAdapter;
	
	private View view;
	private SeekBar seekBarWidth;
	private TextView textWidth;
	private Spinner spinnerCap;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_line, container, false);
		
		line = (ShapeLine) shape;
		capAdapter = new CapAdapter(getActivity());
		
		seekBarWidth = view.findViewById(R.id.seekBar_line_width);
		seekBarWidth.setProgress(line.getLineWidth() - 1);
		seekBarWidth.setOnSeekBarChangeListener(this);
		seekBarWidth.setOnTouchListener(new SeekBarTouchListener());
		
		textWidth = view.findViewById(R.id.line_width);
		textWidth.setText(String.valueOf(line.getLineWidth()));
		
		spinnerCap = view.findViewById(R.id.spinner_line_cap);
		spinnerCap.setAdapter(capAdapter);
		spinnerCap.setSelection(line.getLineCap().ordinal());
		spinnerCap.setOnItemSelectedListener(this);
		
		return view;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		line.setLineWidth(progress + 1);
		textWidth.setText(String.valueOf(progress + 1));
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		Cap cap = capAdapter.getItem(position);
		line.setLineCap(cap);
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) { }
}