package pl.karol202.paintplus.options

import androidx.appcompat.app.AlertDialog
import pl.karol202.paintplus.R
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionLayerDelete(private val viewModel: PaintViewModel,
                        private val imageService: ImageService,
                        private val historyService: HistoryService) : Option
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

	private val actionPreset = Action.namePreset(R.string.history_action_layer_delete)

	fun execute(layer: Layer)
	{
		if(!imageService.image.hasLayer(layer)) return
		viewModel.showDialog { builder, _ ->
			Dialog(builder, layer) { onApplied(layer) }
		}
	}

	private fun onApplied(layer: Layer)
	{
		historyService.commitAction { commit(layer) }
	}

	private fun commit(layer: Layer): Action.ToRevert
	{
		val oldImage = imageService.image
		imageService.editImage { withLayerDeleted(layer) }
		return actionPreset.toRevert(layer.bitmap) { revert(layer, oldImage) }
	}

	private fun revert(layer: Layer, oldImage: Image): Action.ToCommit
	{
		imageService.setImage(oldImage)
		return actionPreset.toCommit(layer.bitmap) { commit(layer) }
	}
}
