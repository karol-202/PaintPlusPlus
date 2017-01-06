package pl.karol202.paintplus.tool.pan;

import android.graphics.Canvas;
import android.view.MotionEvent;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolProperties;

public class ToolPan extends Tool
{
	private int oldImageX;
	private int oldImageY;
	private float oldTouchX;
	private float oldTouchY;
	
	public ToolPan(Image image)
	{
		super(image);
	}
	
	@Override
	public int getName()
	{
		return R.string.tool_pan;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_tool_pan_black_24dp;
	}
	
	@Override
	public Class<? extends ToolProperties> getPropertiesFragmentClass()
	{
		return PanProperties.class;
	}
	
	@Override
	public boolean onTouch(MotionEvent event)
	{
		float x = event.getX() - image.getViewX();
		float y = event.getY() - image.getViewY();
		if(event.getAction() == MotionEvent.ACTION_DOWN) onTouchStart(x, y);
		else if(event.getAction() == MotionEvent.ACTION_MOVE) onTouchMove(x, y);
		else if(event.getAction() == MotionEvent.ACTION_UP) onTouchStop(x, y);
		return true;
	}
	
	private void onTouchStart(float x, float y)
	{
		oldImageX = image.getViewX();
		oldImageY = image.getViewY();
		oldTouchX = x;
		oldTouchY = y;
	}
	
	private void onTouchMove(float x, float y)
	{
		int deltaTouchX = Math.round(x - oldTouchX);
		int deltaTouchY = Math.round(y - oldTouchY);
		image.setViewX(oldImageX - deltaTouchX);
		image.setViewY(oldImageY - deltaTouchY);
		
		checkLimits();
	}
	
	private void checkLimits()
	{
		int xMin = (int) (-image.getViewportWidth() / image.getZoom());
		int xMax = image.getWidth();
		if(image.getViewX() < xMin) image.setViewX(xMin);
		else if(image.getViewX() > xMax) image.setViewX(xMax);
		
		int yMin = (int) (-image.getViewportHeight() / image.getZoom());
		int yMax = image.getHeight();
		if(image.getViewY() < yMin) image.setViewY(yMin);
		else if(image.getViewY() > yMax) image.setViewY(yMax);
	}
	
	private void onTouchStop(float x, float y) { }
	
	@Override
	public void onScreenDraw(Canvas canvas) { }
	
	public float getZoom()
	{
		return image.getZoom();
	}
	
	public void setZoom(float zoom)
	{
		image.setZoom(zoom);
	}
	
	public void centerView()
	{
		image.centerView();
	}
}