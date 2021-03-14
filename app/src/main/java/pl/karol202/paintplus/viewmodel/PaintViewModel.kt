package pl.karol202.paintplus.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.settings.SettingsRepository
import pl.karol202.paintplus.tool.Tool
import pl.karol202.paintplus.tool.Tools
import kotlin.coroutines.suspendCoroutine

private const val DEFAULT_IMAGE_WIDTH = 600
private const val DEFAULT_IMAGE_HEIGHT = 600

class PaintViewModel(application: Application,
                     settingsRepository: SettingsRepository) : BaseViewModel(application)
{
	enum class TitleOverride
	{
		NONE, TOOL_SELECTION, TOOL_PROPERTIES
	}

	enum class ImageEvent
	{
		IMAGE_CHANGED, LAYERS_CHANGED, IMAGE_MATRIX_CHANGED, SELECTION_CHANGED, HISTORY_CHANGED
	}

	data class MessageEvent(@StringRes val text: Int)

	sealed class ActionRequest<R>(val callback: (R) -> Unit)
	{
		class OpenFile(val mimeFilters: List<String>,
		               callback: (Uri?) -> Unit) : ActionRequest<Uri?>(callback)

		class SaveFile(val suggestedName: String,
		               callback: (Uri?) -> Unit) : ActionRequest<Uri?>(callback)
	}

	val context: Context get() = getApplication()
	val image = Image(application)
	val tools = Tools(image)

	private val _currentToolFlow = MutableStateFlow(tools.defaultTool)
	private val _titleOverrideFlow = MutableStateFlow(TitleOverride.NONE)
	private val _dialogFlow = MutableStateFlow<DialogDefinition?>(null)
	private val _messageEventFlow = MutableSharedFlow<MessageEvent>(extraBufferCapacity = 16)
	private val _imageEventFlow = MutableSharedFlow<ImageEvent>(extraBufferCapacity = 16)
	private val _actionRequestEventFlow = MutableSharedFlow<ActionRequest<*>>(extraBufferCapacity = 16)

	val currentTool get() = currentToolFlow.value
	val currentToolId get() = tools.getToolId(currentTool)

	val settingsFlow = settingsRepository.settings
	val currentToolFlow: StateFlow<Tool> = _currentToolFlow
	val titleOverrideFlow: StateFlow<TitleOverride> = _titleOverrideFlow
	val dialogFlow: StateFlow<DialogDefinition?> = _dialogFlow
	val messageEventFlow: Flow<MessageEvent> = _messageEventFlow
	val imageEventFlow: Flow<ImageEvent> = _imageEventFlow
	val actionRequestEventFlow: Flow<ActionRequest<*>> = _actionRequestEventFlow

	init
	{
		image.newImage(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT)
		image.setOnImageChangeListener(object : Image.OnImageChangeListener {
			override fun onImageChanged()
			{
				_imageEventFlow.tryEmit(ImageEvent.IMAGE_CHANGED)
			}

			override fun onLayersChanged()
			{
				_imageEventFlow.tryEmit(ImageEvent.LAYERS_CHANGED)
			}

			override fun onImageMatrixChanged()
			{
				_imageEventFlow.tryEmit(ImageEvent.IMAGE_MATRIX_CHANGED)
			}
		})
		image.addOnSelectionChangeListener {
			_imageEventFlow.tryEmit(ImageEvent.SELECTION_CHANGED)
		}
		image.setOnHistoryUpdateListener {
			image.updateImage() // TODO Investigate on if it is necessary
			_imageEventFlow.tryEmit(ImageEvent.HISTORY_CHANGED)
		}
	}

	fun setCurrentTool(tool: Tool)
	{
		_currentToolFlow.value = tool
	}

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
