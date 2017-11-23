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

import android.graphics.Bitmap;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.AppContext;
import pl.karol202.paintplus.color.manipulators.ColorsInvert;
import pl.karol202.paintplus.color.manipulators.params.InvertParams;
import pl.karol202.paintplus.color.manipulators.params.ManipulatorSelection;
import pl.karol202.paintplus.history.action.ActionLayerChange;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.selection.Selection;

public class OptionColorsInvert extends Option
{
	public OptionColorsInvert(AppContext context, Image image)
	{
		super(context, image);
	}
	
	@Override
	public void execute()
	{
		Layer layer = getImage().getSelectedLayer();
		Bitmap bitmapIn = layer.getBitmap();
		Selection selection = getImage().getSelection();
		
		ActionLayerChange action = new ActionLayerChange(getImage(), R.string.history_action_colors_invert);
		action.setLayerChange(getImage().getLayerIndex(layer), layer.getBitmap());
		
		ColorsInvert invert = new ColorsInvert();
		InvertParams params = new InvertParams(ManipulatorSelection.fromSelection(selection, layer.getBounds()));
		Bitmap bitmapOut = invert.run(bitmapIn, params);
		layer.setBitmap(bitmapOut);
		
		action.applyAction();
	}
}