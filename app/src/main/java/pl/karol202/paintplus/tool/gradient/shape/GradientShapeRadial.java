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

package pl.karol202.paintplus.tool.gradient.shape;

import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import androidx.annotation.NonNull;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.gradient.ToolGradient;

public class GradientShapeRadial extends AbstractGradientShape
{
	GradientShapeRadial(ToolGradient toolGradient)
	{
		super(toolGradient);
	}

	@Override
	public int getName()
	{
		return R.string.gradient_shape_radial;
	}

	@Override
	public int getIcon()
	{
		return R.drawable.ic_gradient_shape_radial;
	}

	@NonNull
	@Override
	public Shader createShader(@NonNull Point start, @NonNull Point end)
	{
		float radius = (float) Math.hypot(end.x - start.x, end.y - start.y);
		radius = Math.max(radius, 0.01f);
		return new RadialGradient(start.x, start.y, radius,
								  getColorsArray(), getPositionsArray(), getTileMode());
	}
}
