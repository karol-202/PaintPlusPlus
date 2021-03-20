package pl.karol202.paintplus.image

import android.graphics.Matrix
import pl.karol202.paintplus.util.postScaled
import pl.karol202.paintplus.util.postTranslated
import pl.karol202.paintplus.util.rectF

data class ViewPosition(val x: Float = 0f,
                        val y: Float = 0f,
                        val zoom: Float = 1f)
{
	val imageMatrix = Matrix()
			.postTranslated(-x, -y)
			.postScaled(zoom, zoom)

	fun getImageRect(image: Image) = rectF(x = -x * zoom, y = -y * zoom,
	                                       width = image.width * zoom, height = image.height * zoom)
}
