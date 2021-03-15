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
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import pl.karol202.paintplus.R
import pl.karol202.paintplus.color.picker.ColorPickerConfig
import pl.karol202.paintplus.databinding.ColorsBinding
import pl.karol202.paintplus.util.viewBinding
import pl.karol202.paintplus.viewmodel.PaintViewModel

class ColorsSelect : Fragment(R.layout.colors)
{
	private enum class PickTarget(val get: ColorsSet.() -> Int,
	                              val set: ColorsSet.(Int) -> Unit)
	{
		FIRST({ firstColor }, { firstColor = it }),
		SECOND({ secondColor }, { secondColor = it })
	}

	private val viewModel by sharedViewModel<PaintViewModel>()
	private val views by viewBinding(ColorsBinding::bind)

	private val colors: ColorsSet by lazy { viewModel.image.colorsSet }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		colors.addListener { updateColorViews() }

		views.viewColorFirst.setOnClickListener { pickColor(PickTarget.FIRST) }
		views.viewColorSecond.setOnClickListener { pickColor(PickTarget.SECOND) }
		views.buttonColorsSwap.setOnClickListener { invertColors() }
		updateColorViews()
	}

	private fun pickColor(target: PickTarget)
	{
		val pickerConfig = ColorPickerConfig(initialColor = target.get(colors), useAlpha = false)
		viewModel.makeActionRequest(PaintViewModel.ActionRequest.PickColor(pickerConfig) { color ->
			color?.toLong()?.or(0xFF000000)?.toInt()?.let { target.set(colors, it) }
			updateColorViews()
		})
	}

	private fun invertColors()
	{
		colors.invert()
		updateColorViews()
	}

	private fun updateColorViews()
	{
		views.viewColorFirst.setBackgroundColor(colors.firstColor)
		views.viewColorSecond.setBackgroundColor(colors.secondColor)
		viewModel.image.updateImage()
	}
}
