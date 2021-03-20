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

package pl.karol202.paintplus.helpers;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;
import pl.karol202.paintplus.image.LegacyImage;

public class HelpersManager
{
	private LegacyImage image;

	private Grid grid;

	public HelpersManager(LegacyImage image, Resources resources)
	{
		this.image = image;

		this.grid = new Grid(image, resources);
	}

	public void onScreenDraw(Canvas canvas)
	{
		grid.onScreenDraw(canvas);
	}

	//Not used yet.
	public void onTouch(MotionEvent event)
	{

	}

	public float snapX(float x)
	{
		if(grid.isSnapToGrid()) return grid.snapXToGrid(x);
		else return x;
	}

	public float snapY(float y)
	{
		if(grid.isSnapToGrid()) return grid.snapYToGrid(y);
		else return y;
	}

	public void snapPoint(PointF point)
	{
		if(grid.isSnapToGrid()) grid.snapPointToGrid(point);
	}

	public Grid getGrid()
	{
		return grid;
	}
}
