package pl.karol202.paintplus.options

import android.graphics.*
import androidx.core.graphics.withClip
import pl.karol202.paintplus.R
import pl.karol202.paintplus.history.action.Action
import pl.karol202.paintplus.history.legacyaction.ActionLayerChange
import pl.karol202.paintplus.image.ClipboardContent
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.util.minus

class OptionCut(private val imageService: ImageService,
                private val historyService: HistoryService,
                private val optionCopy: OptionCopy) : Option
{
	private val clearPaint = Paint().apply {
		xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
	}

	private val actionPreset = Action.Preset(R.string.history_action_cut) { imageService.image.requireSelectedLayer.bitmap }

	fun execute()
	{
		if(imageService.image.selectedLayer == null) return
		optionCopy.execute()
		historyService.commitAction(this::commit)
	}

	private fun commit(): Action.ToRevert = actionPreset.commit {
		val selectedLayer = imageService.image.requireSelectedLayer
		val selection = imageService.selection
		val clipboardContent = optionCopy.createClipboardContent()

		selectedLayer.editCanvas.withClip(selection.path - selectedLayer.topLeft) {
			drawRect(0f, 0f, selectedLayer.width.toFloat(), selectedLayer.height.toFloat(), clearPaint)
		}

		toRevert { revert(clipboardContent) }
	}

	private fun revert(clipboardContent: ClipboardContent): Action.ToCommit = actionPreset.revert {
		val canvas = imageService.image.requireSelectedLayer.editCanvas
		canvas.drawBitmap(clipboardContent.bitmap, clipboardContent.left.toFloat(), clipboardContent.top.toFloat(), null)
		toCommit { commit() }
	}
}
