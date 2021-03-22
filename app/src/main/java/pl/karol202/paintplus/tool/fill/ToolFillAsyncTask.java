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

package pl.karol202.paintplus.tool.fill;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.legacyaction.ActionLayerChange;
import pl.karol202.paintplus.image.LegacyImage;
import pl.karol202.paintplus.image.LegacySelection;

import java.util.Stack;

public class ToolFillAsyncTask extends AsyncTask<FillParams, Void, Bitmap>
{
	public interface OnFillCompleteListener
	{
		void onFillComplete(Bitmap bitmap);
	}

	private static final float COLOR_COMPARISON_CONST = (float) Math.sqrt(3 * Math.pow(255, 2));

	private OnFillCompleteListener listener;
	private LegacyImage image;
	private Bitmap bitmap;
	private LegacySelection selection;

	private int selectedLayerX;
	private int selectedLayerY;
	private int destColor;
	private float threshold;
	private int x;
	private int y;
	private ActionLayerChange historyAction;

	@Override
	protected Bitmap doInBackground(FillParams... paramsArray)
	{
		if(paramsArray.length != 1)
			throw new IllegalArgumentException("There must be only one params object passed to ToolFillAsyncTask.");
		FillParams params = paramsArray[0];
		image = params.getImage();
		ColorsSet colorsSet = image.getColorsSet();
		listener = params.getListener();
		bitmap = image.getSelectedBitmap();
		if(bitmap == null) throw new NullPointerException("There is no selected layer.");
		bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true); // TODO Try to remove copying of bitmap.
		selection = image.getSelection();

		selectedLayerX = image.getSelectedLayerX();
		selectedLayerY = image.getSelectedLayerY();
		destColor = colorsSet.getFirstColor();
		threshold = params.getThreshold();
		x = params.getX();
		y = params.getY();
		historyAction = new ActionLayerChange(image, R.string.tool_fill);
		historyAction.setLayerChange(image.getSelectedLayerIndex(), bitmap);

		fill();
		return bitmap;
	}

	private void fill()
	{
		if(!selection.isEmpty() && !selection.containsPoint(x, y)) return;

		int touchedColor = bitmap.getPixel(x, y);
		if(touchedColor == destColor) return;

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		Stack<Point> pointsToCheck = new Stack<>();
		pointsToCheck.push(new Point(x, y));
		while(!pointsToCheck.isEmpty())
		{
			Point point = pointsToCheck.pop();
			if(!checkSelection(point)) continue;

			int pos = point.y * width + point.x;
			int oldColor = pixels[pos];
			if(!checkColor(touchedColor, oldColor)) continue;
			pixels[pos] = destColor;

			if(point.x > 0)
				pointsToCheck.add(new Point(point.x - 1, point.y));
			if(point.y > 0)
				pointsToCheck.add(new Point(point.x, point.y - 1));
			if(point.x < width - 1)
				pointsToCheck.add(new Point(point.x + 1, point.y));
			if(point.y < height - 1)
				pointsToCheck.add(new Point(point.x, point.y + 1));
		}
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
	}

	private boolean checkSelection(Point point)
	{
		return selection.isEmpty() || selection.containsPoint(point.x + selectedLayerX, point.y + selectedLayerY);
	}

	private boolean checkColor(int touched, int current)
	{
		if(current == touched) return true;
		if(threshold == 0f) return false;
		else
		{
			int distanceR = Color.red(touched) - Color.red(current);
			int distanceG = Color.green(touched) - Color.green(current);
			int distanceB = Color.blue(touched) - Color.blue(current);
			float distance = (float) Math.sqrt(Math.pow(distanceR, 2) + Math.pow(distanceG, 2) + Math.pow(distanceB, 2));
			float percentage = distance / COLOR_COMPARISON_CONST * 100;
			return percentage <= threshold;
		}
	}

	@Override
	protected void onPostExecute(Bitmap bitmap)
	{
		super.onPostExecute(bitmap);
		if(listener != null) listener.onFillComplete(bitmap);
		historyAction.applyAction();
	}
}
