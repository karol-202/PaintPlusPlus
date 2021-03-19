package pl.karol202.paintplus.util

import android.graphics.Matrix
import android.graphics.Path

fun Path.transformedBy(matrix: Matrix) = Path(this).apply {
	transform(matrix)
}
