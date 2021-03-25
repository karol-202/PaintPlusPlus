package pl.karol202.paintplus.util

import android.graphics.Point
import android.graphics.Rect
import android.graphics.Region

infix fun Region.intersectionWith(rect: Rect) = Region(this).apply {
	op(rect, Region.Op.INTERSECT)
}

operator fun Region.plus(offset: Point) = Region(this).apply {
	translate(offset.x, offset.y)
}
