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

package pl.karol202.paintplus.tool.marker;

public class MarkerAdapterQuadraticPath extends MarkerAdapterSimple
{
	private float lastX;
	private float lastY;
	
	MarkerAdapterQuadraticPath(ToolMarker marker)
	{
		super(marker);
	}
	
	@Override
	public void onBeginDraw(float x, float y)
	{
		super.onBeginDraw(x, y);
		
		lastX = -1;
		lastY = -1;
	}
	
	@Override
	public void onDraw(float x, float y)
	{
		super.onDraw(x, y);
		if(lastX != -1 && lastY != -1)
		{
			path.quadTo(lastX, lastY, x, y);
			lastX = -1;
			lastY = -1;
		}
		else
		{
			lastX = x;
			lastY = y;
		}
	}
	
	@Override
	public void onEndDraw(float x, float y)
	{
		if(lastX != -1 && lastY != -1) path.quadTo(lastX, lastY, x, y);
		else path.lineTo(x, y);
		
		lastX = -1;
		lastY = -1;
		
		super.onEndDraw(x, y);
	}
}