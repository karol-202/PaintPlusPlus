package pl.karol202.paintplus.tool.marker;

import pl.karol202.paintplus.util.BezierPoint;

class MarkerAdapterCubicPath extends MarkerAdapterSimple
{
	private BezierPoint previousPoint;
	private BezierPoint lastPoint;
	
	MarkerAdapterCubicPath(ToolMarker marker)
	{
		super(marker);
	}
	
	@Override
	public void onBeginDraw(float x, float y)
	{
		super.onBeginDraw(x, y);
		
		previousPoint = null;
		lastPoint = new BezierPoint(x, y);
	}
	
	@Override
	public void onDraw(float x, float y)
	{
		super.onDraw(x, y);
		if(lastPoint == null) return;
		BezierPoint currentPoint = new BezierPoint(x, y);
		if(previousPoint != null)
		{
			lastPoint.setCX((currentPoint.getX() - previousPoint.getX()) / 3);
			lastPoint.setCY((currentPoint.getY() - previousPoint.getY()) / 3);
			path.cubicTo(previousPoint.getX() + previousPoint.getCX(), previousPoint.getY() + previousPoint.getCY(),
						 lastPoint.getX() - lastPoint.getCX(), lastPoint.getY() - lastPoint.getCY(),
							 lastPoint.getX(), lastPoint.getY());
		}
		else
		{
			lastPoint.setCX((currentPoint.getX() - lastPoint.getX()) / 3);
			lastPoint.setCY((currentPoint.getY() - lastPoint.getY()) / 3);
		}
		previousPoint = lastPoint;
		lastPoint = currentPoint;
	}
	
	@Override
	public void onEndDraw(float x, float y)
	{
		if(lastPoint == null) return;
		if(previousPoint != null)
		{
			lastPoint.setCX((x - previousPoint.getX()) / 3);
			lastPoint.setCY((y - previousPoint.getY()) / 3);
			path.cubicTo(previousPoint.getX() + previousPoint.getCX(), previousPoint.getY() + previousPoint.getCY(),
					lastPoint.getX() - lastPoint.getCX(), lastPoint.getY() - lastPoint.getCY(),
						lastPoint.getX(), lastPoint.getY());
		}
		path.lineTo(x, y);
		
		previousPoint = null;
		lastPoint = null;
		super.onEndDraw(x, y);
	}
}