package pl.karol202.paintplus.options

import android.graphics.Bitmap
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.withClip
import pl.karol202.paintplus.image.ClipboardContent
import pl.karol202.paintplus.image.ClipboardService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.util.minus
import pl.karol202.paintplus.util.topLeft

class OptionCopy(private val imageService: ImageService,
                 private val clipboardService: ClipboardService) : Option
{
	fun execute()
	{
		if(imageService.image.selectedLayer == null) return
		clipboardService.setContent(createClipboardContent())
	}

	fun createClipboardContent(): ClipboardContent
	{
		val selectedLayer = imageService.image.requireSelectedLayer
		val selection = imageService.selection
		val bounds = selection.bounds

		val bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888).applyCanvas {
			withClip(selection.path - bounds.topLeft()) {
				val dstLeft = (-bounds.left + selectedLayer.x).toFloat()
				val dstTop = (-bounds.top + selectedLayer.y).toFloat()
				drawBitmap(selectedLayer.bitmap, dstLeft, dstTop, null)
			}
		}

		return ClipboardContent(bitmap, bounds.left, bounds.top)
	}
}
