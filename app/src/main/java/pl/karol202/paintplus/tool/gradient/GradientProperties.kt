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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import pl.karol202.paintplus.R
import pl.karol202.paintplus.color.picker.ColorPickerConfig
import pl.karol202.paintplus.databinding.PropertiesGradientBinding
import pl.karol202.paintplus.tool.gradient.shape.GradientShapeAdapter
import pl.karol202.paintplus.util.collectIn
import pl.karol202.paintplus.util.setOnItemSelectedListener
import pl.karol202.paintplus.util.viewBinding
import pl.karol202.paintplus.viewmodel.PaintViewModel

class GradientProperties : Fragment(R.layout.properties_gradient)
{
	private val paintViewModel by sharedViewModel<PaintViewModel>()
	private val toolGradient by inject<ToolGradient>()

	private val views by viewBinding(PropertiesGradientBinding::bind)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		setHasOptionsMenu(true)

		toolGradient.updateEventFlow.collectIn(lifecycleScope) { activity?.invalidateOptionsMenu() }

		views.gradientPreview.setGradient(toolGradient.gradient)
		views.gradientPreview.setOnClickListener { openGradientDialog() }

		views.checkGradientRevert.isChecked = toolGradient.isReverted
		views.checkGradientRevert.setOnCheckedChangeListener { _, checked -> toolGradient.isReverted = checked }

		views.spinnerGradientShape.adapter = GradientShapeAdapter(requireContext(), toolGradient.gradientShapes)
		views.spinnerGradientShape.setSelection(toolGradient.gradientShapes.indexOf(toolGradient.shape))
		views.spinnerGradientShape.setOnItemSelectedListener { toolGradient.shape = toolGradient.gradientShapes[it] }

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

	private fun openGradientDialog() =
			GradientDialog(requireContext(), toolGradient.gradient, this::pickColor, this::onGradientEdited).show()

	private fun pickColor(config: ColorPickerConfig, callback: (Int?) -> Unit) =
			paintViewModel.makeActionRequest(PaintViewModel.ActionRequest.PickColor(config, callback))

	private fun onGradientEdited(gradient: Gradient)
	{
		toolGradient.gradient = gradient
		views.gradientPreview.setGradient(gradient)
	}
}
