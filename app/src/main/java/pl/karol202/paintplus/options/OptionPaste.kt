package pl.karol202.paintplus.options

import android.graphics.*
import pl.karol202.paintplus.history.legacyaction.ActionPaste
import pl.karol202.paintplus.image.layer.Layer

class OptionPaste : Option
{
	fun execute()
	{
		val layer: Layer = image.newLayer(bitmap.getWidth(), bitmap.getHeight(), defaultLayerName) ?: return
		layer.setPosition(left, top)
		layer.setBitmap(bitmap)

		val action = ActionPaste(image)
		action.setLayerAfterAdding(layer)
		action.applyAction()
	}
}
