/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package pl.karol202.paintplus.util

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.PointF
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.core.graphics.toPointF
import pl.karol202.paintplus.util.MathUtils.map
import kotlin.math.*

object MathUtils
{
	@JvmStatic
	fun map(src: Float, srcMin: Float, srcMax: Float, dstMin: Float, dstMax: Float) =
			lerp((src - srcMin) / (srcMax - srcMin), dstMin, dstMax)

	@JvmStatic
	fun lerp(value: Float, v1: Float, v2: Float) =
			v1 + value * (v2 - v1)

	@JvmStatic
	fun clamp(value: Float, min: Float, max: Float) =
			value.coerceIn(min, max)

	@JvmStatic
	fun distance(first: Point, second: Point) =
			hypot((first.x - second.x).toFloat(), (first.y - second.y).toFloat())

	@JvmStatic
	fun distance(first: PointF, second: PointF) =
			hypot(first.x - second.x, first.y - second.y)

	@JvmStatic
	fun dpToPixels(context: Context, dp: Float) =
			TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)

	@JvmStatic
	fun getAngle(center: Point, point: Point) = getAngle(center.toPointF(), point.toPointF())

	@JvmStatic
	fun getAngle(center: PointF, point: PointF): Double
	{
		val deltaX = point.x - center.x
		val deltaY = center.y - point.y
		val ratio = deltaX / deltaY
		val angleRad = atan(ratio)
		var angleDeg = Math.toDegrees(angleRad.toDouble())
		if(deltaY < 0) angleDeg += 180f
		if(angleDeg < 0) angleDeg += 360f
		return angleDeg
	}

	@JvmStatic
	fun mapColor(src: Float, srcMin: Float, srcMax: Float, colorMin: Int, colorMax: Int): Int
	{
		val alpha = map(src, srcMin, srcMax, Color.alpha(colorMin).toFloat(), Color.alpha(colorMax).toFloat()).roundToInt()
		val red = map(src, srcMin, srcMax, Color.red(colorMin).toFloat(), Color.red(colorMax).toFloat()).roundToInt()
		val green = map(src, srcMin, srcMax, Color.green(colorMin).toFloat(), Color.green(colorMax).toFloat()).roundToInt()
		val blue = map(src, srcMin, srcMax, Color.blue(colorMin).toFloat(), Color.blue(colorMax).toFloat()).roundToInt()
		return Color.argb(alpha, red, green, blue)
	}
}
