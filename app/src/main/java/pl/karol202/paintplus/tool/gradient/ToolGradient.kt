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

import android.content.Context
import android.graphics.*
import androidx.core.graphics.*
import pl.karol202.paintplus.R
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.EffectsService
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.tool.OnToolChangeListener
import pl.karol202.paintplus.tool.StandardTool
import pl.karol202.paintplus.tool.ToolCoordinateSpace
import pl.karol202.paintplus.tool.gradient.shape.GradientShape
import pl.karol202.paintplus.util.MathUtils
import pl.karol202.paintplus.util.duplicated
import kotlin.math.min

private const val POINT_OUTER_RADIUS_DP = 5f
private const val POINT_INNER_RADIUS_DP = 2f
private const val MAX_TOUCH_DISTANCE_DP = 35f

class ToolGradient(private val context: Context,
                   private val imageService: ImageService,
                   private val viewService: ViewService,
                   effectsService: EffectsService,
                   private val helpersService: HelpersService,
                   private val historyService: HistoryService,
                   val gradientShapes: List<GradientShape>) :
		StandardTool(imageService, viewService, helpersService, effectsService),
		OnToolChangeListener
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

	private val pointOuterRadiusPx = MathUtils.dpToPixels(context, POINT_OUTER_RADIUS_DP)
	private val pointInnerRadiusPx = MathUtils.dpToPixels(context, POINT_INNER_RADIUS_DP)
	private val maxTouchDistancePx get() = MathUtils.dpToPixels(context, MAX_TOUCH_DISTANCE_DP) / viewService.zoom

	override val name get() = R.string.tool_gradient
	override val icon get() = R.drawable.ic_tool_gradient_black_24dp
	override val propertiesFragmentClass get() = GradientProperties::class.java
	override val inputCoordinateSpace get() = ToolCoordinateSpace.IMAGE_SPACE
	override val isUsingSnapping get() = false

	var shape by notifying(gradientShapes.first())
	var gradient: Gradient
		get() = shape.gradient
		set(value) = gradientShapes.forEach { it.gradient = value }
	var repeatability: GradientRepeatability
		get() = shape.repeatability
		set(value) = gradientShapes.forEach { it.repeatability = value }
	var isReverted: Boolean
		get() = shape.isReverted
		set(value) = gradientShapes.forEach { it.isReverted = value }

	private val actionPreset = Action.namePreset(R.string.tool_gradient)

	private val pointOuterPaint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		color = Color.DKGRAY
	}
	private val pointInnerPaint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		color = Color.WHITE
	}

	private var state: State by notifying(State.Idle)
	val isInEditMode get() = state is State.Edit

	override fun onTouchStart(point: PointF, layer: Layer): Boolean
	{
		state = getInitialState(point.toPoint())
		return true
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

	override fun onTouchMove(point: PointF, layer: Layer): Boolean
	{
		state = getEditedState(point.toPoint()) ?: return false
		return true
	}

	override fun onTouchStop(point: PointF, layer: Layer): Boolean
	{
		state = getEditedState(point.toPoint()) ?: return false
		return true
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

	override fun drawOnLayer(canvas: Canvas, layer: Layer)
	{
		if(layer != imageService.image.selectedLayer || !layer.visible || !isInEditMode) return
		val editState = state as? State.Edit ?: return
		canvas.withImageSpace {
			withImageClip {
				withLayerClip(layer) {
					withSelectionClip {
						shape.onScreenDraw(canvas, editState.start, editState.end)
					}
				}
			}
		}
	}

	override fun drawOnTop(canvas: Canvas)
	{
		val editState = state as? State.Edit ?: return
		canvas.withImageSpace {
			drawPoint(canvas, editState.start)
			drawPoint(canvas, editState.end)
		}
	}

	private fun drawPoint(canvas: Canvas, point: Point)
	{
		val outerSize = pointOuterRadiusPx / viewService.zoom
		val innerSize = pointInnerRadiusPx / viewService.zoom
		canvas.drawOval(RectF(point.x - outerSize,
		                      point.y - outerSize,
		                      point.x + outerSize,
		                      point.y + outerSize), pointOuterPaint)
		canvas.drawOval(RectF(point.x - innerSize,
		                      point.y - innerSize,
		                      point.x + innerSize,
		                      point.y + innerSize), pointInnerPaint)
	}

	fun apply()
	{
		val layer = imageService.image.selectedLayer ?: return
		val editState = state as? State.Edit ?: return
		val bitmap = Bitmap.createBitmap(layer.width, layer.height, Bitmap.Config.ARGB_8888).applyCanvas {
			withTranslation(-layer.x.toFloat(), -layer.y.toFloat()) {
				withSelectionClip {
					shape.applyGradient(this, editState.start, editState.end)
				}
			}
		}

		historyService.commitAction { commit(layer, bitmap) }
		cancel()
	}

	private fun commit(layer: Layer, newBitmap: Bitmap): Action.ToRevert
	{
		val oldBitmap = layer.bitmap.duplicated()
		layer.editCanvas.drawBitmap(newBitmap, 0f, 0f, null)
		return actionPreset.toRevert(oldBitmap) { revert(layer, oldBitmap) }
	}

	private fun revert(layer: Layer, oldBitmap: Bitmap): Action.ToCommit
	{
		val newBitmap = layer.bitmap.duplicated()
		layer.editCanvas.drawBitmap(oldBitmap, 0f, 0f, null)
		return actionPreset.toCommit(newBitmap) { commit(layer, newBitmap) }
	}

	fun cancel()
	{
		state = State.Idle
	}

	override fun onToolSelected() {}

	override fun onOtherToolSelected() = cancel()
}
