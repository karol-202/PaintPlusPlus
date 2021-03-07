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
import pl.karol202.paintplus.helpers.HelpersManager
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.image.Image.OnImageChangeListener
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.image.layer.mode.LayerModeType
import pl.karol202.paintplus.settings.Settings
import pl.karol202.paintplus.tool.Tool
import pl.karol202.paintplus.tool.ToolCoordinateSpace
import pl.karol202.paintplus.tool.selection.Selection
import pl.karol202.paintplus.tool.selection.Selection.OnSelectionChangeListener

private val PAINT_DASH = floatArrayOf(5f, 5f)
private const val CHECKERBOARD_OFFSET = 8f

class PaintView(context: Context,
                attrs: AttributeSet?) : SurfaceView(context, attrs)
{
	private var image: Image? = null
	private var selection: Selection? = null
	private var helpersManager: HelpersManager? = null

	private var currentTool: Tool? = null

	private val selectionPaint = createSelectionPaint()
	private val layerBoundsPaint = createLayerBoundsPaint()
	private val checkerboardShader = createCheckerboardShader()
	private val checkerboardPaint = createCheckerboardPaint()

	private val checkerboardMatrix = Matrix()
	private val layerMatrix = Matrix()
	private var boundsPath = Path()

	private var rawLimitedSelectionPath: Path? = null
	private var limitedSelectionPath: Path? = null
	private var rawSelectionPath: Path? = null
	private var selectionPath: Path? = null

	private val screenRect
		get() = RectF(image!!.viewX - 2, image!!.viewY - 2,
		              image!!.viewX + width / image!!.zoom + 2, image!!.viewY + height / image!!.zoom + 2)

	private fun createSelectionPaint() = Paint().apply {
		style = Paint.Style.STROKE
		strokeWidth = 2f
		pathEffect = DashPathEffect(PAINT_DASH, 0f)
	}

	private fun createLayerBoundsPaint() = Paint().apply {
		style = Paint.Style.STROKE
		color = Color.GRAY
		strokeWidth = 2f
		pathEffect = DashPathEffect(PAINT_DASH, 0f)
	}

	private fun createCheckerboardShader(): BitmapShader
	{
		val checkerboard = BitmapFactory.decodeResource(context.resources, R.drawable.checkerboard)
		return BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
	}

	private fun createCheckerboardPaint() = Paint().apply {
		shader = checkerboardShader
		isFilterBitmap = false
	}

	override fun draw(canvas: Canvas)
	{
		super.draw(canvas)
		if(isInEditMode) return
		val image = image ?: return
		val currentTool = currentTool ?: return

		setClipping(canvas, image)
		drawCheckerboard(canvas)
		removeClipping(canvas)
		drawImage(canvas, image, currentTool)
		drawLayerBounds(canvas, image)
		drawSelection(canvas)
		helpersManager?.onScreenDraw(canvas)
	}

	private fun setClipping(canvas: Canvas, image: Image)
	{
		canvas.save()
		canvas.clipRect(image.imageRect)
	}

	private fun removeClipping(canvas: Canvas)
	{
		canvas.restore()
	}

	private fun drawCheckerboard(canvas: Canvas)
	{
		checkerboardShader.setLocalMatrix(checkerboardMatrix)
		canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), checkerboardPaint)
	}

	private fun drawImage(canvas: Canvas, image: Image, currentTool: Tool)
	{
		var screenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
		var screenCanvas = Canvas(screenBitmap)

		for(layer in image.layers.reversed())
		{
			val drawLayer = layer.isVisible && !layer.isTemporaryHidden
			val drawTool = image.isLayerSelected(layer) && currentTool.doesOnLayerDraw(layer.isVisible)
			layer.mode.startDrawing(screenBitmap, screenCanvas)
			if(drawLayer)
			{
				layerMatrix.set(image.imageMatrix)
				layerMatrix.preTranslate(layer.x.toFloat(), layer.y.toFloat())
				layer.mode.setRectClipping(image.imageRect)
				layer.mode.addLayer(layerMatrix)
				layer.mode.resetClipping()
			}
			if(drawTool) layer.mode.addTool(createOnLayerToolBitmap(currentTool, layer, image))
			screenBitmap = layer.mode.apply()
			if(layer.mode.replacesBitmap()) screenCanvas = Canvas(screenBitmap)
		}

		val toolBitmap = createOnTopToolBitmap(currentTool, image)
		if(toolBitmap != null) screenCanvas.drawBitmap(toolBitmap, 0f, 0f, null)

		canvas.drawBitmap(screenBitmap, 0f, 0f, null)
	}

	private fun createOnLayerToolBitmap(tool: Tool, layer: Layer, image: Image): Bitmap?
	{
		if(!tool.doesOnLayerDraw(layer.isVisible)) return null
		val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
		val canvas = Canvas(bitmap)
		transformToolCanvas(canvas, image, tool.onLayerDrawingCoordinateSpace)
		tool.onLayerDraw(canvas)
		return bitmap
	}

	private fun createOnTopToolBitmap(tool: Tool, image: Image): Bitmap?
	{
		if(!tool.doesOnTopDraw()) return null
		val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
		val canvas = Canvas(bitmap)
		transformToolCanvas(canvas, image, tool.onTopDrawingCoordinateSpace)
		tool.onTopDraw(canvas)
		return bitmap
	}

	private fun transformToolCanvas(canvas: Canvas, image: Image, space: ToolCoordinateSpace)
	{
		when(space)
		{
			ToolCoordinateSpace.SCREEN_SPACE -> {}
			ToolCoordinateSpace.IMAGE_SPACE ->
			{
				canvas.scale(image.zoom, image.zoom)
				canvas.translate(-image.viewX, -image.viewY)
			}
			ToolCoordinateSpace.LAYER_SPACE ->
			{
				canvas.scale(image.zoom, image.zoom)
				canvas.translate(-image.viewX + image.selectedLayerX, -image.viewY + image.selectedLayerY)
			}
		}
	}

	private fun drawLayerBounds(canvas: Canvas, image: Image)
	{
		if(image.selectedLayer == null) return
		canvas.drawPath(boundsPath, layerBoundsPaint)
	}

	private fun drawSelection(canvas: Canvas)
	{
		canvas.drawPath(limitedSelectionPath!!, selectionPaint)
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent): Boolean
	{
		val image = image ?: return false
		val currentTool = currentTool ?: return false

		if(image.selectedLayer == null)
		{
			if(event.action != MotionEvent.ACTION_DOWN) currentTool.onTouch(event, context)
			return false
		}
		val result = currentTool.onTouch(event, context)
		if(!result) return false
		invalidate()
		return true
	}

	fun notifyImageChanged()
	{
		if(image == null) return
		updateLayerBounds()
		invalidate()
	}

	fun notifyLayersChanged()
	{
		if(image == null) return
		updateLayerBounds()
	}

	fun notifyImageMatrixChanged()
	{
		if(image == null) return
		updateCheckerboardMatrix()
		updateLayerBounds()
		updateSelectionPath()
		invalidate()
	}

	fun notifySelectionChanged()
	{
		if(image == null) return
		createSelectionPath()
	}

	private fun createSelectionPath()
	{
		val selection = selection ?: return

		val screen = Rect()
		screenRect.round(screen)
		val region = Region(selection.region)
		region.op(screen, Region.Op.INTERSECT)
		rawLimitedSelectionPath = region.boundaryPath
		rawSelectionPath = selection.path
		limitedSelectionPath = Path()
		selectionPath = Path()
		updateSelectionPath()
	}

	private fun updateCheckerboardMatrix()
	{
		checkerboardMatrix.reset()
		checkerboardMatrix.preTranslate(-image!!.viewX * image!!.zoom + CHECKERBOARD_OFFSET, -image!!.viewY * image!!.zoom + CHECKERBOARD_OFFSET)
	}

	private fun updateLayerBounds()
	{
		val selected = image?.selectedLayer ?: return
		val bounds = RectF(selected.bounds)
		bounds.intersect(screenRect)
		boundsPath.reset()
		boundsPath.addRect(bounds, Path.Direction.CW)
		boundsPath.close()
		boundsPath.transform(image!!.imageMatrix)
	}

	private fun updateSelectionPath()
	{
		if(rawLimitedSelectionPath == null || rawSelectionPath == null) return
		rawLimitedSelectionPath!!.transform(image!!.imageMatrix, limitedSelectionPath)
		rawSelectionPath!!.transform(image!!.imageMatrix, selectionPath)
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
	{
		image?.viewportWidth = width
		image?.viewportHeight = height
		image?.centerView()
	}

	fun setImage(image: Image)
	{
		if(this.image != null) throw IllegalStateException("Image already set")
		this.image = image
		selection = image.selection
		helpersManager = image.helpersManager

		notifySelectionChanged()
		notifyImageChanged()
		notifyLayersChanged()
	}

	fun setCurrentTool(tool: Tool)
	{
		currentTool = tool
		notifyImageChanged()
	}

	fun setSettings(settings: Settings)
	{
		LayerModeType.setAntialiasing(settings.smoothView)
	}
}
