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
import android.graphics.*
import android.util.Size
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.toRectF
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogNewLayerBinding
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.util.*
import pl.karol202.paintplus.util.MathUtils.map
import pl.karol202.paintplus.viewmodel.PaintViewModel
import kotlin.math.max
import kotlin.math.min

class OptionLayerNew(private val viewModel: PaintViewModel,
                     private val imageService: ImageService,
                     private val historyService: HistoryService) : Option
{
	@SuppressLint("ClickableViewAccessibility")
	private class Dialog(builder: AlertDialog.Builder,
	                     defaultName: String,
	                     private val imageSize: Size,
	                     private val onApply: (Rect, String) -> Unit) :
			Option.LayoutDialog<DialogNewLayerBinding>(builder, DialogNewLayerBinding::inflate)
	{
		init
		{
			builder.setTitle(R.string.dialog_new_layer)
			builder.setPositiveButton(R.string.ok) { _, _ -> onApply() }
			builder.setNegativeButton(R.string.cancel, null)

			views.editLayerName.setText(defaultName)

			views.editLayerWidth.setText(imageSize.width.toString())
			views.editLayerWidth.addTextChangedListener { onSizeEdit(views.inputLayoutLayerWidth) }

			views.editLayerHeight.setText(imageSize.height.toString())
			views.editLayerHeight.addTextChangedListener { onSizeEdit(views.inputLayoutLayerHeight) }

			views.editLayerX.setText("0")
			views.editLayerX.addTextChangedListener { onPositionEdit() }

			views.editLayerY.setText("0")
			views.editLayerY.addTextChangedListener { onPositionEdit() }
		}

		private fun onPositionEdit() = updatePreview()

		private fun onSizeEdit(inputLayout: TextInputLayout)
		{
			inputLayout.error = getSizeValidationError(inputLayout.editText?.let(this::getValue))
			updatePreview()
		}

		private fun getSizeValidationError(value: Int?) = when
		{
			value == null || value <= 0 -> context.getString(R.string.message_image_invalid_size)
			value > GraphicsHelper.maxTextureSize -> context.getString(R.string.message_image_size_too_big)
			else -> null
		}

		private fun updatePreview()
		{
			views.boundsPreview.bounds = listOf(
					BoundsPreviewView.Bounds.Fill(imageSize.toRectF(), Color.argb(255, 255, 255, 141)),
					BoundsPreviewView.Bounds.Fill(getLayerRect().toRectF(), Color.argb(204, 27, 124, 209))
			)
		}

		// TODO Don't dismiss the dialog if there are any errors
		private fun onApply()
		{
			val layerRect = getLayerRect()
			val layerName = views.editLayerName.text.toString()
			if(getSizeValidationError(layerRect.width()) != null || getSizeValidationError(layerRect.height()) != null)
				return
			onApply(layerRect, layerName)
		}

		private fun getLayerRect(): Rect
		{
			val layerX = getValue(views.editLayerX)
			val layerY = getValue(views.editLayerY)
			val layerWidth = getValue(views.editLayerWidth)
			val layerHeight = getValue(views.editLayerHeight)
			return Rect(layerX, layerY, layerX + layerWidth, layerY + layerHeight)
		}

		private fun getValue(editText: EditText) = editText.text?.toString()?.toIntOrNull() ?: 0
	}

	private val actionPreviewBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
	private val actionPreset = Action.namePreset(R.string.history_action_layer_add).withPreview { actionPreviewBitmap }

	fun execute() = viewModel.showDialog { builder, _ ->
		Dialog(builder, imageService.defaultLayerName, imageService.image.size, this::onApply)
	}

	private fun onApply(layerRect: Rect, name: String)
	{
		if(!imageService.image.canAddMoreLayers) return
		historyService.commitAction { commit(layerRect, name) }
	}

	private fun commit(layerRect: Rect, name: String): Action.ToRevert = actionPreset.commit {
		val oldImage = imageService.image
		val newLayer = Layer.create(layerRect.left, layerRect.top, name, layerRect.width(), layerRect.height(), Color.TRANSPARENT)
		imageService.editImage { withLayerUpdated(newLayer) }
		toRevert { revert(layerRect, name, oldImage) }
	}

	private fun revert(layerRect: Rect, name: String, oldImage: Image): Action.ToCommit = actionPreset.revert {
		imageService.setImage(oldImage)
		toCommit { commit(layerRect, name) }
	}
}
