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
import android.graphics.PointF
import android.view.MotionEvent
import androidx.core.graphics.plus
import androidx.core.graphics.times
import androidx.core.graphics.withMatrix
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.util.div
import pl.karol202.paintplus.util.minus
import kotlin.properties.Delegates

abstract class StandardTool(private val imageService: ImageService,
                            private val viewService: ViewService,
                            private val helpersService: HelpersService) : Tool
{
	abstract val inputCoordinateSpace: ToolCoordinateSpace
	abstract val isUsingSnapping: Boolean

	private val _updateEventFlow = MutableSharedFlow<Unit>()
	override val updateEventFlow: Flow<Unit> = _updateEventFlow

	private var layer: Layer? = null

	abstract fun onTouchStart(point: PointF, layer: Layer): Boolean

	abstract fun onTouchMove(point: PointF, layer: Layer): Boolean

	abstract fun onTouchStop(point: PointF, layer: Layer): Boolean

	final override fun onTouch(event: MotionEvent): Boolean
	{
		val point = createTouchPoint(event.x, event.y)
		return when(event.action)
		{
			MotionEvent.ACTION_DOWN ->
			{
				layer = imageService.image.selectedLayer
				onTouchStart(point, layer ?: return false)
			}
			MotionEvent.ACTION_MOVE ->
				(0 until event.historySize)
						.map { createTouchPoint(event.getHistoricalX(it), event.getHistoricalY(it)) }
						.plus(point)
						.fold(true) { result, p -> result && onTouchMove(p, layer ?: return false) }
			MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
				onTouchStop(point, layer ?: return false).also {
					layer = null
				}
			else -> true
		}
	}

	private fun createTouchPoint(x: Float, y: Float) = PointF(x, y).transformTouchCoordinates().snapTouchCoordinates()

	private fun PointF.transformTouchCoordinates(): PointF
	{
		val selectedLayer = imageService.image.selectedLayer
		return when
		{
			inputCoordinateSpace == ToolCoordinateSpace.LAYER_SPACE && selectedLayer != null ->
				this / viewService.zoom + viewService.viewPosition.offset - selectedLayer.position
			inputCoordinateSpace == ToolCoordinateSpace.IMAGE_SPACE ->
				this / viewService.zoom + viewService.viewPosition.offset
			else -> this
		}
	}

	private fun PointF.snapTouchCoordinates() = if(isUsingSnapping) helpersService.snapPoint(this) else this

	protected fun Canvas.withImageSpace(block: Canvas.() -> Unit) = withMatrix(viewService.viewPosition.imageMatrix, block)

	protected fun Canvas.withLayerSpace(block: Canvas.() -> Unit) = imageService.image.selectedLayer?.let {
		withMatrix(viewService.viewPosition.imageMatrix * it.matrix, block)
	} ?: withImageSpace(block)

	protected fun <V> notifying(initial: V) = Delegates.observable(initial) { _, _, _ -> notifyUpdate() }

	private fun notifyUpdate()
	{
		_updateEventFlow.tryEmit(Unit)
	}
}
