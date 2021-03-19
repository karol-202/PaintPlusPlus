package pl.karol202.paintplus.image.layer.mode

import android.graphics.*
import androidx.core.graphics.applyCanvas
import pl.karol202.paintplus.util.BitmapWithCanvas
import pl.karol202.paintplus.util.memoize

abstract class SimpleLayerMode(override val type: LayerModeType,
                               private val porterDuffMode: PorterDuff.Mode) : LayerMode
{
	private val paint = Paint().apply {
		xfermode = PorterDuffXfermode(porterDuffMode)
	}

	override fun apply(dstBitmap: Bitmap, dstCanvas: Canvas, opacity: Float, builder: Canvas.() -> Unit): BitmapWithCanvas
	{
		val srcBitmap = Bitmap.createBitmap(dstBitmap.width, dstBitmap.height, Bitmap.Config.ARGB_8888)
		val srcCanvas = Canvas(srcBitmap)
		srcCanvas.builder()

		paint.alpha = (opacity * 255).toInt()

		dstCanvas.drawBitmap(srcBitmap, 0f, 0f, paint)
		return BitmapWithCanvas(dstBitmap, dstCanvas)
	}
}
