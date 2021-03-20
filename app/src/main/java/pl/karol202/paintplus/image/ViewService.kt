package pl.karol202.paintplus.image

import android.util.Size
import kotlinx.coroutines.flow.MutableStateFlow
import pl.karol202.paintplus.util.MathUtils.lerp
import pl.karol202.paintplus.util.MathUtils.map
import pl.karol202.paintplus.util.rectF

class ViewService(private val imageService: ImageService)
{
	private val _viewPositionFlow = MutableStateFlow(ViewPosition())
	private val _viewportSizeFlow = MutableStateFlow(Size(0, 0))

	private val viewX get() = _viewPositionFlow.value.x
	private val viewY get() = _viewPositionFlow.value.y
	private val zoom get() = _viewPositionFlow.value.zoom
	private val viewportWidth get() = _viewportSizeFlow.value.width
	private val viewportHeight get() = _viewportSizeFlow.value.height
	private val imageWidth get() = imageService.imageWidth
	private val imageHeight get() = imageService.imageHeight

	fun centerView()
	{
		_viewPositionFlow.value = _viewPositionFlow.value.copy(
				x = (imageWidth - viewportWidth / zoom) / 2f,
				y = (imageHeight - viewportHeight / zoom) / 2f)
	}

	fun setZoom(zoom: Float, focusX: Float, focusY: Float)
	{
		val focusXInImage = map(focusX, -viewX * this.zoom, (-viewX + imageWidth) * this.zoom, 0f, 1f)
		val focusYInImage = map(focusY, -viewY * this.zoom, (-viewY + imageHeight) * this.zoom, 0f, 1f)
		val offsetXLeft = viewX * (this.zoom / zoom) - viewX
		val offsetYTop = viewY * (this.zoom / zoom) - viewY
		val offsetXRight = (viewX * this.zoom + (imageWidth * zoom - imageWidth * this.zoom)) / zoom - viewX
		val offsetYBottom = (viewY * this.zoom + (imageHeight * zoom - imageHeight * this.zoom)) / zoom - viewY
		_viewPositionFlow.value = ViewPosition(
				x = viewX + lerp(focusXInImage, offsetXLeft, offsetXRight),
				y = viewY + lerp(focusYInImage, offsetYTop, offsetYBottom),
				zoom = zoom
		)
	}
}
