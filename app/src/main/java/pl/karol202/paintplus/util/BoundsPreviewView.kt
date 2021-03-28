package pl.karol202.paintplus.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class BoundsPreviewView : View
{
	sealed class Bounds
	{
		data class Fill(override val rect: RectF,
		                override val color: Int) : Bounds()

		data class Stroke(override val rect: RectF,
		                  override val color: Int,
		                  val width: Float = 2f) : Bounds()

		abstract val rect: RectF
		abstract val color: Int
	}

	var bounds by invalidating(emptyList<Bounds>())

	private val fillPaint = Paint()
	private val strokePath = Paint().apply {
		style = Paint.Style.STROKE
		pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)
	}

	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	override fun onDraw(canvas: Canvas)
	{
		super.onDraw(canvas)
		val canvasSize = canvas.size.toSizeF()
		val canvasRect = canvasSize.toRectF()

		val allBounds = bounds.map { it.rect }.union()
		val allSize = allBounds.size()

		val fittedSize = allSize.fitInto(canvasSize)
		val fittedAllRect = fittedSize.toRectF().centeredInside(canvasRect)

		bounds.forEach {
			val mappedRect = it.rect.mapped(allBounds, fittedAllRect)
			canvas.drawBounds(it, mappedRect)
		}
	}

	private fun Canvas.drawBounds(bounds: Bounds, mappedRect: RectF) = when(bounds)
	{
		is Bounds.Fill -> drawFillBounds(mappedRect, bounds.color)
		is Bounds.Stroke -> drawStrokeBounds(mappedRect, bounds.color, bounds.width)
	}

	private fun Canvas.drawFillBounds(rect: RectF, color: Int)
	{
		fillPaint.color = color
		drawRect(rect, fillPaint)
	}

	private fun Canvas.drawStrokeBounds(rect: RectF, color: Int, width: Float)
	{
		strokePath.color = color
		strokePath.strokeWidth = width
		drawPath(rect.toPath(), strokePath)
	}
}
