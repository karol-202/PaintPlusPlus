/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package pl.karol202.paintplus.tool

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.tool.pan.ToolPan
import pl.karol202.paintplus.tool.marker.ToolMarker
import pl.karol202.paintplus.tool.brush.ToolBrush
import pl.karol202.paintplus.tool.fill.ToolFill
import pl.karol202.paintplus.tool.shape.ToolShape
import pl.karol202.paintplus.tool.selection.ToolSelection
import pl.karol202.paintplus.tool.pickcolor.ToolColorPick
import pl.karol202.paintplus.tool.drag.ToolDrag
import pl.karol202.paintplus.tool.rubber.ToolRubber
import pl.karol202.paintplus.tool.gradient.ToolGradient

class ToolsService(val tools: List<Tool>)
{
	private val _currentToolFlow = MutableStateFlow(tools.first())

	val currentToolFlow: StateFlow<Tool> = _currentToolFlow

	fun setCurrentTool(tool: Tool)
	{
		_currentToolFlow.value = tool
	}
}
