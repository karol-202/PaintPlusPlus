package pl.karol202.paintplus.options

import pl.karol202.paintplus.image.layer.Layer

class OptionLayerMergeDown : Option
{
	fun execute(layer: Layer)
	{
		val firstIndex = layers!!.indexOf(firstLayer)
		val secondLayer = layers!![firstIndex + 1]
		val resultBounds = firstLayer.bounds
		resultBounds.union(secondLayer.bounds)
		val matrix = Matrix()
		matrix.preTranslate(-resultBounds.left.toFloat(), -resultBounds.top.toFloat())
		var resultBitmap = Bitmap.createBitmap(resultBounds.width(), resultBounds.height(), Bitmap.Config.ARGB_8888)
		val resultCanvas = Canvas(resultBitmap!!)
		resultBitmap = secondLayer.drawLayerAndReturnBitmap(resultBitmap, resultCanvas, null, matrix)
		resultBitmap = firstLayer.drawLayerAndReturnBitmap(resultBitmap, resultCanvas, null, matrix)
		val resultLayer = Layer(resultBounds.left, resultBounds.top, firstLayer.name, resultBounds.width(), resultBounds.height(), Color.TRANSPARENT)
		resultLayer.setBitmap(resultBitmap)
		image!!.deleteLayer(firstLayer)
		image!!.deleteLayer(secondLayer)
		image!!.addLayer(resultLayer, firstIndex)
		createJoinHistoryAction(firstLayer, secondLayer, firstIndex)
	}

	private fun createJoinHistoryAction(firstLayer: Layer, secondLayer: Layer, resultLayerId: Int)
	{
		val action = ActionLayerJoin(image)
		action.setLayers(firstLayer, secondLayer, resultLayerId)
		action.applyAction()
	}
}
