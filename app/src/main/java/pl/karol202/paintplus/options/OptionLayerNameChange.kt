package pl.karol202.paintplus.options

import androidx.appcompat.app.AlertDialog
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogLayerNameBinding
import pl.karol202.paintplus.history.legacyaction.ActionLayerNameChange
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionLayerNameChange(private val viewModel: PaintViewModel) : Option
{
	private class Dialog(builder: AlertDialog.Builder,
	                     layer: Layer,
	                     onApply: (String) -> Unit) :
			Option.LayoutDialog<DialogLayerNameBinding>(builder, DialogLayerNameBinding::inflate)
	{
		init
		{
			builder.setTitle(R.string.dialog_layer_name)
			builder.setPositiveButton(R.string.ok) { _, _ -> onApply(views.editLayerName.text.toString()) }
			builder.setNegativeButton(R.string.cancel, null)

			views.editLayerName.setText(layer.name)
		}
	}

	fun execute(layer: Layer) = viewModel.showDialog { Dialog(it, layer, this::apply) }

	private fun apply(name: String)
	{
		val action = ActionLayerNameChange(adapter.image)
		action.setLayer(layer)
		layer.setName(name)
		adapter.notifyDataSetChanged()
		action.applyAction()
	}
}
