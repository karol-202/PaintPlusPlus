package pl.karol202.paintplus.image

import android.graphics.Bitmap

data class ClipboardContent(val bitmap: Bitmap,
                            val left: Int,
                            val top: Int)
{
	fun translated(x: Int, y: Int) = copy(left = left + x,
	                                      top = top + y)
}
