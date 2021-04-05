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

import android.graphics.Canvas
import android.graphics.Paint
import pl.karol202.paintplus.tool.gradient.ToolGradient
import android.graphics.PointF
import android.graphics.Shader.TileMode
import android.graphics.Shader
import pl.karol202.paintplus.tool.gradient.Gradient

interface GradientShape
{
	val name: Int
	val icon: Int

	fun applyGradient(canvas: Canvas)

	fun onScreenDraw(canvas: Canvas)
}
