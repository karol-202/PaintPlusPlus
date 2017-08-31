package pl.karol202.paintplus.tool.rubber;

import android.graphics.*;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.StandardTool;
import pl.karol202.paintplus.tool.ToolCoordinateSpace;
import pl.karol202.paintplus.tool.ToolProperties;

public class ToolRubber extends StandardTool
{
	private float size;
	private float strength;
	private boolean smooth;
	
	private Canvas canvas;
	
	private Layer layer;
	
	private Paint pathPaint;
	private Path path;
	private Paint ovalPaint;
	private float lastX;
	private float lastY;
	private boolean pathCreated;
	private boolean editStarted;
	
	public ToolRubber(Image image)
	{
		super(image);
		this.size = 25;
		this.strength = 1;
		this.smooth = true;
		
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
	public ToolCoordinateSpace getCoordinateSpace()
	{
		return ToolCoordinateSpace.LAYER_SPACE;
	}
	
	@Override
	public boolean isUsingSnapping()
	{
		return true;
	}
	
	@Override
	public boolean onTouchStart(float x, float y)
	{
		canvas = image.getSelectedCanvas();
		if(canvas == null) return false;
		layer = image.getSelectedLayer();
		
		updateSelectionPath();
		doLayerAndSelectionClipping(canvas);
		
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
		layer.setTemporaryHidden(true);
		return true;
	}
	
	@Override
	public boolean onTouchMove(float x, float y)
	{
		if(lastX != -1 && lastY != -1) path.quadTo(lastX, lastY, x, y);
		
		lastX = x;
		lastY = y;
		pathCreated = true;
		return true;
	}
	
	@Override
	public boolean onTouchStop(float x, float y)
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
		layer.setTemporaryHidden(false);
		return true;
	}
	
	@Override
	public boolean doesOnLayerDraw(boolean layerVisible)
	{
		return editStarted && layerVisible;
	}
	
	@Override
	public boolean doesOnTopDraw()
	{
		return false;
	}
	
	@Override
	public ToolCoordinateSpace getOnLayerDrawingCoordinateSpace()
	{
		return ToolCoordinateSpace.LAYER_SPACE;
	}
	
	@Override
	public ToolCoordinateSpace getOnTopDrawingCoordinateSpace()
	{
		return null;
	}
	
	@Override
	public void onLayerDraw(Canvas canvas)
	{
		canvas.drawBitmap(layer.getBitmap(), 0, 0, null);
		doLayerAndSelectionClipping(canvas);
		canvas.drawPath(path, pathPaint);
	}
	
	@Override
	public void onTopDraw(Canvas canvas) { }
	
	float getSize()
	{
		return size;
	}
	
	void setSize(float size)
	{
		this.size = size;
	}
	
	float getStrength()
	{
		return strength;
	}
	
	void setStrength(float strength)
	{
		this.strength = strength;
	}
	
	boolean isSmooth()
	{
		return smooth;
	}
	
	void setSmooth(boolean smooth)
	{
		this.smooth = smooth;
	}
}