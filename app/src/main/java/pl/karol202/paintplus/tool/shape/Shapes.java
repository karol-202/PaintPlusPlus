/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package pl.karol202.paintplus.tool.shape;

import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.shape.circle.ShapeCircle;
import pl.karol202.paintplus.tool.shape.line.ShapeLine;
import pl.karol202.paintplus.tool.shape.polygon.ShapePolygon;
import pl.karol202.paintplus.tool.shape.star.ShapeStar;

import java.util.ArrayList;

class Shapes
{
	private ArrayList<Shape> shapes;
	
	Shapes(Image image, OnShapeEditListener shapeEditListener)
	{
		shapes = new ArrayList<>();
		shapes.add(new ShapeLine(image, shapeEditListener));
		shapes.add(new ShapeCircle(image, shapeEditListener));
		shapes.add(new ShapePolygon(image, shapeEditListener));
		shapes.add(new ShapeStar(image, shapeEditListener));
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