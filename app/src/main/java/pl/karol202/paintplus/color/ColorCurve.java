package pl.karol202.paintplus.color;

import android.graphics.Point;
import pl.karol202.paintplus.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ColorCurve
{
	private ChannelInOutSet channels;
	private ArrayList<Point> points;
	private boolean sorted;
	
	public ColorCurve(ChannelInOutSet channels)
	{
		this.channels = channels;
		this.points = new ArrayList<>();
		this.sorted = true;
	}
	
	public void addPoint(Point newPoint)
	{
		for(Point point : points)
			if(point.x == newPoint.x) return;
		
		points.add(newPoint);
		sorted = false;
	}
	
	public boolean movePoint(Point oldPoint, Point newPoint)
	{
		for(Point point : points)
			if(point.x == newPoint.x && point != oldPoint) return false;
		
		if(!points.contains(oldPoint)) return false;
		points.remove(oldPoint);
		points.add(newPoint);
		sorted = false;
		return true;
	}
	
	public boolean removePoint(Point point)
	{
		if(!points.contains(point)) return false;
		if(points.size() <= 2) return false;
		points.remove(point);
		sorted = false;
		return true;
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
	
	public byte[] createByteColorsMap()
	{
		ColorChannel inChannel = channels.getIn();
		byte[] map = new byte[inChannel.getMaxValue() + 1];
		for(int i = 0; i < inChannel.getMaxValue() + 1; i++)
			map[i] = (byte) Math.round(evaluate(i));
		return map;
	}
	
	public short[] createShortColorsMap()
	{
		ColorChannel inChannel = channels.getIn();
		short[] map = new short[inChannel.getMaxValue() + 1];
		for(int i = 0; i < inChannel.getMaxValue() + 1; i++)
			map[i] = (short) Math.round(evaluate(i));
		return map;
	}
	
	private float evaluate(int x)
	{
		sort();
		int pos = 0;
		for(int i = 0; i < points.size(); i++)
		{
			int currentX = points.get(i).x;
			if(currentX < x) pos = i + 1;
		}
		
		if(pos == 0) return points.get(0).y;
		if(pos == points.size()) return points.get(pos - 1).y;
		Point lower = points.get(pos - 1);
		Point higher = points.get(pos);
		return Utils.map(x, lower.x, higher.x, lower.y, higher.y);
	}
	
	public static ColorCurve defaultCurve(ChannelInOutSet channels)
	{
		ColorCurve curve = new ColorCurve(channels);
		curve.addPoint(new Point(0, 0));
		curve.addPoint(new Point(channels.getIn().getMaxValue(), channels.getOut().getMaxValue()));
		return curve;
	}
	
	public static ColorCurve zeroCurve(ChannelInOutSet channels)
	{
		ColorCurve curve = new ColorCurve(channels);
		curve.addPoint(new Point(0, 0));
		curve.addPoint(new Point(channels.getIn().getMaxValue(), 0));
		return curve;
	}
}