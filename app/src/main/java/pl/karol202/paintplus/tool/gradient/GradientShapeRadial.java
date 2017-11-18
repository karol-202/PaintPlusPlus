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

import android.graphics.RadialGradient;
import android.graphics.Shader;
import pl.karol202.paintplus.R;

class GradientShapeRadial extends GradientShape
{
	GradientShapeRadial(ToolGradient toolGradient)
	{
		super(toolGradient);
	}
	
	@Override
	int getName()
	{
		return R.string.gradient_shape_radial;
	}
	
	@Override
	int getIcon()
	{
		return R.drawable.ic_gradient_shape_radial;
	}
	
	@Override
	Shader createShader()
	{
		float radius = (float) Math.hypot(getSecondPoint().x - getFirstPoint().x, getSecondPoint().y - getFirstPoint().y);
		radius = Math.max(radius, 0.01f);
		return new RadialGradient(getFirstPoint().x, getFirstPoint().y, radius,
								  getColorsArray(), getPositionsArray(), getTileMode());
	}
}