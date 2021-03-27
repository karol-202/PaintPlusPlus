package pl.karol202.paintplus.options

import android.content.Context
import pl.karol202.paintplus.R
import pl.karol202.paintplus.history.action.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer

class OptionLayerDuplicate(context: Context,
                           private val imageService: ImageService,
                           private val historyService: HistoryService) : Option
{
	private val duplicateSuffix = context.getString(R.string.duplicate)

	private val actionPreset = Action.namePreset(R.string.history_action_layer_duplicate)

	fun execute(layer: Layer)
	{
		if(!imageService.image.canAddMoreLayers || !imageService.image.hasLayer(layer)) return
		historyService.commitAction { commit(layer) }
	}

	private fun commit(sourceLayer: Layer): Action.ToRevert
	{
		val oldImage = imageService.image
		val layerIndex = imageService.image.getLayerIndex(sourceLayer)
		val newLayer = sourceLayer.duplicated().withName(sourceLayer.name + duplicateSuffix)
		imageService.editImage { withLayerAdded(newLayer, index = layerIndex + 1, autoSelect = true) }
		return actionPreset.toRevert(sourceLayer.bitmap) { revert(newLayer, oldImage) }
	}

	private fun revert(sourceLayer: Layer, oldImage: Image): Action.ToCommit
	{
		imageService.setImage(oldImage)
		return actionPreset.toCommit(sourceLayer.bitmap) { commit(sourceLayer) }
	}
}
