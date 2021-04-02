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
package pl.karol202.paintplus.tool.pickcolor

import android.graphics.*
import androidx.core.graphics.contains
import androidx.core.graphics.plus
import androidx.core.graphics.toPoint
import pl.karol202.paintplus.R
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.image.ColorsService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.tool.StandardTool
import pl.karol202.paintplus.tool.ToolCoordinateSpace
import pl.karol202.paintplus.util.plus
import pl.karol202.paintplus.util.toRect
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class ToolColorPick(private val imageService: ImageService,
                    viewService: ViewService,
                    helpersService: HelpersService,
                    private val colorsService: ColorsService) : StandardTool(imageService, viewService, helpersService)
{
	override val name get() = R.string.tool_color_pick
	override val icon get() = R.drawable.ic_tool_color_pick_black_24dp
	override val propertiesFragmentClass get() = ColorPickProperties::class.java
	override val inputCoordinateSpace get() = ToolCoordinateSpace.LAYER_SPACE
	override val isUsingSnapping get() = false

	var size = 1
	val isAverage get() = size > 1

	override fun onTouchStart(point: PointF, layer: Layer) = true

	override fun onTouchMove(point: PointF, layer: Layer) = true

	override fun onTouchStop(point: PointF, layer: Layer) = false.also { pickColor(point.toPoint(), layer) }

	private fun pickColor(point: Point, layer: Layer) = when
	{
		point !in layer.size.toRect() -> null
		size == 1 -> pickPixelColor(point, layer)
		size > 1 -> pickAverageColor(point, layer)
		else -> null
	}.let { if(it != null) colorsService.setCurrentColor(it) }

	private fun pickPixelColor(point: Point, layer: Layer) = when
	{
		!checkSelection(point, layer) -> null
		else -> layer.bitmap.getPixel(point.x, point.y)
	}

	private fun pickAverageColor(center: Point, layer: Layer): Int?
	{
		var pixels = 0
		var redSum: Long = 0
		var greenSum: Long = 0
		var blueSum: Long = 0
		val regionStartX = center.x - floor((size - 1) / 2f).toInt()
		val regionStartY = center.y - floor((size - 1) / 2f).toInt()
		val regionEndX = center.x + floor(size / 2f).toInt()
		val regionEndY = center.y + floor(size / 2f).toInt()
		for(x in regionStartX..regionEndX)
		{
			for(y in regionStartY..regionEndY)
			{
				if(!checkSelection(Point(x, y), layer)) continue
				val color: Int = layer.bitmap.getPixel(x, y)
				pixels++
				redSum += Color.red(color).toFloat().pow(2f).toLong()
				greenSum += Color.green(color).toFloat().pow(2f).toLong()
				blueSum += Color.blue(color).toFloat().pow(2f).toLong()
			}
		}
		val red = sqrt(redSum / pixels.toDouble()).roundToInt()
		val green = sqrt(greenSum / pixels.toDouble()).roundToInt()
		val blue = sqrt(blueSum / pixels.toDouble()).roundToInt()
		return if(pixels > 0) Color.rgb(red, green, blue) else null
	}

	private fun checkSelection(point: Point, layer: Layer): Boolean
	{
		val selection = imageService.selection
		return selection.isEmpty || (point + layer.position) in selection
	}
}
