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
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogNewImageBinding
import pl.karol202.paintplus.image.FileService
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.util.GraphicsHelper
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionImageNew(private val viewModel: PaintViewModel,
                     private val imageService: ImageService,
                     private val viewService: ViewService,
                     private val fileService: FileService) : Option
{
	private class Dialog(builder: AlertDialog.Builder,
	                     currentSize: Size,
	                     private val onApply: (Size) -> Unit) :
			Option.LayoutDialog<DialogNewImageBinding>(builder, DialogNewImageBinding::inflate)
	{
		init
		{
			builder.setTitle(R.string.dialog_new_image)
			builder.setPositiveButton(R.string.ok) { _, _ -> onApply() }
			builder.setNegativeButton(R.string.cancel, null)

			views.editImageX.setText(currentSize.width.toString())
			views.editImageX.addTextChangedListener { onEdit(views.inputLayoutImageX, it?.toString()) }
			views.editImageY.setText(currentSize.height.toString())
			views.editImageY.addTextChangedListener { onEdit(views.inputLayoutImageY, it?.toString()) }
		}

		private fun onEdit(inputLayout: TextInputLayout, text: String?)
		{
			inputLayout.error = getValidationError(text?.toIntOrNull())
		}

		private fun onApply()
		{
			val width = views.editImageX.text.toString().toIntOrNull() ?: 0
			val height = views.editImageY.text.toString().toIntOrNull() ?: 0
			if(getValidationError(width) == null && getValidationError(height) == null) onApply(Size(width, height))
		}

		private fun getValidationError(value: Int?) = when
		{
			value == null || value <= 0 -> context.getString(R.string.message_image_invalid_size)
			value > GraphicsHelper.maxTextureSize -> context.getString(R.string.message_image_size_too_big)
			else -> null
		}
	}

	fun execute() = viewModel.showDialog { Dialog(it, imageService.image.size, this::onApply) }

	private fun onApply(size: Size)
	{
		imageService.newImage(size.width, size.height)
		viewService.centerView()
		fileService.onFileReset()
	}
}
