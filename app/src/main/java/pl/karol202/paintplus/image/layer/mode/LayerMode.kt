package pl.karol202.paintplus.image.layer.mode

import android.graphics.*
import androidx.annotation.StringRes
import pl.karol202.paintplus.util.BitmapWithCanvas

interface LayerMode
{
	val name: Int
	val category: Int

	fun apply(dstBitmap: Bitmap, dstCanvas: Canvas, opacity: Float = 1f, builder: Canvas.() -> Unit): BitmapWithCanvas
}
