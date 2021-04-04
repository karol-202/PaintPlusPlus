package pl.karol202.paintplus.image

import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.graphics.Region
import androidx.core.graphics.toRectF
import androidx.core.graphics.toRegion
import androidx.core.graphics.xor
import pl.karol202.paintplus.util.modified
import pl.karol202.paintplus.util.plus

data class Selection(val region: Region)
{
	companion object
	{
		val empty = Selection(Region())

		fun fromRect(rect: Rect) = Selection(Region(rect))
	}

	val isEmpty = region.isEmpty
	val path = region.boundaryPath
	val bounds = region.bounds

	operator fun contains(point: Point) = bounds.contains(point.x, point.y)

	fun contains(x: Int, y: Int) = bounds.contains(x, y)

	fun translated(x: Int, y: Int) = Selection(region + Point(x, y))

	fun inverted(rect: Rect) = Selection(region xor rect)

	fun withRectangleOperation(rect: Rect, op: Region.Op) = Selection(region.modified(rect, op))

	fun withOvalOperation(rect: Rect, op: Region.Op): Selection
	{
		val ovalPath = Path().apply {
			addOval(rect.toRectF(), Path.Direction.CW)
		}
		val ovalRegion = Region().apply {
			setPath(ovalPath, rect.toRegion())
		}
		return Selection(region.modified(ovalRegion, op))
	}
}
