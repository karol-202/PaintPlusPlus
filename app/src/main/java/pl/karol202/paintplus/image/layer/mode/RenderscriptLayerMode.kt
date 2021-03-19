package pl.karol202.paintplus.image.layer.mode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.renderscript.Allocation
import android.renderscript.RenderScript
import pl.karol202.paintplus.util.BitmapWithCanvas

abstract class RenderscriptLayerMode(override val type: LayerModeType,
                                     private val renderScript: RenderScript,
                                     private val layerScript: LayerScript) : LayerMode
{
	override fun apply(dstBitmap: Bitmap, dstCanvas: Canvas, opacity: Float, builder: Canvas.() -> Unit): BitmapWithCanvas
	{
		val dstAllocation = Allocation.createFromBitmap(renderScript, dstBitmap)

		val srcBitmap = Bitmap.createBitmap(dstBitmap.width, dstBitmap.height, Bitmap.Config.ARGB_8888)
		val srcCanvas = Canvas(srcBitmap)
		val srcAllocation = Allocation.createFromBitmap(renderScript, srcBitmap)
		srcCanvas.builder()

		val outBitmap = Bitmap.createBitmap(dstBitmap.width, dstBitmap.height, Bitmap.Config.ARGB_8888)
		val outCanvas = Canvas(outBitmap)
		val outAllocation = Allocation.createFromBitmap(renderScript, outBitmap)

		layerScript.setDstAllocation(dstAllocation)
		layerScript.setOpacity(opacity)
		layerScript.run(srcAllocation, outAllocation)
		outAllocation.copyTo(outBitmap)

		return BitmapWithCanvas(outBitmap, outCanvas)
	}
}
