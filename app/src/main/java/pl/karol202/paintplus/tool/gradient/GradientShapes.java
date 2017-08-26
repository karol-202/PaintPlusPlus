package pl.karol202.paintplus.tool.gradient;

import java.util.ArrayList;
import java.util.List;

class GradientShapes
{
	private List<GradientShape> shapes;
	
	GradientShapes(ToolGradient toolGradient)
	{
		shapes = new ArrayList<>();
		shapes.add(new GradientShapeLinear(toolGradient));
		shapes.add(new GradientShapeBilinear(toolGradient));
		shapes.add(new GradientShapeRadial(toolGradient));
		shapes.add(new GradientShapeSweep(toolGradient));
		shapes.add(new GradientShapeSweepSymmetric(toolGradient));
	}
	
	GradientShape getShape(int id)
	{
		return shapes.get(id);
	}
	
	int getIdOfShape(GradientShape shape)
	{
		for(int i = 0; i < shapes.size(); i++)
		{
			if(shapes.get(i) == shape) return i;
		}
		return -1;
	}
	
	public List<GradientShape> getShapes()
	{
		return shapes;
	}
}