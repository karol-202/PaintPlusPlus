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
import pl.karol202.paintplus.history.action.ActionImageResize;
import pl.karol202.paintplus.image.Image;

public class OptionImageResize extends OptionResize
{
	public OptionImageResize(AppContext context, Image image)
	{
		super(context, image);
	}
	
	@Override
	protected int getTitle()
	{
		return R.string.dialog_resize_image;
	}
	
	@Override
	protected int getObjectWidth()
	{
		return getImage().getWidth();
	}
	
	@Override
	protected int getObjectHeight()
	{
		return getImage().getHeight();
	}
	
	@Override
	protected int getOldObjectWidth()
	{
		return getImage().getWidth();
	}
	
	@Override
	protected int getOldObjectHeight()
	{
		return getImage().getHeight();
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
		ActionImageResize action = new ActionImageResize(getImage());
		action.setDataBeforeResizing(getImage().getWidth(), getImage().getHeight(), x, y);
		
		getImage().resize(x, y, width, height);
		
		action.applyAction();
	}
}