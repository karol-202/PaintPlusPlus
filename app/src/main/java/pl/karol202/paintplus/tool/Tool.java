package pl.karol202.paintplus.tool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;
import pl.karol202.paintplus.helpers.HelpersManager;
import pl.karol202.paintplus.image.Image;

public abstract class Tool
{
	protected Image image;
	
	protected Tool(Image image)
	{
		this.image = image;
	}
	
	public abstract int getName();
	
	public abstract int getIcon();
	
	public abstract Class<? extends ToolProperties> getPropertiesFragmentClass();
	
	public abstract CoordinateSpace getCoordinateSpace();
	
	public abstract boolean isUsingSnapping();
	
	public boolean onTouch(MotionEvent event, HelpersManager manager, Context context)
	{
		if(isUsingSnapping())
		{
			PointF point = new PointF(event.getX(), event.getY());
			manager.snapPoint(point);
			event.setLocation(point.x, point.y);
		}
		return false;
	}
	
	public abstract boolean isImageLimited();
	
	public abstract boolean doesScreenDraw(boolean layerVisible);
	
	public abstract boolean isDrawingOnTop();

	public abstract void onScreenDraw(Canvas canvas);
}