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

import pl.karol202.paintplus.image.Image
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
import java.util.ArrayList

class Tools(image: Image)
{
	val tools = listOf(ToolPan(image),
	                   ToolMarker(image),
	                   ToolBrush(image),
	                   ToolFill(image),
	                   ToolShape(image),
	                   ToolSelection(image),
	                   ToolColorPick(image),
	                   ToolDrag(image),
	                   ToolRubber(image),
	                   ToolGradient(image)
	)
	val defaultTool get() = tools.first()

	fun getTool(id: Int) = tools[id]

	fun getToolId(tool: Tool) = tools.indexOf(tool)
}
