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

package pl.karol202.paintplus.tool.gradient;

import android.graphics.*;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.ActionLayerChange;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.OnToolChangeListener;
import pl.karol202.paintplus.tool.StandardTool;
import pl.karol202.paintplus.tool.ToolCoordinateSpace;
import pl.karol202.paintplus.tool.ToolProperties;

public class ToolGradient extends StandardTool implements OnToolChangeListener
{
	interface OnGradientEditListener
	{
		void onGradientSet();
	}

	private static final float POINT_OUTER_RADIUS_DP = 5;
	private static final float POINT_INNER_RADIUS_DP = 2;

	private final float POINT_OUTER_RADIUS_PX;
	private final float POINT_INNER_RADIUS_PX;

	private static final float MAX_TOUCH_DISTANCE_DP = 35;

	private Gradient gradient;
	private GradientShape shape;
	private GradientRepeatability repeatability;
	private boolean revert;

	private OnGradientEditListener listener;
	private Canvas canvas;
	private GradientShapes shapes;
	private Paint maskPaint;
	private Paint pointOuterPaint;
	private Paint pointInnerPaint;

	private PointF firstPoint;
	private PointF secondPoint;
	private boolean pointsCreated;
	private RectF pointDrawRect;

	private int draggingIndex;
	private PointF draggingStart;
	private PointF previousPositionOfDraggedPoint;

	public ToolGradient(Image image)
	{
		super(image);
		POINT_OUTER_RADIUS_PX = POINT_OUTER_RADIUS_DP * image.SCREEN_DENSITY;
		POINT_INNER_RADIUS_PX = POINT_INNER_RADIUS_DP * image.SCREEN_DENSITY;

		gradient = Gradient.createSimpleGradient(Color.WHITE, Color.BLACK);
		repeatability = GradientRepeatability.NO_REPEAT;

		shapes = new GradientShapes(this);

		maskPaint = new Paint();
		maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
		maskPaint.setColor(Color.argb(160, 208, 208, 208));

		pointOuterPaint = new Paint();
		pointOuterPaint.setAntiAlias(true);
		pointOuterPaint.setStyle(Paint.Style.FILL);
		pointOuterPaint.setColor(Color.DKGRAY);

		pointInnerPaint = new Paint();
		pointInnerPaint.setAntiAlias(true);
		pointInnerPaint.setStyle(Paint.Style.FILL);
		pointInnerPaint.setColor(Color.WHITE);

		pointDrawRect = new RectF();

		draggingIndex = -1;

		shape = shapes.getShape(0);
	}

	@Override
	public int getName()
	{
		return R.string.tool_gradient;
	}

	@Override
	public int getIcon()
	{
		return R.drawable.ic_tool_gradient_black_24dp;
	}

	@Override
	public Class<? extends ToolProperties> getPropertiesFragmentClass()
	{
		return GradientProperties.class;
	}

	@Override
	public ToolCoordinateSpace getCoordinateSpace()
	{
		return ToolCoordinateSpace.LAYER_SPACE;
	}

	@Override
	public boolean isUsingSnapping()
	{
		return false;
	}

	@Override
	public boolean onTouchStart(float x, float y)
	{
		updateLayer();
		if(layer == null) return false;

		updateSelectionPath();
		resetClipping(canvas);
		doLayerAndSelectionClipping(canvas);

		if(!pointsCreated) startGradientEditing(x, y);
		else startPointsDragging(x, y);

		return true;
	}

	private void startGradientEditing(float x, float y)
	{
		firstPoint = new PointF(x, y);
		secondPoint = new PointF(x, y);

		draggingIndex = 1;
		draggingStart = new PointF(x, y);
		previousPositionOfDraggedPoint = new PointF(x, y);
	}

	private void startPointsDragging(float x, float y)
	{
		float distanceToFirst = (float) Math.hypot(firstPoint.x - x, firstPoint.y - y);
		float distanceToSecond = (float) Math.hypot(secondPoint.x - x, secondPoint.y - y);

		if(distanceToFirst <= distanceToSecond && distanceToFirst <= getMaxTouchDistance())
		{
			draggingIndex = 0;
			draggingStart = new PointF(x, y);
			previousPositionOfDraggedPoint = new PointF(firstPoint.x, firstPoint.y);
		}
		else if(distanceToSecond < distanceToFirst && distanceToSecond <= getMaxTouchDistance())
		{
			draggingIndex = 1;
			draggingStart = new PointF(x, y);
			previousPositionOfDraggedPoint = new PointF(secondPoint.x, secondPoint.y);
		}
	}

	private float getMaxTouchDistance()
	{
		return MAX_TOUCH_DISTANCE_DP * image.SCREEN_DENSITY / image.getZoom();
	}

	@Override
	public boolean onTouchMove(float x, float y)
	{
		return continuePointsDragging(x, y);
	}

