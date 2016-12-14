package pl.karol202.paintplus.tool.shape.circle;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import pl.karol202.paintplus.Image.OnImageChangeListener;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.tool.shape.OnShapeEditListener;
import pl.karol202.paintplus.tool.shape.Shape;
import pl.karol202.paintplus.tool.shape.ShapeProperties;

public class ShapeCircle implements Shape
{
	private final int MAX_DISTANCE = 50;
	
	private boolean fill;
	private int circleWidth;
	
	private OnImageChangeListener imageChangeListener;
	private OnShapeEditListener shapeEditListener;
	private boolean editMode;
	private boolean circleCreated;
	private Point center;
	private float radius;
	private Paint paint;
	
	private int draggedIndex;
	private Point centerAtBegining;
	private Point draggingStart;
	
	public ShapeCircle(OnImageChangeListener imageChangeListener, OnShapeEditListener shapeEditListener)
	{
		this.fill = false;
		this.circleWidth = 50;
		
		this.imageChangeListener = imageChangeListener;
		this.shapeEditListener = shapeEditListener;
		this.paint = new Paint();
		update();
	}
	
	@Override
	public int getName()
	{
		return R.string.shape_circle;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_shape_circle_black_24dp;
	}
	
	@Override
	public Class<? extends ShapeProperties> getPropertiesClass()
	{
		return CircleProperties.class;
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
		if(!circleCreated) center = new Point(x, y);
		else
		{
			float distanceToCenter = calcDistance(center, x, y);
			float distanceToRadius = Math.abs(distanceToCenter - radius);
			if(Math.min(distanceToCenter, distanceToRadius) > MAX_DISTANCE)
			{
				draggedIndex = -1;
				centerAtBegining = null;
				draggingStart = null;
				return;
			}
			if(distanceToCenter < distanceToRadius)
			{
				draggedIndex = 0;
				centerAtBegining = center;
			}
			else
			{
				draggedIndex = 1;
				centerAtBegining = null;
			}
			draggingStart = new Point(x, y);
		}
	}
	
	private void enableEditMode()
	{
		editMode = true;
		circleCreated = false;
		center = null;
		radius = -1;
		shapeEditListener.onStartShapeEditing();
	}
	
	private void onTouchMove(int x, int y)
	{
		if(!circleCreated) radius = calcDistance(center, x, y);
		else drag(new Point(x, y));
	}
	
	private void onTouchStop(int x, int y)
	{
		if(!circleCreated) radius = calcDistance(center, x, y);
		else drag(new Point(x, y));
		circleCreated = true;
	}
	
	private void drag(Point current)
	{
		if(draggedIndex == 0)
		{
			Point delta = new Point(current);
			delta.x -= draggingStart.x;
			delta.y -= draggingStart.y;
			
			Point newCenter = new Point(centerAtBegining);
			newCenter.offset(delta.x, delta.y);
			center = newCenter;
		}
		else if(draggedIndex == 1) radius = calcDistance(current, center.x, center.y);
	}
	
	private float calcDistance(Point point, int x, int y)
	{
		return (float) Math.sqrt(Math.pow(point.x - x, 2) + Math.pow(point.y - y, 2));
	}
	
	@Override
	public void onScreenDraw(Canvas canvas, ColorsSet colors)
	{
		if(center == null || radius == -1) return;
		paint.setColor(colors.getFirstColor());
		canvas.drawCircle(center.x, center.y, radius, paint);
	}
	
	@Override
	public void apply(Canvas imageCanvas, ColorsSet colors)
	{
		if(center == null || radius == -1) return;
		paint.setColor(colors.getFirstColor());
		imageCanvas.drawCircle(center.x, center.y, radius, paint);
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
		paint.setStyle(fill ? Paint.Style.FILL : Paint.Style.STROKE);
		paint.setStrokeWidth(circleWidth);
		
		imageChangeListener.onImageChanged();
	}
	
	private void cleanUp()
	{
		editMode = false;
		circleCreated = false;
		center = null;
		radius = -1;
		imageChangeListener.onImageChanged();
	}
	
	public boolean isFilled()
	{
		return fill;
	}
	
	public void setFill(boolean fill)
	{
		this.fill = fill;
		update();
	}
	
	public int getCircleWidth()
	{
		return circleWidth;
	}
	
	public void setCircleWidth(int circleWidth)
	{
		this.circleWidth = circleWidth;
		update();
	}
}