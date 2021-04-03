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
package pl.karol202.paintplus.tool.marker

import android.graphics.*
import androidx.core.graphics.toRect
import androidx.core.graphics.toRegion
import pl.karol202.paintplus.R
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.ColorsService
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

class ToolMarker(imageService: ImageService,
                 viewService: ViewService,
                 helpersService: HelpersService,
                 private val historyService: HistoryService,
                 private val colorsService: ColorsService) : StandardTool(imageService, viewService, helpersService)
{
	private sealed class MarkerResult
	{
		data class Path(val path: android.graphics.Path) : MarkerResult()

		data class Point(val oval: RectF): MarkerResult()
	}

	override val name get() = R.string.tool_marker
	override val icon get() = R.drawable.ic_tool_marker_black_24dp
	override val propertiesFragmentClass get() = MarkerProperties::class.java
	override val inputCoordinateSpace get() = ToolCoordinateSpace.LAYER_SPACE
	override val isUsingSnapping get() = true

	var size = 25f
	var opacity = 1f
	var smoothEdge = true

	private val actionPreset = Action.namePreset(R.string.tool_marker)

	private val pathPaint by cache({colorsService.currentColor}, {size}, {opacity}, {smoothEdge}) {
		currentColor, size, opacity, smooth ->
		Paint().apply {
			style = Paint.Style.STROKE
			strokeCap = Paint.Cap.ROUND
			strokeJoin = Paint.Join.ROUND
			color = currentColor
			alpha = (opacity * 255).toInt()
			strokeWidth = size
			isAntiAlias = smooth
		}
	}
	private val ovalPaint by cache({colorsService.currentColor}, {opacity}, {smoothEdge}) { currentColor, opacity, smooth ->
		Paint().apply {
			color = currentColor
			alpha = (opacity * 255).toInt()
			isAntiAlias = smooth
		}
	}

	private var path: Path? = null
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
				if(!path.isEmpty) MarkerResult.Path(path)
				else MarkerResult.Point(RectF(point.x - size / 2,
				                              point.y - size / 2,
				                              point.x + size / 2,
				                              point.y + size / 2))
		historyService.commitAction { commit(layer, result) }
		return true
	}

	private fun commit(layer: Layer, markerResult: MarkerResult): Action.ToRevert
	{
		val dirtyRect = getDirtyRect(markerResult)
		val oldBitmap = layer.bitmap.duplicated()
		val oldDirtyBitmap = layer.bitmap.cropped(dirtyRect)
		when(markerResult)
		{
			is MarkerResult.Path -> layer.editCanvas.drawPath(markerResult.path, pathPaint)
			is MarkerResult.Point -> layer.editCanvas.drawOval(markerResult.oval, ovalPaint)
		}
		return actionPreset.toRevert(oldBitmap) { revert(layer, markerResult, dirtyRect, oldDirtyBitmap) }
	}

	private fun revert(layer: Layer, markerResult: MarkerResult, dirtyRect: Rect, oldDirtyBitmap: Bitmap): Action.ToCommit
	{
		val newBitmap = layer.bitmap.duplicated()
		layer.editCanvas.drawBitmap(oldDirtyBitmap, dirtyRect.left.toFloat(), dirtyRect.top.toFloat(), null)
		return actionPreset.toCommit(newBitmap) { commit(layer, markerResult) }
	}

	private fun getDirtyRect(result: MarkerResult) = when(result)
	{
		is MarkerResult.Path -> result.path.computeBounds()
		is MarkerResult.Point -> result.oval
	}.toRect()

	override fun drawOnLayer(canvas: Canvas, layer: Layer)
	{
		if(layer != currentLayer || !layer.visible) return
		val path = path ?: return
		canvas.withImageSpace {
			withImageClip {
				withLayerClip(layer) {
					withSelectionClip {
						withLayerSpace(layer) {
							drawPath(path, pathPaint)
						}
					}
				}
			}
		}
	}
}
