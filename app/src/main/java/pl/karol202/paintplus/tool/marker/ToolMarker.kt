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
import pl.karol202.paintplus.R
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.*
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.tool.drawing.AbstractToolDrawing
import pl.karol202.paintplus.util.cache

class ToolMarker(imageService: ImageService,
                 viewService: ViewService,
                 helpersService: HelpersService,
                 effectsService: EffectsService,
                 historyService: HistoryService,
                 private val colorsService: ColorsService) :
		AbstractToolDrawing(imageService, viewService, helpersService, effectsService, historyService)
{
	override val name get() = R.string.tool_marker
	override val icon get() = R.drawable.ic_tool_marker_black_24dp
	override val propertiesFragmentClass get() = MarkerProperties::class.java

	override val actionPreset = Action.namePreset(R.string.tool_marker)

	override val pathPaint by cache({colorsService.currentColor}, {size}, {opacity}, {smoothEdge}) {
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
	override val ovalPaint by cache({colorsService.currentColor}, {opacity}, {smoothEdge}) { currentColor, opacity, smooth ->
		Paint().apply {
			color = currentColor
			alpha = (opacity * 255).toInt()
			isAntiAlias = smooth
		}
	}

	override fun drawOnLayer(canvas: Canvas, layer: Layer)
	{
		if(layer != currentLayer || !layer.visible) return
		val path = path ?: return
		canvas.withImageSpace {
			withImageClip {
				withLayerClip(layer) {
					withSelectionClip {
						withLayerSpace(layer) {
							canvas.drawPath(path, pathPaint)
						}
					}
				}
			}
		}
	}
}
