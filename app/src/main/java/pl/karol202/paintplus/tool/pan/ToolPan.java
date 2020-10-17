/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package pl.karol202.paintplus.tool.pan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import androidx.core.view.GestureDetectorCompat;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolBottomBar;
import pl.karol202.paintplus.tool.ToolCoordinateSpace;
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
			checkLimits();

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
			checkLimits();

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
	public Class<? extends ToolBottomBar> getBottomBarFragmentClass()
	{
		return null;
	}

	@Override
	public ToolCoordinateSpace getCoordinateSpace()
	{
		return ToolCoordinateSpace.SCREEN_SPACE;
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
	public boolean providesDirtyRegion()
	{
		return false;
	}

	@Override
	public Rect getDirtyRegion()
	{
		return null;
	}

	@Override
	public void resetDirtyRegion() { }

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
