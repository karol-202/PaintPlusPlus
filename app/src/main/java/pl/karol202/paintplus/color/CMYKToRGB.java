package pl.karol202.paintplus.color;

public class CMYKToRGB
{
	private int r;
	private int g;
	private int b;
	
	public void setColor(int c_, int m_, int y_, int k_)
	{
		float c = c_ / 255f;
		float m = m_ / 255f;
		float y = y_ / 255f;
		float k = k_ / 255f;
		
		r = Math.round((1 - c) * (1 - k) * 255);
		g = Math.round((1 - m) * (1 - k) * 255);
		b = Math.round((1 - y) * (1 - k) * 255);
	}
	
	public int getR()
	{
		return r;
	}
	
	public int getG()
	{
		return g;
	}
	
	public int getB()
	{
		return b;
	}
}