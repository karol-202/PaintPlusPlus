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
package pl.karol202.paintplus.tool.shape.polygon

import android.content.Context
import android.graphics.*
import androidx.core.graphics.minus
import androidx.core.graphics.plus
import androidx.core.graphics.toPoint
import androidx.core.graphics.toPointF
import pl.karol202.paintplus.R
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.image.ColorsService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.tool.shape.AbstractShape
import pl.karol202.paintplus.tool.shape.Join
import pl.karol202.paintplus.util.*
import pl.karol202.paintplus.util.MathUtils.distance
import pl.karol202.paintplus.util.MathUtils.getAngle
import kotlin.math.*

class ShapePolygon(context: Context,
                   viewService: ViewService,
                   colorsService: ColorsService,
                   private val helpersService: HelpersService) : AbstractShape(context, viewService, colorsService)
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
			                   override val radiusOCC: Float,
			                   override val angle: Float) : Edit()

			data class CenterDrag(override val center: Point,
			                      override val radiusOCC: Float,
			                      override val angle: Float,
			                      val dragStart: Point,
			                      val centerAtStart: Point) : Edit()

			data class RadiusDrag(override val center: Point,
			                      override val radiusOCC: Float,
			                      override val angle: Float,
			                      val dragStart: Point,
			                      val radiusAtStart: Float) : Edit()

			abstract val center: Point
			abstract val radiusOCC: Float // Radius of circumscribed circle
			abstract val angle: Float
		}
	}

	override val name get() = R.string.shape_polygon
	override val icon get() = R.drawable.ic_shape_polygon_black_24dp
	override val propertiesClass get() = PolygonProperties::class.java

	var sides by notifying(4).assert { it >= 3 }
	var isFilled by notifying(false)
	var lineWidth by notifying(30)
	var join by notifying(Join.MITTER)

	override val standardPaint by cache({super.standardPaint}, {isFilled}, {lineWidth}, {join}) {
		standardPaint, fill, width, join ->
		Paint(standardPaint).apply {
			style = if(fill) Paint.Style.FILL_AND_STROKE else Paint.Style.STROKE
			strokeWidth = width.toFloat()
			strokeJoin = join.paintJoin
		}
	}
	override val translucentPaint by cache({super.translucentPaint}, {isFilled}, {lineWidth}, {join}) {
		translucentPaint, fill, width, join ->
		Paint(translucentPaint).apply {
			style = if(fill) Paint.Style.FILL_AND_STROKE else Paint.Style.STROKE
			strokeWidth = width.toFloat()
			strokeJoin = join.paintJoin
		}
	}
	private val path by cache({state}) { state ->
		when(state)
		{
			is State.Idle -> null
			is State.Edit -> Path().apply {
				val centralAngle = 360f / sides
				for(i in 0 until sides)
				{
					val angleRad = Math.toRadians((centralAngle * i + state.angle).toDouble()).toFloat()
					val x = state.center.x + (sin(angleRad.toDouble()) * state.radiusOCC).toFloat()
					val y = state.center.y - (cos(angleRad.toDouble()) * state.radiusOCC).toFloat()
					if(i == 0) moveTo(x, y)
					else lineTo(x, y)
				}
				close()
			}
		}
	}

	private var state: State by notifying(State.Idle)

	override val isInEditMode get() = state is State.Edit
	override val bounds get() = path?.computeBounds()?.roundedOut()?.inflated(lineWidth)

	override fun onTouchStart(point: Point)
	{
		state = getInitialState(point)
	}

	private fun getInitialState(point: Point) = when(val state = state)
	{
		is State.Idle -> State.Edit.Initial(snapPoint(point), 0f, 0f)
		is State.Edit ->
		{
			when(getDragType(point, state))
			{
				null -> State.Edit.Initial(state.center, state.radiusOCC, state.angle)
				DragType.CENTER -> State.Edit.CenterDrag(state.center, state.radiusOCC, state.angle, point, state.center)
				DragType.RADIUS -> State.Edit.RadiusDrag(state.center, state.radiusOCC, state.angle, point, state.radiusOCC)
			}
		}
	}

	private fun getDragType(point: Point, state: State.Edit): DragType?
	{
		val side = (2 * state.radiusOCC * sin(Math.PI / sides)).toFloat()
		val radiusOIC = (side / (2 * tan(Math.PI / sides))).toFloat()
		val centralAngle = 360f / sides
		val halfOfCentral = centralAngle / 2
		var angle = getAngle(state.center, point).toFloat() - state.angle
		if(angle < 0) angle += 360f
		val angleMod = angle % centralAngle
		val a = abs(angleMod - halfOfCentral)
		val centerToSide = MathUtils.map(a, 0f, halfOfCentral, radiusOIC, state.radiusOCC)

		val distanceToCenter = distance(state.center, point)
		val distanceToSide = abs(distanceToCenter - centerToSide)
		return when
		{
			min(distanceToCenter, distanceToSide) > maxTouchDistancePx -> null
			distanceToCenter < distanceToSide -> DragType.CENTER
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
		is State.Edit.Initial ->
		{
			val snapped = snapPoint(point)
			state.copy(radiusOCC = distance(state.center, snapped),
			           angle = getAngle(state.center, snapped).toFloat() - 90)
		}
		is State.Edit.CenterDrag ->
		{
			val newPoint = state.centerAtStart + (point - state.dragStart)
			state.copy(center = snapPoint(newPoint))
		}
		is State.Edit.RadiusDrag ->
		{
			val theta = Math.toRadians(getAngle(state.center, point))
			val rCurrent = distance(state.center, point)
			val rDraggingStart = distance(state.center, state.dragStart)
			val rBeginning = state.radiusAtStart
			val rDelta = rCurrent - rDraggingStart
			val rResult = rBeginning + rDelta
			val x = (rResult * cos(theta)).toFloat() + state.center.x
			val y = (rResult * sin(theta)).toFloat() + state.center.y
			val result = snapPoint(PointF(x, y))

			state.copy(radiusOCC = distance(state.center.toPointF(), snapPoint(result)),
			           angle = getAngle(state.center.toPointF(), result).toFloat() - 90)
		}
	}

	private fun snapPoint(point: Point) = helpersService.snapPoint(point.toPointF()).toPoint()

	private fun snapPoint(point: PointF) = helpersService.snapPoint(point)

	override fun drawOnLayer(canvas: Canvas, translucent: Boolean)
	{
		val paint = if(translucent) translucentPaint else standardPaint
		canvas.drawPath(path ?: return, paint)
	}

	override fun apply(imageCanvas: Canvas)
	{
		imageCanvas.drawPath(path ?: return, standardPaint)
		cancel()
	}

	override fun cancel()
	{
		state = State.Idle
	}
}
