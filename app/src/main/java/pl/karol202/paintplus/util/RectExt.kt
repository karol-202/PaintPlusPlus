package pl.karol202.paintplus.util

import android.graphics.*
import android.util.Size
import android.util.SizeF
import androidx.core.graphics.minus
import androidx.core.graphics.plus
import androidx.core.graphics.toRectF

infix fun Rect.intersectionWith(other: RectF) = RectF(this).apply {
	intersect(other)
}

fun RectF.rounded() = Rect().apply {
	this@rounded.round(this)
}

fun Iterable<Rect>.union() = Rect().apply {
	forEach { it.union(this) }
}

fun Iterable<RectF>.union() = RectF().apply {
	forEach { it.union(this) }
}

fun rectF(x: Float, y: Float, width: Float, height: Float) = RectF(x, y, x + width, y + height)

fun Rect.center() = Point(centerX(), centerY())

fun RectF.center() = PointF(centerX(), centerY())

fun Rect.topLeft() = Point(left, top)

fun RectF.size() = SizeF(width(), height())

fun Rect.centeredInside(container: Rect) = this + (container.center() - center())

fun RectF.centeredInside(container: RectF) = this + (container.center() - center())

fun RectF.mapped(src: RectF, dst: RectF) =
		RectF(MathUtils.map(left, src.left, src.right, dst.left, dst.right),
		      MathUtils.map(top, src.top, src.bottom, dst.top, dst.bottom),
		      MathUtils.map(right, src.left, src.right, dst.left, dst.right),
		      MathUtils.map(bottom, src.top, src.bottom, dst.top, dst.bottom))

fun RectF.toPath() = Path().apply {
	addRect(this@toPath, Path.Direction.CW)
	close()
}
