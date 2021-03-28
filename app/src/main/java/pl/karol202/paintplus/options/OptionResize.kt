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
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.text.Editable
import android.util.Size
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toRectF
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogResizeBinding
import pl.karol202.paintplus.util.BoundsPreviewView
import pl.karol202.paintplus.util.GraphicsHelper
import pl.karol202.paintplus.util.MathUtils
import pl.karol202.paintplus.util.toRectF
import pl.karol202.paintplus.viewmodel.PaintViewModel
import kotlin.math.roundToInt

class OptionResize(private val viewModel: PaintViewModel) : Option
{
	@SuppressLint("ClickableViewAccessibility")
	private class Dialog(builder: AlertDialog.Builder,
	                     @StringRes title: Int,
	                     private val initialRect: Rect,
	                     private val onApply: (Rect) -> Unit) :
			Option.LayoutDialog<DialogResizeBinding>(builder, DialogResizeBinding::inflate)
	{
		private var ratio: Float? = null
		private var dontFireEvent = false

		init
		{
			builder.setTitle(title)
			builder.setPositiveButton(R.string.ok) { _, _ -> onApply() }
			builder.setNegativeButton(R.string.cancel, null)

			views.editObjectWidth.setText(initialRect.width().toString())
			views.editObjectWidth.addTextChangedListener { onSizeEdit(views.inputLayoutObjectWidth) }

			views.editObjectHeight.setText(initialRect.height().toString())
			views.editObjectHeight.addTextChangedListener { onSizeEdit(views.inputLayoutObjectHeight) }

			views.editObjectX.setText(initialRect.left.toString())
			views.editObjectX.addTextChangedListener { onPositionEdit() }

			views.editObjectY.setText(initialRect.top.toString())
			views.editObjectY.addTextChangedListener { onPositionEdit() }

			views.checkKeepRatio.setOnCheckedChangeListener { _, checked -> onKeepRatioChanged(checked) }
		}

		private fun onPositionEdit() = updatePreview()

		private fun onSizeEdit(inputLayout: TextInputLayout)
		{
			if(!dontFireEvent)
			{
				dontFireEvent = true
				val preservedSize = preserveSizeRatio(getObjectSize(), inputLayout)
				views.editObjectWidth.setText(preservedSize.width.toString())
				views.editObjectHeight.setText(preservedSize.height.toString())
				dontFireEvent = false
			}

			inputLayout.error = getSizeValidationError(inputLayout.editText?.let(this::getValue))
			updatePreview()
		}

		private fun preserveSizeRatio(size: Size, editedLayout: TextInputLayout): Size
		{
			val ratio = ratio ?: return size
			return when(editedLayout)
			{
				views.inputLayoutObjectWidth -> Size(size.width, (size.height * ratio).roundToInt())
				views.inputLayoutObjectHeight -> Size((size.width / ratio).roundToInt(), size.height)
				else -> size
			}
		}

		private fun getSizeValidationError(value: Int?) = when
		{
			value == null || value <= 0 -> context.getString(R.string.message_image_invalid_size)
			value > GraphicsHelper.maxTextureSize -> context.getString(R.string.message_image_size_too_big)
			else -> null
		}

		private fun onKeepRatioChanged(keepRatio: Boolean)
		{
			val currentWidth = getValue(views.editObjectWidth)
			val currentHeight = getValue(views.editObjectHeight)
			if(keepRatio && (currentWidth == 0 || currentHeight == 0))
				views.checkKeepRatio.isChecked = false
			else
				ratio = if(keepRatio) currentWidth.toFloat() / currentHeight else null
		}

		private fun updatePreview()
		{
			views.boundsPreview.bounds = listOf(
					BoundsPreviewView.Bounds.Fill(initialRect.toRectF(), Color.argb(255, 255, 255, 141)),
					BoundsPreviewView.Bounds.Fill(getObjectRect().toRectF(), Color.argb(204, 27, 124, 209))
			)
		}

		// TODO Don't dismiss the dialog if there are any errors
		private fun onApply()
		{
			val objectRect = getObjectRect()
			if(getSizeValidationError(objectRect.width()) != null || getSizeValidationError(objectRect.height()) != null)
				return
			onApply(objectRect)
		}

		private fun getObjectRect(): Rect
		{
			val x = getValue(views.editObjectX)
			val y = getValue(views.editObjectY)
			val width = getValue(views.editObjectWidth)
			val height = getValue(views.editObjectHeight)
			return Rect(x, y, x + width, y + height)
		}

		private fun getObjectSize(): Size
		{
			val width = getValue(views.editObjectWidth)
			val height = getValue(views.editObjectHeight)
			return Size(width, height)
		}

		private fun getValue(editText: EditText) = editText.text?.toString()?.toIntOrNull() ?: 0
	}

	fun execute(@StringRes title: Int, initialRect: Rect, onApply: (Rect) -> Unit) =
			viewModel.showDialog { builder, _ -> Dialog(builder, title, initialRect, onApply) }
}
