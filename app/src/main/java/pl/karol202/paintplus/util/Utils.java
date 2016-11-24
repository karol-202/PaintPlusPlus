package pl.karol202.paintplus.util;

public class Utils
{
	public static float map(float src, int srcMin, int srcMax, int dstMin, int dstMax)
	{
		float srcPoint = (src - srcMin) / (srcMax - srcMin);
		return lerp(srcPoint, dstMin, dstMax);
	}

	public static float lerp(float value, int v1, int v2)
	{
		return v1 + value * (v2 - v1);
	}
}
