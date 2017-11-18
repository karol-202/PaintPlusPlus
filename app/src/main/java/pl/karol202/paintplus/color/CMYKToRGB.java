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

public class CMYKToRGB
{
	private int r;
	private int g;
	private int b;
	
	public void setColor(int c_, int m_, int y_, int k_)
	{
		float c = c_ / 255f;
		float m = m_ / 255f;
		float y = y_ / 255f;
		float k = k_ / 255f;
		
		r = Math.round((1 - c) * (1 - k) * 255);
		g = Math.round((1 - m) * (1 - k) * 255);
		b = Math.round((1 - y) * (1 - k) * 255);
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