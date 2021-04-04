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
package pl.karol202.paintplus.tool.shape.polygon

import android.annotation.SuppressLint
import pl.karol202.paintplus.tool.shape.JoinAdapter
import android.os.Bundle
import pl.karol202.paintplus.R
import android.view.View
import pl.karol202.paintplus.util.SeekBarTouchListener
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import pl.karol202.paintplus.databinding.PropertiesPolygonBinding
import pl.karol202.paintplus.tool.shape.Join
import pl.karol202.paintplus.util.setOnItemSelectedListener
import pl.karol202.paintplus.util.setOnValueChangeListener
import pl.karol202.paintplus.util.viewBinding

private const val MIN_SIDES = 3
private const val MAX_SIDES = 20

class PolygonProperties : Fragment(R.layout.properties_polygon)
{
	private val shapePolygon by inject<ShapePolygon>()

	private val views by viewBinding(PropertiesPolygonBinding::bind)

	private var errorToFew = getString(R.string.error_polygon_too_few_sides)
	private var errorToMany = getString(R.string.error_polygon_too_many_sides)

	private val sides get() = views.editTextPolygonSides.text?.toString()?.toIntOrNull() ?: 0

	@SuppressLint("ClickableViewAccessibility")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		views.buttonMinusPolygonSides.setOnClickListener { onSidesDecremented() }

		views.buttonPlusPolygonSides.setOnClickListener { onSidesIncremented() }

		views.editLayoutPolygonSides.isHintEnabled = false
		views.editTextPolygonSides.setText(shapePolygon.sides.toString())
		views.editTextPolygonSides.addTextChangedListener { onSidesChanged() }

		views.checkPolygonFill.isChecked = shapePolygon.isFilled
		views.checkPolygonFill.setOnCheckedChangeListener { _, checked: Boolean -> onFillChanged(checked) }

		views.seekPolygonWidth.progress = shapePolygon.lineWidth - 1
		views.seekPolygonWidth.setOnValueChangeListener { onWidthChanged(it) }
		views.seekPolygonWidth.setOnTouchListener(SeekBarTouchListener())

		views.polygonWidth.text = shapePolygon.lineWidth.toString()

		views.spinnerPolygonJoin.adapter = JoinAdapter(requireContext())
		views.spinnerPolygonJoin.setSelection(shapePolygon.join.ordinal)
		views.spinnerPolygonJoin.setOnItemSelectedListener { onJoinChanged(it) }
	}

	private fun onSidesDecremented()
	{
		if(sides > MIN_SIDES) views.editTextPolygonSides.setText((sides - 1).toString())
	}

	private fun onSidesIncremented()
	{
		if(sides < MAX_SIDES) views.editTextPolygonSides.setText((sides + 1).toString())
	}

	private fun onSidesChanged() = when
	{
		sides < MIN_SIDES -> views.editLayoutPolygonSides.error = errorToFew
		sides > MAX_SIDES -> views.editLayoutPolygonSides.error = errorToMany
		else ->
		{
			views.editLayoutPolygonSides.isErrorEnabled = false
			shapePolygon.sides = sides
		}
	}

	private fun onFillChanged(fill: Boolean)
	{
		shapePolygon.isFilled = fill
	}

	private fun onWidthChanged(progress: Int)
	{
		shapePolygon.lineWidth = progress + 1
		views.polygonWidth.text = (progress + 1).toString()
	}

	private fun onJoinChanged(index: Int)
	{
		shapePolygon.join = Join.values()[index]
	}
}
