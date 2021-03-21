package pl.karol202.paintplus.image

import android.content.Context
import android.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.karol202.paintplus.R

private const val DEFAULT_IMAGE_SIZE = 600
private const val DEFAULT_IMAGE_BACKGROUND = Color.WHITE

class ImageService(context: Context)
{
	private val defaultLayerName = context.getString(R.string.new_layer_name)

	private val _imageFlow = MutableStateFlow(Image.new(width = DEFAULT_IMAGE_SIZE,
	                                                    height = DEFAULT_IMAGE_SIZE,
	                                                    layerName = defaultLayerName,
	                                                    color = DEFAULT_IMAGE_BACKGROUND))

	val imageFlow: StateFlow<Image> = _imageFlow

	val imageWidth get() = _imageFlow.value.width
	val imageHeight get() = _imageFlow.value.height
}
