package pl.karol202.paintplus.image

import android.graphics.Point
import android.graphics.Region
import pl.karol202.paintplus.util.plus

data class Selection(val region: Region)
{
	companion object
	{
		val empty = Selection(Region())
	}

	val path = region.boundaryPath
	val bounds = region.bounds

	operator fun contains(point: Point) = bounds.contains(point.x, point.y)

	fun translated(x: Int, y: Int) = copy(region = region + Point(x, y))
}
