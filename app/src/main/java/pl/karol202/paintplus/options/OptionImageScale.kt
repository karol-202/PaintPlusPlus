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

import android.util.Size
import pl.karol202.paintplus.R
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.options.Option
import pl.karol202.paintplus.options.OptionScale

class OptionImageScale(private val imageService: ImageService,
                       private val historyService: HistoryService,
                       private val optionScale: OptionScale) : Option
{
	private val actionPreset = Action.namePreset(R.string.history_action_image_scale).withPreview {
		imageService.image.getFlattenedBitmap()
	}

	fun execute() =
			optionScale.execute(R.string.dialog_scale_image, imageService.image.size, this::onSizeSelected)

	private fun onSizeSelected(size: Size, smooth: Boolean)
	{
		historyService.commitAction { commit(size, smooth) }
	}

	private fun commit(size: Size, smooth: Boolean): Action.ToRevert = actionPreset.commit {
		val oldImage = imageService.image
		imageService.editImage { scaled(size.width, size.height, smooth) }
		toRevert { revert(size, smooth, oldImage) }
	}

	private fun revert(size: Size, smooth: Boolean, oldImage: Image): Action.ToCommit = actionPreset.revert {
		imageService.setImage(oldImage)
		toCommit { commit(size, smooth) }
	}
}
