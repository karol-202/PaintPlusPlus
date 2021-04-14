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

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import pl.karol202.paintplus.R
import pl.karol202.paintplus.color.manipulators.BrightnessColorManipulator
import pl.karol202.paintplus.color.manipulators.ColorManipulatorSelection
import pl.karol202.paintplus.databinding.DialogColorsBrightnessBinding
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.EffectsService
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.util.MathUtils
import pl.karol202.paintplus.util.setOnValueChangeListener
import pl.karol202.paintplus.viewmodel.PaintViewModel
import java.util.*

class OptionLayerColorBrightness(private val imageService: ImageService,
                                 private val historyService: HistoryService,
                                 private val effectsService: EffectsService,
                                 private val brightnessColorManipulator: BrightnessColorManipulator) : Option
{
	@SuppressLint("ClickableViewAccessibility")
	private class Dialog(builder: AlertDialog.Builder,
	                     private val setDialogVisibility: (Boolean) -> Unit,
	                     private val onApply: (Float, Float) -> Action.ToRevert?,
	                     private val onRevert: (Action.ToRevert) -> Unit) :
			Option.LayoutDialog<DialogColorsBrightnessBinding>(builder, DialogColorsBrightnessBinding::inflate)
	{
		private var toRevert: Action.ToRevert? = null

		init
		{
			builder.setTitle(R.string.dialog_colors_brightness)
			builder.setPositiveButton(R.string.ok) { _, _ -> apply() }
			builder.setNegativeButton(R.string.cancel) { _, _ -> revert() }

			views.seekBarBrightness.setOnValueChangeListener { updateBrightnessText() }
			views.seekBarContrast.setOnValueChangeListener { updateContrastText() }

			updateBrightnessText()
			updateContrastText()

			views.buttonPreview.setOnTouchListener { view, event ->
				if(event.action == MotionEvent.ACTION_DOWN) onPreviewTouchStart(view)
				else if(event.action == MotionEvent.ACTION_UP) onPreviewTouchStop(view)
				true
			}
		}

		private fun updateBrightnessText()
		{
			views.textBrightness.text = formatText(views.seekBarBrightness.progress)
		}

		private fun updateContrastText()
		{
			views.textContrast.text = formatText(views.seekBarContrast.progress)
		}

		private fun formatText(seekBarValue: Int) = String.format(Locale.US, "%1\$d%%", seekBarValue - 100)

		private fun onPreviewTouchStart(view: View)
		{
			apply()
			setDialogVisibility(false)
			view.parent.requestDisallowInterceptTouchEvent(true)
		}

		private fun onPreviewTouchStop(view: View)
		{
			revert()
			setDialogVisibility(true)
			view.parent.requestDisallowInterceptTouchEvent(false)
		}

		private fun apply()
		{
			val brightness = MathUtils.map(views.seekBarBrightness.progress.toFloat(), 0f, 200f, -1f, 1f)
			val contrast = MathUtils.map(views.seekBarContrast.progress.toFloat(), 0f, 200f, -1f, 1f)
			toRevert = onApply(brightness, contrast)
		}

		private fun revert()
		{
			toRevert?.let(onRevert)
		}
	}

	private val actionPreset = Action.namePreset(R.string.history_action_brightness)

	fun execute() = effectsService.showDialog { builder, dialogProvider ->
		fun setVisibility(visibility: Boolean) = if(visibility) dialogProvider()?.show() else dialogProvider()?.hide()
		Dialog(builder, ::setVisibility, this::onApply, this::onRevert)
	}

	private fun onApply(brightness: Float, contrast: Float): Action.ToRevert?
	{
		if(imageService.image.selectedLayer == null) return null
		return historyService.commitAction { commit(imageService.image.requireSelectedLayer, brightness, contrast) }
	}

	private fun onRevert(toRevert: Action.ToRevert) = historyService.revertAction(toRevert)

	private fun commit(oldLayer: Layer, brightness: Float, contrast: Float): Action.ToRevert
	{
		val manipulatorSelection = ColorManipulatorSelection.fromSelection(imageService.selection, oldLayer.bounds)
		val params = BrightnessColorManipulator.Params(manipulatorSelection, brightness, contrast)
		val newBitmap = brightnessColorManipulator.run(oldLayer.bitmap, params)
		val newLayer = oldLayer.withBitmap(newBitmap)
		imageService.editImage { withLayerUpdated(newLayer) }
		return actionPreset.toRevert(oldLayer.bitmap) { revert(oldLayer, brightness, contrast, newLayer) }
	}

	private fun revert(oldLayer: Layer, brightness: Float, contrast: Float, newLayer: Layer): Action.ToCommit
	{
		imageService.editImage { withLayerUpdated(oldLayer) }
		return actionPreset.toCommit(newLayer.bitmap) { commit(oldLayer, brightness, contrast) }
	}
}
