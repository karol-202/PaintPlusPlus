package pl.karol202.paintplus.util

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Size
import androidx.core.graphics.scale

val Bitmap.size get() = Size(width, height)

fun Bitmap.fitInto(maxSize: Size): Bitmap
{
	if(size fitsIn maxSize) return this
	val targetSize = size.fitInto(maxSize)
	return scale(targetSize.width, targetSize.height)
}

fun Bitmap.transformedWith(matrix: Matrix, bilinear: Boolean = false): Bitmap =
		Bitmap.createBitmap(this, 0, 0, width, height, matrix, bilinear)

fun Bitmap.ensureMutable() = takeIf { isMutable } ?: copy(config, true)

fun Bitmap.withAlpha(enabled: Boolean) = apply {
	setHasAlpha(enabled)
}
