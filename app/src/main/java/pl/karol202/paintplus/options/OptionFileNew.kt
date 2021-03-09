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
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogNewImageBinding
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.util.GraphicsHelper
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionFileNew : Option
{
	class Dialog(builder: AlertDialog.Builder,
	             private val image: Image) :
			Option.Dialog<DialogNewImageBinding>(builder, DialogNewImageBinding::inflate)
	{
		init
		{
			builder.setTitle(R.string.dialog_new_image)
			builder.setView(views.root)
			builder.setPositiveButton(R.string.ok) { _, _ -> onApply() }
			builder.setNegativeButton(R.string.cancel, null)

			views.editImageX.setText(image.width.toString())
			views.editImageX.addTextChangedListener { onEdit(views.inputLayoutImageX, it?.toString()) }
			views.editImageY.setText(image.height.toString())
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
			if(getValidationError(width) != null || getValidationError(height) != null) return

			image.newImage(width, height)
			image.centerView()
		}

		private fun getValidationError(value: Int?) = when
		{
			value == null || value <= 0 -> context.getString(R.string.message_image_invalid_size)
			value > GraphicsHelper.getMaxTextureSize() -> context.getString(R.string.message_image_size_too_big)
			else -> null
		}
	}

	override fun execute(viewModel: PaintViewModel) = viewModel.showDialog { Dialog(it, viewModel.image) }
}
