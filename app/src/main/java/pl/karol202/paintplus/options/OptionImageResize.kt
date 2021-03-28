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

import android.graphics.Rect
import pl.karol202.paintplus.R
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.util.toRect

class OptionImageResize(private val imageService: ImageService,
                        private val historyService: HistoryService,
                        private val optionResize: OptionResize) : Option
{
	private val actionPreset = Action.namePreset(R.string.history_action_image_resize).withPreview {
		imageService.image.getFlattenedBitmap()
	}

	fun execute() =
			optionResize.execute(R.string.dialog_resize_image, imageService.image.size.toRect(), this::onRectSelected)

	private fun onRectSelected(rect: Rect)
	{
		historyService.commitAction { commit(rect) }
	}

	private fun commit(rect: Rect): Action.ToRevert = actionPreset.commit {
		val oldImage = imageService.image
		imageService.editImage { resized(rect.left, rect.top, rect.width(), rect.height()) }
		toRevert { revert(rect, oldImage) }
	}

	private fun revert(rect: Rect, oldImage: Image): Action.ToCommit = actionPreset.revert {
		imageService.setImage(oldImage)
		toCommit { commit(rect) }
	}
}
