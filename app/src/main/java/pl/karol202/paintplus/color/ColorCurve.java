package pl.karol202.paintplus.color;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ColorCurve
{
	private ArrayList<Point> points;
	private boolean sorted;
	
	public ColorCurve()
	{
		points = new ArrayList<>();
		sorted = true;
	}
	
	public void addPoint(int x, int y)
	{
		points.add(new Point(x, y));
		sorted = false;
	}
	
	public void movePoint(Point oldPoint, Point newPoint)
	{
		points.remove(oldPoint);
		points.add(newPoint);
		sorted = false;
	}
	
	public void removePoint(Point point)
	{
		points.remove(point);
		sorted = false;
	}
	
	public Point[] getPoints()
	{
		sort();
		return convertToArray();
	}
	
	private Point[] convertToArray()
	{
		Point[] array = new Point[points.size()];
		points.toArray(array);
		return array;
	}
	
	private void sort()
	{
		if(sorted) return;
		sorted = true;
		
		Point[] array = convertToArray();
		Arrays.sort(array, new Comparator<Point>()
		{
			@Override
			public int compare(Point p1, Point p2)
			{
				if(p1.x > p2.x) return 1;
				else if(p1.x < p2.x) return -1;
				else return 0;
			}
		});
		points = new ArrayList<>(Arrays.asList(array));
	}
	
	public static ColorCurve defaultCurve()
	{
		ColorCurve curve = new ColorCurve();
		curve.addPoint(0, 0);
		curve.addPoint(255, 255);
		return curve;
	}
}