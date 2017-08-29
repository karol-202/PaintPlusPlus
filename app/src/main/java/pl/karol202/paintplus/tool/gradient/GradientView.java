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

class GradientView extends View
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
			outerPath.offset(xOffset, yOffset);
			
			innerPath = new Path(TRIANGLE_INNER_PATH);
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
	
	private static final int BORDER_WIDTH = 1;
	
	private static final int SIDE_MARGIN = 20;
	private static final int TOP_MARGIN = 1;
	
	private static final int TOP_BAR_HEIGHT = 30;
	private static final int BOTTOM_BAR_HEIGHT = 50;
	
	private static final float MAX_TOUCH_DISTANCE = 0.05f;
	
	private static final int TRIANGLE_Y_OFFSET = TOP_MARGIN + TOP_BAR_HEIGHT + BOTTOM_BAR_HEIGHT - 10;
	private static final Path TRIANGLE_OUTER_PATH = new Path();
	private static final Path TRIANGLE_INNER_PATH = new Path();
	static
	{
		TRIANGLE_OUTER_PATH.moveTo(0, 0);
		TRIANGLE_OUTER_PATH.lineTo(20, 40);
		TRIANGLE_OUTER_PATH.lineTo(-20, 40);
		TRIANGLE_OUTER_PATH.close();
		
		TRIANGLE_INNER_PATH.moveTo(0, 10);
		TRIANGLE_INNER_PATH.lineTo(13, 36);
		TRIANGLE_INNER_PATH.lineTo(-13, 36);
		TRIANGLE_INNER_PATH.close();
	}
	
	private static final int HEIGHT = TOP_MARGIN + TOP_BAR_HEIGHT + BOTTOM_BAR_HEIGHT + 40;
	
	private OnGradientEditorUpdateListener gradientUpdateListener;
	private Gradient gradient;
	private boolean addingMode;
	
	private Rect borderRect;
	private Paint borderPaint;
	private Rect checkerboardRect;
	private Paint checkerboardPaint;
	
	private Rect topBarRect;
	private Paint topBarPaint;
	private Shader topBarShader;
	
	private Rect bottomBarRect;
	private Paint bottomBarPaint;
	private Shader bottomBarShader;
	
	private Map<GradientPoint, Triangle> triangles;
	private Paint triangleOuterPaint;
	private Paint triangleInnerPaint;
	private Paint triangleInnerSelectedPaint;
	
	private GradientPoint selectedPoint;
	
	private GradientPoint draggedPoint;
	private float lastPosition;
	
	GradientView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		gradient = null;
		
		borderPaint = new Paint();
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(BORDER_WIDTH);
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
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), HEIGHT);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		drawBorder(canvas);
		drawCheckerboard(canvas);
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
		borderRect = new Rect(SIDE_MARGIN - BORDER_WIDTH, TOP_MARGIN - BORDER_WIDTH, getWidth() - SIDE_MARGIN,
				TOP_MARGIN + TOP_BAR_HEIGHT + BOTTOM_BAR_HEIGHT);
	}
	
	private void drawCheckerboard(Canvas canvas)
	{
		if(checkerboardRect == null) updateCheckerboardRect();
		canvas.drawRect(checkerboardRect, checkerboardPaint);
	}
	
	private void updateCheckerboardRect()
	{
		checkerboardRect = new Rect(SIDE_MARGIN, TOP_MARGIN,
							  getWidth() - SIDE_MARGIN, TOP_MARGIN + TOP_BAR_HEIGHT + BOTTOM_BAR_HEIGHT);
	}
	
	private void drawTopBar(Canvas canvas)
	{
		if(topBarRect == null) updateTopBarRect();
		if(topBarShader == null) updateTopBarPaint();
		canvas.drawRect(topBarRect, topBarPaint);
	}
	
	private void updateTopBarRect()
	{
		topBarRect = new Rect(SIDE_MARGIN, TOP_MARGIN, getWidth() - SIDE_MARGIN, TOP_MARGIN + TOP_BAR_HEIGHT);
	}
	
	private void updateTopBarPaint()
	{
		topBarShader = new LinearGradient(SIDE_MARGIN, 0, getWidth() - SIDE_MARGIN, 0,
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
		bottomBarRect = new Rect(SIDE_MARGIN, TOP_MARGIN + TOP_BAR_HEIGHT, getWidth() - SIDE_MARGIN,
								 TOP_MARGIN + TOP_BAR_HEIGHT + BOTTOM_BAR_HEIGHT);
	}
	
	private void updateBottomBarPaint()
	{
		int[] colors = removeAlphaFromColorsArray(gradient.getColorsArray());
		bottomBarShader = new LinearGradient(SIDE_MARGIN, 0, getWidth() - SIDE_MARGIN, 0,
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
		float xOffset = Utils.map(point.getPosition(), 0, 1, SIDE_MARGIN, getWidth() - SIDE_MARGIN);
		Triangle triangle = new Triangle(xOffset, TRIANGLE_Y_OFFSET);
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
		if(gradientUpdateListener != null) gradientUpdateListener.onGradientSelectionUpdated(getSelectedPosition(), getSelectedColor());
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
		return Utils.map(viewPosition, SIDE_MARGIN, getWidth() - SIDE_MARGIN, 0, 1);
	}
	
	private float calculateGradientDistance(float distance)
	{
		return Utils.map(distance, 0, getWidth() - (2 * SIDE_MARGIN), 0, 1);
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
		if(gradientUpdateListener != null) gradientUpdateListener.onGradientSelectionUpdated(getSelectedPosition(), getSelectedColor());
		if(gradientUpdateListener != null) gradientUpdateListener.onGradientPointAdded();
		
		invalidate();
	}
	
	void deleteSelectedPoint()
	{
		if(selectedPoint == null || gradient.getPointsAmount() < 3) return;
		gradient.deletePoint(selectedPoint);
		
		removeTriangleForPoint(selectedPoint);
		updateTopBarPaint();
		updateBottomBarPaint();
		
		selectedPoint = null;
		if(gradientUpdateListener != null) gradientUpdateListener.onGradientSelectionUpdated(getSelectedPosition(), getSelectedColor());
		
		invalidate();
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
		if(selectedPoint == null) return Color.MAGENTA;
		return selectedPoint.getColor();
	}
	
	void setSelectedColor(int color)
	{
		if(selectedPoint == null) return;
		selectedPoint.setColor(color);
		
		if(gradientUpdateListener != null) gradientUpdateListener.onGradientSelectionUpdated(getSelectedPosition(), color);
		updateTopBarPaint();
		updateBottomBarPaint();
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