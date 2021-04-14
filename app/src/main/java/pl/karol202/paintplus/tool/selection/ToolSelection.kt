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
package pl.karol202.paintplus.tool.selection

import android.content.Context
import android.graphics.*
import androidx.core.graphics.*
import pl.karol202.paintplus.R
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.*
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.options.OptionSelect
import pl.karol202.paintplus.tool.StandardTool
import pl.karol202.paintplus.tool.ToolCoordinateSpace
import pl.karol202.paintplus.util.*
import pl.karol202.paintplus.util.MathUtils.dpToPixels
import kotlin.math.abs
import kotlin.math.roundToInt

private const val MAX_DRAG_DISTANCE_DP = 50f
private const val SELECTION_LINE_WIDTH_DP = 1f

class ToolSelection(context: Context,
                    private val imageService: ImageService,
                    private val viewService: ViewService,
                    effectsService: EffectsService,
                    private val helpersService: HelpersService,
                    private val historyService: HistoryService,
                    private val optionSelect: OptionSelect) :
		StandardTool(imageService, viewService, helpersService, effectsService)
{
	private enum class MoveType
	{
		NONE,
		LEFT_TOP_CORNER, RIGHT_TOP_CORNER, LEFT_BOTTOM_CORNER, RIGHT_BOTTOM_CORNER,
		LEFT_SIDE, TOP_SIDE, RIGHT_SIDE, BOTTOM_SIDE,
		MOVE
	}

	private sealed class State
	{
		object Idle : State()

		sealed class Edit : State()
		{
			data class Initial(override val rect: Rect) : Edit()

			data class Adjust(override val rect: Rect,
			                  val rectAtStart: Rect,
			                  val moveType: MoveType,
			                  val moveStart: Point) : Edit()

			abstract val rect: Rect
		}
	}

	private val maxDragDistancePx = dpToPixels(context, MAX_DRAG_DISTANCE_DP)
	private val selectionLineWidthPx = dpToPixels(context, SELECTION_LINE_WIDTH_DP)

	override val name get() = R.string.tool_selection
	override val icon get() = R.drawable.ic_tool_selection_black_24dp
	override val propertiesFragmentClass get() = SelectionProperties::class.java
	override val inputCoordinateSpace get() = ToolCoordinateSpace.IMAGE_SPACE
	override val isUsingSnapping get() = false

	var shape = ToolSelectionShape.RECTANGLE
	var mode = ToolSelectionMode.NEW

	private val actionPreset = Action.namePreset(R.string.history_action_selection_change).withPreview {
		optionSelect.createPreviewBitmap()
	}
	private val paint = Paint().apply {
		isAntiAlias = true
		color = Color.BLACK
		style = Paint.Style.STROKE
	}

	private var state by notifying<State>(State.Idle)
	val isInEditMode get() = state is State.Edit

	override fun onTouchStart(point: PointF, layer: Layer): Boolean
	{
		state = getInitialState(point.toPoint())
		return true
	}

	private fun getInitialState(point: Point) = when(val state = state)
	{
		is State.Idle -> State.Edit.Initial(Rect(snapX(point.x), snapY(point.y),
		                                         snapX(point.x), snapY(point.y)))
		is State.Edit -> State.Edit.Adjust(state.rect, state.rect, getMoveType(point, state.rect), point)
	}

	private fun getMoveType(point: Point, rect: Rect): MoveType
	{
		val x = point.x
		val y = point.y

		val leftDist = abs(rect.left - x)
		val topDist = abs(rect.top - y)
		val rightDist = abs(rect.right - x)
		val bottomDist = abs(rect.bottom - y)

		val left: Boolean = leftDist < maxDragDistancePx / viewService.zoom
		val top: Boolean = topDist < maxDragDistancePx / viewService.zoom
		val right: Boolean = rightDist < maxDragDistancePx / viewService.zoom
		val bottom: Boolean = bottomDist < maxDragDistancePx / viewService.zoom

		val xInside = x > rect.left && x < rect.right
		val yInside = y > rect.top && y < rect.bottom

		return when
		{
			xInside && yInside -> MoveType.MOVE
			left && !right && yInside -> MoveType.LEFT_SIDE
			top && !bottom && xInside -> MoveType.TOP_SIDE
			right && !left && yInside -> MoveType.RIGHT_SIDE
			bottom && !top && xInside -> MoveType.BOTTOM_SIDE
			left && top -> MoveType.LEFT_TOP_CORNER
			right && top -> MoveType.RIGHT_TOP_CORNER
			right && bottom -> MoveType.RIGHT_BOTTOM_CORNER
			left && bottom -> MoveType.LEFT_BOTTOM_CORNER
			else -> MoveType.NONE
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
		is State.Edit.Initial -> state.copy(rect = state.rect.copy(right = snapX(point.x),
		                                                           bottom = snapY(point.y)).sorted())
		is State.Edit.Adjust -> state.copy(rect = getAdjustedRect(point, state))
	}

	private fun getAdjustedRect(point: Point, state: State.Edit.Adjust): Rect
	{
		val deltaX = point.x - state.moveStart.x
		val deltaY = point.y - state.moveStart.y
		return when(state.moveType)
		{
			MoveType.NONE -> state.rectAtStart
			MoveType.LEFT_TOP_CORNER -> state.rectAtStart.copy(left = snapX(state.rectAtStart.left + deltaX),
			                                                   top = snapY(state.rectAtStart.top + deltaY))
			MoveType.RIGHT_TOP_CORNER -> state.rectAtStart.copy(right = snapX(state.rectAtStart.right + deltaX),
			                                                    top = snapY(state.rectAtStart.top + deltaY))
			MoveType.LEFT_BOTTOM_CORNER -> state.rectAtStart.copy(left = snapX(state.rectAtStart.left + deltaX),
			                                                      bottom = snapY(state.rectAtStart.bottom + deltaY))
			MoveType.RIGHT_BOTTOM_CORNER -> state.rectAtStart.copy(right = snapX(state.rectAtStart.right + deltaX),
			                                                       bottom = snapY(state.rectAtStart.bottom + deltaY))
			MoveType.LEFT_SIDE -> state.rectAtStart.copy(left = snapX(state.rectAtStart.left + deltaX))
			MoveType.TOP_SIDE -> state.rectAtStart.copy(top = snapY(state.rectAtStart.top + deltaY))
			MoveType.RIGHT_SIDE -> state.rectAtStart.copy(right = snapX(state.rectAtStart.right + deltaX))
			MoveType.BOTTOM_SIDE -> state.rectAtStart.copy(bottom = snapY(state.rectAtStart.bottom + deltaY))
			MoveType.MOVE ->
			{
				val oldCenter = state.rectAtStart.center()
				val newCenter = snapPoint(oldCenter + Point(deltaX, deltaY))
				state.rectAtStart + (newCenter - oldCenter)
			}
		}.sorted()
	}

	private fun snapX(x: Int) = helpersService.snapX(x.toFloat()).roundToInt()

	private fun snapY(y: Int) = helpersService.snapY(y.toFloat()).roundToInt()

	private fun snapPoint(point: Point) = helpersService.snapPoint(point.toPointF()).toPoint()

	fun applySelection()
	{
		val rect = (state as? State.Edit.Adjust)?.rect ?: return
		val apply: Selection.() -> Selection = when(shape)
		{
			ToolSelectionShape.RECTANGLE -> {{ withRectangleOperation(rect, mode.op) }}
			ToolSelectionShape.OVAL -> {{ withOvalOperation(rect, mode.op) }}
		}
		state = State.Idle
		historyService.commitAction { commit(apply) }
	}

	private fun commit(apply: Selection.() -> Selection): Action.ToRevert = actionPreset.commit {
		val oldSelection = imageService.selection
		imageService.editSelection { apply() }
		toRevert { revert(apply, oldSelection) }
	}

	private fun revert(apply: Selection.() -> Selection, oldSelection: Selection): Action.ToCommit = actionPreset.revert {
		imageService.setSelection(oldSelection)
		toCommit { commit(apply) }
	}

	fun cancelSelection()
	{
		state = State.Idle
	}

	override fun drawOnTop(canvas: Canvas) = canvas.withImageSpace {
		val editState = state as? State.Edit ?: return@withImageSpace
		paint.strokeWidth = selectionLineWidthPx / viewService.zoom
		when(shape)
		{
			ToolSelectionShape.RECTANGLE -> canvas.drawRect(editState.rect, paint)
			ToolSelectionShape.OVAL -> canvas.drawOval(editState.rect.toRectF(), paint)
		}
	}
}
