package pl.karol202.paintplus.color.picker.panel;

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
		if(mainChannel == channelRed) return new ChannelXYSet(channelGreen, channelBlue);
		else if(mainChannel == channelGreen) return new ChannelXYSet(channelRed, channelBlue);
		else if(mainChannel == channelBlue) return new ChannelXYSet(channelRed, channelGreen);
		else return null;
	}
	
	@Override
	ColorChannel[] getChannels()
	{
		return new ColorChannel[] { channelRed, channelGreen, channelBlue };
	}
}