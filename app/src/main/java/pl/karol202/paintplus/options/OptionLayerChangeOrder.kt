package pl.karol202.paintplus.options

import pl.karol202.paintplus.history.legacyaction.ActionLayerOrderMove

class OptionLayerChangeOrder : Option
{
	fun execute(layerIndex: Int, target: Int)
	{
		val selected = image!!.selectedLayer
		val layer = layers!!.removeAt(layerIndex)
		layers!!.add(target, layer)
		image!!.selectLayer(layers!!.indexOf(selected))
		image!!.updateImage()
		val action = ActionLayerOrderMove(image)
		action.setSourceAndDestinationLayerPos(layerIndex, target)
		action.applyAction()
	}
}
