package pl.karol202.paintplus.tool.gradient;

class GradientPoint implements Comparable<GradientPoint>
{
	private float position;
	private int color;
	
	GradientPoint(float position, int color)
	{
		this.position = position;
		this.color = color;
	}
	
	GradientPoint(GradientPoint point)
	{
		this(point.position, point.color);
	}
	
	@Override
	public int compareTo(GradientPoint o)
	{
		return Float.compare(position, o.position);
	}
	
	float getPosition()
	{
		return position;
	}
	
	void setPosition(float position)
	{
		this.position = position;
	}
	
	int getColor()
	{
		return color;
	}
	
	void setColor(int color)
	{
		this.color = color;
	}
}