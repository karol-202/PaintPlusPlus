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

import android.graphics.Color;
import android.graphics.RectF;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.LegacyImage;

public class ActionImageResize extends Action
{
	private int width;
	private int height;
	private int resizingDeltaX;
	private int resizingDeltaY;

	public ActionImageResize(LegacyImage image)
	{
		super(image);
		width = -1;
		height = -1;
	}

	private void updateBitmap(LegacyImage image)
	{
		getPreviewBitmap().eraseColor(Color.TRANSPARENT);
		getPreviewCanvas().drawBitmap(image.getFullImage(), null, transformImageRect(image), null);
	}

	private RectF transformImageRect(LegacyImage image)
	{
		float max = Math.max(image.getWidth(), image.getHeight());
		float ratio = getPreviewRect().width() / max;
		RectF rect = new RectF(0, 0, image.getWidth() * ratio, image.getHeight() * ratio);
		rect.offset(getPreviewRect().centerX() - rect.centerX(), getPreviewRect().centerY() - rect.centerY());
		return rect;
	}

	@Override
	public boolean undo(LegacyImage image)
	{
		if(!super.undo(image)) return false;
		int newWidth = image.getWidth();
		int newHeight = image.getHeight();
		image.resize(-resizingDeltaX, -resizingDeltaY, width, height);
		width = newWidth;
		height = newHeight;
		return true;
	}

	@Override
	public boolean redo(LegacyImage image)
	{
		if(!super.redo(image)) return false;
		int oldWidth = image.getWidth();
		int oldHeight = image.getHeight();
		image.resize(resizingDeltaX, resizingDeltaY, width, height);
		width = oldWidth;
		height = oldHeight;
		return true;
	}

	@Override
	boolean canApplyAction()
	{
		return width != -1 && height != -1 && (width != getImage().getWidth() || height != getImage().getHeight() ||
											   resizingDeltaX != 0 || resizingDeltaY != 0);
	}

	@Override
	public int getActionName()
	{
		return R.string.history_action_image_resize;
	}

	public void setDataBeforeResizing(int oldWidth, int oldHeight, int resizingDeltaX, int resizingDeltaY)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history!");
		this.width = oldWidth;
		this.height = oldHeight;
		this.resizingDeltaX = resizingDeltaX;
		this.resizingDeltaY = resizingDeltaY;
		updateBitmap(getImage());
	}
}
