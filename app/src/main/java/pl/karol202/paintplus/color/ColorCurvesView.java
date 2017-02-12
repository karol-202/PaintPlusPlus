package pl.karol202.paintplus.color;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import pl.karol202.paintplus.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class ColorCurvesView extends View
{
	private final int HORIZONTAL_GRID_LINES = 7;
	private final int VERTICAL_GRID_LINES = 7;
	
	private final int LEFT_GRID_MARGIN = 20;
	private final int TOP_GRID_MARGIN = 9;
	private final int RIGHT_GRID_MARGIN = 9;
	private final int BOTTOM_GRID_MARGIN = 20;
	
	private final int HORIZONTAL_SCALE_HEIGHT = 10;
	private final int VERTICAL_SCALE_WIDTH = 10;
	
	private final int POINT_RADIUS = 8;
	private final int POINT_INNER_RADIUS = 3;
	
	private final int MAX_TOUCH_DISTANCE = 50;
	
	private ColorChannel channel;
	private HashMap<ColorChannel, ColorCurve> curves;
	
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
	
	private Point draggedScreenPoint;
	private Point draggedCurvePoint;
	private int draggedPointIndex;
	private Point touchStartPoint;
	
	public ColorCurvesView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		channel = ColorChannel.VALUE;
		curves = new HashMap<>();
		for(ColorChannel channel : ColorChannel.values()) curves.put(channel, ColorCurve.defaultCurve());
		
		gridPaint = new Paint();
		gridPaint.setColor(Color.LTGRAY);
		gridPaint.setStrokeWidth(2f);
		
		pointPaint = new Paint();
		pointPaint.setColor(Color.BLACK);
		pointPaint.setAntiAlias(true);
		
		pointInnerPaint = new Paint();
		pointInnerPaint.setColor(Color.WHITE);
		pointInnerPaint.setAntiAlias(true);
		
		curvePaint = new Paint();
		curvePaint.setColor(Color.BLACK);
		curvePaint.setStrokeWidth(3f);
		curvePaint.setAntiAlias(true);
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
		viewSize = new Point(canvas.getWidth(), canvas.getHeight());
		
		drawGrid(canvas);
		drawScale(canvas);
		
		Point[] points = getCurrentCurve().getPoints();
		drawCurve(canvas, points);
		drawPoints(canvas, points);
	}
	
	private void drawGrid(Canvas canvas)
	{
		if(grid == null) createGrid(canvas.getWidth(), canvas.getHeight());
		canvas.drawLines(grid, gridPaint);
	}
	
	private void drawScale(Canvas canvas)
	{
		if(horizontalScaleShader == null || verticalScaleShader == null) createScalePaints(canvas);
		canvas.drawRect(LEFT_GRID_MARGIN, canvas.getHeight() - HORIZONTAL_SCALE_HEIGHT - 1,
						canvas.getWidth() - RIGHT_GRID_MARGIN, canvas.getHeight() - 1, horizontalScalePaint);
		canvas.drawRect(0, TOP_GRID_MARGIN, VERTICAL_SCALE_WIDTH,
						canvas.getHeight() - BOTTOM_GRID_MARGIN, verticalScalePaint);
	}
	
	private void drawPoints(Canvas canvas, Point[] curvePoints)
	{
		if(points == null) createPoints(curvePoints);
		for(RectF point : points) canvas.drawOval(point, pointPaint);
		for(RectF point : innerPoints) canvas.drawOval(point, pointInnerPaint);
	}
	
	private void drawCurve(Canvas canvas, Point[] curvePoints)
	{
		if(points == null) createPoints(curvePoints);
		float lastX = -1;
		float lastY = -1;
		for(RectF rect : points)
		{
			float x = rect.centerX();
			float y = rect.centerY();
			if(lastX != -1 && lastY != -1) canvas.drawLine(lastX, lastY, x, y, curvePaint);
			lastX = x;
			lastY = y;
		}
	}
	
	private void createGrid(int width, int height)
	{
		float[] linesHorizontal = new float[HORIZONTAL_GRID_LINES * 4];
		for(int i = 0; i < HORIZONTAL_GRID_LINES; i++)
		{
			int verticalMargins = TOP_GRID_MARGIN + BOTTOM_GRID_MARGIN;
			float y = (height - verticalMargins) * (i / (HORIZONTAL_GRID_LINES - 1f)) + TOP_GRID_MARGIN;
			linesHorizontal[i * 4] = LEFT_GRID_MARGIN;
			linesHorizontal[i * 4 + 1] = y;
			linesHorizontal[i * 4 + 2] = width - RIGHT_GRID_MARGIN;
			linesHorizontal[i * 4 + 3] = y;
		}
		
		float[] linesVertical = new float[VERTICAL_GRID_LINES * 4];
		for(int i = 0; i < VERTICAL_GRID_LINES; i++)
		{
			int horizontalMargins = LEFT_GRID_MARGIN + RIGHT_GRID_MARGIN;
			float x = (width - horizontalMargins) * (i / (VERTICAL_GRID_LINES - 1f)) + LEFT_GRID_MARGIN;
			linesVertical[i * 4] = x;
			linesVertical[i * 4 + 1] = TOP_GRID_MARGIN;
			linesVertical[i * 4 + 2] = x;
			linesVertical[i * 4 + 3] = height - BOTTOM_GRID_MARGIN;
		}
		
		grid = new float[linesHorizontal.length + linesVertical.length];
		System.arraycopy(linesHorizontal, 0, grid, 0, linesHorizontal.length);
		System.arraycopy(linesVertical, 0, grid, linesHorizontal.length, linesVertical.length);
	}
	
	private void createScalePaints(Canvas canvas)
	{
		if(channel != ColorChannel.HUE)
		{
			int firstColor = Color.BLACK;
			int secondColor = Color.WHITE;
			if(channel == ColorChannel.RED) secondColor = Color.RED;
			else if(channel == ColorChannel.GREEN) secondColor = Color.GREEN;
			else if(channel == ColorChannel.BLUE) secondColor = Color.BLUE;
			else if(channel == ColorChannel.SATURATION)
			{
				firstColor = Color.WHITE;
				secondColor = Color.RED;
			}
			horizontalScaleShader = new LinearGradient(LEFT_GRID_MARGIN, 0, canvas.getWidth() - RIGHT_GRID_MARGIN,
					0, firstColor, secondColor, Shader.TileMode.CLAMP);
			verticalScaleShader = new LinearGradient(0, canvas.getHeight() - BOTTOM_GRID_MARGIN, 0,
					TOP_GRID_MARGIN, firstColor, secondColor, Shader.TileMode.CLAMP);
		}
		else
		{
			int[] colors = new int[] { Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED };
			horizontalScaleShader = new LinearGradient(LEFT_GRID_MARGIN, 0, canvas.getWidth() - RIGHT_GRID_MARGIN,
					0, colors, null, Shader.TileMode.CLAMP);
			verticalScaleShader = new LinearGradient(0, canvas.getHeight() - BOTTOM_GRID_MARGIN, 0,
					TOP_GRID_MARGIN, colors, null, Shader.TileMode.CLAMP);
		}
		horizontalScalePaint = new Paint();
		horizontalScalePaint.setShader(horizontalScaleShader);
		horizontalScalePaint.setStyle(Paint.Style.FILL);
		
		verticalScalePaint = new Paint();
		verticalScalePaint.setShader(verticalScaleShader);
		verticalScalePaint.setStyle(Paint.Style.FILL);
	}
	
	private void createPoints(Point[] curvePoints)
	{
		points = new ArrayList<>();
		innerPoints = new ArrayList<>();
		for(Point point : curvePoints)
		{
			Point newPoint = curveToScreen(point);
			RectF oval = new RectF(newPoint.x - POINT_RADIUS, newPoint.y - POINT_RADIUS,
								   newPoint.x + POINT_RADIUS, newPoint.y + POINT_RADIUS);
			RectF innerOval = new RectF(newPoint.x - POINT_INNER_RADIUS, newPoint.y - POINT_INNER_RADIUS,
										newPoint.x + POINT_INNER_RADIUS, newPoint.y + POINT_INNER_RADIUS);
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
		Point touch = new Point(x, y);
		
		Point nearest = null;
		int nearestIndex = -1;
		float shortestDistance = -1;
		for(int i = 0; i < points.size(); i++)
		{
			RectF rect = points.get(i);
			Point point = new Point((int) rect.centerX(), (int) rect.centerY());
			float distance = distance(point, touch);
			if((nearest == null || distance < shortestDistance) && distance < MAX_TOUCH_DISTANCE)
			{
				nearest = point;
				nearestIndex = i;
				shortestDistance = distance;
			}
		}
		if(nearest != null)
		{
			draggedScreenPoint = nearest;
			draggedPointIndex = nearestIndex;
			
			Point[] points = getCurrentCurve().getPoints();
			draggedCurvePoint = points[draggedPointIndex];
			
			touchStartPoint = touch;
		}
	}
	
	private void onTouchMove(int x, int y)
	{
		if(draggedScreenPoint != null)
		{
			Point newScreenPoint = new Point(draggedScreenPoint);
			newScreenPoint.offset(x - touchStartPoint.x, y - touchStartPoint.y);
			
			RectF oval = new RectF(newScreenPoint.x - POINT_RADIUS, newScreenPoint.y - POINT_RADIUS,
					newScreenPoint.x + POINT_RADIUS, newScreenPoint.y + POINT_RADIUS);
			RectF innerOval = new RectF(newScreenPoint.x - POINT_INNER_RADIUS, newScreenPoint.y - POINT_INNER_RADIUS,
					newScreenPoint.x + POINT_INNER_RADIUS, newScreenPoint.y + POINT_INNER_RADIUS);
			points.set(draggedPointIndex, oval);
			innerPoints.set(draggedPointIndex, innerOval);
		}
	}
	
	private void onTouchUp(int x, int y)
	{
		if(draggedScreenPoint != null)
		{
			Point newScreenPoint = new Point(draggedScreenPoint);
			newScreenPoint.offset(x - touchStartPoint.x, y - touchStartPoint.y);
			
			Point newCurvePoint = screenToCurve(newScreenPoint);
			getCurrentCurve().movePoint(draggedCurvePoint, newCurvePoint);
			
			draggedScreenPoint = null;
			draggedPointIndex = -1;
			draggedCurvePoint = null;
			touchStartPoint = null;
			points = null;
		}
	}
	
	private float distance(Point first, Point second)
	{
		return (float) Math.sqrt(Math.pow(first.x - second.x, 2) + Math.pow(first.y - second.y, 2));
	}
	
	private Point curveToScreen(Point point)
	{
		Point newPoint = new Point(point);
		newPoint.x = Math.round(Utils.map(point.x, 0, 255,
										  LEFT_GRID_MARGIN, viewSize.x - RIGHT_GRID_MARGIN));
		newPoint.y = Math.round(Utils.map(point.y, 255, 0,
										  TOP_GRID_MARGIN, viewSize.y - BOTTOM_GRID_MARGIN));
		return newPoint;
	}
	
	private Point screenToCurve(Point point)
	{
		Point newPoint = new Point(point);
		newPoint.x = Math.round(Utils.map(point.x, LEFT_GRID_MARGIN, viewSize.x - RIGHT_GRID_MARGIN,
										  0, 255));
		newPoint.y = Math.round(Utils.map(point.y, TOP_GRID_MARGIN, viewSize.y - BOTTOM_GRID_MARGIN,
									  	  255, 0));
		return newPoint;
	}
	
	private void updatePoints()
	{
		this.points = null;
	}
	
	private ColorCurve getCurrentCurve()
	{
		return curves.get(channel);
	}
	
	public void setChannel(ColorChannel channel)
	{
		this.channel = channel;
		this.horizontalScaleShader = null;
		this.verticalScaleShader = null;
		updatePoints();
		invalidate();
	}
}