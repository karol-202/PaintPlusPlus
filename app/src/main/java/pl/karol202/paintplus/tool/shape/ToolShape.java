package pl.karol202.paintplus.tool.shape;

import android.graphics.Canvas;
import android.view.MotionEvent;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.Image.OnImageChangeListener;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.properties.ShapeToolProperties;
import pl.karol202.paintplus.tool.properties.ToolProperties;

public class ToolShape extends Tool implements OnShapeEditListener
{
	private Shape shape;
	
	private Shapes shapes;
	private ColorsSet colors;
	private OnShapeEditListener listener;
	
	public ToolShape(Image image, OnImageChangeListener listener)
	{
		super(image);
		shapes = new Shapes(listener, this);
		colors = image.getColorsSet();
		
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
		shape.onScreenDraw(canvas, colors);
	}
	
	@Override
	public void onStartShapeEditing()
	{
		listener.onStartShapeEditing();
	}
	
	public void apply()
	{
		shape.apply(image.getEditCanvas(), colors);
	}
	
	public void cancel()
	{
		shape.cancel();
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
		this.shape = shape;
	}
	
	public boolean isInEditMode()
	{
		return shape.isInEditMode();
	}
	
	public void setShapeEditListener(OnShapeEditListener listener)
	{
		this.listener = listener;
	}
}