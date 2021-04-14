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
package pl.karol202.paintplus.tool.brush

import android.graphics.*
import androidx.core.graphics.plus
import androidx.core.graphics.toRect
import pl.karol202.paintplus.R
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.*
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.tool.StandardTool
import pl.karol202.paintplus.tool.ToolCoordinateSpace
import pl.karol202.paintplus.util.cropped
import pl.karol202.paintplus.util.duplicated
import pl.karol202.paintplus.util.preTranslated
import kotlin.math.roundToInt

class ToolBrush(private val imageService: ImageService,
                viewService: ViewService,
                helpersService: HelpersService,
                effectsService: EffectsService,
                private val historyService: HistoryService,
                private val colorsService: ColorsService) :
		StandardTool(imageService, viewService, helpersService, effectsService)
{
	private class DrawingState(val shader: Shader,
	                           val paint: Paint,
	                           val bitmap: Bitmap,
	                           val canvas: Canvas,
	                           val path: Path = Path(),
	                           var pathDistance: Float = 0f,
	                           var dirtyRect: Rect = Rect(),
	                           var lastPoint: PointF? = null)

	override val name get() = R.string.tool_brush
	override val icon get() = R.drawable.ic_tool_brush_black_24dp
	override val propertiesFragmentClass get() = BrushProperties::class.java
	override val inputCoordinateSpace get() = ToolCoordinateSpace.LAYER_SPACE
	override val isUsingSnapping get() = true

	var size = 25f
	var shapeOffset = 7f
	var opacity = 1f

	private val actionPreset = Action.namePreset(R.string.tool_brush)

	private var state: DrawingState? = null

	override fun onTouchStart(point: PointF, layer: Layer): Boolean
	{
		state = createInitialState(layer).apply {
			path.moveTo(point.x, point.y)
		}
		return true
	}

	private fun createInitialState(layer: Layer): DrawingState
	{
		val shader = createShader()
		val bitmap = Bitmap.createBitmap(layer.width, layer.height, Bitmap.Config.ARGB_8888)
		val canvas = Canvas(bitmap).apply {
			clipPath(imageService.selection.path)
		}
		return DrawingState(shader = shader,
		                    paint = createPaint(shader),
		                    bitmap = bitmap,
		                    canvas = canvas)
	}

	private fun createShader(): Shader
	{
		val color = colorsService.currentColor
		val center = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color))
		val edge = Color.argb(0, Color.red(color), Color.green(color), Color.blue(color))
		return RadialGradient(0f, 0f, size / 2, center, edge, Shader.TileMode.CLAMP)
	}

	private fun createPaint(gradientShader: Shader) = Paint().apply {
		shader = gradientShader
		strokeWidth = size
		alpha = (opacity * 255).roundToInt()
	}

	override fun onTouchMove(point: PointF, layer: Layer): Boolean
	{
		val state = state ?: return false
		val lastPoint = state.lastPoint

		if(lastPoint != null)
		{
			state.path.quadTo(lastPoint.x, lastPoint.y, point.x, point.y)
			state.lastPoint = null
			drawPointsOnPath(state)
		}
		else state.lastPoint = point

		return true
	}

	override fun onTouchStop(point: PointF, layer: Layer): Boolean
	{
		val state = state ?: return false
		val lastPoint = state.lastPoint

		if(lastPoint != null) state.path.quadTo(lastPoint.x, lastPoint.y, point.x, point.y)
		else state.path.lineTo(point.x, point.y)

		drawPointsOnPath(state)
		drawPoint(state, point.x, point.y)

		historyService.commitAction { commit(layer, state.bitmap.cropped(state.dirtyRect), state.dirtyRect) }

		this.state = null
		return true
	}

	private fun drawPointsOnPath(state: DrawingState)
	{
		val pathMeasure = PathMeasure(state.path, false)
		val point = FloatArray(2)
		while(state.pathDistance <= pathMeasure.length)
		{
			if(!pathMeasure.getPosTan(state.pathDistance, point, null)) break
			drawPoint(state, point[0], point[1])
			state.pathDistance += shapeOffset
		}
	}

	private fun drawPoint(state: DrawingState, x: Float, y: Float)
	{
		state.shader.setLocalMatrix(Matrix().preTranslated(x, y))
		val oval = RectF(x - size / 2,
		                 y - size / 2,
		                 x + size / 2,
		                 y + size / 2)
		state.canvas.drawOval(oval, state.paint)
		state.dirtyRect = state.dirtyRect + oval.toRect()
	}

	private fun commit(layer: Layer, brushBitmap: Bitmap, dirtyRect: Rect): Action.ToRevert
	{
		val oldBitmap = layer.bitmap.duplicated()
		val oldDirtyBitmap = layer.bitmap.cropped(dirtyRect)
		layer.editCanvas.drawBitmap(brushBitmap, dirtyRect.left.toFloat(), dirtyRect.top.toFloat(), null)
		return actionPreset.toRevert(oldBitmap) { revert(layer, brushBitmap, dirtyRect, oldDirtyBitmap) }
	}

	private fun revert(layer: Layer, brushBitmap: Bitmap, dirtyRect: Rect, oldDirtyBitmap: Bitmap): Action.ToCommit
	{
		val newBitmap = layer.bitmap.duplicated()
		layer.editCanvas.drawBitmap(oldDirtyBitmap, dirtyRect.left.toFloat(), dirtyRect.top.toFloat(), null)
		return actionPreset.toCommit(newBitmap) { commit(layer, brushBitmap, dirtyRect) }
	}

	override fun drawOnLayer(canvas: Canvas, layer: Layer)
	{
		if(layer != currentLayer || !layer.visible) return
		canvas.withImageSpace {
			withImageClip {
				state?.bitmap?.let { drawBitmap(it, layer.matrix, null) }
			}
		}
	}
}
