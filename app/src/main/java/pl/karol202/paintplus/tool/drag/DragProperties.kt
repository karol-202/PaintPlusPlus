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
package pl.karol202.paintplus.tool.drag

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.PropertiesDragBinding
import pl.karol202.paintplus.util.viewBinding

class DragProperties : Fragment(R.layout.properties_drag)
{
	private val toolDrag by inject<ToolDrag>()

	private val views by viewBinding(PropertiesDragBinding::bind)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		views.checkDragOneAxis.isChecked = toolDrag.isOneAxis
		views.checkDragOneAxis.setOnCheckedChangeListener { _, checked -> toolDrag.isOneAxis = checked }
	}
}
