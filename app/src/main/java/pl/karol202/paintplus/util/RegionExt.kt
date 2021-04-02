package pl.karol202.paintplus.util

import android.graphics.Point
import android.graphics.Rect
import android.graphics.Region

infix fun Region.intersectionWith(rect: Rect) = modified(rect, Region.Op.INTERSECT)

operator fun Region.plus(offset: Point) = Region(this).apply {
	translate(offset.x, offset.y)
}

fun Region.modified(rect: Rect, op: Region.Op) = Region(this).apply {
	op(rect, op)
}

fun Region.modified(region: Region, op: Region.Op) = Region(this).apply {
	op(region, op)
}
