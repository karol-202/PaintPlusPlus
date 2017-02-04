package pl.karol202.paintplus.tool.marker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.view.MotionEvent;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
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
	private Path selectionPath;
	private Layer layer;
	
	private Paint pathPaint;
	private Path path;
	private Paint ovalPaint;
	private RectF oval;
	private float lastX;
	private float lastY;
	private boolean pathCreated;

	public ToolMarker(Image image)
	{
		super(image);
		this.size = 25;
		this.opacity = 1;
		this.smooth = true;
		
		this.colors = image.getColorsSet();
		this.layer = image.getSelectedLayer();
		updateSelectionPath();
		
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
		updateClipping(canvas);
		
		pathPaint.setColor(colors.getFirstColor());
		pathPaint.setAlpha((int) (opacity * 255));
		pathPaint.setStrokeWidth(size);
		pathPaint.setAntiAlias(smooth);
		
		ovalPaint.setColor(colors.getFirstColor());
		ovalPaint.setAlpha((int) (opacity * 255));
		ovalPaint.setAntiAlias(smooth);
		
		path.reset();
		path.moveTo(x, y);
		
		lastX = x;
		lastY = y;
		pathCreated = false;
		return true;
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
			oval.left = x - size / 2;
			oval.top = y - size / 2;
			oval.right = x + size / 2;
			oval.bottom = y + size / 2;
			canvas.drawOval(oval, ovalPaint);
		}
		
		path.reset();
		lastX = -1;
		lastY = -1;
	}
	
	@Override
	public boolean doesScreenDraw(Layer layer)
	{
		return layer.isVisible();
	}
	
	@Override
	public void onScreenDraw(Canvas canvas)
	{
		layer = image.getSelectedLayer();
		
		canvas.scale(image.getZoom(), image.getZoom());
		canvas.translate(-image.getViewX() + layer.getX(),
						 -image.getViewY() + layer.getY());
		
		updateClipping(canvas);
		canvas.drawPath(path, pathPaint);
	}
	
	private void updateSelectionPath()
	{
		selectionPath = new Path(image.getSelection().getPath());
		selectionPath.offset(-layer.getX(), -layer.getY());
	}
	
	private void updateClipping(Canvas canvas)
	{
		Selection selection = image.getSelection();
		
		canvas.clipRect(0, 0, layer.getWidth(), layer.getHeight(), Op.REPLACE);
		if(!selection.isEmpty()) canvas.clipPath(selectionPath, Op.INTERSECT);
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