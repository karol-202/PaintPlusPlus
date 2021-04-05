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
package pl.karol202.paintplus.tool.gradient

import android.graphics.Color
import pl.karol202.paintplus.util.MathUtils
import pl.karol202.paintplus.util.MathUtils.map
import java.util.*
import kotlin.math.roundToInt

class Gradient private constructor(val points: List<Point>)
{
	data class Point(val id: String,
	                 val position: Float,
	                 val color: Int)
	{
		companion object
		{
			fun create(position: Float, color: Int) = Point(createRandomId(), position, color)

			private fun createRandomId() = UUID.randomUUID().toString()
		}
	}

	companion object
	{
		@JvmStatic
		fun createSimpleGradient(firstColor: Int, secondColor: Int) =
				Gradient(listOf(Point.create(0f, firstColor), Point.create(1f, secondColor)))
	}

	val pointsAmount get() = points.size

	val positionsArray get() = points.map { it.position }.toFloatArray()
	val revertedPositionsArray get() = points.map { 1f - it.position }.reversed().toFloatArray()
	val colorsArray get() = points.map { it.color }.toIntArray()
	val revertedColorsArray get() = points.map { it.color }.reversed().toIntArray()

	operator fun get(id: String) = points.singleOrNull { it.id == id }

	fun withPointAdded(point: Point) =
			Gradient((points + point).sortedBy { it.position })

	fun getColorAtPosition(position: Float): Int
	{
		if(points.isEmpty()) return Color.BLACK
		else if(points.size == 1) return points.first().color

		val (_, firstPos, firstColor) = points.first()
		if(position <= firstPos) return firstColor

		return points.windowed(size = 2)
				.firstOrNull { (_, next) -> position <= next.position }
				?.let { (previous, next) -> MathUtils.mapColor(position, previous.position, next.position, previous.color, next.color) }
				?: points.last().color
	}

	fun withPointUpdated(point: Point) =
			Gradient(points.map { if(it.id == point.id) point else it }.sortedBy { it.position })

	fun withPointDeleted(point: Point) =
			Gradient(points - point)

	override fun equals(other: Any?) = this === other || points == (other as? Gradient)?.points

	override fun hashCode() = points.hashCode()
}
