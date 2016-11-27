package pl.karol202.paintplus.tool;

import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import pl.karol202.paintplus.ColorsSet;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.properties.MarkerProperties;

public class ToolMarker extends Tool
{
	private float size;
	
	private Paint pathPaint;
	private Path path;
	private Paint ovalPaint;
	private RectF oval;
	private float lastX;
	private float lastY;

	public ToolMarker(Image image)
	{
		super(image);
		this.size = 25;
		
		this.pathPaint = new Paint();
		this.pathPaint.setAntiAlias(true);
		this.pathPaint.setStyle(Paint.Style.STROKE);
		this.pathPaint.setStrokeCap(Paint.Cap.ROUND);
		this.pathPaint.setStrokeJoin(Paint.Join.ROUND);

		this.path = new Path();
		this.path.setFillType(Path.FillType.EVEN_ODD);
		
		this.ovalPaint = new Paint();
		this.ovalPaint.setAntiAlias(true);
		
		this.oval = new RectF();
	}
	
	@Override
	public int getName()
	{
		return R.string.tool_marker;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_tool_marker_black_24dp;
	}
	
	@Override
	public Class<? extends Fragment> getPropertiesFragmentClass()
	{
		return MarkerProperties.class;
	}
	
	@Override
	public boolean onlyViewport()
	{
		return false;
	}
	
	@Override
	public boolean onTouch(Canvas edit, ColorsSet colors, MotionEvent event)
	{
		pathPaint.setColor(colors.getFirstColor());
		pathPaint.setStrokeWidth(size);
		ovalPaint.setColor(colors.getFirstColor());

		if(event.getAction() == MotionEvent.ACTION_DOWN) onTouchStart(edit, event.getX(), event.getY());
		else if(event.getAction() == MotionEvent.ACTION_MOVE) onTouchMove(edit, event.getX(), event.getY());
		else if(event.getAction() == MotionEvent.ACTION_UP) onTouchStop(edit, event.getX(), event.getY());
		return true;
	}
	
	private void onTouchStart(Canvas canvas, float x, float y)
	{
		path.reset();
		path.moveTo(x, y);
		
		oval.left = x - size / 2;
		oval.top = y - size / 2;
		oval.right = x + size / 2;
		oval.bottom = y + size / 2;
		canvas.drawOval(oval, ovalPaint);
		
		lastX = x;
		lastY = y;
	}
	
	private void onTouchMove(Canvas canvas, float x, float y)
	{
		if(lastX != -1 && lastY != -1) path.quadTo(lastX, lastY, x, y);
		
		lastX = x;
		lastY = y;
	}
	
	private void onTouchStop(Canvas canvas, float x, float y)
	{
		if(lastX != -1 && lastY != -1) path.lineTo(x, y);
		
		canvas.drawPath(path, pathPaint);
		
		path.reset();
		lastX = -1;
		lastY = -1;
	}
	
	@Override
	public void onTouchOutsideViewport(Canvas edit, ColorsSet colors, MotionEvent event) { }

	@Override
	public void onDraw(Canvas canvas)
	{
		canvas.clipRect(0, 0, image.getWidth(), image.getHeight());
		canvas.drawPath(path, pathPaint);
	}
	
	public float getSize()
	{
		return size;
	}

	public void setSize(float size)
	{
		this.size = size;
	}
}