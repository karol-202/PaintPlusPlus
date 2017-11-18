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

import android.graphics.Color;
import pl.karol202.paintplus.R;

class ColorModeRGB extends ColorMode
{
	private ColorChannel channelAlpha;
	private ColorChannel channelRed;
	private ColorChannel channelGreen;
	private ColorChannel channelBlue;
	
	private ColorChannelViews channelViewAlpha;
	private ColorChannelViews channelViewRed;
	private ColorChannelViews channelViewGreen;
	private ColorChannelViews channelViewBlue;
	private ColorChannelViews channelViewEmpty;
	
	ColorModeRGB(ColorPickerNumericalInterface pickerInterface)
	{
		super(pickerInterface);
	}
	
	@Override
	void createChannels()
	{
		channelAlpha = new ColorChannel(R.string.channel_a, 255, R.color.black, useAlpha);
		channelRed = new ColorChannel(R.string.channel_r, 255, R.color.red);
		channelGreen = new ColorChannel(R.string.channel_g, 255, R.color.green);
		channelBlue = new ColorChannel(R.string.channel_b, 255, R.color.blue);
		
		channelViewAlpha = new ColorChannelViews(pickerInterface.getChannelViewA(), channelAlpha, this);
		channelViewRed = new ColorChannelViews(pickerInterface.getChannelViewB(), channelRed, this);
		channelViewGreen = new ColorChannelViews(pickerInterface.getChannelViewC(), channelGreen, this);
		channelViewBlue = new ColorChannelViews(pickerInterface.getChannelViewD(), channelBlue, this);
		channelViewEmpty = new ColorChannelViews(pickerInterface.getChannelViewE());
	}
	
	@Override
	void updateChannels()
	{
		int color = pickerInterface.getColor();
		
		channelAlpha.setValue(useAlpha ? Color.alpha(color) : 255);
		channelRed.setValue(Color.red(color));
		channelGreen.setValue(Color.green(color));
		channelBlue.setValue(Color.blue(color));
		
		channelViewAlpha.update();
		channelViewRed.update();
		channelViewGreen.update();
		channelViewBlue.update();
		channelViewEmpty.update();
	}
	
	@Override
	public void onColorChanged()
	{
		int color = Color.argb(channelAlpha.getValue(), channelRed.getValue(), channelGreen.getValue(), channelBlue.getValue());
		pickerInterface.setColor(color);
	}
}