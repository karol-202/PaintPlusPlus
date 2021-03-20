package pl.karol202.paintplus.image.layer.mode

import android.renderscript.RenderScript

class LayerModesService(renderScript: RenderScript)
{
	val layerModes = listOf(DefaultLayerMode,
	                        ScreenLayerMode,
	                        OverlayLayerMode,
	                        AddLayerMode(renderScript),
	                        SubtractionLayerMode(renderScript),
	                        DifferenceLayerMode(renderScript),
	                        MultiplyLayerMode(renderScript),
	                        LighterLayerMode(renderScript),
	                        DarkerLayerMode(renderScript))
}
