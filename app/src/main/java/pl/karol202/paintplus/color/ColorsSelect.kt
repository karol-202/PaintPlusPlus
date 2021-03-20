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
package pl.karol202.paintplus.color

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import pl.karol202.paintplus.R
import pl.karol202.paintplus.color.picker.ColorPickerConfig
import pl.karol202.paintplus.databinding.ColorsBinding
import pl.karol202.paintplus.util.collectIn
import pl.karol202.paintplus.util.viewBinding
import pl.karol202.paintplus.viewmodel.PaintViewModel

class ColorsSelect : Fragment(R.layout.colors)
{
	private val viewModel by sharedViewModel<PaintViewModel>()
	private val views by viewBinding(ColorsBinding::bind)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		views.viewColorFirst.setOnClickListener { pickColor() }

		viewModel.currentColorFlow.collectIn(lifecycleScope) { views.viewColorFirst.setBackgroundColor(it) }
	}

	private fun pickColor()
	{
		val pickerConfig = ColorPickerConfig(initialColor = viewModel.currentColor, useAlpha = false)
		viewModel.makeActionRequest(PaintViewModel.ActionRequest.PickColor(pickerConfig) { color ->
			color?.toLong()?.or(0xFF000000)?.toInt()?.let { viewModel.setCurrentColor(it) }
		})
	}
}
