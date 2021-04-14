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
package pl.karol202.paintplus.tool.drawing

import android.graphics.*
import androidx.core.graphics.minus
import androidx.core.graphics.toRect
import androidx.core.graphics.withClip
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.EffectsService
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.tool.StandardTool
import pl.karol202.paintplus.tool.ToolCoordinateSpace
import pl.karol202.paintplus.util.cache
import pl.karol202.paintplus.util.computeBounds
import pl.karol202.paintplus.util.cropped
import pl.karol202.paintplus.util.duplicated

abstract class AbstractToolDrawing(private val imageService: ImageService,
                                   viewService: ViewService,
                                   helpersService: HelpersService,
                                   effectsService: EffectsService,
                                   private val historyService: HistoryService) :
		StandardTool(imageService, viewService, helpersService, effectsService)
{
	private sealed class Result
	{
		data class Path(val path: android.graphics.Path) : Result()

		data class Point(val oval: RectF): Result()
	}

	override val inputCoordinateSpace get() = ToolCoordinateSpace.LAYER_SPACE
	override val isUsingSnapping get() = true

	var size = 25f
	var opacity = 1f
	var smoothEdge = true

	protected abstract val actionPreset: Action.NamePreset
	protected abstract val pathPaint: Paint
	protected abstract val ovalPaint: Paint

	protected var path: Path? = null
	private var lastPoint: PointF? = null

	override fun onTouchStart(point: PointF, layer: Layer): Boolean
	{
		path = Path().apply {
			fillType = Path.FillType.EVEN_ODD
			moveTo(point.x, point.y)
		}
		lastPoint = null
		return true
	}

	override fun onTouchMove(point: PointF, layer: Layer): Boolean
	{
		val path = path ?: return false
		lastPoint?.let { lastPoint ->
			path.quadTo(lastPoint.x, lastPoint.y, point.x, point.y)
		}
		lastPoint = if(lastPoint != null) null else point
		return true
	}

	override fun onTouchStop(point: PointF, layer: Layer): Boolean
	{
		val path = path ?: return false
		lastPoint?.let { lastPoint ->
			path.quadTo(lastPoint.x, lastPoint.y, point.x, point.y)
		} ?: path.lineTo(point.x, point.y)

		this.path = null
		lastPoint = null

		val result =
				if(!path.isEmpty) Result.Path(path)
				else Result.Point(RectF(point.x - size / 2,
				                        point.y - size / 2,
				                        point.x + size / 2,
				                        point.y + size / 2))
		historyService.commitAction { commit(layer, result) }
		return true
	}

	private fun commit(layer: Layer, result: Result): Action.ToRevert
	{
		val dirtyRect = getDirtyRect(result)
		val oldBitmap = layer.bitmap.duplicated()
		val oldDirtyBitmap = layer.bitmap.cropped(dirtyRect)
		layer.editCanvas.withClip(imageService.selection.bounds - layer.position) {
			when(result)
			{
				is Result.Path -> drawPath(result.path, pathPaint)
				is Result.Point -> drawOval(result.oval, ovalPaint)
			}
		}
		return actionPreset.toRevert(oldBitmap) { revert(layer, result, dirtyRect, oldDirtyBitmap) }
	}

	private fun revert(layer: Layer, result: Result, dirtyRect: Rect, oldDirtyBitmap: Bitmap): Action.ToCommit
	{
		val newBitmap = layer.bitmap.duplicated()
		layer.editCanvas.drawBitmap(oldDirtyBitmap, dirtyRect.left.toFloat(), dirtyRect.top.toFloat(), null)
		return actionPreset.toCommit(newBitmap) { commit(layer, result) }
	}

	private fun getDirtyRect(result: Result) = when(result)
	{
		is Result.Path -> result.path.computeBounds()
		is Result.Point -> result.oval
	}.toRect()
}
