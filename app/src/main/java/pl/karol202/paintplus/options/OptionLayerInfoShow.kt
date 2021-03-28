package pl.karol202.paintplus.options

import androidx.appcompat.app.AlertDialog
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogLayerInfoBinding
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionLayerInfoShow(private val viewModel: PaintViewModel) : Option
{
	private class Dialog(builder: AlertDialog.Builder,
	                     layer: Layer) :
			Option.LayoutDialog<DialogLayerInfoBinding>(builder, DialogLayerInfoBinding::inflate)
	{
		init
		{
			builder.setTitle(R.string.dialog_layer_info)
			builder.setPositiveButton(R.string.ok, null)

			views.textLayerInfoNameValue.text = layer.name
			views.textLayerInfoWidthValue.text = layer.width.toString()
			views.textLayerInfoHeightValue.text = layer.height.toString()
			views.textLayerInfoXValue.text = layer.x.toString()
			views.textLayerInfoYValue.text = layer.y.toString()
			views.textLayerInfoOpacityValue.text = context.getString(R.string.opacity, (layer.opacity * 100).toInt())
			views.textLayerInfoModeValue.setText(layer.mode.name)
			views.textLayerInfoVisibilityValue.setText(if(layer.visible) R.string.yes else R.string.no)
		}
	}

	fun execute(layer: Layer) = viewModel.showDialog { builder, _ ->
		Dialog(builder, layer)
	}
}
