package pl.karol202.paintplus.tool.shape.polygon;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.MotionEvent;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.tool.shape.OnShapeEditListener;
import pl.karol202.paintplus.tool.shape.Shape;
import pl.karol202.paintplus.tool.shape.ShapeProperties;

public class ShapePolygon extends Shape
{
	private final int MAX_DISTANCE = 50;
	
	private int sides;
	private boolean fill;
	private int lineWidth;
	
	private boolean polygonCreated;
	private Point center;
	private float radiusOEC; //Radius of escribed circle
	private float angle;
	private Path path;
	
	private int draggedIndex;
	private Point centerAtBeginning;
	private Point draggingStart;
	
	public ShapePolygon(ColorsSet colors, Image.OnImageChangeListener imageChangeListener, OnShapeEditListener shapeEditListener)
	{
		super(colors, imageChangeListener, shapeEditListener);
		this.sides = 4;
		this.fill = false;
		this.lineWidth = 30;
		
		update();
	}
	
	@Override
	public int getName()
	{
		return R.string.shape_polygon;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_shape_polygon_black_24dp;
	}
	
	@Override
	public Class<? extends ShapeProperties> getPropertiesClass()
	{
		return PolygonProperties.class;
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
		if(!isInEditMode()) enableEditMode();
		if(!polygonCreated) center = new Point(x, y);
		else
		{
			float distanceToCenter = calcDistance(center, x, y);
			float distanceToRadius = Math.abs(distanceToCenter - radiusOEC);
			if(Math.min(distanceToCenter, distanceToRadius) > MAX_DISTANCE)
			{
				draggedIndex = -1;
				centerAtBeginning = null;
				draggingStart = null;
				return;
			}
			if(distanceToCenter < distanceToRadius)
			{
				draggedIndex = 0;
				centerAtBeginning = center;
			}
			else
			{
				draggedIndex = 1;
				centerAtBeginning = null;
			}
			draggingStart = new Point(x, y);
		}
	}
	
	private void onTouchMove(int x, int y)
	{
		Point current = new Point(x, y);
		if(!polygonCreated) dragRadius(current);
		else drag(current);
	}
	
	private void onTouchStop(int x, int y)
	{
		Point current = new Point(x, y);
		if(!polygonCreated) dragRadius(current);
		else drag(current);
		polygonCreated = true;
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
		center = newCenter;
		
		createPath();
	}
	
	private void dragRadius(Point current)
	{
		radiusOEC = calcDistance(center, current.x, current.y);
		
		double deltaX = current.x - center.x;
		double deltaY = center.y - current.y;
		double ratio = deltaX / deltaY;
		double angleRad = Math.atan(ratio);
		angle = Math.round(Math.toDegrees(angleRad));
		
		createPath();
	}
	
	@Override
	public void onScreenDraw(Canvas canvas)
	{
		if(path == null) return;
		updateColor();
		canvas.drawPath(path, getPaint());
	}
	
	@Override
	public void apply(Canvas imageCanvas)
	{
		if(path == null) return;
		update();
		imageCanvas.drawPath(path, getPaint());
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
		getPaint().setStrokeWidth(lineWidth);
		createPath();
		super.update();
	}
	
	@Override
	public void cleanUp()
	{
		polygonCreated = false;
		center = null;
		radiusOEC = -1;
		angle = 0;
		path = null;
		super.cleanUp();
	}
	
	@Override
	public void enableEditMode()
	{
		polygonCreated = false;
		center = null;
		radiusOEC = -1;
		angle = 0;
		path = null;
		super.enableEditMode();
	}
	
	private void createPath()
	{
		if(center == null || radiusOEC == -1) return;
		float central = 360f / sides;
		
		path = new Path();
		for(int i = 0; i < sides; i++)
		{
			float angleRad = (float) Math.toRadians((central * i) + angle);
			float x = center.x + (float) (Math.sin(angleRad) * radiusOEC);
			float y = center.y - (float) (Math.cos(angleRad) * radiusOEC);
			if(i == 0) path.moveTo(x, y);
			else path.lineTo(x, y);
		}
		path.close();
	}
	
	public int getSides()
	{
		return sides;
	}
	
	public void setSides(int sides)
	{
		if(sides < 3) throw new IllegalArgumentException("Number of sides of polygon cannot be lower than 3.");
		this.sides = sides;
		update();
	}
	
	public boolean isFill()
	{
		return fill;
	}
	
	public void setFill(boolean fill)
	{
		this.fill = fill;
		update();
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
}