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
package pl.karol202.paintplus.tool.selection

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import org.koin.android.ext.android.inject
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.PropertiesDragBinding
import pl.karol202.paintplus.databinding.PropertiesSelectionBinding
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.options.OptionSelectAll
import pl.karol202.paintplus.options.OptionSelectInversion
import pl.karol202.paintplus.options.OptionSelectNothing
import pl.karol202.paintplus.tool.drag.ToolDrag
import pl.karol202.paintplus.util.collectIn
import pl.karol202.paintplus.util.setOnItemSelectedListener
import pl.karol202.paintplus.util.viewBinding

class SelectionProperties : Fragment(R.layout.properties_selection)
{
	private val toolSelection by inject<ToolSelection>()
	private val optionSelectAll by inject<OptionSelectAll>()
	private val optionSelectNothing by inject<OptionSelectNothing>()
	private val optionSelectInversion by inject<OptionSelectInversion>()

	private val views by viewBinding(PropertiesSelectionBinding::bind)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		setHasOptionsMenu(true)
		toolSelection.updateEventFlow.collectIn(lifecycleScope) { activity?.invalidateOptionsMenu() }

		views.spinnerSelectionShape.adapter = SelectionShapeAdapter(requireContext())
		views.spinnerSelectionShape.setSelection(toolSelection.shape.ordinal)
		views.spinnerSelectionShape.setOnItemSelectedListener { onShapeSelected(it) }

		views.spinnerSelectionMode.adapter = SelectionModeAdapter(requireContext())
		views.spinnerSelectionMode.setSelection(toolSelection.mode.ordinal)
		views.spinnerSelectionMode.setOnItemSelectedListener { onModeSelected(it) }

		views.buttonSelectionAll.setOnClickListener { optionSelectAll.execute() }

		views.buttonSelectionNothing.setOnClickListener { optionSelectNothing.execute() }

		views.buttonSelectionInvert.setOnClickListener { optionSelectInversion.execute() }
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
	{
		if(toolSelection.isInEditMode) inflater.inflate(R.menu.menu_tool_selection, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		if(toolSelection.isInEditMode)
		{
			when(item.itemId)
			{
				R.id.action_apply -> toolSelection.applySelection()
				R.id.action_cancel -> toolSelection.cancelSelection()
			}
			return true
		}
		return super.onOptionsItemSelected(item)
	}

	private fun onShapeSelected(position: Int)
	{
		toolSelection.shape = ToolSelectionShape.values()[position]
	}

	private fun onModeSelected(position: Int)
	{
		toolSelection.mode = ToolSelectionMode.values()[position]
	}
}
