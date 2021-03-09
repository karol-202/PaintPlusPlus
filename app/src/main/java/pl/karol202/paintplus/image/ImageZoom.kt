package pl.karol202.paintplus.image

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

// TODO Find a better place for it
object ImageZoom
{
	private val zoomSteps = (-12 until 8).map { calculateZoomRatio(it) }

	private fun calculateZoomRatio(position: Int): Float
	{
		val value = sqrt(2.0).pow(abs(position))
		val rounded = (value * 2).roundToInt() / 2f
		return if(position >= 0) rounded else 1 / rounded
	}

	fun getLowerZoom(current: Float) = zoomSteps.last { it < current }

	fun getGreaterZoom(current: Float) = zoomSteps.first { it > current }
}
