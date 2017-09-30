package pl.karol202.paintplus.color.picker.panel;

import android.graphics.Color;

class ColorModeRGB extends ColorMode
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
	ChannelXYSet getChannelXYSetForMainChannel(ColorChannel mainChannel)
	{
		if(mainChannel == channelRed) return new ChannelXYSet(channelBlue, channelGreen);
		else if(mainChannel == channelGreen) return new ChannelXYSet(channelBlue, channelRed);
		else if(mainChannel == channelBlue) return new ChannelXYSet(channelGreen, channelRed);
		else return null;
	}
	
	@Override
	ColorChannel[] getChannels()
	{
		return new ColorChannel[] { channelRed, channelGreen, channelBlue };
	}
	
	@Override
	int getColor()
	{
		return Color.rgb(channelRed.getValue(), channelGreen.getValue(), channelBlue.getValue());
	}
	
	@Override
	void setColor(int color)
	{
		channelRed.setValue(Color.red(color));
		channelGreen.setValue(Color.green(color));
		channelBlue.setValue(Color.blue(color));
	}
}