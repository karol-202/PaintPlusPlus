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
import android.graphics.Point
import android.util.DisplayMetrics
import android.util.TypedValue
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt

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
			sqrt((first.x - second.x).toDouble().pow(2.0) +
					     (first.y - second.y).toDouble().pow(2.0)).toFloat()

	@JvmStatic
	fun dpToPixels(context: Context, dp: Float) =
			TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)

	@JvmStatic
	fun getAngle(center: Point, point: Point): Double
	{
		val deltaX = (point.x - center.x).toDouble()
		val deltaY = (center.y - point.y).toDouble()
		val ratio = deltaX / deltaY
		val angleRad = atan(ratio)
		var angleDeg = Math.toDegrees(angleRad)
		if(deltaY < 0) angleDeg += 180.0
		if(angleDeg < 0) angleDeg += 360.0
		return angleDeg
	}
}
