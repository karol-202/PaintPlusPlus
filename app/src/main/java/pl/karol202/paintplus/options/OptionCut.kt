package pl.karol202.paintplus.options

import android.graphics.*
import androidx.core.graphics.withClip
import pl.karol202.paintplus.R
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.ClipboardContent
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.util.minus

class OptionCut(private val imageService: ImageService,
                private val historyService: HistoryService,
                private val optionCopy: OptionCopy) : Option
{
	private val clearPaint = Paint().apply {
		xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
	}

	private val actionPreset = Action.namePreset(R.string.history_action_cut)

	fun execute()
	{
		if(imageService.image.selectedLayer == null) return
		optionCopy.execute()
		historyService.commitAction { commit(imageService.image.requireSelectedLayer) }
	}

	private fun commit(layer: Layer): Action.ToRevert
	{
		val previewBitmap = Action.createThumbnailBitmap(layer.bitmap)
		val selection = imageService.selection
		val clipboardContent = optionCopy.createClipboardContent(layer)

		layer.editCanvas.withClip(selection.path - layer.position) {
			drawRect(0f, 0f, layer.width.toFloat(), layer.height.toFloat(), clearPaint)
		}

		return actionPreset.toRevert(previewBitmap) { revert(layer, clipboardContent) }
	}

	private fun revert(layer: Layer, clipboardContent: ClipboardContent): Action.ToCommit
	{
		val previewBitmap = Action.createThumbnailBitmap(layer.bitmap)
		layer.editCanvas.drawBitmap(clipboardContent.bitmap,
		                            clipboardContent.left.toFloat(), clipboardContent.top.toFloat(), null)
		return actionPreset.toCommit(previewBitmap) { commit(layer) }
	}
}
