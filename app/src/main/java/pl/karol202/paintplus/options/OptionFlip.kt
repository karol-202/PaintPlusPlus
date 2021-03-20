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

import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogFlipBinding
import pl.karol202.paintplus.image.LegacyImage.FlipDirection
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionFlip(private val viewModel: PaintViewModel,
                 @StringRes private val title: Int,
                 private val onFlip: (FlipDirection) -> Unit) : Option
{
	private class Dialog(builder: AlertDialog.Builder,
	                     @StringRes title: Int,
	                     private val onApply: (FlipDirection) -> Unit) :
			Option.LayoutDialog<DialogFlipBinding>(builder, DialogFlipBinding::inflate)
	{
		init
		{
			builder.setTitle(title)
			builder.setPositiveButton(R.string.ok) { _, _ -> onApply() }
			builder.setNegativeButton(R.string.cancel, null)

			views.radioHorizontal.isChecked = true
		}

		private fun onApply()
		{
			when(views.radioGroupDirection.checkedRadioButtonId)
			{
				R.id.radio_horizontal -> FlipDirection.HORIZONTALLY
				R.id.radio_vertical -> FlipDirection.VERTICALLY
				else -> null
			}?.let(onApply)
		}
	}

	fun execute() = viewModel.showDialog { Dialog(it, title, onFlip) }
}
