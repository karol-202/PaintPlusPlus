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

import android.content.Context;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.ActionLayerResize;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;

public class OptionLayerResize extends OptionResize
{
	private Layer layer;
	
	public OptionLayerResize(Context context, Image image)
	{
		super(context, image);
		this.layer = image.getSelectedLayer();
	}
	
	@Override
	protected int getTitle()
	{
		return R.string.dialog_resize_layer;
	}
	
	@Override
	protected int getObjectWidth()
	{
		return layer.getWidth();
	}
	
	@Override
	protected int getObjectHeight()
	{
		return layer.getHeight();
	}
	
	@Override
	protected int getOldObjectWidth()
	{
		return layer.getWidth();
	}
	
	@Override
	protected int getOldObjectHeight()
	{
		return layer.getHeight();
	}
	
	@Override
	protected int getObjectX()
	{
		return 0;
	}
	
	@Override
	protected int getObjectY()
	{
		return 0;
	}
	
	@Override
	protected void applySize(int x, int y, int width, int height)
	{
		ActionLayerResize action = new ActionLayerResize(image);
		action.setLayerBeforeResize(layer);
		
		layer.resize(x, y, width, height);
		
		action.applyAction();
	}
}