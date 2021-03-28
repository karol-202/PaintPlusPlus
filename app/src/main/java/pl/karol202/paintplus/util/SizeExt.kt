package pl.karol202.paintplus.util

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.util.Size
import android.util.SizeF
import kotlin.math.floor
import kotlin.math.max

fun squareSize(width: Int) = Size(width, width)

fun Size.isPositive() = width > 0 && height > 0

fun Size.toRect() = Rect(0, 0, width, height)

fun Size.toRectF() = RectF(0f, 0f, width.toFloat(), height.toFloat())

fun SizeF.toRectF() = RectF(0f, 0f, width, height)

fun Size.toSizeF() = SizeF(width.toFloat(), height.toFloat())

infix fun Size.fitsIn(other: Size) = width <= other.width && height <= other.height

infix fun SizeF.fitsIn(other: SizeF) = width <= other.width && height <= other.height

fun Size.fitInto(maxSize: Size): Size
{
	if(this fitsIn maxSize) return this
	val scaleRatio = this / maxSize
	val higherRatio = max(scaleRatio.x, scaleRatio.y)
	val newWidth = floor(width / higherRatio).toInt()
	val newHeight = floor(height / higherRatio).toInt()
	return Size(newWidth, newHeight)
}

fun SizeF.fitInto(maxSize: SizeF): SizeF
{
	if(this fitsIn maxSize) return this
	val scaleRatio = this / maxSize
	val higherRatio = max(scaleRatio.x, scaleRatio.y)
	val newWidth = width / higherRatio
	val newHeight = height / higherRatio
	return SizeF(newWidth, newHeight)
}

operator fun Size.div(other: Size) = PointF(width / other.width.toFloat(),
                                            height / other.height.toFloat())

operator fun SizeF.div(other: SizeF) = PointF(width / other.width,
                                              height / other.height)
