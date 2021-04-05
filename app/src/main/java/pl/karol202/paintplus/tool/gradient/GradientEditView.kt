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
package pl.karol202.paintplus.tool.gradient

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import pl.karol202.paintplus.R
import pl.karol202.paintplus.util.*
import pl.karol202.paintplus.util.MathUtils.dpToPixels
import pl.karol202.paintplus.util.MathUtils.map
import kotlin.math.abs

private const val SIDE_MARGIN_DP = 10f
private const val TOP_BAR_HEIGHT_DP = 15f
private const val BOTTOM_BAR_HEIGHT_DP = 25f
private const val TRIANGLE_Y_OFFSET_DP = TOP_BAR_HEIGHT_DP + BOTTOM_BAR_HEIGHT_DP - 5
private const val HEIGHT_DP = TOP_BAR_HEIGHT_DP + BOTTOM_BAR_HEIGHT_DP + 20
private const val TOP_MARGIN_PX = 1f
private const val BORDER_WIDTH_PX = 1f
private const val MAX_TOUCH_DISTANCE = 0.05f

class GradientEditView(context: Context, attrs: AttributeSet?) : View(context, attrs)
{
	private val sideMarginPx = dpToPixels(context, SIDE_MARGIN_DP)
	private val topBarHeightPx = dpToPixels(context, TOP_BAR_HEIGHT_DP)
	private val bottomBarHeightPx = dpToPixels(context, BOTTOM_BAR_HEIGHT_DP)
	private val triangleYOffsetPx = dpToPixels(context, TRIANGLE_Y_OFFSET_DP) + TOP_MARGIN_PX
	private val heightPx = dpToPixels(context, HEIGHT_DP) + TOP_MARGIN_PX

	private val triangleMatrix = context.resources.displayMetrics.density.let { scale -> Matrix().preScaled(scale, scale) }
	private val triangleOuterPath = Path().apply {
		moveTo(0f, 0f)
		lineTo(10f, 20f)
		lineTo(-10f, 20f)
		close()
		transform(triangleMatrix)
	}
	private val triangleInnerPath = Path().apply {
		moveTo(0f, 4.5f)
		lineTo(7f, 18f)
		lineTo(-7f, 18f)
		close()
		transform(triangleMatrix)
	}

