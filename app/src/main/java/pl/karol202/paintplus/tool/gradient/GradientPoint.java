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

package pl.karol202.paintplus.tool.gradient;

class GradientPoint implements Comparable<GradientPoint>
{
	private float position;
	private int color;
	
	GradientPoint(float position, int color)
	{
		this.position = position;
		this.color = color;
	}
	
	GradientPoint(GradientPoint point)
	{
		this(point.position, point.color);
	}
	
	@Override
	public int compareTo(GradientPoint o)
	{
		return Float.compare(position, o.position);
	}
	
	float getPosition()
	{
		return position;
	}
	
	void setPosition(float position)
	{
		this.position = position;
	}
	
	int getColor()
	{
		return color;
	}
	
	void setColor(int color)
	{
		this.color = color;
	}
}