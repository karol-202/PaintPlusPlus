package pl.karol202.paintplus.tool.shape;

import pl.karol202.paintplus.Image.OnImageChangeListener;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.tool.shape.circle.ShapeCircle;
import pl.karol202.paintplus.tool.shape.line.ShapeLine;
import pl.karol202.paintplus.tool.shape.polygon.ShapePolygon;

import java.util.ArrayList;

public class Shapes
{
	private ArrayList<Shape> shapes;
	
	public Shapes(ColorsSet colors, OnImageChangeListener imageChangeListener, OnShapeEditListener shapeEditListener)
	{
		shapes = new ArrayList<>();
		shapes.add(new ShapeLine(colors, imageChangeListener, shapeEditListener));
		shapes.add(new ShapeCircle(colors, imageChangeListener, shapeEditListener));
		shapes.add(new ShapePolygon(colors, imageChangeListener, shapeEditListener));
	}
	
	public Shape getShape(int id)
	{
		return shapes.get(id);
	}
	
	public int getShapeId(Shape shape)
	{
		for(int i = 0; i < shapes.size(); i++)
		{
			Shape next = shapes.get(i);
			if(next == shape) return i;
		}
		return -1;
	}
	
	public ArrayList<Shape> getShapes()
	{
		return shapes;
	}
	
	public void setSmooth(boolean smooth)
	{
		for(Shape shape : shapes) shape.setSmooth(smooth);
	}
	
	public void setOpacity(float opacity)
	{
		for(Shape shape : shapes) shape.setOpacity(opacity);
	}
}