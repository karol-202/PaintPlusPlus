package pl.karol202.paintplus.tool.rubber;

import android.graphics.*;
import android.view.MotionEvent;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolProperties;
import pl.karol202.paintplus.tool.selection.Selection;

public class ToolRubber extends Tool
{
	private float size;
	private float strength;
	private boolean smooth;
	
	private Canvas canvas;
	private Path selectionPath;
	private Layer layer;
	
	private Paint pathPaint;
	private Path path;
	private Paint ovalPaint;
	private float lastX;
	private float lastY;
	private boolean pathCreated;
	private boolean editStarted;
	private boolean oldVisibility;
	
	public ToolRubber(Image image)
	{
		super(image);
		this.size = 25;
		this.strength = 1;
		this.smooth = true;
		
		this.layer = image.getSelectedLayer();
		updateSelectionPath();
		
		this.pathPaint = new Paint();
		this.pathPaint.setStyle(Paint.Style.STROKE);
		this.pathPaint.setStrokeCap(Paint.Cap.ROUND);
		this.pathPaint.setStrokeJoin(Paint.Join.ROUND);
		
		this.path = new Path();
		this.path.setFillType(Path.FillType.EVEN_ODD);
		
		this.ovalPaint = new Paint();
	}
	
	@Override
	public int getName()
	{
		return R.string.tool_rubber;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_tool_rubber_black_24dp;
	}
	
	@Override
	public Class<? extends ToolProperties> getPropertiesFragmentClass()
	{
		return RubberProperties.class;
	}
	
	@Override
	public boolean isLayerSpace()
	{
		return true;
	}
	
	@Override
	public boolean isImageLimited()
	{
		return true;
	}
	
	@Override
	public boolean onTouch(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN) return onTouchStart(event.getX(), event.getY());
		else if(event.getAction() == MotionEvent.ACTION_MOVE) onTouchMove(event.getX(), event.getY());
		else if(event.getAction() == MotionEvent.ACTION_UP) onTouchStop(event.getX(), event.getY());
		return true;
	}
	
	private boolean onTouchStart(float x, float y)
	{
		canvas = image.getSelectedCanvas();
		if(canvas == null) return false;
		layer = image.getSelectedLayer();
		
		updateSelectionPath();
		updateClipping();
		
		pathPaint.setColor(Color.TRANSPARENT);
		pathPaint.setAlpha((int) (strength * 255));
		pathPaint.setStrokeWidth(size);
		pathPaint.setAntiAlias(smooth);
		pathPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		
		ovalPaint.setColor(Color.TRANSPARENT);
		ovalPaint.setAlpha((int) (strength * 255));
		ovalPaint.setAntiAlias(smooth);
		ovalPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		
		path.reset();
		path.moveTo(x, y);
		
		lastX = x;
		lastY = y;
		pathCreated = false;
		editStarted = true;
		oldVisibility = layer.isVisible();
		layer.setVisibility(false);
		return true;
	}
	
	private void updateSelectionPath()
	{
		selectionPath = new Path(image.getSelection().getPath());
		selectionPath.offset(-layer.getX(), -layer.getY());
	}
	
	private void updateClipping()
	{
		Selection selection = image.getSelection();
		canvas.clipRect(0, 0, layer.getWidth(), layer.getHeight(), Region.Op.REPLACE);
		if(!selection.isEmpty()) canvas.clipPath(selectionPath, Region.Op.INTERSECT);
	}
	
	private void onTouchMove(float x, float y)
	{
		if(lastX != -1 && lastY != -1) path.quadTo(lastX, lastY, x, y);
		
		lastX = x;
		lastY = y;
		pathCreated = true;
	}
	
	private void onTouchStop(float x, float y)
	{
		if(lastX != -1 && lastY != -1) path.lineTo(x, y);
		
		if(pathCreated) canvas.drawPath(path, pathPaint);
		else
		{
			RectF oval = new RectF();
			oval.left = x - size / 2;
			oval.top = y - size / 2;
			oval.right = x + size / 2;
			oval.bottom = y + size / 2;
			canvas.drawOval(oval, ovalPaint);
		}
		
		path.reset();
		lastX = -1;
		lastY = -1;
		pathCreated = false;
		editStarted = false;
		layer.setVisibility(oldVisibility);
	}
	
	@Override
	public boolean doesScreenDraw(boolean layerVisible)
	{
		return editStarted && oldVisibility;
	}
	
	@Override
	public boolean isDrawingOnTop()
	{
		return false;
	}
	
	@Override
	public void onScreenDraw(Canvas canvas)
	{
		canvas.scale(image.getZoom(), image.getZoom());
		canvas.translate(-image.getViewX() + layer.getX(),
				-image.getViewY() + layer.getY());
		
		canvas.drawBitmap(layer.getBitmap(), 0, 0, null);
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
	
	public float getStrength()
	{
		return strength;
	}
	
	public void setStrength(float strength)
	{
		this.strength = strength;
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