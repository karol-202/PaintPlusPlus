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
package pl.karol202.paintplus.tool.marker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.PropertiesBrushBinding
import pl.karol202.paintplus.databinding.PropertiesMarkerBinding
import pl.karol202.paintplus.tool.brush.ToolBrush
import pl.karol202.paintplus.util.SeekBarTouchListener
import pl.karol202.paintplus.util.setOnValueChangeListener
import pl.karol202.paintplus.util.viewBinding
import java.util.*

class MarkerProperties : Fragment(R.layout.properties_marker)
{
	private val toolMarker by inject<ToolMarker>()

	private val views by viewBinding(PropertiesMarkerBinding::bind)

	@SuppressLint("ClickableViewAccessibility")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		views.seekBarMarkerSize.progress = (toolMarker.size - 1).toInt()
		views.seekBarMarkerSize.setOnValueChangeListener { setMarkerSize(it) }
		views.seekBarMarkerSize.setOnTouchListener(SeekBarTouchListener())

		views.textMarkerSize.text = (views.seekBarMarkerSize.progress + 1).toString()

		views.seekBarMarkerTranslucency.progress = ((1 - toolMarker.opacity) * 100).toInt()
		views.seekBarMarkerTranslucency.setOnValueChangeListener { setMarkerTranslucency(it) }
		views.seekBarMarkerTranslucency.setOnTouchListener(SeekBarTouchListener())

		views.textMarkerTranslucency.text = String.format(Locale.US, "%1\$d%%", views.seekBarMarkerTranslucency.progress)

		views.checkSmoothEdge.isChecked = toolMarker.smoothEdge
		views.checkSmoothEdge.setOnCheckedChangeListener { _, checked -> onSmoothEdgeChanged(checked) }
	}

	private fun setMarkerSize(size: Int)
	{
		toolMarker.size = size.toFloat()
		views.textMarkerSize.text = (size + 1).toString()
	}

	private fun setMarkerTranslucency(translucency: Int)
	{
		toolMarker.opacity = 1 - translucency / 100f
		views.textMarkerTranslucency.text = String.format(Locale.US, "%1\$d%%", translucency)
	}

	private fun onSmoothEdgeChanged(smoothEdge: Boolean)
	{
		toolMarker.smoothEdge = smoothEdge
	}
}
