package pl.karol202.paintplus.options

import pl.karol202.paintplus.R
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService

class OptionLayerChangeOrder(private val imageService: ImageService,
                             private val historyService: HistoryService) : Option
{
	private val actionPreset = Action.namePreset(R.string.history_action_layer_order_move)

	fun execute(layerIndex: Int, targetIndex: Int)
	{
		if(!imageService.image.hasLayerIndex(layerIndex) || !imageService.image.hasLayerIndex(targetIndex)) return
		historyService.commitAction { commit(layerIndex, targetIndex) }
	}

	private fun commit(layerIndex: Int, targetIndex: Int): Action.ToRevert
	{
		val layer = imageService.image.layers[layerIndex]
		imageService.editImage { withLayerMoved(layerIndex, targetIndex) }
		return actionPreset.toRevert(layer.bitmap) { revert(layerIndex, targetIndex) }
	}

	private fun revert(layerIndex: Int, targetIndex: Int): Action.ToCommit
	{
		val layer = imageService.image.layers[targetIndex]
		imageService.editImage { withLayerMoved(targetIndex, layerIndex) }
		return actionPreset.toCommit(layer.bitmap) { commit(layerIndex, targetIndex) }
	}
}
