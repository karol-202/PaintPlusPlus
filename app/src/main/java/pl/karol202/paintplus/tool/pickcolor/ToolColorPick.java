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

package pl.karol202.paintplus.tool.pickcolor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.image.LegacyImage;
import pl.karol202.paintplus.tool.StandardTool;
import pl.karol202.paintplus.tool.ToolCoordinateSpace;
import pl.karol202.paintplus.tool.ToolProperties;

public class ToolColorPick extends StandardTool
{
	private int size;

	private Bitmap bitmap;
	private ColorsSet colors;

	public ToolColorPick(LegacyImage image)
	{
		super(image);
		this.size = 1;

		this.colors = image.getColorsSet();
	}

	@Override
	public int getName()
	{
		return R.string.tool_color_pick;
	}

	@Override
	public int getIcon()
	{
		return R.drawable.ic_tool_color_pick_black_24dp;
	}

	@Override
	public Class<? extends ToolProperties> getPropertiesFragmentClass()
	{
		return ColorPickProperties.class;
	}

	@Override
	public ToolCoordinateSpace getCoordinateSpace()
	{
		return ToolCoordinateSpace.LAYER_SPACE;
	}

	@Override
	public boolean isUsingSnapping()
	{
		return false;
	}

	@Override
	public boolean onTouchStart(float x, float y)
	{
		return true;
	}

	@Override
	public boolean onTouchMove(float x, float y)
	{
		return true;
	}

	@Override
	public boolean onTouchStop(float x, float y)
	{
		pickColor((int) x, (int) y);
		return false;
	}

	private void pickColor(int x, int y)
	{
		bitmap = image.getSelectedBitmap();
		if(x < 0 || y < 0 || x >= bitmap.getWidth() || y >= bitmap.getHeight()) return;

		if(bitmap == null) colors.setFirstColor(Color.BLACK);
		else if(size == 1) pickPixelColor(x, y);
		else if(size > 1) pickAverageColor(x, y);
	}

	private void pickPixelColor(int x, int y)
	{
		if(!checkSelection(x, y)) return;
		int color = bitmap.getPixel(x, y);
		colors.setFirstColor(color);
	}

	private void pickAverageColor(int centerX, int centerY)
	{
		int pixels = 0;
		long redSum = 0;
		long greenSum = 0;
		long blueSum = 0;

		int regionStartX = centerX - (int) Math.floor((size - 1) / 2);
		int regionStartY = centerY - (int) Math.floor((size - 1) / 2);
		int regionEndX = centerX + (int) Math.floor(size / 2);
		int regionEndY = centerY + (int) Math.floor(size / 2);
		for(int x = regionStartX; x <= regionEndX; x++)
		{
			for(int y = regionStartY; y <= regionEndY; y++)
			{
				if(!checkSelection(x, y)) continue;
				int color = bitmap.getPixel(x, y);
				pixels++;
				redSum += Math.pow(Color.red(color), 2);
				greenSum += Math.pow(Color.green(color), 2);
				blueSum += Math.pow(Color.blue(color), 2);
			}
		}

		int red = (int) Math.round(Math.sqrt(redSum / (double) pixels));
		int green = (int) Math.round(Math.sqrt(greenSum / (double) pixels));
		int blue = (int) Math.round(Math.sqrt(blueSum / (double) pixels));
		colors.setFirstColor(Color.rgb(red, green, blue));
	}

	private boolean checkSelection(int x, int y)
	{
		return selection.isEmpty() || selection.containsPoint(x + image.getSelectedLayerX(), y + image.getSelectedLayerY());
	}

	@Override
	public boolean providesDirtyRegion()
	{
		return false;
	}

	@Override
	public Rect getDirtyRegion()
	{
		return null;
	}

	@Override
	public void resetDirtyRegion() { }

	@Override
	public boolean doesOnLayerDraw(boolean layerVisible)
	{
		return false;
	}

	@Override
	public boolean doesOnTopDraw()
	{
		return false;
	}

	@Override
	public ToolCoordinateSpace getOnLayerDrawingCoordinateSpace()
	{
		return null;
	}

	@Override
	public ToolCoordinateSpace getOnTopDrawingCoordinateSpace()
	{
		return null;
	}

	@Override
	public void onLayerDraw(Canvas canvas) { }

	@Override
	public void onTopDraw(Canvas canvas) { }

	int getSize()
	{
		return size;
	}

	void setSize(int size)
	{
		this.size = size;
	}
}