	private boolean continuePointsDragging(float x, float y)
	{
		if(draggingIndex == -1) return false;
		float newX = x - draggingStart.x + previousPositionOfDraggedPoint.x;
		float newY = y - draggingStart.y + previousPositionOfDraggedPoint.y;
		if(draggingIndex == 0) firstPoint.set(newX, newY);
		else if(draggingIndex == 1) secondPoint.set(newX, newY);
		return true;
	}

	@Override
	public boolean onTouchStop(float x, float y)
	{
		if(!continuePointsDragging(x, y)) return false;

		pointsCreated = true;
		draggingIndex = -1;
		listener.onGradientSet();
		return true;
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
		return layerVisible;
	}

	@Override
	public boolean doesOnTopDraw()
	{
		return true;
	}

	@Override
	public ToolCoordinateSpace getOnLayerDrawingCoordinateSpace()
	{
		return ToolCoordinateSpace.LAYER_SPACE;
	}

	@Override
	public ToolCoordinateSpace getOnTopDrawingCoordinateSpace()
	{
		return ToolCoordinateSpace.LAYER_SPACE;
	}

	@Override
	public void onLayerDraw(Canvas canvas)
	{
		if(!canDrawGradient()) return;
		resetClipping(canvas);
		doLayerAndSelectionClipping(canvas);
		doImageClipping(canvas);
		shape.onScreenDraw(canvas);
	}

	@Override
	public void onTopDraw(Canvas canvas)
	{
		if(firstPoint != null) drawPoint(canvas, firstPoint);
		if(secondPoint != null) drawPoint(canvas, secondPoint);
	}

	private void drawPoint(Canvas canvas, PointF point)
	{
		float outerSize = POINT_OUTER_RADIUS_PX / image.getZoom();
		float innerSize = POINT_INNER_RADIUS_PX / image.getZoom();

		pointDrawRect.left = point.x - outerSize;
		pointDrawRect.top = point.y - outerSize;
		pointDrawRect.right = point.x + outerSize;
		pointDrawRect.bottom = point.y + outerSize;
		canvas.drawOval(pointDrawRect, pointOuterPaint);

		pointDrawRect.left = point.x - innerSize;
		pointDrawRect.top = point.y - innerSize;
		pointDrawRect.right = point.x + innerSize;
		pointDrawRect.bottom = point.y + innerSize;
		canvas.drawOval(pointDrawRect, pointInnerPaint);
	}

	@Override
	protected void updateLayer()
	{
		Layer newLayer = image.getSelectedLayer();
		if(layer == null) layer = newLayer;
		if(newLayer != layer)
			offsetGradient(layer.getX() - newLayer.getX(), layer.getY() - newLayer.getY());
		layer = newLayer;

		canvas = image.getSelectedCanvas();
	}

	private void offsetGradient(int x, int y)
	{
		if(firstPoint == null || secondPoint == null) return;
		firstPoint.offset(x, y);
		secondPoint.offset(x, y);
	}

	void apply()
	{
		ActionLayerChange action = new ActionLayerChange(image, R.string.tool_gradient);
		action.setLayerChange(image.getLayerIndex(layer), layer.getBitmap());

		shape.applyGradient(canvas);
		action.applyAction();
		cancel();
	}

	void cancel()
	{
		draggingIndex = -1;
		firstPoint = null;
		secondPoint = null;
		pointsCreated = false;
		image.updateImage();
	}

	@Override
	public void onToolSelected() { }

	@Override
	public void onOtherToolSelected()
	{
		cancel();
	}

	void setOnGradientEditListener(OnGradientEditListener listener)
	{
		this.listener = listener;
	}

	boolean isInEditMode()
	{
		return pointsCreated;
	}

	boolean canDrawGradient()
	{
		return firstPoint != null && secondPoint != null;
	}

	Gradient getGradient()
	{
		return gradient;
	}

	void setGradient(Gradient gradient)
	{
		this.gradient = gradient;
	}

	GradientShapes getShapes()
	{
		return shapes;
	}

	GradientShape getShape()
	{
		return shape;
	}

	void setShape(GradientShape shape)
	{
		this.shape = shape;
		image.updateImage();
	}

	GradientRepeatability getRepeatability()
	{
		return repeatability;
	}

	void setRepeatability(GradientRepeatability repeatability)
	{
		this.repeatability = repeatability;
	}

	boolean isReverted()
	{
		return revert;
	}

	void setRevert(boolean revert)
	{
		this.revert = revert;
		image.updateImage();
	}

	PointF getFirstPoint()
	{
		return firstPoint;
	}

	PointF getSecondPoint()
	{
		return secondPoint;
	}

	int getLayerWidth()
	{
		return layer.getWidth();
	}

	int getLayerHeight()
	{
		return layer.getHeight();
	}
}
