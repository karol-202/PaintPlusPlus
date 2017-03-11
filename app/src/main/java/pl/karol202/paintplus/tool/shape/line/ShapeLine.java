package pl.karol202.paintplus.tool.shape.line;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;
import pl.karol202.paintplus.helpers.HelpersManager;
import pl.karol202.paintplus.image.Image.OnImageChangeListener;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.tool.shape.OnShapeEditListener;
import pl.karol202.paintplus.tool.shape.Shape;
import pl.karol202.paintplus.tool.shape.ShapeProperties;

public class ShapeLine extends Shape
{
	private final int MAX_DISTANCE = 50;
	
	private int lineWidth;
	private Cap lineCap;
	
	private HelpersManager helpersManager;
	private boolean lineCreated;
	private Point start;
	private Point end;
	
	private int draggedIndex;
	private Point draggedPoint;
	private Point draggingStart;
	
	public ShapeLine(ColorsSet colors, OnImageChangeListener imageChangeListener, OnShapeEditListener shapeEditListener)
	{
		super(colors, imageChangeListener, shapeEditListener);
		this.lineWidth = 10;
		this.lineCap = Cap.ROUND;
		
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
	public boolean onTouch(MotionEvent event, HelpersManager manager)
	{
		helpersManager = manager;
		if(event.getAction() == MotionEvent.ACTION_DOWN) onTouchStart(Math.round(event.getX()), Math.round(event.getY()));
		else if(event.getAction() == MotionEvent.ACTION_MOVE) onTouchMove(Math.round(event.getX()), Math.round(event.getY()));
		else if(event.getAction() == MotionEvent.ACTION_UP) onTouchStop(Math.round(event.getX()), Math.round(event.getY()));
		return true;
	}
	
	private void onTouchStart(int x, int y)
	{
		if(!isInEditMode()) enableEditMode();
		if(!lineCreated) setStartPoint(new Point(x, y));
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

	private void onTouchMove(int x, int y)
	{
		if(!lineCreated) setEndPoint(new Point(x, y));
		else dragPoint(new Point(x, y));
	}
	
	private void onTouchStop(int x, int y)
	{
		if(!lineCreated) setEndPoint(new Point(x, y));
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
		if(this.draggedIndex == 0) setStartPoint(dragged);
		else setEndPoint(dragged);
	}
	
	private void setStartPoint(Point point)
	{
		PointF snapped = new PointF(point);
		helpersManager.snapPoint(snapped);
		start = new Point((int) snapped.x, (int) snapped.y);
	}
	
	private void setEndPoint(Point point)
	{
		PointF snapped = new PointF(point);
		helpersManager.snapPoint(snapped);
		end = new Point((int) snapped.x, (int) snapped.y);
	}
	
	@Override
	public void onScreenDraw(Canvas canvas)
	{
		if(start == null || end == null) return;
		updateColor();
		canvas.drawLine(start.x, start.y, end.x, end.y, getPaint());
	}
	
	@Override
	public void apply(Canvas imageCanvas)
	{
		if(start == null || end == null) return;
		update();
		imageCanvas.drawLine(start.x, start.y, end.x, end.y, getPaint());
		cleanUp();
	}
	
	@Override
	public void cancel()
	{
		cleanUp();
	}
	
	@Override
	public void update()
	{
		getPaint().setStrokeWidth(lineWidth);
		getPaint().setStrokeCap(lineCap.getPaintCap());
		super.update();
	}
	
	@Override
	public void cleanUp()
	{
		lineCreated = false;
		start = null;
		end = null;
		super.cleanUp();
	}
	
	@Override
	public void enableEditMode()
	{
		lineCreated = false;
		start = null;
		end = null;
		super.enableEditMode();
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