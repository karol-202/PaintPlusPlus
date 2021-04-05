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

import android.graphics.*;
import androidx.annotation.NonNull;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.gradient.ToolGradient;

public class GradientShapeBilinear extends AbstractGradientShape
{
	GradientShapeBilinear(ToolGradient toolGradient)
	{
		super(toolGradient);
	}

	@Override
	public int getName()
	{
		return R.string.gradient_shape_bilinear;
	}

	@Override
	public int getIcon()
	{
		return R.drawable.ic_gradient_shape_bilinear;
	}

	@NonNull
	@Override
	public Shader createShader(@NonNull Point start, @NonNull Point end)
	{
		int[] oldColors = getColorsArray();
		float[] oldPositions = getPositionsArray();
		for(int i = 0; i < oldPositions.length; i++) oldPositions[i] = oldPositions[i] / 2;

		if(oldColors.length != oldPositions.length) throw new RuntimeException("Corrupted gradient.");
		int oldLength = oldColors.length;
		int newLength = (oldLength * 2) - 1;

		int[] newColors = new int[newLength];
		float[] newPositions = new float[newLength];

		System.arraycopy(oldColors, 0, newColors, 0, oldLength);
		System.arraycopy(oldPositions, 0, newPositions, 0, oldLength);
		for(int i = oldLength; i < newLength; i++)
		{
			newColors[i] = oldColors[newLength - i - 1];
			newPositions[i] = 1 - oldPositions[newLength - i - 1];
		}

		return new LinearGradient(start.x, start.y, end.x, end.y,
								  newColors, newPositions, getTileMode());
	}
}