	private val borderPaint = Paint().apply {
		style = Paint.Style.STROKE
		strokeWidth = BORDER_WIDTH_PX
		color = ResourcesCompat.getColor(context.resources, R.color.border, null)
	}
	private val checkerboardPaint = Paint().apply {
		val checkerboard = BitmapFactory.decodeResource(resources, R.drawable.checkerboard)
		shader = BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT).apply {
			setLocalMatrix(Matrix().preTranslated(-5f, -7f))
		}
		isFilterBitmap = false
	}
	private val topBarPaint by cache({gradient}) { gradient ->
		Paint().apply {
			shader = LinearGradient(sideMarginPx, 0f, width - sideMarginPx, 0f, gradient.colorsArray,
			                        gradient.positionsArray, Shader.TileMode.CLAMP)
		}
	}
	private val bottomBarPaint by cache({gradient}) { gradient ->
		Paint().apply {
			val colors = gradient.colorsArray.map { (it.toLong() or 0xFF000000).toInt() }.toIntArray()
			shader = LinearGradient(sideMarginPx, 0f, width - sideMarginPx, 0f, colors,
			                        gradient.positionsArray, Shader.TileMode.CLAMP)
		}
	}
	private val triangleOuterPaint = Paint().apply {
		isAntiAlias = true
		color = Color.DKGRAY
	}
	private val triangleInnerPaint = Paint().apply {
		isAntiAlias = true
		color = Color.WHITE
	}
	private val triangleInnerSelectedPaint = Paint().apply {
		isAntiAlias = true
		color = ResourcesCompat.getColor(context.resources, R.color.gradient_point_selected, null)
	}

	private val borderRect by lazy {
		RectF(sideMarginPx - BORDER_WIDTH_PX,
		      TOP_MARGIN_PX - BORDER_WIDTH_PX,
		      width - sideMarginPx,
		      TOP_MARGIN_PX + topBarHeightPx + bottomBarHeightPx)
	}
	private val checkerboardRect by lazy {
		RectF(sideMarginPx,
		      TOP_MARGIN_PX,
		      width - sideMarginPx,
		      TOP_MARGIN_PX + topBarHeightPx + bottomBarHeightPx)
	}
	private val topBarRect by lazy {
		RectF(sideMarginPx,
		      TOP_MARGIN_PX,
		      width - sideMarginPx,
		      TOP_MARGIN_PX + topBarHeightPx)
	}
	private val bottomBarRect by lazy {
		RectF(sideMarginPx,
		      TOP_MARGIN_PX + topBarHeightPx,
		      width - sideMarginPx,
		      TOP_MARGIN_PX + topBarHeightPx + bottomBarHeightPx)
	}

	var gradient by invalidating(Gradient.createSimpleGradient(Color.BLACK, Color.WHITE))
	var addingMode = false
	var onGradientUpdated: (() -> Unit)? = null

	private var selectedPointId: String? = null
	private var draggedPointId: String? = null
	private var lastPosition = 0f

	private val selectedPoint get() = selectedPointId?.let(gradient::get)
	private val draggedPoint get() = draggedPointId?.let(gradient::get)

	val selectedPosition get() = selectedPoint?.position
	val selectedColor get() = selectedPoint?.color
	val canDeletePoint = selectedPointId != null && gradient.pointsAmount >= 3

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		setMeasuredDimension(measuredWidth, heightPx.toInt())
	}

	override fun onDraw(canvas: Canvas)
	{
		super.onDraw(canvas)
		drawBorder(canvas)
		drawCheckerboard(canvas)
		drawTopBar(canvas)
		drawBottomBar(canvas)
		drawTriangles(canvas)
	}

	private fun drawBorder(canvas: Canvas) = canvas.drawRect(borderRect, borderPaint)

	private fun drawCheckerboard(canvas: Canvas) = canvas.drawRect(checkerboardRect, checkerboardPaint)

	private fun drawTopBar(canvas: Canvas) = canvas.drawRect(topBarRect, topBarPaint)

	private fun drawBottomBar(canvas: Canvas) = canvas.drawRect(bottomBarRect, bottomBarPaint)

	private fun drawTriangles(canvas: Canvas) = gradient.points.forEach { drawTriangle(canvas, it) }

	private fun drawTriangle(canvas: Canvas, point: Gradient.Point)
	{
		val selected = point.id == selectedPointId
		val xOffset = map(point.position, 0f, 1f, sideMarginPx, width - sideMarginPx)
		val offset = PointF(xOffset, triangleYOffsetPx)
		canvas.drawPath(triangleOuterPath + offset, triangleOuterPaint)
		canvas.drawPath(triangleInnerPath + offset, if(selected) triangleInnerSelectedPaint else triangleInnerPaint)
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent) = when(event.action)
	{
		MotionEvent.ACTION_DOWN -> onTouchDown(event.x)
		MotionEvent.ACTION_MOVE -> true.also { onTouchMove(event.x) }
		MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> true.also { onTouchUp(event.x) }
		else -> true
	}.also { invalidate() }

	private fun onTouchDown(x: Float): Boolean
	{
		if(addingMode) return true
		val nearestPoint = findNearestPoint(calculateGradientPosition(x))
		selectedPointId = nearestPoint?.id
		draggedPointId = nearestPoint?.id
		lastPosition = x
		onGradientUpdated?.invoke()
		return nearestPoint != null
	}

	private fun findNearestPoint(gradientPos: Float): Gradient.Point?
	{
		var nearest: Gradient.Point? = null
		var nearestDistance = MAX_TOUCH_DISTANCE
		for(point in gradient.points)
		{
			val distance: Float = abs(point.position - gradientPos)
			if(distance >= nearestDistance) continue
			nearest = point
			nearestDistance = distance
		}
		return nearest
	}

	private fun onTouchMove(x: Float)
	{
		if(addingMode) return
		val draggedPoint = draggedPoint ?: return
		val gradientOffset = calculateGradientDistance(x - lastPosition)
		val newPosition = (draggedPoint.position + gradientOffset).coerceIn(0f, 1f)

		lastPosition = x
		setSelectedPosition(newPosition)
	}

	private fun calculateGradientPosition(viewPosition: Float) =
			map(viewPosition, sideMarginPx, width - sideMarginPx, 0f, 1f)

	private fun calculateGradientDistance(distance: Float) =
			map(distance, 0f, width - 2 * sideMarginPx, 0f, 1f)

	private fun onTouchUp(x: Float)
	{
		if(addingMode) addPoint(x)
		else
		{
			onTouchMove(x)
			draggedPointId = null
		}
	}

	private fun addPoint(viewX: Float)
	{
		val gradientPosition = calculateGradientPosition(viewX)
		val point = Gradient.Point.create(gradientPosition, gradient.getColorAtPosition(gradientPosition))
		gradient = gradient.withPointAdded(point)
		selectedPointId = point.id
		onGradientUpdated?.invoke()
	}

	fun setSelectedPosition(position: Float)
	{
		val point = selectedPoint ?: return
		gradient = gradient.withPointUpdated(point.copy(position = position))
		onGradientUpdated?.invoke()
	}

	fun setSelectedColor(color: Int)
	{
		val point = selectedPoint ?: return
		gradient = gradient.withPointUpdated(point.copy(color = color))
		onGradientUpdated?.invoke()
	}

	fun deleteSelectedPoint()
	{
		if(!canDeletePoint) return
		val point = selectedPoint ?: return
		gradient = gradient.withPointDeleted(point)
		selectedPointId = null
		onGradientUpdated?.invoke()
	}
}
