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
package pl.karol202.paintplus.tool.gradient

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import pl.karol202.paintplus.R
import pl.karol202.paintplus.color.picker.ColorPickerConfig
import pl.karol202.paintplus.databinding.DialogGradientBinding
import pl.karol202.paintplus.util.layoutInflater
import java.util.*

class GradientDialog(private val context: Context,
                     private val initialGradient: Gradient,
                     private val colorPicker: (ColorPickerConfig, (Int?) -> Unit) -> Unit,
                     private val onApply: (Gradient) -> Unit)
{
	private val views = DialogGradientBinding.inflate(context.layoutInflater)
	private val dialog = createDialog()

	private fun createDialog(): AlertDialog
	{
		val builder = AlertDialog.Builder(context)
		builder.setTitle(R.string.dialog_gradient)
		builder.setView(views.root)
		builder.setPositiveButton(R.string.ok) { _, _ -> applyGradient() }
		builder.setNegativeButton(R.string.cancel, null)

		views.gradientView.onGradientUpdated = this::onGradientUpdated
		views.gradientView.gradient = initialGradient

		views.textGradientPosition.text = ""

		views.buttonAddGradientPoint.setOnCheckedChangeListener { _, checked -> onAddButtonCheckedChange(checked) }

		views.buttonDeleteGradientPoint.setOnClickListener { onDeleteButtonClick() }
		views.buttonDeleteGradientPoint.isEnabled = false

		views.gradientColor.setColor(views.gradientView.selectedColor ?: Color.TRANSPARENT)
		views.gradientColor.setOnClickListener { onColorViewClick() }

		return builder.create()
	}

	fun show() = dialog.show()

	private fun applyGradient() = onApply(views.gradientView.gradient)

	private fun onGradientUpdated()
	{
		views.textGradientPosition.text = views.gradientView.selectedPosition
				?.let { String.format(Locale.US, "%.0f%%", it * 100) }
				?: ""
		views.gradientColor.setColor(views.gradientView.selectedColor ?: Color.TRANSPARENT)
		views.buttonAddGradientPoint.isChecked = views.gradientView.addingMode
		views.buttonDeleteGradientPoint.isEnabled = views.gradientView.canDeletePoint
	}

	private fun onAddButtonCheckedChange(checked: Boolean)
	{
		views.gradientView.addingMode = checked
	}

	private fun onDeleteButtonClick() = views.gradientView.deleteSelectedPoint()

	private fun onColorViewClick()
	{
		val selectedColor = views.gradientView.selectedColor ?: return
		colorPicker(ColorPickerConfig(selectedColor, true), this::onColorPick)
	}

	private fun onColorPick(color: Int?) = color?.let { views.gradientView.setSelectedColor(it) }
}
