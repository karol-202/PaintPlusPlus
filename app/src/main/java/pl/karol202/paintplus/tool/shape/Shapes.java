package pl.karol202.paintplus.tool.shape;

import pl.karol202.paintplus.tool.shape.line.ShapeLine;

import java.util.ArrayList;

public class Shapes
{
	private ArrayList<Shape> shapes;
	
	public Shapes()
	{
		shapes = new ArrayList<>();
		shapes.add(new ShapeLine());
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
}