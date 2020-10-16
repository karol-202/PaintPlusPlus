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

import android.graphics.Color;

class ColorModeRGB implements ColorMode
{
	private ColorChannel channelRed;
	private ColorChannel channelGreen;
	private ColorChannel channelBlue;
	
	ColorModeRGB()
	{
		channelRed = new ColorChannel(this, ColorChannel.ColorChannelType.RED);
		channelGreen = new ColorChannel(this, ColorChannel.ColorChannelType.GREEN);
		channelBlue = new ColorChannel(this, ColorChannel.ColorChannelType.BLUE);
	}
	
	@Override
	public ChannelXYSet getChannelXYSetForMainChannel(ColorChannel mainChannel)
	{
		if(mainChannel == channelRed) return new ChannelXYSet(channelBlue, channelGreen);
		else if(mainChannel == channelGreen) return new ChannelXYSet(channelBlue, channelRed);
		else if(mainChannel == channelBlue) return new ChannelXYSet(channelGreen, channelRed);
		else return null;
	}
	
	@Override
	public ColorChannel[] getChannels()
	{
		return new ColorChannel[] { channelRed, channelGreen, channelBlue };
	}
	
	@Override
	public int getColor()
	{
		return Color.rgb(channelRed.getValue(), channelGreen.getValue(), channelBlue.getValue());
	}
	
	@Override
	public void setColor(int color)
	{
		channelRed.setValue(Color.red(color));
		channelGreen.setValue(Color.green(color));
		channelBlue.setValue(Color.blue(color));
	}
}