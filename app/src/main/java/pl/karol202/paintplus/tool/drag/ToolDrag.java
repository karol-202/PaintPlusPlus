package pl.karol202.paintplus.tool.drag;

import android.graphics.Canvas;
import android.graphics.PointF;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.helpers.HelpersManager;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.CoordinateSpace;
import pl.karol202.paintplus.tool.StandardTool;
import pl.karol202.paintplus.tool.ToolProperties;

public class ToolDrag extends StandardTool
{
	private HelpersManager helpersManager;
	private Layer layer;
	private int oldLayerX;
	private int oldLayerY;
	private float oldTouchX;
	private float oldTouchY;
	
	public ToolDrag(Image image)
	{
		super(image);
		helpersManager = image.getHelpersManager();
	}
	
	@Override
	public int getName()
	{
		return R.string.tool_drag;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_tool_drag_black_24dp;
	}
	
	@Override
	public Class<? extends ToolProperties> getPropertiesFragmentClass()
	{
		return DragProperties.class;
	}
	
	@Override
	public CoordinateSpace getCoordinateSpace()
	{
		return CoordinateSpace.IMAGE_SPACE;
	}
	
	@Override
	public boolean isUsingSnapping()
	{
		return false;
	}
	
	@Override
	public boolean onTouchStart(float x, float y)
	{
		x -= image.getViewX();
		y -= image.getViewY();
		
		layer = image.getSelectedLayer();
		oldLayerX = layer.getX();
		oldLayerY = layer.getY();
		oldTouchX = x;
		oldTouchY = y;
		return true;
	}
	
	@Override
	public boolean onTouchMove(float x, float y)
	{
		x -= image.getViewX();
		y -= image.getViewY();
		
		int deltaTouchX = Math.round(x - oldTouchX);
		int deltaTouchY = Math.round(y - oldTouchY);
		
		PointF snapped = new PointF(oldLayerX + deltaTouchX, oldLayerY + deltaTouchY);
		helpersManager.snapPoint(snapped);
		
		layer.setX((int) snapped.x);
		layer.setY((int) snapped.y);
		return true;
	}
	
	@Override
	public boolean onTouchStop(float x, float y)
	{
		return true;
	}
	
	@Override
	public boolean isImageLimited()
	{
		return false;
	}
	
	@Override
	public boolean doesScreenDraw(boolean layerVisible)
	{
		return false;
	}
	
	@Override
	public boolean isDrawingOnTop()
	{
		return false;
	}
	
	@Override
	public void onScreenDraw(Canvas canvas) { }
}