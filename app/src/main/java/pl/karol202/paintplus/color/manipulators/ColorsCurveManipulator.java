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

package pl.karol202.paintplus.color.manipulators;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import pl.karol202.paintplus.color.curves.ChannelInOutSet;
import pl.karol202.paintplus.color.curves.ColorChannel;
import pl.karol202.paintplus.color.curves.ColorChannel.ColorChannelType;
import pl.karol202.paintplus.color.curves.ColorCurve;
import pl.karol202.paintplus.color.manipulators.params.CurveManipulatorParams;
import pl.karol202.paintplus.color.manipulators.params.ManipulatorSelection;
import pl.karol202.paintplus.util.GraphicsHelper;

public class ColorsCurveManipulator implements ColorsManipulator<CurveManipulatorParams>
{
	private RenderScript renderScript;
	private CurveManipulatorParams params;
	private Allocation allocationSelection;
	private Allocation allocationIn;
	private Allocation allocationOut;
	private Rect selectionBounds;
	
	public ColorsCurveManipulator()
	{
		renderScript = GraphicsHelper.getRenderScript();
	}
	
	@Override
	public Bitmap run(Bitmap in, CurveManipulatorParams params)
	{
		this.params = params;
		prepareSelection();
		
		Bitmap out = Bitmap.createBitmap(in.getWidth(), in.getHeight(), Bitmap.Config.ARGB_8888);
		
		allocationIn = Allocation.createFromBitmap(renderScript, in);
		allocationOut = Allocation.createFromBitmap(renderScript, out);
		
		if(params.getChannelType() == ColorChannelType.RGB) runRGB();
		else if(params.getChannelType() == ColorChannelType.HSV) runHSV();
		allocationOut.copyTo(out);
		
		return out;
	}
	
	private void prepareSelection()
	{
		ManipulatorSelection selection = params.getSelection();
		if(selection == null) return;
		byte[] selectionData = selection.getData();
		selectionBounds = selection.getBounds();
		
		allocationSelection = Allocation.createSized(renderScript, Element.U8(renderScript), selectionData.length);
		allocationSelection.copyFrom(selectionData);
	}
	
	private void runRGB()
	{
		ScriptC_cm_curves_rgb script = new ScriptC_cm_curves_rgb(renderScript);
		
		if(allocationSelection != null)
		{
			script.bind_selectionData(allocationSelection);
			script.set_selectionWidth(selectionBounds.width());
			script.set_selectionLeft(selectionBounds.left);
			script.set_selectionTop(selectionBounds.top);
			script.set_selectionRight(selectionBounds.right);
			script.set_selectionBottom(selectionBounds.bottom);
		}
		
		attachCurvesRGB(script, params);
		script.forEach_transform(allocationIn, allocationOut);
	}
	
	private void attachCurvesRGB(ScriptC_cm_curves_rgb script, CurveManipulatorParams params)
	{
		for(int i = 0; i < params.getCurvesAmount(); i++)
		{
			ChannelInOutSet channels = params.getChannel(i);
			ColorCurve curve = params.getCurve(i);
			byte[] curveMap = curve.createByteColorsMap();
			Allocation allocation = Allocation.createSized(renderScript, Element.U8(renderScript), curveMap.length);
			allocation.copyFrom(curveMap);
			
			if(channels.getIn() == ColorChannel.RED && channels.getOut() == ColorChannel.RED)
				script.bind_curve_rtr(allocation);
			else if(channels.getIn() == ColorChannel.RED && channels.getOut() == ColorChannel.GREEN)
				script.bind_curve_rtg(allocation);
			else if(channels.getIn() == ColorChannel.RED && channels.getOut() == ColorChannel.BLUE)
				script.bind_curve_rtb(allocation);
			else if(channels.getIn() == ColorChannel.GREEN && channels.getOut() == ColorChannel.RED)
				script.bind_curve_gtr(allocation);
			else if(channels.getIn() == ColorChannel.GREEN && channels.getOut() == ColorChannel.GREEN)
				script.bind_curve_gtg(allocation);
			else if(channels.getIn() == ColorChannel.GREEN && channels.getOut() == ColorChannel.BLUE)
				script.bind_curve_gtb(allocation);
			else if(channels.getIn() == ColorChannel.BLUE && channels.getOut() == ColorChannel.RED)
				script.bind_curve_btr(allocation);
			else if(channels.getIn() == ColorChannel.BLUE && channels.getOut() == ColorChannel.GREEN)
				script.bind_curve_btg(allocation);
			else if(channels.getIn() == ColorChannel.BLUE && channels.getOut() == ColorChannel.BLUE)
				script.bind_curve_btb(allocation);
		}
	}
	
	private void runHSV()
	{
		ScriptC_cm_curves_hsv script = new ScriptC_cm_curves_hsv(renderScript);
		
		if(allocationSelection != null)
		{
			script.bind_selectionData(allocationSelection);
			script.set_selectionWidth(selectionBounds.width());
			script.set_selectionLeft(selectionBounds.left);
			script.set_selectionTop(selectionBounds.top);
			script.set_selectionRight(selectionBounds.right);
			script.set_selectionBottom(selectionBounds.bottom);
		}
		
		attachCurvesHSV(script, params);
		script.forEach_transform(allocationIn, allocationOut);
	}
	
	private void attachCurvesHSV(ScriptC_cm_curves_hsv script, CurveManipulatorParams params)
	{
		for(int i = 0; i < params.getCurvesAmount(); i++)
		{
			ChannelInOutSet channels = params.getChannel(i);
			ColorCurve curve = params.getCurve(i);
			short[] curveMap = curve.createShortColorsMap();
			Allocation allocation = Allocation.createSized(renderScript, Element.U16(renderScript), curveMap.length);
			allocation.copyFrom(curveMap);
			
			if(channels.getIn() == ColorChannel.HUE && channels.getOut() == ColorChannel.HUE)
				script.bind_curve_hth(allocation);
			else if(channels.getIn() == ColorChannel.HUE && channels.getOut() == ColorChannel.SATURATION)
				script.bind_curve_hts(allocation);
			else if(channels.getIn() == ColorChannel.HUE && channels.getOut() == ColorChannel.VALUE)
				script.bind_curve_htv(allocation);
			else if(channels.getIn() == ColorChannel.SATURATION && channels.getOut() == ColorChannel.HUE)
				script.bind_curve_sth(allocation);
			else if(channels.getIn() == ColorChannel.SATURATION && channels.getOut() == ColorChannel.SATURATION)
				script.bind_curve_sts(allocation);
			else if(channels.getIn() == ColorChannel.SATURATION && channels.getOut() == ColorChannel.VALUE)
				script.bind_curve_stv(allocation);
			else if(channels.getIn() == ColorChannel.VALUE && channels.getOut() == ColorChannel.HUE)
				script.bind_curve_vth(allocation);
			else if(channels.getIn() == ColorChannel.VALUE && channels.getOut() == ColorChannel.SATURATION)
				script.bind_curve_vts(allocation);
			else if(channels.getIn() == ColorChannel.VALUE && channels.getOut() == ColorChannel.VALUE)
				script.bind_curve_vtv(allocation);
		}
	}
}