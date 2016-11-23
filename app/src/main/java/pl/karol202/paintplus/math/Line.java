package pl.karol202.paintplus.math;

public class Line
{
	private Vector2f start;
	private Vector2f end;

	public Line(Vector2f start, Vector2f end)
	{
		this.start = start;
		this.end = end;
	}

	public Vector2f getStart()
	{
		return start;
	}

	public void setStart(Vector2f start)
	{
		this.start = start;
	}

	public Vector2f getEnd()
	{
		return end;
	}

	public void setEnd(Vector2f end)
	{
		this.end = end;
	}

	public Line perpendicular(float length)
	{
		Vector2f relStart = end.sub(start).normalize().rotate(90).mul(length / 2);
		Vector2f relEnd = relStart.mul(-1);
		Vector2f absStart = relStart.add(start);
		Vector2f absEnd = relEnd.add(start);
		return new Line(absStart, absEnd);
	}
}
