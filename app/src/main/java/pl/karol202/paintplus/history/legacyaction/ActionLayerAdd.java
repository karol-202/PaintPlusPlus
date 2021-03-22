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

package pl.karol202.paintplus.history.legacyaction;

import android.graphics.Bitmap;
import android.graphics.Color;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.LegacyImage;
import pl.karol202.paintplus.image.layer.Layer;

public class ActionLayerAdd extends LegacyAction
{
	private Layer layer;
	private int layerPosition;

	public ActionLayerAdd(LegacyImage image)
	{
		super(image);
	}

	private void updateBitmap()
	{
		Bitmap layerBitmap = layer.getBitmap();
		getPreviewBitmap().eraseColor(Color.TRANSPARENT);
		getPreviewCanvas().drawBitmap(layerBitmap, null, transformLayerRect(layerBitmap), null);
	}

	@Override
	public boolean undo(LegacyImage image)
	{
		if(!super.undo(image)) return false;
		image.deleteLayer(layer);
		return true;
	}

	@Override
	public boolean redo(LegacyImage image)
	{
		if(!super.redo(image)) return false;
		image.addLayer(layer, layerPosition);
		return true;
	}

	@Override
	boolean canApplyAction()
	{
		return layer != null;
	}

	@Override
	public int getActionName()
	{
		return R.string.history_action_layer_add;
	}

	public void setLayerAfterAdding(Layer layer)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history.");
		this.layer = layer;
		this.layerPosition = getImage().getLayerIndex(layer);
		updateBitmap();
	}
}
