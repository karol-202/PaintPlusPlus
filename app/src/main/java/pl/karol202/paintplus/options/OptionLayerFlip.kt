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
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.FlipDirection
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer

class OptionLayerFlip(private val imageService: ImageService,
                      private val historyService: HistoryService,
                      private val optionFlip: OptionFlip) : Option
{
	private val actionPreset = Action.namePreset(R.string.history_action_layer_flip)

	fun execute() = optionFlip.execute(R.string.dialog_flip_layer, this::onDirectionSelected)

	private fun onDirectionSelected(direction: FlipDirection)
	{
		if(imageService.image.selectedLayer == null) return
		historyService.commitAction { commit(imageService.image.requireSelectedLayer, direction) }
	}

	private fun commit(oldLayer: Layer, direction: FlipDirection): Action.ToRevert
	{
		val newLayer = flipLayer(oldLayer, direction)
		return actionPreset.toRevert(oldLayer.bitmap) { revert(oldLayer, newLayer, direction) }
	}

	private fun revert(oldLayer: Layer, newLayer: Layer, direction: FlipDirection): Action.ToCommit
	{
		imageService.editImage { withLayerUpdated(oldLayer) }
		return actionPreset.toCommit(newLayer.bitmap) { commit(oldLayer, direction) }
	}

	private fun flipLayer(sourceLayer: Layer, direction: FlipDirection): Layer
	{
		val newLayer = sourceLayer.flipped(direction)
		imageService.editImage { withLayerUpdated(newLayer) }
		return newLayer
	}
}
