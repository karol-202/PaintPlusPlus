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
package pl.karol202.paintplus.tool.shape.line

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import androidx.core.graphics.minus
import androidx.core.graphics.plus
import androidx.core.graphics.toPoint
import androidx.core.graphics.toPointF
import pl.karol202.paintplus.R
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.image.ColorsService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.tool.shape.AbstractShape
import pl.karol202.paintplus.util.MathUtils
import pl.karol202.paintplus.util.cache
import kotlin.math.max
import kotlin.math.min

class ShapeLine(context: Context,
                viewService: ViewService,
                colorsService: ColorsService,
                private val helpersService: HelpersService) : AbstractShape(context, viewService, colorsService)
{
	private enum class DragType
	{
		START, END
	}

	private sealed class State
	{
		object Idle : State()

		sealed class Edit : State()
		{
			data class Initial(override val start: Point,
			                   override val end: Point) : Edit()

			data class Drag(override val start: Point,
			                override val end: Point,
			                val dragType: DragType,
			                val dragStart: Point,
			                val pointAtStart: Point) : Edit()

			abstract val start: Point
			abstract val end: Point
		}
	}

	override val name get() = R.string.shape_line
	override val icon get() = R.drawable.ic_shape_line_black_24dp
	override val propertiesClass get() = LineProperties::class.java

	var lineWidth by notifying(10)
	var lineCap by notifying(Cap.ROUND)

	override val standardPaint by cache({super.standardPaint}, {lineWidth}, {lineCap}) { standardPaint, width, cap ->
		Paint(standardPaint).apply {
			strokeWidth = width.toFloat()
			strokeCap = cap.paintCap
		}
	}
	override val translucentPaint by cache({super.translucentPaint}, {lineWidth}, {lineCap}) { translucentPaint, width, cap ->
		Paint(translucentPaint).apply {
			strokeWidth = width.toFloat()
			strokeCap = cap.paintCap
		}
	}

	private var state: State by notifying(State.Idle)

	override val isInEditMode get() = state is State.Edit
	override val bounds get() = when(val state = state)
	{
		is State.Idle -> null
		is State.Edit -> Rect(min(state.start.x, state.end.x) - lineWidth,
		                      min(state.start.y, state.end.y) - lineWidth,
		                      max(state.start.x, state.end.y) + lineWidth,
		                      max(state.start.x, state.end.y) + lineWidth)
	}

	override fun onTouchStart(point: Point)
	{
		state = getInitialState(point)
	}

	private fun getInitialState(point: Point) = when(val state = state)
	{
		is State.Idle -> State.Edit.Initial(snapPoint(point), snapPoint(point))
		is State.Edit ->
		{
			when(getDragType(point, state))
			{
				null -> State.Edit.Initial(state.start, state.end)
				DragType.START -> State.Edit.Drag(state.start, state.end, DragType.START, point, state.start)
				DragType.END -> State.Edit.Drag(state.start, state.end, DragType.END, point, state.end)
			}

		}
	}

	private fun getDragType(point: Point, state: State.Edit): DragType?
	{
		val distanceToStart = MathUtils.distance(state.start, point)
		val distanceToEnd = MathUtils.distance(state.end, point)
		return when
		{
			min(distanceToStart, distanceToEnd) > maxTouchDistancePx -> null
			distanceToStart < distanceToEnd -> DragType.START
			else -> DragType.END
		}
	}

	override fun onTouchMove(point: Point)
	{
		state = getEditedState(point) ?: return
	}

	override fun onTouchStop(point: Point)
	{
		state = getEditedState(point) ?: return
	}

	private fun getEditedState(point: Point) = when(val state = state)
	{
		is State.Idle -> null
		is State.Edit.Initial -> state.copy(end = snapPoint(point))
		is State.Edit.Drag ->
		{
			val newPoint = state.pointAtStart + (point - state.dragStart)
			when(state.dragType)
			{
				DragType.START -> state.copy(start = snapPoint(newPoint))
				DragType.END -> state.copy(end = snapPoint(newPoint))
			}
		}
	}

	private fun snapPoint(point: Point) = helpersService.snapPoint(point.toPointF()).toPoint()

	override fun drawOnLayer(canvas: Canvas, translucent: Boolean)
	{
		val editState = state as? State.Edit ?: return
		val paint = if(translucent) translucentPaint else standardPaint
		canvas.drawLine(editState.start.x.toFloat(), editState.start.y.toFloat(),
		                editState.end.x.toFloat(), editState.end.y.toFloat(), paint)
	}

	override fun apply(imageCanvas: Canvas)
	{
		val editState = state as? State.Edit ?: return
		imageCanvas.drawLine(editState.start.x.toFloat(), editState.start.y.toFloat(),
		                     editState.end.x.toFloat(), editState.end.y.toFloat(), standardPaint)
		cancel()
	}

	override fun cancel()
	{
		state = State.Idle
	}
}
