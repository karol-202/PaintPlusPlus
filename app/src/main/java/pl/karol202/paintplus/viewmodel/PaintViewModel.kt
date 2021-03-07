package pl.karol202.paintplus.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.settings.SettingsRepository
import pl.karol202.paintplus.tool.Tool
import pl.karol202.paintplus.tool.Tools

private const val DEFAULT_IMAGE_WIDTH = 600
private const val DEFAULT_IMAGE_HEIGHT = 600

class PaintViewModel(application: Application,
                     settingsRepository: SettingsRepository) : AndroidViewModel(application)
{
	enum class TitleOverride
	{
		NONE, TOOL_SELECTION, TOOL_PROPERTIES
	}

	enum class ImageEvent
	{
		IMAGE_CHANGED, LAYERS_CHANGED, IMAGE_MATRIX_CHANGED, SELECTION_CHANGED, HISTORY_CHANGED
	}

	val image = Image(application)
	val tools = Tools(image)

	private val _currentToolFlow = MutableStateFlow(tools.defaultTool)
	private val _titleOverrideFlow = MutableStateFlow(TitleOverride.NONE)
	private val _imageEventFlow = MutableSharedFlow<ImageEvent>(replay = 16)

	val currentTool get() = currentToolFlow.value
	val currentToolId get() = tools.getToolId(currentTool)

	val settingsFlow = settingsRepository.settings
	val currentToolFlow: StateFlow<Tool> = _currentToolFlow
	val titleOverrideFlow: StateFlow<TitleOverride> = _titleOverrideFlow
	val imageEventFlow: Flow<ImageEvent> = _imageEventFlow

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
}
