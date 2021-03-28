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

import androidx.appcompat.app.AlertDialog
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogRotateLayerBinding
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.util.MathUtils
import pl.karol202.paintplus.util.setOnValueChangeListener
import pl.karol202.paintplus.viewmodel.PaintViewModel
import kotlin.math.roundToInt

class OptionLayerRotate(private val viewModel: PaintViewModel,
                        private val imageService: ImageService,
                        private val historyService: HistoryService) : Option
{
	private class Dialog(builder: AlertDialog.Builder,
	                     private val onApply: (Float) -> Unit) :
			Option.LayoutDialog<DialogRotateLayerBinding>(builder, DialogRotateLayerBinding::inflate)
	{
		init
		{
			builder.setTitle(R.string.dialog_rotate_layer)
			builder.setPositiveButton(R.string.ok) { _, _ -> onApply() }
			builder.setNegativeButton(R.string.cancel, null)

			views.seekBarAngle.progress = angleToProgress(0f)
			views.seekBarAngle.setOnValueChangeListener { updateAngleText() }

			updateAngleText()
		}

		private fun updateAngleText()
		{
			val angle = progressToAngle(views.seekBarAngle.progress).roundToInt()
			views.textAngle.text = context.getString(R.string.angle, angle)
		}

		private fun onApply() = onApply(progressToAngle(views.seekBarAngle.progress))

		private fun angleToProgress(angle: Float) =
				MathUtils.map(angle, -180f, 180f, 0f, views.seekBarAngle.max.toFloat()).toInt()

		private fun progressToAngle(progress: Int) =
				MathUtils.map(progress.toFloat(), 0f, views.seekBarAngle.max.toFloat(), -180f, 180f)
	}

	private val actionPreset = Action.namePreset(R.string.history_action_layer_rotate)

	fun execute()
	{
		viewModel.showDialog { builder, _ ->
			Dialog(builder, this::onApply)
		}
	}

	private fun onApply(angle: Float)
	{
		if(imageService.image.selectedLayer == null) return
		historyService.commitAction { commit(imageService.image.requireSelectedLayer, angle) }
	}

	private fun commit(oldLayer: Layer, angle: Float): Action.ToRevert
	{
		val newLayer = oldLayer.rotated(angle)
		imageService.editImage { withLayerUpdated(newLayer) }
		return actionPreset.toRevert(oldLayer.bitmap) { revert(oldLayer, angle, newLayer) }
	}

	private fun revert(oldLayer: Layer, angle: Float, newLayer: Layer): Action.ToCommit
	{
		imageService.editImage { withLayerUpdated(oldLayer) }
		return actionPreset.toCommit(newLayer.bitmap) { commit(oldLayer, angle) }
	}
}
