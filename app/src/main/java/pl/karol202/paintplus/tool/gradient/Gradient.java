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
import pl.karol202.paintplus.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Gradient
{
	private List<GradientPoint> points;
	
	private Gradient()
	{
		points = new ArrayList<>();
	}
	
	Gradient(Gradient gradient)
	{
		this();
		setGradient(gradient);
	}
	
	static Gradient createSimpleGradient(int firstColor, int secondColor)
	{
		Gradient gradient = new Gradient();
		gradient.addPoint(0f, firstColor);
		gradient.addPoint(1f, secondColor);
		return gradient;
	}
	
	private void addPoint(float position, int color)
	{
		points.add(new GradientPoint(position, color));
		sort();
	}
	
	GradientPoint addPoint(float position)
	{
		GradientPoint point = new GradientPoint(position, getValueAtPosition(position));
		points.add(point);
		sort();
		return point;
	}
	
	private int getValueAtPosition(float position)
	{
		GradientPoint lastPoint = points.get(points.size() - 1);
		if(lastPoint.getPosition() <= position) return lastPoint.getColor();
		GradientPoint nextPoint;
		int nextIndex = -1;
		do
		{
			nextPoint = points.get(++nextIndex);
			if(nextPoint.getPosition() == position) return nextPoint.getColor();
		}
		while(nextPoint.getPosition() < position);
		if(nextIndex == 0) return nextPoint.getColor();
		GradientPoint previousPoint = points.get(nextIndex - 1);
		return mapColor(position, previousPoint.getPosition(), nextPoint.getPosition(),
								  previousPoint.getColor(), nextPoint.getColor());
	}
	
	private int mapColor(float src, float srcMin, float srcMax, int colorMin, int colorMax)
	{
		int alpha = Math.round(Utils.map(src, srcMin, srcMax, Color.alpha(colorMin), Color.alpha(colorMax)));
		int red = Math.round(Utils.map(src, srcMin, srcMax, Color.red(colorMin), Color.red(colorMax)));
		int green = Math.round(Utils.map(src, srcMin, srcMax, Color.green(colorMin), Color.green(colorMax)));
		int blue = Math.round(Utils.map(src, srcMin, srcMax, Color.blue(colorMin), Color.blue(colorMax)));
		return Color.argb(alpha, red, green, blue);
	}
	
	void deletePoint(GradientPoint point)
	{
		points.remove(point);
	}
	
	void sort()
	{
		Collections.sort(points);
	}
	
	float[] getPositionsArray()
	{
		float[] array = new float[points.size()];
		for(int i = 0; i < points.size(); i++) array[i] = points.get(i).getPosition();
		return array;
	}
	
	float[] getRevertedPositionsArray()
	{
		float[] array = new float[points.size()];
		for(int i = 0; i < points.size(); i++) array[i] = 1 - points.get(points.size() - 1 - i).getPosition();
		return array;
	}
	
	int[] getColorsArray()
	{
		int[] array = new int[points.size()];
		for(int i = 0; i < points.size(); i++) array[i] = points.get(i).getColor();
		return array;
	}
	
	int[] getRevertedColorsArray()
	{
		int[] array = new int[points.size()];
		for(int i = 0; i < points.size(); i++) array[i] = points.get(points.size() - 1 - i).getColor();
		return array;
	}
	
	int getPointsAmount()
	{
		return points.size();
	}
	
	List<GradientPoint> getPoints()
	{
		return new ArrayList<>(points);
	}
	
	void setGradient(Gradient gradient)
	{
		points.clear();
		for(GradientPoint point : gradient.points) points.add(new GradientPoint(point));
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		
		Gradient gradient = (Gradient) o;
		
		return points.equals(gradient.points);
	}
	
	@Override
	public int hashCode()
	{
		return points.hashCode();
	}
}