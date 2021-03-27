package pl.karol202.paintplus.options

import android.content.Context
import pl.karol202.paintplus.R
import pl.karol202.paintplus.history.action.Action
import pl.karol202.paintplus.image.*
import pl.karol202.paintplus.image.layer.Layer

class OptionPaste(context: Context,
                  private val imageService: ImageService,
                  private val historyService: HistoryService,
                  private val clipboardService: ClipboardService) : Option
{
	private val pastedLayerName = context.getString(R.string.pasted_layer)

	private val actionPreset = Action.namePreset(R.string.history_action_paste)

	fun execute()
	{
		val clipboardContent = clipboardService.content
		if(!imageService.image.canAddMoreLayers || clipboardContent == null) return
		historyService.commitAction { commit(clipboardContent) }
	}

	private fun commit(content: ClipboardContent): Action.ToRevert
	{
		val oldImage = imageService.image
		val layer = Layer.create(content.left, content.top, pastedLayerName, content.bitmap)
		imageService.editImage { withLayerAdded(layer, autoSelect = true) }
		return actionPreset.toRevert(content.bitmap) { revert(content, oldImage) }
	}

	private fun revert(content: ClipboardContent, image: Image): Action.ToCommit
	{
		imageService.setImage(image)
		return actionPreset.toCommit(content.bitmap) { commit(content) }
	}
}
