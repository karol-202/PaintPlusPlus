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
package pl.karol202.paintplus.tool.shape.star

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
import pl.karol202.paintplus.tool.shape.Join
import pl.karol202.paintplus.util.*
import pl.karol202.paintplus.util.MathUtils.distance
import kotlin.math.*

class ShapeStar(context: Context,
                viewService: ViewService,
                colorsService: ColorsService,
                effectsService: EffectsService,
                private val helpersService: HelpersService) :
		AbstractShape(context, viewService, colorsService, effectsService)
{
	private enum class DragType
	{
		CENTER, OUTER_RADIUS, INNER_RADIUS
	}

	private sealed class State
	{
		object Idle : State()

		sealed class Edit : State()
		{
			data class Initial(override val center: Point,
			                   override val outerRadius : Float,
			                   override val innerRadius : Float,
			                   override val angle: Float) : Edit()

			data class CenterDrag(override val center: Point,
			                      override val outerRadius: Float,
			                      override val innerRadius: Float,
			                      override val angle: Float,
			                      val dragStart: Point,
			                      val centerAtStart: Point) : Edit()

			data class OuterRadiusDrag(override val center: Point,
			                           override val outerRadius: Float,
			                           override val innerRadius: Float,
			                           override val angle: Float,
			                           val dragStart: Point,
			                           val outerRadiusAtStart: Float) : Edit()

			data class InnerRadiusDrag(override val center: Point,
			                           override val outerRadius: Float,
			                           override val innerRadius: Float,
			                           override val angle: Float,
			                           val dragStart: Point,
			                           val innerRadiusAtStart: Float) : Edit()

			abstract val center: Point
			abstract val outerRadius: Float
			abstract val innerRadius: Float
			abstract val angle: Float
		}
	}

	override val name get() = R.string.shape_star
	override val icon get() = R.drawable.ic_shape_star_black_24dp
	override val propertiesClass get() = StarProperties::class.java

	var corners by notifying(4).assert { it >= 3 }
	var isFilled by notifying(false)
	var lineWidth by notifying(30)
	var join by notifying(Join.MITTER)

	override val standardPaint by cache({super.standardPaint}, {isFilled}, {lineWidth}, {join}) {
		standardPaint, fill, width, join ->
		Paint(standardPaint).apply {
			style = if(fill) Paint.Style.FILL_AND_STROKE else Paint.Style.STROKE
			strokeWidth = width.toFloat()
			strokeJoin = join.paintJoin
			strokeMiter = 360f
		}
	}
	override val translucentPaint by cache({super.translucentPaint}, {isFilled}, {lineWidth}, {join}) {
		translucentPaint, fill, width, join ->
		Paint(translucentPaint).apply {
			style = if(fill) Paint.Style.FILL_AND_STROKE else Paint.Style.STROKE
			strokeWidth = width.toFloat()
			strokeJoin = join.paintJoin
			strokeMiter = 360f
		}
	}
	private val path by cache({state}) { state ->
		when(state)
		{
			is State.Idle -> null
			is State.Edit -> Path().apply {
				val centralAngle = 360f / corners
				val halfOfCentral = centralAngle / 2f
				for(i in 0 until corners)
				{
					val angleRadA = Math.toRadians((centralAngle * i + state.angle).toDouble()).toFloat()
					val aX = state.center.x + (sin(angleRadA) * state.outerRadius)
					val aY = state.center.y - (cos(angleRadA) * state.outerRadius)

					if(i == 0) moveTo(aX, aY)
					else lineTo(aX, aY)

					val angleRadB = Math.toRadians((centralAngle * i + halfOfCentral + state.angle).toDouble()).toFloat()
					val bX = state.center.x + (sin(angleRadB) * state.innerRadius)
					val bY = state.center.y - (cos(angleRadB) * state.innerRadius)

					lineTo(bX, bY)
				}
				close()
			}
		}
	}
	private val initialOuterToInnerRatio get() = atan((corners * 0.24f).toDouble()).toFloat() * 0.51f

	private var state: State by notifying(State.Idle)

	override val isInEditMode get() = state is State.Edit
	override val bounds get() = path?.computeBounds()?.roundedOut()?.inflated(lineWidth)

	override fun onTouchStart(point: Point)
	{
		state = getInitialState(point)
	}

	private fun getInitialState(point: Point) = when(val state = state)
	{
		is State.Idle -> State.Edit.Initial(snapPoint(point), 0f, 0f, 0f)
		is State.Edit ->
		{
			when(getDragType(point, state))
			{
				null -> State.Edit.Initial(state.center, state.outerRadius, state.innerRadius, state.angle)
				DragType.CENTER -> State.Edit.CenterDrag(state.center, state.outerRadius, state.innerRadius,
				                                         state.angle, point, state.center)
				DragType.OUTER_RADIUS -> State.Edit.OuterRadiusDrag(state.center, state.outerRadius, state.innerRadius,
				                                                    state.angle, point, state.outerRadius)
				DragType.INNER_RADIUS -> State.Edit.InnerRadiusDrag(state.center, state.outerRadius, state.innerRadius,
				                                                    state.angle, point, state.innerRadius)
			}
		}
	}

	private fun getDragType(point: Point, state: State.Edit): DragType?
	{
		val centralAngle = 360f / corners
		val halfOfCentral = centralAngle / 2
		var angle = MathUtils.getAngle(state.center, point).toFloat() - state.angle
		if(angle < 0) angle += 360f
		val angleMod = angle % centralAngle
		val a = abs(angleMod - halfOfCentral)

		val centerToSide = MathUtils.map(a, 0f, halfOfCentral, state.innerRadius, state.outerRadius)
		val distanceToCenter = distance(state.center, point)
		val distanceToSide = abs(distanceToCenter - centerToSide)

		return when
		{
			min(distanceToCenter, distanceToSide) > maxTouchDistancePx -> null
			distanceToCenter < distanceToSide -> DragType.CENTER
			a < centralAngle / 4 -> DragType.INNER_RADIUS
			else -> DragType.OUTER_RADIUS
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
			val outerRadius = distance(state.center, snapped)
			state.copy(outerRadius = outerRadius,
			           innerRadius = outerRadius * initialOuterToInnerRatio,
			           angle = MathUtils.getAngle(state.center, snapped).toFloat() - 90)
		}
		is State.Edit.CenterDrag -> state.copy(center = snapPoint(state.centerAtStart + (point - state.dragStart)))
		is State.Edit.OuterRadiusDrag ->
		{
			val result = getDraggedRadiusPoint(point, state.center, state.dragStart, state.outerRadiusAtStart)

			val outerRadius = distance(state.center.toPointF(), result)
					.coerceAtLeast(getOuterRadiusByRadiusOfInscribedCircle(state.innerRadius))

			state.copy(outerRadius = outerRadius,
			           angle = MathUtils.getAngle(state.center.toPointF(), result).toFloat() - 90)
		}
		is State.Edit.InnerRadiusDrag ->
		{
			val result = getDraggedRadiusPoint(point, state.center, state.dragStart, state.innerRadiusAtStart)

			val innerRadius = distance(state.center.toPointF(), result)
					.coerceAtMost(getRadiusOfInscribedCircle(state.outerRadius))

			state.copy(innerRadius = innerRadius,
			           angle = MathUtils.getAngle(state.center.toPointF(), result).toFloat() - 90)
		}
	}

	private fun getDraggedRadiusPoint(point: Point, center: Point, dragStart: Point, radiusAtStart: Float): PointF
	{
		val theta = Math.toRadians(MathUtils.getAngle(center, point))
		val rCurrent = distance(center, point)
		val rDraggingStart = distance(center, dragStart)
		val rBeginning = radiusAtStart
		val rDelta = rCurrent - rDraggingStart
		val rResult = rBeginning + rDelta
		val x = (rResult * cos(theta)).toFloat() + center.x
		val y = (rResult * sin(theta)).toFloat() + center.y
		return snapPoint(PointF(x, y))
	}

	private fun getRadiusOfInscribedCircle(outerRadius: Float) =
			(outerRadius * cos(Math.PI / corners)).toFloat()

	private fun getOuterRadiusByRadiusOfInscribedCircle(radiusOfInscribedCircle: Float) =
			(radiusOfInscribedCircle / cos(Math.PI / corners)).toFloat()

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
