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
package pl.karol202.paintplus.tool.shape.circle

import android.content.Context
import android.graphics.*
import androidx.core.graphics.minus
import androidx.core.graphics.plus
import androidx.core.graphics.toPoint
import androidx.core.graphics.toPointF
import pl.karol202.paintplus.R
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.image.ColorsService
import pl.karol202.paintplus.image.EffectsService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.tool.shape.AbstractShape
import pl.karol202.paintplus.util.MathUtils
import pl.karol202.paintplus.util.MathUtils.distance
import pl.karol202.paintplus.util.cache
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class ShapeCircle(context: Context,
                  viewService: ViewService,
                  colorsService: ColorsService,
                  effectsService: EffectsService,
                  private val helpersService: HelpersService) :
		AbstractShape(context, viewService, colorsService, effectsService)
{
	private enum class DragType
	{
		CENTER, RADIUS
	}

	private sealed class State
	{
		object Idle : State()

		sealed class Edit : State()
		{
			data class Initial(override val center: Point,
			                   override val radius: Float) : Edit()

			data class CenterDrag(override val center: Point,
			                      override val radius: Float,
			                      val dragStart: Point,
			                      val centerAtStart: Point) : Edit()

			data class RadiusDrag(override val center: Point,
			                      override val radius: Float,
			                      val dragStart: Point,
			                      val radiusAtStart: Float) : Edit()

			abstract val center: Point
			abstract val radius: Float
		}
	}

	override val name get() = R.string.shape_circle
	override val icon get() = R.drawable.ic_shape_circle_black_24dp
	override val propertiesClass get() = CircleProperties::class.java

	var isFilled by notifying(false)
	var circleWidth by notifying(30)

	override val standardPaint by cache({super.standardPaint}, {isFilled}, {circleWidth}) { standardPaint, fill, width ->
		Paint(standardPaint).apply {
			style = if(fill) Paint.Style.FILL_AND_STROKE else Paint.Style.STROKE
			strokeWidth = width.toFloat()
		}
	}
	override val translucentPaint by cache({super.translucentPaint}, {isFilled}, {circleWidth}) { translucentPaint, fill, width ->
		Paint(translucentPaint).apply {
			style = if(fill) Paint.Style.FILL_AND_STROKE else Paint.Style.STROKE
			strokeWidth = width.toFloat()
		}
	}

	private var state: State by notifying(State.Idle)

	override val isInEditMode get() = state is State.Edit
	override val bounds get() = when(val state = state)
	{
		is State.Idle -> null
		is State.Edit ->
		{
			val halfSize = state.radius.toInt() + circleWidth
			Rect(state.center.x - halfSize, state.center.y - halfSize,
			     state.center.x + halfSize, state.center.y + halfSize)
		}
	}

	override fun onTouchStart(point: Point)
	{
		state = getInitialState(point)
	}

	private fun getInitialState(point: Point) = when(val state = state)
	{
		is State.Idle -> State.Edit.Initial(snapPoint(point), 0f)
		is State.Edit ->
		{
			when(getDragType(point, state))
			{
				null -> State.Edit.Initial(state.center, state.radius)
				DragType.CENTER -> State.Edit.CenterDrag(state.center, state.radius, point, state.center)
				DragType.RADIUS -> State.Edit.RadiusDrag(state.center, state.radius, point, state.radius)
			}

		}
	}

	private fun getDragType(point: Point, state: State.Edit): DragType?
	{
		val distanceToCenter: Float = distance(state.center, point)
		val distanceToRadius = abs(distanceToCenter - state.radius)
		return when
		{
			min(distanceToCenter, distanceToRadius) > maxTouchDistancePx -> null
			distanceToCenter < distanceToRadius -> DragType.CENTER
			else -> DragType.RADIUS
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
		is State.Edit.Initial -> state.copy(radius = distance(state.center, snapPoint(point)))
		is State.Edit.CenterDrag ->
		{
			val newPoint = state.centerAtStart + (point - state.dragStart)
			state.copy(center = snapPoint(newPoint))
		}
		is State.Edit.RadiusDrag ->
		{
			val theta = Math.toRadians(MathUtils.getAngle(state.center, point))
			val rCurrent = distance(state.center, point)
			val rDraggingStart = distance(state.center, state.dragStart)
			val rBeginning = state.radiusAtStart
			val rDelta = rCurrent - rDraggingStart
			val rResult = rBeginning + rDelta
			val x = (rResult * cos(theta)).toFloat() + state.center.x
			val y = (rResult * sin(theta)).toFloat() + state.center.y
			val result = PointF(x, y)

			state.copy(radius = distance(state.center.toPointF(), snapPoint(result)))
		}
	}

	private fun snapPoint(point: Point) = helpersService.snapPoint(point.toPointF()).toPoint()

	private fun snapPoint(point: PointF) = helpersService.snapPoint(point)

	override fun drawOnLayer(canvas: Canvas, translucent: Boolean)
	{
		val editState = state as? State.Edit ?: return
		val paint = if(translucent) translucentPaint else standardPaint
		canvas.drawCircle(editState.center.x.toFloat(), editState.center.y.toFloat(), editState.radius, paint)
	}

	override fun apply(imageCanvas: Canvas)
	{
		val editState = state as? State.Edit ?: return
		imageCanvas.drawCircle(editState.center.x.toFloat(), editState.center.y.toFloat(), editState.radius, standardPaint)
		cancel()
	}

	override fun cancel()
	{
		state = State.Idle
	}
}
