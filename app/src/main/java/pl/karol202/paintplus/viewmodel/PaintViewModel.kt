package pl.karol202.paintplus.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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

	val image = Image(application)
	val tools = Tools(image)

	private val _currentToolFlow = MutableStateFlow(tools.defaultTool)
	private val _titleOverrideFlow = MutableStateFlow(TitleOverride.NONE)

	val currentTool get() = currentToolFlow.value
	val currentToolId get() = tools.getToolId(currentTool)

	val settingsFlow = settingsRepository.settings
	val currentToolFlow: StateFlow<Tool> = _currentToolFlow
	val titleOverrideFlow: StateFlow<TitleOverride> = _titleOverrideFlow

	init
	{
		image.newImage(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT)
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
