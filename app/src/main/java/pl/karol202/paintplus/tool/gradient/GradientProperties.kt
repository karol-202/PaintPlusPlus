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

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import pl.karol202.paintplus.R
import pl.karol202.paintplus.color.picker.ColorPickerConfig
import pl.karol202.paintplus.databinding.PropertiesGradientBinding
import pl.karol202.paintplus.tool.ToolProperties
import pl.karol202.paintplus.util.setOnItemSelectedListener
import pl.karol202.paintplus.util.viewBinding
import pl.karol202.paintplus.viewmodel.PaintViewModel

class GradientProperties : ToolProperties(R.layout.properties_gradient)
{
	private val toolGradient by lazy { tool as ToolGradient }
	private val views by viewBinding(PropertiesGradientBinding::bind)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		setHasOptionsMenu(true)

		toolGradient.setOnGradientEditListener(this::onGradientEdit)

		views.gradientPreview.setGradient(toolGradient.gradient)
		views.gradientPreview.setOnClickListener { openGradientDialog() }

		views.checkGradientRevert.isChecked = toolGradient.isReverted
		views.checkGradientRevert.setOnCheckedChangeListener { _, checked -> toolGradient.setRevert(checked) }

		views.spinnerGradientShape.adapter = GradientShapeAdapter(requireContext(), toolGradient.shapes.shapes)
		views.spinnerGradientShape.setSelection(toolGradient.shapeId)
		views.spinnerGradientShape.setOnItemSelectedListener { toolGradient.shapeId = it }

		views.spinnerGradientRepeatability.adapter = GradientRepeatabilityAdapter(requireContext())
		views.spinnerGradientRepeatability.setSelection(toolGradient.repeatability.ordinal)
		views.spinnerGradientRepeatability.setOnItemSelectedListener {
			toolGradient.repeatability = GradientRepeatability.values()[it]
		}
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
	{
		if(toolGradient.isInEditMode) inflater.inflate(R.menu.menu_tool_gradient, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		if(toolGradient.isInEditMode)
		{
			when(item.itemId)
			{
				R.id.action_apply -> toolGradient.apply()
				R.id.action_cancel -> toolGradient.cancel()
			}
			activity?.invalidateOptionsMenu()
			return true
		}
		return super.onOptionsItemSelected(item)
	}

	private fun onGradientEdit()
	{
		activity?.invalidateOptionsMenu()
	}

	private fun openGradientDialog() =
			GradientDialog(requireContext(), toolGradient.gradient, this::pickColor, this::onGradientUpdate).show()

	private fun pickColor(config: ColorPickerConfig, callback: (Int?) -> Unit) =
			paintViewModel.makeActionRequest(PaintViewModel.ActionRequest.PickColor(config, callback))

	private fun onGradientUpdate() = views.gradientPreview.update()
}
