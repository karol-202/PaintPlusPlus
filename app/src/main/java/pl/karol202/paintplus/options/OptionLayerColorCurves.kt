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
import pl.karol202.paintplus.color.curves.ColorChannel
import pl.karol202.paintplus.color.curves.ColorChannel.ColorChannelType
import pl.karol202.paintplus.color.curves.ColorChannelsAdapter
import pl.karol202.paintplus.color.manipulators.CurvesColorsManipulator
import pl.karol202.paintplus.color.manipulators.ColorManipulatorSelection
import pl.karol202.paintplus.databinding.DialogColorCurvesBinding
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.util.SeekBarTouchListener
import pl.karol202.paintplus.util.setOnItemSelectedListener
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionLayerColorCurves(private val viewModel: PaintViewModel,
                             private val imageService: ImageService,
                             private val historyService: HistoryService,
                             private val curvesColorsManipulator: CurvesColorsManipulator) : Option
{
	@SuppressLint("ClickableViewAccessibility")
	private class Dialog(builder: AlertDialog.Builder,
	                     channelType: ColorChannelType,
	                     private val setDialogVisibility: (Boolean) -> Unit,
	                     private val onApply: ((CurvesColorsManipulator.Params) -> Unit) -> Action.ToRevert?,
	                     private val onRevert: (Action.ToRevert) -> Unit) :
			Option.LayoutDialog<DialogColorCurvesBinding>(builder, DialogColorCurvesBinding::inflate)
	{
		private val defaultChannelPosition = when(channelType)
		{
			ColorChannelType.RGB -> 0
			ColorChannelType.HSV -> 2
		}

		private var toRevert: Action.ToRevert? = null

		init
		{
			builder.setTitle(if(channelType == ColorChannelType.RGB) R.string.dialog_color_curves_rgb
			                 else R.string.dialog_color_curves_hsv)
			builder.setPositiveButton(R.string.ok) { _, _ -> apply() }
			builder.setNegativeButton(R.string.cancel) { _, _ -> revert() }

			views.spinnerCurvesChannelIn.adapter = ColorChannelsAdapter(context, channelType)
			views.spinnerCurvesChannelIn.setOnItemSelectedListener { onInChannelSelected() }
			views.spinnerCurvesChannelIn.setSelection(defaultChannelPosition)

			views.spinnerCurvesChannelOut.adapter = ColorChannelsAdapter(context, channelType)
			views.spinnerCurvesChannelOut.setOnItemSelectedListener { onOutChannelSelected() }
			views.spinnerCurvesChannelOut.setSelection(defaultChannelPosition)

			views.colorCurvesView.setOnTouchListener(SeekBarTouchListener())
			views.colorCurvesView.setOnCurveEditListener(this::updateCurvePointText)

			views.colorCurvesView.setChannelType(channelType)
			views.colorCurvesView.setChannelIn(views.spinnerCurvesChannelIn.selectedItem as ColorChannel)
			views.colorCurvesView.setChannelOut(views.spinnerCurvesChannelOut.selectedItem as ColorChannel)

			updateCurvePointText()

			views.buttonCurvesPreview.setOnTouchListener { view, event ->
				if(event.action == MotionEvent.ACTION_DOWN) onPreviewTouchStart(view)
				else if(event.action == MotionEvent.ACTION_UP) onPreviewTouchStop(view)
				true
			}

			views.buttonCurvesRestore.setOnClickListener { restoreCurrentCurve() }
		}

		private fun onInChannelSelected() =
				views.colorCurvesView.setChannelIn(views.spinnerCurvesChannelIn.selectedItem as ColorChannel)

		private fun onOutChannelSelected() =
				views.colorCurvesView.setChannelOut(views.spinnerCurvesChannelOut.selectedItem as ColorChannel)

		private fun updateCurvePointText()
		{
			views.textCurvePoint.text = views.colorCurvesView.infoText
		}

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

		private fun restoreCurrentCurve() = views.colorCurvesView.restoreCurrentCurve()

		private fun apply()
		{
			toRevert = onApply { views.colorCurvesView.attachCurvesToParamsObject(it) }
		}

		private fun revert()
		{
			toRevert?.let(onRevert)
		}
	}

	private val actionPreset = Action.namePreset(R.string.history_action_color_curves)

	fun execute(channelType: ColorChannelType) = viewModel.showDialog { builder, dialogProvider ->
		fun setVisibility(visibility: Boolean) = if(visibility) dialogProvider()?.show() else dialogProvider()?.hide()
		Dialog(builder, channelType, ::setVisibility, { onApply(channelType, it) }, this::onRevert)
	}

	private fun onApply(channelType: ColorChannelType, paramsModifier: (CurvesColorsManipulator.Params) -> Unit): Action.ToRevert?
	{
		if(imageService.image.selectedLayer == null) return null
		return historyService.commitAction { commit(imageService.image.requireSelectedLayer, channelType, paramsModifier) }
	}

	private fun onRevert(toRevert: Action.ToRevert) = historyService.revertAction(toRevert)

	private fun commit(oldLayer: Layer, channelType: ColorChannelType,
	                   paramsModifier: (CurvesColorsManipulator.Params) -> Unit): Action.ToRevert
	{
		val manipulatorSelection = ColorManipulatorSelection.fromSelection(imageService.selection, oldLayer.bounds)
		val params = CurvesColorsManipulator.Params(manipulatorSelection, channelType).also(paramsModifier)
		val newBitmap = curvesColorsManipulator.run(oldLayer.bitmap, params)
		val newLayer = oldLayer.withBitmap(newBitmap)
		imageService.editImage { withLayerUpdated(newLayer) }
		return actionPreset.toRevert(oldLayer.bitmap) { revert(oldLayer, channelType, paramsModifier, newLayer) }
	}

	private fun revert(oldLayer: Layer, channelType: ColorChannelType,
	                   paramsModifier: (CurvesColorsManipulator.Params) -> Unit, newLayer: Layer): Action.ToCommit
	{
		imageService.editImage { withLayerUpdated(oldLayer) }
		return actionPreset.toCommit(newLayer.bitmap) { commit(oldLayer, channelType, paramsModifier) }
	}
}
