package pl.karol202.paintplus.tool.shape;

import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.Image.OnImageChangeListener;
import pl.karol202.paintplus.tool.shape.circle.ShapeCircle;
import pl.karol202.paintplus.tool.shape.line.ShapeLine;
import pl.karol202.paintplus.tool.shape.polygon.ShapePolygon;

import java.util.ArrayList;

class Shapes
{
	private ArrayList<Shape> shapes;
	
	Shapes(Image image, OnImageChangeListener imageChangeListener, OnShapeEditListener shapeEditListener)
	{
		shapes = new ArrayList<>();
		shapes.add(new ShapeLine(image, imageChangeListener, shapeEditListener));
		shapes.add(new ShapeCircle(image, imageChangeListener, shapeEditListener));
		shapes.add(new ShapePolygon(image, imageChangeListener, shapeEditListener));
	}
	
	Shape getShape(int id)
	{
		return shapes.get(id);
	}
	
	int getShapeId(Shape shape)
	{
		for(int i = 0; i < shapes.size(); i++)
		{
			Shape next = shapes.get(i);
			if(next == shape) return i;
		}
		return -1;
	}
	
	ArrayList<Shape> getShapes()
	{
		return shapes;
	}
	
	void setSmooth(boolean smooth)
	{
		for(Shape shape : shapes) shape.setSmooth(smooth);
	}
	
	void setOpacity(float opacity)
	{
		for(Shape shape : shapes) shape.setOpacity(opacity);
	}
}