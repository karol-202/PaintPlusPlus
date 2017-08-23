package pl.karol202.paintplus.tool.pan;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.CoordinateSpace;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolProperties;

public class ToolPan implements Tool
{
	interface OnZoomChangeListener
	{
		void onZoomChanged();
	}
	
	private class ImageOnGestureListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onDown(MotionEvent e)
		{
			return true;
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
		{
			image.setViewX(image.getViewX() + (distanceX / image.getZoom()));
			image.setViewY(image.getViewY() + (distanceY / image.getZoom()));
			
			return true;
		}
	}
	
	private class ImageOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
	{
		private boolean scaling;
		private float lastFocusX;
		private float lastFocusY;
		
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector)
		{
			scaling = true;
			lastFocusX = detector.getFocusX();
			lastFocusY = detector.getFocusY();
			return true;
		}
		
		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
			image.setZoom(detector.getScaleFactor() * image.getZoom(), detector.getFocusX(), detector.getFocusY());
			
			image.setViewX(image.getViewX() + ((lastFocusX - detector.getFocusX()) / image.getZoom()));
			image.setViewY(image.getViewY() + ((lastFocusY - detector.getFocusY()) / image.getZoom()));

			lastFocusX = detector.getFocusX();
			lastFocusY = detector.getFocusY();
			
			if(zoomListener != null) zoomListener.onZoomChanged();
			return true;
		}
		
		@Override
		public void onScaleEnd(ScaleGestureDetector detector)
		{
			scaling = false;
		}
		
		boolean isScaling()
		{
			return scaling;
		}
	}
	
	private Image image;
	private Context context;
	private OnZoomChangeListener zoomListener;
	
	private ImageOnScaleGestureListener scaleGestureListener;
	private ImageOnGestureListener gestureListener;
	
	private GestureDetectorCompat gestureDetector;
	private ScaleGestureDetector scaleGestureDetector;
	
	public ToolPan(Image image)
	{
		this.image = image;
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
	public CoordinateSpace getCoordinateSpace()
	{
		return CoordinateSpace.SCREEN_SPACE;
	}
	
	@Override
	public boolean isUsingSnapping()
	{
		return false;
	}
	
	@Override
	public boolean onTouch(MotionEvent event, Context context)
	{
		if(this.context != context) initDetectors(context);
		
		scaleGestureDetector.onTouchEvent(event);
		if(!scaleGestureListener.isScaling()) gestureDetector.onTouchEvent(event);
		return true;
	}
	
	private void initDetectors(Context context)
	{
		this.context = context;
		
		this.gestureListener = new ImageOnGestureListener();
		this.scaleGestureListener = new ImageOnScaleGestureListener();
		
		this.gestureDetector = new GestureDetectorCompat(context, gestureListener);
		this.scaleGestureDetector = new ScaleGestureDetector(context, scaleGestureListener);
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
	
	void setZoomListener(OnZoomChangeListener zoomListener)
	{
		this.zoomListener = zoomListener;
	}
}