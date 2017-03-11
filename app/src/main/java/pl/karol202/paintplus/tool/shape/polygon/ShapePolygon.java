package pl.karol202.paintplus.tool.shape.polygon;

import android.graphics.*;
import android.view.MotionEvent;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.helpers.HelpersManager;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.shape.OnShapeEditListener;
import pl.karol202.paintplus.tool.shape.Shape;
import pl.karol202.paintplus.tool.shape.ShapeProperties;
import pl.karol202.paintplus.util.Utils;

public class ShapePolygon extends Shape
{
	private final int MAX_DISTANCE = 50;
	
	private int sides;
	private boolean fill;
	private int lineWidth;
	
	private HelpersManager helpersManager;
	private boolean polygonCreated;
	private Point center;
	private float radiusOCC; //Radius of circumscribed circle
	private float angle;
	private Path path;
	
	private Point draggingStart;
	private int draggedIndex;
	private Point centerAtBeginning;
	private float radiusOCCAtBeginning;
	private float angleAtBeginning;
	
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
		Point touchPoint = new Point(x, y);
		if(!isInEditMode()) enableEditMode();
		if(!polygonCreated) setCenterPoint(touchPoint);
		else
		{
			float side = (float) (2 * radiusOCC * Math.sin(Math.PI / sides));
			float radiusOIC = (float) (side / (2 * Math.tan(Math.PI / sides)));
			
			float centralAngle = 360f / sides;
			float halfOfCentral = centralAngle / 2;
			float angle = (float) getAngle(touchPoint) - this.angle;
			if(angle < 0) angle += 360;
			float angleMod = angle % centralAngle;
			float a = Math.abs(angleMod - halfOfCentral);
			float centerToSide = map(0, halfOfCentral, radiusOIC, radiusOCC, a);
			
			float distanceToCenter = calcDistance(center, x, y);
			float distanceToSide = Math.abs(distanceToCenter - centerToSide);
			
			draggingStart = touchPoint;
			centerAtBeginning = center;
			radiusOCCAtBeginning = radiusOCC;
			angleAtBeginning = this.angle;
			if(Math.min(distanceToCenter, distanceToSide) > MAX_DISTANCE) draggedIndex = -1;
			else if(distanceToCenter < distanceToSide) draggedIndex = 0;
			else draggedIndex = 1;
		}
	}
	
	private float map(float srcMin, float srcMax, float dstMin, float dstMax, float value)
	{
		return (value - srcMin) / (srcMax - srcMin) * (dstMax - dstMin) + dstMin;
	}
	
	private void onTouchMove(int x, int y)
	{
		Point current = new Point(x, y);
		if(!polygonCreated) dragRadius(current);
		else drag(current);
	}
	
	private void onTouchStop(int x, int y)
	{
		onTouchMove(x, y);
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
		setCenterPoint(newCenter);
		
		createPath();
	}
	
	private void dragRadius(Point current)
	{
		if(draggingStart != null)
		{
			double theta = Math.toRadians(Utils.getAngle(center, current));
			float rCurrent = calcDistance(center, current.x, current.y);
			float rDraggingStart = calcDistance(center, draggingStart.x, draggingStart.y);
			float rBeginning = radiusOCCAtBeginning;
			
			float rDelta = rCurrent - rDraggingStart;
			float rResult = rBeginning + rDelta;
			
			float x = (float) (rResult * Math.cos(theta)) + center.x;
			float y = (float) (rResult * Math.sin(theta)) + center.y;
			PointF result = new PointF(x, y);
			helpersManager.snapPoint(result);
			
			radiusOCC = calcDistance(center, (int) result.x, (int) result.y);
			
			angle = (float) getAngle(new Point((int) result.x, (int) result.y));
		}
		else
		{
			PointF snapped = new PointF(current);
			helpersManager.snapPoint(snapped);
			radiusOCC = calcDistance(center, (int) snapped.x, (int) snapped.y);
			
			angle = (float) getAngle(new Point((int) snapped.x, (int) snapped.y));
		}
		
		createPath();
	}
	
	private double getAngle(Point point)
	{
		double deltaX = point.x - center.x;
		double deltaY = center.y - point.y;
		double ratio = deltaX / deltaY;
		double angleRad = Math.atan(ratio);
		double angleDeg = Math.toDegrees(angleRad);
		if(deltaY < 0) angleDeg += 180;
		if(angleDeg < 0) angleDeg += 360;
		return angleDeg;
	}
	
	private void setCenterPoint(Point point)
	{
		PointF snapped = new PointF(point);
		helpersManager.snapPoint(snapped);
		center = new Point((int) snapped.x, (int) snapped.y);
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
		radiusOCC = -1;
		angle = 0;
		path = null;
		super.cleanUp();
	}
	
	@Override
	public void enableEditMode()
	{
		polygonCreated = false;
		center = null;
		radiusOCC = -1;
		angle = 0;
		path = null;
		super.enableEditMode();
	}
	
	private void createPath()
	{
		if(center == null || radiusOCC == -1) return;
		float central = 360f / sides;
		
		path = new Path();
		for(int i = 0; i < sides; i++)
		{
			float angleRad = (float) Math.toRadians((central * i) + angle);
			float x = center.x + (float) (Math.sin(angleRad) * radiusOCC);
			float y = center.y - (float) (Math.cos(angleRad) * radiusOCC);
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