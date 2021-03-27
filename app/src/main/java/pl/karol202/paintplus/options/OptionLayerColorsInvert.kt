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
import pl.karol202.paintplus.color.manipulators.ColorsInvert
import pl.karol202.paintplus.color.manipulators.params.InvertParams
import pl.karol202.paintplus.color.manipulators.params.ManipulatorSelection
import pl.karol202.paintplus.history.action.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer

class OptionLayerColorsInvert(private val imageService: ImageService,
                              private val historyService: HistoryService) : Option
{
	private val actionPreset = Action.namePreset(R.string.history_action_colors_invert)

	fun execute()
	{
		if(imageService.image.selectedLayer == null) return
		historyService.commitAction { commit(imageService.image.requireSelectedLayer) }
	}

	private fun commit(oldLayer: Layer): Action.ToRevert
	{
		val newLayer = invertColors(oldLayer)
		return actionPreset.toRevert(oldLayer.bitmap) { revert(newLayer) }
	}

	private fun revert(newLayer: Layer): Action.ToCommit
	{
		val oldLayer = invertColors(newLayer)
		return actionPreset.toCommit(newLayer.bitmap) { commit(oldLayer) }
	}

	private fun invertColors(sourceLayer: Layer): Layer
	{
		val params = InvertParams(ManipulatorSelection.fromSelection(imageService.selection, sourceLayer.bounds))
		val newBitmap = ColorsInvert().run(sourceLayer.bitmap, params)
		val newLayer = sourceLayer.withBitmap(newBitmap)
		imageService.editImage { withLayerUpdated(newLayer) }
		return newLayer
	}
}
