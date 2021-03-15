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

package pl.karol202.paintplus.color.curves;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import pl.karol202.paintplus.util.ErrorHandler;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.curves.ColorChannel.ColorChannelType;
import pl.karol202.paintplus.color.manipulators.params.CurveManipulatorParams;
import pl.karol202.paintplus.util.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ColorCurvesView extends View
{
	private static final int HORIZONTAL_GRID_LINES = 7;
	private static final int VERTICAL_GRID_LINES = 7;

	private static final float LEFT_GRID_MARGIN_DP = 10;
	private static final float TOP_GRID_MARGIN_DP = 5;
	private static final float RIGHT_GRID_MARGIN_DP = 5;
	private static final float BOTTOM_GRID_MARGIN_DP = 10;
	private static final float GRID_LINE_WIDTH_DP = 1.5f;

	private static final float HORIZONTAL_SCALE_HEIGHT_DP = 5;
	private static final float VERTICAL_SCALE_WIDTH_DP = 5;

	private static final float POINT_RADIUS_DP = 4;
	private static final float POINT_INNER_RADIUS_DP = 1.5f;
	private static final float CURVE_WIDTH_DP = 1;

	private static final float MAX_TOUCH_DISTANCE_DP = 32;
	private static final float REMOVE_LIMIT_DP = 50;

	private final float LEFT_GRID_MARGIN_PX;
	private final float TOP_GRID_MARGIN_PX;
	private final float RIGHT_GRID_MARGIN_PX;
	private final float BOTTOM_GRID_MARGIN_PX;
	private final float GRID_LINE_WIDTH_PX;
	private final float HORIZONTAL_SCALE_HEIGHT_PX;
	private final float VERTICAL_SCALE_WIDTH_PX;
	private final float POINT_RADIUS_PX;
	private final float POINT_INNER_RADIUS_PX;
	private final float CURVE_WIDTH_PX;
	private final float MAX_TOUCH_DISTANCE_PX;
	private final float REMOVE_LIMIT_PX;

	private OnCurveEditListener listener;
	private ColorChannelType channelType;
	private ColorChannel channelIn;
	private ColorChannel channelOut;
	private HashMap<ChannelInOutSet, ColorCurve> curves;

	private Point viewSize;
	private float[] grid;
	private Paint gridPaint;
	private Shader horizontalScaleShader;
	private Shader verticalScaleShader;
	private Paint horizontalScalePaint;
	private Paint verticalScalePaint;
	private ArrayList<RectF> points;
	private ArrayList<RectF> innerPoints;
	private Paint pointPaint;
	private Paint pointInnerPaint;
	private Paint curvePaint;

	private Point oldTouchPoint;
	private Point oldDraggedScreenPoint;
	private Point newDraggedCurvePoint;
	private boolean draggedPointRemoved;

	public ColorCurvesView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		LEFT_GRID_MARGIN_PX = MathUtils.dpToPixels(context, LEFT_GRID_MARGIN_DP);
		TOP_GRID_MARGIN_PX = MathUtils.dpToPixels(context, TOP_GRID_MARGIN_DP);
		RIGHT_GRID_MARGIN_PX = MathUtils.dpToPixels(context, RIGHT_GRID_MARGIN_DP);
		BOTTOM_GRID_MARGIN_PX = MathUtils.dpToPixels(context, BOTTOM_GRID_MARGIN_DP);
		GRID_LINE_WIDTH_PX = MathUtils.dpToPixels(context, GRID_LINE_WIDTH_DP);
		HORIZONTAL_SCALE_HEIGHT_PX = MathUtils.dpToPixels(context, HORIZONTAL_SCALE_HEIGHT_DP);
		VERTICAL_SCALE_WIDTH_PX = MathUtils.dpToPixels(context, VERTICAL_SCALE_WIDTH_DP);
		POINT_RADIUS_PX = MathUtils.dpToPixels(context, POINT_RADIUS_DP);
		POINT_INNER_RADIUS_PX = MathUtils.dpToPixels(context, POINT_INNER_RADIUS_DP);
		CURVE_WIDTH_PX = MathUtils.dpToPixels(context, CURVE_WIDTH_DP);
		MAX_TOUCH_DISTANCE_PX = MathUtils.dpToPixels(context, MAX_TOUCH_DISTANCE_DP);
		REMOVE_LIMIT_PX = MathUtils.dpToPixels(context, REMOVE_LIMIT_DP);

		channelType = null;
		channelIn = null;
		channelOut = null;

		viewSize = new Point();

		gridPaint = new Paint();
		gridPaint.setColor(Color.LTGRAY);
		gridPaint.setStrokeWidth(GRID_LINE_WIDTH_PX);

		pointPaint = new Paint();
		pointPaint.setColor(Color.BLACK);
		pointPaint.setAntiAlias(true);

		pointInnerPaint = new Paint();
		pointInnerPaint.setColor(Color.WHITE);
		pointInnerPaint.setAntiAlias(true);

		curvePaint = new Paint();
		curvePaint.setColor(Color.BLACK);
		curvePaint.setStrokeWidth(CURVE_WIDTH_PX);
		curvePaint.setAntiAlias(true);
	}

	private void initChannels(ColorChannelType type)
	{
		if(channelType != null)
		{
			ErrorHandler.report(new RuntimeException("Channel settings are already set!"));
			return;
		}
		channelType = type;

		curves = new HashMap<>();
		for(ColorChannel channelIn : ColorChannel.filterByType(channelType))
			for(ColorChannel channelOut : ColorChannel.filterByType(channelType))
				initChannel(channelIn, channelOut);
	}

	private void initChannel(ColorChannel in, ColorChannel out)
	{
		ChannelInOutSet set = new ChannelInOutSet(in, out);
		curves.put(set, getDefaultCurve(set));
	}

	private ColorCurve getDefaultCurve(ChannelInOutSet set)
	{
		if(set.getIn() == set.getOut()) return ColorCurve.defaultCurve(set);
		else return ColorCurve.zeroCurve(set);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getMeasuredWidth();
		setMeasuredDimension(width, width);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if(isInEditMode() && channelType == null)
		{
			initChannels(ColorChannelType.HSV);
			setChannelIn(ColorChannel.VALUE);
			setChannelOut(ColorChannel.VALUE);
		}

		if(channelType == null) return;
		viewSize.x = getWidth();
		viewSize.y = getHeight();

		drawGrid(canvas);
		drawScale(canvas);

		drawCurve(canvas);
		drawPoints(canvas);
	}

	private void drawGrid(Canvas canvas)
	{
		if(grid == null) createGrid();
		canvas.drawLines(grid, gridPaint);
	}

	private void drawScale(Canvas canvas)
	{
		if(horizontalScaleShader == null || verticalScaleShader == null) createScalePaints();
		canvas.drawRect(LEFT_GRID_MARGIN_PX, viewSize.y - HORIZONTAL_SCALE_HEIGHT_PX - 1,
				viewSize.x - RIGHT_GRID_MARGIN_PX, viewSize.y - 1, horizontalScalePaint);
		canvas.drawRect(0, TOP_GRID_MARGIN_PX, VERTICAL_SCALE_WIDTH_PX,
				viewSize.y - BOTTOM_GRID_MARGIN_PX, verticalScalePaint);
	}

	private void drawPoints(Canvas canvas)
	{
		if(points == null) createPoints();

		for(RectF point : points) canvas.drawOval(point, pointPaint);
		for(RectF point : innerPoints) canvas.drawOval(point, pointInnerPaint);
	}

	private void drawCurve(Canvas canvas)
	{
		if(points == null) createPoints();

		float lastX = LEFT_GRID_MARGIN_PX;
		float lastY = points.get(0).centerY();
		for(RectF rect : points)
		{
			float x = rect.centerX();
			float y = rect.centerY();
			canvas.drawLine(lastX, lastY, x, y, curvePaint);
			lastX = x;
			lastY = y;
		}
		canvas.drawLine(lastX, lastY, canvas.getWidth() - RIGHT_GRID_MARGIN_PX, lastY, curvePaint);
	}

	private void createGrid()
	{
		float[] linesHorizontal = new float[HORIZONTAL_GRID_LINES * 4];
		for(int i = 0; i < HORIZONTAL_GRID_LINES; i++)
		{
			float verticalMargins = TOP_GRID_MARGIN_PX + BOTTOM_GRID_MARGIN_PX;
			float y = (viewSize.y - verticalMargins) * (i / (HORIZONTAL_GRID_LINES - 1f)) + TOP_GRID_MARGIN_PX;
			linesHorizontal[i * 4] = LEFT_GRID_MARGIN_PX;
			linesHorizontal[i * 4 + 1] = y;
			linesHorizontal[i * 4 + 2] = viewSize.x - RIGHT_GRID_MARGIN_PX;
			linesHorizontal[i * 4 + 3] = y;
		}

		float[] linesVertical = new float[VERTICAL_GRID_LINES * 4];
		for(int i = 0; i < VERTICAL_GRID_LINES; i++)
		{
			float horizontalMargins = LEFT_GRID_MARGIN_PX + RIGHT_GRID_MARGIN_PX;
			float x = (viewSize.x - horizontalMargins) * (i / (VERTICAL_GRID_LINES - 1f)) + LEFT_GRID_MARGIN_PX;
			linesVertical[i * 4] = x;
			linesVertical[i * 4 + 1] = TOP_GRID_MARGIN_PX;
			linesVertical[i * 4 + 2] = x;
			linesVertical[i * 4 + 3] = viewSize.y - BOTTOM_GRID_MARGIN_PX;
		}

		grid = new float[linesHorizontal.length + linesVertical.length];
		System.arraycopy(linesHorizontal, 0, grid, 0, linesHorizontal.length);
		System.arraycopy(linesVertical, 0, grid, linesHorizontal.length, linesVertical.length);
	}

	private void createScalePaints()
	{
		createHorizontalScalePaints();
		createVerticalScalePaints();
	}

	private void createHorizontalScalePaints()
	{
		if(channelIn != ColorChannel.HUE)
		{
			int firstColor = Color.BLACK;
			int secondColor = Color.WHITE;
			if(channelIn == ColorChannel.RED) secondColor = Color.RED;
			else if(channelIn == ColorChannel.GREEN) secondColor = Color.GREEN;
			else if(channelIn == ColorChannel.BLUE) secondColor = Color.BLUE;
			else if(channelIn == ColorChannel.SATURATION)
			{
				firstColor = Color.WHITE;
				secondColor = Color.RED;
			}
			horizontalScaleShader = new LinearGradient(LEFT_GRID_MARGIN_PX, 0, viewSize.x - RIGHT_GRID_MARGIN_PX,
					0, firstColor, secondColor, Shader.TileMode.CLAMP);
		}
		else
		{
			int[] colors = new int[] { Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED };
			horizontalScaleShader = new LinearGradient(LEFT_GRID_MARGIN_PX, 0, viewSize.x - RIGHT_GRID_MARGIN_PX,
					0, colors, null, Shader.TileMode.CLAMP);
		}
		horizontalScalePaint = new Paint();
		horizontalScalePaint.setShader(horizontalScaleShader);
		horizontalScalePaint.setStyle(Paint.Style.FILL);
	}

	private void createVerticalScalePaints()
	{
		if(channelOut != ColorChannel.HUE)
		{
			int firstColor = Color.BLACK;
			int secondColor = Color.WHITE;
			if(channelOut == ColorChannel.RED) secondColor = Color.RED;
			else if(channelOut == ColorChannel.GREEN) secondColor = Color.GREEN;
			else if(channelOut == ColorChannel.BLUE) secondColor = Color.BLUE;
			else if(channelOut == ColorChannel.SATURATION)
			{
				firstColor = Color.WHITE;
				secondColor = Color.RED;
			}
			verticalScaleShader = new LinearGradient(0, viewSize.y - BOTTOM_GRID_MARGIN_PX, 0,
					TOP_GRID_MARGIN_PX, firstColor, secondColor, Shader.TileMode.CLAMP);
		}
		else
		{
			int[] colors = new int[] { Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED };
			verticalScaleShader = new LinearGradient(0, viewSize.y - BOTTOM_GRID_MARGIN_PX, 0,
					TOP_GRID_MARGIN_PX, colors, null, Shader.TileMode.CLAMP);
		}
		verticalScalePaint = new Paint();
		verticalScalePaint.setShader(verticalScaleShader);
		verticalScalePaint.setStyle(Paint.Style.FILL);
	}

	private void createPoints()
	{
		Point[] curvePoints = getCurrentCurve().getPoints();
		points = new ArrayList<>();
		innerPoints = new ArrayList<>();
		for(Point point : curvePoints)
		{
			Point newPoint = curveToScreen(point);
			RectF oval = new RectF(newPoint.x - POINT_RADIUS_PX, newPoint.y - POINT_RADIUS_PX,
								   newPoint.x + POINT_RADIUS_PX, newPoint.y + POINT_RADIUS_PX);
			RectF innerOval = new RectF(newPoint.x - POINT_INNER_RADIUS_PX, newPoint.y - POINT_INNER_RADIUS_PX,
										newPoint.x + POINT_INNER_RADIUS_PX, newPoint.y + POINT_INNER_RADIUS_PX);
			points.add(oval);
			innerPoints.add(innerOval);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN) onTouchDown((int) event.getX(), (int) event.getY());
		else if(event.getAction() == MotionEvent.ACTION_MOVE) onTouchMove((int) event.getX(), (int) event.getY());
		else if(event.getAction() == MotionEvent.ACTION_UP) onTouchUp((int) event.getX(), (int) event.getY());

		invalidate();
		return true;
	}

	private void onTouchDown(int x, int y)
	{
		oldTouchPoint = new Point(x, y);

		Point nearest = null;
		int nearestIndex = -1;
		float shortestDistance = -1;
		for(int i = 0; i < points.size(); i++)
		{
			RectF rect = points.get(i);
			Point point = new Point((int) rect.centerX(), (int) rect.centerY());
			float distance = MathUtils.distance(point, oldTouchPoint);
			if((nearest == null || distance < shortestDistance) && distance < MAX_TOUCH_DISTANCE_PX)
			{
				nearest = point;
				nearestIndex = i;
				shortestDistance = distance;
			}
		}

		ColorCurve curve = getCurrentCurve();
		if(nearest != null)
		{
			oldDraggedScreenPoint = nearest;
			newDraggedCurvePoint = curve.getPoints()[nearestIndex];
		}
		else
		{
			oldDraggedScreenPoint = new Point(oldTouchPoint);
			newDraggedCurvePoint = screenToCurve(oldDraggedScreenPoint);
			curve.addPoint(newDraggedCurvePoint);
			createPoints();
		}

		if(listener != null) listener.onCurveEdited();
	}

	private void onTouchMove(int x, int y)
	{
		if(oldDraggedScreenPoint != null)
		{
			Point newScreenPoint = new Point(oldDraggedScreenPoint);
			newScreenPoint.offset(x - oldTouchPoint.x, y - oldTouchPoint.y);
			boolean shouldRemove = !checkBounds(newScreenPoint);
			if(shouldRemove && !draggedPointRemoved && getCurrentCurve().removePoint(newDraggedCurvePoint))
				draggedPointRemoved = true;
			if(!shouldRemove && draggedPointRemoved)
			{
				getCurrentCurve().addPoint(newDraggedCurvePoint);
				draggedPointRemoved = false;
			}

			Point newCurvePoint = screenToCurve(newScreenPoint);
			boolean moved = getCurrentCurve().movePoint(newDraggedCurvePoint, newCurvePoint);

			createPoints();
			if(moved) newDraggedCurvePoint = newCurvePoint;
			if(listener != null) listener.onCurveEdited();
		}
	}

	private void onTouchUp(int x, int y)
	{
		if(oldDraggedScreenPoint != null)
		{
			Point newScreenPoint = new Point(oldDraggedScreenPoint);
			newScreenPoint.offset(x - oldTouchPoint.x, y - oldTouchPoint.y);
			boolean shouldRemove = !checkBounds(newScreenPoint);
			boolean removed = false;
			if(shouldRemove && !draggedPointRemoved && getCurrentCurve().removePoint(newDraggedCurvePoint))
				removed = true;

			if(!removed)
			{
				Point newCurvePoint = screenToCurve(newScreenPoint);
				getCurrentCurve().movePoint(newDraggedCurvePoint, newCurvePoint);
			}

			createPoints();
			oldTouchPoint = null;
			oldDraggedScreenPoint = null;
			newDraggedCurvePoint = null;
			draggedPointRemoved = false;
			if(listener != null) listener.onCurveEdited();
		}
	}

	private boolean checkBounds(Point point)
	{
		float left = point.x - LEFT_GRID_MARGIN_PX;
		float top = point.y - TOP_GRID_MARGIN_PX;
		float right = point.x - (viewSize.x - RIGHT_GRID_MARGIN_PX);
		float bottom = point.y - (viewSize.y - BOTTOM_GRID_MARGIN_PX);

		boolean shouldRemove = left <= -REMOVE_LIMIT_PX || top <= -REMOVE_LIMIT_PX ||
							   right >= REMOVE_LIMIT_PX || bottom >= REMOVE_LIMIT_PX;
		if(left < 0) point.x = (int) LEFT_GRID_MARGIN_PX;
		else if(right > 0) point.x = (int) (viewSize.x - RIGHT_GRID_MARGIN_PX);
		if(top < 0) point.y = (int) TOP_GRID_MARGIN_PX;
		else if(bottom > 0) point.y = (int) (viewSize.y - BOTTOM_GRID_MARGIN_PX);
		return !shouldRemove;
	}

	private Point curveToScreen(Point point)
	{
		Point newPoint = new Point(point);
		newPoint.x = Math.round(MathUtils.map(point.x, 0, channelIn.getMaxValue(),
		                                      LEFT_GRID_MARGIN_PX, viewSize.x - RIGHT_GRID_MARGIN_PX));
		newPoint.y = Math.round(MathUtils.map(point.y, channelOut.getMaxValue(), 0,
		                                      TOP_GRID_MARGIN_PX, viewSize.y - BOTTOM_GRID_MARGIN_PX));
		return newPoint;
	}

	private Point screenToCurve(Point point)
	{
		Point newPoint = new Point(point);
		newPoint.x = Math.round(MathUtils.map(point.x, LEFT_GRID_MARGIN_PX, viewSize.x - RIGHT_GRID_MARGIN_PX,
		                                      0, channelIn.getMaxValue()));
		newPoint.y = Math.round(MathUtils.map(point.y, TOP_GRID_MARGIN_PX, viewSize.y - BOTTOM_GRID_MARGIN_PX,
		                                      channelOut.getMaxValue(), 0));
		return newPoint;
	}

	private void updatePoints()
	{
		this.points = null;
	}

	public void attachCurvesToParamsObject(CurveManipulatorParams params)
	{
		for(ChannelInOutSet set : curves.keySet())
		{
			ColorCurve curve = curves.get(set);
			ColorCurve defaultCurve = getDefaultCurve(set);
			if(!curve.equals(defaultCurve)) params.addCurve(set, curve);
		}
	}

	public void restoreCurrentCurve()
	{
		initChannel(channelIn, channelOut);
		updatePoints();
		invalidate();
	}

	public String getInfoText()
	{
		if(newDraggedCurvePoint == null || draggedPointRemoved) return "";
		return getContext().getString(R.string.color_curve_point_info, newDraggedCurvePoint.x, newDraggedCurvePoint.y);
	}

	private ColorCurve getCurrentCurve()
	{
		ChannelInOutSet set = new ChannelInOutSet(channelIn, channelOut);
		return curves.get(set);
	}

	public void setChannelType(ColorChannelType channelType)
	{
		initChannels(channelType);
	}

	public void setOnCurveEditListener(OnCurveEditListener listener)
	{
		this.listener = listener;
	}

	public void setChannelIn(ColorChannel channelIn)
	{
		this.channelIn = channelIn;
		this.horizontalScaleShader = null;
		this.verticalScaleShader = null;
		updatePoints();
		invalidate();
	}

	public void setChannelOut(ColorChannel channelOut)
	{
		this.channelOut = channelOut;
		this.horizontalScaleShader = null;
		this.verticalScaleShader = null;
		updatePoints();
		invalidate();
	}
}
