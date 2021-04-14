package pl.karol202.paintplus.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Size
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.karol202.paintplus.R
import pl.karol202.paintplus.color.curves.ColorChannel
import pl.karol202.paintplus.color.picker.ColorPickerConfig
import pl.karol202.paintplus.helpers.Grid
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.image.*
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.options.*
import pl.karol202.paintplus.settings.SettingsRepository
import pl.karol202.paintplus.tool.Tool
import pl.karol202.paintplus.tool.ToolsService
import pl.karol202.paintplus.util.collectIn

class PaintViewModel(application: Application,
                     settingsRepository: SettingsRepository,
                     imageService: ImageService,
                     private val viewService: ViewService,
                     private val toolsService: ToolsService,
                     private val colorService: ColorsService,
                     private val historyService: HistoryService,
                     private val helpersService: HelpersService,
                     private val effectsService: EffectsService,
                     private val grid: Grid,
                     private val optionCopy: OptionCopy,
                     private val optionCut: OptionCut,
                     private val optionImageCapturePhoto: OptionImageCapturePhoto,
                     private val optionImageCropBySelection: OptionImageCropBySelection,
                     private val optionImageFlatten: OptionImageFlatten,
                     private val optionImageFlip: OptionImageFlip,
                     private val optionImageNew: OptionImageNew,
                     private val optionImageOpen: OptionImageOpen,
                     private val optionImageResize: OptionImageResize,
                     private val optionImageRotate: OptionImageRotate,
                     private val optionImageSaveAs: OptionImageSaveAs,
                     private val optionImageSaveLast: OptionImageSaveLast,
                     private val optionImageScale: OptionImageScale,
                     private val optionLayerChangeOrder: OptionLayerChangeOrder,
                     private val optionLayerColorBrightness: OptionLayerColorBrightness,
                     private val optionLayerColorCurves: OptionLayerColorCurves,
                     private val optionLayerColorInvert: OptionLayerColorInvert,
                     private val optionLayerCropBySelection: OptionLayerCropBySelection,
                     private val optionLayerDelete: OptionLayerDelete,
                     private val optionLayerDrag: OptionLayerDrag,
                     private val optionLayerDuplicate: OptionLayerDuplicate,
                     private val optionLayerFitToImage: OptionLayerFitToImage,
                     private val optionLayerFlip: OptionLayerFlip,
                     private val optionLayerInfoShow: OptionLayerInfoShow,
                     private val optionLayerMergeDown: OptionLayerMergeDown,
                     private val optionLayerNameChange: OptionLayerNameChange,
                     private val optionLayerNew: OptionLayerNew,
                     private val optionLayerOpen: OptionLayerOpen,
                     private val optionLayerPropertiesEdit: OptionLayerPropertiesEdit,
                     private val optionLayerResize: OptionLayerResize,
                     private val optionLayerRotate: OptionLayerRotate,
                     private val optionLayerSave: OptionLayerSave,
                     private val optionLayerScale: OptionLayerScale,
                     private val optionLayerSelect: OptionLayerSelect,
                     private val optionLayerVisibilityToggle: OptionLayerVisibilityToggle,
                     private val optionPaste: OptionPaste,
                     private val optionSelectAll: OptionSelectAll,
                     private val optionSelectInversion: OptionSelectInversion,
                     private val optionSelectNothing: OptionSelectNothing,
                     private val optionSetZoom: OptionSetZoom) : BaseViewModel(application)
{
	enum class TitleOverride
	{
		NONE, TOOL_SELECTION, TOOL_PROPERTIES
	}

	// TODO Allow dialogs to be stateful
	fun interface DialogDefinition
	{
		fun init(builder: AlertDialog.Builder, dialogProvider: () -> AlertDialog?)
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

	private val context: Context get() = getApplication()

	private val _titleOverrideFlow = MutableStateFlow(TitleOverride.NONE)

	val imageFlow = imageService.imageFlow
	val selectionFlow = imageService.selectionFlow
	val viewPositionFlow = viewService.viewPositionFlow
	val currentToolFlow = toolsService.currentToolFlow
	val currentColorFlow = colorService.currentColorFlow
	val settingsFlow = settingsRepository.settings
	val dialogFlow = effectsService.dialogFlow
	val messageEventFlow = effectsService.messageEventFlow
	val actionRequestEventFlow = effectsService.actionRequestEventFlow
	val viewUpdateEventFlow = effectsService.viewUpdateEventFlow

	val titleFlow = _titleOverrideFlow.combine(currentToolFlow) { override, tool -> createTitle(override, tool) }

	val tools get() = toolsService.tools
	val currentTool get() = currentToolFlow.value
	val currentColor get() = currentColorFlow.value
	val helpers get() = helpersService.helpers

	init
	{
		effectsService.longTaskRunFlow.collectIn(viewModelScope, this::postLongTask)
	}

	// TODO Split task into two parts: asynchronous operation and result callback (executed in UI thread)
	private fun postLongTask(task: () -> Unit)
	{
		viewModelScope.launch(Dispatchers.IO) { task() }
	}

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

	fun newImage() = optionImageNew.execute()

	fun openImage() = optionImageOpen.execute()

	fun openImage(uri: Uri) = optionImageOpen.executeWithUri(uri)

	fun openImageWithoutSaving() = optionImageOpen.executeWithoutSaving()

	fun openImageFromCamera() = optionImageCapturePhoto.execute()

	fun saveImage() = optionImageSaveLast.execute()

	fun saveImageAs() = optionImageSaveAs.execute()

	fun resizeImage() = optionImageResize.execute()

	fun scaleImage() = optionImageScale.execute()

	fun flipImage() = optionImageFlip.execute()

	fun rotateImage() = optionImageRotate.execute()

	fun flattenImage() = optionImageFlatten.execute()

	fun cropImageBySelection() = optionImageCropBySelection.execute()

	fun newLayer() = optionLayerNew.execute()

	fun openLayer() = optionLayerOpen.execute()

	fun saveLayer() = optionLayerSave.execute()

	fun dragLayer() = optionLayerDrag.execute()

	fun resizeLayer() = optionLayerResize.execute()

	fun scaleLayer() = optionLayerScale.execute()

	fun flipLayer() = optionLayerFlip.execute()

	fun rotateLayer() = optionLayerRotate.execute()

	fun fitLayerToImage() = optionLayerFitToImage.execute()

	fun cropLayerBySelection() = optionLayerCropBySelection.execute()

	fun changeLayerOrder(layerIndex: Int, target: Int) = optionLayerChangeOrder.execute(layerIndex, target)

	fun showLayerInfo(layer: Layer) = optionLayerInfoShow.execute(layer)

	fun editLayerProperties(layer: Layer) = optionLayerPropertiesEdit.execute(layer)

	fun selectLayer(layer: Layer) = optionLayerSelect.execute(layer)

	fun toggleLayerVisibility(layer: Layer) = optionLayerVisibilityToggle.execute(layer)

	fun changeLayerName(layer: Layer) = optionLayerNameChange.execute(layer)

	fun duplicateLayer(layer: Layer) = optionLayerDuplicate.execute(layer)

	fun mergeLayerDown(layer: Layer) = optionLayerMergeDown.execute(layer)

	fun deleteLayer(layer: Layer) = optionLayerDelete.execute(layer)

	fun undo() = historyService.undo()

	fun redo() = historyService.redo()

	fun cut() = optionCut.execute()

	fun copy() = optionCopy.execute()

	fun paste() = optionPaste.execute()

	fun changeZoom() = optionSetZoom.execute()

	fun changeZoomToDefault() = viewService.setDefaultZoom()

	fun centerImage() = viewService.centerView()

	fun toggleGrid() = grid.toggleGrid()

	fun toggleSnapToGrid() = grid.toggleSnapping()

	fun selectAll() = optionSelectAll.execute()

	fun selectNothing() = optionSelectNothing.execute()

	fun invertSelection() = optionSelectInversion.execute()

	fun invertColors() = optionLayerColorInvert.execute()

	fun changeBrightness() = optionLayerColorBrightness.execute()

	fun changeColorCurves(channelType: ColorChannel.ColorChannelType) = optionLayerColorCurves.execute(channelType)

	fun setViewportSize(size: Size) = viewService.setViewportSize(size)

	fun setCurrentTool(tool: Tool) = toolsService.setCurrentTool(tool)

	fun setCurrentColor(@ColorInt color: Int) = colorService.setCurrentColor(color)

	fun setTitleOverride(titleOverride: TitleOverride)
	{
		_titleOverrideFlow.value = titleOverride
	}

	fun hideDialog() = effectsService.hideDialog()

	fun <R> makeActionRequest(request: ActionRequest<R>) = effectsService.makeActionRequest(request)
}
