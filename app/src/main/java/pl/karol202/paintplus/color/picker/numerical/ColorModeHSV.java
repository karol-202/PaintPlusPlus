package pl.karol202.paintplus.color.picker.numerical;

import android.graphics.Color;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.HSVToRGB;
import pl.karol202.paintplus.color.RGBToHSV;

class ColorModeHSV extends ColorMode
{
	private ColorChannel channelAlpha;
	private ColorChannel channelHue;
	private ColorChannel channelSaturation;
	private ColorChannel channelValue;
	
	private ColorChannelViews channelViewAlpha;
	private ColorChannelViews channelViewHue;
	private ColorChannelViews channelViewSaturation;
	private ColorChannelViews channelViewValue;
	private ColorChannelViews channelViewEmpty;
	
	private RGBToHSV rgbToHSV;
	private HSVToRGB hsvToRGB;
	
	ColorModeHSV(ColorPickerNumericalInterface pickerInterface)
	{
		super(pickerInterface);
		rgbToHSV = new RGBToHSV();
		hsvToRGB = new HSVToRGB();
	}
	
	@Override
	void createChannels()
	{
		channelAlpha = new ColorChannel(R.string.channel_a, 255, pl.karol202.paintplus.R.color.black, useAlpha);
		channelHue = new ColorChannel(R.string.channel_h, 359, R.color.color_accent);
		channelSaturation = new ColorChannel(R.string.channel_s, 100, R.color.color_accent);
		channelValue = new ColorChannel(R.string.channel_v, 100, R.color.color_accent);
		
		channelViewAlpha = new ColorChannelViews(pickerInterface.getChannelViewA(), channelAlpha, this);
		channelViewHue = new ColorChannelViews(pickerInterface.getChannelViewB(), channelHue, this);
		channelViewSaturation = new ColorChannelViews(pickerInterface.getChannelViewC(), channelSaturation, this);
		channelViewValue = new ColorChannelViews(pickerInterface.getChannelViewD(), channelValue, this);
		channelViewEmpty = new ColorChannelViews(pickerInterface.getChannelViewE());
	}
	
	@Override
	void updateChannels()
	{
		int color = pickerInterface.getColor();
		rgbToHSV.setColor(Color.red(color), Color.green(color), Color.blue(color));
		
		channelAlpha.setValue(useAlpha ? Color.alpha(color) : 255);
		channelHue.setValue(rgbToHSV.getH());
		channelSaturation.setValue(rgbToHSV.getS());
		channelValue.setValue(rgbToHSV.getV());
		
		channelViewAlpha.update();
		channelViewHue.update();
		channelViewSaturation.update();
		channelViewValue.update();
		channelViewEmpty.update();
	}
	
	@Override
	public void onColorChanged()
	{
		hsvToRGB.setColor(channelHue.getValue(), channelSaturation.getValue(), channelValue.getValue());
		int color = Color.argb(channelAlpha.getValue(), hsvToRGB.getR(), hsvToRGB.getG(), hsvToRGB.getB());
		pickerInterface.setColor(color);
	}
}