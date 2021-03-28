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
package pl.karol202.paintplus.tool.pan

import android.os.Bundle
import pl.karol202.paintplus.R
import android.text.Editable
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import pl.karol202.paintplus.databinding.PropertiesGradientBinding
import pl.karol202.paintplus.databinding.PropertiesPanBinding
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.tool.gradient.ToolGradient
import pl.karol202.paintplus.util.collectIn
import pl.karol202.paintplus.util.viewBinding
import pl.karol202.paintplus.viewmodel.PaintViewModel
import java.util.*
import kotlin.math.roundToInt

class PanProperties : Fragment(R.layout.properties_pan)
{
	private val viewService by inject<ViewService>()

	private val views by viewBinding(PropertiesPanBinding::bind)

	private var dontFireEvent = false

	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		views.buttonZoomOut.setOnClickListener { onZoomOut() }
		views.buttonZoomIn.setOnClickListener { onZoomIn() }
		views.buttonCenterView.setOnClickListener { viewService.centerView() }
		views.editZoom.addTextChangedListener { onZoomEdited(it?.toString() ?: "") }

		viewService.viewPositionFlow.collectIn(lifecycleScope) { updateText() }
	}

	private fun onZoomOut()
	{
		viewService.setLowerZoomStep()
		updateText()
	}

	private fun onZoomIn()
	{
		viewService.setGreaterZoomStep()
		updateText()
	}

	private fun onZoomEdited(text: String)
	{
		when
		{
			dontFireEvent -> {}
			!text.endsWith("%") -> {
				updateText()
				views.editZoom.setSelection(views.editZoom.text.length - 1)
			}
			text != "%" -> parseZoom(text)?.let(viewService::setZoom)
		}
	}

	private fun updateText()
	{
		dontFireEvent = true
		views.editZoom.setText(String.format(Locale.US, "%.1f%%", viewService.zoom * 100))
		dontFireEvent = false
	}

	private fun parseZoom(text: String) = text.substring(0, text.length - 1).toFloatOrNull()?.div(100f)
}
