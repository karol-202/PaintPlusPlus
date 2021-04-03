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
package pl.karol202.paintplus.tool

import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PointF
import android.view.MotionEvent
import androidx.core.graphics.plus
import androidx.core.graphics.times
import androidx.core.graphics.withClip
import androidx.core.graphics.withMatrix
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.util.div
import pl.karol202.paintplus.util.minus
import pl.karol202.paintplus.util.toRect
import kotlin.properties.Delegates

abstract class StandardTool(private val imageService: ImageService,
                            private val viewService: ViewService,
                            private val helpersService: HelpersService) : Tool
{
	abstract val inputCoordinateSpace: ToolCoordinateSpace
	abstract val isUsingSnapping: Boolean

	private val _updateEventFlow = MutableSharedFlow<Unit>()
	override val updateEventFlow: Flow<Unit> = _updateEventFlow

	protected var currentLayer: Layer? = null
		private set

	abstract fun onTouchStart(point: PointF, layer: Layer): Boolean

	abstract fun onTouchMove(point: PointF, layer: Layer): Boolean

	abstract fun onTouchStop(point: PointF, layer: Layer): Boolean

	final override fun onTouch(event: MotionEvent) = when(event.action)
	{
		MotionEvent.ACTION_DOWN ->
			imageService.image.selectedLayer?.let { layer ->
				currentLayer = layer
				onTouchStart(createTouchPoint(event.x, event.y, layer), layer)
			}
		MotionEvent.ACTION_MOVE ->
			currentLayer?.let { layer ->
				(0 until event.historySize)
						.map { createTouchPoint(event.getHistoricalX(it), event.getHistoricalY(it), layer) }
						.plus(createTouchPoint(event.x, event.y, layer))
						.fold(true) { result, p -> result && onTouchMove(p, layer) }
			}
		MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
			currentLayer?.let { layer ->
				currentLayer = null
				onTouchStop(createTouchPoint(event.x, event.y, layer), layer)
			}
		else -> true
	} ?: false

	private fun createTouchPoint(x: Float, y: Float, layer: Layer) =
			PointF(x, y).transformTouchCoordinates(layer).snapTouchCoordinates()

	private fun PointF.transformTouchCoordinates(layer: Layer) = when(inputCoordinateSpace)
	{
		ToolCoordinateSpace.LAYER_SPACE -> (this / viewService.zoom) + viewService.viewPosition.offset - layer.position
		ToolCoordinateSpace.IMAGE_SPACE -> (this / viewService.zoom) + viewService.viewPosition.offset
		else -> this
	}

	private fun PointF.snapTouchCoordinates() =
			if(isUsingSnapping) helpersService.snapPoint(this)
			else this

	protected fun Canvas.withImageSpace(block: Canvas.() -> Unit) =
			withMatrix(viewService.viewPosition.imageMatrix, block)

	protected fun Canvas.withImageClip(block: Canvas.() -> Unit) =
			withClip(imageService.image.size.toRect(), block)

	protected fun Canvas.withLayerClip(layer: Layer, block: Canvas.() -> Unit) =
			withClip(layer.bounds, block)

	protected fun Canvas.withSelectionClip(block: Canvas.() -> Unit) =
			withClip(imageService.selection.bounds, block)

	protected fun Canvas.withLayerSpace(layer: Layer, block: Canvas.() -> Unit) =
			withMatrix(layer.matrix, block)

	protected fun <V> notifying(initial: V) = Delegates.observable(initial) { _, _, _ -> notifyUpdate() }

	private fun notifyUpdate()
	{
		_updateEventFlow.tryEmit(Unit)
	}
}
