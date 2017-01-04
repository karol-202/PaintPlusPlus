package pl.karol202.paintplus.util;

import android.graphics.Color;

public class ColorRGB
{
	private int red;
	private int green;
	private int blue;
	
	public ColorRGB(int color)
	{
		red = Color.red(color);
		green = Color.green(color);
		blue = Color.blue(color);
	}
	
	public ColorRGB add(ColorRGB color)
	{
		red += color.getRed();
		green += color.getGreen();
		blue += color.getBlue();
		check();
		return this;
	}
	
	public ColorRGB multiply(float value)
	{
		red *= value;
		green *= value;
		blue *= value;
		check();
		return this;
	}
	
	private void check()
	{
		if(red > 255) red = 255;
		if(green > 255) green = 255;
		if(blue > 255) blue = 255;
	}
	
	public int getColor()
	{
		return Color.rgb(red, green, blue);
	}
	
	public int getRed()
	{
		return red;
	}
	
	public void setRed(int red)
	{
		this.red = red;
	}
	
	public int getGreen()
	{
		return green;
	}
	
	public void setGreen(int green)
	{
		this.green = green;
	}
	
	public int getBlue()
	{
		return blue;
	}
	
	public void setBlue(int blue)
	{
		this.blue = blue;
	}
}