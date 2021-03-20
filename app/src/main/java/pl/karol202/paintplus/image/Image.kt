package pl.karol202.paintplus.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import androidx.annotation.ColorInt
import androidx.core.graphics.times
import pl.karol202.paintplus.image.FlipDirection.*
import pl.karol202.paintplus.image.RotationAmount.*
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.util.preTranslated
import pl.karol202.paintplus.util.union
import kotlin.math.roundToInt

private const val MAX_LAYERS = 8

class Image private constructor(val width: Int,
                                val height: Int,
                                val layers: List<Layer>, // In order of drawing
                                val selectedLayerIndex: Int?,
                                val layersLocked: Boolean = false)
{
	companion object
	{
		fun new(width: Int, height: Int, layerName: String, @ColorInt color: Int) = Image(
				width = width,
				height = height,
				layers = listOf(Layer.create(
						name = layerName,
						width = width,
						height = height,
						color = color)),
				selectedLayerIndex = 0)
	}

	val selectedLayer get() = selectedLayerIndex?.let(layers::get)

	fun isLayerSelected(layer: Layer) = layer == selectedLayer

	// TODO viewX -= x; viewY -= y
	// TODO Offset selection
	fun resized(x: Int, y: Int, width: Int, height: Int) =
			copy(width = width,
			     height = height,
			     layers = layers.map { it.translated(-x, -y) })

	fun scaled(width: Int, height: Int, bilinear: Boolean): Image
	{
		val scaleX = width.toFloat() / this.width
		val scaleY = height.toFloat() / this.height
		return copy(width = width,
		            height = height,
		            layers = layers.map {
			            it.scaled(scaleX, scaleY, bilinear).withPosition(x = (it.x * scaleX).roundToInt(),
			                                                             y = (it.y * scaleY).roundToInt())
		            })
	}

	fun flipped(direction: FlipDirection) =
			copy(layers = layers.map {
				it.flip(direction).withPosition(x = if(direction == HORIZONTALLY) width - it.x - it.width else it.x,
				                                y = if(direction == VERTICALLY) height - it.y - it.height else it.y)
			})

	// TODO Correct viewX and viewY
	fun rotated(angle: RotationAmount) =
			copy(width = if(angle == ANGLE_90 || angle == ANGLE_270) height else width,
			     height = if(angle == ANGLE_90 || angle == ANGLE_270) width else height,
			     layers = layers.map {
				     when(angle)
				     {
				     	 ANGLE_90 -> it.withPosition(x = height - it.y - it.height,
				                                     y = it.x)
					     ANGLE_180 -> it.withPosition(x = width - it.x - it.width,
					                                  y = height - it.y - it.height)
					     ANGLE_270 -> it.withPosition(x = it.y,
					                                  y = width - it.x - it.width)
				     }.rotate(angle.angle, offset = false)
			     })

	fun withFlattenedLayers(newLayerName: String): Image
	{
		val bounds = layers.map { it.bounds }.union()
		val bitmap = flattenRect(bounds.left, bounds.top, bounds.width(), bounds.height())
		return copy(layers = listOf(Layer.create(x = bounds.left,
		                                         y = bounds.top,
		                                         name = newLayerName,
		                                         bitmap = bitmap)),
		            selectedLayerIndex = 0)
	}

	fun getFlattenedBitmap() = flattenRect(0, 0, width, height)

	private fun flattenRect(x: Int, y: Int, width: Int, height: Int): Bitmap
	{
		val initialBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
		val imageMatrix = Matrix().preTranslated(-x.toFloat(), -y.toFloat())
		return layers.fold(initialBitmap) { bitmap, layer ->
			if(!layer.visible) return@fold bitmap
			layer.mode.apply(bitmap, Canvas(bitmap), opacity = layer.opacity) {
				drawBitmap(layer.bitmap, imageMatrix * layer.matrix, null)
			}.bitmap
		}
	}

	fun withLayer(layer: Layer, autoSelect: Boolean = false) =
			if(layers.size >= MAX_LAYERS) null
			else copy(layers = layers + layer,
			          selectedLayerIndex = if(autoSelect) layers.size else selectedLayerIndex)

	private fun copy(width: Int = this.width, height: Int = this.height, layers: List<Layer> = this.layers,
	                 selectedLayerIndex: Int? = this.selectedLayerIndex, layersLocked: Boolean = this.layersLocked) =
			Image(width, height, layers, selectedLayerIndex, layersLocked)
}
