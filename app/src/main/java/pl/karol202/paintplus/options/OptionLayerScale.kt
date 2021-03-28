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
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.options.Option
import pl.karol202.paintplus.options.OptionResize
import pl.karol202.paintplus.options.OptionScale
import pl.karol202.paintplus.util.toRect

class OptionLayerScale(private val imageService: ImageService,
                       private val historyService: HistoryService,
                       private val optionScale: OptionScale) : Option
{
	private val actionPreset = Action.namePreset(R.string.history_action_layer_scale)

	fun execute()
	{
		val selectedLayer = imageService.image.selectedLayer ?: return
		optionScale.execute(R.string.dialog_scale_layer, imageService.image.size) { size, smooth ->
			onResizeSelected(selectedLayer, size, smooth)
		}
	}

	private fun onResizeSelected(layer: Layer, size: Size, smooth: Boolean)
	{
		historyService.commitAction { commit(layer, size, smooth) }
	}

	private fun commit(oldLayer: Layer, size: Size, smooth: Boolean): Action.ToRevert
	{
		val newLayer = oldLayer.scaled(size.width, size.height, smooth)
		imageService.editImage { withLayerUpdated(newLayer) }
		return actionPreset.toRevert(oldLayer.bitmap) { revert(oldLayer, size, smooth, newLayer) }
	}

	private fun revert(oldLayer: Layer, size: Size, smooth: Boolean, newLayer: Layer): Action.ToCommit
	{
		imageService.editImage { withLayerUpdated(oldLayer) }
		return actionPreset.toCommit(newLayer.bitmap) { commit(oldLayer, size, smooth) }
	}
}
