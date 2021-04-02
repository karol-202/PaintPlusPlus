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
package pl.karol202.paintplus.helpers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.Size
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import pl.karol202.paintplus.image.ViewPosition
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.util.cache
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

private const val LINE_WIDTH = 1
private const val LINE_OFFSET_CONSTANT = 40
private const val LINE_OFFSET_BASE = 20
private const val SNAP_DISTANCE_DP = 15

class Grid(context: Context,
           private val viewService: ViewService) : SnappingHelper
{
	private data class LinesSet(val vertical: List<Int>,
	                            val horizontal: List<Int>)

	private enum class State
	{
		DISABLED, VISIBLE, VISIBLE_WITH_SNAPPING
	}

	private val _stateFlow = MutableStateFlow(State.DISABLED)

	override val updateEventFlow = _stateFlow.map { }

	private val isEnabled get() = _stateFlow.value != State.DISABLED
	private val isSnappingEnabled get() = _stateFlow.value == State.VISIBLE_WITH_SNAPPING

	private val density = context.resources.displayMetrics.density
	private val snapDistance = (SNAP_DISTANCE_DP * density).toInt()

	private val paint = Paint().apply {
		strokeWidth = LINE_WIDTH.toFloat()
	}

	private val lines by cache({viewService.viewPosition}, {viewService.viewportSize}) { viewPosition, viewportSize ->
		createLines(viewPosition, viewportSize)
	}

	private fun createLines(viewPosition: ViewPosition, canvasSize: Size): LinesSet
	{
		val offset = calculateLineOffset(viewPosition)
		val right = viewPosition.x + canvasSize.width / viewPosition.zoom
		val bottom = viewPosition.y + canvasSize.height / viewPosition.zoom
		val firstVerticalLineIndex = ceil((viewPosition.x / offset)).toInt()
		val lastVerticalLineIndex = floor((right / offset)).toInt()
		val firstHorizontalLineIndex = ceil((viewPosition.y / offset)).toInt()
		val lastHorizontalLineIndex = floor((bottom / offset)).toInt()

		return LinesSet(vertical = (firstVerticalLineIndex..lastVerticalLineIndex).map { index -> index * offset },
		                horizontal = (firstHorizontalLineIndex..lastHorizontalLineIndex).map { index -> index * offset })
	}

	private fun calculateLineOffset(viewPosition: ViewPosition): Int
	{
		val offsetRaw = density * LINE_OFFSET_CONSTANT / viewPosition.zoom
		var nearestOffset = LINE_OFFSET_BASE
		var offset = LINE_OFFSET_BASE
		while(abs(offsetRaw - offset) <= abs(offsetRaw - nearestOffset))
		{
			nearestOffset = offset
			offset *= 2
		}
		return nearestOffset
	}

	override fun onScreenDraw(canvas: Canvas)
	{
		if(isEnabled) drawLines(canvas)
	}

	private fun drawLines(canvas: Canvas)
	{
		for(imageX in lines.vertical)
		{
			val canvasX = ((imageX - viewService.viewX) * viewService.zoom).toInt()
			canvas.drawLine(canvasX.toFloat(), 0f, canvasX.toFloat(), canvas.height.toFloat(), paint)
		}
		for(imageY in lines.horizontal)
		{
			val canvasY = ((imageY - viewService.viewY) * viewService.zoom).toInt()
			canvas.drawLine(0f, canvasY.toFloat(), canvas.width.toFloat(), canvasY.toFloat(), paint)
		}
	}

	override fun snapX(x: Float) = snapValue(x, lines.vertical)

	override fun snapY(y: Float) = snapValue(y, lines.horizontal)

	override fun snapPoint(point: PointF) = PointF(snapX(point.x), snapY(point.y))

	private fun snapValue(value: Float, lines: List<Int>): Float
	{
		if(!isEnabled || !isSnappingEnabled) return value
		var squareIndex = -1
		var line = 0
		while(line < lines.size && lines[line] <= value) squareIndex = line++

		val start = if(squareIndex != -1) lines[squareIndex] else -1
		val end = if(squareIndex + 1 < lines.size) lines[squareIndex + 1] else -1
		val startSnap = value - start < snapDistance && start != -1
		val endSnap = end - value < snapDistance && end != -1
		return when
		{
			startSnap && !endSnap -> start.toFloat()
			!startSnap && endSnap -> end.toFloat()
			startSnap && endSnap ->
				if(value - start <= end - value) start.toFloat()
				else end.toFloat()
			else -> value
		}
	}

	fun toggleGrid()
	{
		_stateFlow.value = when(_stateFlow.value)
		{
			State.DISABLED -> State.VISIBLE
			else -> State.DISABLED
		}
	}

	fun toggleSnapping()
	{
		_stateFlow.value = when(_stateFlow.value)
		{
			State.DISABLED -> State.DISABLED
			State.VISIBLE -> State.VISIBLE_WITH_SNAPPING
			State.VISIBLE_WITH_SNAPPING -> State.VISIBLE
		}
	}
}
