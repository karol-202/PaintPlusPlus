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
		
		ColorChannelType(int name, int maxValue)
		{
			this.name = name;
			this.maxValue = maxValue;
		}
		
		private int name;
		private int maxValue;
		
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
	
	int getValue()
	{
		return value;
	}
	
	void setValue(int value)
	{
		this.value = value;
	}
}