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
package pl.karol202.paintplus.tool.gradient.shape

import android.graphics.*
import pl.karol202.paintplus.tool.gradient.Gradient
import pl.karol202.paintplus.tool.gradient.GradientRepeatability
import pl.karol202.paintplus.tool.gradient.ToolGradient
import pl.karol202.paintplus.util.cache
import pl.karol202.paintplus.util.memoize

abstract class AbstractGradientShape : GradientShape
{
	override var gradient = Gradient.createSimpleGradient(Color.WHITE, Color.BLACK)
	override var repeatability = GradientRepeatability.NO_REPEAT
	override var isReverted = false

	protected val positionsArray get() =
		if(isReverted) gradient.revertedPositionsArray
		else gradient.positionsArray
	protected val colorsArray get() =
		if(isReverted) gradient.revertedColorsArray
		else gradient.colorsArray
	protected val tileMode get() = repeatability.tileMode

	private val paint by memoize { _: Gradient, _: GradientRepeatability, _: Boolean, start: Point, end: Point ->
		Paint().apply {
			shader = createShader(start, end)
		}
	}

	abstract fun createShader(start: Point, end: Point): Shader

	override fun applyGradient(canvas: Canvas, start: Point, end: Point) = drawGradient(canvas, start, end)

	override fun onScreenDraw(canvas: Canvas, start: Point, end: Point) = drawGradient(canvas, start, end)

	private fun drawGradient(canvas: Canvas, start: Point, end: Point) =
			canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(),
			                paint(gradient, repeatability, isReverted, start, end))
}
