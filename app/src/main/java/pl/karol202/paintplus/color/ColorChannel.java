package pl.karol202.paintplus.color;

import pl.karol202.paintplus.R;

public enum ColorChannel
{
	RED(R.string.channel_red, R.drawable.ic_channel_red),
	GREEN(R.string.channel_green, R.drawable.ic_channel_green),
	BLUE(R.string.channel_blue, R.drawable.ic_channel_blue),
	HUE(R.string.channel_hue, R.drawable.ic_channel_hue),
	SATURATION(R.string.channel_saturation, R.drawable.ic_channel_saturation),
	VALUE(R.string.channel_value, R.drawable.ic_channel_value);
	
	private int name;
	private int icon;
	
	ColorChannel(int name, int icon)
	{
		this.name = name;
		this.icon = icon;
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
}