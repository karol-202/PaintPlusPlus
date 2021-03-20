package pl.karol202.paintplus.util

import android.graphics.Rect
import android.graphics.RectF

infix fun Rect.intersectionWith(other: RectF) = RectF(this).apply {
	intersect(other)
}

fun RectF.rounded() = Rect().apply {
	this@rounded.round(this)
}

fun Iterable<Rect>.union() = Rect().apply {
	forEach { it.union(this) }
}

fun rectF(x: Float, y: Float, width: Float, height: Float) = RectF(x, y, x + width, y + height)
