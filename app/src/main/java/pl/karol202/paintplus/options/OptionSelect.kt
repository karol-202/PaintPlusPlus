package pl.karol202.paintplus.options

import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.applyCanvas
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.util.component1
import pl.karol202.paintplus.util.component2
import pl.karol202.paintplus.util.div
import pl.karol202.paintplus.util.size
import kotlin.math.max

private const val SELECTION_LINE_WIDTH = 2f

class OptionSelect(private val imageService: ImageService) : Option
{
	private val selectionPaint = Paint().apply {
		color = Color.DKGRAY
		style = Paint.Style.STROKE
	}

	fun createPreviewBitmap() = imageService.image.getFlattenedBitmap().applyCanvas {
		val previewScale = (size / Action.maxPreviewSize).let { (scaleX, scaleY) -> max(scaleX, scaleY) }
		selectionPaint.strokeWidth = SELECTION_LINE_WIDTH * previewScale
		drawPath(imageService.selection.path, selectionPaint)
	}
}
