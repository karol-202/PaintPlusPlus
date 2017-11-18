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

package pl.karol202.paintplus.color;

public class RGBToHSV
{
	private float r;
	private float g;
	private float b;
	
	private float min;
	private float max;
	private float delta;
	
	private int h;
	private int s;
	private int v;
	
	public void setColor(int r_, int g_, int b_)
	{
		this.r = r_ / 255f;
		this.g = g_ / 255f;
		this.b = b_ / 255f;
		
		min = Math.min(Math.min(r, g), b);
		max = Math.max(Math.max(r, g), b);
		delta = max - min;
		
		h = calculateHue();
		s = Math.round(max != 0 ? delta / max * 100 : 0);
		v = Math.round(max * 100);
	}
	
	private int calculateHue()
	{
		float hue = 0;
		if(delta == 0) hue = 0;
		else if(max == r) hue = ((g - b) / delta);
		else if(max == g) hue = ((b - r) / delta) + 2;
		else if(max == b) hue = ((r - g) / delta) + 4;
		if(hue < 0) hue += 6;
		return Math.round(hue * 60);
	}
	
	public int getH()
	{
		return h;
	}
	
	public int getS()
	{
		return s;
	}
	
	public int getV()
	{
		return v;
	}
}