package pl.karol202.paintplus.util

import android.graphics.Rect
import android.graphics.RectF

infix fun Rect.intersectionWith(other: RectF) = RectF(this).apply {
	intersect(other)
}

fun RectF.rounded() = Rect().apply {
	this@rounded.round(this)
}
