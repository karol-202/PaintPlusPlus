package pl.karol202.paintplus.options

import pl.karol202.paintplus.history.legacyaction.ActionLayerVisibilityChange
import pl.karol202.paintplus.image.layer.Layer

class OptionLayerVisibilityToggle : Option
{
	fun execute(layer: Layer)
	{
		val action = ActionLayerVisibilityChange(adapter.image)
		action.setLayerBeforeChange(layer)
		layer.setVisibility(!layer!!.visible)
		if(adapter.isLayerSelected(layer))
			views.buttonLayerVisibility.setImageResource(if(layer!!.visible) R.drawable.ic_visible_white_24dp
			                                             else R.drawable.ic_invisible_white_24dp)
		else views.buttonLayerVisibility.setImageResource(if(layer!!.visible) R.drawable.ic_visible_black_24dp
		                                                  else R.drawable.ic_invisible_black_24dp)
		action.applyAction()
	}
}
