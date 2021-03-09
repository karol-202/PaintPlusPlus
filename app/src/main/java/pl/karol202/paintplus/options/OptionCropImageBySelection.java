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

import android.graphics.Rect;
import pl.karol202.paintplus.history.action.ActionImageCrop;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.legacy.AppContextLegacy;
import pl.karol202.paintplus.legacy.OptionLegacy;

public class OptionCropImageBySelection extends OptionLegacy
{
	public OptionCropImageBySelection(AppContextLegacy context, Image image)
	{
		super(context, image);
	}

	@Override
	public void execute()
	{
		Image image = getImage();
		Rect bounds = image.getSelection().getBounds();

		ActionImageCrop action = new ActionImageCrop(image);
		action.setDataBeforeResizing(image.getWidth(), image.getHeight(), bounds.left, bounds.top);

		image.resize(bounds.left, bounds.top, bounds.width(), bounds.height());
		getImage().updateImage();

		action.applyAction();
	}
}
