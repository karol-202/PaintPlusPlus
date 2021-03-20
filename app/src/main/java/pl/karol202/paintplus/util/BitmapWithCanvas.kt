package pl.karol202.paintplus.util

import android.graphics.Bitmap
import android.graphics.Canvas

data class BitmapWithCanvas(val bitmap: Bitmap,
                            val canvas: Canvas)
{
	companion object
	{
		fun create(bitmap: Bitmap) = BitmapWithCanvas(bitmap, Canvas(bitmap))
	}
}
