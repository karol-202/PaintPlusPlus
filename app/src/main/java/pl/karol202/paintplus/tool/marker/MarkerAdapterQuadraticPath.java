package pl.karol202.paintplus.tool.marker;

public class MarkerAdapterQuadraticPath extends MarkerAdapterSimple
{
	private float lastX;
	private float lastY;
	
	MarkerAdapterQuadraticPath(ToolMarker marker)
	{
		super(marker);
	}
	
	@Override
	public void onBeginDraw(float x, float y)
	{
		super.onBeginDraw(x, y);
		
		lastX = -1;
		lastY = -1;
	}
	
	@Override
	public void onDraw(float x, float y)
	{
		super.onDraw(x, y);
		if(lastX != -1 && lastY != -1)
		{
			path.quadTo(lastX, lastY, x, y);
			lastX = -1;
			lastY = -1;
		}
		else
		{
			lastX = x;
			lastY = y;
		}
	}
	
	@Override
	public void onEndDraw(float x, float y)
	{
		if(lastX != -1 && lastY != -1) path.quadTo(lastX, lastY, x, y);
		else path.lineTo(x, y);
		
		lastX = -1;
		lastY = -1;
		
		super.onEndDraw(x, y);
	}
}