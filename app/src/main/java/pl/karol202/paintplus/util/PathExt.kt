package pl.karol202.paintplus.util

import android.graphics.Matrix
import android.graphics.Path
import android.graphics.Point

fun Path.transformedBy(matrix: Matrix) = Path(this).apply {
	transform(matrix)
}

operator fun Path.minus(point: Point) = Path(this).apply {
	offset(-point.x.toFloat(), -point.y.toFloat())
}
