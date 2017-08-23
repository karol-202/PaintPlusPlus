package pl.karol202.paintplus.tool.marker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import pl.karol202.paintplus.color.ColorsSet;

abstract class MarkerAdapterSimple implements MarkerAdapter
{
	private ToolMarker marker;
	private ColorsSet colors;
	
	Paint pathPaint;
	Path path;
	Paint ovalPaint;
	RectF oval;
	
	boolean pathCreated;
	
	MarkerAdapterSimple(ToolMarker marker)
	{
		this.marker = marker;
		this.colors = marker.getColors();
		
		this.pathPaint = new Paint();
		this.pathPaint.setStyle(Paint.Style.STROKE);
		this.pathPaint.setStrokeCap(Paint.Cap.ROUND);
		this.pathPaint.setStrokeJoin(Paint.Join.ROUND);
		
		this.path = new Path();
		this.path.setFillType(Path.FillType.EVEN_ODD);
		
		this.ovalPaint = new Paint();
		
		this.oval = new RectF();
	}
	
	@Override
	public void onBeginDraw(float x, float y)
	{
		pathPaint.setColor(colors.getFirstColor());
		pathPaint.setAlpha((int) (marker.getOpacity() * 255));
		pathPaint.setStrokeWidth(marker.getSize());
		pathPaint.setAntiAlias(marker.isSmoothEdge());
		
		ovalPaint.setColor(colors.getFirstColor());
		ovalPaint.setAlpha((int) (marker.getSize() * 255));
		ovalPaint.setAntiAlias(marker.isSmoothEdge());
		
		path.reset();
		path.moveTo(x, y);
		
		pathCreated = false;
	}
	
	@Override
	public void onDraw(float x, float y)
	{
		pathCreated = true;
	}
	
	@Override
	public void onEndDraw(float x, float y)
	{
		if(pathCreated) marker.getCanvas().drawPath(path, pathPaint);
		else
		{
			oval.left = x - marker.getSize() / 2;
			oval.top = y - marker.getSize() / 2;
			oval.right = x + marker.getSize() / 2;
			oval.bottom = y + marker.getSize() / 2;
			marker.getCanvas().drawOval(oval, ovalPaint);
		}
		
		path.reset();
	}
	
	@Override
	public void onScreenDraw(Canvas canvas)
	{
		canvas.drawPath(path, pathPaint);
	}
}