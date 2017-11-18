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

import pl.karol202.paintplus.R;

class ColorChannel
{
	enum ColorChannelType
	{
		RED(R.string.channel_r, 255),
		GREEN(R.string.channel_g, 255),
		BLUE(R.string.channel_b, 255),
		HUE(R.string.channel_h, 359),
		SATURATION(R.string.channel_s, 100),
		VALUE(R.string.channel_v, 100);
		
		private int name;
		private int maxValue;
		
		ColorChannelType(int name, int maxValue)
		{
			this.name = name;
			this.maxValue = maxValue;
		}
		
		public int getName()
		{
			return name;
		}
		
		public int getMaxValue()
		{
			return maxValue;
		}
	}
	
	private ColorMode mode;
	private ColorChannelType type;
	
	private int value;
	
	ColorChannel(ColorMode mode, ColorChannelType type)
	{
		this.mode = mode;
		this.type = type;
	}
	
	ColorMode getMode()
	{
		return mode;
	}
	
	ColorChannelType getType()
	{
		return type;
	}
	
	int getName()
	{
		return type.getName();
	}
	
	int getMaxValue()
	{
		return type.getMaxValue();
	}
	
	int getValue()
	{
		return value;
	}
	
	void setValue(int value)
	{
		this.value = value;
	}
}