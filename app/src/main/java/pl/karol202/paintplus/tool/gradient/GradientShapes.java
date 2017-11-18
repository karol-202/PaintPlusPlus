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