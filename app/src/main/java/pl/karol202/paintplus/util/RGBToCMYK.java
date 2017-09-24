package pl.karol202.paintplus.util;

public class RGBToCMYK
{
	private int c;
	private int m;
	private int y;
	private int k;
	
	public void setColor(int r_, int g_, int b_)
	{
		float r = r_ / 255f;
		float g = g_ / 255f;
		float b = b_ / 255f;
		
		float k = 1 - Math.max(Math.max(r, g), b);
		float c = (1 - r - k) / (1 - k);
		float m = (1 - g - k) / (1 - k);
		float y = (1 - b - k) / (1 - k);
		
		this.c = Math.round(c * 255);
		this.m = Math.round(m * 255);
		this.y = Math.round(y * 255);
		this.k = Math.round(k * 255);
	}
	
	public int getC()
	{
		return c;
	}
	
	public int getM()
	{
		return m;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getK()
	{
		return k;
	}
}