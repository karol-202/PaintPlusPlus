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
import android.graphics.Color;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.LegacyImage;
import pl.karol202.paintplus.image.layer.Layer;

public class ActionLayerPropertiesChange extends Action
{
	private int layerId;
	private LegacyLayerMode mode;
	private float opacity;

	public ActionLayerPropertiesChange(LegacyImage image)
	{
		super(image);
		this.layerId = -1;
	}

	private void updateBitmap(LegacyImage image)
	{
		Bitmap layerBitmap = image.getLayerAtIndex(layerId).getBitmap();
		getPreviewBitmap().eraseColor(Color.TRANSPARENT);
		getPreviewCanvas().drawBitmap(layerBitmap, null, transformLayerRect(layerBitmap), null);
	}

	@Override
	public boolean undo(LegacyImage image)
	{
		if(!super.undo(image)) return false;
		Layer layer = image.getLayerAtIndex(layerId);

		LegacyLayerMode newMode = layer.getMode();
		float newOpacity = layer.getOpacity();

		layer.setMode(mode);
		layer.setOpacity(opacity);

		mode = newMode;
		opacity = newOpacity;

		return true;
	}

	@Override
	public boolean redo(LegacyImage image)
	{
		if(!super.redo(image)) return false;
		Layer layer = image.getLayerAtIndex(layerId);

		LegacyLayerMode oldMode = layer.getMode();
		float oldOpacity = layer.getOpacity();

		layer.setMode(mode);
		layer.setOpacity(opacity);

		mode = oldMode;
		opacity = oldOpacity;

		return true;
	}

	@Override
	boolean canApplyAction()
	{
		Layer layer = getImage().getLayerAtIndex(layerId);
		return layerId != -1 && (mode != layer.getMode() || opacity != layer.getOpacity());
	}

	@Override
	public int getActionName()
	{
		return R.string.history_action_layer_properties_change;
	}

	public void setLayerBeforeChange(Layer layer)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history.");
		this.layerId = getImage().getLayerIndex(layer);
		this.mode = layer.getMode();
		this.opacity = layer.getOpacity();
		updateBitmap(getImage());
	}
}
