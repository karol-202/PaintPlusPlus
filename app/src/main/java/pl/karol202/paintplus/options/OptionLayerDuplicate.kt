package pl.karol202.paintplus.options

import pl.karol202.paintplus.image.layer.Layer

class OptionLayerDuplicate : Option
{
	//private val DUPLICATE_INDICATOR = context.getString(R.string.duplicate)

	fun execute(layer: Layer)
	{
		val layerIndex = layers!!.indexOf(layer)
		val newName = layer.name + DUPLICATE_INDICATOR
		val newLayer = Layer(layer.x, layer.y, newName, layer.width, layer.height, Color.BLACK)
		val newBitmap = Bitmap.createBitmap(layer.bitmap)
		newLayer.setBitmap(newBitmap)
		newLayer.setMode(copyLayerMode(layer.mode))
		newLayer.setOpacity(layer.opacity)
		if(!image!!.addLayer(newLayer, layerIndex)) appContext.createSnackbar(R.string.too_many_layers, Toast.LENGTH_SHORT)
				.show()
		else createDuplicateHistoryAction(newLayer)
	}

	private fun createDuplicateHistoryAction(newLayer: pl.karol202.paintplus.image.layer.Layer)
	{
		val action = ActionLayerDuplicate(image)
		action.setLayerAfterAdding(newLayer)
		action.applyAction()
	}
}
