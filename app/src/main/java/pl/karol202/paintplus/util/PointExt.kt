package pl.karol202.paintplus.util

import android.graphics.PointF

operator fun PointF.component1() = x
operator fun PointF.component2() = y

fun PointF.translated(dx: Float, dy: Float) = PointF(x + dx, y + dy)
