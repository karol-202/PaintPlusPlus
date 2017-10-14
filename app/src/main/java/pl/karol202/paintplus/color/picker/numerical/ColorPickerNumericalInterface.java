package pl.karol202.paintplus.color.picker.numerical;

import android.view.View;

class ColorPickerNumericalInterface
{
	private ColorNumericalFragment fragment;
	
	ColorPickerNumericalInterface(ColorNumericalFragment fragment)
	{
		this.fragment = fragment;
	}
	
	boolean isUsingAlpha()
	{
		return fragment.isUsingAlpha();
	}
	
	View getChannelViewA()
	{
		return fragment.getChannelViewA();
	}
	
	View getChannelViewB()
	{
		return fragment.getChannelViewB();
	}
	
	View getChannelViewC()
	{
		return fragment.getChannelViewC();
	}
	
	View getChannelViewD()
	{
		return fragment.getChannelViewD();
	}
	
	View getChannelViewE()
	{
		return fragment.getChannelViewE();
	}
	
	int getColor()
	{
		return fragment.getCurrentColor();
	}
	
	void setColor(int color)
	{
		fragment.updateColor(color, true);
	}
}