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

import android.renderscript.RenderScript
import android.graphics.Bitmap
import android.renderscript.Allocation
import pl.karol202.paintplus.color.manipulators.BrightnessColorManipulator.Params

class BrightnessColorManipulator(private val renderScript: RenderScript) : ColorManipulator<Params>
{
	class Params(selection: ColorManipulatorSelection?,
	             val brightness: Float,
	             val contrast: Float) : ColorManipulator.Params(selection)

	override fun run(input: Bitmap, params: Params): Bitmap
	{
		val out = Bitmap.createBitmap(input.width, input.height, Bitmap.Config.ARGB_8888)
		val allocationIn = Allocation.createFromBitmap(renderScript, input)
		val allocationOut = Allocation.createFromBitmap(renderScript, out)
		val script = ScriptC_cm_brightness(renderScript)
		script.attachSelection(params.selection)
		script._brightness = params.brightness
		script._contrast = params.contrast
		script.forEach_invert(allocationIn, allocationOut)
		allocationOut.copyTo(out)
		return out
	}

	private fun ScriptC_cm_brightness.attachSelection(selection: ColorManipulatorSelection?)
	{
		if(selection == null) return
		bind_selectionData(selection.createAllocation(renderScript))
		_selectionWidth = selection.width
		_selectionLeft = selection.left
		_selectionTop = selection.top
		_selectionRight = selection.right
		_selectionBottom = selection.bottom
	}
}
