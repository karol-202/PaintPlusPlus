package pl.karol202.paintplus.options

import androidx.appcompat.app.AlertDialog
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogLayerPropertiesBinding
import pl.karol202.paintplus.history.legacyaction.ActionLayerPropertiesChange
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.image.layer.mode.LayerMode
import pl.karol202.paintplus.image.layer.mode.LayerModeAdapter
import pl.karol202.paintplus.image.layer.mode.LayerModesService
import pl.karol202.paintplus.util.setOnItemSelectedListener
import pl.karol202.paintplus.util.setOnValueChangeListener
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionLayerPropertiesEdit(private val viewModel: PaintViewModel,
                                private val layerModesService: LayerModesService) : Option
{
	private class Dialog(builder: AlertDialog.Builder,
	                     private val layer: Layer,
	                     private val layerModes: List<LayerMode>,
	                     private val onApply: (Layer) -> Unit) :
			Option.LayoutDialog<DialogLayerPropertiesBinding>(builder, DialogLayerPropertiesBinding::inflate)
	{
		private var layerMode = layer.mode
		private var opacity = layer.opacity

		init
		{
			builder.setTitle(R.string.dialog_layer_properties)
			builder.setPositiveButton(R.string.ok) { _, _ -> onApply() }
			builder.setNegativeButton(R.string.cancel, null)

			val adapter = LayerModeAdapter(context, layerModes)
			views.spinnerLayerMode.adapter = adapter
			views.spinnerLayerMode.setSelection(layerModes.indexOf(layer.mode))
			views.spinnerLayerMode.setOnItemSelectedListener(this::onModeSelected)

			views.seekBarLayerOpacity.progress = (layer.opacity * 100).toInt()
			views.seekBarLayerOpacity.setOnValueChangeListener(this::onOpacitySelected)

			views.textLayerOpacity.text = context.getString(R.string.opacity, views.seekBarLayerOpacity.progress)
		}

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
	}

	fun execute(layer: Layer) = viewModel.showDialog { Dialog(it, layer, layerModesService.layerModes, this::apply) }

	private fun apply(layer: Layer)
	{
		val action = ActionLayerPropertiesChange(image)
		action.setLayerBeforeChange(layer)
		if(layerMode.getClass() !== layer.mode.javaClass) layer.setMode(layerMode)
		layer.setOpacity(opacity)
		action.applyAction()
	}
}
