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

package pl.karol202.paintplus.history.action;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import pl.karol202.paintplus.history.HistoryActionViewHolder;
import pl.karol202.paintplus.image.LegacyImage;

public abstract class Action
{
	private boolean done;

	private boolean applied;
	private LegacyImage temporaryImage;

	private Bitmap previewBitmap;
	private Canvas previewCanvas;
	private Rect previewRect;

	Action(LegacyImage image)
	{
		done = true;

		applied = false;
		temporaryImage = image;
		createBitmap(image);
	}

	private void createBitmap(LegacyImage image)
	{
		int bitmapSize = (int) Math.floor(HistoryActionViewHolder.PREVIEW_SIZE_DP * image.SCREEN_DENSITY);
		previewBitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
		previewCanvas = new Canvas(previewBitmap);
		previewRect = new Rect(0, 0, bitmapSize, bitmapSize);
	}

	RectF transformLayerRect(Bitmap layerBitmap)
	{
		float max = Math.max(layerBitmap.getWidth(), layerBitmap.getHeight());
		float ratio = getPreviewRect().width() / max;
		RectF rect = new RectF(0, 0, layerBitmap.getWidth() * ratio, layerBitmap.getHeight() * ratio);
		rect.offset(getPreviewRect().centerX() - rect.centerX(), getPreviewRect().centerY() - rect.centerY());
		return rect;
	}

	public boolean undo(LegacyImage image)
	{
		if(!done || !applied) return false;
		done = false;
		return true;
	}

	public boolean redo(LegacyImage image)
	{
		if(done || !applied) return false;
		done = true;
		return true;
	}

	public void applyAction()
	{
		if(!canApplyAction()) return;
		temporaryImage.addHistoryAction(this);
		applied = true;
		temporaryImage = null;
	}

	abstract boolean canApplyAction();

	public Bitmap getActionPreview()
	{
		return previewBitmap;
	}

	boolean isApplied()
	{
		return applied;
	}

	LegacyImage getImage()
	{
		return temporaryImage;
	}

	Bitmap getPreviewBitmap()
	{
		return previewBitmap;
	}

	Canvas getPreviewCanvas()
	{
		return previewCanvas;
	}

	Rect getPreviewRect()
	{
		return previewRect;
	}

	public abstract int getActionName();
}
