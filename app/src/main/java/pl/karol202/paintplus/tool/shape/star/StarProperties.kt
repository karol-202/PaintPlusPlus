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
package pl.karol202.paintplus.tool.shape.star

import android.annotation.SuppressLint
import pl.karol202.paintplus.tool.shape.JoinAdapter
import android.os.Bundle
import pl.karol202.paintplus.R
import android.view.View
import pl.karol202.paintplus.util.SeekBarTouchListener
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import pl.karol202.paintplus.databinding.PropertiesStarBinding
import pl.karol202.paintplus.tool.shape.Join
import pl.karol202.paintplus.util.setOnItemSelectedListener
import pl.karol202.paintplus.util.setOnValueChangeListener
import pl.karol202.paintplus.util.viewBinding

private const val MIN_CORNERS = 3
private const val MAX_CORNERS = 20

class StarProperties : Fragment(R.layout.properties_star)
{
	private val shapeStar by inject<ShapeStar>()

	private val views by viewBinding(PropertiesStarBinding::bind)

	private var errorToFew = getString(R.string.error_polygon_too_few_sides)
	private var errorToMany = getString(R.string.error_polygon_too_many_sides)

	private val corners get() = views.editTextStarCorners.text?.toString()?.toIntOrNull() ?: 0

	@SuppressLint("ClickableViewAccessibility")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		views.buttonMinusStarCorners.setOnClickListener { onCornersDecremented() }

		views.buttonPlusStarCorners.setOnClickListener { onCornersIncremented() }

		views.editLayoutStarCorners.isHintEnabled = false

		views.editTextStarCorners.setText(shapeStar.corners.toString())
		views.editTextStarCorners.addTextChangedListener { onCornersChanged() }

		views.checkStarFill.isChecked = shapeStar.isFilled
		views.checkStarFill.setOnCheckedChangeListener { _, checked: Boolean -> onFillChanged(checked) }

		views.seekStarWidth.progress = shapeStar.lineWidth - 1
		views.seekStarWidth.setOnValueChangeListener { progress -> onWidthChanged(progress) }
		views.seekStarWidth.setOnTouchListener(SeekBarTouchListener())

		views.starWidth.text = shapeStar.lineWidth.toString()

		views.spinnerStarJoin.adapter = JoinAdapter(requireContext())
		views.spinnerStarJoin.setSelection(shapeStar.join.ordinal)
		views.spinnerStarJoin.setOnItemSelectedListener { index -> onJoinChanged(index) }
	}

	private fun onCornersDecremented()
	{
		if(corners > MIN_CORNERS) views.editTextStarCorners.setText((corners - 1).toString())
	}

	private fun onCornersIncremented()
	{
		if(corners < MAX_CORNERS) views.editTextStarCorners.setText((corners + 1).toString())
	}

	private fun onCornersChanged() = when
	{
		corners < MIN_CORNERS -> views.editLayoutStarCorners.error = errorToFew
		corners > MAX_CORNERS -> views.editLayoutStarCorners.error = errorToMany
		else ->
		{
			views.editLayoutStarCorners.isErrorEnabled = false
			shapeStar.corners = corners
		}
	}

	private fun onFillChanged(fill: Boolean)
	{
		shapeStar.isFilled = fill
	}

	private fun onWidthChanged(progress: Int)
	{
		shapeStar.lineWidth = progress + 1
		views.starWidth.text = (progress + 1).toString()
	}

	private fun onJoinChanged(index: Int)
	{
		shapeStar.join = Join.values()[index]
	}
}
