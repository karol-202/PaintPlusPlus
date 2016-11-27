package pl.karol202.paintplus.tool;

import android.app.Fragment;
import android.graphics.Canvas;
import android.view.MotionEvent;
import pl.karol202.paintplus.ColorsSet;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.properties.PanProperties;

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
	public Class<? extends Fragment> getPropertiesFragmentClass()
	{
		return PanProperties.class;
	}
	
	@Override
	public boolean onlyViewport()
	{
		return false;
	}
	
	@Override
	public boolean onTouch(Canvas edit, ColorsSet colors, MotionEvent event)
	{
		float x = event.getX() - image.getViewX();
		float y = event.getY() - image.getViewY();
		if(event.getAction() == MotionEvent.ACTION_DOWN) onTouchStart(edit, x, y);
		else if(event.getAction() == MotionEvent.ACTION_MOVE) onTouchMove(edit, x, y);
		else if(event.getAction() == MotionEvent.ACTION_UP) onTouchStop(edit, x, y);
		return true;
	}
	
	private void onTouchStart(Canvas canvas, float x, float y)
	{
		oldImageX = image.getViewX();
		oldImageY = image.getViewY();
		oldTouchX = x;
		oldTouchY = y;
	}
	
	private void onTouchMove(Canvas canvas, float x, float y)
	{
		int deltaTouchX = Math.round(x - oldTouchX);
		int deltaTouchY = Math.round(y - oldTouchY);
		image.setViewX(oldImageX - deltaTouchX);
		image.setViewY(oldImageY - deltaTouchY);
	}
	
	private void onTouchStop(Canvas canvas, float x, float y) { }
	
	@Override
	public void onTouchOutsideViewport(Canvas edit, ColorsSet colors, MotionEvent event) { }
	
	@Override
	public void onDraw(Canvas canvas) { }
	
	public float getZoom()
	{
		return image.getZoom();
	}
	
	public void setZoom(float zoom)
	{
		image.setZoom(zoom);
	}
}