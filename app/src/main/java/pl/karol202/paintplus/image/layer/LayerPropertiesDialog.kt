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
package pl.karol202.paintplus.image.layer

import android.content.Context
import androidx.appcompat.app.AlertDialog
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogLayerPropertiesBinding
import pl.karol202.paintplus.image.layer.mode.LayerMode
import pl.karol202.paintplus.image.layer.mode.LayerModeAdapter
import pl.karol202.paintplus.util.layoutInflater
import pl.karol202.paintplus.util.setOnItemSelectedListener
import pl.karol202.paintplus.util.setOnValueChangeListener

internal class LayerPropertiesDialog(private val context: Context,
                                     private val layerModes: List<LayerMode>,
                                     private val layer: Layer,
                                     private val onApply: (Layer) -> Unit)
{
	private var layerMode = layer.mode
	private var opacity = layer.opacity

	private val views = DialogLayerPropertiesBinding.inflate(context.layoutInflater)
	private val dialog: AlertDialog

	init
	{
		val builder = AlertDialog.Builder(context)
		builder.setTitle(R.string.dialog_layer_properties)
		builder.setPositiveButton(R.string.ok) { _, _ -> onApply() }
		builder.setNegativeButton(R.string.cancel, null)
		builder.setView(views.root)

		val adapter = LayerModeAdapter(context, layerModes)
		views.spinnerLayerMode.adapter = adapter
		views.spinnerLayerMode.setSelection(layerModes.indexOf(layer.mode))
		views.spinnerLayerMode.setOnItemSelectedListener(this::onModeSelected)

		views.seekBarLayerOpacity.progress = (layer.opacity * 100).toInt()
		views.seekBarLayerOpacity.setOnValueChangeListener(this::onOpacitySelected)

		views.textLayerOpacity.text = context.getString(R.string.opacity, views.seekBarLayerOpacity.progress)
		dialog = builder.create()
	}

	fun show() = dialog.show()

	private fun onModeSelected(index: Int)
	{
		layerMode = layerModes[index]
	}

	private fun onOpacitySelected(progress: Int)
	{
		opacity = progress / 100f
		views.textLayerOpacity.text = context.getString(R.string.opacity, progress)
	}

	private fun onApply() = onApply(layer.withOpacity(opacity).withMode(layerMode))

	// TODO
	/*val action = ActionLayerPropertiesChange(image)
	action.setLayerBeforeChange(layer)
	if(layerMode.getClass() !== layer.mode.javaClass) layer.setMode(layerMode)
	layer.setOpacity(opacity)
	action.applyAction()*/
}
