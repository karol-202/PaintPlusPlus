package pl.karol202.paintplus.options

import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer

class OptionLayerSelect(private val imageService: ImageService) : Option
{
	fun execute(layer: Layer)
	{
		if(!imageService.image.hasLayer(layer)) return
		imageService.editImage { withLayerSelected(layer) }
	}
}
