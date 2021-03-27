package pl.karol202.paintplus.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.karol202.paintplus.R

private const val DEFAULT_IMAGE_SIZE = 600
private const val DEFAULT_IMAGE_BACKGROUND = Color.WHITE

class ImageService(context: Context,
                   private val historyService: HistoryService)
{
	private val defaultLayerName = context.getString(R.string.new_layer_name)

	private val _imageFlow = MutableStateFlow(Image.new(width = DEFAULT_IMAGE_SIZE,
	                                                    height = DEFAULT_IMAGE_SIZE,
	                                                    layerName = defaultLayerName,
	                                                    color = DEFAULT_IMAGE_BACKGROUND))
	private val _selectionFlow = MutableStateFlow(Selection.empty)

	val imageFlow: StateFlow<Image> = _imageFlow
	val selectionFlow: StateFlow<Selection> = _selectionFlow

	val image get() = _imageFlow.value
	val imageWidth get() = image.width
	val imageHeight get() = image.height
	val selection get() = _selectionFlow.value

	fun newImage(width: Int, height: Int)
	{
		_imageFlow.value = Image.new(width = width,
		                             height = height,
		                             layerName = defaultLayerName,
		                             color = DEFAULT_IMAGE_BACKGROUND)
		_selectionFlow.value = Selection.empty
		historyService.clearHistory()
	}

	fun openImage(bitmap: Bitmap)
	{
		_imageFlow.value = Image.fromBitmap(bitmap = bitmap,
		                                    layerName = defaultLayerName)
		_selectionFlow.value = Selection.empty
		historyService.clearHistory()
	}

	inline fun editImage(builder: Image.() -> Image) = setImage(image.builder())

	inline fun editSelection(builder: Selection.() -> Selection) = setSelection(selection.builder())

	fun setImage(image: Image)
	{
		_imageFlow.value = image
	}

	fun setSelection(selection: Selection)
	{
		_selectionFlow.value = selection
	}
}
