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
package pl.karol202.paintplus.options

import pl.karol202.paintplus.R
import pl.karol202.paintplus.color.manipulators.ColorsInvert
import pl.karol202.paintplus.color.manipulators.params.InvertParams
import pl.karol202.paintplus.color.manipulators.params.ManipulatorSelection
import pl.karol202.paintplus.history.legacyaction.ActionLayerChange
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionColorsInvert(private val viewModel: PaintViewModel) : Option
{
	fun execute()
	{
		val image = viewModel.image
		val layer = image.selectedLayer
		val bitmapIn = layer.bitmap
		val selection = image.selection
		val action = ActionLayerChange(image, R.string.history_action_colors_invert)
		action.setLayerChange(image.getLayerIndex(layer), layer.bitmap)
		val invert = ColorsInvert()
		val params = InvertParams(ManipulatorSelection.fromSelection(selection, layer.bounds))
		val bitmapOut = invert.run(bitmapIn, params)
		layer.bitmap = bitmapOut
		action.applyAction()
	}
}
