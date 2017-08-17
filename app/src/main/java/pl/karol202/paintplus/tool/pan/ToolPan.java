package pl.karol202.paintplus.tool.pan;

import android.graphics.Canvas;
import android.view.MotionEvent;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.helpers.HelpersManager;
import pl.karol202.paintplus.image.Image;
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
	public boolean isUsingSnapping()
	{
		return false;
	}
	
	@Override
	public boolean isImageLimited()
	{
		return false;
	}
	
	@Override
	public boolean onTouch(MotionEvent event, HelpersManager manager)
	{
		super.onTouch(event, manager);
		float x = event.getX() - image.getViewX();
		float y = event.getY() - image.getViewY();
		if(event.getAction() == MotionEvent.ACTION_DOWN) onTouchStart(x, y);
		else if(event.getAction() == MotionEvent.ACTION_MOVE) onTouchMove(x, y);
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
	
	@Override
	public boolean isLayerSpace()
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
	
	float getZoom()
	{
		return image.getZoom();
	}
	
	void setZoom(float zoom)
	{
		image.setZoom(zoom);
	}
	
	void centerView()
	{
		image.centerView();
	}
}