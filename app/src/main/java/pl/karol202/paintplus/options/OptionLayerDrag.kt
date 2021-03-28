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

import android.graphics.Point
import androidx.appcompat.app.AlertDialog
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogDragLayerBinding
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionLayerDrag(private val viewModel: PaintViewModel,
                      private val imageService: ImageService,
                      private val historyService: HistoryService) : Option
{
	private class Dialog(builder: AlertDialog.Builder,
	                     initialPosition: Point,
	                     private val onApply: (Point) -> Unit) :
			Option.LayoutDialog<DialogDragLayerBinding>(builder, DialogDragLayerBinding::inflate)
	{
		init
		{
			builder.setTitle(R.string.dialog_drag_layer)
			builder.setPositiveButton(R.string.ok) { _, _ -> onApply() }
			builder.setNegativeButton(R.string.cancel, null)

			views.editLayerOffsetX.setText(initialPosition.x.toString())
			views.editLayerOffsetY.setText(initialPosition.y.toString())
		}

		// TODO Add validation feedback (error indicator)
		private fun onApply()
		{
			val x = views.editLayerOffsetX.text.toString().toIntOrNull() ?: 0
			val y = views.editLayerOffsetY.text.toString().toIntOrNull() ?: 0
			onApply(Point(x, y))
		}
	}

	private val actionPreset = Action.namePreset(R.string.history_action_layer_drag)

	fun execute()
	{
		val selectedLayer = imageService.image.selectedLayer ?: return
		viewModel.showDialog { builder, _ ->
			Dialog(builder, selectedLayer.position) { onApply(selectedLayer, it) }
		}
	}

	private fun onApply(layer: Layer, position: Point)
	{
		historyService.commitAction { commit(layer, position) }
	}

	private fun commit(oldLayer: Layer, position: Point): Action.ToRevert
	{
		val newLayer = oldLayer.withPosition(position.x, position.y)
		imageService.editImage { withLayerUpdated(newLayer) }
		return actionPreset.toRevert(oldLayer.bitmap) { revert(oldLayer, position) }
	}

	private fun revert(oldLayer: Layer, position: Point): Action.ToCommit
	{
		imageService.editImage { withLayerUpdated(oldLayer) }
		return actionPreset.toCommit(oldLayer.bitmap) { commit(oldLayer, position) }
	}
}
