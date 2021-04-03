package pl.karol202.paintplus.util

import android.graphics.*

fun Path.transformedBy(matrix: Matrix) = Path(this).apply {
	transform(matrix)
}

operator fun Path.minus(point: Point) = Path(this).apply {
	offset(-point.x.toFloat(), -point.y.toFloat())
}

fun Path.computeBounds() = RectF().also {
	computeBounds(it, true)
}
