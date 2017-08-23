package pl.karol202.paintplus.util;

public class BezierPoint
{
	private float x;
	private float y;
	private float cx;
	private float cy;
	
	public BezierPoint(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public float getX()
	{
		return x;
	}
	
	public float getY()
	{
		return y;
	}
	
	public float getCX()
	{
		return cx;
	}
	
	public void setCX(float cx)
	{
		this.cx = cx;
	}
	
	public float getCY()
	{
		return cy;
	}
	
	public void setCY(float cy)
	{
		this.cy = cy;
	}
}