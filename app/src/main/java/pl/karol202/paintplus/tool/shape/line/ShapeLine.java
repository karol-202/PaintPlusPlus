package pl.karol202.paintplus.tool.shape.line;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import pl.karol202.paintplus.Image.OnImageChangeListener;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.tool.shape.OnShapeEditListener;
import pl.karol202.paintplus.tool.shape.Shape;
import pl.karol202.paintplus.tool.shape.properties.LineProperties;
import pl.karol202.paintplus.tool.shape.properties.ShapeProperties;

public class ShapeLine implements Shape
{
	private final int MAX_DISTANCE = 50;
	
	private int lineWidth;
	private Cap lineCap;
	
	private OnImageChangeListener imageChangeListener;
	private OnShapeEditListener shapeEditListener;
	private boolean editMode;
	private boolean lineCreated;
	private Point start;
	private Point end;
	private Paint paint;
	
	private int draggedIndex;
	private Point draggedPoint;
	private Point draggingStart;
	
	public ShapeLine(OnImageChangeListener imageChangeListener, OnShapeEditListener shapeEditListener)
	{
		this.lineWidth = 10;
		this.lineCap = Cap.ROUND;
		
		this.imageChangeListener = imageChangeListener;
		this.shapeEditListener = shapeEditListener;
		this.paint = new Paint();
		update();
	}
	
	@Override
	public int getName()
	{
		return R.string.shape_line;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_shape_line_black_24dp;
	}
	
	@Override
	public Class<? extends ShapeProperties> getPropertiesClass()
	{
		return LineProperties.class;
	}
	
	@Override
	public boolean onTouch(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN) onTouchStart(Math.round(event.getX()), Math.round(event.getY()));
		else if(event.getAction() == MotionEvent.ACTION_MOVE) onTouchMove(Math.round(event.getX()), Math.round(event.getY()));
		else if(event.getAction() == MotionEvent.ACTION_UP) onTouchStop(Math.round(event.getX()), Math.round(event.getY()));
		return true;
	}
	
	private void onTouchStart(int x, int y)
	{
		if(!editMode) enableEditMode();
		if(!lineCreated) start = new Point(x, y);
		else
		{
			float distanceToStart = calcDistance(start, x, y);
			float distanceToEnd = calcDistance(end, x, y);
			if(Math.min(distanceToStart, distanceToEnd) > MAX_DISTANCE)
			{
				draggedIndex = -1;
				draggedPoint = null;
				return;
			}
			if(distanceToStart < distanceToEnd)
			{
				draggedIndex = 0;
				draggedPoint = start;
			}
			else
			{
				draggedIndex = 1;
				draggedPoint = end;
			}
			draggingStart = new Point(x, y);
		}
	}
	
	private void enableEditMode()
	{
		editMode = true;
		lineCreated = false;
		start = null;
		end = null;
		shapeEditListener.onStartShapeEditing();
	}
	
	private float calcDistance(Point point, int x, int y)
	{
		return (float) Math.sqrt(Math.pow(point.x - x, 2) + Math.pow(point.y - y, 2));
	}
	
	private void onTouchMove(int x, int y)
	{
		if(!lineCreated) end = new Point(x, y);
		else dragPoint(new Point(x, y));
	}
	
	private void onTouchStop(int x, int y)
	{
		if(!lineCreated) end = new Point(x, y);
		else dragPoint(new Point(x, y));
		lineCreated = true;
	}
	
	private void dragPoint(Point current)
	{
		if(draggedIndex == -1) return;
		
		Point delta = new Point(current);
		delta.x -= draggingStart.x;
		delta.y -= draggingStart.y;
		
		Point dragged = new Point(draggedPoint);
		dragged.offset(delta.x, delta.y);
		if(this.draggedIndex == 0) start = dragged;
		else end = dragged;
	}
	
	@Override
	public void onScreenDraw(Canvas canvas, ColorsSet colors)
	{
		if(start == null || end == null) return;
		paint.setColor(colors.getFirstColor());
		canvas.drawLine(start.x, start.y, end.x, end.y, paint);
	}
	
	@Override
	public void apply(Canvas imageCanvas, ColorsSet colors)
	{
		if(start == null || end == null) return;
		paint.setColor(colors.getFirstColor());
		imageCanvas.drawLine(start.x, start.y, end.x, end.y, paint);
		cleanUp();
	}
	
	@Override
	public void cancel()
	{
		cleanUp();
	}
	
	@Override
	public boolean isInEditMode()
	{
		return editMode;
	}
	
	private void update()
	{
		paint.setStrokeWidth(lineWidth);
		paint.setStrokeCap(lineCap.getPaintCap());
		
		imageChangeListener.onImageChanged();
	}
	
	private void cleanUp()
	{
		editMode = false;
		lineCreated = false;
		start = null;
		end = null;
		imageChangeListener.onImageChanged();
	}
	
	public int getLineWidth()
	{
		return lineWidth;
	}
	
	public void setLineWidth(int lineWidth)
	{
		this.lineWidth = lineWidth;
		update();
	}
	
	public Cap getLineCap()
	{
		return lineCap;
	}
	
	public void setLineCap(Cap lineCap)
	{
		this.lineCap = lineCap;
		update();
	}
}