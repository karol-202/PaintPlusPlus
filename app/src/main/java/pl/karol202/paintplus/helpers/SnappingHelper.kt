package pl.karol202.paintplus.helpers

import android.graphics.PointF

interface SnappingHelper : Helper
{
	fun snapX(x: Float): Float

	fun snapY(y: Float): Float

	fun snapPoint(point: PointF): PointF
}
