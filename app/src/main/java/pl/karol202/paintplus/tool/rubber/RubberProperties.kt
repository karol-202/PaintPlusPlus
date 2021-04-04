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
package pl.karol202.paintplus.tool.rubber

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.PropertiesRubberBinding
import pl.karol202.paintplus.util.SeekBarTouchListener
import pl.karol202.paintplus.util.setOnValueChangeListener
import pl.karol202.paintplus.util.viewBinding

class RubberProperties : Fragment(R.layout.properties_rubber)
{
	private val toolRubber by inject<ToolRubber>()

	private val views by viewBinding(PropertiesRubberBinding::bind)

	@SuppressLint("ClickableViewAccessibility")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		views.seekBarRubberSize.progress = toolRubber.size.toInt() - 1
		views.seekBarRubberSize.setOnValueChangeListener { setRubberSize(it) }
		views.seekBarRubberSize.setOnTouchListener(SeekBarTouchListener())

		views.textRubberSize.text = (views.seekBarRubberSize.progress + 1).toString()

		views.checkRubberSmooth.isChecked = toolRubber.smoothEdge
		views.checkRubberSmooth.setOnCheckedChangeListener { _, checked -> toolRubber.smoothEdge = checked }
	}

	private fun setRubberSize(size: Int)
	{
		toolRubber.size = (size + 1).toFloat()
		views.textRubberSize.text = (size + 1).toString()
	}
}
