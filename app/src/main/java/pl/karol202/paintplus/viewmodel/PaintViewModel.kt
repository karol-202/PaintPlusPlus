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
import pl.karol202.paintplus.color.picker.ColorPickerConfig
import pl.karol202.paintplus.image.ColorsService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.LegacyImage
import pl.karol202.paintplus.image.layer.mode.LayerModesService
import pl.karol202.paintplus.settings.SettingsRepository
import pl.karol202.paintplus.tool.Tool
import pl.karol202.paintplus.tool.ToolsService

private const val DEFAULT_IMAGE_WIDTH = 600
private const val DEFAULT_IMAGE_HEIGHT = 600

class PaintViewModel(application: Application,
                     settingsRepository: SettingsRepository,
                     imageService: ImageService,
                     private val toolsService: ToolsService,
                     private val colorService: ColorsService,
                     private val layerModesService: LayerModesService) : BaseViewModel(application)
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
	val layerModes get() = layerModesService.layerModes

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

	fun postLongTask(task: () -> Unit)
	{
		viewModelScope.launch(Dispatchers.IO) { task() }
	}
}
