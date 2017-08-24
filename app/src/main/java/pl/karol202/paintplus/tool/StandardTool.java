package pl.karol202.paintplus.tool;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import pl.karol202.paintplus.helpers.HelpersManager;
import pl.karol202.paintplus.image.Image;

public abstract class StandardTool implements Tool
{
	protected Image image;
	protected HelpersManager helpersManager;
	
	public StandardTool(Image image)
	{
		this.image = image;
		this.helpersManager = image.getHelpersManager();
	}
	
	public abstract boolean onTouchStart(float x, float y);
	
	public abstract boolean onTouchMove(float x, float y);
	
	public abstract boolean onTouchStop(float x, float y);
	
	@Override
	public boolean onTouch(MotionEvent event, Context context)
	{
		PointF point = createTouchPoint(event.getX(), event.getY());
		
		boolean result = true;
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			result = onTouchStart(point.x, point.y);
			break;
		case MotionEvent.ACTION_MOVE:
			for(int i = 0; i < event.getHistorySize(); i++)
			{
				PointF historicalPoint = createTouchPoint(event.getHistoricalX(i), event.getHistoricalY(i));
				result = result && onTouchMove(historicalPoint.x, historicalPoint.y);
			}
			result = result && onTouchMove(point.x, point.y);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			result = onTouchStop(point.x, point.y);
			break;
		}
		return result;
	}
	
	private PointF createTouchPoint(float x, float y)
	{
		PointF point = new PointF(x, y);
		transformTouchCoordinates(point);
		snapTouchCoordinates(point);
		return point;
	}
	
	private void transformTouchCoordinates(PointF point)
	{
		if(getCoordinateSpace() == CoordinateSpace.SCREEN_SPACE) return;
		
		float x = (point.x / image.getZoom()) + image.getViewX();
		float y = (point.y / image.getZoom()) + image.getViewY();
		
		if(getCoordinateSpace() == CoordinateSpace.LAYER_SPACE)
		{
			x -= image.getSelectedLayerX();
			y -= image.getSelectedLayerY();
		}
		
		point.x = x;
		point.y = y;
	}
	
	private void snapTouchCoordinates(PointF point)
	{
		if(isUsingSnapping()) helpersManager.snapPoint(point);
	}
}