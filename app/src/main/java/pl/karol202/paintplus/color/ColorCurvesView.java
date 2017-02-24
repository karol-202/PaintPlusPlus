package pl.karol202.paintplus.color;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.google.firebase.crash.FirebaseCrash;
import pl.karol202.paintplus.color.ColorChannel.ColorChannelType;
import pl.karol202.paintplus.color.manipulators.params.CurveManipulatorParams;
import pl.karol202.paintplus.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

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
	private final int ADDITIONAL_MARGIN_SPACING = 9;
	
	private final int MAX_TOUCH_DISTANCE = 65;
	private final int REMOVE_LIMIT = 100;
	
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
		
		channelType = null;
		channelIn = null;
		channelOut = null;
		
		viewSize = new Point();
		
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
	
	private void initChannels(ColorChannelType type)
	{
		if(channelType != null)
		{
			FirebaseCrash.report(new RuntimeException("Channel settings are already set!"));
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
		canvas.drawRect(LEFT_GRID_MARGIN, viewSize.y - HORIZONTAL_SCALE_HEIGHT - 1,
				viewSize.x - RIGHT_GRID_MARGIN, viewSize.y - 1, horizontalScalePaint);
		canvas.drawRect(0, TOP_GRID_MARGIN, VERTICAL_SCALE_WIDTH,
				viewSize.y - BOTTOM_GRID_MARGIN, verticalScalePaint);
	}
	
	private void drawPoints(Canvas canvas)
	{
		if(points == null) createPoints();
		canvas.clipRect(LEFT_GRID_MARGIN - ADDITIONAL_MARGIN_SPACING,
				TOP_GRID_MARGIN - ADDITIONAL_MARGIN_SPACING,
				canvas.getWidth() - RIGHT_GRID_MARGIN + ADDITIONAL_MARGIN_SPACING,
				canvas.getHeight() - BOTTOM_GRID_MARGIN + ADDITIONAL_MARGIN_SPACING,
				Region.Op.REPLACE);
		
		for(RectF point : points) canvas.drawOval(point, pointPaint);
		for(RectF point : innerPoints) canvas.drawOval(point, pointInnerPaint);
	}
	
	private void drawCurve(Canvas canvas)
	{
		if(points == null) createPoints();
		canvas.clipRect(LEFT_GRID_MARGIN, TOP_GRID_MARGIN,
						canvas.getWidth() - RIGHT_GRID_MARGIN, canvas.getHeight() - BOTTOM_GRID_MARGIN,
						Region.Op.REPLACE);
		
		float lastX = LEFT_GRID_MARGIN;
		float lastY = points.get(0).centerY();
		for(RectF rect : points)
		{
			float x = rect.centerX();
			float y = rect.centerY();
			canvas.drawLine(lastX, lastY, x, y, curvePaint);
			lastX = x;
			lastY = y;
		}
		canvas.drawLine(lastX, lastY, canvas.getWidth() - RIGHT_GRID_MARGIN, lastY, curvePaint);
	}
	
	private void createGrid()
	{
		float[] linesHorizontal = new float[HORIZONTAL_GRID_LINES * 4];
		for(int i = 0; i < HORIZONTAL_GRID_LINES; i++)
		{
			int verticalMargins = TOP_GRID_MARGIN + BOTTOM_GRID_MARGIN;
			float y = (viewSize.y - verticalMargins) * (i / (HORIZONTAL_GRID_LINES - 1f)) + TOP_GRID_MARGIN;
			linesHorizontal[i * 4] = LEFT_GRID_MARGIN;
			linesHorizontal[i * 4 + 1] = y;
			linesHorizontal[i * 4 + 2] = viewSize.x - RIGHT_GRID_MARGIN;
			linesHorizontal[i * 4 + 3] = y;
		}
		
		float[] linesVertical = new float[VERTICAL_GRID_LINES * 4];
		for(int i = 0; i < VERTICAL_GRID_LINES; i++)
		{
			int horizontalMargins = LEFT_GRID_MARGIN + RIGHT_GRID_MARGIN;
			float x = (viewSize.x - horizontalMargins) * (i / (VERTICAL_GRID_LINES - 1f)) + LEFT_GRID_MARGIN;
			linesVertical[i * 4] = x;
			linesVertical[i * 4 + 1] = TOP_GRID_MARGIN;
			linesVertical[i * 4 + 2] = x;
			linesVertical[i * 4 + 3] = viewSize.y - BOTTOM_GRID_MARGIN;
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
			horizontalScaleShader = new LinearGradient(LEFT_GRID_MARGIN, 0, viewSize.x - RIGHT_GRID_MARGIN,
					0, firstColor, secondColor, Shader.TileMode.CLAMP);
		}
		else
		{
			int[] colors = new int[] { Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED };
			horizontalScaleShader = new LinearGradient(LEFT_GRID_MARGIN, 0, viewSize.x - RIGHT_GRID_MARGIN,
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
			verticalScaleShader = new LinearGradient(0, viewSize.y - BOTTOM_GRID_MARGIN, 0,
					TOP_GRID_MARGIN, firstColor, secondColor, Shader.TileMode.CLAMP);
		}
		else
		{
			int[] colors = new int[] { Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED };
			verticalScaleShader = new LinearGradient(0, viewSize.y - BOTTOM_GRID_MARGIN, 0,
					TOP_GRID_MARGIN, colors, null, Shader.TileMode.CLAMP);
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
		oldTouchPoint = new Point(x, y);
		
		Point nearest = null;
		int nearestIndex = -1;
		float shortestDistance = -1;
		for(int i = 0; i < points.size(); i++)
		{
			RectF rect = points.get(i);
			Point point = new Point((int) rect.centerX(), (int) rect.centerY());
			float distance = distance(point, oldTouchPoint);
			if((nearest == null || distance < shortestDistance) && distance < MAX_TOUCH_DISTANCE)
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
		int left = point.x - LEFT_GRID_MARGIN;
		int top = point.y - TOP_GRID_MARGIN;
		int right = point.x - (viewSize.x - RIGHT_GRID_MARGIN);
		int bottom = point.y - (viewSize.y - BOTTOM_GRID_MARGIN);
		
		boolean shouldRemove = left <= -REMOVE_LIMIT || top <= -REMOVE_LIMIT ||
							   right >= REMOVE_LIMIT || bottom >= REMOVE_LIMIT;
		if(left < 0) point.x = LEFT_GRID_MARGIN;
		else if(right > 0) point.x = viewSize.x - RIGHT_GRID_MARGIN;
		if(top < 0) point.y = TOP_GRID_MARGIN;
		else if(bottom > 0) point.y = viewSize.y - BOTTOM_GRID_MARGIN;
		return !shouldRemove;
	}
	
	private float distance(Point first, Point second)
	{
		return (float) Math.sqrt(Math.pow(first.x - second.x, 2) + Math.pow(first.y - second.y, 2));
	}
	
	private Point curveToScreen(Point point)
	{
		Point newPoint = new Point(point);
		newPoint.x = Math.round(Utils.map(point.x, 0, channelIn.getMaxValue(),
										  LEFT_GRID_MARGIN, viewSize.x - RIGHT_GRID_MARGIN));
		newPoint.y = Math.round(Utils.map(point.y, channelOut.getMaxValue(), 0,
										  TOP_GRID_MARGIN, viewSize.y - BOTTOM_GRID_MARGIN));
		return newPoint;
	}
	
	private Point screenToCurve(Point point)
	{
		Point newPoint = new Point(point);
		newPoint.x = Math.round(Utils.map(point.x, LEFT_GRID_MARGIN, viewSize.x - RIGHT_GRID_MARGIN,
										  0, channelIn.getMaxValue()));
		newPoint.y = Math.round(Utils.map(point.y, TOP_GRID_MARGIN, viewSize.y - BOTTOM_GRID_MARGIN,
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
		return String.format(Locale.US, "X: %1$d   Y: %2$d", newDraggedCurvePoint.x, newDraggedCurvePoint.y);
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