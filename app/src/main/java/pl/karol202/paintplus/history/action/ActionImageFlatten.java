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

import java.util.ArrayList;
import java.util.List;

public class ActionImageFlatten extends Action
{
	private List<Layer> layers;

	public ActionImageFlatten(LegacyImage image)
	{
		super(image);
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
		updateBitmap(image);
		image.deleteAllLayers();
		for(int i = 0; i < layers.size(); i++) image.addLayer(layers.get(i), i);
		return true;
	}

	@Override
	public boolean redo(LegacyImage image)
	{
		if(!super.redo(image)) return false;
		updateBitmap(image);
		image.flattenImage();
		return true;
	}

	@Override
	boolean canApplyAction()
	{
		return layers != null;
	}

	@Override
	public int getActionName()
	{
		return R.string.history_action_image_flatten;
	}

	public void setImageBeforeFlattening(LegacyImage image)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history!");
		this.layers = new ArrayList<>(image.getLayers());
		updateBitmap(getImage());
	}
}
