package pl.karol202.paintplus.tool.shape;

import android.graphics.Canvas;
import android.view.MotionEvent;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.Image.OnImageChangeListener;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.tool.OnToolChangeListener;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.properties.ShapeToolProperties;
import pl.karol202.paintplus.tool.properties.ToolProperties;

public class ToolShape extends Tool implements OnShapeEditListener, OnToolChangeListener
{
	private Shape shape;
	
	private ColorsSet colors;
	private Shapes shapes;
	private OnImageChangeListener imageChangeListener;
	private OnShapeEditListener shapeEditListener;
	
	public ToolShape(Image image, OnImageChangeListener imageChangeListener)
	{
		super(image);
		this.colors = image.getColorsSet();
		this.shapes = new Shapes(colors, imageChangeListener, this);
		this.imageChangeListener = imageChangeListener;
		
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
	public boolean onTouch(MotionEvent event)
	{
		return shape.onTouch(event);
	}
	
	@Override
	public void onScreenDraw(Canvas canvas)
	{
		int clipLeft = Math.max(0, (int) -(image.getViewX() * image.getZoom()));
		int clipTop = Math.max(0, (int) -(image.getViewY() * image.getZoom()));
		int clipRight = Math.min(canvas.getWidth(), (int) ((image.getWidth() - image.getViewX()) * image.getZoom()));
		int clipBottom = Math.min(canvas.getHeight(), (int) ((image.getHeight() - image.getViewY()) * image.getZoom()));
		canvas.clipRect(clipLeft, clipTop, clipRight, clipBottom);
		
		canvas.scale(image.getZoom(), image.getZoom());
		canvas.translate(-image.getViewX(), -image.getViewY());
		shape.onScreenDraw(canvas);
	}
	
	@Override
	public void onStartShapeEditing()
	{
		if(shapeEditListener != null) shapeEditListener.onStartShapeEditing();
	}
	
	public void apply()
	{
		shape.apply(image.getEditCanvas());
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
	
	public boolean isInEditMode()
	{
		return shape.isInEditMode();
	}
	
	public void setShapeEditListener(OnShapeEditListener listener)
	{
		this.shapeEditListener = listener;
	}
	
	public boolean isSmoothed()
	{
		return shape.isSmooth();
	}
	
	public void setSmoothed(boolean smooth)
	{
		shapes.setSmooth(smooth);
	}
}