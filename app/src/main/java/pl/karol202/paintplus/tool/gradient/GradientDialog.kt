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
import androidx.appcompat.app.AlertDialog
import pl.karol202.paintplus.R
import pl.karol202.paintplus.color.picker.ColorPickerConfig
import pl.karol202.paintplus.databinding.DialogGradientBinding
import pl.karol202.paintplus.tool.gradient.GradientView.OnGradientEditorUpdateListener
import pl.karol202.paintplus.util.layoutInflater
import java.util.*

internal class GradientDialog(private val context: Context,
                              private val originalGradient: Gradient,
                              private val colorPicker: (ColorPickerConfig, (Int?) -> Unit) -> Unit,
                              private val onGradientUpdate: () -> Unit)
{
	private val gradient = Gradient(originalGradient)

	private val views = DialogGradientBinding.inflate(context.layoutInflater)
	private val dialog = createDialog()

	private fun createDialog(): AlertDialog
	{
		val builder = AlertDialog.Builder(context)
		builder.setTitle(R.string.dialog_gradient)
		builder.setView(views.root)
		builder.setPositiveButton(R.string.ok) { _, _ -> applyGradient() }
		builder.setNegativeButton(R.string.cancel, null)

		views.gradientView.setGradientUpdateListener(object : OnGradientEditorUpdateListener
		                                             {
			                                             override fun onGradientPositionUpdated(position: Float) =
			                                             		this@GradientDialog.onGradientPositionUpdated(position)

			                                             override fun onGradientSelectionUpdated(position: Float, color: Int) =
			                                             		this@GradientDialog.onGradientSelectionUpdated(position, color)

			                                             override fun onGradientPointAdded() =
			                                             		this@GradientDialog.onGradientPointAdded()
		                                             })
		views.gradientView.gradient = gradient

		views.textGradientPosition.text = ""

		views.buttonAddGradientPoint.setOnCheckedChangeListener { _, checked -> onAddButtonCheckedChange(checked) }

		views.buttonDeleteGradientPoint.setOnClickListener { onDeleteButtonClick() }
		views.buttonDeleteGradientPoint.isEnabled = false

		views.gradientColor.setColor(views.gradientView.selectedColor)
		views.gradientColor.setOnClickListener { onColorViewClick() }

		return builder.create()
	}

	fun show() = dialog.show()

	private fun applyGradient()
	{
		originalGradient.setGradient(gradient)
		onGradientUpdate()
	}

	private fun onGradientPositionUpdated(position: Float)
	{
		views.textGradientPosition.text = position.takeUnless { it == -1f }
				?.let { String.format(Locale.US, "%.0f%%", position * 100) }
				?: ""
	}

	private fun onGradientSelectionUpdated(position: Float, color: Int)
	{
		onGradientPositionUpdated(position)
		views.buttonDeleteGradientPoint.isEnabled = views.gradientView.canDeletePoint()
		views.gradientColor.setColor(color)
	}

	private fun onGradientPointAdded()
	{
		views.buttonAddGradientPoint.isChecked = false
		onAddButtonCheckedChange(false)
	}

	private fun onAddButtonCheckedChange(checked: Boolean) = views.gradientView.setAddingMode(checked)

	private fun onDeleteButtonClick() = views.gradientView.deleteSelectedPoint()

	private fun onColorViewClick()
	{
		if(views.gradientView.isAnyColorSelected) pickColor()
	}

	private fun pickColor() = colorPicker(ColorPickerConfig(views.gradientView.selectedColor, true), this::onColorPick)

	private fun onColorPick(color: Int?) = color?.let { views.gradientView.selectedColor = it }
}
