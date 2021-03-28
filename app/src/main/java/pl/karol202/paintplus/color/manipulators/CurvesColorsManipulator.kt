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
import android.renderscript.Allocation
import android.graphics.Bitmap
import android.renderscript.Element
import pl.karol202.paintplus.color.curves.ColorChannel
import pl.karol202.paintplus.color.curves.ColorCurve

class CurvesColorsManipulator(private val renderScript: RenderScript) : ColorManipulator<CurvesColorsManipulator.Params>
{
	class Params(selection: ColorManipulatorSelection?,
	             val channelType: ColorChannel.ColorChannelType) : ColorManipulator.Params(selection)
	{
		private val _curves = mutableListOf<ColorCurve>()
		val curves: List<ColorCurve> = _curves

		fun addCurve(curve: ColorCurve)
		{
			_curves.add(curve)
		}
	}

	override fun run(input: Bitmap, params: Params): Bitmap
	{
		val out = Bitmap.createBitmap(input.width, input.height, Bitmap.Config.ARGB_8888)
		val allocationIn = Allocation.createFromBitmap(renderScript, input)
		val allocationOut = Allocation.createFromBitmap(renderScript, out)
		when(params.channelType)
		{
			ColorChannel.ColorChannelType.RGB -> runRGB(params, allocationIn, allocationOut)
			ColorChannel.ColorChannelType.HSV -> runHSV(params, allocationIn, allocationOut)
		}
		allocationOut.copyTo(out)
		return out
	}

	private fun runRGB(params: Params, allocationIn: Allocation, allocationOut: Allocation)
	{
		val script = ScriptC_cm_curves_rgb(renderScript)
		script.attachCurvesRGB(params)
		script.attachSelection(params.selection)
		script.forEach_transform(allocationIn, allocationOut)
	}

	private fun runHSV(params: Params, allocationIn: Allocation, allocationOut: Allocation)
	{
		val script = ScriptC_cm_curves_hsv(renderScript)
		script.attachCurvesHSV(params)
		script.attachSelection(params.selection)
		script.forEach_transform(allocationIn, allocationOut)
	}

	private fun ScriptC_cm_curves_rgb.attachCurvesRGB(params: Params) = params.curves.forEach { curve ->
		val channels = curve.channels
		val curveMap = curve.createByteColorsMap()
		val allocation = Allocation.createSized(renderScript, Element.U8(renderScript), curveMap.size)
		allocation.copyFrom(curveMap)
		when
		{
			channels.input == ColorChannel.RED && channels.output == ColorChannel.RED -> bind_curve_rtr(allocation)
			channels.input == ColorChannel.RED && channels.output == ColorChannel.GREEN -> bind_curve_rtg(allocation)
			channels.input == ColorChannel.RED && channels.output == ColorChannel.BLUE -> bind_curve_rtb(allocation)
			channels.input == ColorChannel.GREEN && channels.output == ColorChannel.RED -> bind_curve_gtr(allocation)
			channels.input == ColorChannel.GREEN && channels.output == ColorChannel.GREEN -> bind_curve_gtg(allocation)
			channels.input == ColorChannel.GREEN && channels.output == ColorChannel.BLUE -> bind_curve_gtb(allocation)
			channels.input == ColorChannel.BLUE && channels.output == ColorChannel.RED -> bind_curve_btr(allocation)
			channels.input == ColorChannel.BLUE && channels.output == ColorChannel.GREEN -> bind_curve_btg(allocation)
			channels.input == ColorChannel.BLUE && channels.output == ColorChannel.BLUE -> bind_curve_btb(allocation)
		}
	}

	private fun ScriptC_cm_curves_hsv.attachCurvesHSV(params: Params) = params.curves.forEach { curve ->
		val channels = curve.channels
		val curveMap = curve.createByteColorsMap()
		val allocation = Allocation.createSized(renderScript, Element.U16(renderScript), curveMap.size)
		allocation.copyFrom(curveMap)
		when
		{
			channels.input == ColorChannel.HUE && channels.output == ColorChannel.HUE -> bind_curve_hth(allocation)
			channels.input == ColorChannel.HUE && channels.output == ColorChannel.SATURATION -> bind_curve_hts(allocation)
			channels.input == ColorChannel.HUE && channels.output == ColorChannel.VALUE -> bind_curve_htv(allocation)
			channels.input == ColorChannel.SATURATION && channels.output == ColorChannel.HUE -> bind_curve_sth(allocation)
			channels.input == ColorChannel.SATURATION && channels.output == ColorChannel.SATURATION -> bind_curve_sts(allocation)
			channels.input == ColorChannel.SATURATION && channels.output == ColorChannel.VALUE -> bind_curve_stv(allocation)
			channels.input == ColorChannel.VALUE && channels.output == ColorChannel.HUE -> bind_curve_vth(allocation)
			channels.input == ColorChannel.VALUE && channels.output == ColorChannel.SATURATION -> bind_curve_vts(allocation)
			channels.input == ColorChannel.VALUE && channels.output == ColorChannel.VALUE -> bind_curve_vtv(allocation)
		}
	}

	private fun ScriptC_cm_curves_rgb.attachSelection(selection: ColorManipulatorSelection?)
	{
		if(selection == null) return
		bind_selectionData(selection.createAllocation(renderScript))
		_selectionWidth = selection.width
		_selectionLeft = selection.left
		_selectionTop = selection.top
		_selectionRight = selection.right
		_selectionBottom = selection.bottom
	}

	private fun ScriptC_cm_curves_hsv.attachSelection(selection: ColorManipulatorSelection?)
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
