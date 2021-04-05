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
package pl.karol202.paintplus.tool.shape

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.minus
import androidx.core.graphics.toPoint
import androidx.core.graphics.withTranslation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import pl.karol202.paintplus.R
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.tool.OnToolChangeListener
import pl.karol202.paintplus.tool.StandardTool
import pl.karol202.paintplus.tool.ToolCoordinateSpace
import pl.karol202.paintplus.util.cropped
import pl.karol202.paintplus.util.duplicated
import pl.karol202.paintplus.util.onChange

class ToolShape(private val imageService: ImageService,
                viewService: ViewService,
                helpersService: HelpersService,
                private val historyService: HistoryService,
                val shapes: List<Shape>) : StandardTool(imageService, viewService, helpersService),
                                           OnToolChangeListener
{
	override val name get() = R.string.tool_shape
	override val icon get() = R.drawable.ic_tool_shape_black_24dp
	override val propertiesFragmentClass get() = ShapeToolProperties::class.java
	override val inputCoordinateSpace get() = ToolCoordinateSpace.IMAGE_SPACE
	override val isUsingSnapping get() = false

	override val updateEventFlow: Flow<Unit>
		get() = super.updateEventFlow.flatMapLatest { shape.updateEventFlow.onStart { emit(Unit) } }

	var opacity: Float
		get() = shape.opacity
		set(opacity) = shapes.forEach { it.opacity = opacity }
	var smooth: Boolean
		get() = shape.smooth
		set(smooth) = shapes.forEach { it.smooth = smooth }
	val isInEditMode get() = shape.isInEditMode
	var shape: Shape by notifying(shapes.first()).onChange { old, new -> if(new != old) old.cancel() }
	val shapeIndex get() = shapes.indexOf(shape)

	private val actionPreset = Action.namePreset(R.string.tool_shape)

	override fun onTouchStart(point: PointF, layer: Layer) = true.also { shape.onTouchStart(point.toPoint()) }

	override fun onTouchMove(point: PointF, layer: Layer) = true.also { shape.onTouchMove(point.toPoint()) }

	override fun onTouchStop(point: PointF, layer: Layer) = true.also { shape.onTouchStop(point.toPoint()) }

	override fun drawOnLayer(canvas: Canvas, layer: Layer)
	{
		if(layer != imageService.image.selectedLayer || !layer.visible) return
		canvas.withImageSpace {
			shape.drawOnLayer(canvas, true)
			withImageClip {
				withLayerClip(layer) {
					withSelectionClip {
						shape.drawOnLayer(canvas, false)
					}
				}
			}
		}
	}

	fun apply()
	{
		val layer = imageService.image.selectedLayer ?: return
		val dirtyRect = shape.bounds ?: return
		val dirtyBitmap = Bitmap.createBitmap(dirtyRect.width(), dirtyRect.height(), Bitmap.Config.ARGB_8888).applyCanvas {
			withTranslation(-dirtyRect.left.toFloat(), -dirtyRect.top.toFloat()) {
				withLayerClip(layer) {
					withSelectionClip {
						shape.apply(this)
					}
				}
			}
		}
		val dirtyRectInLayerSpace = dirtyRect - layer.position

		historyService.commitAction { commit(layer, dirtyRectInLayerSpace, dirtyBitmap) }
	}

	private fun commit(layer: Layer, dirtyRect: Rect, newDirtyBitmap: Bitmap): Action.ToRevert
	{
		val oldBitmap = layer.bitmap.duplicated()
		val oldDirtyBitmap = layer.bitmap.cropped(dirtyRect)
		layer.editCanvas.drawBitmap(newDirtyBitmap, dirtyRect.left.toFloat(), dirtyRect.top.toFloat(), null)
		return actionPreset.toRevert(oldBitmap) { revert(layer, dirtyRect, oldDirtyBitmap) }
	}

	private fun revert(layer: Layer, dirtyRect: Rect, oldDirtyBitmap: Bitmap): Action.ToCommit
	{
		val newBitmap = layer.bitmap.duplicated()
		val newDirtyBitmap = layer.bitmap.cropped(dirtyRect)
		layer.editCanvas.drawBitmap(oldDirtyBitmap, dirtyRect.left.toFloat(), dirtyRect.top.toFloat(), null)
		return actionPreset.toCommit(newBitmap) { commit(layer, dirtyRect, newDirtyBitmap) }
	}

	fun cancel() = shape.cancel()

	override fun onToolSelected() {}

	override fun onOtherToolSelected() = cancel()
}
