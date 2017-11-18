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

public class RGBToCMYK
{
	private int c;
	private int m;
	private int y;
	private int k;
	
	public void setColor(int r_, int g_, int b_)
	{
		float r = r_ / 255f;
		float g = g_ / 255f;
		float b = b_ / 255f;
		
		float k = 1 - Math.max(Math.max(r, g), b);
		float c = (1 - r - k) / (1 - k);
		float m = (1 - g - k) / (1 - k);
		float y = (1 - b - k) / (1 - k);
		
		this.c = Math.round(c * 255);
		this.m = Math.round(m * 255);
		this.y = Math.round(y * 255);
		this.k = Math.round(k * 255);
	}
	
	public int getC()
	{
		return c;
	}
	
	public int getM()
	{
		return m;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getK()
	{
		return k;
	}
}