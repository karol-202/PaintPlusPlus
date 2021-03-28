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
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.options.Option
import pl.karol202.paintplus.options.OptionResize
import pl.karol202.paintplus.util.toRect

class OptionLayerResize(private val imageService: ImageService,
                        private val historyService: HistoryService,
                        private val optionResize: OptionResize) : Option
{
	private val actionPreset = Action.namePreset(R.string.history_action_layer_resize)

	fun execute()
	{
		val selectedLayer = imageService.image.selectedLayer ?: return
		optionResize.execute(R.string.dialog_resize_layer, imageService.image.size.toRect()) { onResizeSelected(selectedLayer, it) }
	}

	private fun onResizeSelected(layer: Layer, rect: Rect)
	{
		historyService.commitAction { commit(layer, rect) }
	}

	private fun commit(oldLayer: Layer, rect: Rect): Action.ToRevert
	{
		val newLayer = oldLayer.resized(rect.left, rect.top, rect.width(), rect.height())
		imageService.editImage { withLayerUpdated(newLayer) }
		return actionPreset.toRevert(oldLayer.bitmap) { revert(oldLayer, rect, newLayer) }
	}

	private fun revert(oldLayer: Layer, rect: Rect, newLayer: Layer): Action.ToCommit
	{
		imageService.editImage { withLayerUpdated(oldLayer) }
		return actionPreset.toCommit(newLayer.bitmap) { commit(oldLayer, rect) }
	}
}
