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

public class HSVToRGB
{
	private int r;
	private int g;
	private int b;
	
	public void setColor(int h_, int s_, int v_)
	{
		float h = h_;
		float s = s_ / 100f;
		float v = v_ / 100f;
		
		float c = v * s;
		float x = c * (1 - Math.abs(((h / 60) % 2) - 1));
		float m = v - c;
		
		float r_ = 0;
		float g_ = 0;
		float b_ = 0;
		if(h >= 0 && h < 60)
		{
			r_ = c;
			g_ = x;
			b_ = 0;
		}
		else if(h >= 60 && h < 120)
		{
			r_ = x;
			g_ = c;
			b_ = 0;
		}
		else if(h >= 120 && h < 180)
		{
			r_ = 0;
			g_ = c;
			b_ = x;
		}
		else if(h >= 180 && h < 240)
		{
			r_ = 0;
			g_ = x;
			b_ = c;
		}
		else if(h >= 240 && h < 300)
		{
			r_ = x;
			g_ = 0;
			b_ = c;
		}
		else if(h >= 300 && h < 360)
		{
			r_ = c;
			g_ = 0;
			b_ = x;
		}
		
		r = Math.round((r_ + m) * 255);
		g = Math.round((g_ + m) * 255);
		b = Math.round((b_ + m) * 255);
	}
	
	public int getR()
	{
		return r;
	}
	
	public int getG()
	{
		return g;
	}
	
	public int getB()
	{
		return b;
	}
}