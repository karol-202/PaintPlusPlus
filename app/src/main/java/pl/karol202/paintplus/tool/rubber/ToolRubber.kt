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
package pl.karol202.paintplus.tool.rubber

import android.graphics.*
import pl.karol202.paintplus.R
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.EffectsService
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.tool.drawing.AbstractToolDrawing
import pl.karol202.paintplus.util.cache

class ToolRubber(private val imageService: ImageService,
                 viewService: ViewService,
                 helpersService: HelpersService,
                 effectsService: EffectsService,
                 historyService: HistoryService) :
		AbstractToolDrawing(imageService, viewService, helpersService, effectsService, historyService)
{
	override val name get() = R.string.tool_rubber
	override val icon get() = R.drawable.ic_tool_rubber_black_24dp
	override val propertiesFragmentClass get() = RubberProperties::class.java

	override val actionPreset = Action.namePreset(R.string.tool_rubber)

	override val pathPaint by cache({size}, {opacity}, {smoothEdge}) { size, opacity, smooth ->
		Paint().apply {
			style = Paint.Style.STROKE
			strokeCap = Paint.Cap.ROUND
			strokeJoin = Paint.Join.ROUND
			xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
			color = Color.TRANSPARENT
			alpha = (opacity * 255).toInt()
			strokeWidth = size
			isAntiAlias = smooth
		}
	}
	override val ovalPaint by cache({opacity}, {smoothEdge}) { opacity, smooth ->
		Paint().apply {
			xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
			color = Color.TRANSPARENT
			alpha = (opacity * 255).toInt()
			isAntiAlias = smooth
		}
	}

	override fun onTouchStart(point: PointF, layer: Layer) = super.onTouchStart(point, layer).also {
		imageService.editImage { withLayerUpdated(layer.withVisibility(false)) }
	}

	override fun onTouchStop(point: PointF, layer: Layer) = super.onTouchStop(point, layer).also {
		imageService.editImage { withLayerUpdated(layer.withVisibility(true)) }
	}

	override fun drawOnLayer(canvas: Canvas, layer: Layer)
	{
		if(layer != currentLayer || !layer.visible) return
		val path = path ?: return
		canvas.withImageSpace {
			withImageClip {
				drawBitmap(layer.bitmap, layer.matrix, null)
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
