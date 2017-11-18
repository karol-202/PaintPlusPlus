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

package pl.karol202.paintplus.util;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Utils
{
	public static float map(float src, float srcMin, float srcMax, float dstMin, float dstMax)
	{
		float srcPoint = (src - srcMin) / (srcMax - srcMin);
		return lerp(srcPoint, dstMin, dstMax);
	}

	public static float lerp(float value, float v1, float v2)
	{
		return v1 + value * (v2 - v1);
	}
	
	public static float clamp(float value, float min, float max)
	{
		return Math.min(Math.max(value, min), max);
	}
	
	public static int roundAwayFromZero(float value)
	{
		return (int) (value > 0 ? Math.ceil(value) : Math.floor(value));
	}
	
	public static float dpToPixels(Context context, float dp)
	{
		return dpToPixels(context.getResources().getDisplayMetrics(), dp);
	}
	
	public static float dpToPixels(DisplayMetrics metrics, float dp)
	{
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
	}
	
	public static double getAngle(Point center, Point point)
	{
		double deltaX = point.x - center.x;
		double deltaY = center.y - point.y;
		double ratio = deltaX / deltaY;
		double angleRad = Math.atan(ratio);
		double angleDeg = Math.toDegrees(angleRad);
		if(deltaY < 0) angleDeg += 180;
		if(angleDeg < 0) angleDeg += 360;
		return angleDeg;
	}
}