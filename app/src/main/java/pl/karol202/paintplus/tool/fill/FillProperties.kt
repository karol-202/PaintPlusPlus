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
package pl.karol202.paintplus.tool.fill

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.PropertiesBrushBinding
import pl.karol202.paintplus.databinding.PropertiesFillBinding
import pl.karol202.paintplus.tool.brush.ToolBrush
import pl.karol202.paintplus.util.SeekBarTouchListener
import pl.karol202.paintplus.util.setOnValueChangeListener
import pl.karol202.paintplus.util.viewBinding
import java.util.*

class FillProperties : Fragment(R.layout.properties_fill)
{
	private val toolFill by inject<ToolFill>()

	private val views by viewBinding(PropertiesFillBinding::bind)

	@SuppressLint("ClickableViewAccessibility")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		views.seekBarFillThreshold.progress = toolFill.threshold.toInt()
		views.seekBarFillThreshold.setOnValueChangeListener { setFillThreshold(it) }
		views.seekBarFillThreshold.setOnTouchListener(SeekBarTouchListener())

		views.fillThreshold.text = String.format(Locale.US, "%d%%", views.seekBarFillThreshold.progress)

		views.seekBarFillTranslucency.progress = ((1 - toolFill.opacity) * 100).toInt()
		views.seekBarFillTranslucency.setOnValueChangeListener { setFillTranslucency(it) }
		views.seekBarFillTranslucency.setOnTouchListener(SeekBarTouchListener())

		views.fillTranslucency.text = String.format(Locale.US, "%d%%", views.seekBarFillTranslucency.progress)
	}

	private fun setFillThreshold(threshold: Int)
	{
		toolFill.threshold = threshold / 100f
		views.fillThreshold.text = String.format(Locale.US, "%d%%", threshold)
	}

	private fun setFillTranslucency(translucency: Int)
	{
		toolFill.opacity = 1 - translucency / 100f
		views.fillTranslucency.text = String.format(Locale.US, "%d%%", translucency)
	}
}
