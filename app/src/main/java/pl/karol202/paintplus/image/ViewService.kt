package pl.karol202.paintplus.image

import android.util.Size
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.karol202.paintplus.util.MathUtils.lerp
import pl.karol202.paintplus.util.MathUtils.map
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

private val ZOOM_RANGE = 0.009f..16f

class ViewService(private val imageService: ImageService)
{
	private val _viewPositionFlow = MutableStateFlow(ViewPosition())
	private val _viewportSizeFlow = MutableStateFlow(Size(0, 0))

	val viewPositionFlow: StateFlow<ViewPosition> = _viewPositionFlow

	val viewPosition get() = _viewPositionFlow.value
	val viewportSize get() = _viewportSizeFlow.value
	val viewX get() = viewPosition.x
	val viewY get() = viewPosition.y
	val zoom get() = viewPosition.zoom

	private val viewportWidth get() = _viewportSizeFlow.value.width
	private val viewportHeight get() = _viewportSizeFlow.value.height
	private val imageWidth get() = imageService.imageWidth
	private val imageHeight get() = imageService.imageHeight

	private val zoomSteps = (-12 until 8).map { calculateZoomRatio(it) }

	fun centerView() = setViewPosition(viewPosition.copy(
			x = (imageWidth - viewportWidth / zoom) / 2f,
			y = (imageHeight - viewportHeight / zoom) / 2f))

	fun offsetView(x: Int, y: Int) = setViewPosition(viewPosition.copy(
			x = viewX + x,
			y = viewY + y
	))

	fun setDefaultZoom() = setZoom(1f)

	fun setLowerZoomStep() = zoomSteps.lastOrNull { it < zoom }?.let(this::setZoom)

	fun setGreaterZoomStep() = zoomSteps.firstOrNull { it > zoom }?.let(this::setZoom)

	fun setZoom(zoom: Float, focusX: Float = 0.5f, focusY: Float = 0.5f)
	{
		if(zoom !in ZOOM_RANGE) return setZoom(zoom.coerceIn(ZOOM_RANGE), focusX, focusY)

		val focusXInImage = map(focusX, -viewX * this.zoom, (-viewX + imageWidth) * this.zoom, 0f, 1f)
		val focusYInImage = map(focusY, -viewY * this.zoom, (-viewY + imageHeight) * this.zoom, 0f, 1f)
		val offsetXLeft = viewX * (this.zoom / zoom) - viewX
		val offsetYTop = viewY * (this.zoom / zoom) - viewY
		val offsetXRight = (viewX * this.zoom + (imageWidth * zoom - imageWidth * this.zoom)) / zoom - viewX
		val offsetYBottom = (viewY * this.zoom + (imageHeight * zoom - imageHeight * this.zoom)) / zoom - viewY
		setViewPosition(ViewPosition(
				x = viewX + lerp(focusXInImage, offsetXLeft, offsetXRight),
				y = viewY + lerp(focusYInImage, offsetYTop, offsetYBottom),
				zoom = zoom))
	}

	private fun calculateZoomRatio(step: Int): Float
	{
		val value = sqrt(2.0).pow(abs(step))
		val rounded = (value * 2).roundToInt() / 2f
		return if(step >= 0) rounded else 1 / rounded
	}

	private fun setViewPosition(viewPosition: ViewPosition)
	{
		_viewPositionFlow.value = viewPosition
	}

	fun setViewportSize(size: Size)
	{
		_viewportSizeFlow.value = size
	}
}
