package pl.karol202.paintplus.options

import android.graphics.*
import pl.karol202.paintplus.R
import pl.karol202.paintplus.history.legacyaction.ActionLayerChange

class OptionCut(private val optionCopy: OptionCopy) : Option
{
	fun execute()
	{
		optionCopy.execute()
		//copy(selectedLayer)

		val action = ActionLayerChange(image, R.string.history_action_cut)
		action.setLayerChange(image.getLayerIndex(selectedLayer), selectedLayer.bitmap, selection.getBounds())

		val paint = Paint()
		paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

		val path = Path(selection.getPath())
		path.offset(-selectedLayer.x.toFloat(), -selectedLayer.y.toFloat())

		val canvas: Canvas = selectedLayer.editCanvas
		if(canvas.saveCount > 0) canvas.restoreToCount(1)
		canvas.clipPath(path)
		canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)

		action.applyAction()
	}
}
