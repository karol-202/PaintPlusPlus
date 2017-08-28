package pl.karol202.paintplus.tool.gradient;

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
	
	void addPoint(float position, int color)
	{
		points.add(new GradientPoint(position, color));
		sort();
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
	
	int[] getColorsArray()
	{
		int[] array = new int[points.size()];
		for(int i = 0; i < points.size(); i++) array[i] = points.get(i).getColor();
		return array;
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