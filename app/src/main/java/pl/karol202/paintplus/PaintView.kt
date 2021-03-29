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
package pl.karol202.paintplus

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Size
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.core.graphics.*
import pl.karol202.paintplus.helpers.Helper
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.image.Selection
import pl.karol202.paintplus.image.ViewPosition
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.tool.Tool
import pl.karol202.paintplus.tool.ToolCoordinateSpace
import pl.karol202.paintplus.util.*

private val PAINT_DASH = floatArrayOf(5f, 5f)
private const val CHECKERBOARD_OFFSET = 8f

// TODO Center view on start
class PaintView(context: Context,
                attrs: AttributeSet?) : SurfaceView(context, attrs)
{
	var image by invalidating(null as Image?).require()
	var viewPosition by invalidating(ViewPosition())
	var currentTool by invalidating(null as Tool?).require()
	var selection by invalidating(Selection.empty)
	var helpers by invalidating(emptyList<Helper>())
	var filtering by invalidating(false)
	var onViewportSizeChangeListener: ((Size) -> Unit)? = null

	private val checkerboardShader =
			BitmapShader(BitmapFactory.decodeResource(context.resources, R.drawable.checkerboard),
			             Shader.TileMode.REPEAT,
			             Shader.TileMode.REPEAT)

	private val selectionPaint = Paint().apply {
		style = Paint.Style.STROKE
		strokeWidth = 2f
		pathEffect = DashPathEffect(PAINT_DASH, 0f)
	}

	private val layerBoundsPaint = Paint().apply {
		style = Paint.Style.STROKE
		color = Color.GRAY
		strokeWidth = 2f
		pathEffect = DashPathEffect(PAINT_DASH, 0f)
	}

	private val checkerboardPaint = Paint().apply {
		shader = checkerboardShader
		isFilterBitmap = false
	}

	private val layerPaint by cache({filtering}) { filtering ->
		Paint().apply {
			isFilterBitmap = filtering
		}
	}

	private val checkerboardMatrix by cache({viewPosition}) { viewPosition ->
		Matrix().apply {
			preTranslate(-viewPosition.x * viewPosition.zoom + CHECKERBOARD_OFFSET,
			             -viewPosition.y * viewPosition.zoom + CHECKERBOARD_OFFSET)
		}
	}

	private val layerBoundsPath by cache({image.selectedLayer?.bounds}, {viewPosition.imageMatrix}) { bounds, imageMatrix ->
		bounds?.toRectF()?.toPath()?.transformedBy(imageMatrix) ?: Path()
	}

	private val selectionPath by cache({selection.region}, {viewPosition.imageMatrix}) { selectionRegion, imageMatrix ->
		selectionRegion.boundaryPath.transformedBy(imageMatrix)
	}

	override fun draw(canvas: Canvas)
	{
		super.draw(canvas)
		if(isInEditMode) return

		canvas.withClip(viewPosition.getImageRect(image)) {
			drawCheckerboard(canvas)
		}
		drawImage(canvas)
		drawLayerBounds(canvas)
		drawSelection(canvas)
		helpers.forEach { it.onScreenDraw(canvas) }
	}

	private fun drawCheckerboard(canvas: Canvas)
	{
		checkerboardShader.setLocalMatrix(checkerboardMatrix)
		canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), checkerboardPaint)
	}

	private fun drawImage(canvas: Canvas)
	{
		val initialBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
		val resultBitmap = image.layers.fold(BitmapWithCanvas.create(initialBitmap)) { (bitmap, canvas), layer ->
			drawLayer(bitmap, canvas, layer)
		}.bitmap

		canvas.drawBitmap(resultBitmap, 0f, 0f, null)
		currentTool.drawOnTop(canvas)
	}

	private fun drawLayer(bitmap: Bitmap, canvas: Canvas, layer: Layer) =
			layer.mode.apply(bitmap, canvas) {
				if(layer.visible)
					withClip(viewPosition.getImageRect(image)) {
						drawBitmap(layer.bitmap, viewPosition.imageMatrix * layer.matrix, layerPaint)
					}
				if(image.isLayerSelected(layer))
					currentTool.drawOnLayer(this, layer.visible)
			}

	private fun drawLayerBounds(canvas: Canvas) = canvas.drawPath(layerBoundsPath, layerBoundsPaint)

	private fun drawSelection(canvas: Canvas) = canvas.drawPath(selectionPath, selectionPaint)

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent) = currentTool.onTouch(event).also {
		if(it) invalidate()
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
	{
		onViewportSizeChangeListener?.invoke(Size(w, h))
	}
}
