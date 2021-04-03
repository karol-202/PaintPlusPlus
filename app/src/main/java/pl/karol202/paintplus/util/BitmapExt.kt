package pl.karol202.paintplus.util

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
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

fun Bitmap.ensureMutable() = takeIf { isMutable } ?: duplicated()

fun Bitmap.withAlpha(enabled: Boolean) = apply {
	setHasAlpha(enabled)
}

fun Bitmap.duplicated() = copy(config, true) ?: error("Could not duplicate bitmap")

fun Bitmap.cropped(srcRect: Rect) =
		Bitmap.createBitmap(this, srcRect.left, srcRect.top, srcRect.width(), srcRect.height())
				?: error("Could not crop bitmap")


