package pl.karol202.paintplus.tool.shape

import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.Flow

interface Shape
{
	val name: Int
	val icon: Int
	val propertiesClass: Class<out Fragment>

	var opacity: Float
	var smooth: Boolean

	val isInEditMode: Boolean
	val bounds: Rect?

	fun onTouchStart(point: Point)

	fun onTouchMove(point: Point)

	fun onTouchStop(point: Point)

	fun drawOnLayer(canvas: Canvas, translucent: Boolean)

	fun apply(imageCanvas: Canvas)

	fun cancel()
}
