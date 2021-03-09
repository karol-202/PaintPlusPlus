package pl.karol202.paintplus.util

import android.graphics.Bitmap
import android.util.Size
import androidx.core.graphics.scale

val Bitmap.size get() = Size(width, height)

fun Bitmap.fitInto(maxSize: Size): Bitmap
{
	if(size fitsIn maxSize) return this
	val targetSize = size.fitInto(maxSize)
	return scale(targetSize.width, targetSize.height)
}
