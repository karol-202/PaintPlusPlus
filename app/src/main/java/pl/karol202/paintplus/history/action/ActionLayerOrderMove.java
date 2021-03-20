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
import pl.karol202.paintplus.image.layer.Layer;

public class ActionLayerOrderMove extends Action
{
	private int sourceLayerPos;
	private int destinationLayerPos;

	public ActionLayerOrderMove(LegacyImage image)
	{
		super(image);
		this.sourceLayerPos = -1;
		this.destinationLayerPos = -1;
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
		Layer layer = image.getLayerAtIndex(destinationLayerPos);
		image.deleteLayer(layer);

		image.addLayer(layer, sourceLayerPos);
		return true;
	}

	@Override
	public boolean redo(LegacyImage image)
	{
		if(!super.redo(image)) return false;
		Layer layer = image.getLayerAtIndex(sourceLayerPos);
		image.deleteLayer(layer);

		image.addLayer(layer, destinationLayerPos);
		return true;
	}

	@Override
	boolean canApplyAction()
	{
		return sourceLayerPos != -1 && destinationLayerPos != -1 && sourceLayerPos != destinationLayerPos;
	}

	@Override
	public int getActionName()
	{
		return R.string.history_action_layer_order_move;
	}

	public void setSourceAndDestinationLayerPos(int sourcePos, int destinationPos)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history.");
		this.sourceLayerPos = sourcePos;
		this.destinationLayerPos = destinationPos;
		updateBitmap(getImage());
	}
}
