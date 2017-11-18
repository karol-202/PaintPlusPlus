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

package pl.karol202.paintplus.color.picker.panel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.picker.ColorPickerFragment;
import pl.karol202.paintplus.color.picker.DualColorPreviewView;

public class ColorPanelFragment extends ColorPickerFragment
{
	private ColorModeRGB modeRGB;
	private ColorModeHSV modeHSV;
	private ColorMode currentMode;
	
	private ColorChannelsAdapter channelsAdapter;
	
	private View view;
	private DualColorPreviewView colorView;
	private ColorPickerSquarePanel squarePanel;
	private ColorPickerBar bar;
	private Spinner spinnerChannel;
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		modeRGB = new ColorModeRGB();
		modeHSV = new ColorModeHSV();
		
		channelsAdapter = new ColorChannelsAdapter(getContext());
		
		view = inflater.inflate(R.layout.color_picker_panel, container, false);
		
		colorView = view.findViewById(R.id.view_colors_panel_color);
		colorView.setOldColor(getCurrentColor());
		colorView.setNewColor(getCurrentColor());
		
		squarePanel = view.findViewById(R.id.color_picker_square_panel);
		squarePanel.setOnColorPanelUpdateListener(new ColorPickerSquarePanel.OnColorPanelUpdateListener() {
			@Override
			public void onChannelsValueChanged()
			{
				updateColor();
			}
		});
		
		bar = view.findViewById(R.id.color_picker_bar);
		bar.setOnColorBarUpdateListener(new ColorPickerBar.OnColorBarUpdateListener() {
			@Override
			public void onChannelValueChanged()
			{
				updateColor();
				squarePanel.update();
			}
		});
		
		spinnerChannel = view.findViewById(R.id.spinner_color_picker_channel);
		spinnerChannel.setAdapter(channelsAdapter);
		spinnerChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
			{
				setMainChannel(currentMode.getChannels()[position]);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) { }
		});
		
		setMode(modeHSV);
		return view;
	}
	
	@Override
	protected void onColorModeSelected(int actionId)
	{
		switch(actionId)
		{
		case R.id.mode_rgb: setMode(modeRGB); break;
		case R.id.mode_hsv: setMode(modeHSV); break;
		}
	}
	
	@Override
	protected boolean isColorModeSupported(int actionId)
	{
		return actionId == R.id.mode_rgb ||
				actionId == R.id.mode_hsv;
	}
	
	@Override
	protected void onTabSelected()
	{
		currentMode.setColor(getCurrentColor());
		
		colorView.setNewColor(getCurrentColor());
		squarePanel.update();
		bar.update();
	}
	
	private void updateColor()
	{
		if(currentMode == modeHSV) modeRGB.setColor(currentMode.getColor());
		else if(currentMode == modeRGB) modeHSV.setColor(currentMode.getColor());
		
		colorView.setNewColor(currentMode.getColor());
		setCurrentColor(currentMode.getColor());
	}
	
	private void setMode(ColorMode mode)
	{
		this.currentMode = mode;
		ColorChannel mainChannel = currentMode.getChannels()[0];
		
		squarePanel.setModeAndMainChannel(currentMode, mainChannel);
		bar.setChannel(mainChannel);
		channelsAdapter.setColorMode(currentMode);
		spinnerChannel.setSelection(0);
	}
	
	private void setMainChannel(ColorChannel mainChannel)
	{
		squarePanel.setModeAndMainChannel(currentMode, mainChannel);
		bar.setChannel(mainChannel);
	}
}