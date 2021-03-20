package pl.karol202.paintplus.util

import android.graphics.Matrix

fun Matrix.preTranslated(dx: Float, dy: Float) = Matrix(this).apply {
	preTranslate(dx, dy)
}

fun Matrix.preRotated(degrees: Float) = Matrix(this).apply {
	preRotate(degrees)
}

fun Matrix.preScaled(sx: Float, sy: Float) = Matrix(this).apply {
	preScale(sx, sy)
}
