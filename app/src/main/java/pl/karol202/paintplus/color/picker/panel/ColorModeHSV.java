/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package pl.karol202.paintplus.color.picker.panel;

import android.graphics.Color;
import pl.karol202.paintplus.color.HSVToRGB;
import pl.karol202.paintplus.color.RGBToHSV;

class ColorModeHSV implements ColorMode
{
	private ColorChannel channelHue;
	private ColorChannel channelSaturation;
	private ColorChannel channelValue;
	
	private HSVToRGB hsvToRGB;
	private RGBToHSV rgbToHSV;
	
	ColorModeHSV()
	{
		channelHue = new ColorChannel(this, ColorChannel.ColorChannelType.HUE);
		channelSaturation = new ColorChannel(this, ColorChannel.ColorChannelType.SATURATION);
		channelValue = new ColorChannel(this, ColorChannel.ColorChannelType.VALUE);
		
		hsvToRGB = new HSVToRGB();
		rgbToHSV = new RGBToHSV();
	}
	
	@Override
	public ChannelXYSet getChannelXYSetForMainChannel(ColorChannel mainChannel)
	{
		if(mainChannel == channelHue) return new ChannelXYSet(channelValue, channelSaturation);
		else if(mainChannel == channelSaturation) return new ChannelXYSet(channelValue, channelHue);
		else if(mainChannel == channelValue) return new ChannelXYSet(channelSaturation, channelHue);
		else return null;
	}
	
	@Override
	public ColorChannel[] getChannels()
	{
		return new ColorChannel[] { channelHue, channelSaturation, channelValue };
	}
	
	@Override
	public int getColor()
	{
		hsvToRGB.setColor(channelHue.getValue(), channelSaturation.getValue(), channelValue.getValue());
		return Color.rgb(hsvToRGB.getR(), hsvToRGB.getG(), hsvToRGB.getB());
	}
	
	@Override
	public void setColor(int color)
	{
		rgbToHSV.setColor(Color.red(color), Color.green(color), Color.blue(color));
		channelHue.setValue(rgbToHSV.getH());
		channelSaturation.setValue(rgbToHSV.getS());
		channelValue.setValue(rgbToHSV.getV());
	}
}