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

import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.util.MathUtils;

import java.util.*;

class GradientShapeSweepSymmetric extends GradientShape
{
	private int[] oldColors;
	private float[] oldPositions;

	GradientShapeSweepSymmetric(ToolGradient toolGradient)
	{
		super(toolGradient);
	}

	@Override
	int getName()
	{
		return R.string.gradient_shape_sweep_symmetric;
	}

	@Override
	int getIcon()
	{
		return R.drawable.ic_gradient_shape_sweep_symmetric;
	}

	@Override
	Shader createShader()
	{
		float angle = (float) Math.toDegrees(Math.atan2(getSecondPoint().y - getFirstPoint().y,
														getSecondPoint().x - getFirstPoint().x));
		float offset = MathUtils.map(angle, -180, 180, -0.5f, 0.5f);

		mirrorGradient();
		int oldLength = oldColors.length;

		Map<Float, Integer> gradient = new HashMap<>();
		for(int i = 0; i < oldLength; i++)
		{
			int color = oldColors[i];
			float position = oldPositions[i] + offset;
			if(position < 0) position += 1.0001;
			if(position > 1) position -= 1.0001;
			gradient.put(position, color);
		}
		if(!gradient.containsKey(0f) || !gradient.containsKey(1f))
		{
			float zeroPos = offset > 0 ? 1 - offset : 0 - offset;
			int zeroColor = interpolate(oldColors, oldPositions, zeroPos);
			gradient.put(0f, zeroColor);
			gradient.put(1f, zeroColor);
		}

		List<Map.Entry<Float, Integer>> entries = new ArrayList<>(gradient.entrySet());
		Collections.sort(entries, (e1, e2) -> Float.compare(e1.getKey(), e2.getKey()));
		int[] newColors = new int[gradient.size()];
		float[] newPositions = new float[gradient.size()];
		int i = 0;
		float lastPosition = -1;
		for(Map.Entry<Float, Integer> entry : entries)
		{
			float position = entry.getKey();
			if(position == lastPosition) position += 0.001f;
			newColors[i] = entry.getValue();
			newPositions[i++] = position;
			lastPosition = position;
		}

		return new SweepGradient(getFirstPoint().x, getFirstPoint().y, newColors, newPositions);
	}

	private void mirrorGradient()
	{
		oldColors = getColorsArray();
		oldPositions = getPositionsArray();
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

		oldColors = newColors;
		oldPositions = newPositions;
	}

	private int interpolate(int[] colors, float[] positions, float position)
	{
		int lowerColor = Color.BLACK;
		float lowerPosition = 0f;
		int higherColor = Color.BLACK;
		float higherPosition = 1f;
		for(int i = 0; i < positions.length; i++)
		{
			if(positions[i] < position && positions[i] >= lowerPosition)
			{
				lowerColor = colors[i];
				lowerPosition = positions[i];
			}
			if(positions[i] > position && positions[i] <= higherPosition)
			{
				higherColor = colors[i];
				higherPosition = positions[i];
			}
		}
		return mapColor(position, lowerPosition, higherPosition, lowerColor, higherColor);
	}

	private int mapColor(float src, float srcMin, float srcMax, int colorMin, int colorMax)
	{
		int alpha = Math.round(MathUtils.map(src, srcMin, srcMax, Color.alpha(colorMin), Color.alpha(colorMax)));
		int red = Math.round(MathUtils.map(src, srcMin, srcMax, Color.red(colorMin), Color.red(colorMax)));
		int green = Math.round(MathUtils.map(src, srcMin, srcMax, Color.green(colorMin), Color.green(colorMax)));
		int blue = Math.round(MathUtils.map(src, srcMin, srcMax, Color.blue(colorMin), Color.blue(colorMax)));
		return Color.argb(alpha, red, green, blue);
	}
}
