package pl.karol202.paintplus.color.picker.panel;

class ChannelXYSet
{
	private ColorChannel channelX;
	private ColorChannel channelY;
	
	public ChannelXYSet(ColorChannel channelX, ColorChannel channelY)
	{
		this.channelX = channelX;
		this.channelY = channelY;
	}
	
	public ColorChannel getChannelX()
	{
		return channelX;
	}
	
	public ColorChannel getChannelY()
	{
		return channelY;
	}
}