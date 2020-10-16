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

package pl.karol202.paintplus.color.curves;

import android.graphics.Point;
import pl.karol202.paintplus.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class ColorCurve
{
	private ChannelInOutSet channels;
	private ArrayList<Point> points;
	private boolean sorted;
	
	private ColorCurve(ChannelInOutSet channels)
	{
		this.channels = channels;
		this.points = new ArrayList<>();
		this.sorted = true;
	}
	
	void addPoint(Point newPoint)
	{
		for(Point point : points)
			if(point.x == newPoint.x) return;
		
		points.add(newPoint);
		sorted = false;
	}
	
	boolean movePoint(Point oldPoint, Point newPoint)
	{
		for(Point point : points)
			if(point.x == newPoint.x && point != oldPoint) return false;
		
		if(!points.contains(oldPoint)) return false;
		points.remove(oldPoint);
		points.add(newPoint);
		sorted = false;
		return true;
	}
	
	boolean removePoint(Point point)
	{
		if(!points.contains(point)) return false;
		if(points.size() <= 2) return false;
		points.remove(point);
		sorted = false;
		return true;
	}
	
	Point[] getPoints()
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
		Arrays.sort(array, (p1, p2) -> Integer.compare(p1.x, p2.x));
		points = new ArrayList<>(Arrays.asList(array));
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		
		ColorCurve that = (ColorCurve) o;
		
		return (channels != null ? channels.equals(that.channels) : that.channels == null) && points.equals(that.points);
	}
	
	@Override
	public int hashCode()
	{
		int result = channels != null ? channels.hashCode() : 0;
		result = 31 * result + points.hashCode();
		return result;
	}
	
	public byte[] createByteColorsMap()
	{
		ColorChannel inChannel = channels.getIn();
		byte[] map = new byte[inChannel.getMaxValue() + 1];
		for(int i = 0; i < map.length; i++)
			map[i] = (byte) Math.round(evaluate(i));
		return map;
	}
	
	public short[] createShortColorsMap()
	{
		ColorChannel inChannel = channels.getIn();
		short[] map = new short[inChannel.getMaxValue() + 1];
		for(int i = 0; i < map.length; i++)
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
		else if(pos == points.size()) return points.get(pos - 1).y;
		Point lower = points.get(pos - 1);
		Point higher = points.get(pos);
		return Utils.map(x, lower.x, higher.x, lower.y, higher.y);
	}
	
	static ColorCurve defaultCurve(ChannelInOutSet channels)
	{
		ColorCurve curve = new ColorCurve(channels);
		curve.addPoint(new Point(0, 0));
		curve.addPoint(new Point(channels.getIn().getMaxValue(), channels.getOut().getMaxValue()));
		return curve;
	}
	
	static ColorCurve zeroCurve(ChannelInOutSet channels)
	{
		ColorCurve curve = new ColorCurve(channels);
		curve.addPoint(new Point(0, 0));
		curve.addPoint(new Point(channels.getIn().getMaxValue(), 0));
		return curve;
	}
}