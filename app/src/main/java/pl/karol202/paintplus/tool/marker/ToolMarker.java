package pl.karol202.paintplus.tool.marker;

import android.graphics.*;
import android.graphics.Region.Op;
import android.view.MotionEvent;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolProperties;
import pl.karol202.paintplus.tool.selection.Selection;

public class ToolMarker extends Tool
{
	private float size;
	private float opacity;
	private boolean smooth;
	
	private Canvas canvas;
	private ColorsSet colors;
	private Paint pathPaint;
	private Path path;
	private float lastX;
	private float lastY;

	public ToolMarker(Image image)
	{
		super(image);
		this.size = 25;
		this.opacity = 1;
		this.smooth = true;
		
		this.pathPaint = new Paint();
		this.pathPaint.setStyle(Paint.Style.STROKE);
		this.pathPaint.setStrokeCap(Paint.Cap.ROUND);
		this.pathPaint.setStrokeJoin(Paint.Join.ROUND);

		this.path = new Path();
		this.path.setFillType(Path.FillType.EVEN_ODD);
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
	public Class<? extends ToolProperties> getPropertiesFragmentClass()
	{
		return MarkerProperties.class;
	}
	
	@Override
	public boolean onTouch(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN) onTouchStart(event.getX(), event.getY());
		else if(event.getAction() == MotionEvent.ACTION_MOVE) onTouchMove(event.getX(), event.getY());
		else if(event.getAction() == MotionEvent.ACTION_UP) onTouchStop(event.getX(), event.getY());
		return true;
	}
	
	private void onTouchStart(float x, float y)
	{
		canvas = image.getEditCanvas();
		colors = image.getColorsSet();
		pathPaint.setColor(colors.getFirstColor());
		pathPaint.setAlpha((int) (opacity * 255));
		pathPaint.setStrokeWidth(size);
		pathPaint.setAntiAlias(smooth);
		updateClipping(canvas);
		
		path.reset();
		path.moveTo(x, y);
		
		lastX = x;
		lastY = y;
	}
	
	private void onTouchMove(float x, float y)
	{
		if(lastX != -1 && lastY != -1) path.quadTo(lastX, lastY, x, y);
		
		lastX = x;
		lastY = y;
	}
	
	private void onTouchStop(float x, float y)
	{
		if(lastX != -1 && lastY != -1) path.lineTo(x, y);
		
		canvas.drawPath(path, pathPaint);
		
		path.reset();
		lastX = -1;
		lastY = -1;
	}

	@Override
	public void onScreenDraw(Canvas canvas)
	{
		canvas.scale(image.getZoom(), image.getZoom());
		canvas.translate(-image.getViewX(), -image.getViewY());
		
		updateClipping(canvas);
		canvas.drawPath(path, pathPaint);
	}
	
	private void updateClipping(Canvas canvas)
	{
		Selection selection = image.getSelection();
		if(selection.isEmpty()) canvas.clipRect(0, 0, image.getWidth(), image.getHeight(), Op.REPLACE);
		else canvas.clipPath(selection.getPath(), Op.REPLACE);
	}
	
	public float getSize()
	{
		return size;
	}

	public void setSize(float size)
	{
		this.size = size;
	}
	
	public float getOpacity()
	{
		return opacity;
	}
	
	public void setOpacity(float opacity)
	{
		this.opacity = opacity;
	}
	
	public boolean isSmooth()
	{
		return smooth;
	}
	
	public void setSmooth(boolean smooth)
	{
		this.smooth = smooth;
	}
}