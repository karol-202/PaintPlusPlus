package pl.karol202.paintplus.options

import pl.karol202.paintplus.image.layer.Layer

class OptionLayerSelect : Option
{
	fun execute(layer: Layer)
	{
		if(adapter.isLayerSelected(layer) || adapter.areLayersLocked()) return
		adapter.notifyItemChanged(adapter.image.selectedLayerIndex)
		adapter.image.selectLayer(layer)
		bind(layer)
	}
}
