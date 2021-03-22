package pl.karol202.paintplus.options

import android.graphics.*

class OptionCopy : Option
{
	fun execute()
	{
		val bounds: Rect = selection.getBounds()
		left = bounds.left
		top = bounds.top
		val path = Path(selection.getPath())
		path.offset(-left.toFloat(), -top.toFloat())

		bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888)
		val canvas = Canvas(bitmap)
		canvas.clipPath(path)
		canvas.drawBitmap(selectedLayer.bitmap, (-left + selectedLayer.x).toFloat(), (-top + selectedLayer.y).toFloat(), null)
	}
}
