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

package pl.karol202.paintplus.options;

import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.AppContext;
import pl.karol202.paintplus.history.action.ActionLayerFlip;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.Image.FlipDirection;
import pl.karol202.paintplus.image.layer.Layer;

public class OptionLayerFlip extends OptionFlip
{
	private Layer layer;
	
	public OptionLayerFlip(AppContext context, Image image)
	{
		super(context, image);
		this.layer = image.getSelectedLayer();
	}
	
	@Override
	protected int getTitle()
	{
		return R.string.dialog_flip_layer;
	}
	
	@Override
	protected void flip(FlipDirection direction)
	{
		ActionLayerFlip action = new ActionLayerFlip(getImage());
		action.setLayerAndFlipDirection(getImage().getLayerIndex(layer), direction);
		
		layer.flip(direction);
		
		action.applyAction();
	}
}