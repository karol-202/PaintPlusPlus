package pl.karol202.paintplus.tool.shape;

import pl.karol202.paintplus.tool.shape.properties.ShapeProperties;

public interface Shape
{
	int getName();
	
	int getIcon();
	
	Class<? extends ShapeProperties> getPropertiesClass();
}