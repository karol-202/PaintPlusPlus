package pl.karol202.paintplus.util

import android.graphics.Rect
import android.graphics.Region

infix fun Region.intersectionWith(rect: Rect) = Region(this).apply {
	op(rect, Region.Op.INTERSECT)
}
