package pl.karol202.paintplus.options

import pl.karol202.paintplus.R
import pl.karol202.paintplus.history.action.Action
import pl.karol202.paintplus.history.legacyaction.ActionLayerVisibilityChange
import pl.karol202.paintplus.image.FlipDirection
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer

class OptionLayerVisibilityToggle(private val imageService: ImageService,
                                  private val historyService: HistoryService) : Option
{
	private val actionPreset = Action.namePreset(R.string.history_action_layer_visibility_change)

	fun execute(layer: Layer)
	{
		if(!imageService.image.hasLayer(layer)) return
		historyService.commitAction { commit(imageService.image.requireSelectedLayer) }
	}

	private fun commit(oldLayer: Layer): Action.ToRevert
	{
		imageService.editImage { withLayerUpdated(oldLayer.withVisibility(!oldLayer.visible)) }
		return actionPreset.toRevert(oldLayer.bitmap) { revert(oldLayer) }
	}

	private fun revert(oldLayer: Layer): Action.ToCommit
	{
		imageService.editImage { withLayerUpdated(oldLayer) }
		return actionPreset.toCommit(oldLayer.bitmap) { commit(oldLayer) }
	}
}
