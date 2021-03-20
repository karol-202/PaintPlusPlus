package pl.karol202.paintplus.util

import android.graphics.PointF

fun PointF.translated(dx: Float, dy: Float) = PointF(x + dx, y + dy)
