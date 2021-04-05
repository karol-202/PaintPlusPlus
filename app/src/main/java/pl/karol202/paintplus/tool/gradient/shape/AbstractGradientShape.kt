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
import pl.karol202.paintplus.tool.gradient.ToolGradient
import pl.karol202.paintplus.util.cache

abstract class AbstractGradientShape(private val toolGradient: ToolGradient) : GradientShape
{
	protected val positionsArray get() =
		if(toolGradient.isReverted) toolGradient.gradient.revertedPositionsArray
		else toolGradient.gradient.positionsArray
	protected val colorsArray get() =
		if(toolGradient.isReverted) toolGradient.gradient.revertedColorsArray
		else toolGradient.gradient.colorsArray
	protected val tileMode get() = toolGradient.repeatability.tileMode

	private val paint by cache({toolGradient.gradient}, {toolGradient.repeatability}, {toolGradient.isReverted}) { _, _, _ ->
		Paint().apply {
			val (start, end) = toolGradient.gradientPoints ?: return@apply
			shader = createShader(start, end)
		}
	}

	abstract fun createShader(start: Point, end: Point): Shader

	override fun applyGradient(canvas: Canvas) = drawGradient(canvas)

	override fun onScreenDraw(canvas: Canvas) = drawGradient(canvas)

	private fun drawGradient(canvas: Canvas) =
			canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)
}
