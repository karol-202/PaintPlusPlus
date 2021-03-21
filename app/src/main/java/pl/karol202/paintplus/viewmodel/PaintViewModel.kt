package pl.karol202.paintplus.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.karol202.paintplus.R
import pl.karol202.paintplus.color.curves.ColorChannel
import pl.karol202.paintplus.color.picker.ColorPickerConfig
import pl.karol202.paintplus.image.ColorsService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.image.layer.mode.LayerModesService
import pl.karol202.paintplus.options.*
import pl.karol202.paintplus.settings.SettingsRepository
import pl.karol202.paintplus.tool.Tool
import pl.karol202.paintplus.tool.ToolsService

class PaintViewModel(application: Application,
                     settingsRepository: SettingsRepository,
                     imageService: ImageService,
                     private val toolsService: ToolsService,
                     private val colorService: ColorsService,
                     private val layerModesService: LayerModesService,
                     private val optionColorsInvert: OptionColorsInvert,
                     private val optionCropImageBySelection: OptionCropImageBySelection,
                     private val optionCropLayerBySelection: OptionCropLayerBySelection,
                     private val optionFileCapturePhoto: OptionFileCapturePhoto,
                     private val optionFileNew: OptionFileNew,
                     private val optionFileOpen: OptionFileOpen,
                     private val optionFileSave: OptionFileSave,
                     private val optionImageFlatten: OptionImageFlatten,
                     private val optionImageFlip: OptionImageFlip,
                     private val optionImageRotate: OptionImageRotate,
                     private val optionLayerChangeOrder: OptionLayerChangeOrder,
                     private val optionLayerDelete: OptionLayerDelete,
                     private val optionLayerDuplicate: OptionLayerDuplicate,
                     private val optionLayerFlip: OptionLayerFlip,
                     private val optionLayerInfoShow: OptionLayerInfoShow,
                     private val optionLayerMergeDown: OptionLayerMergeDown,
                     private val optionLayerNameChange: OptionLayerNameChange,
                     private val optionLayerOpen: OptionLayerOpen,
                     private val optionLayerPropertiesEdit: OptionLayerPropertiesEdit,
                     private val optionLayerSave: OptionLayerSave,
                     private val optionLayerSelect: OptionLayerSelect,
                     private val optionLayerToImageSize: OptionLayerToImageSize,
                     private val optionLayerVisibilityToggle: OptionLayerVisibilityToggle,
                     private val optionSetZoom: OptionSetZoom) : BaseViewModel(application)
{
	enum class TitleOverride
	{
		NONE, TOOL_SELECTION, TOOL_PROPERTIES
	}

	data class MessageEvent(@StringRes val text: Int)

	sealed class ActionRequest<R>(val callback: (R) -> Unit)
	{
		class OpenFile(val mimeFilters: List<String>,
		               callback: (Uri?) -> Unit) : ActionRequest<Uri?>(callback)

		class SaveFile(val suggestedName: String,
		               callback: (Uri?) -> Unit) : ActionRequest<Uri?>(callback)

		class CapturePhoto(val uri: Uri,
		                   callback: (Boolean) -> Unit) : ActionRequest<Boolean>(callback)

		class PickColor(val pickerConfig: ColorPickerConfig,
		                callback: (Int?) -> Unit) : ActionRequest<Int?>(callback)
	}

	val context: Context get() = getApplication()

	private val _titleOverrideFlow = MutableStateFlow(TitleOverride.NONE)
	private val _dialogFlow = MutableStateFlow<DialogDefinition?>(null)
	private val _messageEventFlow = MutableSharedFlow<MessageEvent>(extraBufferCapacity = 16)
	private val _actionRequestEventFlow = MutableSharedFlow<ActionRequest<*>>(extraBufferCapacity = 16)

	val imageFlow = imageService.imageFlow
	val currentToolFlow = toolsService.currentToolFlow
	val currentColorFlow = colorService.currentColorFlow
	val settingsFlow = settingsRepository.settings
	val titleFlow = _titleOverrideFlow.combine(currentToolFlow) { override, tool -> createTitle(override, tool) }
	val dialogFlow: StateFlow<DialogDefinition?> = _dialogFlow
	val messageEventFlow: Flow<MessageEvent> = _messageEventFlow
	val actionRequestEventFlow: Flow<ActionRequest<*>> = _actionRequestEventFlow

	val currentTool get() = currentToolFlow.value
	val currentColor get() = currentColorFlow.value

	private fun createTitle(override: TitleOverride, tool: Tool): String
	{
		val toolName = context.getString(tool.name)
		return when(override)
		{
			TitleOverride.NONE -> toolName
			TitleOverride.TOOL_SELECTION -> context.getString(R.string.choice_of_tool)
			TitleOverride.TOOL_PROPERTIES -> context.getString(R.string.properties, toolName)
		}
	}

	fun newImage() = optionFileNew.execute()

	fun openImage() = optionFileOpen.execute()

	fun openImage(uri: Uri) = optionFileOpen.executeWithUri(uri)

	fun openImageWithoutSaving() = optionFileOpen.executeWithoutSaving()

	fun openImageFromCamera() = optionFileCapturePhoto.execute()

	// TODO Check for last format: executeWithUriAndFormat(image.lastUri, image.lastFormat) / executeWithUri(image.lastUri)
	fun saveImage() = optionFileSave.execute()

	fun saveImageAs() = optionFileSave.execute()

	fun resizeImage() = optionTodo()

	fun scaleImage() = optionTodo()

	fun flipImage() = optionImageFlip.execute()

	fun rotateImage() = optionImageRotate.execute()

	fun flattenImage() = optionImageFlatten.execute()

	fun cropImageBySelection() = optionCropImageBySelection.execute()

	fun newLayer() = optionTodo()

	fun openLayer() = optionLayerOpen.execute()

	fun saveLayer() = optionLayerSave.execute()

	fun dragLayer() = optionTodo()

	fun resizeLayer() = optionTodo()

	fun scaleLayer() = optionTodo()

	fun flipLayer() = optionLayerFlip.execute()

	fun rotateLayer() = optionTodo()

	fun fitLayerToImage() = optionLayerToImageSize.execute()

	fun cropLayerBySelection() = optionCropLayerBySelection.execute()

	fun changeLayerOrder(layerIndex: Int, target: Int) = optionLayerChangeOrder.execute(layerIndex, target)

	fun showLayerInfo(layer: Layer) = optionLayerInfoShow.execute(layer)

	fun editLayerProperties(layer: Layer) = optionLayerPropertiesEdit.execute(layer)

	fun selectLayer(layer: Layer) = optionLayerSelect.execute(layer)

	fun toggleLayerVisibility(layer: Layer) = optionLayerVisibilityToggle.execute(layer)

	fun changeLayerName(layer: Layer) = optionLayerNameChange.execute(layer)

	fun duplicateLayer(layer: Layer) = optionLayerDuplicate.execute(layer)

	fun mergeLayerDown(layer: Layer) = optionLayerMergeDown.execute(layer)

	fun deleteLayer(layer: Layer) = optionLayerDelete.execute(layer)

	fun undo() = optionTodo()

	fun redo() = optionTodo()

	fun cut() = optionTodo()

	fun copy() = optionTodo()

	fun paste() = optionTodo()

	fun changeZoom() = optionSetZoom.execute()

	fun changeZoomToDefault() = optionTodo()

	fun centerImage() = optionTodo()

	fun toggleGrid() = optionTodo()

	fun toggleSnapToGrid() = optionTodo()

	fun selectAll() = optionTodo()

	fun selectNothing() = optionTodo()

	fun invertSelection() = optionTodo()

	fun invertColors() = optionColorsInvert.execute()

	fun changeBrightness() = optionTodo()

	fun changeColorCurves(channelType: ColorChannel.ColorChannelType) = optionTodo()

	private fun optionTodo(): Unit = TODO()

	fun setCurrentTool(tool: Tool) = toolsService.setCurrentTool(tool)

	fun setCurrentColor(@ColorInt color: Int) = colorService.setCurrentColor(color)

	fun setTitleOverride(titleOverride: TitleOverride)
	{
		_titleOverrideFlow.value = titleOverride
	}

	fun showDialog(dialogDefinition: DialogDefinition)
	{
		_dialogFlow.value = dialogDefinition
	}

	fun hideDialog()
	{
		_dialogFlow.value = null
	}

	fun showMessage(@StringRes text: Int)
	{
		_messageEventFlow.tryEmit(MessageEvent(text))
	}

	fun <R> makeActionRequest(request: ActionRequest<R>)
	{
		_actionRequestEventFlow.tryEmit(request)
	}

	// TODO Split task into two parts: asynchronous operation and result callback (executed in UI thread)
	fun postLongTask(task: () -> Unit)
	{
		viewModelScope.launch(Dispatchers.IO) { task() }
	}
}
