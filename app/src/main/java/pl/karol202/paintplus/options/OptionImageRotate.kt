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
import pl.karol202.paintplus.databinding.DialogRotateImageBinding
import pl.karol202.paintplus.history.action.ActionImageRotate
import pl.karol202.paintplus.image.Image.RotationAmount
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionImageRotate(private val viewModel: PaintViewModel) : Option
{
	private class Dialog(builder: AlertDialog.Builder,
	                     private val onApply: (RotationAmount) -> Unit) :
			Option.LayoutDialog<DialogRotateImageBinding>(builder, DialogRotateImageBinding::inflate)
	{
		init
		{
			builder.setTitle(R.string.action_rotate_image)
			builder.setPositiveButton(R.string.ok) { _, _ -> onApply() }
			builder.setNegativeButton(R.string.cancel, null)

			views.radioAngle90.isChecked = true
		}

		private fun onApply()
		{
			when(views.radioGroupAngle.checkedRadioButtonId)
			{
				R.id.radio_angle_90 -> RotationAmount.ANGLE_90
				R.id.radio_angle_180 -> RotationAmount.ANGLE_180
				R.id.radio_angle_270 -> RotationAmount.ANGLE_270
				else -> null
			}?.let(onApply)
		}
	}

	fun execute() = viewModel.showDialog { Dialog(it, this::rotate) }

	private fun rotate(rotationAmount: RotationAmount)
	{
		val action = ActionImageRotate(viewModel.image)
		action.setRotationAmount(rotationAmount)
		viewModel.image.rotate(rotationAmount)
		action.applyAction()
	}
}
