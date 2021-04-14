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
package pl.karol202.paintplus.tool.fill

import android.graphics.*
import androidx.core.graphics.*
import pl.karol202.paintplus.R
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.*
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.tool.StandardTool
import pl.karol202.paintplus.tool.ToolCoordinateSpace
import pl.karol202.paintplus.util.cache
import pl.karol202.paintplus.util.duplicated
import pl.karol202.paintplus.viewmodel.PaintViewModel
import java.util.*
import kotlin.math.roundToInt

class ToolFill(private val imageService: ImageService,
               viewService: ViewService,
               helpersService: HelpersService,
               private val effectsService: EffectsService,
               private val historyService: HistoryService,
               private val colorsService: ColorsService) :
		StandardTool(imageService, viewService, helpersService, effectsService)
{
	override val name get() = R.string.tool_fill
	override val icon get() = R.drawable.ic_tool_fill_black_24dp
	override val propertiesFragmentClass get() = FillProperties::class.java
	override val inputCoordinateSpace get() = ToolCoordinateSpace.LAYER_SPACE
	override val isUsingSnapping get() = false

	var threshold = 0f
	var opacity = 1f

	private val actionPreset = Action.namePreset(R.string.tool_fill)

	private val paint by cache({opacity}) { opacity ->
		Paint().apply {
			alpha = (opacity * 255).roundToInt()
		}
	}

	override fun onTouchStart(point: PointF, layer: Layer) = true

	override fun onTouchMove(point: PointF, layer: Layer) = true

	override fun onTouchStop(point: PointF, layer: Layer): Boolean
	{
		effectsService.postLongTask {
			val bitmap = fill(layer, point.toPoint(), imageService.selection, colorsService.currentColor) ?: return@postLongTask
			historyService.commitAction { commit(layer, bitmap) }
			effectsService.notifyViewUpdate()
		}
		return false
	}

	private fun fill(layer: Layer, startPoint: Point, selection: Selection, fillColor: Int): Bitmap?
	{
		if(startPoint !in (layer.bounds - layer.position) ||
				(!selection.isEmpty && !selection.contains(startPoint))) return null

		val touchedColor = layer.bitmap.getPixel(startPoint.x, startPoint.y)
		val touchedR = touchedColor.red
		val touchedG = touchedColor.green
		val touchedB = touchedColor.blue

		val maxDistance = (threshold * 3 * 255 * 255).roundToInt()

		val width = layer.width
		val height = layer.height
		val pixels = IntArray(width * height)
		layer.bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

		val pointsToCheckX = Stack<Int>()
		val pointsToCheckY = Stack<Int>()
		pointsToCheckX.push(startPoint.x)
		pointsToCheckY.push(startPoint.y)

		while(!pointsToCheckX.isEmpty())
		{
			val x = pointsToCheckX.pop()
			val y = pointsToCheckY.pop()
			if(!selection.isEmpty && !selection.contains(x + layer.x, y + layer.y)) continue
			val pos = y * width + x
			val oldColor = pixels[pos]
			if(!checkColor(oldColor, touchedColor, touchedR, touchedG, touchedB, maxDistance)) continue
			pixels[pos] = fillColor
			if(x > 0)
			{
				pointsToCheckX.add(x - 1)
				pointsToCheckY.add(y)
			}
			if(y > 0)
			{
				pointsToCheckX.add(x)
				pointsToCheckY.add(y - 1)
			}
			if(x < width - 1)
			{
				pointsToCheckX.add(x + 1)
				pointsToCheckY.add(y)
			}
			if(y < height - 1)
			{
				pointsToCheckX.add(x)
				pointsToCheckY.add(y + 1)
			}
		}
		return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
	}

	private fun checkColor(current: Int, touched: Int, touchedR: Int, touchedG: Int, touchedB: Int, maxDistance: Int) = when
	{
		current == touched -> true
		maxDistance == 0 -> false
		else ->
		{
			val distanceR = touchedR - Color.red(current)
			val distanceG = touchedG - Color.green(current)
			val distanceB = touchedB - Color.blue(current)
			val distance = (distanceR * distanceR) + (distanceG * distanceG) + (distanceB * distanceB)
			distance <= maxDistance
		}
	}

	private fun commit(layer: Layer, fillBitmap: Bitmap): Action.ToRevert
	{
		val oldBitmap = layer.bitmap.duplicated()
		layer.editCanvas.drawBitmap(fillBitmap, 0f, 0f, paint)
		return actionPreset.toRevert(oldBitmap) { revert(layer, fillBitmap, oldBitmap) }
	}

	private fun revert(layer: Layer, fillBitmap: Bitmap, oldBitmap: Bitmap): Action.ToCommit
	{
		val newBitmap = layer.bitmap.duplicated()
		layer.editCanvas.drawBitmap(oldBitmap, 0f, 0f, null)
		return actionPreset.toCommit(newBitmap) { commit(layer, fillBitmap) }
	}
}
