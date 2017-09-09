package pl.karol202.paintplus.tool.drag;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.StandardTool;
import pl.karol202.paintplus.tool.ToolCoordinateSpace;
import pl.karol202.paintplus.tool.ToolProperties;

public class ToolDrag extends StandardTool
{
	private int oldLayerX;
	private int oldLayerY;
	private float oldTouchX;
	private float oldTouchY;
	
	private Rect dirtyRect;
	
	public ToolDrag(Image image)
	{
		super(image);
		dirtyRect = new Rect();
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
	public ToolCoordinateSpace getCoordinateSpace()
	{
		return ToolCoordinateSpace.IMAGE_SPACE;
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
		
		layer.setPosition((int) snapped.x, (int) snapped.y);
		
		expandDirtyRect();
		return true;
	}
	
	private void expandDirtyRect()
	{
		dirtyRect.left = Math.min(dirtyRect.left, layer.getX());
		dirtyRect.top = Math.min(dirtyRect.top, layer.getY());
		dirtyRect.right = Math.max(dirtyRect.right, layer.getX() + layer.getWidth());
		dirtyRect.bottom = Math.max(dirtyRect.bottom, layer.getY() + layer.getHeight());
	}
	
	@Override
	public boolean onTouchStop(float x, float y)
	{
		return true;
	}
	
	@Override
	public boolean providesDirtyRegion()
	{
		return true;
	}
	
	@Override
	public Rect getDirtyRegion()
	{
		return dirtyRect;
	}
	
	@Override
	public void resetDirtyRegion()
	{
		dirtyRect.setEmpty();
	}
	
	@Override
	public boolean doesOnLayerDraw(boolean layerVisible)
	{
		return false;
	}
	
	@Override
	public boolean doesOnTopDraw()
	{
		return false;
	}
	
	@Override
	public ToolCoordinateSpace getOnLayerDrawingCoordinateSpace()
	{
		return null;
	}
	
	@Override
	public ToolCoordinateSpace getOnTopDrawingCoordinateSpace()
	{
		return null;
	}
	
	@Override
	public void onLayerDraw(Canvas canvas) { }
	
	@Override
	public void onTopDraw(Canvas canvas) { }
}