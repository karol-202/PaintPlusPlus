package pl.karol202.paintplus.tool.shape;

import android.graphics.*;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.OnToolChangeListener;
import pl.karol202.paintplus.tool.StandardTool;
import pl.karol202.paintplus.tool.ToolCoordinateSpace;
import pl.karol202.paintplus.tool.ToolProperties;

public class ToolShape extends StandardTool implements OnShapeEditListener, OnToolChangeListener
{
	private Shape shape;
	
	private Canvas canvas;
	
	private Shapes shapes;
	private OnShapeEditListener shapeEditListener;
	private Paint maskPaint;
	
	public ToolShape(Image image)
	{
		super(image);
		
		this.shapes = new Shapes(image, this);
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
	public ToolCoordinateSpace getCoordinateSpace()
	{
		return ToolCoordinateSpace.LAYER_SPACE;
	}
	
	@Override
	public boolean isUsingSnapping()
	{
		return false;
	}
	
	@Override
	public boolean onTouchStart(float x, float y)
	{
		canvas = image.getSelectedCanvas();
		if(canvas == null) return false;
		layer = image.getSelectedLayer();
		
		updateSelectionPath();
		resetClipping(canvas);
		doLayerAndSelectionClipping(canvas);
		
		shape.onTouchStart((int) x, (int) y);
		return true;
	}
	
	@Override
	public boolean onTouchMove(float x, float y)
	{
		shape.onTouchMove((int) x, (int) y);
		return true;
	}
	
	@Override
	public boolean onTouchStop(float x, float y)
	{
		shape.onTouchStop((int) x, (int) y);
		return true;
	}
	
	@Override
	public boolean doesOnLayerDraw(boolean layerVisible)
	{
		return layerVisible;
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
		resetClipping(canvas);
		shape.onScreenDraw(canvas, true);
		
		doLayerAndSelectionClipping(canvas);
		doImageClipping(canvas);
		shape.onScreenDraw(canvas, false);
	}
	
	@Override
	public void onTopDraw(Canvas canvas) { }
	
	public void apply()
	{
		shape.apply(canvas);
	}
	
	public void cancel()
	{
		shape.cancel();
	}
	
	@Override
	public void onStartShapeEditing()
	{
		if(shapeEditListener != null) shapeEditListener.onStartShapeEditing();
	}
	
	@Override
	public void onToolSelected() { }
	
	@Override
	public void onOtherToolSelected()
	{
		cancel();
	}
	
	Shapes getShapesClass()
	{
		return shapes;
	}
	
	Shape getShape()
	{
		return shape;
	}
	
	void setShape(Shape shape)
	{
		if(this.shape == shape) this.shape.cancel();
		this.shape = shape;
		image.updateImage();
	}
	
	boolean isSmoothed()
	{
		return shape.isSmooth();
	}
	
	void setSmoothed(boolean smooth)
	{
		shapes.setSmooth(smooth);
	}
	
	float getOpacity()
	{
		return shape.getOpacity();
	}
	
	void setOpacity(float opacity)
	{
		shapes.setOpacity(opacity);
	}
	
	boolean isInEditMode()
	{
		return shape.isInEditMode();
	}
	
	void setShapeEditListener(OnShapeEditListener listener)
	{
		this.shapeEditListener = listener;
	}
}