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
package pl.karol202.paintplus.tool.shape.circle

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.PropertiesCircleBinding
import pl.karol202.paintplus.util.SeekBarTouchListener
import pl.karol202.paintplus.util.setOnValueChangeListener
import pl.karol202.paintplus.util.viewBinding

class CircleProperties : Fragment(R.layout.properties_circle)
{
	private val shapeCircle by inject<ShapeCircle>()

	private val views by viewBinding(PropertiesCircleBinding::bind)

	@SuppressLint("ClickableViewAccessibility")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		views.checkCircleFill.isChecked = shapeCircle.isFilled
		views.checkCircleFill.setOnCheckedChangeListener { _, checked -> onFillChanged(checked) }

		views.seekBarCircleWidth.progress = shapeCircle.circleWidth - 1
		views.seekBarCircleWidth.setOnValueChangeListener { onWidthChanged(it) }
		views.seekBarCircleWidth.setOnTouchListener(SeekBarTouchListener())

		views.circleWidth.text = shapeCircle.circleWidth.toString()
	}

	private fun onFillChanged(fill: Boolean)
	{
		shapeCircle.isFilled = fill
	}

	private fun onWidthChanged(progress: Int)
	{
		shapeCircle.circleWidth = progress + 1
		views.circleWidth.text = (progress + 1).toString()
	}
}
