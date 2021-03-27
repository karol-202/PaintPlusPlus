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
import pl.karol202.paintplus.image.FlipDirection
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService

class OptionImageFlip(private val imageService: ImageService,
                      private val historyService: HistoryService,
                      private val optionFlip: OptionFlip) : Option
{
	private val actionPreset = Action.namePreset(R.string.history_action_image_flip).withPreview {
		imageService.image.getFlattenedBitmap()
	}

	fun execute() = optionFlip.execute(R.string.dialog_flip_image, this::onDirectionSelected)

	private fun onDirectionSelected(direction: FlipDirection) = historyService.commitAction { commit(direction) }

	private fun commit(direction: FlipDirection): Action.ToRevert = actionPreset.commit {
		flipImage(direction)
		toRevert { revert(direction) }
	}

	private fun revert(direction: FlipDirection): Action.ToCommit = actionPreset.revert {
		flipImage(direction)
		toCommit { commit(direction) }
	}

	private fun flipImage(direction: FlipDirection) = imageService.editImage { flipped(direction) }
}
