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
package pl.karol202.paintplus.image.layer

import android.graphics.*
import pl.karol202.paintplus.image.FlipDirection
import kotlin.jvm.JvmOverloads
import pl.karol202.paintplus.image.layer.mode.DefaultLayerMode
import pl.karol202.paintplus.image.layer.mode.LayerMode
import pl.karol202.paintplus.util.*
import kotlin.math.roundToInt

class Layer(val x: Int,
            val y: Int,
            val name: String,
            val bitmap: Bitmap,
            val mode: LayerMode = DefaultLayerMode,
            val visible: Boolean = true,
            val opacity: Float = 1f)
{
	companion object
	{
		fun create(x: Int = 0, y: Int = 0, name: String, bitmap: Bitmap) =
				Layer(x, y, name, bitmap.ensureMutable().withAlpha(true))

		fun create(x: Int = 0, y: Int = 0, name: String, width: Int, height: Int, color: Int) =
				create(x, y, name, Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)).apply {
					bitmap.eraseColor(color)
				}
	}

	val editCanvas = Canvas(bitmap)

	val width = bitmap.width
	val height = bitmap.height
	val bounds = Rect(x, y, x + width, y + height)
	val matrix = Matrix().preTranslated(x.toFloat(), y.toFloat())

	fun translated(x: Int, y: Int) =
			withPosition(this.x + x, this.y + y)

	fun cropped(x: Int, y: Int, width: Int, height: Int) =
			withBitmap(width, height).translated(x, y).apply {
				editCanvas.drawBitmap(this@Layer.bitmap, -x.toFloat(), -y.toFloat(), null)
			}

	fun scaled(width: Int, height: Int, bilinear: Boolean) =
			withBitmap(bitmap.transformedWith(Matrix().preScaled(width.toFloat() / bitmap.width,
			                                                     height.toFloat() / bitmap.height), bilinear))

	fun scaled(scaleX: Float, scaleY: Float, bilinear: Boolean) =
			scaled((bitmap.width * scaleX).roundToInt(), (bitmap.height * scaleY).roundToInt(), bilinear)

	fun flip(direction: FlipDirection) =
			scaled(if(direction == FlipDirection.HORIZONTALLY) -1f else 1f,
			       if(direction == FlipDirection.VERTICALLY) -1f else 1f, bilinear = false)

	@JvmOverloads
	fun rotate(angle: Float, offset: Boolean = true): Layer
	{
		val newBitmap = bitmap.transformedWith(Matrix().preRotated(angle), bilinear = true)
		return withBitmap(newBitmap).translated(if(offset) (newBitmap.width - width) / 2 else 0,
		                                        if(offset) (newBitmap.height - height) / 2 else 0)
	}

	fun withPosition(x: Int, y: Int) = copy(x = x, y = y)

	fun withOpacity(opacity: Float) = copy(opacity = opacity)

	fun withVisibility(visible: Boolean) = copy(visible = visible)

	fun withMode(mode: LayerMode) = copy(mode = mode)

	private fun withBitmap(bitmap: Bitmap) =
			copy(bitmap = bitmap)

	private fun withBitmap(width: Int, height: Int) =
			withBitmap(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))

	private fun copy(x: Int = this.x, y: Int = this.x, name: String = this.name, bitmap: Bitmap = this.bitmap,
	                 mode: LayerMode = this.mode, visible: Boolean = this.visible, opacity: Float = this.opacity) =
			Layer(x, y, name, bitmap, mode, visible, opacity)
}