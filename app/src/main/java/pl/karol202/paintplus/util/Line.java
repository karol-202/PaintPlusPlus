package pl.karol202.paintplus.util;

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
}
