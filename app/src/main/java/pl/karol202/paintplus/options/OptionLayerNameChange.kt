package pl.karol202.paintplus.options

import androidx.appcompat.app.AlertDialog
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogLayerNameBinding
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionLayerNameChange(private val viewModel: PaintViewModel,
                            private val imageService: ImageService,
                            private val historyService: HistoryService) : Option
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

	private val actionPreset = Action.namePreset(R.string.history_action_layer_name_change)

	fun execute(layer: Layer)
	{
		if(!imageService.image.hasLayer(layer)) return
		viewModel.showDialog { builder, _ ->
			Dialog(builder, layer) { name -> onApply(layer, name) }
		}
	}

	private fun onApply(layer: Layer, newName: String)
	{
		historyService.commitAction { commit(layer, newName) }
	}

	private fun commit(oldLayer: Layer, newName: String): Action.ToRevert
	{
		val newLayer = oldLayer.withName(newName)
		imageService.editImage { withLayerUpdated(newLayer) }
		return actionPreset.toRevert(oldLayer.bitmap) { revert(oldLayer, newName) }
	}

	private fun revert(oldLayer: Layer, newName: String): Action.ToCommit
	{
		imageService.editImage { withLayerUpdated(oldLayer) }
		return actionPreset.toCommit(oldLayer.bitmap) { commit(oldLayer, newName) }
	}
}
