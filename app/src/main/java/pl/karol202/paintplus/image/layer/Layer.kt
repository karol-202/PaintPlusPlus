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
import pl.karol202.paintplus.image.Image.FlipDirection
import kotlin.jvm.JvmOverloads
import pl.karol202.paintplus.image.layer.mode.DefaultLayerMode
import pl.karol202.paintplus.image.layer.mode.LayerMode
import pl.karol202.paintplus.util.preRotated
import pl.karol202.paintplus.util.preScaled
import pl.karol202.paintplus.util.transformedWith
import kotlin.math.roundToInt

class Layer private constructor(val x: Int,
                                val y: Int,
                                val name: String,
                                val bitmap: Bitmap,
                                val mode: LayerMode = DefaultLayerMode,
                                val visible: Boolean = true,
                                val opacity: Float = 1f)
{
	companion object
	{
		fun create(x: Int, y: Int, name: String, bitmap: Bitmap): Layer
		{
			val finalBitmap = if(!bitmap.isMutable) bitmap.copy(Bitmap.Config.ARGB_8888, true) else bitmap
			finalBitmap.setHasAlpha(true)
			return Layer(x, y, name, bitmap)
		}

		fun create(x: Int, y: Int, name: String, width: Int, height: Int, color: Int) =
				create(x, y, name, Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)).apply {
					bitmap.eraseColor(color)
				}
	}

	val editCanvas = Canvas(bitmap)

	val width get() = bitmap.width
	val height get() = bitmap.height
	val bounds get() = Rect(x, y, x + width, y + height)

	private fun copy(x: Int = this.x, y: Int = this.x, name: String = this.name, bitmap: Bitmap = this.bitmap,
	                 mode: LayerMode = this.mode, visible: Boolean = this.visible, opacity: Float = this.opacity) =
			Layer(x, y, name, bitmap, mode, visible, opacity)

	private fun withBitmap(bitmap: Bitmap) =
			copy(bitmap = bitmap)

	private fun withBitmap(width: Int, height: Int) =
			withBitmap(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))

	fun translated(x: Int, y: Int) =
			copy(x = this.x + x, y = this.y + y)

	fun resized(x: Int, y: Int, width: Int, height: Int) =
			withBitmap(width, height).translated(x, y).apply {
				editCanvas.drawBitmap(this@Layer.bitmap, -x.toFloat(), -y.toFloat(), null)
			}

	fun scaled(width: Int, height: Int, bilinear: Boolean) =
			withBitmap(bitmap.transformedWith(Matrix().preScaled(width.toFloat() / bitmap.width,
			                                                     height.toFloat() / bitmap.height), bilinear))

	fun scaled(scaleX: Double, scaleY: Double, bilinear: Boolean) =
			scaled((bitmap.width * scaleX).roundToInt(), (bitmap.height * scaleY).roundToInt(), bilinear)

	fun flip(direction: FlipDirection) =
			scaled(if(direction == FlipDirection.HORIZONTALLY) -1.0 else 1.0,
			       if(direction == FlipDirection.VERTICALLY) -1.0 else 1.0, bilinear = false)

	@JvmOverloads
	fun rotate(angle: Float, offset: Boolean = true): Layer
	{
		val newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, Matrix().preRotated(angle), true)
		return withBitmap(newBitmap).translated(if(offset) (newBitmap.width - width) / 2 else 0,
		                                        if(offset) (newBitmap.height - height) / 2 else 0)
	}

	/*fun drawLayerAndReturnBitmap(bitmap: Bitmap?, canvas: Canvas?, clipRect: RectF?, imageMatrix: Matrix?): Bitmap
	{
		val layerMatrix = Matrix(imageMatrix)
		layerMatrix.preTranslate(x.toFloat(), y.toFloat())
		mode.startDrawing(bitmap, canvas)
		if(clipRect != null) mode.setRectClipping(clipRect)
		mode.addLayer(layerMatrix)
		if(clipRect != null) mode.resetClipping()
		return mode.apply()
	}*/
}
