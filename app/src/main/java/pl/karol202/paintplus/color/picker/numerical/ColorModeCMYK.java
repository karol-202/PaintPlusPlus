package pl.karol202.paintplus.color.picker.numerical;

import android.graphics.Color;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.CMYKToRGB;
import pl.karol202.paintplus.color.RGBToCMYK;

class ColorModeCMYK extends ColorMode
{
	private ColorChannel channelAlpha;
	private ColorChannel channelCyan;
	private ColorChannel channelMagenta;
	private ColorChannel channelYellow;
	private ColorChannel channelBlack;
	
	private ColorChannelViews channelViewAlpha;
	private ColorChannelViews channelViewCyan;
	private ColorChannelViews channelViewMagenta;
	private ColorChannelViews channelViewYellow;
	private ColorChannelViews channelViewBlack;
	
	private RGBToCMYK rgbToCMYK;
	private CMYKToRGB cmykToRGB;
	
	ColorModeCMYK(ColorPickerNumericalInterface pickerInterface)
	{
		super(pickerInterface);
		rgbToCMYK = new RGBToCMYK();
		cmykToRGB = new CMYKToRGB();
	}
	
	@Override
	void createChannels()
	{
		channelAlpha = new ColorChannel(R.string.channel_a, 255, R.color.black, useAlpha);
		channelCyan = new ColorChannel(R.string.channel_c, 255, R.color.cyan);
		channelMagenta = new ColorChannel(R.string.channel_m, 255, R.color.magenta);
		channelYellow = new ColorChannel(R.string.channel_y, 255, R.color.yellow);
		channelBlack = new ColorChannel(R.string.channel_k, 255, R.color.black);
		
		channelViewAlpha = new ColorChannelViews(pickerInterface.getChannelViewA(), channelAlpha, this);
		channelViewCyan = new ColorChannelViews(pickerInterface.getChannelViewB(), channelCyan, this);
		channelViewMagenta = new ColorChannelViews(pickerInterface.getChannelViewC(), channelMagenta, this);
		channelViewYellow = new ColorChannelViews(pickerInterface.getChannelViewD(), channelYellow, this);
		channelViewBlack = new ColorChannelViews(pickerInterface.getChannelViewE(), channelBlack, this);
	}
	
	@Override
	void updateChannels()
	{
		int color = pickerInterface.getColor();
		rgbToCMYK.setColor(Color.red(color), Color.green(color), Color.blue(color));
		
		channelAlpha.setValue(useAlpha ? Color.alpha(color) : 255);
		channelCyan.setValue(rgbToCMYK.getC());
		channelMagenta.setValue(rgbToCMYK.getM());
		channelYellow.setValue(rgbToCMYK.getY());
		channelBlack.setValue(rgbToCMYK.getK());
		
		channelViewAlpha.update();
		channelViewCyan.update();
		channelViewMagenta.update();
		channelViewYellow.update();
		channelViewBlack.update();
	}
	
	@Override
	public void onColorChanged()
	{
		cmykToRGB.setColor(channelCyan.getValue(), channelMagenta.getValue(), channelYellow.getValue(), channelBlack.getValue());
		int color = Color.argb(channelAlpha.getValue(), cmykToRGB.getR(), cmykToRGB.getG(), cmykToRGB.getB());
		pickerInterface.setColor(color);
	}
}