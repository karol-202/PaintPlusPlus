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

import android.content.Context;
import android.graphics.*;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class GradientView extends View
{
	interface OnGradientEditorUpdateListener
	{
		void onGradientPositionUpdated(float position);
		
		void onGradientSelectionUpdated(float position, int color);
		
		void onGradientPointAdded();
	}
	
	private class Triangle
	{
		private Path outerPath;
		private Path innerPath;
		
		Triangle(float xOffset, float yOffset)
		{
			outerPath = new Path(TRIANGLE_OUTER_PATH);
			outerPath.close();
			outerPath.offset(xOffset, yOffset);
			
			innerPath = new Path(TRIANGLE_INNER_PATH);
			innerPath.close();
			innerPath.offset(xOffset, yOffset);
		}
		
		Path getOuterPath()
		{
			return outerPath;
		}
		
		Path getInnerPath()
		{
			return innerPath;
		}
	}
	
	private final float MAX_TOUCH_DISTANCE = 0.05f;
	
	private final float BORDER_WIDTH_PX = 1;
	
	private final float SIDE_MARGIN_DP = 10;
	private final float TOP_MARGIN_PX = 1;
	
	private final float TOP_BAR_HEIGHT_DP = 15;
	private final float BOTTOM_BAR_HEIGHT_DP = 25;
	
	private final float HEIGHT_DP = TOP_BAR_HEIGHT_DP + BOTTOM_BAR_HEIGHT_DP + 20;
	
	private final float TRIANGLE_Y_OFFSET_DP = TOP_BAR_HEIGHT_DP + BOTTOM_BAR_HEIGHT_DP - 5;
	private final Path TRIANGLE_OUTER_PATH = new Path();
	private final Path TRIANGLE_INNER_PATH = new Path();
	
	private final float SIDE_MARGIN_PX;
	private final float TOP_BAR_HEIGHT_PX;
	private final float BOTTOM_BAR_HEIGHT_PX;
	private final float TRIANGLE_Y_OFFSET_PX;
	private final float HEIGHT_PX;
	
	private OnGradientEditorUpdateListener gradientUpdateListener;
	private Gradient gradient;
	private boolean addingMode;
	
	private RectF borderRect;
	private Paint borderPaint;
	private RectF checkerboardRect;
	private Paint checkerboardPaint;
	
	private RectF topBarRect;
	private Paint topBarPaint;
	private Shader topBarShader;
	
	private RectF bottomBarRect;
	private Paint bottomBarPaint;
	private Shader bottomBarShader;
	
	private Map<GradientPoint, Triangle> triangles;
	private Paint triangleOuterPaint;
	private Paint triangleInnerPaint;
	private Paint triangleInnerSelectedPaint;
	
	private GradientPoint selectedPoint;
	
	private GradientPoint draggedPoint;
	private float lastPosition;
	
	public GradientView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		SIDE_MARGIN_PX = Utils.dpToPixels(context, SIDE_MARGIN_DP);
		TOP_BAR_HEIGHT_PX = Utils.dpToPixels(context, TOP_BAR_HEIGHT_DP);
		BOTTOM_BAR_HEIGHT_PX = Utils.dpToPixels(context, BOTTOM_BAR_HEIGHT_DP);
		TRIANGLE_Y_OFFSET_PX = Utils.dpToPixels(context, TRIANGLE_Y_OFFSET_DP) + TOP_MARGIN_PX;
		HEIGHT_PX = Utils.dpToPixels(context, HEIGHT_DP) + TOP_MARGIN_PX;
		initTriangles(context);
		
		gradient = null;
		
		borderPaint = new Paint();
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(BORDER_WIDTH_PX);
		borderPaint.setColor(ResourcesCompat.getColor(context.getResources(), R.color.border, null));
		
		Bitmap checkerboard = BitmapFactory.decodeResource(getResources(), R.drawable.checkerboard);
		Matrix checkerboardMatrix = new Matrix();
		Shader checkerboardShader = new BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		checkerboardMatrix.preTranslate(-5, -7);
		checkerboardShader.setLocalMatrix(checkerboardMatrix);
		checkerboardPaint = new Paint();
		checkerboardPaint.setShader(checkerboardShader);
		checkerboardPaint.setFilterBitmap(false);
		
		topBarPaint = new Paint();
		bottomBarPaint = new Paint();
		
		triangleOuterPaint = new Paint();
		triangleOuterPaint.setAntiAlias(true);
		triangleOuterPaint.setColor(Color.DKGRAY);
		
		triangleInnerPaint = new Paint();
		triangleInnerPaint.setAntiAlias(true);
		triangleInnerPaint.setColor(Color.WHITE);
		
		triangleInnerSelectedPaint = new Paint();
		triangleInnerSelectedPaint.setAntiAlias(true);
		triangleInnerSelectedPaint.setColor(ResourcesCompat.getColor(context.getResources(), R.color.gradient_point_selected, null));
	}
	
	private void initTriangles(Context context)
	{
		TRIANGLE_OUTER_PATH.moveTo(0, 0);
		TRIANGLE_OUTER_PATH.lineTo(10, 20);
		TRIANGLE_OUTER_PATH.lineTo(-10, 20);
		TRIANGLE_OUTER_PATH.close();
		
		TRIANGLE_INNER_PATH.moveTo(0, 4.5f);
		TRIANGLE_INNER_PATH.lineTo(7, 18);
		TRIANGLE_INNER_PATH.lineTo(-7, 18);
		TRIANGLE_INNER_PATH.close();
		
		float scale = context.getResources().getDisplayMetrics().density;
		Matrix matrix = new Matrix();
		matrix.preScale(scale, scale);
		TRIANGLE_OUTER_PATH.transform(matrix);
		TRIANGLE_INNER_PATH.transform(matrix);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), (int) HEIGHT_PX);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		drawBorder(canvas);
		drawCheckerboard(canvas);
		if(gradient == null && isInEditMode()) setGradient(Gradient.createSimpleGradient(Color.BLACK, Color.WHITE));
		if(gradient == null) return;
		drawTopBar(canvas);
		drawBottomBar(canvas);
		drawTriangles(canvas);
	}
	
	private void drawBorder(Canvas canvas)
	{
		if(borderRect == null) updateBorderRect();
		canvas.drawRect(borderRect, borderPaint);
	}
	
	private void updateBorderRect()
	{
		borderRect = new RectF(SIDE_MARGIN_PX - BORDER_WIDTH_PX, TOP_MARGIN_PX - BORDER_WIDTH_PX,
				getWidth() - SIDE_MARGIN_PX, TOP_MARGIN_PX + TOP_BAR_HEIGHT_PX + BOTTOM_BAR_HEIGHT_PX);
	}
	
	private void drawCheckerboard(Canvas canvas)
	{
		if(checkerboardRect == null) updateCheckerboardRect();
		canvas.drawRect(checkerboardRect, checkerboardPaint);
	}
	
	private void updateCheckerboardRect()
	{
		checkerboardRect = new RectF(SIDE_MARGIN_PX, TOP_MARGIN_PX, getWidth() - SIDE_MARGIN_PX,
				TOP_MARGIN_PX + TOP_BAR_HEIGHT_PX + BOTTOM_BAR_HEIGHT_PX);
	}
	
	private void drawTopBar(Canvas canvas)
	{
		if(topBarRect == null) updateTopBarRect();
		if(topBarShader == null) updateTopBarPaint();
		canvas.drawRect(topBarRect, topBarPaint);
	}
	
	private void updateTopBarRect()
	{
		topBarRect = new RectF(SIDE_MARGIN_PX, TOP_MARGIN_PX, getWidth() - SIDE_MARGIN_PX,
				TOP_MARGIN_PX + TOP_BAR_HEIGHT_PX);
	}
	
	private void updateTopBarPaint()
	{
		topBarShader = new LinearGradient(SIDE_MARGIN_PX, 0, getWidth() - SIDE_MARGIN_PX, 0,
										  gradient.getColorsArray(), gradient.getPositionsArray(), Shader.TileMode.CLAMP);
		topBarPaint.setShader(topBarShader);
	}
	
	private void drawBottomBar(Canvas canvas)
	{
		if(bottomBarRect == null) updateBottomBarRect();
		if(bottomBarShader == null) updateBottomBarPaint();
		canvas.drawRect(bottomBarRect, bottomBarPaint);
	}
	
	private void updateBottomBarRect()
	{
		bottomBarRect = new RectF(SIDE_MARGIN_PX, TOP_MARGIN_PX + TOP_BAR_HEIGHT_PX, getWidth() - SIDE_MARGIN_PX,
								 TOP_MARGIN_PX + TOP_BAR_HEIGHT_PX + BOTTOM_BAR_HEIGHT_PX);
	}
	
	private void updateBottomBarPaint()
	{
		int[] colors = removeAlphaFromColorsArray(gradient.getColorsArray());
		bottomBarShader = new LinearGradient(SIDE_MARGIN_PX, 0, getWidth() - SIDE_MARGIN_PX, 0,
											 colors, gradient.getPositionsArray(), Shader.TileMode.CLAMP);
		bottomBarPaint.setShader(bottomBarShader);
	}
	
	private int[] removeAlphaFromColorsArray(int[] colors)
	{
		for(int i = 0; i < colors.length; i++) colors[i] |= 0xFF000000;
		return colors;
	}
	
	private void drawTriangles(Canvas canvas)
	{
		if(triangles == null) createTriangles();
		for(Map.Entry<GradientPoint, Triangle> entry : triangles.entrySet())
		{
			boolean selected = entry.getKey() == selectedPoint;
			Triangle triangle = entry.getValue();
			canvas.drawPath(triangle.getOuterPath(), triangleOuterPaint);
			canvas.drawPath(triangle.getInnerPath(), selected ? triangleInnerSelectedPaint : triangleInnerPaint);
		}
	}
	
	private void createTriangles()
	{
		triangles = new HashMap<>();
		for(GradientPoint point : gradient.getPoints())
			createTriangleForPoint(point);
	}
	
	private void createTriangleForPoint(GradientPoint point)
	{
		float xOffset = Utils.map(point.getPosition(), 0, 1, SIDE_MARGIN_PX, getWidth() - SIDE_MARGIN_PX);
		Triangle triangle = new Triangle(xOffset, TRIANGLE_Y_OFFSET_PX);
		triangles.put(point, triangle);
	}
	
	private void removeTriangleForPoint(GradientPoint point)
	{
		triangles.remove(point);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		boolean result = true;
		if(event.getAction() == MotionEvent.ACTION_DOWN) result = onTouchDown(event.getX());
		else if(event.getAction() == MotionEvent.ACTION_MOVE) onTouchMove(event.getX());
		else if(event.getAction() == MotionEvent.ACTION_UP) onTouchUp(event.getX());
		
		invalidate();
		return result;
	}
	
	private boolean onTouchDown(float x)
	{
		if(addingMode) return true;
		
		float gradientPos = calculateGradientPosition(x);
		GradientPoint nearestPoint = findNearestPoint(gradientPos);
		selectedPoint = nearestPoint;
		draggedPoint = nearestPoint;
		lastPosition = x;
		if(gradientUpdateListener != null)
			gradientUpdateListener.onGradientSelectionUpdated(getSelectedPosition(), getSelectedColor());
		return nearestPoint != null;
	}
	
	private GradientPoint findNearestPoint(float gradientPos)
	{
		GradientPoint nearest = null;
		float nearestDistance = MAX_TOUCH_DISTANCE;
		for(GradientPoint point : gradient.getPoints())
		{
			float distance = Math.abs(point.getPosition() - gradientPos);
			if(distance >= nearestDistance) continue;
			nearest = point;
			nearestDistance = distance;
		}
		return nearest;
	}
	
	private void onTouchMove(float x)
	{
		if(draggedPoint == null || addingMode) return;
		
		float screenOffset = x - lastPosition;
		float gradientOffset = calculateGradientDistance(screenOffset);
		float newPosition = draggedPoint.getPosition() + gradientOffset;
		if(newPosition < 0) newPosition = 0;
		else if(newPosition > 1) newPosition = 1;
		draggedPoint.setPosition(newPosition);
		lastPosition = x;
		
		gradient.sort();
		createTriangleForPoint(draggedPoint);
		updateTopBarPaint();
		updateBottomBarPaint();
		if(gradientUpdateListener != null) gradientUpdateListener.onGradientPositionUpdated(newPosition);
	}
	
	private float calculateGradientPosition(float viewPosition)
	{
		return Utils.map(viewPosition, SIDE_MARGIN_PX, getWidth() - SIDE_MARGIN_PX, 0, 1);
	}
	
	private float calculateGradientDistance(float distance)
	{
		return Utils.map(distance, 0, getWidth() - (2 * SIDE_MARGIN_PX), 0, 1);
	}
	
	private void onTouchUp(float x)
	{
		if(addingMode) addPoint(x);
		else
		{
			onTouchMove(x);
			draggedPoint = null;
		}
	}
	
	private void addPoint(float viewX)
	{
		float gradientPosition = calculateGradientPosition(viewX);
		GradientPoint point = gradient.addPoint(gradientPosition);
		
		createTriangleForPoint(point);
		updateTopBarPaint();
		updateBottomBarPaint();
		
		selectedPoint = point;
		invalidate();
		if(gradientUpdateListener == null) return;
		gradientUpdateListener.onGradientSelectionUpdated(getSelectedPosition(), getSelectedColor());
		gradientUpdateListener.onGradientPointAdded();
	}
	
	void deleteSelectedPoint()
	{
		if(selectedPoint == null || gradient.getPointsAmount() < 3) return;
		gradient.deletePoint(selectedPoint);
		
		removeTriangleForPoint(selectedPoint);
		updateTopBarPaint();
		updateBottomBarPaint();
		
		selectedPoint = null;
		invalidate();
		if(gradientUpdateListener != null)
			gradientUpdateListener.onGradientSelectionUpdated(getSelectedPosition(), getSelectedColor());
	}
	
	boolean canDeletePoint()
	{
		return selectedPoint != null && gradient.getPointsAmount() >= 3;
	}
	
	boolean isAnyColorSelected()
	{
		return selectedPoint != null;
	}
	
	private float getSelectedPosition()
	{
		if(selectedPoint == null) return -1;
		return selectedPoint.getPosition();
	}
	
	int getSelectedColor()
	{
		if(selectedPoint == null) return Color.TRANSPARENT;
		return selectedPoint.getColor();
	}
	
	void setSelectedColor(int color)
	{
		if(selectedPoint == null) return;
		selectedPoint.setColor(color);
		
		if(gradientUpdateListener != null) gradientUpdateListener.onGradientSelectionUpdated(getSelectedPosition(), color);
		updateTopBarPaint();
		updateBottomBarPaint();
		invalidate();
	}
	
	void setGradientUpdateListener(OnGradientEditorUpdateListener gradientUpdateListener)
	{
		this.gradientUpdateListener = gradientUpdateListener;
	}
	
	Gradient getGradient()
	{
		return gradient;
	}
	
	void setGradient(Gradient gradient)
	{
		this.gradient = gradient;
		this.topBarShader = null;
		this.bottomBarShader = null;
		this.triangles = null;
		invalidate();
	}
	
	void setAddingMode(boolean enabled)
	{
		addingMode = enabled;
	}
}