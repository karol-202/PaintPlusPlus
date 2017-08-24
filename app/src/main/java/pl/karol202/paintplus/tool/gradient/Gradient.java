package pl.karol202.paintplus.tool.gradient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Gradient
{
	private class GradientPoint implements Comparable<GradientPoint>
	{
		private float position;
		private int color;
		
		GradientPoint(float position, int color)
		{
			this.position = position;
			this.color = color;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;
			
			GradientPoint that = (GradientPoint) o;
			
			if(Float.compare(that.position, position) != 0) return false;
			return color == that.color;
		}
		
		@Override
		public int hashCode()
		{
			int result = (position != +0.0f ? Float.floatToIntBits(position) : 0);
			result = 31 * result + color;
			return result;
		}
		
		@Override
		public int compareTo(GradientPoint o)
		{
			return (int) Math.ceil(position - o.position);
		}
	}
	
	private List<GradientPoint> points;
	
	Gradient()
	{
		points = new ArrayList<>();
	}
	
	static Gradient createSimpleGradient(int firstColor, int secondColor)
	{
		Gradient gradient = new Gradient();
		gradient.addPoint(0f, firstColor);
		gradient.addPoint(1f, secondColor);
		return gradient;
	}
	
	void addPoint(float position, int color)
	{
		points.add(new GradientPoint(position, color));
		Collections.sort(points);
	}
	
	float[] getPositionsArray()
	{
		float[] array = new float[points.size()];
		for(int i = 0; i < points.size(); i++) array[i] = points.get(i).position;
		return array;
	}
	
	int[] getColorsArray()
	{
		int[] array = new int[points.size()];
		for(int i = 0; i < points.size(); i++) array[i] = points.get(i).color;
		return array;
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