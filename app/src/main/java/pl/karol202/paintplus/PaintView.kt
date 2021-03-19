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
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.core.graphics.withClip
import androidx.core.graphics.withMatrix
import androidx.core.graphics.withTranslation
import pl.karol202.paintplus.helpers.HelpersManager
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.image.layer.mode.LayerMode
import pl.karol202.paintplus.settings.Settings
import pl.karol202.paintplus.tool.Tool
import pl.karol202.paintplus.tool.ToolCoordinateSpace
import pl.karol202.paintplus.tool.selection.Selection
import pl.karol202.paintplus.util.*
import kotlin.properties.Delegates.notNull

private val PAINT_DASH = floatArrayOf(5f, 5f)
private const val CHECKERBOARD_OFFSET = 8f

class PaintView(context: Context,
                attrs: AttributeSet?) : SurfaceView(context, attrs)
{
	private var image by notNull<Image>()
	private var selection by notNull<Selection>()
	private var helpersManager by notNull<HelpersManager>()

	private var currentTool by notNull<Tool>()

	private var filtering: Boolean = false

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

	private val checkerboardMatrix by cache({image.viewX}, {image.viewY}, {image.zoom}) { viewX, viewY, zoom ->
		Matrix().apply {
			preTranslate(-viewX * zoom + CHECKERBOARD_OFFSET, -viewY * zoom + CHECKERBOARD_OFFSET)
		}
	}

	private val layerBoundsPath by cache({image.selectedLayer?.bounds}, {screenRect}) { bounds, screenRect ->
		Path().apply {
			if(bounds == null) return@apply
			addRect(bounds intersectionWith screenRect, Path.Direction.CW)
			close()
			transform(image.imageMatrix)
		}
	}

	private val selectionPath by cache({selection.region}, {screenRect}, {image.imageMatrix}) {
		selectionRegion, screenRect, imageMatrix ->
		(selectionRegion intersectionWith screenRect.rounded()).boundaryPath.transformedBy(imageMatrix)
	}

	private val screenRect by cache({image.viewX}, {image.viewY}, {image.zoom}, {width}, {height}) {
		viewX, viewY, zoom, width, height ->
		RectF(viewX - 2, viewY - 2, viewX + width / zoom + 2, viewY + height / zoom + 2)
	}

	override fun draw(canvas: Canvas)
	{
		super.draw(canvas)
		if(isInEditMode) return

		canvas.withClip(image.imageRect) {
			drawCheckerboard(canvas)
		}
		drawImage(canvas)
		drawLayerBounds(canvas)
		drawSelection(canvas)
		helpersManager.onScreenDraw(canvas)
	}

	private fun drawCheckerboard(canvas: Canvas)
	{
		checkerboardShader.setLocalMatrix(checkerboardMatrix)
		canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), checkerboardPaint)
	}

	private fun drawImage(canvas: Canvas)
	{
		var screenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
		var screenCanvas = Canvas(screenBitmap)

		for(layer in image.layers.asReversed())
		{
			val drawLayer = layer.isVisible && !layer.isTemporaryHidden
			val drawToolOnLayer = image.isLayerSelected(layer) && currentTool.doesOnLayerDraw(layer.isVisible)

			val layerMode: LayerMode = null
			val (resultBitmap, resultCanvas) = layerMode.apply(screenBitmap, screenCanvas) {
				if(drawLayer)
					withClip(image.imageRect) {
						val layerMatrix = image.imageMatrix.preTranslated(layer.x.toFloat(), layer.y.toFloat())
						drawBitmap(layer.bitmap, layerMatrix, layerPaint)
					}
				if(drawToolOnLayer)
					withToolSpace(currentTool.onLayerDrawingCoordinateSpace) {
						currentTool.onLayerDraw(this)
					}
			}
			screenBitmap = resultBitmap
			screenCanvas = resultCanvas
		}

		if(currentTool.doesOnTopDraw())
			screenCanvas.withToolSpace(currentTool.onTopDrawingCoordinateSpace) {
				currentTool.onTopDraw(this)
			}

		canvas.drawBitmap(screenBitmap, 0f, 0f, null)
	}

	private fun Canvas.withToolSpace(space: ToolCoordinateSpace, block: Canvas.() -> Unit) =
			if(space == ToolCoordinateSpace.SCREEN_SPACE) block()
			else withMatrix(image.imageMatrix) {
				if(space == ToolCoordinateSpace.IMAGE_SPACE) block()
				else withTranslation(image.selectedLayerX.toFloat(), image.selectedLayerY.toFloat(), block)
			}

	private fun drawLayerBounds(canvas: Canvas) = canvas.drawPath(layerBoundsPath, layerBoundsPaint)

	private fun drawSelection(canvas: Canvas) = canvas.drawPath(selectionPath, selectionPaint)

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent): Boolean
	{
		if(image.selectedLayer == null)
		{
			if(event.action != MotionEvent.ACTION_DOWN) currentTool.onTouch(event, context)
			return false
		}
		return currentTool.onTouch(event, context).also {
			if(it) invalidate()
		}
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
	{
		image.viewportWidth = width
		image.viewportHeight = height
		image.centerView()
	}

	fun setImageTemp(image: Image)
	{
		this.image = image
		selection = image.selection
		helpersManager = image.helpersManager

		invalidate()
	}

	fun setCurrentToolTemp(tool: Tool)
	{
		currentTool = tool
		invalidate()
	}

	fun setSettings(settings: Settings)
	{
		filtering = settings.smoothView
	}
}
