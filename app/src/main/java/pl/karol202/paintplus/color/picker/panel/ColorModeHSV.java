package pl.karol202.paintplus.color.picker.panel;

class ColorModeHSV extends ColorMode
{
	private ColorChannel channelHue;
	private ColorChannel channelSaturation;
	private ColorChannel channelValue;
	
	@Override
	void createChannels()
	{
		channelHue = new ColorChannel(this, ColorChannel.ColorChannelType.HUE);
		channelSaturation = new ColorChannel(this, ColorChannel.ColorChannelType.SATURATION);
		channelValue = new ColorChannel(this, ColorChannel.ColorChannelType.VALUE);
	}
	
	@Override
	ChannelXYSet getChannelXYSetForMainChannel(ColorChannel mainChannel)
	{
		if(mainChannel == channelHue) return new ChannelXYSet(channelSaturation, channelValue);
		else if(mainChannel == channelSaturation) return new ChannelXYSet(channelHue, channelValue);
		else if(mainChannel == channelValue) return new ChannelXYSet(channelHue, channelSaturation);
		else return null;
	}
}