package pl.karol202.paintplus.tool.shape;

import android.graphics.Canvas;
import android.view.MotionEvent;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.properties.ShapeToolProperties;
import pl.karol202.paintplus.tool.properties.ToolProperties;

public class ToolShape extends Tool
{
	private Shape shape;
	
	private Shapes shapes;
	
	public ToolShape(Image image)
	{
		super(image);
		shapes = new Shapes();
		
		shape = shapes.getShape(0);
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
		return false;
	}
	
	@Override
	public void onScreenDraw(Canvas canvas)
	{
		
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
}