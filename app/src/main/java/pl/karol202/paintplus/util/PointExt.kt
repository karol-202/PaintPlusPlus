package pl.karol202.paintplus.util

import android.graphics.PointF
import android.graphics.RectF

operator fun PointF.component1() = x
operator fun PointF.component2() = y

fun PointF.translated(dx: Float, dy: Float) = PointF(x + dx, y + dy)

fun PointF.mapped(src: RectF, dst: RectF) =
		PointF(MathUtils.map(x, src.left, src.right, dst.left, dst.right),
		       MathUtils.map(y, src.top, src.bottom, dst.top, dst.bottom))
