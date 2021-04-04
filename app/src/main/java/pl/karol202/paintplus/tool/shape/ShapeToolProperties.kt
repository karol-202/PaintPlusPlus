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
package pl.karol202.paintplus.tool.shape

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import org.koin.android.ext.android.inject
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.PropertiesShapeBinding
import pl.karol202.paintplus.util.*
import java.util.*

class ShapeToolProperties : Fragment(R.layout.properties_shape)
{
	private val toolShape by inject<ToolShape>()

	private val views by viewBinding(PropertiesShapeBinding::bind)

	@SuppressLint("ClickableViewAccessibility")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		setHasOptionsMenu(true)
		toolShape.updateEventFlow.collectIn(lifecycleScope) { activity?.invalidateOptionsMenu() }

		views.spinnerShape.adapter = ShapeAdapter(requireContext(), toolShape.shapes)
		views.spinnerShape.setSelection(toolShape.shapeIndex)
		views.spinnerShape.setOnItemSelectedListener { onShapeSelected(it) }

		views.seekBarShapeTranslucency.progress = ((1 - toolShape.opacity) * 100).toInt()
		views.seekBarShapeTranslucency.setOnValueChangeListener { onTranslucencyChanged(it) }
		views.seekBarShapeTranslucency.setOnTouchListener(SeekBarTouchListener())

		views.textShapeTranslucency.text = String.format(Locale.US, "%1\$d%%", views.seekBarShapeTranslucency.progress)

		views.checkShapeSmooth.isChecked = toolShape.smooth
		views.checkShapeSmooth.setOnCheckedChangeListener { _, checked -> onSmoothChanged(checked) }

		updateFragment()
	}

	private fun onShapeSelected(index: Int)
	{
		val shape = toolShape.shapes[index]
		toolShape.shape = shape
		updateFragment()
	}

	private fun onTranslucencyChanged(progress: Int)
	{
		toolShape.opacity = 1 - (progress / 100f)
		views.textShapeTranslucency.text = String.format(Locale.US, "%1\$d%%", progress)
	}

	private fun onSmoothChanged(smooth: Boolean)
	{
		toolShape.smooth = smooth
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
	{
		if(toolShape.isInEditMode) inflater.inflate(R.menu.menu_tool_shape, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		if(toolShape.isInEditMode)
		{
			when(item.itemId)
			{
				R.id.action_apply -> toolShape.apply()
				R.id.action_cancel -> toolShape.cancel()
			}
			return true
		}
		return super.onOptionsItemSelected(item)
	}

	private fun updateFragment() = childFragmentManager.commit {
		replace(R.id.fragment_shape, toolShape.shape.propertiesClass.newInstance())
	}
}
