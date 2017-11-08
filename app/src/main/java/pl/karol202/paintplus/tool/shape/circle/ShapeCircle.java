package pl.karol202.paintplus.tool.shape.circle;

import android.graphics.*;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.shape.OnShapeEditListener;
import pl.karol202.paintplus.tool.shape.Shape;
import pl.karol202.paintplus.tool.shape.ShapeProperties;
import pl.karol202.paintplus.util.Utils;

public class ShapeCircle extends Shape
{
	private boolean fill;
	private int circleWidth;
	
	private boolean circleCreated;
	private Point center;
	private float radius;
	
	private Point draggingStart;
	private int draggedIndex;
	private Point centerAtBeginning;
	private float radiusAtBeginning;
	
	public ShapeCircle(Image image, OnShapeEditListener shapeEditListener)
	{
		super(image, shapeEditListener);
		this.fill = false;
		this.circleWidth = 30;
		
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
	public void onTouchStart(int x, int y)
	{
		if(!isInEditMode()) enableEditMode();
		if(!circleCreated) setCenterPoint(new Point(x, y));
		else
		{
			float distanceToCenter = calcDistance(center, x, y);
			float distanceToRadius = Math.abs(distanceToCenter - radius);
			
			draggingStart = new Point(x, y);
			centerAtBeginning = center;
			radiusAtBeginning = radius;
			
			if(Math.min(distanceToCenter, distanceToRadius) > getMaxTouchDistance()) draggedIndex = -1;
			else if(distanceToCenter < distanceToRadius) draggedIndex = 0;
			else draggedIndex = 1;
		}
	}
	
	@Override
	public void onTouchMove(int x, int y)
	{
		Point current = new Point(x, y);
		if(!circleCreated) dragRadius(current);
		else drag(current);
	}
	
	@Override
	public void onTouchStop(int x, int y)
	{
		onTouchMove(x, y);
		
		circleCreated = true;
		draggingStart = null;
	}
	
	private void drag(Point current)
	{
		if(draggedIndex == 0) dragCenter(current);
		else if(draggedIndex == 1) dragRadius(current);
	}
	
	private void dragCenter(Point current)
	{
		Point delta = new Point(current);
		delta.x -= draggingStart.x;
		delta.y -= draggingStart.y;
		
		Point newCenter = new Point(centerAtBeginning);
		newCenter.offset(delta.x, delta.y);
		setCenterPoint(newCenter);
	}
	
	private void dragRadius(Point current)
	{
		if(draggingStart != null)
		{
			double theta = Math.toRadians(Utils.getAngle(center, current));
			float rCurrent = calcDistance(center, current.x, current.y);
			float rDraggingStart = calcDistance(center, draggingStart.x, draggingStart.y);
			float rBeginning = radiusAtBeginning;
			
			float rDelta = rCurrent - rDraggingStart;
			float rResult = rBeginning + rDelta;
			
			float x = (float) (rResult * Math.cos(theta)) + center.x;
			float y = (float) (rResult * Math.sin(theta)) + center.y;
			PointF result = new PointF(x, y);
			getHelpersManager().snapPoint(result);
			
			radius = calcDistance(center, (int) result.x, (int) result.y);
		}
		else
		{
			PointF snapped = new PointF(current);
			getHelpersManager().snapPoint(snapped);
			radius = calcDistance(center, (int) snapped.x, (int) snapped.y);
		}
	}
	
	private void setCenterPoint(Point point)
	{
		PointF snapped = new PointF(point);
		getHelpersManager().snapPoint(snapped);
		center = new Point((int) snapped.x, (int) snapped.y);
	}
	
	@Override
	public void expandDirtyRect(Rect dirtyRect)
	{
		dirtyRect.left = Math.min(dirtyRect.left, (int) (center.x - radius - circleWidth));
		dirtyRect.top = Math.min(dirtyRect.top, (int) (center.y - radius - circleWidth));
		dirtyRect.right = Math.max(dirtyRect.right, (int) (center.x + radius + circleWidth));
		dirtyRect.bottom = Math.max(dirtyRect.bottom, (int) (center.y + radius + circleWidth));
	}
	
	@Override
	public void onScreenDraw(Canvas canvas, boolean translucent)
	{
		if(center == null || radius == -1) return;
		updateColor(translucent);
		canvas.drawCircle(center.x, center.y, radius, getPaint());
	}
	
	@Override
	public Rect getBoundsOfShape()
	{
		int halfSize = (int) radius + circleWidth;
		return new Rect(center.x - halfSize, center.y - halfSize,
					   center.x + halfSize, center.y + halfSize);
	}
	
	@Override
	public void apply(Canvas imageCanvas)
	{
		if(center == null || radius == -1) return;
		update();
		imageCanvas.drawCircle(center.x, center.y, radius, getPaint());
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
		getPaint().setStyle(fill ? Paint.Style.FILL_AND_STROKE : Paint.Style.STROKE);
		getPaint().setStrokeWidth(circleWidth);
		super.update();
	}
	
	@Override
	public void cleanUp()
	{
		circleCreated = false;
		center = null;
		radius = -1;
		super.cleanUp();
	}
	
	@Override
	public void enableEditMode()
	{
		circleCreated = false;
		center = null;
		radius = -1;
		super.enableEditMode();
	}
	
	boolean isFilled()
	{
		return fill;
	}
	
	void setFill(boolean fill)
	{
		this.fill = fill;
		update();
	}
	
	int getCircleWidth()
	{
		return circleWidth;
	}
	
	void setCircleWidth(int circleWidth)
	{
		this.circleWidth = circleWidth;
		update();
	}
}