package pl.karol202.paintplus.tool.shape;

import android.graphics.*;
import android.graphics.Region.Op;
import android.view.MotionEvent;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.Image.OnImageChangeListener;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.OnToolChangeListener;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolProperties;
import pl.karol202.paintplus.tool.selection.Selection;

public class ToolShape extends Tool implements OnShapeEditListener, OnToolChangeListener
{
	private Shape shape;
	
	private Canvas canvas;
	private ColorsSet colors;
	private Selection selection;
	private Path selectionPath;
	private Layer layer;
	
	private Shapes shapes;
	private OnImageChangeListener imageChangeListener;
	private OnShapeEditListener shapeEditListener;
	private Paint maskPaint;
	
	public ToolShape(Image image, OnImageChangeListener imageChangeListener)
	{
		super(image);
		this.colors = image.getColorsSet();
		this.selection = image.getSelection();
		this.layer = image.getSelectedLayer();
		updateSelectionPath();
		
		this.shapes = new Shapes(colors, imageChangeListener, this);
		this.imageChangeListener = imageChangeListener;
		this.maskPaint = new Paint();
		this.maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
		this.maskPaint.setColor(Color.argb(160, 208, 208, 208));
		
		setShape(shapes.getShape(0));
	}
	
	@Override
	public int getName()
	{
		return R.string.tool_shape;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_tool_shape_black_24dp;
	}
	
	@Override
	public Class<? extends ToolProperties> getPropertiesFragmentClass()
	{
		return ShapeToolProperties.class;
	}
	
	@Override
	public boolean isLayerSpace()
	{
		return true;
	}
	
	@Override
	public boolean isImageLimited()
	{
		return false;
	}
	
	@Override
	public boolean onTouch(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			canvas = image.getSelectedCanvas();
			if(canvas == null) return false;
			selection = image.getSelection();
			layer = image.getSelectedLayer();
			
			updateSelectionPath();
			updateClipping(canvas);
		}
		return shape.onTouch(event);
	}
	
	@Override
	public boolean doesScreenDraw(boolean layerVisible)
	{
		return layerVisible;
	}
	
	@Override
	public boolean isDrawingOnTop()
	{
		return false;
	}
	
	@Override
	public void onScreenDraw(Canvas canvas)
	{
		layer = image.getSelectedLayer();
		
		canvas.scale(image.getZoom(), image.getZoom());
		canvas.translate(-image.getViewX() + layer.getX(),
						 -image.getViewY() + layer.getY());
		shape.onScreenDraw(canvas);
		
		updateClipping(canvas);
		canvas.translate(image.getViewX() - layer.getX(), image.getViewY() - layer.getY());
		canvas.scale(1 / image.getZoom(), 1 / image.getZoom());
		canvas.clipRect(0, 0, canvas.getWidth(), canvas.getHeight(), Op.XOR);
		
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), maskPaint);
	}
	
	@Override
	public void onStartShapeEditing()
	{
		if(shapeEditListener != null) shapeEditListener.onStartShapeEditing();
	}
	
	public void apply()
	{
		shape.apply(canvas);
	}
	
	private void updateSelectionPath()
	{
		selectionPath = new Path(selection.getPath());
		selectionPath.offset(-layer.getX(), -layer.getY());
	}
	
	private void updateClipping(Canvas canvas)
	{
		canvas.clipRect(0, 0, layer.getWidth(), layer.getHeight(), Op.REPLACE);
		if(!selection.isEmpty()) canvas.clipPath(selectionPath, Op.INTERSECT);
	}
	
	public void cancel()
	{
		shape.cancel();
	}
	
	@Override
	public void onToolSelected() { }
	
	@Override
	public void onOtherToolSelected()
	{
		cancel();
	}
	
	public Shapes getShapesClass()
	{
		return shapes;
	}
	
	public Shape getShape()
	{
		return shape;
	}
	
	public void setShape(Shape shape)
	{
		if(this.shape == shape) this.shape.cancel();
		this.shape = shape;
		imageChangeListener.onImageChanged();
	}
	
	public boolean isSmoothed()
	{
		return shape.isSmooth();
	}
	
	public void setSmoothed(boolean smooth)
	{
		shapes.setSmooth(smooth);
	}
	
	public float getOpacity()
	{
		return shape.getOpacity();
	}
	
	public void setOpacity(float opacity)
	{
		shapes.setOpacity(opacity);
	}
	
	public boolean isInEditMode()
	{
		return shape.isInEditMode();
	}
	
	public void setShapeEditListener(OnShapeEditListener listener)
	{
		this.shapeEditListener = listener;
	}
}