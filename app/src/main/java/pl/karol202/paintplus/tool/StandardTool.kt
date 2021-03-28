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
import androidx.core.graphics.withTranslation
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.util.div
import pl.karol202.paintplus.util.minus

abstract class StandardTool(private val imageService: ImageService,
                            private val viewService: ViewService,
                            private val helpersService: HelpersService) : Tool
{
	abstract val inputCoordinateSpace: ToolCoordinateSpace
	abstract val isUsingSnapping: Boolean

	abstract fun onTouchStart(x: Float, y: Float): Boolean

	abstract fun onTouchMove(x: Float, y: Float): Boolean

	abstract fun onTouchStop(x: Float, y: Float): Boolean

	override fun onTouch(event: MotionEvent): Boolean
	{
		val point = createTouchPoint(event.x, event.y)
		return when(event.action)
		{
			MotionEvent.ACTION_DOWN -> onTouchStart(point.x, point.y)
			MotionEvent.ACTION_MOVE ->
				(0 until event.historySize)
						.map { createTouchPoint(event.getHistoricalX(it), event.getHistoricalY(it)) }
						.plus(point)
						.fold(true) { result, p -> result && onTouchMove(p.x, p.y) }
			MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> onTouchStop(point.x, point.y)
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

	override fun getOnLayerDrawCoordinateSpace(layerVisible: Boolean): ToolCoordinateSpace? = null

	override fun getOnTopDrawCoordinateSpace(): ToolCoordinateSpace? = null

	override fun drawOnLayer(canvas: Canvas) { }

	override fun drawOnTop(canvas: Canvas) { }

	protected fun Canvas.withImageSpace(block: Canvas.() -> Unit) = withMatrix(viewService.viewPosition.imageMatrix, block)

	protected fun Canvas.withLayerSpace(block: Canvas.() -> Unit) = imageService.image.selectedLayer?.let {
		withMatrix(viewService.viewPosition.imageMatrix * it.matrix, block)
	} ?: withImageSpace(block)
}
