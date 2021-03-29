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
package pl.karol202.paintplus.tool.pickcolor

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.PropertiesColorPickBinding
import pl.karol202.paintplus.util.SeekBarTouchListener
import pl.karol202.paintplus.util.setOnValueChangeListener
import pl.karol202.paintplus.util.viewBinding

class ColorPickProperties : Fragment(R.layout.properties_color_pick)
{
	private val toolColorPick by inject<ToolColorPick>()

	private val views by viewBinding(PropertiesColorPickBinding::bind)

	@SuppressLint("ClickableViewAccessibility")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		views.checkPickAverage.isChecked = toolColorPick.isAverage
		views.checkPickAverage.setOnCheckedChangeListener { _, checked -> onAverageChanged(checked) }

		views.seekBarPickSize.isEnabled = toolColorPick.isAverage
		views.seekBarPickSize.progress = (toolColorPick.size - 2).coerceAtLeast(0)
		views.seekBarPickSize.setOnTouchListener(SeekBarTouchListener())
		views.seekBarPickSize.setOnValueChangeListener { onSizeChanged(it) }

		views.textPickSize.text = (views.seekBarPickSize.progress + 2).toString()
	}

	private fun onAverageChanged(average: Boolean)
	{
		toolColorPick.size = if(average) views.seekBarPickSize.progress + 2 else 1
		views.seekBarPickSize.isEnabled = average
	}

	private fun onSizeChanged(progress: Int)
	{
		val size = progress + 2
		toolColorPick.size = size
		views.textPickSize.text = size.toString()
	}
}
