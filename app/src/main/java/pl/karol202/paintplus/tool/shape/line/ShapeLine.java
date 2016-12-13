package pl.karol202.paintplus.tool.shape.line;

import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.shape.Shape;
import pl.karol202.paintplus.tool.shape.properties.LineProperties;
import pl.karol202.paintplus.tool.shape.properties.ShapeProperties;

public class ShapeLine implements Shape
{
	private int lineWidth;
	private Cap lineCap;
	
	public ShapeLine()
	{
		this.lineWidth = 10;
		this.lineCap = Cap.ROUND;
	}
	
	@Override
	public int getName()
	{
		return R.string.shape_line;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_shape_line_black_24dp;
	}
	
	@Override
	public Class<? extends ShapeProperties> getPropertiesClass()
	{
		return LineProperties.class;
	}
	
	public int getLineWidth()
	{
		return lineWidth;
	}
	
	public void setLineWidth(int lineWidth)
	{
		this.lineWidth = lineWidth;
	}
	
	public Cap getLineCap()
	{
		return lineCap;
	}
	
	public void setLineCap(Cap lineCap)
	{
		this.lineCap = lineCap;
	}
}