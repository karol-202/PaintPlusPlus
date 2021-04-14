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
package pl.karol202.paintplus.options

import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogSetZoomBinding
import pl.karol202.paintplus.image.EffectsService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.viewmodel.PaintViewModel
import java.util.*

class OptionSetZoom(private val viewService: ViewService,
                    private val effectsService: EffectsService) : Option
{
	private class Dialog(builder: AlertDialog.Builder,
	                     private val viewService: ViewService) :
			Option.LayoutDialog<DialogSetZoomBinding>(builder, DialogSetZoomBinding::inflate)
	{
		private var dontFireEvent = false

		init
		{
			builder.setTitle(R.string.dialog_set_zoom)
			builder.setPositiveButton(R.string.ok, null)

			views.buttonZoomOut.setOnClickListener { onZoomOut() }
			views.buttonZoomIn.setOnClickListener { onZoomIn() }
			views.editZoom.addTextChangedListener { onTextUpdate(it?.toString() ?: "") }

			updateText()
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

		private fun onTextUpdate(text: String)
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

	fun execute() = effectsService.showDialog { builder, _ ->
		Dialog(builder, viewService)
	}
}
