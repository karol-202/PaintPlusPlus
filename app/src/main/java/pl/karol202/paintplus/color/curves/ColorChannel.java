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

package pl.karol202.paintplus.color.curves;

import pl.karol202.paintplus.R;

import java.util.ArrayList;

import static pl.karol202.paintplus.color.curves.ColorChannel.ColorChannelType.HSV;
import static pl.karol202.paintplus.color.curves.ColorChannel.ColorChannelType.RGB;

public enum ColorChannel
{
	RED(R.string.channel__red, R.drawable.ic_channel_red_24dp, RGB, 255),
	GREEN(R.string.channel__green, R.drawable.ic_channel_green_24dp, RGB, 255),
	BLUE(R.string.channel__blue, R.drawable.ic_channel_blue_24dp, RGB, 255),
	HUE(R.string.channel__hue, R.drawable.ic_channel_hue_24dp, HSV, 359),
	SATURATION(R.string.channel__saturation, R.drawable.ic_channel_saturation_24dp, HSV, 100),
	VALUE(R.string.channel__value, R.drawable.ic_channel_value_24dp, HSV, 100);
	
	public enum ColorChannelType
	{
		RGB, HSV
	}
	
	private int name;
	private int icon;
	private ColorChannelType type;
	private int maxValue;
	
	ColorChannel(int name, int icon, ColorChannelType type, int maxValue)
	{
		this.name = name;
		this.icon = icon;
		this.type = type;
		this.maxValue = maxValue;
	}
	
	public int getName()
	{
		return name;
	}
	
	public int getIcon()
	{
		return icon;
	}
	
	public ColorChannelType getType()
	{
		return type;
	}
	
	public int getMaxValue()
	{
		return maxValue;
	}
	
	public static ColorChannel[] filterByType(ColorChannelType type)
	{
		ArrayList<ColorChannel> filteredChannels = new ArrayList<>();
		for(ColorChannel channel : values())
			if(channel.getType() == type) filteredChannels.add(channel);
		ColorChannel[] result = new ColorChannel[filteredChannels.size()];
		filteredChannels.toArray(result);
		return result;
	}
}