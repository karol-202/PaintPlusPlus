package pl.karol202.paintplus.color.picker.panel;

abstract class ColorMode
{
	abstract ChannelXYSet getChannelXYSetForMainChannel(ColorChannel mainChannel);
	
	abstract ColorChannel[] getChannels();
	
	abstract int getColor();
	
	abstract void setColor(int color);
}