package pl.karol202.paintplus.options

import androidx.appcompat.app.AlertDialog
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogLayerNameBinding
import pl.karol202.paintplus.history.action.ActionLayerNameChange
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionLayerDelete(private val viewModel: PaintViewModel) : Option
{
	private class Dialog(builder: AlertDialog.Builder,
	                     layer: Layer,
	                     onApply: (Layer) -> Unit) : Option.BasicDialog(builder)
	{
		init
		{
			builder.setMessage(context.getString(R.string.dialog_layer_delete, layer.name))
			builder.setPositiveButton(R.string.layer_delete) { _, _ -> onApply(layer) }
			builder.setNegativeButton(R.string.cancel, null)
		}
	}

	fun execute(layer: Layer) = viewModel.showDialog { Dialog(it, layer, this::apply) }

	private fun apply(layer: Layer)
	{
		val action = ActionLayerDelete(adapter.image)
		action.setLayerBeforeDeleting(layer)
		adapter.image.deleteLayer(layer)
		adapter.notifyDataSetChanged()
		action.applyAction()
	}
}
