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
import pl.karol202.paintplus.history.action.Action
import pl.karol202.paintplus.history.legacyaction.ActionImageCrop
import pl.karol202.paintplus.image.*
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionCropImageBySelection(private val imageService: ImageService,
                                 private val viewService: ViewService,
                                 private val historyService: HistoryService,
                                 private val clipboardService: ClipboardService) : Option
{
	private val actionPreset = Action.Preset(R.string.history_action_image_crop) { imageService.image.getFlattenedBitmap() }

	fun execute() = historyService.commitAction(this::commit)

	// TODO Consider creating action that won't expand the size of the layer
	private fun commit(): Action.ToRevert = actionPreset.commit {
		val oldImage = imageService.image
		val bounds = imageService.selection.bounds
		imageService.editImage { resized(bounds.left, bounds.top, bounds.width(), bounds.height()) }
		imageService.editSelection { translated(-bounds.left, -bounds.top) }
		clipboardService.offsetClipboard(-bounds.left, -bounds.top)
		viewService.offsetView(-bounds.left, -bounds.top)
		toRevert { revert(oldImage, bounds.left, bounds.top) }
	}

	private fun revert(oldImage: Image, xOffset: Int, yOffset: Int): Action.ToCommit = actionPreset.revert {
		imageService.editImage { oldImage }
		imageService.editSelection { translated(xOffset, yOffset) }
		clipboardService.offsetClipboard(xOffset, yOffset)
		viewService.offsetView(xOffset, yOffset)
		toCommit { commit() }
	}
}
