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
package pl.karol202.paintplus.tool.brush

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.PropertiesBrushBinding
import pl.karol202.paintplus.databinding.PropertiesSelectionBinding
import pl.karol202.paintplus.tool.selection.ToolSelection
import pl.karol202.paintplus.util.SeekBarTouchListener
import pl.karol202.paintplus.util.setOnValueChangeListener
import pl.karol202.paintplus.util.viewBinding
import java.util.*

class BrushProperties : Fragment(R.layout.properties_brush)
{
	private val toolBrush by inject<ToolBrush>()

	private val views by viewBinding(PropertiesBrushBinding::bind)

	@SuppressLint("ClickableViewAccessibility")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		views.seekBarBrushSize.progress = toolBrush.size.toInt() - 1
		views.seekBarBrushSize.setOnValueChangeListener { setBrushSize(it + 1) }
		views.seekBarBrushSize.setOnTouchListener(SeekBarTouchListener())

		views.textBrushSize.text = (views.seekBarBrushSize.progress + 1).toString()

		views.seekBarBrushShapeOffset.progress = toolBrush.shapeOffset.toInt() - 1
		views.seekBarBrushShapeOffset.setOnValueChangeListener { setBrushShapeOffset(it + 1) }
		views.seekBarBrushShapeOffset.setOnTouchListener(SeekBarTouchListener())

		views.textBrushShapeOffset.text = (views.seekBarBrushShapeOffset.progress + 1).toString()

		views.seekBarBrushTranslucency.progress = ((1 - toolBrush.opacity) * 100).toInt()
		views.seekBarBrushTranslucency.setOnValueChangeListener { setBrushTranslucency(it) }
		views.seekBarBrushTranslucency.setOnTouchListener(SeekBarTouchListener())

		views.textBrushTranslucency.text = String.format(Locale.US, "%1\$d%%", views.seekBarBrushTranslucency.progress)
	}

	private fun setBrushSize(size: Int)
	{
		toolBrush.size = size.toFloat()
		views.textBrushSize.text = size.toString()
	}

	private fun setBrushShapeOffset(offset: Int)
	{
		toolBrush.shapeOffset = offset.toFloat()
		views.textBrushShapeOffset.text = offset.toString()
	}

	private fun setBrushTranslucency(translucency: Int)
	{
		toolBrush.opacity = 1 - (translucency / 100f)
		views.textBrushTranslucency.text = String.format(Locale.US, "%d%%", translucency)
	}
}
