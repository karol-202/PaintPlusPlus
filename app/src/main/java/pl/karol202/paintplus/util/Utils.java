package pl.karol202.paintplus.util;

import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Utils
{
	public static float map(float src, int srcMin, int srcMax, int dstMin, int dstMax)
	{
		float srcPoint = (src - srcMin) / (srcMax - srcMin);
		return lerp(srcPoint, dstMin, dstMax);
	}

	private static float lerp(float value, int v1, int v2)
	{
		return v1 + value * (v2 - v1);
	}
	
	public static int dpToPixels(DisplayMetrics metrics, int dp)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
	}
}