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

import android.content.Context
import pl.karol202.paintplus.R
import pl.karol202.paintplus.history.action.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.image.ImageService

class OptionImageFlatten(context: Context,
                         private val imageService: ImageService,
                         private val historyService: HistoryService) : Option
{
	private val flattenedLayerName = context.getString(R.string.flattened)

	private val actionPreset = Action.namePreset(R.string.history_action_image_flatten).withPreview {
		imageService.image.getFlattenedBitmap()
	}

	fun execute() = historyService.commitAction(this::commit)

	private fun commit(): Action.ToRevert = actionPreset.commit {
		val oldImage = imageService.image
		imageService.editImage { flattened(flattenedLayerName) }
		toRevert { revert(oldImage) }
	}

	private fun revert(image: Image): Action.ToCommit = actionPreset.revert {
		imageService.setImage(image)
		toCommit { commit() }
	}
}
