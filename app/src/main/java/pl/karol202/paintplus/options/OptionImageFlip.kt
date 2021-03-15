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
import pl.karol202.paintplus.history.action.ActionImageFlip
import pl.karol202.paintplus.image.Image.FlipDirection
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionImageFlip(private val viewModel: PaintViewModel) : Option
{
	private val optionFlip = OptionFlip(viewModel, R.string.dialog_flip_image, this::flip)

	fun execute() = optionFlip.execute()

	private fun flip(direction: FlipDirection)
	{
		val action = ActionImageFlip(viewModel.image)
		action.setDirectionBeforeFlip(direction)
		viewModel.image.flip(direction)
		action.applyAction()
		viewModel.image.updateImage()
	}
}
