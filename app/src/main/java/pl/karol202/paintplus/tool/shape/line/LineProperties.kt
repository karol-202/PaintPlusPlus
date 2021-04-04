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
package pl.karol202.paintplus.tool.shape.line

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import pl.karol202.paintplus.R
import pl.karol202.paintplus.util.SeekBarTouchListener
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import pl.karol202.paintplus.databinding.PropertiesLineBinding
import pl.karol202.paintplus.util.setOnItemSelectedListener
import pl.karol202.paintplus.util.setOnValueChangeListener
import pl.karol202.paintplus.util.viewBinding

class LineProperties : Fragment(R.layout.properties_line)
{
	private val shapeLine by inject<ShapeLine>()

	private val views by viewBinding(PropertiesLineBinding::bind)

	@SuppressLint("ClickableViewAccessibility")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		views.seekBarLineWidth.progress = shapeLine.lineWidth - 1
		views.seekBarLineWidth.setOnValueChangeListener { onLineWidthChanged(it) }
		views.seekBarLineWidth.setOnTouchListener(SeekBarTouchListener())

		views.lineWidth.text = shapeLine.lineWidth.toString()

		views.spinnerLineCap.adapter = CapAdapter(activity)
		views.spinnerLineCap.setSelection(shapeLine.lineCap.ordinal)
		views.spinnerLineCap.setOnItemSelectedListener { onItemSelected(it) }
	}

	private fun onLineWidthChanged(progress: Int)
	{
		shapeLine.lineWidth = progress + 1
		views.lineWidth.text = (progress + 1).toString()
	}

	private fun onItemSelected(index: Int)
	{
		shapeLine.lineCap = Cap.values()[index]
	}
}
