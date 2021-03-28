package pl.karol202.paintplus.util

import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF

operator fun PointF.component1() = x
operator fun PointF.component2() = y

operator fun PointF.minus(other: Point) = PointF(x - other.x, y - other.y)

operator fun PointF.times(factor: Float) = PointF(x * factor, y * factor)

operator fun PointF.div(factor: Float) = PointF(x / factor, y / factor)

fun PointF.mapped(src: RectF, dst: RectF) =
		PointF(MathUtils.map(x, src.left, src.right, dst.left, dst.right),
		       MathUtils.map(y, src.top, src.bottom, dst.top, dst.bottom))
