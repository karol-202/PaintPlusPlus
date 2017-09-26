package pl.karol202.paintplus.color.picker.panel;

abstract class ColorMode
{
	abstract void createChannels();
	
	abstract ChannelXYSet getChannelXYSetForMainChannel(ColorChannel mainChannel);
}