package pl.karol202.paintplus.tool.drag;

import android.graphics.Canvas;
import android.view.MotionEvent;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.Layer;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolProperties;

public class ToolDrag extends Tool
{
	private Layer layer;
	private int oldLayerX;
	private int oldLayerY;
	private float oldTouchX;
	private float oldTouchY;
	
	public ToolDrag(Image image)
	{
		super(image);
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
	public boolean isLayerSpace()
	{
		return false;
	}
	
	@Override
	public boolean doesScreenDraw()
	{
		return false;
	}
	
	@Override
	public boolean isImageLimited()
	{
		return false;
	}
	
	@Override
	public boolean onTouch(MotionEvent event)
	{
		float x = event.getX() - image.getViewX();
		float y = event.getY() - image.getViewY();
		if(event.getAction() == MotionEvent.ACTION_DOWN) onTouchStart(x, y);
		else if(event.getAction() == MotionEvent.ACTION_MOVE) onTouchMove(x, y);
		return true;
	}
	
	private void onTouchStart(float x, float y)
	{
		layer = image.getSelectedLayer();
		oldLayerX = layer.getX();
		oldLayerY = layer.getY();
		oldTouchX = x;
		oldTouchY = y;
	}
	
	private void onTouchMove(float x, float y)
	{
		int deltaTouchX = Math.round(x - oldTouchX);
		int deltaTouchY = Math.round(y - oldTouchY);
		layer.setX(oldLayerX + deltaTouchX);
		layer.setY(oldLayerY + deltaTouchY);
	}
	
	@Override
	public void onScreenDraw(Canvas canvas) { }
}