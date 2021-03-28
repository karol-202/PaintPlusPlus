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
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Size
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.applyCanvas
import androidx.core.widget.addTextChangedListener
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogNewLayerBinding
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.util.GraphicsHelper
import pl.karol202.paintplus.util.MathUtils.map
import pl.karol202.paintplus.util.size
import pl.karol202.paintplus.viewmodel.PaintViewModel
import kotlin.math.max
import kotlin.math.min

class OptionLayerNew(private val viewModel: PaintViewModel,
                     private val imageService: ImageService,
                     private val historyService: HistoryService) : Option
{
	private data class NewLayerProps(val x: Int,
	                                 val y: Int,
	                                 val width: Int,
	                                 val height: Int,
	                                 val name: String)

	@SuppressLint("ClickableViewAccessibility")
	private class Dialog(builder: AlertDialog.Builder,
	                     defaultName: String,
	                     private val imageSize: Size,
	                     private val onApply: (NewLayerProps) -> Unit) :
			Option.LayoutDialog<DialogNewLayerBinding>(builder, DialogNewLayerBinding::inflate)
	{
		private val previewOldPaint = Paint().apply {
			color = Color.argb(255, 255, 255, 141)
		}
		private val previewNewPaint = Paint().apply {
			color = Color.argb(204, 27, 124, 209)
		}

		init
		{
			builder.setTitle(R.string.dialog_new_layer)
			builder.setPositiveButton(R.string.ok) { _, _ -> onApply() }
			builder.setNegativeButton(R.string.cancel, null)

			views.editLayerName.setText(defaultName)

			views.editLayerWidth.setText(imageSize.width.toString())
			views.editLayerWidth.addTextChangedListener { updatePreview() }

			views.editLayerHeight.setText(imageSize.height.toString())
			views.editLayerHeight.addTextChangedListener { updatePreview() }

			views.editLayerX.setText("0")
			views.editLayerX.addTextChangedListener { updatePreview() }

			views.editLayerY.setText("0")
			views.editLayerY.addTextChangedListener { updatePreview() }
		}

		private fun updatePreview()
		{
			val previewSize = views.imageSizePreview.size
			Bitmap.createBitmap(previewSize.width, previewSize.height, Bitmap.Config.ARGB_8888).applyCanvas {
				val layerX = getValue(views.editLayerX)
				val layerY = getValue(views.editLayerY)
				val layerWidth = getValue(views.editLayerWidth)
				val layerHeight = getValue(views.editLayerHeight)
				val left = min(0, layerX)
				val top = min(0, layerY)
				val right = max(imageSize.width, layerWidth + layerX)
				val bottom = max(imageSize.height, layerHeight + layerY)
				val min = min(left, top).toFloat()
				val max = max(right, bottom).toFloat()
				val previewSizeMax = max(previewSize.width, previewSize.height).toFloat()
				val oldLeft = map(0f, min, max, 0f, previewSizeMax)
				val oldTop = map(0f, min, max, 0f, previewSizeMax)
				val oldRight = map(imageSize.width.toFloat(), min, max, 0f, previewSizeMax)
				val oldBottom = map(imageSize.height.toFloat(), min, max, 0f, previewSizeMax)
				val oldRect = RectF(oldLeft, oldTop, oldRight, oldBottom)
				val newLeft = map(layerX.toFloat(), min, max, 0f, previewSizeMax)
				val newTop = map(layerY.toFloat(), min, max, 0f, previewSizeMax)
				val newRight = map((layerX + layerWidth).toFloat(), min, max, 0f, previewSizeMax)
				val newBottom = map((layerY + layerHeight).toFloat(), min, max, 0f, previewSizeMax)
				val newRect = RectF(newLeft, newTop, newRight, newBottom)
				drawRect(oldRect, previewOldPaint)
				drawRect(newRect, previewNewPaint)
			}.let { views.imageSizePreview.setImageBitmap(it) }
		}

		// TODO Add validation feedback (error indicator)
		private fun onApply()
		{
			val layerX = getValue(views.editLayerX)
			val layerY = getValue(views.editLayerY)
			val layerWidth = getValue(views.editLayerWidth)
			val layerHeight = getValue(views.editLayerHeight)
			val layerName = views.editLayerName.text.toString()
			if(layerWidth == 0 || layerHeight == 0 ||
					layerWidth > GraphicsHelper.maxTextureSize || layerHeight > GraphicsHelper.maxTextureSize) return
			onApply(NewLayerProps(layerX, layerY, layerWidth, layerHeight, layerName))
		}

		private fun getValue(editText: EditText) = editText.text?.toString()?.toIntOrNull() ?: 0
	}

	private val actionPreviewBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
	private val actionPreset = Action.namePreset(R.string.history_action_layer_add).withPreview { actionPreviewBitmap }

	// TODO Call updatePreview() as soon as the dialog is visible on the screen
	fun execute() = viewModel.showDialog { builder, _ ->
		Dialog(builder, imageService.defaultLayerName, imageService.image.size, this::onApply)
	}

	private fun onApply(newLayerProps: NewLayerProps)
	{
		if(!imageService.image.canAddMoreLayers) return
		historyService.commitAction { commit(newLayerProps) }
	}

	private fun commit(props: NewLayerProps): Action.ToRevert = actionPreset.commit {
		val oldImage = imageService.image
		val newLayer = Layer.create(props.x, props.y, props.name, props.width, props.height, Color.TRANSPARENT)
		imageService.editImage { withLayerUpdated(newLayer) }
		toRevert { revert(props, oldImage) }
	}

	private fun revert(newLayerProps: NewLayerProps, oldImage: Image): Action.ToCommit = actionPreset.revert {
		imageService.setImage(oldImage)
		toCommit { commit(newLayerProps) }
	}
}
