package pl.karol202.paintplus.util

import android.graphics.Rect
import android.util.Size
import kotlin.math.floor
import kotlin.math.max

fun squareSize(width: Int) = Size(width, width)

fun Size.isPositive() = width > 0 && height > 0

infix fun Size.fitsIn(other: Size) = width <= other.width && height <= other.height

fun Size.fitInto(maxSize: Size): Size
{
	if(this fitsIn maxSize) return this
	val widthRatio = width / maxSize.width.toDouble()
	val heightRatio = height / maxSize.height.toDouble()
	val higherRatio = max(widthRatio, heightRatio)
	val newWidth = floor(width / higherRatio).toInt()
	val newHeight = floor(height / higherRatio).toInt()
	return Size(newWidth, newHeight)
}

fun Size.toRect() = Rect(0, 0, width, height)
