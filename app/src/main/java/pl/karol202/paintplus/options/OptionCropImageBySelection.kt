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

import pl.karol202.paintplus.history.legacyaction.ActionImageCrop
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionCropImageBySelection(private val viewModel: PaintViewModel) : Option
{
	fun execute()
	{
		val image = viewModel.image
		val bounds = image.selection.bounds
		val action = ActionImageCrop(image)
		action.setDataBeforeResizing(image.width, image.height, bounds.left, bounds.top)
		image.resize(bounds.left, bounds.top, bounds.width(), bounds.height())
		image.updateImage()
		action.applyAction()
	}
}
