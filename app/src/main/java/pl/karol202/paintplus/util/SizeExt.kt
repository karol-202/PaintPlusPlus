package pl.karol202.paintplus.util

import android.util.Size

fun squareSize(width: Int) = Size(width, width)

infix fun Size.fitsIn(other: Size) = width <= other.width && height <= other.height

fun Size.isPositive() = width > 0 && height > 0
