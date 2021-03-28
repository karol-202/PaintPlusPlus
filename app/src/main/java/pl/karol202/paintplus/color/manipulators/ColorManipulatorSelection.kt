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
package pl.karol202.paintplus.color.manipulators

import android.graphics.Point
import android.graphics.Rect
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import androidx.core.graphics.minus
import pl.karol202.paintplus.image.Selection
import pl.karol202.paintplus.util.intersectionWith
import pl.karol202.paintplus.util.topLeft

class ColorManipulatorSelection(val data: ByteArray,
                                val bounds: Rect)
{
	companion object
	{
		private const val NOT_SELECTED: Byte = 0
		private const val SELECTED: Byte = 1

		private val empty = ColorManipulatorSelection(ByteArray(0), Rect())

		fun fromSelection(selection: Selection, layerBounds: Rect): ColorManipulatorSelection?
		{
			if(selection.isEmpty) return null
			val bounds = selection.bounds.intersectionWith(layerBounds) ?: return empty

			val array = ByteArray(bounds.width() * bounds.height())
			for(x in bounds.left until bounds.right)
			{
				for(y in bounds.top until bounds.bottom)
				{
					val arrayX = x - bounds.left
					val arrayY = y - bounds.top
					array[arrayY * bounds.width() + arrayX] = if(selection.contains(Point(x, y))) SELECTED else NOT_SELECTED
				}
			}
			return ColorManipulatorSelection(array, bounds - layerBounds.topLeft())
		}
	}

	val width get() = bounds.width()
	val left get() = bounds.left
	val top get() = bounds.top
	val right get() = bounds.right
	val bottom get() = bounds.bottom

	fun createAllocation(renderScript: RenderScript): Allocation =
			Allocation.createSized(renderScript, Element.U8(renderScript), data.size).apply {
				copyFrom(data)
			}
}
