package pl.karol202.paintplus.options

import pl.karol202.paintplus.R
import pl.karol202.paintplus.history.action.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer

class OptionLayerMergeDown(private val imageService: ImageService,
                           private val historyService: HistoryService) : Option
{
	private val actionPreset = Action.namePreset(R.string.history_action_layer_merge)

	fun execute(layer: Layer)
	{
		if(!imageService.image.hasLayer(layer) || !imageService.image.canMergeLayerDown(layer)) return
		historyService.commitAction { commit(layer) }
	}

	private fun commit(layer: Layer): Action.ToRevert
	{
		val oldImage = imageService.image
		imageService.editImage { withLayerMergedDown(layer) }
		return actionPreset.toRevert(layer.bitmap) { revert(oldImage, layer) }
	}

	private fun revert(oldImage: Image, layer: Layer): Action.ToCommit
	{
		imageService.setImage(oldImage)
		return actionPreset.toCommit(layer.bitmap) { commit(layer) }
	}
}
