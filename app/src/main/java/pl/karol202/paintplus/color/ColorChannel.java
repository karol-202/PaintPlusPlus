package pl.karol202.paintplus.color;

import pl.karol202.paintplus.R;

import java.util.ArrayList;

import static pl.karol202.paintplus.color.ColorChannel.ColorChannelType.HSV;
import static pl.karol202.paintplus.color.ColorChannel.ColorChannelType.RGB;

public enum ColorChannel
{
	RED(R.string.channel__red, R.drawable.ic_channel_red, RGB, 255),
	GREEN(R.string.channel__green, R.drawable.ic_channel_green, RGB, 255),
	BLUE(R.string.channel__blue, R.drawable.ic_channel_blue, RGB, 255),
	HUE(R.string.channel__hue, R.drawable.ic_channel_hue, HSV, 359),
	SATURATION(R.string.channel__saturation, R.drawable.ic_channel_saturation, HSV, 100),
	VALUE(R.string.channel__value, R.drawable.ic_channel_value, HSV, 100);
	
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
	
	public void setName(int name)
	{
		this.name = name;
	}
	
	public int getIcon()
	{
		return icon;
	}
	
	public void setIcon(int icon)
	{
		this.icon = icon;
	}
	
	public ColorChannelType getType()
	{
		return type;
	}
	
	public void setType(ColorChannelType type)
	{
		this.type = type;
	}
	
	public int getMaxValue()
	{
		return maxValue;
	}
	
	public void setMaxValue(int maxValue)
	{
		this.maxValue = maxValue;
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
	
	public enum ColorChannelType
	{
		RGB, HSV
	}
}