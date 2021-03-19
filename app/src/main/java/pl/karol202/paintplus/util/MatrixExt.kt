package pl.karol202.paintplus.util

import android.graphics.Matrix

fun Matrix.preTranslated(dx: Float, dy: Float) = Matrix(this).apply {
	preTranslate(dx, dy)
}
