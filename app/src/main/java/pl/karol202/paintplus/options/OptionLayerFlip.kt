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
import pl.karol202.paintplus.history.action.ActionLayerFlip
import pl.karol202.paintplus.image.LegacyImage.FlipDirection
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionLayerFlip(private val viewModel: PaintViewModel) : Option
{
	private val optionFlip = OptionFlip(viewModel, R.string.dialog_flip_layer, this::flip)

	fun execute() = optionFlip.execute()

	private fun flip(direction: FlipDirection)
	{
		val action = ActionLayerFlip(viewModel.image)
		action.setLayerAndFlipDirection(viewModel.image.selectedLayerIndex, direction)
		viewModel.image.selectedLayer.flip(direction)
		action.applyAction()
		viewModel.image.updateImage()
	}
}
