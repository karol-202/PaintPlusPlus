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

package pl.karol202.paintplus.color.picker.numerical;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.picker.ColorPickerFragment;
import pl.karol202.paintplus.color.picker.DualColorPreviewView;

public class ColorNumericalFragment extends ColorPickerFragment
{
	private ColorPickerNumericalInterface pickerInterface;
	private ColorMode colorModeRGB;
	private ColorMode colorModeHSV;
	private ColorMode colorModeCMYK;
	private ColorMode currentColorMode;
	private boolean updating;

	private View view;
	private DualColorPreviewView colorView;
	private View channelViewA;
	private View channelViewB;
	private View channelViewC;
	private View channelViewD;
	private View channelViewE;
	private EditText editHex;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.color_picker_numerical, container, false);

		pickerInterface = new ColorPickerNumericalInterface(ColorNumericalFragment.this);

		colorView = view.findViewById(R.id.view_colors_numerical_color);
		colorView.setOldColor(getCurrentColor());
		colorView.setNewColor(getCurrentColor());

		channelViewA = view.findViewById(R.id.colors_numerical_channel_a);
		channelViewB = view.findViewById(R.id.colors_numerical_channel_b);
		channelViewC = view.findViewById(R.id.colors_numerical_channel_c);
		channelViewD = view.findViewById(R.id.colors_numerical_channel_d);
		channelViewE = view.findViewById(R.id.colors_numerical_channel_e);

		editHex = view.findViewById(R.id.edit_color_hex);
		addLengthFilterToEditText();
		editHex.setText(currentColorToHex());
		editHex.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }

			@Override
			public void afterTextChanged(Editable s)
			{
				onHexTextChanged();
			}
		});

		colorModeRGB = new ColorModeRGB(pickerInterface);
		colorModeHSV = new ColorModeHSV(pickerInterface);
		colorModeCMYK = new ColorModeCMYK(pickerInterface);
		setCurrentColorMode(colorModeHSV);

		return view;
	}

	private void addLengthFilterToEditText()
	{
		InputFilter[] oldFilters = editHex.getFilters();
		InputFilter[] newFilters = new InputFilter[oldFilters.length + 1];
		System.arraycopy(oldFilters, 0, newFilters, 0, oldFilters.length);
		newFilters[newFilters.length - 1] = new InputFilter.LengthFilter(isUsingAlpha() ? 8 : 6);
		editHex.setFilters(newFilters);
	}

	private String currentColorToHex()
	{
		return isUsingAlpha() ? String.format("%08X", getCurrentColor()) :
								String.format("%06X", getCurrentColor() & 0xFFFFFF);
	}

	private void onHexTextChanged()
	{
		if(updating) return;
		String text = editHex.getText().toString();
		if(text.length() == (isUsingAlpha() ? 8 : 6))
		{
			long color = Long.parseLong(text, 16);
			if(!isUsingAlpha()) color |= 0xFF000000;
			updateColor((int) color, false);
		}
		else if(text.length() == (isUsingAlpha() ? 4 : 3))
		{
			int hexValue = Integer.parseInt(text, 16);
			long color = !isUsingAlpha() ? 0xFF000000 : 0;
			color |= (hexValue & 0xF000) << 16 | (hexValue & 0xF000) << 12;
			color |= (hexValue & 0x0F00) << 12 | (hexValue & 0x0F00) << 8;
			color |= (hexValue & 0x00F0) << 8 | (hexValue & 0x00F0) << 4;
			color |= (hexValue & 0x000F) << 4 | (hexValue & 0x000F);
			updateColor((int) color, false);
		}
		currentColorMode.updateChannels();
	}

	@Override
	protected void onColorModeSelected(int actionId)
	{
		switch(actionId)
		{
		case R.id.mode_rgb: setCurrentColorMode(colorModeRGB); break;
		case R.id.mode_hsv: setCurrentColorMode(colorModeHSV); break;
		case R.id.mode_cmyk: setCurrentColorMode(colorModeCMYK); break;
		}
	}

	@Override
	protected boolean isColorModeSupported(int actionId)
	{
		return actionId == R.id.mode_rgb ||
			   actionId == R.id.mode_hsv ||
			   actionId == R.id.mode_cmyk;
	}

	@Override
	protected void onTabSelected()
	{
		currentColorMode.updateChannels();

		colorView.setNewColor(getCurrentColor());
	}

	void updateColor(int color, boolean updateHex)
	{
		updating = true;
		setCurrentColor(color);
		colorView.setNewColor(color);
		if(updateHex) editHex.setText(currentColorToHex());
		updating = false;
	}

	private void setCurrentColorMode(ColorMode colorMode)
	{
		this.currentColorMode = colorMode;
		colorMode.updateChannels();
	}

	View getChannelViewA()
	{
		return channelViewA;
	}

	View getChannelViewB()
	{
		return channelViewB;
	}

	View getChannelViewC()
	{
		return channelViewC;
	}

	View getChannelViewD()
	{
		return channelViewD;
	}

	View getChannelViewE()
	{
		return channelViewE;
	}
}
