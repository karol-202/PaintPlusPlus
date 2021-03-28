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
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer

class OptionLayerFitToImage(private val imageService: ImageService,
                            private val historyService: HistoryService) : Option
{
	private val actionPreset = Action.namePreset(R.string.history_action_layer_resize)

	fun execute()
	{
		if(imageService.image.selectedLayer == null) return
		historyService.commitAction { commit(imageService.image.requireSelectedLayer) }
	}

	private fun commit(layer: Layer): Action.ToRevert
	{
		val oldImage = imageService.image
		val newLayer = layer.resized(-layer.x, -layer.y, oldImage.width, oldImage.height)
		imageService.editImage { withLayerUpdated(newLayer) }
		return actionPreset.toRevert(layer.bitmap) { revert(layer, oldImage) }
	}

	private fun revert(layer: Layer, oldImage: Image): Action.ToCommit
	{
		imageService.setImage(oldImage)
		return actionPreset.toCommit(layer.bitmap) { commit(layer) }
	}
}
