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
package pl.karol202.paintplus.tool.selection

import pl.karol202.paintplus.R

enum class ToolSelectionShape(val displayName: Int,
                              val icon: Int)
{
	RECTANGLE(R.string.selection_shape_rectangle, R.drawable.ic_selection_rectangular_black_24px),
	OVAL(R.string.selection_shape_oval, R.drawable.ic_selection_circular_black_24dp)
}
