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
package pl.karol202.paintplus.tool.drag

import android.graphics.Point
import android.graphics.PointF
import androidx.core.graphics.minus
import androidx.core.graphics.toPoint
import pl.karol202.paintplus.R
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.EffectsService
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.tool.StandardTool
import pl.karol202.paintplus.tool.ToolCoordinateSpace
import pl.karol202.paintplus.util.plus
import kotlin.math.abs

class ToolDrag(private val imageService: ImageService,
               viewService: ViewService,
               private val helpersService: HelpersService,
               effectsService: EffectsService,
               private val historyService: HistoryService) :
		StandardTool(imageService, viewService, helpersService, effectsService)
{
	override val name get() = R.string.tool_drag
	override val icon get() = R.drawable.ic_tool_drag_black_24dp
	override val propertiesFragmentClass get() = DragProperties::class.java
	override val inputCoordinateSpace get() = ToolCoordinateSpace.IMAGE_SPACE
	override val isUsingSnapping get() = false

	var isOneAxis = false

	private val actionPreset = Action.namePreset(R.string.history_action_layer_drag)

	private var touchStart: PointF? = null

	override fun onTouchStart(point: PointF, layer: Layer): Boolean
	{
		touchStart = point
		return true
	}

	override fun onTouchMove(point: PointF, layer: Layer): Boolean
	{
		val newPosition = calculateNewLayerPosition(point, touchStart ?: return false, layer)
		imageService.editImage { withLayerUpdated(layer.withPosition(newPosition.x, newPosition.y)) }
		return true
	}

	override fun onTouchStop(point: PointF, layer: Layer): Boolean
	{
		val newPosition = calculateNewLayerPosition(point, touchStart ?: return false, layer)
		historyService.commitAction { commit(layer, newPosition) }
		return true
	}

	private fun commit(oldLayer: Layer, newPosition: Point): Action.ToRevert
	{
		val newLayer = oldLayer.withPosition(newPosition.x, newPosition.y)
		imageService.editImage { withLayerUpdated(newLayer) }
		return actionPreset.toRevert(oldLayer.bitmap) { revert(oldLayer, newPosition) }
	}

	private fun revert(oldLayer: Layer, newPosition: Point): Action.ToCommit
	{
		imageService.editImage { withLayerUpdated(oldLayer) }
		return actionPreset.toCommit(oldLayer.bitmap) { commit(oldLayer, newPosition) }
	}

	private fun calculateNewLayerPosition(touch: PointF, touchStart: PointF, oldLayer: Layer): Point
	{
		val deltaTouch = touch - touchStart
		if(isOneAxis)
		{
			if(abs(deltaTouch.x) >= abs(deltaTouch.y)) deltaTouch.y = 0f
			else deltaTouch.x = 0f
		}
		return helpersService.snapPoint(oldLayer.position + deltaTouch).toPoint()
	}
}
