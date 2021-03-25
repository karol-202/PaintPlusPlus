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
import pl.karol202.paintplus.history.legacyaction.ActionLayerCrop
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer

class OptionCropLayerBySelection(private val imageService: ImageService,
                                 private val historyService: HistoryService) : Option
{
	private val actionPreset = Action.Preset(R.string.history_action_layer_crop) {
		imageService.image.requireSelectedLayer.bitmap
	}

	fun execute() = historyService.commitAction(this::commit)

	private fun commit(): Action.ToRevert = actionPreset.commit {
		val oldLayer = imageService.image.requireSelectedLayer
		val bounds = imageService.selection.bounds
		imageService.editImage {
			withSelectedLayerUpdated(oldLayer.resized(bounds.left, bounds.top, bounds.width(), bounds.height()))
		}
		toRevert { revert(oldLayer) }
	}

	private fun revert(oldLayer: Layer): Action.ToCommit = actionPreset.revert {
		imageService.editImage { withSelectedLayerUpdated(oldLayer) }
		toCommit { commit() }
	}
}
