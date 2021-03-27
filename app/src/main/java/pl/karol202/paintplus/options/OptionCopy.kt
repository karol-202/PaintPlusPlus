package pl.karol202.paintplus.options

import android.graphics.Bitmap
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.withClip
import pl.karol202.paintplus.image.ClipboardContent
import pl.karol202.paintplus.image.ClipboardService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.util.minus
import pl.karol202.paintplus.util.topLeft

class OptionCopy(private val imageService: ImageService,
                 private val clipboardService: ClipboardService) : Option
{
	fun execute()
	{
		if(imageService.image.selectedLayer == null) return
		clipboardService.setContent(createClipboardContent(imageService.image.requireSelectedLayer))
	}

	fun createClipboardContent(layer: Layer): ClipboardContent
	{
		val selection = imageService.selection
		val bounds = selection.bounds

		val bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888).applyCanvas {
			withClip(selection.path - bounds.topLeft()) {
				val dstLeft = (-bounds.left + layer.x).toFloat()
				val dstTop = (-bounds.top + layer.y).toFloat()
				drawBitmap(layer.bitmap, dstLeft, dstTop, null)
			}
		}

		return ClipboardContent(bitmap, bounds.left, bounds.top)
	}
}
